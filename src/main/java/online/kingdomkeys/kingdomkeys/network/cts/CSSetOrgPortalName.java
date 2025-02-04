package online.kingdomkeys.kingdomkeys.network.cts;


import java.util.UUID;
import java.util.function.Supplier;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.network.NetworkEvent;
import online.kingdomkeys.kingdomkeys.capability.ModCapabilities;
import online.kingdomkeys.kingdomkeys.entity.block.OrgPortalTileEntity;
import online.kingdomkeys.kingdomkeys.network.PacketHandler;
import online.kingdomkeys.kingdomkeys.network.stc.SCSyncWorldCapability;

public class CSSetOrgPortalName {

	BlockPos pos;
    String name;
	
    public CSSetOrgPortalName() {}

    public CSSetOrgPortalName(BlockPos pos, String name) {
        this.pos = pos;
        this.name = name;
    }

    
    public void encode(FriendlyByteBuf buffer) {
        buffer.writeBlockPos(pos);
        buffer.writeInt(name.length());
        buffer.writeUtf(name, name.length());
    }

    public static CSSetOrgPortalName decode(FriendlyByteBuf buffer) {
        CSSetOrgPortalName msg = new CSSetOrgPortalName();
        msg.pos = buffer.readBlockPos();
        int len = buffer.readInt();
        msg.name = buffer.readUtf(len);
        return msg;
    }

    public static void handle(CSSetOrgPortalName message, final Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            Player player = ctx.get().getSender();
            if(player.level.getBlockEntity(message.pos) != null && player.level.getBlockEntity(message.pos) instanceof OrgPortalTileEntity) {
            	OrgPortalTileEntity te = (OrgPortalTileEntity) player.level.getBlockEntity(message.pos);
            	UUID portalUUID = te.getUUID();
            	ModCapabilities.getWorld(player.level).getPortalFromUUID(portalUUID).setName(message.name);
				PacketHandler.sendTo(new SCSyncWorldCapability(ModCapabilities.getWorld(player.level)), (ServerPlayer) player);            	
            }
        });
        ctx.get().setPacketHandled(true);
    }

}
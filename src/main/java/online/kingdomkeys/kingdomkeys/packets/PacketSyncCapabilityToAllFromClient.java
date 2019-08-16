package online.kingdomkeys.kingdomkeys.packets;

import java.util.function.Supplier;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;
import online.kingdomkeys.kingdomkeys.capability.ModCapabilities;

public class PacketSyncCapabilityToAllFromClient {

	public PacketSyncCapabilityToAllFromClient() {
	}

	public void encode(PacketBuffer buffer) {
		
	}

	public static PacketSyncCapabilityToAllFromClient decode(PacketBuffer buffer) {
		PacketSyncCapabilityToAllFromClient msg = new PacketSyncCapabilityToAllFromClient();
		
		return msg;
	}

	public static void handle(final PacketSyncCapabilityToAllFromClient message, Supplier<NetworkEvent.Context> ctx) {
		ctx.get().enqueueWork(() -> {
			PlayerEntity player = ctx.get().getSender();
			PacketHandler.syncToAllAround(player, ModCapabilities.get(player));
		});
		ctx.get().setPacketHandled(true);
	}

}
package online.kingdomkeys.kingdomkeys.network.cts;

import java.util.function.Supplier;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.network.NetworkEvent;
import online.kingdomkeys.kingdomkeys.capability.IPlayerCapabilities;
import online.kingdomkeys.kingdomkeys.capability.ModCapabilities;
import online.kingdomkeys.kingdomkeys.driveform.DriveForm;
import online.kingdomkeys.kingdomkeys.driveform.ModDriveForms;
import online.kingdomkeys.kingdomkeys.lib.Strings;
import online.kingdomkeys.kingdomkeys.network.PacketHandler;

public class CSUseDriveFormPacket {

	String form;

	public CSUseDriveFormPacket() {
	}

	public CSUseDriveFormPacket(String form) {
		this.form = form;
	}

	public void encode(FriendlyByteBuf buffer) {
		buffer.writeInt(this.form.length());
		buffer.writeUtf(this.form);
	}

	public static CSUseDriveFormPacket decode(FriendlyByteBuf buffer) {
		CSUseDriveFormPacket msg = new CSUseDriveFormPacket();
		int length = buffer.readInt();
		msg.form = buffer.readUtf(length);
		return msg;
	}

	public static void handle(CSUseDriveFormPacket message, final Supplier<NetworkEvent.Context> ctx) {
		ctx.get().enqueueWork(() -> {
			Player player = ctx.get().getSender();
			IPlayerCapabilities playerData = ModCapabilities.getPlayer(player);
			
			if (message.form.equals(Strings.Form_Anti)) { //If target is antiform
				playerData.setActiveDriveForm(Strings.Form_Anti);
				playerData.setDP(0);
				playerData.setFP(1000);
				playerData.setAntiPoints(playerData.getAntiPoints() -4);
				PacketHandler.syncToAllAround(player, playerData);
			} else { //if target is a normal form or revert
				if (!playerData.getActiveDriveForm().equals(DriveForm.NONE.toString()) && message.form.equals(DriveForm.NONE.toString())) { // If is in a drive form and the target is "" (player)
					DriveForm form = ModDriveForms.registry.get().getValue(new ResourceLocation(playerData.getActiveDriveForm()));
					form.endDrive(player);
				} else if (!message.form.equals(DriveForm.NONE.toString())) { // If is not in a form and wants to drive
					DriveForm form = ModDriveForms.registry.get().getValue(new ResourceLocation(message.form));
					form.initDrive(player);
				}
			}
		});
		ctx.get().setPacketHandled(true);
	}

}

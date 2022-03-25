package online.kingdomkeys.kingdomkeys.command;

import java.util.Collection;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.Util;
import net.minecraft.network.chat.TranslatableComponent;
import online.kingdomkeys.kingdomkeys.KingdomKeys;
import online.kingdomkeys.kingdomkeys.capability.IPlayerCapabilities;
import online.kingdomkeys.kingdomkeys.capability.ModCapabilities;
import online.kingdomkeys.kingdomkeys.network.PacketHandler;
import online.kingdomkeys.kingdomkeys.network.stc.SCSyncCapabilityPacket;

public class KKDrivePointsCommand extends BaseCommand{ //kk_dp <give/take/set> <amount> [player]
	public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
		LiteralArgumentBuilder<CommandSourceStack> builder = Commands.literal("kk_dp").requires(source -> source.hasPermission(2));
		
		builder.then(Commands.literal("set")
			.then(Commands.argument("value", IntegerArgumentType.integer(1,Integer.MAX_VALUE))
				.then(Commands.argument("targets", EntityArgument.players())
					.executes(KKDrivePointsCommand::setValue)
				)
				.executes(KKDrivePointsCommand::setValue)
			)
		);
		
		builder.then(Commands.literal("give")
			.then(Commands.argument("value", IntegerArgumentType.integer(1,Integer.MAX_VALUE))
				.then(Commands.argument("targets", EntityArgument.players())
					.executes(KKDrivePointsCommand::addValue)
				)
				.executes(KKDrivePointsCommand::addValue)
			)
		);
		
		builder.then(Commands.literal("take")
			.then(Commands.argument("value", IntegerArgumentType.integer(1,Integer.MAX_VALUE))
				.then(Commands.argument("targets", EntityArgument.players())
					.executes(KKDrivePointsCommand::removeValue)
				)
				.executes(KKDrivePointsCommand::removeValue)
			)
		);
		
		dispatcher.register(builder);
		KingdomKeys.LOGGER.warn("Registered command "+builder.getLiteral());
	}

	private static int setValue(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
		Collection<ServerPlayer> players = getPlayers(context, 3);
		int value = IntegerArgumentType.getInteger(context, "value");
		
		for (ServerPlayer player : players) {
			setValue(context,value,player);
		}
		return 1;
	}
	
	private static int setValue(CommandContext<CommandSourceStack> context, int value, ServerPlayer player) throws CommandSyntaxException {
		IPlayerCapabilities playerData = ModCapabilities.getPlayer(player);
		playerData.setDP(value);
		
			context.getSource().sendSuccess(new TranslatableComponent("Set "+player.getDisplayName().getString()+" dp to "+value), true);
		
		player.sendMessage(new TranslatableComponent("Your dp has been set to "+value),Util.NIL_UUID);
		PacketHandler.sendTo(new SCSyncCapabilityPacket(playerData), player);
		return 1;
	}
	
	private static int addValue(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
		Collection<ServerPlayer> players = getPlayers(context, 3);
		int value = IntegerArgumentType.getInteger(context, "value");
		
		for (ServerPlayer player : players) {
			addValue(context,value,player);
		}
		return 1;
	}
	private static int addValue(CommandContext<CommandSourceStack> context, int value, ServerPlayer player) throws CommandSyntaxException {
		IPlayerCapabilities playerData = ModCapabilities.getPlayer(player);
		playerData.addDP(value);
		
			context.getSource().sendSuccess(new TranslatableComponent("Added "+value+" dp to "+player.getDisplayName().getString()), true);
		
		player.sendMessage(new TranslatableComponent("Your dp has been increased by "+value),Util.NIL_UUID);
		PacketHandler.sendTo(new SCSyncCapabilityPacket(playerData), player);
		return 1;
	}
	
	private static int removeValue(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
		Collection<ServerPlayer> players = getPlayers(context, 3);
		int value = IntegerArgumentType.getInteger(context, "value");
		
		for (ServerPlayer player : players) {
			removeValue(context,value,player);
		}
		return 1;
	}
	
	private static int removeValue(CommandContext<CommandSourceStack> context, int value, ServerPlayer player) throws CommandSyntaxException {
		IPlayerCapabilities playerData = ModCapabilities.getPlayer(player);
		playerData.remDP(value);
		
			context.getSource().sendSuccess(new TranslatableComponent("Taken "+value+" dp from "+player.getDisplayName().getString()), true);
		
		player.sendMessage(new TranslatableComponent("Your dp has been decreased by "+value),Util.NIL_UUID);
		PacketHandler.sendTo(new SCSyncCapabilityPacket(playerData), player);
		return 1;
	}
}

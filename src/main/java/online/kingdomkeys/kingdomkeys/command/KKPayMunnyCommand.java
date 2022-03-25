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

public class KKPayMunnyCommand extends BaseCommand { // kk_paymunny <player> <value>
	public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
		LiteralArgumentBuilder<CommandSourceStack> builder = Commands.literal("kk_paymunny").requires(source -> source.hasPermission(0));
		
		builder.then(Commands.argument("targets", EntityArgument.players())
				.then(Commands.argument("value", IntegerArgumentType.integer(1, Integer.MAX_VALUE))
					.executes(KKPayMunnyCommand::payValue)));

		dispatcher.register(builder);
		KingdomKeys.LOGGER.warn("Registered command " + builder.getLiteral());
	}

	private static int payValue(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
		ServerPlayer user = context.getSource().getPlayerOrException();
		IPlayerCapabilities userData = ModCapabilities.getPlayer(user);

		Collection<ServerPlayer> players = EntityArgument.getPlayers(context, "targets");
		int value = IntegerArgumentType.getInteger(context, "value");
		if(userData.getMunny() - (value * players.size()) >= 0) {
			for (ServerPlayer target : players) {
				IPlayerCapabilities targetData = ModCapabilities.getPlayer(target);
				userData.setMunny(userData.getMunny() - value);
				targetData.setMunny(targetData.getMunny() + value);
				user.sendMessage(new TranslatableComponent("You paid " + value + " munny to " + target.getDisplayName().getString()), Util.NIL_UUID);
				target.sendMessage(new TranslatableComponent("You got " + value + " munny from " + user.getDisplayName().getString()), Util.NIL_UUID);
			}
		} else {
			user.sendMessage(new TranslatableComponent("You don't have enough munny (" + value + ") to pay " + players.toString()), Util.NIL_UUID);	
		}
		return 1;
	}

}

package online.kingdomkeys.kingdomkeys.commands;

import java.util.Collection;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.EntityArgument;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.text.TranslationTextComponent;
import online.kingdomkeys.kingdomkeys.KingdomKeys;
import online.kingdomkeys.kingdomkeys.capability.IPlayerCapabilities;
import online.kingdomkeys.kingdomkeys.capability.ModCapabilities;

public class KKLevelCommand extends BaseCommand{ //kklevel <give/take/set> <amount> [player]
	public static void register(CommandDispatcher<CommandSource> dispatcher) {
		LiteralArgumentBuilder<CommandSource> builder = Commands.literal("kklevel").requires(source -> source.hasPermissionLevel(3));
		
		builder.then(Commands.literal("set")
			.then(Commands.argument("level", IntegerArgumentType.integer(1,100))
				.then(Commands.argument("targets", EntityArgument.players())
					.executes(KKLevelCommand::setValue)
				)
				.executes(KKLevelCommand::setValue)
			)
		);
		
		/*builder.then(Commands.literal("give")
			.then(Commands.argument("value", IntegerArgumentType.integer(1,Integer.MAX_VALUE))
				.then(Commands.argument("targets", EntityArgument.players())
					.executes(KKLevelCommand::addValue)
				)
				.executes(KKLevelCommand::addValue)
			)
		);
		
		builder.then(Commands.literal("take")
			.then(Commands.argument("value", IntegerArgumentType.integer(1,Integer.MAX_VALUE))
				.then(Commands.argument("targets", EntityArgument.players())
					.executes(KKLevelCommand::removeValue)
				)
				.executes(KKLevelCommand::removeValue)
			)
		);*/
		
		dispatcher.register(builder);
		KingdomKeys.LOGGER.warn("Registered command "+builder.getLiteral());
	}

	private static int setValue(CommandContext<CommandSource> context) throws CommandSyntaxException {
		Collection<ServerPlayerEntity> players = getPlayers(context, 3);
		int level = IntegerArgumentType.getInteger(context, "level");
		
		for (ServerPlayerEntity player : players) {
			IPlayerCapabilities playerData = ModCapabilities.getPlayer(player);
            
			playerData.setLevel(1);
			playerData.setExperience(0);
			playerData.setMaxHP(20);
            player.setHealth(playerData.getMaxHP());
            playerData.setMaxMP(0);
            playerData.setMP(playerData.getMaxMP());
            
            playerData.setMaxAP(10);
            playerData.setConsumedAP(0);
            
            //TODO Change this with the SOA choice
            playerData.setStrength(1);
            playerData.setMagic(1);
            playerData.setDefense(1);
            
            playerData.clearAbilities();
			while (playerData.getLevel() < level) {
				playerData.addExperience(player, playerData.getExpNeeded(level - 1, playerData.getExperience()));
			}
			player.heal(playerData.getMaxHP());
			playerData.setMP(playerData.getMaxMP());

			if(player != context.getSource().asPlayer()) {
				context.getSource().sendFeedback(new TranslationTextComponent("Set "+player.getDisplayName().getFormattedText()+" level to "+level), true);
			}
			player.sendMessage(new TranslationTextComponent("Your level is now "+level));
		}
		return 1;
	}
	

	private static int addValue(CommandContext<CommandSource> context) throws CommandSyntaxException {
		Collection<ServerPlayerEntity> players = getPlayers(context, 3);
		int value = IntegerArgumentType.getInteger(context, "value");
		
		for (ServerPlayerEntity player : players) {
			IPlayerCapabilities playerData = ModCapabilities.getPlayer(player);
			playerData.setMunny(playerData.getMunny() + value);
			if(player != context.getSource().asPlayer()) {
				context.getSource().sendFeedback(new TranslationTextComponent("Added "+value+" munny to "+player.getDisplayName().getFormattedText()), true);
			}
			player.sendMessage(new TranslationTextComponent("Your munny has been increased by "+value));		}
		return 1;
	}
	
	
	private static int removeValue(CommandContext<CommandSource> context) throws CommandSyntaxException {
		Collection<ServerPlayerEntity> players = getPlayers(context, 3);
		int value = IntegerArgumentType.getInteger(context, "value");
		
		for (ServerPlayerEntity player : players) {
			IPlayerCapabilities playerData = ModCapabilities.getPlayer(player);
			playerData.setMunny(playerData.getMunny() - value);
			if(player != context.getSource().asPlayer()) {
				context.getSource().sendFeedback(new TranslationTextComponent("Taken "+value+" munny from "+player.getDisplayName().getFormattedText()), true);
			}
			player.sendMessage(new TranslationTextComponent("Your munny has been decreased by "+value));
		}
		return 1;
	}
	
}
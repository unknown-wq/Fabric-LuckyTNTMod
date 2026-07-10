package luckytnt.registry;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;

import luckytnt.commands.LTMDisastersCommand;
import luckytnt.commands.RandomTNTCommand;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.Commands;

import net.minecraft.commands.CommandSourceStack;

public class CommandRegistry {
	
	private static final CommandRegistrationCallback LTMDISASTER = new CommandRegistrationCallback() {
		
		@Override
		public void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext registryAccess, Commands.CommandSelection environment) {
			dispatcher.register(Commands.literal("ltmdisaster").requires(Commands.hasPermission(Commands.LEVEL_GAMEMASTERS)).executes(LTMDisastersCommand::executeGetActiveDisasters)
					.then(Commands.literal("clear").executes(LTMDisastersCommand::executeClear))
					.then(Commands.literal("doomsday").executes(LTMDisastersCommand::executeDoomsday))
					.then(Commands.literal("toxic_clouds").executes(LTMDisastersCommand::executeToxicClouds))
					.then(Commands.literal("ice_age").executes(LTMDisastersCommand::executeIceAge))
					.then(Commands.literal("heat_death").executes(LTMDisastersCommand::executeHeatDeath))
					.then(Commands.literal("tnt_rain").executes(LTMDisastersCommand::executeTNTRain))
			);
		}
	};
	private static final CommandRegistrationCallback RANDOMTNT = new CommandRegistrationCallback() {
		
		@Override
		public void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext registryAccess, Commands.CommandSelection environment) {
			dispatcher.register(Commands.literal("randomtnt").requires(Commands.hasPermission(Commands.LEVEL_GAMEMASTERS))
					
					.then(Commands.literal("normal_tnt")
					.then(Commands.argument("amount", IntegerArgumentType.integer(1)).executes((p) -> {
						return RandomTNTCommand.executeGiveItems(p.getSource(), IntegerArgumentType.getInteger(p, "amount"), true, "n");
				    })
					.then(Commands.argument("allowDuplicate", BoolArgumentType.bool()).executes((p) -> {
						return RandomTNTCommand.executeGiveItems(p.getSource(), IntegerArgumentType.getInteger(p, "amount"), BoolArgumentType.getBool(p, "allowDuplicate"), "n");
					}))))
					
					.then(Commands.literal("dynamite")
					.then(Commands.argument("amount", IntegerArgumentType.integer(1)).executes((p) -> {
						return RandomTNTCommand.executeGiveItems(p.getSource(), IntegerArgumentType.getInteger(p, "amount"), true, "dy");
					})
					.then(Commands.argument("allowDuplicate", BoolArgumentType.bool()).executes((p) -> {
						return RandomTNTCommand.executeGiveItems(p.getSource(), IntegerArgumentType.getInteger(p, "amount"), BoolArgumentType.getBool(p, "allowDuplicate"), "dy");
					}))))
					
					.then(Commands.literal("god_tnt")
					.then(Commands.argument("amount", IntegerArgumentType.integer(1)).executes((p) -> {
						return RandomTNTCommand.executeGiveItems(p.getSource(), IntegerArgumentType.getInteger(p, "amount"), true, "g");
					})
					.then(Commands.argument("allowDuplicate", BoolArgumentType.bool()).executes((p) -> {
						return RandomTNTCommand.executeGiveItems(p.getSource(), IntegerArgumentType.getInteger(p, "amount"), BoolArgumentType.getBool(p, "allowDuplicate"), "g");
					}))))
					
					.then(Commands.literal("doomsday_tnt")
					.then(Commands.argument("amount", IntegerArgumentType.integer(1)).executes((p) -> {
						return RandomTNTCommand.executeGiveItems(p.getSource(), IntegerArgumentType.getInteger(p, "amount"), true, "d");
					})
					.then(Commands.argument("allowDuplicate", BoolArgumentType.bool()).executes((p) -> {
						return RandomTNTCommand.executeGiveItems(p.getSource(), IntegerArgumentType.getInteger(p, "amount"), BoolArgumentType.getBool(p, "allowDuplicate"), "d");
					}))))
			);
		}
	};

	public static void init() {
		CommandRegistrationCallback.EVENT.register(LTMDISASTER);
		CommandRegistrationCallback.EVENT.register(RANDOMTNT);
		
	}
}

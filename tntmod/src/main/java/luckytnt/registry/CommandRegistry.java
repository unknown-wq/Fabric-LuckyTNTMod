package luckytnt.registry;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;

import luckytnt.commands.LTMDisastersCommand;
import luckytnt.commands.RandomTNTCommand;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.CommandManager.RegistrationEnvironment;
import net.minecraft.server.command.ServerCommandSource;

public class CommandRegistry {
	
	private static final CommandRegistrationCallback LTMDISASTER = new CommandRegistrationCallback() {
		
		@Override
		public void register(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess registryAccess, RegistrationEnvironment environment) {
			dispatcher.register(CommandManager.literal("ltmdisaster").requires(s -> s.hasPermissionLevel(2)).executes(LTMDisastersCommand::executeGetActiveDisasters)
					.then(CommandManager.literal("clear").executes(LTMDisastersCommand::executeClear))
					.then(CommandManager.literal("doomsday").executes(LTMDisastersCommand::executeDoomsday))
					.then(CommandManager.literal("toxic_clouds").executes(LTMDisastersCommand::executeToxicClouds))
					.then(CommandManager.literal("ice_age").executes(LTMDisastersCommand::executeIceAge))
					.then(CommandManager.literal("heat_death").executes(LTMDisastersCommand::executeHeatDeath))
					.then(CommandManager.literal("tnt_rain").executes(LTMDisastersCommand::executeTNTRain))
			);
		}
	};
	private static final CommandRegistrationCallback RANDOMTNT = new CommandRegistrationCallback() {
		
		@Override
		public void register(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess registryAccess, RegistrationEnvironment environment) {
			dispatcher.register(CommandManager.literal("randomtnt").requires(s -> s.hasPermissionLevel(2))
					
					.then(CommandManager.literal("normal_tnt")
					.then(CommandManager.argument("amount", IntegerArgumentType.integer(1)).executes((p) -> {
						return RandomTNTCommand.executeGiveItems(p.getSource(), IntegerArgumentType.getInteger(p, "amount"), true, "n");
				    })
					.then(CommandManager.argument("allowDuplicate", BoolArgumentType.bool()).executes((p) -> {
						return RandomTNTCommand.executeGiveItems(p.getSource(), IntegerArgumentType.getInteger(p, "amount"), BoolArgumentType.getBool(p, "allowDuplicate"), "n");
					}))))
					
					.then(CommandManager.literal("dynamite")
					.then(CommandManager.argument("amount", IntegerArgumentType.integer(1)).executes((p) -> {
						return RandomTNTCommand.executeGiveItems(p.getSource(), IntegerArgumentType.getInteger(p, "amount"), true, "dy");
					})
					.then(CommandManager.argument("allowDuplicate", BoolArgumentType.bool()).executes((p) -> {
						return RandomTNTCommand.executeGiveItems(p.getSource(), IntegerArgumentType.getInteger(p, "amount"), BoolArgumentType.getBool(p, "allowDuplicate"), "dy");
					}))))
					
					.then(CommandManager.literal("god_tnt")
					.then(CommandManager.argument("amount", IntegerArgumentType.integer(1)).executes((p) -> {
						return RandomTNTCommand.executeGiveItems(p.getSource(), IntegerArgumentType.getInteger(p, "amount"), true, "g");
					})
					.then(CommandManager.argument("allowDuplicate", BoolArgumentType.bool()).executes((p) -> {
						return RandomTNTCommand.executeGiveItems(p.getSource(), IntegerArgumentType.getInteger(p, "amount"), BoolArgumentType.getBool(p, "allowDuplicate"), "g");
					}))))
					
					.then(CommandManager.literal("doomsday_tnt")
					.then(CommandManager.argument("amount", IntegerArgumentType.integer(1)).executes((p) -> {
						return RandomTNTCommand.executeGiveItems(p.getSource(), IntegerArgumentType.getInteger(p, "amount"), true, "d");
					})
					.then(CommandManager.argument("allowDuplicate", BoolArgumentType.bool()).executes((p) -> {
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

package luckytnt.commands;


import com.mojang.brigadier.context.CommandContext;

import luckytnt.LevelVariables;
import luckytnt.config.LuckyTNTConfigValues;
import net.minecraft.world.entity.player.Player;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.network.chat.Component;

public class LTMDisastersCommand {

	/**
	 * Rolls the duration of a disaster: the configured maximum plus up to one more of it.
	 * (The cast used to sit on Math.random() alone, so the random term was always exactly 0.)
	 */
	private static int rollDisasterTime() {
		int max = LuckyTNTConfigValues.MAXIMUM_DISASTER_TIME.get();
		return 1000 * max + (int)(Math.random() * 1000 * max);
	}

	public static int executeGetActiveDisasters(CommandContext<CommandSourceStack> command) {
		if(command.getSource().getEntity() instanceof Player) {
			ServerLevel level = command.getSource().getLevel();
			LevelVariables variables = LevelVariables.get(level);
			boolean disasterActive = false;
			if(variables.doomsdayTime > 0) {
				int minutes = variables.doomsdayTime / 1200;
				command.getSource().sendSuccess(() -> Component.translatable("command.ltmdisaster.doomsdayactive").append(Component.literal("" + minutes)).append(Component.translatable("command.ltmdisaster.minute")), false);
				disasterActive = true;
			}
			if(variables.toxicCloudsTime > 0) {
				int minutes = variables.toxicCloudsTime / 1200;
				command.getSource().sendSuccess(() -> Component.translatable("command.ltmdisaster.toxicactive").append(Component.literal("" + minutes)).append(Component.translatable("command.ltmdisaster.minute")), false);
				disasterActive = true;
			}
			if(variables.iceAgeTime > 0) {
				int minutes = variables.iceAgeTime / 1200;
				command.getSource().sendSuccess(() -> Component.translatable("command.ltmdisaster.iceageactive").append(Component.literal("" + minutes)).append(Component.translatable("command.ltmdisaster.minute")), false);
				disasterActive = true;
			}
			if(variables.heatDeathTime > 0) {
				int minutes = variables.heatDeathTime / 1200;
				command.getSource().sendSuccess(() -> Component.translatable("command.ltmdisaster.heatdeathactive").append(Component.literal("" + minutes)).append(Component.translatable("command.ltmdisaster.minute")), false);
				disasterActive = true;
			}
			if(variables.tntRainTime > 0) {
				int minutes = variables.tntRainTime / 1200;
				command.getSource().sendSuccess(() -> Component.translatable("command.ltmdisaster.tntrainactive").append(Component.literal("" + minutes)).append(Component.translatable("command.ltmdisaster.minute")), false);
				disasterActive = true;
			}
			if(!disasterActive) {
				command.getSource().sendSuccess(() -> Component.translatable("command.ltmdisaster.nothingactive"), false);
			}
		}
		return 1;
	}

	public static int executeClear(CommandContext<CommandSourceStack> command) {
		if(command.getSource().getEntity() instanceof Player) {
			ServerLevel level = command.getSource().getLevel();
			LevelVariables variables = LevelVariables.get(level);
			variables.doomsdayTime = 0;
			variables.toxicCloudsTime = 0;
			variables.iceAgeTime = 0;
			variables.heatDeathTime = 0;
			variables.tntRainTime = 0;
			variables.sync(level);
			level.getServer().setWeatherParameters(0, 0, false, false);
			command.getSource().sendSuccess(() -> Component.translatable("command.ltmdisaster.clear"), false);
		}
		return 1;
	}

	public static int executeDoomsday(CommandContext<CommandSourceStack> command) {
		if(command.getSource().getEntity() instanceof Player) {
			ServerLevel level = command.getSource().getLevel();
			LevelVariables variables = LevelVariables.get(level);
			variables.doomsdayTime = rollDisasterTime();
			variables.sync(level);
			level.getServer().setWeatherParameters(0, variables.doomsdayTime, true, true);
			command.getSource().sendSuccess(() -> Component.translatable("command.ltmdisaster.doomsday"), false);
		}
		return 1;
	}

	public static int executeToxicClouds(CommandContext<CommandSourceStack> command) {
		if(command.getSource().getEntity() instanceof Player) {
			ServerLevel level = command.getSource().getLevel();
			LevelVariables variables = LevelVariables.get(level);
			variables.toxicCloudsTime = rollDisasterTime();
			variables.sync(level);
			command.getSource().sendSuccess(() -> Component.translatable("command.ltmdisaster.toxicclouds"), false);
		}
		return 1;
	}

	public static int executeIceAge(CommandContext<CommandSourceStack> command) {
		if(command.getSource().getEntity() instanceof Player) {
			ServerLevel level = command.getSource().getLevel();
			LevelVariables variables = LevelVariables.get(level);
			variables.iceAgeTime = rollDisasterTime();
			variables.sync(level);
			level.getServer().setWeatherParameters(0, variables.iceAgeTime, true, true);
			command.getSource().sendSuccess(() -> Component.translatable("command.ltmdisaster.iceage"), false);
		}
		return 1;
	}

	public static int executeHeatDeath(CommandContext<CommandSourceStack> command) {
		if(command.getSource().getEntity() instanceof Player) {
			ServerLevel level = command.getSource().getLevel();
			LevelVariables variables = LevelVariables.get(level);
			variables.heatDeathTime = rollDisasterTime();
			variables.sync(level);
			command.getSource().sendSuccess(() -> Component.translatable("command.ltmdisaster.heatdeath"), false);
		}
		return 1;
	}

	public static int executeTNTRain(CommandContext<CommandSourceStack> command) {
		if(command.getSource().getEntity() instanceof Player) {
			ServerLevel level = command.getSource().getLevel();
			LevelVariables variables = LevelVariables.get(level);
			variables.tntRainTime = rollDisasterTime();
			variables.sync(level);
			command.getSource().sendSuccess(() -> Component.translatable("command.ltmdisaster.tntrain"), false);
		}
		return 1;
	}
}

package luckytnt.commands;


import com.mojang.brigadier.context.CommandContext;

import luckytnt.LevelVariables;
import luckytnt.config.LuckyTNTConfigValues;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;

public class LTMDisastersCommand {

	public static int executeGetActiveDisasters(CommandContext<ServerCommandSource> command) {
		if(command.getSource().getEntity() instanceof PlayerEntity) {
			ServerWorld level = command.getSource().getWorld();
			boolean disasterActive = false;
			if(LevelVariables.get(level).doomsdayTime > 0) {
				command.getSource().sendFeedback(() -> Text.translatable("command.ltmdisaster.doomsdayactive").append(Text.literal("" + LevelVariables.get(level).doomsdayTime / 1200)).append(Text.translatable("command.ltmdisaster.minute")), false);
				disasterActive = true;
			}
			if(LevelVariables.get(level).toxicCloudsTime > 0) {
				command.getSource().sendFeedback(() -> Text.translatable("command.ltmdisaster.toxicactive").append(Text.literal("" + LevelVariables.get(level).toxicCloudsTime / 1200)).append(Text.translatable("command.ltmdisaster.minute")), false);
				disasterActive = true;
			}
			if(LevelVariables.get(level).iceAgeTime > 0) {
				command.getSource().sendFeedback(() -> Text.translatable("command.ltmdisaster.iceageactive").append(Text.literal("" + LevelVariables.get(level).iceAgeTime / 1200)).append(Text.translatable("command.ltmdisaster.minute")), false);
				disasterActive = true;
			}
			if(LevelVariables.get(level).heatDeathTime > 0) {
				command.getSource().sendFeedback(() -> Text.translatable("command.ltmdisaster.heatdeathactive").append(Text.literal("" + LevelVariables.get(level).heatDeathTime / 1200)).append(Text.translatable("command.ltmdisaster.minute")), false);
				disasterActive = true;
			}
			if(LevelVariables.get(level).tntRainTime > 0) {
				command.getSource().sendFeedback(() -> Text.translatable("command.ltmdisaster.tntrainactive").append(Text.literal("" + LevelVariables.get(level).tntRainTime / 1200)).append(Text.translatable("command.ltmdisaster.minute")), false);
				disasterActive = true;
			}
			if(!disasterActive) {
				command.getSource().sendFeedback(() -> Text.translatable("command.ltmdisaster.nothingactive"), false);				
			}
		}
		return 1;
	}
	
	public static int executeClear(CommandContext<ServerCommandSource> command) {
		if(command.getSource().getEntity() instanceof PlayerEntity) {
			ServerWorld level = command.getSource().getWorld();
			LevelVariables.get(level).doomsdayTime = 0;
			LevelVariables.get(level).toxicCloudsTime = 0;
			LevelVariables.get(level).iceAgeTime = 0;
			LevelVariables.get(level).heatDeathTime = 0;
			LevelVariables.get(level).tntRainTime = 0;
			LevelVariables.get(level).sync(level);
			level.setWeather(0, 0, false, false);
			command.getSource().sendFeedback(() -> Text.translatable("command.ltmdisaster.clear"), false);
		}		
		return 1;
	}
	
	public static int executeDoomsday(CommandContext<ServerCommandSource> command) {
		if(command.getSource().getEntity() instanceof PlayerEntity) {
			ServerWorld level = command.getSource().getWorld();
			LevelVariables.get(level).doomsdayTime = 1000 * LuckyTNTConfigValues.MAXIMUM_DISASTER_TIME.get() + (int)Math.random() * 1000 * LuckyTNTConfigValues.MAXIMUM_DISASTER_TIME.get();
			LevelVariables.get(level).sync(level);
			level.setWeather(0, LevelVariables.get(level).doomsdayTime, true, true);
			command.getSource().sendFeedback(() -> Text.translatable("command.ltmdisaster.doomsday"), false);
		}		
		return 1;
	}
	
	public static int executeToxicClouds(CommandContext<ServerCommandSource> command) {
		if(command.getSource().getEntity() instanceof PlayerEntity) {
			ServerWorld level = command.getSource().getWorld();
			LevelVariables.get(level).toxicCloudsTime = 1000 * LuckyTNTConfigValues.MAXIMUM_DISASTER_TIME.get() + (int)Math.random() * 1000 * LuckyTNTConfigValues.MAXIMUM_DISASTER_TIME.get();
			LevelVariables.get(level).sync(level);
			command.getSource().sendFeedback(() -> Text.translatable("command.ltmdisaster.toxicclouds"), false);
		}		
		return 1;
	}
	
	public static int executeIceAge(CommandContext<ServerCommandSource> command) {
		if(command.getSource().getEntity() instanceof PlayerEntity) {
			ServerWorld level = command.getSource().getWorld();
			LevelVariables.get(level).iceAgeTime = 1000 * LuckyTNTConfigValues.MAXIMUM_DISASTER_TIME.get() + (int)Math.random() * 1000 * LuckyTNTConfigValues.MAXIMUM_DISASTER_TIME.get();
			LevelVariables.get(level).sync(level);
			level.setWeather(0, LevelVariables.get(level).iceAgeTime, true, true);
			command.getSource().sendFeedback(() -> Text.translatable("command.ltmdisaster.iceage"), false);
		}		
		return 1;
	}
	
	public static int executeHeatDeath(CommandContext<ServerCommandSource> command) {
		if(command.getSource().getEntity() instanceof PlayerEntity) {
			ServerWorld level = command.getSource().getWorld();
			LevelVariables.get(level).heatDeathTime = 1000 * LuckyTNTConfigValues.MAXIMUM_DISASTER_TIME.get() + (int)Math.random() * 1000 * LuckyTNTConfigValues.MAXIMUM_DISASTER_TIME.get();
			LevelVariables.get(level).sync(level);
			command.getSource().sendFeedback(() -> Text.translatable("command.ltmdisaster.heatdeath"), false);
		}		
		return 1;
	}
	
	public static int executeTNTRain(CommandContext<ServerCommandSource> command) {
		if(command.getSource().getEntity() instanceof PlayerEntity) {
			ServerWorld level = command.getSource().getWorld();
			LevelVariables.get(level).tntRainTime = 1000 * LuckyTNTConfigValues.MAXIMUM_DISASTER_TIME.get() + (int)Math.random() * 1000 * LuckyTNTConfigValues.MAXIMUM_DISASTER_TIME.get();
			LevelVariables.get(level).sync(level);
			command.getSource().sendFeedback(() -> Text.translatable("command.ltmdisaster.tntrain"), false);
		}		
		return 1;
	}
}

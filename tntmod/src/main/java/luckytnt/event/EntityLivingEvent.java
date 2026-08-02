package luckytnt.event;

import luckytnt.LevelVariables;
import luckytnt.config.LuckyTNTConfigValues;
import luckytntlib.util.LuckyTNTEntityExtension;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.damagesource.DamageSources;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.level.LightLayer;

public class EntityLivingEvent {

	public static void playerLivingTick(LivingEntity entity) {
		if(entity instanceof Player player && player instanceof LuckyTNTEntityExtension lent) {
			if(lent.getAdditionalPersistentData().getIntOr("shakeTime", 0) > 0) {
				CompoundTag tag = lent.getAdditionalPersistentData();
				tag.putInt("shakeTime", tag.getIntOr("shakeTime", 0) - 1);
				lent.setAdditionalPersistentData(tag);
			}
		}
	}
	
	public static void onLivingTick(LivingEntity ent) {
		if(ent == null) {
			return;
		}
		if(!(ent.level() instanceof ServerLevel sLevel) || !(ent instanceof LuckyTNTEntityExtension lentity)) {
			return;
		}

		//one data storage lookup instead of two
		LevelVariables variables = LevelVariables.get(sLevel);
		boolean iceAge = variables.iceAgeTime > 0;
		boolean heatDeath = variables.heatDeathTime > 0;

		//each key is read exactly once instead of ~14 times
		CompoundTag tag = lentity.getAdditionalPersistentData();
		int freezeTime = tag.getIntOr("freezeTime", 0);
		int heatTime = tag.getIntOr("heatTime", 0);

		//common case: no disaster running and no leftover freeze/heat state on this entity
		if(!iceAge && !heatDeath && freezeTime == 0 && heatTime == 0) {
			return;
		}

		//matches the old '(ent instanceof Player pl && !pl.isCreative()) || !(ent instanceof Player)'
		boolean affected = !(ent instanceof Player pl) || !pl.isCreative();

		//FREEZE
		int newFreezeTime = freezeTime;
		if(iceAge) {
			if(affected) {
				int brightness = sLevel.getBrightness(LightLayer.BLOCK, new BlockPos(Mth.floor(ent.getX()), Mth.floor(ent.getY()), Mth.floor(ent.getZ())));
				if(brightness < 11) {
					newFreezeTime = freezeTime + LuckyTNTConfigValues.AVERAGE_DIASTER_INTENSITY.get().intValue();
				}
				else if(freezeTime > 0) {
					newFreezeTime = (int)Mth.clamp(freezeTime - 0.5f * brightness, 0, Double.POSITIVE_INFINITY);
				}
			}
			else {
				newFreezeTime = 0;
			}
		} else if(freezeTime > 0) {
			newFreezeTime = (int)Mth.clamp(freezeTime - 10, 0, Double.POSITIVE_INFINITY);
		}
		if(newFreezeTime != freezeTime) {
			freezeTime = newFreezeTime;
			tag.putInt("freezeTime", freezeTime);
			lentity.setAdditionalPersistentData(tag);
		}

		if(freezeTime >= 600) {
			ent.addEffect(new MobEffectInstance(MobEffects.SLOWNESS, 100, freezeTime / 600));
			ent.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 100, freezeTime / 900));
		}
		if(freezeTime >= 1200 && freezeTime % 10 == 0) {
			DamageSources sources = ent.level().damageSources();
			ent.hurtServer(sLevel, sources.freeze(), 1);
		}

		//HEAT
		int newHeatTime = heatTime;
		if(heatDeath) {
			if(affected) {
				if(!sLevel.getBlockState(ent.blockPosition()).is(Blocks.WATER)) {
					newHeatTime = heatTime + LuckyTNTConfigValues.AVERAGE_DIASTER_INTENSITY.get().intValue();
				}
				else if(heatTime > 0) {
					newHeatTime = (int)Mth.clamp(heatTime - 20, 0, Double.POSITIVE_INFINITY);
				}
			}
			else {
				newHeatTime = 0;
			}
		} else if(heatTime > 0) {
			newHeatTime = (int)Mth.clamp(heatTime - 20, 0, Double.POSITIVE_INFINITY);
		}
		if(newHeatTime != heatTime) {
			heatTime = newHeatTime;
			tag.putInt("heatTime", heatTime);
			lentity.setAdditionalPersistentData(tag);
		}

		if(heatTime >= 600) {
			ent.addEffect(new MobEffectInstance(MobEffects.SLOWNESS, 100, 0));
			ent.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 100, 0));
		}
		if(heatTime >= 1200 && heatTime % 10 == 0) {
			ent.igniteForSeconds(heatTime / 800);
			ent.addEffect(new MobEffectInstance(MobEffects.SLOWNESS, 100, 1));
			ent.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 100, 1));
		}
	}
}

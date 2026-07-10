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
		if(ent != null) {
			if(ent.level() instanceof ServerLevel sLevel && ent instanceof LuckyTNTEntityExtension lentity) {
				if(LevelVariables.get(sLevel).iceAgeTime > 0) {
					if((ent instanceof Player pl && !pl.isCreative()) || !(ent instanceof Player)) {
						if(sLevel.getBrightness(LightLayer.BLOCK, new BlockPos(Mth.floor(ent.getX()), Mth.floor(ent.getY()), Mth.floor(ent.getZ()))) < 11) {
							CompoundTag tag = lentity.getAdditionalPersistentData();
							tag.putInt("freezeTime", lentity.getAdditionalPersistentData().getIntOr("freezeTime", 0) + LuckyTNTConfigValues.AVERAGE_DIASTER_INTENSITY.get().intValue());
							lentity.setAdditionalPersistentData(tag);
						}
						else if(lentity.getAdditionalPersistentData().getIntOr("freezeTime", 0) > 0){
							CompoundTag tag = lentity.getAdditionalPersistentData();
							tag.putInt("freezeTime", (int)Mth.clamp(lentity.getAdditionalPersistentData().getIntOr("freezeTime", 0) - 0.5f * sLevel.getBrightness(LightLayer.BLOCK, new BlockPos(Mth.floor(ent.getX()), Mth.floor(ent.getY()), Mth.floor(ent.getZ()))), 0, Double.POSITIVE_INFINITY));
							lentity.setAdditionalPersistentData(tag);
						}
					}
					else {
						CompoundTag tag = lentity.getAdditionalPersistentData();
						tag.putInt("freezeTime", 0);
						lentity.setAdditionalPersistentData(tag);
					}
				} else if(lentity.getAdditionalPersistentData().getIntOr("freezeTime", 0) > 0) {
					CompoundTag tag = lentity.getAdditionalPersistentData();
					tag.putInt("freezeTime", (int)Mth.clamp(lentity.getAdditionalPersistentData().getIntOr("freezeTime", 0) - 10, 0, Double.POSITIVE_INFINITY));
					lentity.setAdditionalPersistentData(tag);
				}
				if(lentity.getAdditionalPersistentData().getIntOr("freezeTime", 0) >= 600) {
					ent.addEffect(new MobEffectInstance(MobEffects.SLOWNESS, 100, lentity.getAdditionalPersistentData().getIntOr("freezeTime", 0) / 600));
					ent.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 100, lentity.getAdditionalPersistentData().getIntOr("freezeTime", 0) / 900));
				}
				if(lentity.getAdditionalPersistentData().getIntOr("freezeTime", 0) >= 1200 && lentity.getAdditionalPersistentData().getIntOr("freezeTime", 0) % 10 == 0) {
					DamageSources sources = ent.level().damageSources();
					ent.hurtServer(sLevel, sources.freeze(), 1);
				}
				
				
				if(LevelVariables.get(sLevel).heatDeathTime > 0) {
					if((ent instanceof Player pl && !pl.isCreative()) || !(ent instanceof Player)) {
						if(!sLevel.getBlockState(ent.blockPosition()).is(Blocks.WATER)) {
							CompoundTag tag = lentity.getAdditionalPersistentData();
							tag.putInt("heatTime", lentity.getAdditionalPersistentData().getIntOr("heatTime", 0) + LuckyTNTConfigValues.AVERAGE_DIASTER_INTENSITY.get().intValue());
							lentity.setAdditionalPersistentData(tag);
						}
						else if(lentity.getAdditionalPersistentData().getIntOr("heatTime", 0) > 0){
							CompoundTag tag = lentity.getAdditionalPersistentData();
							tag.putInt("heatTime", (int)Mth.clamp(lentity.getAdditionalPersistentData().getIntOr("heatTime", 0) - 20, 0, Double.POSITIVE_INFINITY));
							lentity.setAdditionalPersistentData(tag);
						}
					}
					else {
						CompoundTag tag = lentity.getAdditionalPersistentData();
						tag.putInt("heatTime", 0);
						lentity.setAdditionalPersistentData(tag);
					}
				} else if(lentity.getAdditionalPersistentData().getIntOr("heatTime", 0) > 0) {
					CompoundTag tag = lentity.getAdditionalPersistentData();
					tag.putInt("heatTime", (int)Mth.clamp(lentity.getAdditionalPersistentData().getIntOr("heatTime", 0) - 20, 0, Double.POSITIVE_INFINITY));
					lentity.setAdditionalPersistentData(tag);
				}
				if(lentity.getAdditionalPersistentData().getIntOr("heatTime", 0) >= 600) {
					ent.addEffect(new MobEffectInstance(MobEffects.SLOWNESS, 100, 0));
					ent.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 100, 0));
				}
				if(lentity.getAdditionalPersistentData().getIntOr("heatTime", 0) >= 1200 && lentity.getAdditionalPersistentData().getIntOr("heatTime", 0) % 10 == 0) {
					ent.igniteForSeconds(lentity.getAdditionalPersistentData().getIntOr("heatTime", 0) / 800);
					ent.addEffect(new MobEffectInstance(MobEffects.SLOWNESS, 100, 1));
					ent.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 100, 1));
				}
			}
		}
	}
}

package luckytnt.event;

import luckytnt.LevelVariables;
import luckytnt.config.LuckyTNTConfigValues;
import luckytntlib.util.LuckyTNTEntityExtension;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.damagesource.DamageSources;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.LightType;

public class EntityLivingEvent {

	public static void playerLivingTick(LivingEntity entity) {
		if(entity instanceof Player player && player instanceof LuckyTNTEntityExtension lent) {
			if(lent.getAdditionalPersistentData().getInt("shakeTime") > 0) {
				CompoundTag tag = lent.getAdditionalPersistentData();
				tag.putInt("shakeTime", tag.getInt("shakeTime") - 1);
				lent.setAdditionalPersistentData(tag);
			}
		}
	}
	
	public static void onLivingTick(LivingEntity ent) {
		if(ent != null) {
			if(ent.level() instanceof ServerLevel sLevel && ent instanceof LuckyTNTEntityExtension lentity) {
				if(LevelVariables.get(sLevel).iceAgeTime > 0) {
					if((ent instanceof Player pl && !pl.isCreative()) || !(ent instanceof Player)) {
						if(sLevel.getLightLevel(LightType.BLOCK, new BlockPos(Mth.floor(ent.getX()), Mth.floor(ent.getY()), Mth.floor(ent.getZ()))) < 11) {
							CompoundTag tag = lentity.getAdditionalPersistentData();
							tag.putInt("freezeTime", lentity.getAdditionalPersistentData().getInt("freezeTime") + LuckyTNTConfigValues.AVERAGE_DIASTER_INTENSITY.get().intValue());
							lentity.setAdditionalPersistentData(tag);
						}
						else if(lentity.getAdditionalPersistentData().getInt("freezeTime") > 0){
							CompoundTag tag = lentity.getAdditionalPersistentData();
							tag.putInt("freezeTime", (int)Mth.clamp(lentity.getAdditionalPersistentData().getInt("freezeTime") - 0.5f * sLevel.getLightLevel(LightType.BLOCK, new BlockPos(Mth.floor(ent.getX()), Mth.floor(ent.getY()), Mth.floor(ent.getZ()))), 0, Double.POSITIVE_INFINITY));
							lentity.setAdditionalPersistentData(tag);
						}
					}
					else {
						CompoundTag tag = lentity.getAdditionalPersistentData();
						tag.putInt("freezeTime", 0);
						lentity.setAdditionalPersistentData(tag);
					}
				} else if(lentity.getAdditionalPersistentData().getInt("freezeTime") > 0) {
					CompoundTag tag = lentity.getAdditionalPersistentData();
					tag.putInt("freezeTime", (int)Mth.clamp(lentity.getAdditionalPersistentData().getInt("freezeTime") - 10, 0, Double.POSITIVE_INFINITY));
					lentity.setAdditionalPersistentData(tag);
				}
				if(lentity.getAdditionalPersistentData().getInt("freezeTime") >= 600) {
					ent.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, 100, lentity.getAdditionalPersistentData().getInt("freezeTime") / 600));
					ent.addStatusEffect(new StatusEffectInstance(StatusEffects.WEAKNESS, 100, lentity.getAdditionalPersistentData().getInt("freezeTime") / 900));
				}
				if(lentity.getAdditionalPersistentData().getInt("freezeTime") >= 1200 && lentity.getAdditionalPersistentData().getInt("freezeTime") % 10 == 0) {
					DamageSources sources = ent.level().getDamageSources();
					ent.damage(sources.freeze(), 1);
				}
				
				
				if(LevelVariables.get(sLevel).heatDeathTime > 0) {
					if((ent instanceof Player pl && !pl.isCreative()) || !(ent instanceof Player)) {
						if(!sLevel.getBlockState(ent.getBlockPos()).isOf(Blocks.WATER)) {
							CompoundTag tag = lentity.getAdditionalPersistentData();
							tag.putInt("heatTime", lentity.getAdditionalPersistentData().getInt("heatTime") + LuckyTNTConfigValues.AVERAGE_DIASTER_INTENSITY.get().intValue());
							lentity.setAdditionalPersistentData(tag);
						}
						else if(lentity.getAdditionalPersistentData().getInt("heatTime") > 0){
							CompoundTag tag = lentity.getAdditionalPersistentData();
							tag.putInt("heatTime", (int)Mth.clamp(lentity.getAdditionalPersistentData().getInt("heatTime") - 20, 0, Double.POSITIVE_INFINITY));
							lentity.setAdditionalPersistentData(tag);
						}
					}
					else {
						CompoundTag tag = lentity.getAdditionalPersistentData();
						tag.putInt("heatTime", 0);
						lentity.setAdditionalPersistentData(tag);
					}
				} else if(lentity.getAdditionalPersistentData().getInt("heatTime") > 0) {
					CompoundTag tag = lentity.getAdditionalPersistentData();
					tag.putInt("heatTime", (int)Mth.clamp(lentity.getAdditionalPersistentData().getInt("heatTime") - 20, 0, Double.POSITIVE_INFINITY));
					lentity.setAdditionalPersistentData(tag);
				}
				if(lentity.getAdditionalPersistentData().getInt("heatTime") >= 600) {
					ent.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, 100, 0));
					ent.addStatusEffect(new StatusEffectInstance(StatusEffects.WEAKNESS, 100, 0));
				}
				if(lentity.getAdditionalPersistentData().getInt("heatTime") >= 1200 && lentity.getAdditionalPersistentData().getInt("heatTime") % 10 == 0) {
					ent.setOnFireFor(lentity.getAdditionalPersistentData().getInt("heatTime") / 800);
					ent.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, 100, 1));
					ent.addStatusEffect(new StatusEffectInstance(StatusEffects.WEAKNESS, 100, 1));
				}
			}
		}
	}
}

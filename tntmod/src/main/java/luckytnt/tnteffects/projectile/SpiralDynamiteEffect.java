package luckytnt.tnteffects.projectile;

import net.minecraft.world.entity.EntitySpawnReason;

import luckytnt.registry.EntityRegistry;
import luckytnt.registry.ItemRegistry;
import luckytntlib.entity.LExplosiveProjectile;
import luckytntlib.util.IExplosiveEntity;
import luckytntlib.util.tnteffects.PrimedTNTEffect;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.Item;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;

public class SpiralDynamiteEffect extends PrimedTNTEffect{

	@Override
	public void explosionTick(IExplosiveEntity entity) {
		Entity ent = (Entity)entity;
		// Kept unguarded so the client keeps floating smoothly between tracker updates.
		ent.setDeltaMovement(ent.getDeltaMovement().add(0f, 0.08f, 0f));
		if(!(entity.getLevel() instanceof ServerLevel)) {
			return;
		}
		if(entity.getTNTFuse() < 30) {
			if(entity.getTNTFuse() % 3 == 0) {
				ent.setXRot(180f);
				ent.setYRot(entity.getPersistentData().getFloatOr("angle", 0f) + 30f);
				CompoundTag tag = entity.getPersistentData();
				tag.putFloat("angle", ent.getYRot());
				tag.putFloat("spiral_power", Mth.clamp(entity.getPersistentData().getFloatOr("spiral_power", 0f) + 0.12f, 0.2f, Float.MAX_VALUE));
				entity.setPersistentData(tag);
				LExplosiveProjectile spiral_tnt = EntityRegistry.SPIRAL_PROJECTILE.get().create(entity.getLevel(), EntitySpawnReason.MOB_SUMMONED);
				spiral_tnt.setPos(entity.x(), entity.y(), entity.z());
				spiral_tnt.setOwner(entity.owner());
				spiral_tnt.shoot(ent.getLookAngle().x, ent.getLookAngle().y, ent.getLookAngle().z, entity.getPersistentData().getFloatOr("spiral_power", 0f), 0);
				entity.getLevel().playSound(null, toBlockPos(entity.getPos()), SoundEvents.DISPENSER_LAUNCH, SoundSource.BLOCKS, 1, 1);
				entity.getLevel().addFreshEntity(spiral_tnt);
			}
		}
	}
	
	@Override
	public boolean playsSound() {
		return false;
	}

	@Override
	public boolean airFuse() {
		return true;
	}
	
	@Override
	public boolean explodesOnImpact() {
		return false;
	}
	
	@Override
	public Item getItem() {
		return ItemRegistry.SPIRAL_DYNAMITE.get();
	}
	
	@Override
	public int getDefaultFuse(IExplosiveEntity entity) {
		return 60;
	}
}

package luckytnt.tnteffects.projectile;

import luckytnt.registry.ItemRegistry;
import luckytntlib.util.IExplosiveEntity;
import luckytntlib.util.explosions.ImprovedExplosion;
import luckytntlib.util.tnteffects.PrimedTNTEffect;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.Item;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;

public class AcceleratingDynamiteEffect extends PrimedTNTEffect{

	/**
	 * Hard ceiling on the compounding velocity. The stored speed is clamped to 20 for the explosion
	 * radius anyway, so capping here keeps the explosion identical while stopping the per-tick
	 * movement raycast from growing without bound.
	 */
	private static final double MAX_SPEED = 20d;
	/**
	 * Minimum increase in speed before the synched NBT is rewritten. Every write forces a full
	 * CompoundTag packet to every tracking client.
	 */
	private static final double SPEED_WRITE_STEP = 0.25d;

	@Override
	public void serverExplosion(IExplosiveEntity entity) {
		ImprovedExplosion explosion = new ImprovedExplosion(entity.getLevel(), (Entity)entity, entity.getPos(), (int)Math.round(2f * Mth.clamp(entity.getPersistentData().getDoubleOr("speed", 0d), 1f, 20f)));
		explosion.doEntityExplosion(1.5f, true);
		explosion.doBlockExplosion(1f, 1f, 1f, 1.25f, false, false);
	}

	@Override
	public void explosionTick(IExplosiveEntity entity) {
		// Guarded before the motion write on purpose: accelerating on the client only overwrites the
		// server's authoritative velocity and causes rubber-banding.
		if(!(entity.getLevel() instanceof ServerLevel)) {
			return;
		}
		Entity ent = (Entity)entity;
		Vec3 movement = ent.getDeltaMovement();
		double speed = movement.length();
		if(speed < MAX_SPEED) {
			movement = movement.add(movement.scale(0.05f));
			ent.setDeltaMovement(movement);
			speed = movement.length();
		}
		double storedSpeed = entity.getPersistentData().getDoubleOr("speed", 0d);
		if(speed > storedSpeed + SPEED_WRITE_STEP) {
			CompoundTag nbt = entity.getPersistentData();
			nbt.putDouble("speed", speed);
			entity.setPersistentData(nbt);
		}
	}

	@Override
	public boolean airFuse() {
		return true;
	}

	@Override
	public Item getItem() {
		return ItemRegistry.ACCELERATING_DYNAMITE.get();
	}

	@Override
	public int getDefaultFuse(IExplosiveEntity entity) {
		return 200;
	}
}

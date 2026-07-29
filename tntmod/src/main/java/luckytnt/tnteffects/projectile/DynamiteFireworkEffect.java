package luckytnt.tnteffects.projectile;

import net.minecraft.world.entity.EntitySpawnReason;

import luckytnt.registry.EntityRegistry;
import luckytnt.registry.ItemRegistry;
import luckytntlib.entity.LExplosiveProjectile;
import luckytntlib.util.IExplosiveEntity;
import luckytntlib.util.tnteffects.PrimedTNTEffect;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.Item;
import net.minecraft.core.particles.ParticleTypes;

public class DynamiteFireworkEffect extends PrimedTNTEffect{

	/**
	 * Sub-dynamites per firework. The old loop was {@code count <= 300}, i.e. 301 entities - each one a
	 * full projectile with its own movement raycast and its own explosion.
	 */
	private static final int SUB_DYNAMITE_COUNT = 60;
	/** Server-sent particles that keep the burst reading as dense as it used to. */
	private static final int BURST_PARTICLES = 250;

	@Override
	public void serverExplosion(IExplosiveEntity entity) {
		RandomSource random = entity.getLevel().getRandom();
		for(int count = 0; count < SUB_DYNAMITE_COUNT; count++) {
			LExplosiveProjectile dynamite = EntityRegistry.DYNAMITE.get().create(entity.getLevel(), EntitySpawnReason.MOB_SUMMONED);
			dynamite.setPos(entity.getPos());
			// Was dynamite.setOwner(dynamite.owner()), which read the brand new projectile's own (null)
			// owner instead of the firework's.
			dynamite.setOwner(entity.owner());
			dynamite.setDeltaMovement(random.nextDouble() * 2f - 1f, random.nextDouble() * 2f - 1f, random.nextDouble() * 2f - 1f);
			entity.getLevel().addFreshEntity(dynamite);
		}
		if(entity.getLevel() instanceof ServerLevel serverLevel) {
			serverLevel.sendParticles(ParticleTypes.FIREWORK, entity.x(), entity.y(), entity.z(), BURST_PARTICLES, 0d, 0d, 0d, 0.75d);
			serverLevel.sendParticles(ParticleTypes.FLAME, entity.x(), entity.y(), entity.z(), BURST_PARTICLES / 2, 0d, 0d, 0d, 0.5d);
		}
	}

	@Override
	public void explosionTick(IExplosiveEntity entity) {
		// Deliberately not side-guarded: this is the only statement in the method and it has to keep
		// running on the client so the dynamite keeps floating smoothly between tracker updates.
		((Entity)entity).setDeltaMovement(((Entity)entity).getDeltaMovement().add(0f, 0.08f, 0f));
	}

	@Override
	public void spawnParticles(IExplosiveEntity entity) {
		entity.getLevel().addParticle(ParticleTypes.FLAME, entity.x(), entity.y(), entity.z(), 0, 0, 0);
	}

	@Override
	public boolean airFuse() {
		return true;
	}

	@Override
	public Item getItem() {
		return ItemRegistry.DYNAMITE_FIREWORK.get();
	}

	@Override
	public int getDefaultFuse(IExplosiveEntity entity) {
		return 40;
	}
}

package luckytnt.tnteffects.projectile;


import org.joml.Vector3f;

import luckytnt.registry.ItemRegistry;
import luckytntlib.entity.LExplosiveProjectile;
import luckytntlib.util.IExplosiveEntity;
import luckytntlib.util.explosions.ImprovedExplosion;
import luckytntlib.util.tnteffects.PrimedTNTEffect;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.Item;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.level.Level;

public class PulseDynamiteEffect extends PrimedTNTEffect{

	/** Particles in the shell, per client tick. */
	private static final int PARTICLE_COUNT = 64;
	/** The shell never changes shape, so the Fibonacci sphere is built once instead of every tick. */
	private static final double[] SPHERE_OFFSETS = new double[PARTICLE_COUNT * 3];
	/** DustParticleOptions is immutable, so one shared instance replaces 200 allocations per tick. */
	private static final DustParticleOptions PULSE_DUST = new DustParticleOptions(((int)(0.4f*255)<<16)|((int)(0.4f*255)<<8)|(int)(1f*255), 0.75f);

	static {
		double phi = Math.PI * (3d - Math.sqrt(5d));
		for(int i = 0; i < PARTICLE_COUNT; i++) {
			double y = 1d - ((double)i / (PARTICLE_COUNT - 1d)) * 2d;
			double radius = Math.sqrt(1d - y * y);
			double theta = phi * i;
			SPHERE_OFFSETS[i * 3] = Math.cos(theta) * radius;
			SPHERE_OFFSETS[i * 3 + 1] = y;
			SPHERE_OFFSETS[i * 3 + 2] = Math.sin(theta) * radius;
		}
	}

	@Override
	public void baseTick(IExplosiveEntity entity) {
		Level level = entity.getLevel();
		if(entity instanceof LExplosiveProjectile ent) {
			if(ent.inGround()) {
				CompoundTag tag = ent.getPersistentData();
				tag.putBoolean("hitBefore", true);
				ent.setPersistentData(tag);
			}
			if(ent.getTNTFuse() == 0) {
				ent.destroy();
			}
			if(ent.inGround() || ent.getPersistentData().getBooleanOr("hitBefore", false)) {
				explosionTick(ent);
				ent.setTNTFuse(ent.getTNTFuse() - 1);
			}
			if(level.isClientSide()) {
				spawnParticles(entity);
			}
		}
	}
	
	@Override
	public void explosionTick(IExplosiveEntity entity) {
		Level level = entity.getLevel();
		if (entity.getTNTFuse() <= 185) {
			((Entity)entity).setDeltaMovement(0, 0, 0);
			((Entity)entity).setPos(((Entity) entity).position());
			if (entity.getTNTFuse() % 20 == 0) {
				if (entity.getLevel() instanceof ServerLevel) {
					ImprovedExplosion explosion = new ImprovedExplosion(entity.getLevel(), (Entity)entity, entity.getPos(), entity.getPersistentData().getIntOr("strength", 0));
					explosion.doEntityExplosion(1f, true);
					explosion.doBlockExplosion(1f, 1f, 1f, 1.25f, false, false);
					level.playSound((Entity)entity, toBlockPos(entity.getPos()), SoundEvents.GENERIC_EXPLODE.value(), SoundSource.BLOCKS, 4f, (1f + (level.getRandom().nextFloat() - level.getRandom().nextFloat()) * 0.2f) * 0.7f);
					CompoundTag tag = entity.getPersistentData();
					tag.putInt("strength", entity.getPersistentData().getIntOr("strength", 0) + 1);
					entity.setPersistentData(tag);
				}
			}
		}
	}
	
	@Override
	public void spawnParticles(IExplosiveEntity entity) {
		// baseTick calls this unconditionally on the client, including while the dynamite is still in
		// flight with its fuse frozen - which made the cost effectively unbounded. The shell only means
		// anything once the dynamite has landed and started pulsing.
		if(entity instanceof LExplosiveProjectile ent && !ent.inGround() && !ent.getPersistentData().getBooleanOr("hitBefore", false)) {
			return;
		}
		for(int i = 0; i < PARTICLE_COUNT; i++) {
			entity.getLevel().addParticle(PULSE_DUST, entity.x() + SPHERE_OFFSETS[i * 3], entity.y() + SPHERE_OFFSETS[i * 3 + 1] + 0.5f, entity.z() + SPHERE_OFFSETS[i * 3 + 2], 0, 0, 0);
		}
	}
	
	@Override
	public boolean explodesOnImpact() {
		return false;
	}
	
	@Override
	public Item getItem() {
		return ItemRegistry.PULSE_DYNAMITE.get();
	}
	
	@Override
	public int getDefaultFuse(IExplosiveEntity entity) {
		return 300;
	}
}

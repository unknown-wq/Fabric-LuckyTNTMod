package luckytnt.tnteffects.projectile;

import luckytnt.registry.EntityRegistry;
import luckytntlib.util.IExplosiveEntity;
import luckytntlib.util.explosions.IForEachEntityExplosionEffect;
import luckytntlib.util.explosions.ImprovedExplosion;
import luckytntlib.util.tnteffects.PrimedTNTEffect;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;

public class VacuumShotEffect extends PrimedTNTEffect{

	/**
	 * Stateless, so it is allocated once instead of once per shot per tick.
	 */
	private static final IForEachEntityExplosionEffect SUCK = new IForEachEntityExplosionEffect() {

		@Override
		public void doEntityExplosion(Entity entity, double distance) {
			//identity comparison on the EntityType instead of EntityType.equals, and the entity's own
			//RandomSource instead of the shared, CAS guarded java.util.Random behind Math.random().
			//The type test stays in front of the roll so the roll only happens for actual toxic clouds.
			if(entity.getType() == EntityRegistry.TOXIC_CLOUD.get() && entity.getRandom().nextFloat() < 0.2f) {
				entity.discard();
			}
		}
	};

	@Override
	public void explosionTick(IExplosiveEntity entity) {
		//explosionTick runs on both logical sides. Discarding an entity is server authoritative, so the
		//client half only allocated an explosion, scanned the entity sections and desynced the toxic
		//clouds until the server resent them. Nothing here moves an entity, so no hurtMarked is needed.
		if(!(entity.getLevel() instanceof ServerLevel level)) {
			return;
		}
		new ImprovedExplosion(level, (Entity)entity, entity.getPos(), 2).doEntityExplosion(SUCK);
	}

	@Override
	public void spawnParticles(IExplosiveEntity entity) {
		//pure addParticle, client side only by nature: intentionally left unguarded
		entity.getLevel().addParticle(ParticleTypes.CLOUD, entity.x(), entity.y(), entity.z(), 0, 0, 0);
	}

	@Override
	public boolean playsSound() {
		return false;
	}
}

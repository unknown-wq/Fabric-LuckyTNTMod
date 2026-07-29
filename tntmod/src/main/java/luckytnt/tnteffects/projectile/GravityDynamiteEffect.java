package luckytnt.tnteffects.projectile;

import java.util.List;

import luckytnt.registry.ItemRegistry;
import luckytntlib.util.IExplosiveEntity;
import luckytntlib.util.tnteffects.PrimedTNTEffect;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.Item;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class GravityDynamiteEffect extends PrimedTNTEffect{

	/**
	 * Only poll for nearby entities every this many ticks. The pull is applied as a raw velocity, so
	 * halving the poll rate keeps the effect while halving the number of AABB queries.
	 */
	private static final int PULL_INTERVAL = 2;

	@Override
	public void explosionTick(IExplosiveEntity entity) {
		// Guarded before the motion write on purpose: writing motion on the client just overwrites the
		// server's authoritative value and produces rubber-banding.
		if(!(entity.getLevel() instanceof ServerLevel)) {
			return;
		}
		((Entity)entity).setDeltaMovement(((Entity)entity).getDeltaMovement().add(0f, 0.08f, 0f));
		if(entity.getTNTFuse() % PULL_INTERVAL != 0) {
			return;
		}
		List<Entity> ents = entity.getLevel().getEntities((Entity)entity, new AABB(entity.getPos().add(-10f, -10f, -10f), entity.getPos().add(10f, 10f, 10f)));
		for(Entity ent : ents) {
			if(!ent.equals(entity.owner()) && !(ent instanceof IExplosiveEntity)) {
				Vec3 direction = entity.getPos().subtract(ent.position()).normalize();
				ent.setDeltaMovement(direction.scale(1.5f));
			}
		}
	}

	@Override
	public boolean explodesOnImpact() {
		return false;
	}

	@Override
	public boolean airFuse() {
		return true;
	}

	@Override
	public Item getItem() {
		return ItemRegistry.GRAVITY_DYNAMITE.get();
	}

	@Override
	public int getDefaultFuse(IExplosiveEntity entity) {
		return 60;
	}
}

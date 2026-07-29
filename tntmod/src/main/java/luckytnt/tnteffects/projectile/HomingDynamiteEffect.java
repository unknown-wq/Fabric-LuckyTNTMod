package luckytnt.tnteffects.projectile;

import org.jetbrains.annotations.Nullable;

import luckytnt.registry.ItemRegistry;
import luckytntlib.util.IExplosiveEntity;
import luckytntlib.util.explosions.ImprovedExplosion;
import luckytntlib.util.tnteffects.PrimedTNTEffect;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.Level;

public class HomingDynamiteEffect extends PrimedTNTEffect{

	/**
	 * Half the edge length of the box searched for a homing target.
	 * Every entity section inside the box has to be visited, so this scales cubically.
	 */
	private static final double SEARCH_RADIUS = 32d;
	/**
	 * Only re-run the (expensive) target search every this many ticks. Having no target is the common
	 * case, so an unthrottled search would run two full AABB queries every single tick of the fuse.
	 */
	private static final int RETARGET_INTERVAL = 10;

	@Override
	public void serverExplosion(IExplosiveEntity entity) {
		ImprovedExplosion explosion = new ImprovedExplosion(entity.getLevel(), (Entity)entity, entity.getPos(), 20);
		explosion.doEntityExplosion(1.5f, true);
		explosion.doBlockExplosion(1f, 1f, 1f, 1.25f, false, false);
	}

	@Override
	public void explosionTick(IExplosiveEntity entity) {
		if(!(entity.getLevel() instanceof ServerLevel)) {
			return;
		}
		if(entity.getTNTFuse() < 390) {
			Entity target = entity.getLevel().getEntity(entity.getPersistentData().getIntOr("targetID", 0));
			if(target == null) {
				if(entity.getTNTFuse() % RETARGET_INTERVAL == 0) {
					setTarget(entity);
				}
			}
			else {
				Vec3 movement = target.position().subtract(entity.getPos()).normalize();
				((Entity)entity).setDeltaMovement(movement);
				// Let vanilla piggyback the new motion onto the regular tracker update instead of
				// broadcasting a ClientboundSetEntityMotionPacket to every player in the level.
				((Entity)entity).hurtMarked = true;
			}
		}
	}

	@Nullable
	public Entity setTarget(IExplosiveEntity entity) {
		Level level = entity.getLevel();
		Entity target = null;
		AABB searchBox = new AABB(entity.getPos().add(-SEARCH_RADIUS, -SEARCH_RADIUS, -SEARCH_RADIUS), entity.getPos().add(SEARCH_RADIUS, SEARCH_RADIUS, SEARCH_RADIUS));
		double distance = SEARCH_RADIUS;
		for(Player player : level.getEntitiesOfClass(Player.class, searchBox)) {
			double entityDistance = entity.getPos().distanceTo(player.position());
			if(!player.equals(entity.owner()) && entityDistance <= distance) {
				distance = entityDistance;
				target = player;
			}
		}
		if(target == null) {
			distance = SEARCH_RADIUS;
			for(LivingEntity ent : level.getEntitiesOfClass(LivingEntity.class, searchBox)) {
				double entityDistance = entity.getPos().distanceTo(ent.position());
				if(!ent.equals(entity.owner()) && entityDistance <= distance) {
					distance = entityDistance;
					target = ent;
				}
			}
		}
		// One synched NBT write for the winner instead of one per candidate that happened to be closer.
		if(target != null) {
			CompoundTag tag = entity.getPersistentData();
			tag.putInt("targetID", target.getId());
			entity.setPersistentData(tag);
		}
		return target;
	}

	@Override
	public boolean airFuse() {
		return true;
	}

	@Override
	public Item getItem() {
		return ItemRegistry.HOMING_DYNAMITE.get();
	}

	@Override
	public int getDefaultFuse(IExplosiveEntity entity) {
		return 400;
	}
}

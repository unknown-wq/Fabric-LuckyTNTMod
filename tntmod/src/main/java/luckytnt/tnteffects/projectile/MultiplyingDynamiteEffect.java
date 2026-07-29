package luckytnt.tnteffects.projectile;

import net.minecraft.world.entity.EntitySpawnReason;


import luckytnt.registry.EntityRegistry;
import luckytnt.registry.ItemRegistry;
import luckytntlib.entity.LExplosiveProjectile;
import luckytntlib.util.IExplosiveEntity;
import luckytntlib.util.explosions.ImprovedExplosion;
import luckytntlib.util.tnteffects.PrimedTNTEffect;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.Item;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.level.Level;

public class MultiplyingDynamiteEffect extends PrimedTNTEffect{

	/** Generation at which a projectile stops splitting and detonates instead. */
	private static final int MAX_GENERATION = 3;
	/**
	 * Fan-out. 4/2/2 gives 1 + 4 + 8 + 16 = 29 entities and 16 leaf explosions per throw, down from
	 * 4/4/4 = 85 entities and 64 leaf explosions.
	 */
	private static final int FIRST_GENERATION_CHILDREN = 4;
	private static final int LATER_GENERATION_CHILDREN = 2;

	@Override
	public void baseTick(IExplosiveEntity entity) {
		Level level = entity.getLevel();
		if(entity instanceof LExplosiveProjectile ent) {
			int generation = ent.getPersistentData().getIntOr("level", 0);
			boolean landed = ent.inGround() && generation >= MAX_GENERATION;
			// These two used to be independent ifs, so a projectile that landed on the same tick its fuse
			// ran out exploded twice.
			if(level instanceof ServerLevel && (landed || ent.getTNTFuse() <= 0)) {
				serverExplosion(ent);
				ent.destroy();
			}
			else {
				// The fuse used to be frozen for the last generation, so those projectiles could never
				// reach fuse 0 and only ever died on impact - over water, a ravine or the void they
				// lingered until vanilla despawned them.
				explosionTick(ent);
				ent.setTNTFuse(ent.getTNTFuse() - 1);
			}
			if(level.isClientSide()) {
				spawnParticles(entity);
			}
		}
	}
	
	@Override
	public void serverExplosion(IExplosiveEntity entity) {
		Level level = entity.getLevel();
		int generation = entity.getPersistentData().getIntOr("level", 0);
		if(generation < MAX_GENERATION) {
			int children = generation == 0 ? FIRST_GENERATION_CHILDREN : LATER_GENERATION_CHILDREN;
			for(int count = 0; count < children; count++) {
				LExplosiveProjectile dynamite = EntityRegistry.MULTIPLYING_DYNAMITE.get().create(entity.getLevel(), EntitySpawnReason.MOB_SUMMONED);
				dynamite.setPos(entity.getPos());
				dynamite.setOwner(entity.owner());
				dynamite.setDeltaMovement(((Entity)entity).getDeltaMovement().add(Math.random() * 0.5f - 0.25f, Math.random() * 0.5f - 0.25f, Math.random() * 0.5f - 0.25f));
				CompoundTag tag = dynamite.getPersistentData();
				tag.putInt("level", generation + 1);
				dynamite.setPersistentData(tag);
				entity.getLevel().addFreshEntity(dynamite);
			}
		}
		else {
			ImprovedExplosion explosion = new ImprovedExplosion(entity.getLevel(), (Entity)entity, entity.getPos(), 8);
			explosion.doEntityExplosion(0.75f, true);
			explosion.doBlockExplosion(1f, 1f, 1f, 1.25f, false, false);
			level.playSound((Entity)entity, toBlockPos(entity.getPos()), SoundEvents.GENERIC_EXPLODE.value(), SoundSource.BLOCKS, 4f, (1f + (level.getRandom().nextFloat() - level.getRandom().nextFloat()) * 0.2f) * 0.7f);
		}
	}
	
	@Override
	public void explosionTick(IExplosiveEntity entity) {
		if(entity.getPersistentData().getIntOr("level", 0) < MAX_GENERATION) {
			((Entity)entity).setDeltaMovement(((Entity)entity).getDeltaMovement().add(0f, 0.08f, 0f));
		}
	}
	
	@Override
	public Item getItem() {
		return ItemRegistry.MULTIPLYING_DYNAMITE.get();
	}
	
	@Override
	public int getDefaultFuse(IExplosiveEntity entity) {
		return 20;
	}
}

package luckytnt.tnteffects;

import java.util.List;

import luckytnt.registry.BlockRegistry;
import luckytntlib.util.IExplosiveEntity;
import luckytntlib.util.tnteffects.PrimedTNTEffect;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.resources.Identifier;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.entity.EntityTypes;

public class EntityFireworkEffect extends PrimedTNTEffect {

	/**
	 * Spawning 300 arbitrary mobs in a single tick (each one running finalizeSpawn: goal selectors,
	 * villager trades, horse variant rolls) is the single most expensive thing this effect does.
	 * Halved; the burst still reads as a shower of mobs.
	 */
	private static final int SPAWN_COUNT = 150;

	@Override
	public void explosionTick(IExplosiveEntity ent) {
		((Entity)ent).setDeltaMovement(((Entity)ent).getDeltaMovement().x, 0.8f, ((Entity)ent).getDeltaMovement().z);
		// the picked type is only consumed by serverExplosion, so the 40 wide query has no business
		// running on the client as well
		if(ent.getTNTFuse() == 40 && ent.getLevel() instanceof ServerLevel) {
			List<LivingEntity> ents = ent.getLevel().getEntitiesOfClass(LivingEntity.class, new AABB(ent.x() - 20, ent.y() - 20, ent.z() - 20, ent.x() + 20, ent.y() + 20, ent.z() + 20));
	      	double distance = 2000;
	      	for(LivingEntity lent : ents) {
	      		double xD = lent.getX() - ent.x();
	      		double yD = lent.getY() - ent.y();
	      		double zD = lent.getZ() - ent.z();
	      		double d = Math.sqrt(xD * xD + yD * yD + zD * zD);
	      		if(d < distance && !(lent instanceof Player)) {
	      			distance = d;
	      			CompoundTag tag = ent.getPersistentData();
	      			tag.putString("type", EntityType.getKey(lent.getType()).toString());
	      			ent.setPersistentData(tag);
	      		}
	      	}
		}
	}
	
	@Override
	public void serverExplosion(IExplosiveEntity ent) {
		EntityType<?> type = BuiltInRegistries.ENTITY_TYPE.getValue(Identifier.parse(ent.getPersistentData().getStringOr("type", "")));
		if(type == null) {
			type = EntityTypes.PIG;
		}
		// serverExplosion is only ever reached from the ServerLevel branch of baseTick, and
		// addFreshEntity would be a no-op on a client level anyway
		if(!(ent.getLevel() instanceof ServerLevel sLevel)) {
			return;
		}
		// getCurrentDifficultyAt builds a DifficultyInstance and reads the chunk inhabited time,
		// and the position does not change, so it is resolved once instead of once per mob
		var difficulty = sLevel.getCurrentDifficultyAt(toBlockPos(ent.getPos()));
		for(int count = 0; count < SPAWN_COUNT; count++) {
			Entity lent = type.create(sLevel, EntitySpawnReason.MOB_SUMMONED);
			lent.setPos(ent.x(), ent.y(), ent.z());
			lent.setDeltaMovement(Math.random() * 3f - 1.5f, Math.random() * 3f - 1.5f, Math.random() * 3f - 1.5f);
			if(lent instanceof Mob mob) {
				mob.finalizeSpawn(sLevel, difficulty, EntitySpawnReason.MOB_SUMMONED, null);
			}
			sLevel.addFreshEntity(lent);
		}
	}
	
	@Override
	public void spawnParticles(IExplosiveEntity ent) {
		ent.getLevel().addParticle(ParticleTypes.FLAME, ent.x(), ent.y(), ent.z(), 0, 0, 0);
	}
	
	@Override
	public Block getBlock() {
		return BlockRegistry.ENTITY_FIREWORK.get();
	}
	
	@Override
	public int getDefaultFuse(IExplosiveEntity ent) {
		return 40;
	}
}

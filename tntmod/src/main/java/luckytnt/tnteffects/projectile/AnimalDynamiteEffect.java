package luckytnt.tnteffects.projectile;

import java.util.List;

import luckytnt.registry.ItemRegistry;
import luckytntlib.util.IExplosiveEntity;
import luckytntlib.util.tnteffects.PrimedTNTEffect;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.Item;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityTypes;

public class AnimalDynamiteEffect extends PrimedTNTEffect{

	private static final List<EntityType<?>> ENTITY_TYPES = List.of(EntityTypes.SPIDER, EntityTypes.SKELETON, EntityTypes.ZOMBIE, EntityTypes.CREEPER, EntityTypes.PILLAGER, EntityTypes.VILLAGER, EntityTypes.ENDERMAN, EntityTypes.SHEEP, EntityTypes.COW, EntityTypes.PIG, EntityTypes.CHICKEN, EntityTypes.SLIME);

	/** Mobs spawned per entity type. Every one of them ticks AI for as long as it lives. */
	private static final int SPAWNS_PER_TYPE = 1;

	@Override
	public void serverExplosion(IExplosiveEntity entity) {
		for (EntityType<?> entType : ENTITY_TYPES) {
			for (int count = 0; count < SPAWNS_PER_TYPE; count++) {
				Entity ent = entType.create(entity.getLevel(), EntitySpawnReason.MOB_SUMMONED);
				// EntityType#create is nullable; the old code dereferenced it straight away.
				if (ent == null) {
					continue;
				}
				ent.setPos(entity.getPos());
				if (entity.getLevel() instanceof ServerLevel sLevel && ent instanceof Mob mob) {
					mob.finalizeSpawn(sLevel, sLevel.getCurrentDifficultyAt(toBlockPos(entity.getPos())), EntitySpawnReason.MOB_SUMMONED, null);
				}
				entity.getLevel().addFreshEntity(ent);
			}
		}
	}

	@Override
	public Item getItem() {
		return ItemRegistry.ANIMAL_DYNAMITE.get();
	}
}

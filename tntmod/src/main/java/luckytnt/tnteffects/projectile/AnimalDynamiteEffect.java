package luckytnt.tnteffects.projectile;

import java.util.List;

import luckytnt.registry.ItemRegistry;
import luckytntlib.util.IExplosiveEntity;
import luckytntlib.util.tnteffects.PrimedTNTEffect;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.item.Item;
import net.minecraft.server.world.ServerWorld;

public class AnimalDynamiteEffect extends PrimedTNTEffect{

	@Override
	public void serverExplosion(IExplosiveEntity entity) {
		List<EntityType<?>> entities = List.of(EntityType.SPIDER, EntityType.SKELETON, EntityType.ZOMBIE, EntityType.CREEPER, EntityType.PILLAGER, EntityType.VILLAGER, EntityType.ENDERMAN, EntityType.SHEEP, EntityType.COW, EntityType.PIG, EntityType.CHICKEN, EntityType.SLIME);
		for (EntityType<?> entType : entities) {
			for (int count = 0; count < 2; count++) {
				Entity ent = entType.create(entity.getLevel());
				ent.setPosition(entity.getPos());
				if (entity.getLevel() instanceof ServerWorld sLevel && ent instanceof MobEntity mob) {
					mob.initialize(sLevel, entity.getLevel().getLocalDifficulty(toBlockPos(entity.getPos())), SpawnReason.MOB_SUMMONED, null);
				}
				entity.getLevel().spawnEntity(ent);
			}
		}
	}
	
	@Override
	public Item getItem() {
		return ItemRegistry.ANIMAL_DYNAMITE.get();
	}
}

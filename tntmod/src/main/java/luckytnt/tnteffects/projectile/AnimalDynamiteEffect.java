package luckytnt.tnteffects.projectile;

import java.util.List;

import luckytnt.registry.ItemRegistry;
import luckytntlib.util.IExplosiveEntity;
import luckytntlib.util.tnteffects.PrimedTNTEffect;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.world.item.Item;
import net.minecraft.server.level.ServerLevel;

public class AnimalDynamiteEffect extends PrimedTNTEffect{

	@Override
	public void serverExplosion(IExplosiveEntity entity) {
		List<EntityType<?>> entities = List.of(EntityType.SPIDER, EntityType.SKELETON, EntityType.ZOMBIE, EntityType.CREEPER, EntityType.PILLAGER, EntityType.VILLAGER, EntityType.ENDERMAN, EntityType.SHEEP, EntityType.COW, EntityType.PIG, EntityType.CHICKEN, EntityType.SLIME);
		for (EntityType<?> entType : entities) {
			for (int count = 0; count < 2; count++) {
				Entity ent = entType.create(entity.getLevel());
				ent.setPosition(entity.getPos());
				if (entity.getLevel() instanceof ServerLevel sLevel && ent instanceof MobEntity mob) {
					mob.initialize(sLevel, entity.getLevel().getLocalDifficulty(toBlockPos(entity.getPos())), SpawnReason.MOB_SUMMONED, null);
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

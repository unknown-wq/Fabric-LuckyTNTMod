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

	@Override
	public void serverExplosion(IExplosiveEntity entity) {
		List<EntityType<?>> entities = List.of(EntityTypes.SPIDER, EntityTypes.SKELETON, EntityTypes.ZOMBIE, EntityTypes.CREEPER, EntityTypes.PILLAGER, EntityTypes.VILLAGER, EntityTypes.ENDERMAN, EntityTypes.SHEEP, EntityTypes.COW, EntityTypes.PIG, EntityTypes.CHICKEN, EntityTypes.SLIME);
		for (EntityType<?> entType : entities) {
			for (int count = 0; count < 2; count++) {
				Entity ent = entType.create(entity.getLevel(), EntitySpawnReason.MOB_SUMMONED);
				ent.setPos(entity.getPos());
				if (entity.getLevel() instanceof ServerLevel sLevel && ent instanceof Mob mob) {
					mob.finalizeSpawn(sLevel, entity.getLevel().getLocalDifficulty(toBlockPos(entity.getPos())), EntitySpawnReason.MOB_SUMMONED, null);
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

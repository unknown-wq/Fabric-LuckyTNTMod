package luckytnt.tnteffects;

import java.util.List;

import luckytnt.registry.BlockRegistry;
import luckytntlib.util.IExplosiveEntity;
import luckytntlib.util.tnteffects.PrimedTNTEffect;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.Mob;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityTypes;

public class AnimalTNTEffect extends PrimedTNTEffect{

	@Override
	public void serverExplosion(IExplosiveEntity entity) {
		List<EntityType<?>> entities = List.of(EntityTypes.BAT, EntityTypes.SPIDER, EntityTypes.SKELETON, EntityTypes.ZOMBIE, EntityTypes.CREEPER, EntityTypes.PILLAGER, EntityTypes.VILLAGER, EntityTypes.ENDERMAN, EntityTypes.EVOKER, EntityTypes.IRON_GOLEM,
												EntityTypes.WITHER_SKELETON, EntityTypes.SHEEP, EntityTypes.COW, EntityTypes.PIG, EntityTypes.CHICKEN, EntityTypes.GIANT, EntityTypes.AXOLOTL, EntityTypes.WOLF, EntityTypes.WITCH, EntityTypes.SLIME, EntityTypes.MAGMA_CUBE,
												EntityTypes.GUARDIAN, EntityTypes.ELDER_GUARDIAN, EntityTypes.CAT, EntityTypes.STRIDER);
		for(EntityType<?> entType : entities) {
			for(int count = 0; count < 2; count++){
				Entity ent = entType.create(entity.getLevel(), EntitySpawnReason.MOB_SUMMONED);
				ent.setPos(entity.getPos());
				if(entity.getLevel() instanceof ServerLevel sLevel && ent instanceof Mob mob) {
					mob.finalizeSpawn(sLevel, sLevel.getCurrentDifficultyAt(toBlockPos(entity.getPos())), EntitySpawnReason.MOB_SUMMONED, null);
				}
				entity.getLevel().addFreshEntity(ent);
			}
		}
	}
	
	@Override
	public Block getBlock() {
		return BlockRegistry.ANIMAL_TNT.get();
	}
}

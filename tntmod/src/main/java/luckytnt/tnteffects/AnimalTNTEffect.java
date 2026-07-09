package luckytnt.tnteffects;

import java.util.List;

import luckytnt.registry.BlockRegistry;
import luckytntlib.util.IExplosiveEntity;
import luckytntlib.util.tnteffects.PrimedTNTEffect;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.server.world.ServerWorld;

public class AnimalTNTEffect extends PrimedTNTEffect{

	@Override
	public void serverExplosion(IExplosiveEntity entity) {
		List<EntityType<?>> entities = List.of(EntityType.BAT, EntityType.SPIDER, EntityType.SKELETON, EntityType.ZOMBIE, EntityType.CREEPER, EntityType.PILLAGER, EntityType.VILLAGER, EntityType.ENDERMAN, EntityType.EVOKER, EntityType.IRON_GOLEM,
												EntityType.WITHER_SKELETON, EntityType.SHEEP, EntityType.COW, EntityType.PIG, EntityType.CHICKEN, EntityType.GIANT, EntityType.AXOLOTL, EntityType.WOLF, EntityType.WITCH, EntityType.SLIME, EntityType.MAGMA_CUBE,
												EntityType.GUARDIAN, EntityType.ELDER_GUARDIAN, EntityType.CAT, EntityType.STRIDER);
		for(EntityType<?> entType : entities) {
			for(int count = 0; count < 2; count++){
				Entity ent = entType.create(entity.getLevel());
				ent.setPosition(entity.getPos());
				if(entity.getLevel() instanceof ServerWorld sLevel && ent instanceof MobEntity mob) {
					mob.initialize(sLevel, entity.getLevel().getLocalDifficulty(toBlockPos(entity.getPos())), SpawnReason.MOB_SUMMONED, null);
				}
				entity.getLevel().spawnEntity(ent);
			}
		}
	}
	
	@Override
	public Block getBlock() {
		return BlockRegistry.ANIMAL_TNT.get();
	}
}

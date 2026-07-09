package luckytnt.tnteffects;

import luckytnt.registry.BlockRegistry;
import luckytntlib.util.IExplosiveEntity;
import luckytntlib.util.tnteffects.PrimedTNTEffect;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.IronGolemEntity;

public class VillageDefenseEffect extends PrimedTNTEffect{

	@Override
	public void serverExplosion(IExplosiveEntity entity) {
		for(int count = 0; count <= 15; count++) {
			IronGolemEntity golem = new IronGolemEntity(EntityType.IRON_GOLEM, entity.getLevel());
			golem.setPosition(entity.getPos());
			golem.setPlayerCreated(true);
			entity.getLevel().spawnEntity(golem);
		}
	}
	
	@Override
	public Block getBlock() {
		return BlockRegistry.VILLAGE_DEFENSE.get();
	}
}

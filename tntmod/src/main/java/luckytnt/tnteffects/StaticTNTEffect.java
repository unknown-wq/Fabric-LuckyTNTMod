package luckytnt.tnteffects;

import luckytnt.registry.BlockRegistry;
import luckytntlib.util.IExplosiveEntity;
import luckytntlib.util.tnteffects.PrimedTNTEffect;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.entity.Entity;

public class StaticTNTEffect extends PrimedTNTEffect{

	@Override
	public void explosionTick(IExplosiveEntity entity) {
		((Entity)entity).setPosition(((Entity)entity).prevX, ((Entity)entity).prevY, ((Entity)entity).prevZ);
		((Entity)entity).setDeltaMovement(0, 0, 0);
	}
	
	@Override
	public Block getBlock() {
		return BlockRegistry.STATIC_TNT.get();
	}
}
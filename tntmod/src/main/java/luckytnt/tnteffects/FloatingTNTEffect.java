package luckytnt.tnteffects;

import luckytnt.registry.BlockRegistry;
import luckytntlib.util.IExplosiveEntity;
import luckytntlib.util.tnteffects.PrimedTNTEffect;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.entity.Entity;

public class FloatingTNTEffect extends PrimedTNTEffect{

	@Override
	public void explosionTick(IExplosiveEntity entity) {
		Entity ent = (Entity)entity;
		ent.setDeltaMovement(ent.getDeltaMovement().x, 0.15f, ent.getDeltaMovement().z);
	}
	
	@Override
	public Block getBlock() {
		return BlockRegistry.FLOATING_TNT.get();
	}
}

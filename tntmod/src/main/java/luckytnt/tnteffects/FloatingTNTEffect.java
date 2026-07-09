package luckytnt.tnteffects;

import luckytnt.registry.BlockRegistry;
import luckytntlib.util.IExplosiveEntity;
import luckytntlib.util.tnteffects.PrimedTNTEffect;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;

public class FloatingTNTEffect extends PrimedTNTEffect{

	@Override
	public void explosionTick(IExplosiveEntity entity) {
		Entity ent = (Entity)entity;
		ent.setVelocity(ent.getVelocity().x, 0.15f, ent.getVelocity().z);
	}
	
	@Override
	public Block getBlock() {
		return BlockRegistry.FLOATING_TNT.get();
	}
}

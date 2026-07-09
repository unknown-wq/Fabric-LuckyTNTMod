package luckytnt.tnteffects;

import luckytnt.registry.BlockRegistry;
import luckytntlib.util.IExplosiveEntity;
import luckytntlib.util.tnteffects.PrimedTNTEffect;
import net.minecraft.block.Block;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

public class SpammingTNTEffect extends PrimedTNTEffect{

	@Override
	public void serverExplosion(IExplosiveEntity entity) {
		for(int count = 0; count <= 400; count++) {
			ItemEntity dirt = new ItemEntity(entity.getLevel(), entity.x(), entity.y(), entity.z(), new ItemStack(Items.DIRT));
			dirt.setVelocity(Math.random() * 6 - 3, 3 + Math.random() * 3, Math.random() * 6 - 3);
			entity.getLevel().spawnEntity(dirt);
		}
	}
	
	@Override
	public void explosionTick(IExplosiveEntity entity) {
		for(int count = 0; count <= 20; count++) {
			ItemEntity dirt = new ItemEntity(entity.getLevel(), entity.x(), entity.y(), entity.z(), new ItemStack(Items.DIRT));
			dirt.setVelocity(Math.random() * 4 - 2, 2 + Math.random() * 2, Math.random() * 4 - 2);
			entity.getLevel().spawnEntity(dirt);
		}
	}
	
	@Override
	public Block getBlock() {
		return BlockRegistry.SPAMMING_TNT.get();
	}
	
	@Override
	public int getDefaultFuse(IExplosiveEntity entity) {
		return 160;
	}
}

package luckytnt.tnteffects;

import luckytnt.registry.BlockRegistry;
import luckytntlib.util.IExplosiveEntity;
import luckytntlib.util.tnteffects.PrimedTNTEffect;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class SpammingTNTEffect extends PrimedTNTEffect{

	@Override
	public void serverExplosion(IExplosiveEntity entity) {
		for(int count = 0; count <= 400; count++) {
			ItemEntity dirt = new ItemEntity(entity.getLevel(), entity.x(), entity.y(), entity.z(), new ItemStack(Items.DIRT));
			dirt.setDeltaMovement(Math.random() * 6 - 3, 3 + Math.random() * 3, Math.random() * 6 - 3);
			entity.getLevel().addFreshEntity(dirt);
		}
	}
	
	@Override
	public void explosionTick(IExplosiveEntity entity) {
		// explosionTick runs on both logical sides: without this guard the client built and threw
		// away 21 ItemEntities every tick as well.
		Level level = entity.getLevel();
		if(!(level instanceof ServerLevel)) {
			return;
		}
		// Spawning every other tick with stacks of 2 keeps the total amount of dirt identical
		// while halving the number of ItemEntities (3360 -> 840 over the 160 tick fuse).
		if(entity.getTNTFuse() % 2 != 0) {
			return;
		}
		for(int count = 0; count <= 20; count++) {
			ItemEntity dirt = new ItemEntity(level, entity.x(), entity.y(), entity.z(), new ItemStack(Items.DIRT, 2));
			dirt.setDeltaMovement(Math.random() * 4 - 2, 2 + Math.random() * 2, Math.random() * 4 - 2);
			level.addFreshEntity(dirt);
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

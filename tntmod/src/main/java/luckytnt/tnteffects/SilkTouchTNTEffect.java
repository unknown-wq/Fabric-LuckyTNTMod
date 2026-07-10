package luckytnt.tnteffects;

import net.minecraft.server.level.ServerLevel;
import luckytnt.registry.BlockRegistry;
import luckytntlib.util.IExplosiveEntity;
import luckytntlib.util.explosions.ExplosionHelper;
import luckytntlib.util.explosions.IForEachBlockExplosionEffect;
import luckytntlib.util.explosions.ImprovedExplosion;
import luckytntlib.util.tnteffects.PrimedTNTEffect;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

public class SilkTouchTNTEffect extends PrimedTNTEffect {

	@Override
	public void serverExplosion(IExplosiveEntity ent) {
		ExplosionHelper.doSphericalExplosion(ent.getLevel(), ent.getPos(), 15, new IForEachBlockExplosionEffect() {
			
			@Override
			public void doBlockExplosion(Level level, BlockPos pos, BlockState state, double distance) {
				if(state.getBlock().getExplosionResistance() < 100 && !state.isAir() && Math.abs(ent.y() - pos.getY()) <= 5) {
					Block block = state.getBlock();
					block.wasExploded((ServerLevel)level, pos, ImprovedExplosion.dummyExplosion(ent.getLevel()));
					level.setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
					ItemEntity item = new ItemEntity(level, pos.getX() + 0.5D, pos.getY(), pos.getZ() + 0.5D, new ItemStack(block));
					level.addFreshEntity(item);
					level.setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
				}
			}
		});
	}
	
	@Override
	public Block getBlock() {
		return BlockRegistry.SILK_TOUCH_TNT.get();
	}
}

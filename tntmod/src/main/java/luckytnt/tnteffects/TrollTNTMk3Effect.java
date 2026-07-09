package luckytnt.tnteffects;

import luckytnt.block.TrollTNTMk3Block;
import luckytnt.registry.BlockRegistry;
import luckytntlib.util.IExplosiveEntity;
import luckytntlib.util.explosions.IBlockExplosionCondition;
import luckytntlib.util.explosions.IForEachBlockExplosionEffect;
import luckytntlib.util.explosions.ImprovedExplosion;
import luckytntlib.util.tnteffects.PrimedTNTEffect;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class TrollTNTMk3Effect extends PrimedTNTEffect{

	@Override
	public void serverExplosion(IExplosiveEntity entity) {
		ImprovedExplosion explosion = new ImprovedExplosion(entity.getLevel(), (Entity)entity, entity.getPos(), 10);
		explosion.doBlockExplosion(new IBlockExplosionCondition() {
			
			@Override
			public boolean conditionMet(World level, BlockPos pos, BlockState state, double distance) {
				return state.getBlock() instanceof TrollTNTMk3Block;
			}
		}, new IForEachBlockExplosionEffect() {
			
			@Override
			public void doBlockExplosion(World level, BlockPos pos, BlockState state, double distance) {
				state.getBlock().onDestroyedByExplosion(level, pos, explosion);
				level.setBlockState(pos, Blocks.AIR.getDefaultState(), 3);
			}
		});
	}
	
	@Override
	public Block getBlock() {
		return BlockRegistry.TROLL_TNT_MK3.get();
	}
	
	@Override
	public int getDefaultFuse(IExplosiveEntity entity) {
		return 1;
	}
}

package luckytnt.tnteffects;

import luckytnt.registry.BlockRegistry;
import luckytntlib.util.IExplosiveEntity;
import luckytntlib.util.explosions.ExplosionHelper;
import luckytntlib.util.explosions.IForEachBlockExplosionEffect;
import luckytntlib.util.explosions.ImprovedExplosion;
import luckytntlib.util.tnteffects.PrimedTNTEffect;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class SnowTNTEffect extends PrimedTNTEffect{

	private final int strength;
	
	public SnowTNTEffect(int strength) {
		this.strength = strength;
	}
	
	@Override
	public void serverExplosion(IExplosiveEntity entity) {
		ExplosionHelper.doTopBlockExplosionForAll(entity.getLevel(), entity.getPos(), strength, new IForEachBlockExplosionEffect() {
			
			@Override
			public void doBlockExplosion(World level, BlockPos pos, BlockState state, double distance) {
				state.getBlock().onDestroyedByExplosion(level, pos, ImprovedExplosion.dummyExplosion(entity.getLevel()));
				level.setBlockState(pos, Blocks.SNOW.getDefaultState());
			}
		});
	}
	
	@Override
	public Block getBlock() {
		return BlockRegistry.SNOW_TNT.get();
	}
}

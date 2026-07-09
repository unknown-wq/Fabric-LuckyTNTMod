package luckytnt.tnteffects;

import luckytnt.registry.BlockRegistry;
import luckytntlib.util.IExplosiveEntity;
import luckytntlib.util.explosions.ExplosionHelper;
import luckytntlib.util.explosions.IForEachBlockExplosionEffect;
import luckytntlib.util.explosions.ImprovedExplosion;
import luckytntlib.util.tnteffects.PrimedTNTEffect;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.block.PlantBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

public class FreezeTNTEffect extends PrimedTNTEffect{

	private final int strength;
	
	public FreezeTNTEffect(int strength) {
		this.strength = strength;
	}
	
	@Override
	public void serverExplosion(IExplosiveEntity entity) {
		ExplosionHelper.doSphericalExplosion(entity.getLevel(), entity.getPos(), strength, new IForEachBlockExplosionEffect() {
			
			@Override
			public void doBlockExplosion(Level level, BlockPos pos, BlockState state, double distance) {
				if((state.getBlock().getBlastResistance() < 100 || state.getBlock() instanceof LiquidBlock) && !(state.getBlock() instanceof PlantBlock) && !state.isAir()) {
					state.getBlock().onDestroyedByExplosion(level, pos, ImprovedExplosion.dummyExplosion(entity.getLevel()));
					level.setBlockState(pos, Blocks.ICE.getDefaultState());
				}
			}
		});
	}
	
	@Override
	public Block getBlock() {
		return BlockRegistry.FREEZE_TNT.get();
	}
}

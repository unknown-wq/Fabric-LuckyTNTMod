package luckytnt.tnteffects;

import luckytnt.config.LuckyTNTConfigValues;
import luckytnt.registry.BlockRegistry;
import luckytntlib.util.IExplosiveEntity;
import luckytntlib.util.explosions.ExplosionHelper;
import luckytntlib.util.explosions.IForEachBlockExplosionEffect;
import luckytntlib.util.tnteffects.PrimedTNTEffect;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class FloatingIslandEffect extends PrimedTNTEffect{
	
	private final int strength;
	
	public FloatingIslandEffect(int strength) {
		this.strength = strength;
	}
	
	@Override
	public void serverExplosion(IExplosiveEntity entity) {
		ExplosionHelper.doSphericalExplosion(entity.getLevel(), entity.getPos(), strength, new IForEachBlockExplosionEffect() {
			
			@Override
			public void doBlockExplosion(World level, BlockPos pos, BlockState state, double distance) {
				if(distance <= 20 && Math.abs(pos.getY() - entity.getPos().y) <= 15 && state.getBlock().getBlastResistance() <= 100) {
					if(level.getBlockState(pos.add(0, LuckyTNTConfigValues.ISLAND_HEIGHT.get(), 0)).getBlock().getBlastResistance() <= 100) {
						level.setBlockState(pos.add(0, LuckyTNTConfigValues.ISLAND_HEIGHT.get(), 0), state);
					}
				}
			}
		});
	}

	@Override
	public Block getBlock() {
		return BlockRegistry.FLOATING_ISLAND.get();
	}
	
	@Override
	public int getDefaultFuse(IExplosiveEntity entity) {
		return 120;
	}
}

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
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

public class CubicTNTEffect extends PrimedTNTEffect{
	private final int strength;
	
	public CubicTNTEffect(int strength) {
		this.strength = strength;
	}

	@Override
	public void serverExplosion(IExplosiveEntity entity) {
		ExplosionHelper.doCubicalExplosion(entity.getLevel(), entity.getPos(), strength, new IForEachBlockExplosionEffect() {
			
			@Override
			public void doBlockExplosion(Level level, BlockPos pos, BlockState state, double distance) {
				if(state.getBlock().getExplosionResistance() <= 100) {
					state.getBlock().wasExploded((ServerLevel) level, pos, ImprovedExplosion.dummyExplosion(entity.getLevel()));
					level.setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
				}
			}
		});
	}
	
	@Override
	public Block getBlock() {
		return BlockRegistry.CUBIC_TNT.get();
	}
}

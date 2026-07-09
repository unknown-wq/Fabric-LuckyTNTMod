package luckytnt.tnteffects;

import luckytnt.registry.BlockRegistry;
import luckytntlib.util.IExplosiveEntity;
import luckytntlib.util.explosions.ExplosionHelper;
import luckytntlib.util.explosions.IForEachBlockExplosionEffect;
import luckytntlib.util.explosions.ImprovedExplosion;
import luckytntlib.util.tnteffects.PrimedTNTEffect;
import net.fabricmc.fabric.api.tag.convention.v2.ConventionalBlockTags;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class MiningflatTNTEffect extends PrimedTNTEffect{

	private final int radius;
	private final int radiusY;
	
	public MiningflatTNTEffect(int radius, int radiusY) {
		this.radius = radius;
		this.radiusY = radiusY;
	}
	
	@Override
	public void serverExplosion(IExplosiveEntity entity) {
		ExplosionHelper.doCylindricalExplosion(entity.getLevel(), entity.getPos(), radius, radiusY, new IForEachBlockExplosionEffect() {
			
			@Override
			public void doBlockExplosion(World level, BlockPos pos, BlockState state, double distance) {
				if(pos.getY() >= entity.y() - 0.5f) {
					if(state.getBlock().getBlastResistance() < 100) {
						if(state.isIn(ConventionalBlockTags.ORES)) {
							Block.dropStacks(state, level, pos);
						}
						state.getBlock().onDestroyedByExplosion(level, pos, ImprovedExplosion.dummyExplosion(entity.getLevel()));
						level.setBlockState(pos, Blocks.AIR.getDefaultState(), 3);
						if(pos.getY() - Math.round(entity.y()) == 0) {
							if(Math.random() < 0.05f && Block.sideCoversSmallSquare(level, pos.down(), Direction.UP)) {
								level.setBlockState(pos, Blocks.TORCH.getDefaultState());
							}
						}
					}
				}
			}
		});
	}
	
	@Override
	public Block getBlock() {
		return BlockRegistry.MININGFLAT_TNT.get();
	}
}

package luckytnt.tnteffects;
import net.minecraft.server.level.ServerLevel;

import luckytnt.registry.BlockRegistry;
import luckytntlib.util.IExplosiveEntity;
import luckytntlib.util.explosions.ExplosionHelper;
import luckytntlib.util.explosions.IForEachBlockExplosionEffect;
import luckytntlib.util.explosions.ImprovedExplosion;
import luckytntlib.util.tnteffects.PrimedTNTEffect;
import net.fabricmc.fabric.api.tag.convention.v2.ConventionalBlockTags;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;

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
			public void doBlockExplosion(Level level, BlockPos pos, BlockState state, double distance) {
				if(pos.getY() >= entity.y() - 0.5f) {
					if(state.getBlock().getExplosionResistance() < 100) {
						if(state.is(ConventionalBlockTags.ORES)) {
							Block.dropStacks(state, level, pos);
						}
						state.getBlock().wasExploded((ServerLevel)level, pos, ImprovedExplosion.dummyExplosion(entity.getLevel()));
						level.setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
						if(pos.getY() - Math.round(entity.y()) == 0) {
							if(Math.random() < 0.05f && Block.sideCoversSmallSquare(level, pos.below(), Direction.UP)) {
								level.setBlockAndUpdate(pos, Blocks.TORCH.defaultBlockState());
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

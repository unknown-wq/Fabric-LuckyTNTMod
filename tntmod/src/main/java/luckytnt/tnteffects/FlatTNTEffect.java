package luckytnt.tnteffects;
import net.minecraft.server.level.ServerLevel;

import java.util.function.Supplier;

import luckytntlib.block.LTNTBlock;
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

public class FlatTNTEffect extends PrimedTNTEffect{
	private final int radius;
	private final int radiusY;
	private int fuse = 80;
	private Supplier<Supplier<LTNTBlock>> block;
	
	public FlatTNTEffect(int radius, int radiusY, int fuse) {
		this.radius = radius;
		this.radiusY = radiusY;
		this.fuse = fuse;
	}
	
	public FlatTNTEffect(Supplier<Supplier<LTNTBlock>> block, int radius, int radiusY, int fuse) {
		this.radius = radius;
		this.radiusY = radiusY;
		this.block = block;
		this.fuse = fuse;
	}
	
	@Override
	public void serverExplosion(IExplosiveEntity entity) {
		ImprovedExplosion dummyExplosion = ImprovedExplosion.dummyExplosion(entity.getLevel());
		ExplosionHelper.doCylindricalExplosion(entity.getLevel(), entity.getPos(), radius, radiusY, new IForEachBlockExplosionEffect() {
			
			@Override
			public void doBlockExplosion(Level level, BlockPos pos, BlockState state, double distance) {
				if(pos.getY() >= entity.y() - 0.5f) {
					if(state.getBlock().getExplosionResistance() <= 100) {
						state.getBlock().wasExploded((ServerLevel) level, pos, dummyExplosion);
						level.setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
					}
				}
			}
		});
	}
	
	@Override
	public Block getBlock() {
		return block.get().get();
	}
	
	@Override
	public int getDefaultFuse(IExplosiveEntity ent) {
		return fuse;
	}
}

package luckytnt.tnteffects;

import java.util.function.Supplier;

import luckytntlib.block.LTNTBlock;
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
			public void doBlockExplosion(World level, BlockPos pos, BlockState state, double distance) {
				if(pos.getY() >= entity.y() - 0.5f) {
					if(state.getBlock().getBlastResistance() <= 100) {
						state.getBlock().onDestroyedByExplosion(level, pos, dummyExplosion);
						level.setBlockState(pos, Blocks.AIR.getDefaultState(), 3);
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

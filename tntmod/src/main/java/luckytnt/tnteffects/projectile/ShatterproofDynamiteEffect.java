package luckytnt.tnteffects.projectile;

import org.joml.Vector3f;

import luckytnt.registry.ItemRegistry;
import luckytntlib.util.IExplosiveEntity;
import luckytntlib.util.explosions.ExplosionHelper;
import luckytntlib.util.explosions.IForEachBlockExplosionEffect;
import luckytntlib.util.tnteffects.PrimedTNTEffect;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.item.Item;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

public class ShatterproofDynamiteEffect extends PrimedTNTEffect{

	private static final BlockState OBSIDIAN = Blocks.OBSIDIAN.defaultBlockState();

	@Override
	public void serverExplosion(IExplosiveEntity entity) {
		ExplosionHelper.doSphericalExplosion(entity.getLevel(), entity.getPos(), 5, new IForEachBlockExplosionEffect() {
			
			@Override
			public void doBlockExplosion(Level level, BlockPos pos, BlockState state, double distance) {
				if(state.isAir() || state.is(Blocks.OBSIDIAN)) {
					return;
				}
				if(state.isCollisionShapeFullBlock(level, pos) && state.getBlock().getExplosionResistance() < 1200) {
					// Flag 2 = notify clients only. setBlockAndUpdate (flag 3) additionally propagates a
					// neighbour update out of every one of the ~524 blocks; obsidian has no
					// neighbour-dependent behaviour, so that work is pure overhead here.
					level.setBlock(pos, OBSIDIAN, 2);
				}
			}
		});
	}
	
	@Override
	public void spawnParticles(IExplosiveEntity entity) {
		entity.getLevel().addParticle(new DustParticleOptions(((int)(0.1f*255)<<16)|((int)(0.1f*255)<<8)|(int)(0.1f*255), 1), entity.x(), entity.y(), entity.z(), 0, 0, 0);
		entity.getLevel().addParticle(new DustParticleOptions(((int)(0.5f*255)<<16)|((int)(0.3f*255)<<8)|(int)(0.8f*255), 1), entity.x(), entity.y(), entity.z(), 0, 0, 0);
	}
	
	@Override
	public Item getItem() {
		return ItemRegistry.SHATTERPROOF_DYNAMITE.get();
	}
}

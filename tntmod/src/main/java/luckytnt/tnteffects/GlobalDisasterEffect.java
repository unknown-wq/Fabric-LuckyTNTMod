package luckytnt.tnteffects;

import org.joml.Vector3f;

import luckytnt.registry.BlockRegistry;
import luckytntlib.util.IExplosiveEntity;
import luckytntlib.util.explosions.ExplosionHelper;
import luckytntlib.util.explosions.IForEachBlockExplosionEffect;
import luckytntlib.util.explosions.ImprovedExplosion;
import luckytntlib.util.tnteffects.PrimedTNTEffect;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class GlobalDisasterEffect extends PrimedTNTEffect{

	@Override
	public void serverExplosion(IExplosiveEntity entity) {
		ExplosionHelper.doSphericalExplosion(entity.getLevel(), entity.getPos(), 50, new IForEachBlockExplosionEffect() {
		
			@Override
			public void doBlockExplosion(World level, BlockPos pos, BlockState state, double distance) {
				if(state.getBlock().getBlastResistance() < 200 && !state.isAir()) {
					state.getBlock().onDestroyedByExplosion(level, pos, ImprovedExplosion.dummyExplosion(entity.getLevel()));
					level.setBlockState(pos, Blocks.AIR.getDefaultState(), 3);
				}
			}
		});
	}
	
	@Override
	public void spawnParticles(IExplosiveEntity ent) {
		for(double angle = 0; angle < 360; angle += 6D) {
			ent.getLevel().addParticle(new DustParticleEffect(new Vector3f(0.2f, 0.2f, 0.2f), 0.75f), ent.x() + 2 * Math.cos(angle * Math.PI / 180), ent.y() + 0.5f, ent.z() + 2 * Math.sin(angle * Math.PI / 180), 0, 0, 0);
			ent.getLevel().addParticle(new DustParticleEffect(new Vector3f(0.2f, 0.2f, 0.2f), 0.75f), ent.x() + 2 * Math.cos(angle * Math.PI / 180), ent.y() + 0.5f + 2 * Math.sin(angle * Math.PI / 180), ent.z(), 0, 0, 0);
			ent.getLevel().addParticle(new DustParticleEffect(new Vector3f(0.2f, 0.2f, 0.2f), 0.75f), ent.x(), ent.y() + 0.5f + 2 * Math.cos(angle * Math.PI / 180), ent.z() + 2 * Math.sin(angle * Math.PI / 180), 0, 0, 0);
		}
	}

	@Override
	public Block getBlock() {
		return BlockRegistry.GLOBAL_DISASTER.get();
	}
	
	@Override
	public int getDefaultFuse(IExplosiveEntity entity) {
		return 240;
	}
}

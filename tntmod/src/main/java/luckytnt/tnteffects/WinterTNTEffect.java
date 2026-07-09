package luckytnt.tnteffects;

import org.joml.Vector3f;

import luckytnt.entity.SnowySnowball;
import luckytnt.registry.BlockRegistry;
import luckytnt.util.BlockSurviveChecks;
import luckytnt.util.Materials;
import luckytntlib.util.IExplosiveEntity;
import luckytntlib.util.explosions.ExplosionHelper;
import luckytntlib.util.explosions.IForEachBlockExplosionEffect;
import luckytntlib.util.tnteffects.PrimedTNTEffect;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.core.particles.DustParticleEffect;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

public class WinterTNTEffect extends PrimedTNTEffect {

	@Override
	public void serverExplosion(IExplosiveEntity ent) {
		ExplosionHelper.doSphericalExplosion(ent.getLevel(), ent.getPos(), 150, new IForEachBlockExplosionEffect() {
			
			@Override
			public void doBlockExplosion(Level level, BlockPos pos, BlockState state, double distance) {
				if(state.getBlock().getBlastResistance() < 200) {
					if(state.isOf(Blocks.BUBBLE_COLUMN) || state.isOf(Blocks.WATER) || Materials.isWaterPlant(state) || state.getBlock() == Blocks.WATER) {
						level.setBlockState(pos, Blocks.ICE.getDefaultState(), 3);
					}
				}
			}
		});
		
		ExplosionHelper.doTopBlockExplosionForAll(ent.getLevel(), ent.getPos(), 150, new IForEachBlockExplosionEffect() {
			
			@Override
			public void doBlockExplosion(Level level, BlockPos pos, BlockState state, double distance) {
				if(state.getBlock().getBlastResistance() < 200 && BlockSurviveChecks.canSnowPlaceAt(state, level, pos)) {
					level.setBlockState(pos, Blocks.SNOW.getDefaultState(), 3);
				}
			}
		});
	}
	
	@Override
	public void explosionTick(IExplosiveEntity ent) {
		for(int i = 0; i <= 50; i++) {
			SnowySnowball ball = new SnowySnowball(ent.getLevel(), ent.x() + Math.random() * 100 - Math.random() * 100, ent.y() + 30D, ent.z() + Math.random() * 100 - Math.random() * 100);
			ball.setDeltaMovement(Math.random() * 0.1D - Math.random() * 0.1D, -0.1D - Math.random() * 0.4D, Math.random() * 0.1D - Math.random() * 0.1D);
			ent.getLevel().addFreshEntity(ball);
		}
	}
	
	@Override
	public void spawnParticles(IExplosiveEntity ent) {
		ent.getLevel().addParticle(new DustParticleEffect(new Vector3f(1f, 1f, 1f), 1f), ent.x(), ent.y() + 1D, ent.z(), 0, 0, 0);
	}
	
	@Override
	public Block getBlock() {
		return BlockRegistry.WINTER_TNT.get();
	}
	
	@Override
	public int getDefaultFuse(IExplosiveEntity ent) {
		return 200;
	}
}

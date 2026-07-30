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
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.level.Level;

public class WinterTNTEffect extends PrimedTNTEffect {

	@Override
	public void serverExplosion(IExplosiveEntity ent) {
		ExplosionHelper.doSphericalExplosion(ent.getLevel(), ent.getPos(), 150, new IForEachBlockExplosionEffect() {
			
			@Override
			public void doBlockExplosion(Level level, BlockPos pos, BlockState state, double distance) {
				// Identity compares first (both tests are pure, so the order cannot change the result);
				// "state.getBlock() == Blocks.WATER" was a duplicate of "state.is(Blocks.WATER)".
				if((state.is(Blocks.BUBBLE_COLUMN) || state.is(Blocks.WATER) || Materials.isWaterPlant(state)) && state.getBlock().getExplosionResistance() < 200) {
					level.setBlock(pos, Blocks.ICE.defaultBlockState(), 3);
				}
			}
		});
		
		ExplosionHelper.doTopBlockExplosionForAll(ent.getLevel(), ent.getPos(), 150, new IForEachBlockExplosionEffect() {
			
			@Override
			public void doBlockExplosion(Level level, BlockPos pos, BlockState state, double distance) {
				if(state.getBlock().getExplosionResistance() < 200 && BlockSurviveChecks.canSnowPlaceAt(state, level, pos)) {
					level.setBlock(pos, Blocks.SNOW.defaultBlockState(), 3);
				}
			}
		});
	}
	
	/**
	 * Upper bound on how many SnowySnowballs may be falling at once, and how the batches are spread out.
	 * explosionTick runs every tick on both logical sides for the whole 200 tick fuse and used to spawn
	 * 51 snowballs each time: 10200 projectiles server side, the same 10200 built and thrown away on the
	 * logical client, and - because a ball dropped from y+30 needs ~100 ticks to land - roughly 5000 of
	 * them alive at any moment, every one of them running a motion ray cast per tick.
	 * 12 balls every 4th tick is the same visual density at a quarter of the entities (10200 -> 2400,
	 * ~5000 -> ~300 concurrent), and the live population is capped the same way
	 * {@link BlackHoleTNTEffect} caps its falling blocks.
	 */
	private static final int MAX_LIVE_SNOWBALLS = 400;
	private static final int SNOWBALL_INTERVAL = 4;
	private static final int SNOWBALLS_PER_BATCH = 12;

	@Override
	public void explosionTick(IExplosiveEntity ent) {
		// addFreshEntity is a no-op on the client, so none of this work belongs there
		if(!(ent.getLevel() instanceof ServerLevel sLevel) || ent.getTNTFuse() % SNOWBALL_INTERVAL != 0) {
			return;
		}
		// one section walk every 4th tick is far cheaper than the entities it keeps from being created
		AABB range = new AABB(ent.x() - 110D, ent.y() - 60D, ent.z() - 110D, ent.x() + 110D, ent.y() + 40D, ent.z() + 110D);
		int live = sLevel.getEntitiesOfClass(SnowySnowball.class, range).size();
		int amount = Math.min(SNOWBALLS_PER_BATCH, MAX_LIVE_SNOWBALLS - live);
		for(int i = 0; i < amount; i++) {
			SnowySnowball ball = new SnowySnowball(sLevel, ent.x() + Math.random() * 100 - Math.random() * 100, ent.y() + 30D, ent.z() + Math.random() * 100 - Math.random() * 100);
			ball.setDeltaMovement(Math.random() * 0.1D - Math.random() * 0.1D, -0.1D - Math.random() * 0.4D, Math.random() * 0.1D - Math.random() * 0.1D);
			sLevel.addFreshEntity(ball);
		}
	}
	
	@Override
	public void spawnParticles(IExplosiveEntity ent) {
		ent.getLevel().addParticle(new DustParticleOptions(((int)(1f*255)<<16)|((int)(1f*255)<<8)|(int)(1f*255), 1f), ent.x(), ent.y() + 1D, ent.z(), 0, 0, 0);
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

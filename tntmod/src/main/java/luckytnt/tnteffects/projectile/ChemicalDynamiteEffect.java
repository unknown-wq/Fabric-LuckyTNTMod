package luckytnt.tnteffects.projectile;

import org.joml.Vector3f;

import luckytnt.registry.ItemRegistry;
import luckytntlib.util.IExplosiveEntity;
import luckytntlib.util.explosions.ExplosionHelper;
import luckytntlib.util.explosions.IForEachBlockExplosionEffect;
import luckytntlib.util.explosions.ImprovedExplosion;
import luckytntlib.util.tnteffects.PrimedTNTEffect;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.item.Item;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

public class ChemicalDynamiteEffect extends PrimedTNTEffect{

	/**
	 * Ticks between two dissolve spheres. The dynamite only moves ~1.5 blocks per tick, so a radius-4
	 * sphere every second tick still covers its path without gaps, at half the cost.
	 */
	private static final int DISSOLVE_INTERVAL = 2;

	@Override
	public void explosionTick(IExplosiveEntity entity) {
		if(entity.getLevel() instanceof ServerLevel serverLevel && entity.getTNTFuse() % DISSOLVE_INTERVAL == 0) {
			RandomSource random = serverLevel.getRandom();
			ExplosionHelper.doSphericalExplosion(entity.getLevel(), entity.getPos(), 4, new IForEachBlockExplosionEffect() {

				@Override
				public void doBlockExplosion(Level level, BlockPos pos, BlockState state, double distance) {
					// Air has resistance 0 and therefore passed the test below, so most of every sphere was
					// spent re-clearing blocks an earlier tick had already dissolved.
					if(state.isAir()) {
						return;
					}
					if(state.getBlock().getExplosionResistance() < 100 && distance + random.nextDouble() <= 4) {
						state.getBlock().wasExploded((ServerLevel)level, pos, ImprovedExplosion.dummyExplosion(entity.getLevel()));
						level.setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
					}
				}
			});
		}
	}
	
	@Override
	public void spawnParticles(IExplosiveEntity entity) {
		entity.getLevel().addParticle(new DustParticleOptions(((int)(0.1f*255)<<16)|((int)(1f*255)<<8)|(int)(0.6f*255), 1), entity.x(), entity.y(), entity.z(), 0, 0, 0);
		entity.getLevel().addParticle(new DustParticleOptions(((int)(0.6f*255)<<16)|((int)(0.8f*255)<<8)|(int)(0.4f*255), 1), entity.x(), entity.y(), entity.z(), 0, 0, 0);
		entity.getLevel().addParticle(new DustParticleOptions(((int)(0.8f*255)<<16)|((int)(1f*255)<<8)|(int)(0.8f*255), 1), entity.x(),+ entity.y(), entity.z(), 0, 0, 0);
		entity.getLevel().addParticle(new DustParticleOptions(((int)(0.1f*255)<<16)|((int)(1f*255)<<8)|(int)(0.2f*255), 1), entity.x(),+ entity.y(), entity.z(), 0, 0, 0);
	}
	
	@Override
	public boolean playsSound() {
		return false;
	}
	
	@Override
	public boolean airFuse() {
		return true;
	}

	@Override
	public boolean explodesOnImpact() {
		return false;
	}
	
	@Override
	public Item getItem() {
		return ItemRegistry.CHEMICAL_DYNAMITE.get();
	}
	
	@Override
	public int getDefaultFuse(IExplosiveEntity entity) {
		return 40;
	}
}

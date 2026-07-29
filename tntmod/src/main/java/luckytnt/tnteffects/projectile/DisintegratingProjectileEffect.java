package luckytnt.tnteffects.projectile;

import java.util.List;

import luckytnt.registry.BlockRegistry;
import luckytntlib.util.IExplosiveEntity;
import luckytntlib.util.explosions.ExplosionHelper;
import luckytntlib.util.explosions.IForEachBlockExplosionEffect;
import luckytntlib.util.explosions.ImprovedExplosion;
import luckytntlib.util.tnteffects.PrimedTNTEffect;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSources;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

public class DisintegratingProjectileEffect extends PrimedTNTEffect {

	private static final BlockState AIR = Blocks.AIR.defaultBlockState();

	private static final DustParticleOptions WHITE = new DustParticleOptions((255 << 16) | (255 << 8) | 255, 1f);
	private static final DustParticleOptions RED = new DustParticleOptions(255 << 16, 1f);

	/**
	 * The sweep runs every other tick instead of every tick, see {@link #explosionTick(IExplosiveEntity)}.
	 * The toxic stone chance is doubled to compensate so that the shell ends up with the same density.
	 */
	private static final double TOXIC_STONE_CHANCE = 0.02D;

	/**
	 * Stateless, so it is allocated once instead of once per projectile per tick.
	 * <p>
	 * The original rolled {@link Math#random()} as the very first test, so it was rolled for all 15625
	 * positions of the cube every tick, air included. Now the state is rejected first and the roll only
	 * happens in the band where it can actually change the outcome.
	 */
	private static final IForEachBlockExplosionEffect DISINTEGRATE = new IForEachBlockExplosionEffect() {

		@Override
		public void doBlockExplosion(Level level, BlockPos pos, BlockState state, double distance) {
			//isAir is a cached flag on the state; after the first sweep almost the whole sphere is air,
			//so this rejects nearly everything before any further work
			if(state.isAir() || distance > 13D) {
				return;
			}
			if(distance < 10D) {
				//10D is the lower bound of the random destruction threshold, so this band is unconditional
				destroy(level, pos, state);
				return;
			}
			if(distance < 12D && distance < 10D + level.getRandom().nextDouble() * 2) {
				destroy(level, pos, state);
				return;
			}
			if(distance > 11D && state.getBlock() == Blocks.STONE && level.getRandom().nextDouble() < TOXIC_STONE_CHANCE) {
				//flag 3 is kept here: toxic stone is placed rarely and it is a real block placement,
				//not bulk clearing, so its neighbours should react normally
				level.setBlock(pos, BlockRegistry.TOXIC_STONE.get().defaultBlockState(), 3);
			}
		}

		private void destroy(Level level, BlockPos pos, BlockState state) {
			Block block = state.getBlock();
			if(block.getExplosionResistance() >= 200) {
				return;
			}
			block.wasExploded((ServerLevel)level, pos, ImprovedExplosion.dummyExplosion(level));
			//UPDATE_CLIENTS instead of flag 3: the projectile carves out one contiguous volume, so the
			//6 neighbour updates per block were spent notifying blocks that are removed in the same pass
			level.setBlock(pos, AIR, Block.UPDATE_CLIENTS);
		}
	};

	@Override
	public void baseTick(IExplosiveEntity entity) {
		if(!entity.getLevel().isClientSide()) {
			explosionTick(entity);
		} else {
			spawnParticles(entity);
		}
		entity.setTNTFuse(entity.getTNTFuse() - 1);
		if(entity.getTNTFuse() <= 0) {
			entity.destroy();
		}
	}

	@Override
	public void explosionTick(IExplosiveEntity ent) {
		Level level = ent.getLevel();
		int fuse = ent.getTNTFuse();
		if(fuse == 0) {
			level.playSound(null, toBlockPos(ent.getPos()), SoundEvents.FIRE_EXTINGUISH, SoundSource.MASTER, 1f, 1f);
		}
		if(!(level instanceof ServerLevel sLevel)) {
			return;
		}
		int id = ((Entity)ent).getId();
		//All 50 projectiles are spawned on the same tick and share a fuse, so keying the phase off the
		//entity id spreads the sweeps evenly over the two ticks instead of stacking them on one.
		if(fuse % 2 == (id & 1)) {
			//A spherical sweep of radius 13 visits the positions with distance <= 13, which is the only
			//band the predicate reacts to at all. The cubical sweep of radius 12 visited 15625 positions
			//to reach those 9219, so 41% of the getBlockState calls were thrown away.
			ExplosionHelper.doSphericalExplosion(level, ent.getPos(), 13, DISINTEGRATE);
		}
		if(fuse % 20 == Math.floorMod(id, 20)) {
			int x = Mth.floor(ent.x());
			int y = Mth.floor(ent.y());
			int z = Mth.floor(ent.z());
			List<LivingEntity> list = level.getEntitiesOfClass(LivingEntity.class, new AABB(x - 6, y - 6, z - 6, x + 6, y + 6, z + 6));
			if(!list.isEmpty()) {
				DamageSources sources = level.damageSources();
				for(LivingEntity lent : list) {
					lent.hurtServer(sLevel, sources.magic(), 5f);
				}
			}
		}
	}

	@Override
	public void spawnParticles(IExplosiveEntity ent) {
		Level level = ent.getLevel();
		double x = ent.x();
		double y = ent.y();
		double z = ent.z();
		level.addParticle(WHITE, x + 0.2f, y + 1f, z, 0, 0, 0);
		level.addParticle(WHITE, x - 0.2f, y + 1f, z, 0, 0, 0);
		level.addParticle(RED, x, y + 1f, z + 0.2f, 0, 0, 0);
		level.addParticle(RED, x, y + 1f, z - 0.2f, 0, 0, 0);
	}

	@Override
	public Block getBlock() {
		return BlockRegistry.DISINTEGRATING_TNT.get();
	}

	@Override
	public int getDefaultFuse(IExplosiveEntity ent) {
		return 120;
	}
}

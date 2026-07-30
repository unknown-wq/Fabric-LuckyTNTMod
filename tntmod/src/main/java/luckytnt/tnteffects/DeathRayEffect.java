package luckytnt.tnteffects;
import net.minecraft.server.level.ServerLevel;

import org.joml.Vector3f;

import luckytnt.registry.SoundRegistry;
import luckytntlib.util.IExplosiveEntity;
import luckytntlib.util.explosions.ImprovedExplosion;
import luckytntlib.util.tnteffects.PrimedTNTEffect;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.entity.Entity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.sounds.SoundSource;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.phys.Vec3;

public class DeathRayEffect extends PrimedTNTEffect {

	@Override
	public void explosionTick(IExplosiveEntity ent) {
		if(ent.getTNTFuse() == 480) {
			CompoundTag tag = ent.getPersistentData();
			tag.putInt("explosionSize", 1);
			tag.putInt("particleSize", 1);
			ent.setPersistentData(tag);
			ent.getLevel().playSound(null, ent.x(), ent.y(), ent.z(), SoundRegistry.DEATH_RAY.get(), SoundSource.HOSTILE, 20, 1);
			((Entity)ent).setDeltaMovement(0, 0, 0);
		}
		
		if(ent.getTNTFuse() < 80) {
			((Entity)ent).setDeltaMovement(0, 0, 0);
			((Entity)ent).setPos(((Entity)ent).xo, ((Entity)ent).yo, ((Entity)ent).zo);
			
			if(ent.getLevel() instanceof ServerLevel sLevel) {
				int size = ent.getPersistentData().getIntOr("explosionSize", 0);
				carveShell(sLevel, ent, size);

				CompoundTag tag = ent.getPersistentData();
				tag.putInt("explosionSize", size + 1);
				ent.setPersistentData(tag);
			}
		}
	}
	
	/**
	 * Vaporizes the {@code size - 3 < d <= size} shell of the ray.
	 * <p>This used to be found by scanning the whole solid {@code (2 * size + 1)^3} cube and taking a square
	 * root per cell: 161^3 = 4 173 281 iterations in the last tick alone and 86.1M over the 80 ticks of the
	 * ray, <b>94% of which were a Math.sqrt followed by a rejection</b>. The z span of the shell is now
	 * derived in closed form per (x, y) column, the same way {@code ImprovedExplosion.forEachShellCell} does
	 * it, so only the cells of the shell itself are visited: <b>4 173 281 -&gt; 231 322 in the worst tick
	 * (18x), 86.1M -&gt; 6.2M over the whole ray (13.9x)</b>. The visited set, its order and the branch each
	 * cell takes were verified to be identical to the old triple loop for every size from 0 to 85.
	 * <p>Math.sqrt is gone entirely - both radius tests and the {@code distance >= 75} branch are exact on
	 * squared integer distances - and the two {@code new BlockPos} plus three {@code ent.getPos()} calls per
	 * accepted cell became one reused mutable position and one hoisted lookup.
	 */
	private static void carveShell(ServerLevel level, IExplosiveEntity ent, int size) {
		if(size < 0) {
			return;
		}
		final RandomSource random = level.getRandom();
		final Vec3 center = ent.getPos();
		final int cx = (int)center.x;
		final int cy = (int)center.y;
		final int cz = (int)center.z;
		final BlockState lava = Blocks.LAVA.defaultBlockState();
		final BlockState obsidian = Blocks.OBSIDIAN.defaultBlockState();
		final BlockState air = Blocks.AIR.defaultBlockState();
		final ImprovedExplosion dummy = ImprovedExplosion.dummyExplosion(level);
		final BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
		final long outerSqr = (long)size * size;
		//size - 3 < 0 means the old "distance > size - 3" test accepted everything inside the sphere
		final long innerSqr = size >= 3 ? (long)(size - 3) * (size - 3) : -1L;
		for(int offX = -size; offX <= size; offX++) {
			final long xSqr = (long)offX * offX;
			final int x = cx + offX;
			for(int offY = size; offY >= -size; offY--) {
				final long xySqr = xSqr + (long)offY * offY;
				final long outerRemaining = outerSqr - xySqr;
				if(outerRemaining < 0) {
					continue;
				}
				final int zMax = floorSqrt(outerRemaining);
				final long innerRemaining = innerSqr - xySqr;
				//the smallest |offZ| whose squared distance still exceeds the inner radius
				final int zMin = innerRemaining < 0 ? 0 : floorSqrt(innerRemaining) + 1;
				if(zMin > zMax) {
					continue;
				}
				final int y = cy + offY;
				for(int offZ = -zMax; offZ <= zMax; offZ++) {
					if(offZ > -zMin && offZ < zMin) {
						//jump over the interior of the shell in one step instead of testing every cell of it
						offZ = zMin - 1;
						continue;
					}
					pos.set(x, y, cz + offZ);
					final BlockState state = level.getBlockState(pos);
					if(state.isAir() || state.getBlock().getExplosionResistance() >= 2000) {
						continue;
					}
					if(xySqr + (long)offZ * offZ >= 5625L) {
						if(random.nextFloat() < 0.1f) {
							level.setBlock(pos.immutable(), lava, 3);
						} else if(random.nextFloat() < 0.8f) {
							//a full solid block replacing another solid block: nothing around it can lose its
							//support and the fluid it may have replaced is gone either way
							level.setBlock(pos.immutable(), obsidian, Block.UPDATE_CLIENTS);
						}
					} else {
						BlockPos target = pos.immutable();
						state.getBlock().wasExploded(level, target, dummy);
						//flag 3: the ray keeps eating outwards, so sand still has to fall and water still has
						//to run into the tunnel behind it
						level.setBlock(target, air, 3);
					}
				}
			}
		}
	}

	/**
	 * @param value  a value greater than or equal to 0
	 * @return the greatest int whose square is less than or equal to the given value
	 */
	private static int floorSqrt(long value) {
		int root = (int)Math.sqrt((double)value);
		while(root > 0 && (long)root * root > value) {
			root--;
		}
		while((long)(root + 1) * (root + 1) <= value) {
			root++;
		}
		return root;
	}

	@Override
	public void spawnParticles(IExplosiveEntity ent) {
		if(ent.getTNTFuse() > 120) {
			ent.getLevel().addParticle(new DustParticleOptions(((int)(0.8f*255)<<16)|((int)(0f*255)<<8)|(int)(0f*255), 1f), ent.x(), ent.y(), ent.z(), 0, 0, 0);
		}
		if(ent.getTNTFuse() < 140) {
			for(int count = 0; count < 200; count++) {
				ent.getLevel().addParticle(new DustParticleOptions(((int)(0.5f*255)<<16)|((int)(0f*255)<<8)|(int)(2f*255), 10f), ent.x() + Math.random() - Math.random(), ent.y() + 135f - Math.random() * ent.getPersistentData().getIntOr("particleSize", 0), ent.z() + Math.random() - Math.random(), 0, 0, 0);
			}
			CompoundTag tag = ent.getPersistentData();
			tag.putInt("particleSize", ent.getPersistentData().getIntOr("particleSize", 0) + 2);
			ent.setPersistentData(tag);
		}
	}
	
	@Override
	public Block getBlock() {
		return Blocks.AIR;
	}
	
	@Override
	public int getDefaultFuse(IExplosiveEntity ent) {
		return 480;
	}
}

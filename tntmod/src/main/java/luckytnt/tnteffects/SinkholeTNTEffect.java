package luckytnt.tnteffects;

import net.minecraft.server.level.ServerLevel;
import luckytnt.registry.BlockRegistry;
import luckytntlib.util.IExplosiveEntity;
import luckytntlib.util.explosions.ImprovedExplosion;
import luckytntlib.util.tnteffects.PrimedTNTEffect;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.entity.Entity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.phys.Vec3;

public class SinkholeTNTEffect extends PrimedTNTEffect {

	/** the widest the +-2 RNG jitter can ever push the r=30 carve radius */
	private static final int MAX_RADIUS = 32;
	private static final int MAX_RADIUS_SQ = MAX_RADIUS * MAX_RADIUS;
	/** below this the jitter can never push a cell back outside the r=30 carve radius */
	private static final int ALWAYS_RADIUS_SQ = 28 * 28;

	@Override
	public void explosionTick(IExplosiveEntity ent) {
		if(ent.getTNTFuse() == 250) {
			CompoundTag tag = ent.getPersistentData();
			tag.putInt("depth", 20);
			ent.setPersistentData(tag);
		}
		if(ent.getTNTFuse() <= 150) {
			((Entity)ent).setDeltaMovement(Vec3.ZERO);
			((Entity)ent).setNoGravity(true);
		}
		if(ent.getTNTFuse() <= 150 && ent.getTNTFuse() % 2 == 0 && ent.getLevel() instanceof ServerLevel level) {
			// Every one of the 67^3 = 300 763 cells of the old cube did a Math.sqrt, a Math.random(), a
			// new BlockPos and - worst - an ent.getPersistentData() (a SynchedEntityData get plus a
			// CompoundTag lookup) *before* the distance test, for a value that is constant across the
			// whole firing. All four are gone:
			//  - depth / position / air / the dummy explosion are hoisted (300 763 NBT lookups -> 1),
			//  - the cube is replaced by the r=32 ball, the widest the +-2 jitter can ever reach
			//    (300 763 -> 4/3*pi*32^3 = 137 258 iterations, z span derived in closed form),
			//  - a cell closer than 28 always passes, so the RNG only runs in the 28..32 shell,
			//  - the state is read once instead of twice.
			// Over the 76 firings that is 22.6M iterations -> 10.4M, almost all of them a single
			// getBlockState.
			final int depth = ent.getPersistentData().getIntOr("depth", 0);
			final int centerX = Mth.floor(ent.x());
			final int centerY = Mth.floor(ent.y()) + depth;
			final int centerZ = Mth.floor(ent.z());
			final BlockState air = Blocks.AIR.defaultBlockState();
			final ImprovedExplosion dummy = ImprovedExplosion.dummyExplosion(level);
			final RandomSource random = level.getRandom();
			BlockPos.MutableBlockPos mutable = new BlockPos.MutableBlockPos();

			for(int offX = -MAX_RADIUS; offX <= MAX_RADIUS; offX++) {
				int xSq = offX * offX;
				int posX = centerX + offX;
				for(int offY = -MAX_RADIUS; offY <= MAX_RADIUS; offY++) {
					int xySq = xSq + offY * offY;
					if(xySq > MAX_RADIUS_SQ) {
						continue;
					}
					int posY = centerY + offY;
					int zMax = floorSqrt(MAX_RADIUS_SQ - xySq);
					for(int offZ = -zMax; offZ <= zMax; offZ++) {
						int distanceSq = xySq + offZ * offZ;
						if(distanceSq > ALWAYS_RADIUS_SQ && Math.sqrt(distanceSq) + random.nextDouble() * 4D - 2D > 30D) {
							continue;
						}
						mutable.set(posX, posY, centerZ + offZ);
						BlockState state = level.getBlockState(mutable);
						// Air was carved every single firing before: setBlock(AIR) onto air is a no op
						// (LevelChunk.setBlockState bails out on the identical state, so Level.setBlock never
						// notifies anything) and AirBlock does not override wasExploded, so skipping it is
						// behaviour preserving - it just removes the ~113k repeat writes per firing into the
						// part of the hole that is already carved out. ~8.5M -> a few hundred thousand
						// setBlock calls over the whole effect.
						if(state.isAir() || state.getBlock().getExplosionResistance() >= 200) {
							continue;
						}
						BlockPos pos = mutable.immutable();
						// flag 3 is deliberately kept: the sinkhole relies on the neighbour cascade to make
						// the surrounding sand/gravel collapse into the hole and water/lava run back in
						state.getBlock().wasExploded(level, pos, dummy);
						level.setBlock(pos, air, 3);
					}
				}
			}

			CompoundTag tag = ent.getPersistentData();
			tag.putInt("depth", depth - 1);
			ent.setPersistentData(tag);
		}
	}

	/**
	 * @param value  a value greater than or equal to 0
	 * @return the greatest int whose square is less than or equal to the given value
	 */
	private static int floorSqrt(int value) {
		int root = (int)Math.sqrt(value);
		while(root > 0 && root * root > value) {
			root--;
		}
		while((root + 1) * (root + 1) <= value) {
			root++;
		}
		return root;
	}

	@Override
	public Block getBlock() {
		return BlockRegistry.SINKHOLE_TNT.get();
	}

	@Override
	public int getDefaultFuse(IExplosiveEntity ent) {
		return 250;
	}
}

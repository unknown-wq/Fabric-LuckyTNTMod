package luckytnt.tnteffects;

import java.util.List;

import luckytnt.registry.BlockRegistry;
import luckytntlib.util.IExplosiveEntity;
import luckytntlib.util.LuckyTNTEntityExtension;
import luckytntlib.util.tnteffects.PrimedTNTEffect;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.player.Player;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.levelgen.Heightmap;

public class ContinentalDriftEffect extends PrimedTNTEffect {

	@Override
	public void explosionTick(IExplosiveEntity ent) {
		if(ent.getTNTFuse() == 400) {
			double vecx = Math.random() * 2D - 1D;
			double vecz = Math.random() * 2D - 1D;
			Vec3 vec = new Vec3(vecx, 0, vecz).normalize();
			CompoundTag tag = ent.getPersistentData();
			tag.putDouble("vecx", vec.x);
			tag.putDouble("vecz", vec.z);
			
			double vecx2 = Math.random() * 2D - 1D;
			double vecz2 = Math.random() * 2D - 1D;
			Vec3 vec2 = new Vec3(vecx2, 0, vecz2).normalize();
			tag.putDouble("vecx2", vec2.x);
			tag.putDouble("vecz2", vec2.z);
			
			tag.putDouble("x", ent.x());
			tag.putDouble("y", ent.y());
			tag.putDouble("z", ent.z());
	      	
			tag.putInt("second", 30 + ent.getLevel().getRandom().nextInt(101));
			
			ent.setPersistentData(tag);
	      	
	      	List<Player> list = ent.getLevel().getEntitiesOfClass(Player.class, new AABB(ent.x() - 200, ent.y() - 200, ent.z() - 200, ent.x() + 200, ent.y() + 200, ent.z() + 200));
	      	for(Player player : list) {
	      		if(player instanceof LuckyTNTEntityExtension eplayer) {
	      			CompoundTag etag = eplayer.getAdditionalPersistentData();
		      		etag.putInt("shakeTime", 400);
		      		eplayer.setAdditionalPersistentData(etag);
	      		}
	      	}
		}
		
		if(ent.getTNTFuse() <= 400 && (ent.getTNTFuse() % 60 == 0 || ent.getTNTFuse() == 400) && !ent.getLevel().isClientSide()) {
			BlockPos origin = toBlockPos(new Vec3(ent.getPersistentData().getDoubleOr("x", 0), ent.getPersistentData().getDoubleOr("y", 0), ent.getPersistentData().getDoubleOr("z", 0)));
			BlockPos start = origin.offset(toBlockPos(new Vec3(ent.getPersistentData().getDoubleOr("vecx", 0) * -80, 0, ent.getPersistentData().getDoubleOr("vecz", 0) * -80)));
			Vec3 vec = new Vec3(ent.getPersistentData().getDoubleOr("vecx", 0), 0, ent.getPersistentData().getDoubleOr("vecz", 0));
			Vec3 vec2 = new Vec3(ent.getPersistentData().getDoubleOr("vecx2", 0), 0, ent.getPersistentData().getDoubleOr("vecz2", 0));
			BlockPos start2 = start.offset(toBlockPos(new Vec3(vec.x * ent.getPersistentData().getIntOr("second", 0), 0, vec.z * ent.getPersistentData().getIntOr("second", 0)))).offset(toBlockPos(new Vec3(vec2.x * 8, 0, vec2.z * 8)));
			
			carveRift(ent, start, vec, 160);
			carveRift(ent, start2, vec2, 60);
		}
	}

	/**
	 * The 21x21 offset ring is loop invariant, so the per-offset chance is looked up from a table that is
	 * built once instead of taking a square root and three range tests per cell. Cells outside r=10 are
	 * skipped before anything is allocated, and the two identical copies of this body were merged.
	 * <p>The three original ranges are mutually exclusive, so at most one roll happens per in-range cell -
	 * exactly as before.
	 * <p>The rift stamp is 21 blocks wide while the line advances about one block per step, so the same
	 * column is hit roughly ten times per firing. Every hit removes the block the heightmap currently points
	 * at, which is what makes the rift a trench instead of a one block deep scratch - so the hits must be
	 * counted, not deduplicated. Counting them first and then carving each column top down in a single pass
	 * keeps the result identical while collapsing ~43 000 level wide
	 * {@code getHeight} + {@code getBlockState} + {@code new BlockPos} round trips (each of which resolves
	 * the chunk again) into <b>one chunk lookup per carved column, ~4 400 of them</b>, plus a local heightmap
	 * read per removed block. A column whose top block is too tough to carve now also stops after the first
	 * attempt instead of retrying it up to twenty times.
	 */
	private static void carveRift(IExplosiveEntity ent, BlockPos start, Vec3 vec, int length) {
		Level level = ent.getLevel();
		RandomSource random = level.getRandom();
		BlockState air = Blocks.AIR.defaultBlockState();
		int startX = start.getX();
		int startZ = start.getZ();

		// chance[offX + 10][offZ + 10]: the roll threshold, or Double.NaN outside the rift.
		double[] chance = new double[21 * 21];
		for(int offX = -10; offX <= 10; offX++) {
			for(int offZ = -10; offZ <= 10; offZ++) {
				int d2 = offX * offX + offZ * offZ;
				double threshold;
				if(d2 <= 49) {
					threshold = 0.1D;
				} else if(d2 <= 81) {
					threshold = 0.5D;
				} else if(d2 <= 100) {
					threshold = 0.9D;
				} else {
					threshold = Double.NaN;
				}
				chance[(offX + 10) * 21 + (offZ + 10)] = threshold;
			}
		}

		// hits[x][z] over the bounding box of the rift: how often this column was carved. vec is a unit
		// vector, so floor(i * vec) is monotonic in i and the box is at most (length + 21)^2 columns.
		int endX = Mth.floor((length - 1) * vec.x);
		int endZ = Mth.floor((length - 1) * vec.z);
		int minX = startX + Math.min(0, endX) - 10;
		int minZ = startZ + Math.min(0, endZ) - 10;
		int sizeX = Math.max(0, endX) - Math.min(0, endX) + 21;
		int sizeZ = Math.max(0, endZ) - Math.min(0, endZ) + 21;
		int[] hits = new int[sizeX * sizeZ];

		for(int i = 0; i < length; i++) {
			// floor(i * vec.x + offX) == floor(i * vec.x) + offX for integral offX, so the walk along the
			// rift can be hoisted out of the 21x21 loop and no Vec3/BlockPos is built per cell.
			int lineX = startX + Mth.floor(i * vec.x);
			int lineZ = startZ + Mth.floor(i * vec.z);
			for(int offX = -10; offX <= 10; offX++) {
				int row = (offX + 10) * 21 + 10;
				int column = (lineX + offX - minX) * sizeZ + (lineZ - minZ);
				for(int offZ = -10; offZ <= 10; offZ++) {
					double threshold = chance[row + offZ];
					if(Double.isNaN(threshold) || random.nextDouble() <= threshold) {
						continue;
					}
					hits[column + offZ]++;
				}
			}
		}

		BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
		int minY = level.getMinY();
		for(int indexX = 0; indexX < sizeX; indexX++) {
			int x = minX + indexX;
			int base = indexX * sizeZ;
			for(int indexZ = 0; indexZ < sizeZ; indexZ++) {
				int carves = hits[base + indexZ];
				if(carves == 0) {
					continue;
				}
				int z = minZ + indexZ;
				pos.set(x, minY, z);
				// the old code went through Level.getHeight, which quietly returns the world floor for an
				// unloaded chunk; taking the chunk directly needs the guard to not generate anything
				if(!level.isLoaded(pos)) {
					continue;
				}
				LevelChunk chunk = level.getChunk(x >> 4, z >> 4);
				for(int carve = 0; carve < carves; carve++) {
					// ChunkAccess.getHeight is Level.getHeight(...) - 1 without the chunk lookup
					int y = chunk.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, x, z);
					if(y < minY) {
						break;
					}
					pos.set(x, y, z);
					if(level.getBlockState(pos).getBlock().getExplosionResistance() > 100) {
						// the heightmap cannot move past a block that will not break, so every remaining
						// carve of this column would be the exact same no-op
						break;
					}
					// flag 3: the carve exposes whatever sat on top of the removed block and opens the rift
					// wall towards neighbouring water and gravity blocks, all of which need the update
					level.setBlock(pos.immutable(), air, 3);
				}
			}
		}
	}
	
	@Override
	public Block getBlock() {
		return BlockRegistry.CONTINENTAL_DRIFT.get();
	}
	
	@Override
	public int getDefaultFuse(IExplosiveEntity ent) {
		return 480;
	}
}

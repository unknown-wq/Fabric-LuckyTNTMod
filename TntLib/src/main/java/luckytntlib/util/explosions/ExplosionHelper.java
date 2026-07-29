package luckytntlib.util.explosions;

import java.util.HashMap;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.DirectionalPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

/**
 * The ExplosionHelper offers many basic functions that help you get or edit large ares of blocks in the current {@link Level}.
 * This includes simple spherical functions, but also more complex methods like getting the top most block in a sphere.
 */
public class ExplosionHelper {

	/**
	 * Gets all blocks in a specified sphere and returns them in a HashMap consisting of {@link BlockPos} and {@link BlockState}
	 * @param level  the current level
	 * @param position  the center position of the sphere
	 * @param radius  the radius of the sphere
	 * @return a {@link HashMap} of {@link BlockPos} and {@link BlockState}
	 */
	public static HashMap<BlockPos, BlockState> getBlocksInSphere(Level level, Vec3 position, int radius) {
		HashMap<BlockPos, BlockState> blocks = new HashMap<>();
		final int cx = Mth.floor(position.x);
		final int cy = Mth.floor(position.y);
		final int cz = Mth.floor(position.z);
		final long radiusSqr = (long)radius * radius;
		for(int offX = -radius; offX <= radius; offX++) {
			final long xSqr = (long)offX * offX;
			for(int offY = radius; offY >= -radius; offY--) {
				final long xySqr = xSqr + (long)offY * offY;
				if(xySqr > radiusSqr) {
					continue;
				}
				final int zMax = floorSqrt(radiusSqr - xySqr);
				for(int offZ = -zMax; offZ <= zMax; offZ++) {
					BlockPos pos = new BlockPos(cx + offX, cy + offY, cz + offZ);
					BlockState state = level.getBlockState(pos);
					blocks.put(pos, state);
				}
			}
		}
		return blocks;
	}

	/**
	 * Gets all blocks in a specified cuboid and returns them in a HashMap consisting of {@link BlockPos} and {@link BlockState}
	 * @param level  the current level
	 * @param position  the center position of the cuboid
	 * @param radii  the radii for the x, y and z directions
	 * @return a {@link HashMap} of {@link BlockPos} and {@link BlockState}
	 */
	public static HashMap<BlockPos, BlockState> getBlocksInCuboid(Level level, Vec3 position, Vec3 radii) {
		HashMap<BlockPos, BlockState> blocks = new HashMap<>();
		final int cx = Mth.floor(position.x);
		final int cy = Mth.floor(position.y);
		final int cz = Mth.floor(position.z);
		for(int offX = (int)-radii.x; offX <= (int)radii.x; offX++) {
			for(int offY = (int)radii.y; offY >= (int)-radii.y; offY--) {
				for(int offZ = (int)-radii.z; offZ <= (int)radii.z; offZ++) {
					BlockPos pos = new BlockPos(cx + offX, cy + offY, cz + offZ);
					BlockState state = level.getBlockState(pos);
					blocks.put(pos, state);
				}
			}
		}
		return blocks;
	}

	/**
	 * Gets all blocks in a specified cylinder and returns them in a HashMap consisting of {@link BlockPos} and {@link BlockState}
	 * @param level  the current level
	 * @param position  the center position of the cylinder
	 * @param radius  the radius of the cylinder
	 * @return a {@link HashMap} of {@link BlockPos} and {@link BlockState}
	 */
	public static HashMap<BlockPos, BlockState> getBlocksInCylinder(Level level, Vec3 position, int radius, int radiusY) {
		HashMap<BlockPos, BlockState> blocks = new HashMap<>();
		final int cx = Mth.floor(position.x);
		final int cy = Mth.floor(position.y);
		final int cz = Mth.floor(position.z);
		final long radiusSqr = (long)radius * radius;
		for(int offX = -radius; offX <= radius; offX++) {
			final long xSqr = (long)offX * offX;
			final int zMax = floorSqrt(radiusSqr - xSqr);
			for(int offY = radiusY; offY >= -radiusY; offY--) {
				for(int offZ = -zMax; offZ <= zMax; offZ++) {
					BlockPos pos = new BlockPos(cx + offX, cy + offY, cz + offZ);
					BlockState state = level.getBlockState(pos);
					blocks.put(pos, state);
				}
			}
		}
		return blocks;
	}

	/**
	 * Gets blocks contained in a specified sphere around a center position and edits them according to the given blockEffect.
	 * @param level  the current level
	 * @param position  the center position of the spherical explosion
	 * @param radius  the radius of the sphere
	 * @param blockEffect  determines what should happen to the blocks gotten by this function
	 */
	public static void doSphericalExplosion(Level level, Vec3 position, int radius, IForEachBlockExplosionEffect blockEffect) {
		final int cx = Mth.floor(position.x);
		final int cy = Mth.floor(position.y);
		final int cz = Mth.floor(position.z);
		final long radiusSqr = (long)radius * radius;
		for(int offX = -radius; offX <= radius; offX++) {
			final long xSqr = (long)offX * offX;
			for(int offY = radius; offY >= -radius; offY--) {
				final long xySqr = xSqr + (long)offY * offY;
				if(xySqr > radiusSqr) {
					continue;
				}
				final int zMax = floorSqrt(radiusSqr - xySqr);
				for(int offZ = -zMax; offZ <= zMax; offZ++) {
					double distance = Math.sqrt(xySqr + (double)offZ * offZ);
					BlockPos pos = new BlockPos(cx + offX, cy + offY, cz + offZ);
					BlockState state = level.getBlockState(pos);
					blockEffect.doBlockExplosion(level, pos, state, distance);
				}
			}
		}
	}

	/**
	 * Gets blocks contained in a specified sphere around a center position and edits them according to the given blockEffect.
	 * The sphere can be scaled in all the 3 directions individually.
	 * @param level  the current level
	 * @param position  the center position of the spherical explosion
	 * @param radius  the radius of the sphere
	 * @param scaling  the scaling of the sphere
	 * @param blockEffect  determines what should happen to the blocks gotten by this function
	 */
	public static void doModifiedSphericalExplosion(Level level, Vec3 position, int radius, Vec3 scaling, IForEachBlockExplosionEffect blockEffect) {
		final int cx = Mth.floor(position.x);
		final int cy = Mth.floor(position.y);
		final int cz = Mth.floor(position.z);
		//a small tolerance is added so that no column that could still contain an accepted block is ever culled,
		//every block that is not culled is still tested individually
		final double radiusSqr = (double)radius * radius * 1.000001d + 1d;
		//culling is only valid if every summand of the distance is positive
		final boolean cull = scaling.x > 0 && scaling.y > 0 && scaling.z > 0;
		for(double offX = -radius * scaling.x; offX <= radius * scaling.x; offX++) {
			final double xTerm = offX * offX / scaling.x;
			if(cull && xTerm > radiusSqr) {
				continue;
			}
			for(double offY = radius * scaling.y; offY >= -radius * scaling.y; offY--) {
				if(cull && xTerm + offY * offY / scaling.y > radiusSqr) {
					continue;
				}
				for(double offZ = -radius * scaling.z; offZ <= radius * scaling.z; offZ++) {
					double distance = Math.sqrt(offX * offX / scaling.x + offY * offY / scaling.y + offZ * offZ / scaling.z);
					if(distance <= radius) {
						BlockPos pos = new BlockPos(cx + (int)offX, cy + (int)offY, cz + (int)offZ);
						BlockState state = level.getBlockState(pos);
						blockEffect.doBlockExplosion(level, pos, state, distance);
					}
				}
			}
		}
	}

	/**
	 * Gets blocks contained in a specified cube around a center position and edits them according to the given blockEffect.
	 * @param level  the current level
	 * @param position  the center position of the cubical explosion
	 * @param radius  the radius of the cube
	 * @param blockEffect  determines what should happen to the blocks gotten by this function
	 */
	public static void doCubicalExplosion(Level level, Vec3 position, int radius, IForEachBlockExplosionEffect blockEffect) {
		final int cx = Mth.floor(position.x);
		final int cy = Mth.floor(position.y);
		final int cz = Mth.floor(position.z);
		for(int offX = -radius; offX <= radius; offX++) {
			final long xSqr = (long)offX * offX;
			for(int offY = -radius; offY <= radius; offY++) {
				final long xySqr = xSqr + (long)offY * offY;
				for(int offZ = -radius; offZ <= radius; offZ++) {
					double distance = Math.sqrt(xySqr + (double)offZ * offZ);
					BlockPos pos = new BlockPos(cx + offX, cy + offY, cz + offZ);
					BlockState state = level.getBlockState(pos);
					blockEffect.doBlockExplosion(level, pos, state, distance);
				}
			}
		}
	}

	/**
	 * Gets blocks contained in a specified cuboid around a center position and edits them according to the given blockEffect.
	 * @param level  the current level
	 * @param position  the center position of the cubical explosion
	 * @param radii  the radii for the x, y and z directions
	 * @param blockEffect  determines what should happen to the blocks gotten by this function
	 */
	public static void doCuboidExplosion(Level level, Vec3 position, Vec3 radii, IForEachBlockExplosionEffect blockEffect) {
		final int cx = Mth.floor(position.x);
		final int cy = Mth.floor(position.y);
		final int cz = Mth.floor(position.z);
		for(int offX = (int)-radii.x; offX <= (int)radii.x; offX++) {
			final long xSqr = (long)offX * offX;
			for(int offY = (int)-radii.y; offY <= (int)radii.y; offY++) {
				final long xySqr = xSqr + (long)offY * offY;
				for(int offZ = (int)-radii.z; offZ <= (int)radii.z; offZ++) {
					double distance = Math.sqrt(xySqr + (double)offZ * offZ);
					BlockPos pos = new BlockPos(cx + offX, cy + offY, cz + offZ);
					BlockState state = level.getBlockState(pos);
					blockEffect.doBlockExplosion(level, pos, state, distance);
				}
			}
		}
	}

	/**
	 * Gets blocks contained in a specified cylinder around a center position and edits them according to the given blockEffect.
	 * @param level  the current level
	 * @param position  the center position of the cubical explosion
	 * @param radius  the radius of the x and z dimensions of the cylinder
	 * @param radiusY  the radius of the y dimension of the cylinder
	 * @param blockEffect  determines what should happen to the blocks gotten by this function
	 */
	public static void doCylindricalExplosion(Level level, Vec3 position, int radius, int radiusY, IForEachBlockExplosionEffect blockEffect) {
		final int cx = Mth.floor(position.x);
		final int cy = Mth.floor(position.y);
		final int cz = Mth.floor(position.z);
		final long radiusSqr = (long)radius * radius;
		//the distance of a cylindrical explosion only depends on the x and z offset, so it is calculated once per column
		final double[] distances = new double[Math.max(radius, 0) + 1];
		for(int offX = -radius; offX <= radius; offX++) {
			final long xSqr = (long)offX * offX;
			final int zMax = floorSqrt(radiusSqr - xSqr);
			for(int offZ = 0; offZ <= zMax; offZ++) {
				distances[offZ] = Math.sqrt(xSqr + (double)offZ * offZ);
			}
			for(int offY = -radiusY; offY <= radiusY; offY++) {
				for(int offZ = -zMax; offZ <= zMax; offZ++) {
					double distance = distances[Math.abs(offZ)];
					BlockPos pos = new BlockPos(cx + offX, cy + offY, cz + offZ);
					BlockState state = level.getBlockState(pos);
					blockEffect.doBlockExplosion(level, pos, state, distance);
				}
			}
		}
	}

	/**
	 * Gets only the top most blocks in a sphere and edits them according to the given blockEffect.
	 * The function goes from top to bottom and the first block that is air or not solid and followed by a solid block below is considered the top most block.
	 * @param level  the current level
	 * @param position  the center position of the top block explosion
	 * @param radius  the radius of the sphere
	 * @param blockEffect  determines what should happen to the blocks gotten by this function
	 */
	public static void doTopBlockExplosion(Level level, Vec3 position, int radius, IForEachBlockExplosionEffect blockEffect) {
		final int cx = Mth.floor(position.x);
		final int cy = Mth.floor(position.y);
		final int cz = Mth.floor(position.z);
		final long radiusSqr = (long)radius * radius;
		for(int offX = -radius; offX <= radius; offX++) {
			final long xSqr = (long)offX * offX;
			for(int offZ = -radius; offZ <= radius; offZ++) {
				final long xzSqr = xSqr + (long)offZ * offZ;
				if(xzSqr > radiusSqr) {
					continue;
				}
				final int yMax = floorSqrt(radiusSqr - xzSqr);
				topToBottom: for(int offY = yMax; offY >= -yMax; offY--) {
					double distance = Math.sqrt(xzSqr + (double)offY * offY);
					BlockPos pos = new BlockPos(cx + offX, cy + offY, cz + offZ);
					BlockState state = level.getBlockState(pos);
					BlockPos below = pos.below();
					BlockState belowState = level.getBlockState(below);
					if((belowState.isCollisionShapeFullBlock(level, below) || belowState.isFaceSturdy(level, below, Direction.UP)) && (state.isAir() || (!state.isCollisionShapeFullBlock(level, pos) && state.getBlock().getExplosionResistance() == 0) || state.is(BlockTags.FLOWERS) || state.canBeReplaced(new DirectionalPlaceContext(level, pos, Direction.DOWN, ItemStack.EMPTY, Direction.UP)))) {
						blockEffect.doBlockExplosion(level, pos, state, distance);
						break topToBottom;
					}
				}
			}
		}
	}

	/**
	 * Gets only the top most blocks in a sphere and edits them according to the given blockEffect.
	 * The function goes from top to bottom and the block above the first block that is not air is considered the top most block.
	 * If the condition is not met it will continue to search for another top block further down
	 * @param level  the current level
	 * @param position  the center position of the top block explosion
	 * @param radius  the radius of the sphere
	 * @param condition  the condition for the top block to be considered, otherwise a new block further down will be searched for
	 * @param blockEffect  determines what should happen to the blocks gotten by this function
	 */
	public static void doTopBlockExplosion(Level level, Vec3 position, int radius, IBlockExplosionCondition condition, IForEachBlockExplosionEffect blockEffect) {
		final int cx = Mth.floor(position.x);
		final int cy = Mth.floor(position.y);
		final int cz = Mth.floor(position.z);
		final long radiusSqr = (long)radius * radius;
		for(int offX = -radius; offX <= radius; offX++) {
			final long xSqr = (long)offX * offX;
			for(int offZ = -radius; offZ <= radius; offZ++) {
				final long xzSqr = xSqr + (long)offZ * offZ;
				if(xzSqr > radiusSqr) {
					continue;
				}
				final int yMax = floorSqrt(radiusSqr - xzSqr);
				topToBottom: for(int offY = yMax; offY >= -yMax; offY--) {
					double distance = Math.sqrt(xzSqr + (double)offY * offY);
					BlockPos pos = new BlockPos(cx + offX, cy + offY, cz + offZ);
					BlockState state = level.getBlockState(pos);
					BlockPos below = pos.below();
					BlockState belowState = level.getBlockState(below);
					if(!belowState.isAir()) {
						if(condition.conditionMet(level, below, belowState, Math.sqrt(xzSqr + (double)(offY - 1) * (offY - 1)))) {
							blockEffect.doBlockExplosion(level, pos, state, distance);
							break topToBottom;
						}
					}
				}
			}
		}
	}

	/**
	 * Gets all the top blocks in a sphere and edits them according to the given blockEffect.
	 * A top block is any air or non-solid block followed by a solid block below.
	 * @param level  the current level
	 * @param position  the center position of the top block explosion
	 * @param radius  the radius of the sphere
	 * @param blockEffect  determines what should happen to the blocks gotten by this function
	 */
	public static void doTopBlockExplosionForAll(Level level, Vec3 position, int radius, IForEachBlockExplosionEffect blockEffect) {
		final int cx = Mth.floor(position.x);
		final int cy = Mth.floor(position.y);
		final int cz = Mth.floor(position.z);
		final long radiusSqr = (long)radius * radius;
		for(int offX = -radius; offX <= radius; offX++) {
			final long xSqr = (long)offX * offX;
			for(int offZ = -radius; offZ <= radius; offZ++) {
				final long xzSqr = xSqr + (long)offZ * offZ;
				if(xzSqr > radiusSqr) {
					continue;
				}
				final int yMax = floorSqrt(radiusSqr - xzSqr);
				for(int offY = yMax; offY >= -yMax; offY--) {
					double distance = Math.sqrt(xzSqr + (double)offY * offY);
					BlockPos pos = new BlockPos(cx + offX, cy + offY, cz + offZ);
					BlockState state = level.getBlockState(pos);
					BlockPos below = pos.below();
					BlockState belowState = level.getBlockState(below);
					if((belowState.isCollisionShapeFullBlock(level, below) || belowState.isFaceSturdy(level, below, Direction.UP)) && (state.isAir() || (!state.isCollisionShapeFullBlock(level, pos) && state.getBlock().getExplosionResistance() == 0) || state.is(BlockTags.FLOWERS) || state.canBeReplaced(new DirectionalPlaceContext(level, pos, Direction.DOWN, ItemStack.EMPTY, Direction.UP)))) {
						blockEffect.doBlockExplosion(level, pos, state, distance);
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
}

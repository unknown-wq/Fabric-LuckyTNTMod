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
		//offZ only ever takes the values zStart + n for a whole n, so the span of the inner loop can be moved
		//without changing which positions are visited
		final double zStart = -radius * scaling.z;
		final double zEnd = radius * scaling.z;
		for(double offX = -radius * scaling.x; offX <= radius * scaling.x; offX++) {
			final double xTerm = offX * offX / scaling.x;
			if(cull && xTerm > radiusSqr) {
				continue;
			}
			for(double offY = radius * scaling.y; offY >= -radius * scaling.y; offY--) {
				final double xyTerm = xTerm + offY * offY / scaling.y;
				if(cull && xyTerm > radiusSqr) {
					continue;
				}
				//the x and y loops were culled but the z loop always ran its full 2 * radius * scaling.z span,
				//paying three divisions and a square root for every cell outside the ellipsoid.
				//A cell can only be accepted while offZ * offZ / scaling.z <= radiusSqr - xyTerm, so the span is
				//derived in closed form instead. At radius 250 with a scaling of (1, 2/3, 1) that turns
				//80.3 M inner iterations into the 53.4 M that are actually inside the ellipsoid.
				//The tolerance already contained in radiusSqr, the rounding down of the start and the
				//unchanged individual test below guarantee that no accepted cell is ever culled.
				double zFrom = zStart;
				double zTo = zEnd;
				if(cull) {
					final double zLimit = Math.sqrt(Math.max(radiusSqr - xyTerm, 0d) * scaling.z);
					final double steps = Math.floor(-zLimit - zStart);
					if(steps > 0d) {
						zFrom = zStart + steps;
					}
					if(zLimit < zTo) {
						zTo = zLimit;
					}
				}
				for(double offZ = zFrom; offZ <= zTo; offZ++) {
					double distance = Math.sqrt(xyTerm + offZ * offZ / scaling.z);
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
		//every cell of the cube is handed to the blockEffect, so unlike the spherical traversals there is
		//nothing that could be culled here. The distance however only depends on the absolute value of the
		//z offset, so a column of 2 * radius + 1 cells needs radius + 1 square roots instead of one per cell
		final double[] distances = new double[Math.max(radius, 0) + 1];
		for(int offX = -radius; offX <= radius; offX++) {
			final long xSqr = (long)offX * offX;
			final int x = cx + offX;
			for(int offY = -radius; offY <= radius; offY++) {
				final long xySqr = xSqr + (long)offY * offY;
				final int y = cy + offY;
				for(int offZ = 0; offZ <= radius; offZ++) {
					distances[offZ] = Math.sqrt(xySqr + (double)offZ * offZ);
				}
				for(int offZ = -radius; offZ <= radius; offZ++) {
					BlockPos pos = new BlockPos(x, y, cz + offZ);
					BlockState state = level.getBlockState(pos);
					blockEffect.doBlockExplosion(level, pos, state, distances[Math.abs(offZ)]);
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
		final int radiusZ = (int)radii.z;
		//see doCubicalExplosion: nothing can be culled, but the square root only depends on the absolute
		//value of the z offset and is therefore computed once per half column
		final double[] distances = new double[Math.max(radiusZ, 0) + 1];
		for(int offX = (int)-radii.x; offX <= (int)radii.x; offX++) {
			final long xSqr = (long)offX * offX;
			final int x = cx + offX;
			for(int offY = (int)-radii.y; offY <= (int)radii.y; offY++) {
				final long xySqr = xSqr + (long)offY * offY;
				final int y = cy + offY;
				for(int offZ = 0; offZ <= radiusZ; offZ++) {
					distances[offZ] = Math.sqrt(xySqr + (double)offZ * offZ);
				}
				for(int offZ = -radiusZ; offZ <= radiusZ; offZ++) {
					BlockPos pos = new BlockPos(x, y, cz + offZ);
					BlockState state = level.getBlockState(pos);
					blockEffect.doBlockExplosion(level, pos, state, distances[Math.abs(offZ)]);
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
		//see doTopBlockExplosionForAll for the reasoning behind the two mutable positions, the carried
		//block state and the guard in front of the DirectionalPlaceContext
		final BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
		final BlockPos.MutableBlockPos below = new BlockPos.MutableBlockPos();
		for(int offX = -radius; offX <= radius; offX++) {
			final long xSqr = (long)offX * offX;
			final int x = cx + offX;
			for(int offZ = -radius; offZ <= radius; offZ++) {
				final long xzSqr = xSqr + (long)offZ * offZ;
				if(xzSqr > radiusSqr) {
					continue;
				}
				final int z = cz + offZ;
				final int yMax = floorSqrt(radiusSqr - xzSqr);
				BlockState state = null;
				topToBottom: for(int offY = yMax; offY >= -yMax; offY--) {
					final int y = cy + offY;
					pos.set(x, y, z);
					if(state == null) {
						state = level.getBlockState(pos);
					}
					below.set(x, y - 1, z);
					final BlockState belowState = level.getBlockState(below);
					if(belowState.isCollisionShapeFullBlock(level, below) || belowState.isFaceSturdy(level, below, Direction.UP)) {
						final boolean noFullCollision = !state.isCollisionShapeFullBlock(level, pos);
						if(state.isAir()
								|| (noFullCollision && state.getBlock().getExplosionResistance() == 0)
								|| state.is(BlockTags.FLOWERS)
								|| ((noFullCollision || state.canBeReplaced()) && state.canBeReplaced(new DirectionalPlaceContext(level, pos, Direction.DOWN, ItemStack.EMPTY, Direction.UP)))) {
							//the effect may keep the position, so only the accepted one becomes a real BlockPos
							blockEffect.doBlockExplosion(level, pos.immutable(), state, Math.sqrt(xzSqr + (double)offY * offY));
							break topToBottom;
						}
					}
					state = belowState;
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
		//two reused mutable positions instead of two BlockPos per visited cell. The state read one block
		//further down is carried into the next iteration, but only while the condition was not consulted:
		//a condition is allowed to edit the world (NetherGroveTNTEffect does), so after every call to it the
		//state is read again. Above the ground, where belowState is air and the condition is skipped, this
		//halves the block state lookups of the column walk.
		final BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
		final BlockPos.MutableBlockPos below = new BlockPos.MutableBlockPos();
		for(int offX = -radius; offX <= radius; offX++) {
			final long xSqr = (long)offX * offX;
			final int x = cx + offX;
			for(int offZ = -radius; offZ <= radius; offZ++) {
				final long xzSqr = xSqr + (long)offZ * offZ;
				if(xzSqr > radiusSqr) {
					continue;
				}
				final int z = cz + offZ;
				final int yMax = floorSqrt(radiusSqr - xzSqr);
				BlockState state = null;
				topToBottom: for(int offY = yMax; offY >= -yMax; offY--) {
					final int y = cy + offY;
					pos.set(x, y, z);
					if(state == null) {
						state = level.getBlockState(pos);
					}
					below.set(x, y - 1, z);
					final BlockState belowState = level.getBlockState(below);
					if(!belowState.isAir()) {
						//the condition may keep the position, so it gets a real BlockPos
						if(condition.conditionMet(level, below.immutable(), belowState, Math.sqrt(xzSqr + (double)(offY - 1) * (offY - 1)))) {
							blockEffect.doBlockExplosion(level, pos.immutable(), state, Math.sqrt(xzSqr + (double)offY * offY));
							break topToBottom;
						}
						state = null;
					} else {
						state = belowState;
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
		//This traversal visits the whole volume of the sphere and used to pay, per visited cell, two BlockPos
		//(pos and pos.below()), two block state lookups and - for every solid block sitting on another solid
		//block, which is essentially all underground rock - a DirectionalPlaceContext plus the BlockHitResult
		//and Vec3 it allocates, only for canBeReplaced to return false.
		//Now two mutable positions are reused, the state read one block further down is carried into the next
		//iteration (one lookup per cell instead of two) and the context is only built for a state that can
		//possibly be replaceable: a block whose collision shape is a full block is never replaceable unless it
		//carries the replaceable property.
		//At radius 150 (WinterTNTEffect) that is 14.1 M cells, so ~28 M state reads and ~10 M contexts before
		//and ~14 M state reads and next to no contexts after. Only the far smaller set of accepted positions
		//is still turned into a real BlockPos, because the effect may keep it.
		final BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
		final BlockPos.MutableBlockPos below = new BlockPos.MutableBlockPos();
		for(int offX = -radius; offX <= radius; offX++) {
			final long xSqr = (long)offX * offX;
			final int x = cx + offX;
			for(int offZ = -radius; offZ <= radius; offZ++) {
				final long xzSqr = xSqr + (long)offZ * offZ;
				if(xzSqr > radiusSqr) {
					continue;
				}
				final int z = cz + offZ;
				final int yMax = floorSqrt(radiusSqr - xzSqr);
				BlockState state = null;
				for(int offY = yMax; offY >= -yMax; offY--) {
					final int y = cy + offY;
					pos.set(x, y, z);
					if(state == null) {
						state = level.getBlockState(pos);
					}
					below.set(x, y - 1, z);
					final BlockState belowState = level.getBlockState(below);
					if(belowState.isCollisionShapeFullBlock(level, below) || belowState.isFaceSturdy(level, below, Direction.UP)) {
						final boolean noFullCollision = !state.isCollisionShapeFullBlock(level, pos);
						if(state.isAir()
								|| (noFullCollision && state.getBlock().getExplosionResistance() == 0)
								|| state.is(BlockTags.FLOWERS)
								|| ((noFullCollision || state.canBeReplaced()) && state.canBeReplaced(new DirectionalPlaceContext(level, pos, Direction.DOWN, ItemStack.EMPTY, Direction.UP)))) {
							blockEffect.doBlockExplosion(level, pos.immutable(), state, Math.sqrt(xzSqr + (double)offY * offY));
							//the effect may have edited this position, so the state below is read again instead of being reused
							state = null;
							continue;
						}
					}
					state = belowState;
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

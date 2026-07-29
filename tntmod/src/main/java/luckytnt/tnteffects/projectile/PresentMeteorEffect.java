package luckytnt.tnteffects.projectile;

import java.util.Random;

import luckytnt.block.PresentBlock;
import luckytnt.config.LuckyTNTConfigValues;
import luckytnt.registry.BlockRegistry;
import luckytnt.util.BlockSurviveChecks;
import luckytntlib.util.IExplosiveEntity;
import luckytntlib.util.explosions.ExplosionHelper;
import luckytntlib.util.explosions.IForEachBlockExplosionEffect;
import luckytntlib.util.explosions.ImprovedExplosion;
import luckytntlib.util.tnteffects.PrimedTNTEffect;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SnowLayerBlock;
import net.minecraft.world.entity.Entity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.DirectionalPlaceContext;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class PresentMeteorEffect extends PrimedTNTEffect {

	@Override
	public void serverExplosion(IExplosiveEntity entity) {
		((ServerLevel)entity.getLevel()).sendParticles(ParticleTypes.WAX_OFF, entity.x(), entity.y() + 2, entity.z(), 500, 3f, 3f, 3f, 0f);
		Random random = new Random();
		if(LuckyTNTConfigValues.PRESENT_DROP_DESTROY_BLOCKS.get()) {
			ImprovedExplosion explosion = new ImprovedExplosion(entity.getLevel(), (Entity)entity, entity.getPos(), 40);
			explosion.doEntityExplosion(3, true);
			ExplosionHelper.doSphericalExplosion(entity.getLevel(), entity.getPos(), 40, new IForEachBlockExplosionEffect() {

				@Override
				public void doBlockExplosion(Level level, BlockPos pos, BlockState state, double distance) {
					//Both branches below can only ever turn a block into air or ice, so an already empty position
					//has nothing to contribute. Rejecting it here spares a setBlock (and its neighbour update)
					//for every air block of the sphere, which is the majority of its volume.
					//The resistance test is pulled up as well because it gates both branches anyway.
					if(state.isAir() || state.getBlock().getExplosionResistance() > 100) {
						return;
					}
					if(distance <= 35) {
						state.getBlock().wasExploded((ServerLevel) level, pos, explosion);
						level.setBlock(pos, Blocks.AIR.defaultBlockState(), Block.UPDATE_ALL);
					}
					else if(Math.random() < 0.6f) {
						state.getBlock().wasExploded((ServerLevel) level, pos, explosion);
						//This used to be a setBlock to air followed by a second setBlock of the ice onto the very
						//same position, so every ice block cost two chunk writes and two neighbour update rounds.
						//The final state is decided first and written once. The random draws are unchanged:
						//the 0.5 draw is still only made when the 0.25 draw succeeded.
						level.setBlock(pos, Math.random() < 0.25f
								? (Math.random() < 0.5f ? Blocks.BLUE_ICE.defaultBlockState() : Blocks.PACKED_ICE.defaultBlockState())
								: Blocks.AIR.defaultBlockState(), Block.UPDATE_ALL);
					}
				}
			});
		}
		forEachTopBlock(entity.getLevel(), entity.getPos(), 70, new IForEachBlockExplosionEffect() {

			@Override
			public void doBlockExplosion(Level level, BlockPos pos, BlockState state, double distance) {
				if(Math.random() < 0.025f) {
					Direction dir = Direction.NORTH;
					switch(random.nextInt(4)) {
						case 1: dir = Direction.EAST; break;
						case 2: dir = Direction.SOUTH; break;
						case 3: dir = Direction.WEST; break;
					}
					//Presents are purely decorative: they have no updateShape, no canSurvive and nothing reacts to
					//them, so UPDATE_CLIENTS is enough and the neighbour update storm of UPDATE_ALL is avoided.
					//pos is a reused mutable position, so it is made immutable before it is handed to the level.
					level.setBlock(pos.immutable(), BlockRegistry.PRESENT.get().defaultBlockState().setValue(PresentBlock.FACING, dir).setValue(PresentBlock.TYPE, random.nextInt(4)), Block.UPDATE_CLIENTS);
				}
				else if(BlockSurviveChecks.canSnowPlaceAt(state, level, pos) && (distance < 60 || Math.random() < 0.7f)) {
					//The support of the snow layer has just been verified, so it does not need a shape update either.
					level.setBlock(pos.immutable(), Blocks.SNOW.defaultBlockState().setValue(SnowLayerBlock.LAYERS, random.nextInt(1, 3)), Block.UPDATE_CLIENTS);
				}
			}
		});
	}

	/**
	 * Allocation lean variant of {@link ExplosionHelper#doTopBlockExplosionForAll(Level, Vec3, int, IForEachBlockExplosionEffect)}.
	 * It visits exactly the same positions in exactly the same order and accepts them by the same predicate, but
	 * <ul>
	 * <li>reuses two mutable positions instead of allocating two {@link BlockPos} per visited position,
	 * <li>reuses the state read one block further down as the state of the next iteration, halving the block state lookups,
	 * <li>and only builds the {@link DirectionalPlaceContext} for states that can possibly be replaceable.
	 * </ul>
	 * The last point is what actually mattered: a sphere of radius 70 centered on the ground contains roughly
	 * 700k solid blocks that all sit on another solid block, and the library allocated a DirectionalPlaceContext
	 * (plus its hit result and vector) for every single one of them only to have {@code canBeReplaced} return false.
	 * A block whose collision shape is a full block is never replaceable unless it carries the replaceable
	 * property, so that combination is the guard.
	 * @param level  the current level
	 * @param position  the center position of the top block explosion
	 * @param radius  the radius of the sphere
	 * @param blockEffect  determines what should happen to the blocks gotten by this function
	 */
	private static void forEachTopBlock(Level level, Vec3 position, int radius, IForEachBlockExplosionEffect blockEffect) {
		final int cx = Mth.floor(position.x);
		final int cy = Mth.floor(position.y);
		final int cz = Mth.floor(position.z);
		final long radiusSqr = (long)radius * radius;
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
							blockEffect.doBlockExplosion(level, pos, state, Math.sqrt(xzSqr + (double)offY * offY));
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

	@Override
	public void spawnParticles(IExplosiveEntity entity) {
		entity.getLevel().addParticle(ParticleTypes.SNOWFLAKE, entity.x(), entity.y() + 4, entity.z(), 0, 0, 0);
	}

	@Override
	public float getSize(IExplosiveEntity entity) {
		return 4;
	}

	@Override
	public BlockState getBlockState(IExplosiveEntity entity) {
		if(!entity.getPersistentData().getBooleanOr("has_present", false)) {
			CompoundTag tag = entity.getPersistentData();
			tag.putBoolean("has_present", true);
			tag.putInt("type", new Random().nextInt(4));
			entity.setPersistentData(tag);
		}
		return BlockRegistry.PRESENT.get().defaultBlockState().setValue(PresentBlock.TYPE, entity.getPersistentData().getIntOr("type", 0));
	}
}

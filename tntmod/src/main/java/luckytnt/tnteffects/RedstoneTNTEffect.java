package luckytnt.tnteffects;

import net.minecraft.server.level.ServerLevel;

import luckytnt.registry.BlockRegistry;
import luckytntlib.util.IExplosiveEntity;
import luckytntlib.util.explosions.ExplosionHelper;
import luckytntlib.util.explosions.IBlockExplosionCondition;
import luckytntlib.util.explosions.IForEachBlockExplosionEffect;
import luckytntlib.util.explosions.ImprovedExplosion;
import luckytntlib.util.tnteffects.PrimedTNTEffect;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.properties.AttachFace;
import net.minecraft.world.level.block.state.properties.ComparatorMode;
import net.minecraft.world.item.context.DirectionalPlaceContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;

public class RedstoneTNTEffect extends PrimedTNTEffect{

	@Override
	public void serverExplosion(IExplosiveEntity entity) {
		// Every accepted block used to allocate between one and four `new Random()` (the switch plus the
		// direction helpers), each of which also contends on java.util.Random's global seed uniquifier.
		// One RandomSource for the whole sweep, as AnimalKingdomEffect:89 and HoneyTNTEffect already do.
		final Level sweepLevel = entity.getLevel();
		final ServerLevel sLevel = (ServerLevel)sweepLevel;
		final RandomSource random = sweepLevel.getRandom();
		final ImprovedExplosion dummy = ImprovedExplosion.dummyExplosion(sweepLevel);
		ExplosionHelper.doTopBlockExplosion(sweepLevel, entity.getPos(), 10, new IBlockExplosionCondition() {
			
			@Override
			public boolean conditionMet(Level level, BlockPos pos, BlockState state, double distance) {
				return (state.isCollisionShapeFullBlock(level, pos) || state.isFaceSturdy(level, pos, Direction.UP)) && (level.getBlockState(pos.above()).isAir() || level.getBlockState(pos.above()).canBeReplaced(new DirectionalPlaceContext(level, pos.above(), Direction.DOWN, ItemStack.EMPTY, Direction.UP)) || !level.getBlockState(pos.above()).isCollisionShapeFullBlock(level, pos.above()) || level.getBlockState(pos.above()).is(BlockTags.FLOWERS)) && (Math.random() < 0.4f && !state.is(BlockTags.LEAVES));
			}
		}, new IForEachBlockExplosionEffect() {
			
			@Override
			public void doBlockExplosion(Level level, BlockPos pos, BlockState state, double distance) {
				int roll = random.nextInt(17);
				BlockState replace = null;

				switch(roll) {
					case 0: replace = Blocks.REDSTONE_BLOCK.defaultBlockState(); break;
					case 1: replace = Blocks.REDSTONE_LAMP.defaultBlockState(); break;
					case 2: replace = Blocks.NOTE_BLOCK.defaultBlockState().setValue(BlockStateProperties.NOTE, random.nextInt(25)); break;
					case 3: replace = Blocks.REDSTONE_TORCH.defaultBlockState(); break;
					case 4: replace = Blocks.REDSTONE_WIRE.defaultBlockState(); break;
					case 5: replace = Blocks.TARGET.defaultBlockState(); break;
					case 6: replace = Blocks.SCULK_SENSOR.defaultBlockState(); break;
					case 7: replace = Blocks.HOPPER.defaultBlockState().setValue(BlockStateProperties.FACING_HOPPER, getRandomDirectionNotUp(random)); break;
					case 8: replace = Blocks.PISTON.defaultBlockState().setValue(BlockStateProperties.FACING, getRandomDirection(random)); break;
					case 9: replace = Blocks.STICKY_PISTON.defaultBlockState().setValue(BlockStateProperties.FACING, getRandomDirection(random)); break;
					case 10: replace = Blocks.OBSERVER.defaultBlockState().setValue(BlockStateProperties.FACING, getRandomDirection(random)); break;
					case 11: replace = Blocks.DROPPER.defaultBlockState().setValue(BlockStateProperties.FACING, getRandomDirection(random)); break;
					case 12: replace = Blocks.DISPENSER.defaultBlockState().setValue(BlockStateProperties.FACING, getRandomDirection(random)); break;
					case 13: replace = Blocks.DAYLIGHT_DETECTOR.defaultBlockState().setValue(BlockStateProperties.INVERTED, Math.random() < 0.5f); break;
					case 14: replace = Blocks.LEVER.defaultBlockState().setValue(BlockStateProperties.POWERED, Math.random() < 0.5f).setValue(BlockStateProperties.HORIZONTAL_FACING, getRandomDirectionHorizontal(random)).setValue(BlockStateProperties.ATTACH_FACE, AttachFace.FLOOR); break;
					case 15: replace = Blocks.REPEATER.defaultBlockState().setValue(BlockStateProperties.HORIZONTAL_FACING, getRandomDirectionHorizontal(random)).setValue(BlockStateProperties.DELAY, 1 + random.nextInt(4)).setValue(BlockStateProperties.LOCKED, Math.random() < 0.5f); break;
					case 16: replace = Blocks.COMPARATOR.defaultBlockState().setValue(BlockStateProperties.HORIZONTAL_FACING, getRandomDirectionHorizontal(random)).setValue(BlockStateProperties.MODE_COMPARATOR, Math.random() < 0.5f ? ComparatorMode.COMPARE : ComparatorMode.SUBTRACT); break;
				}
				Block block = state.getBlock();
				block.wasExploded(sLevel, pos, dummy);
				level.setBlock(pos, replace, 3);
			}
		});
	}
	
	@Override
	public void spawnParticles(IExplosiveEntity entity) {
		entity.getLevel().addParticle(DustParticleOptions.REDSTONE, entity.x(), entity.y() + 1f, entity.z(), 0, 0, 0);
	}
	
	@Override
	public Block getBlock() {
		return BlockRegistry.REDSTONE_TNT.get();
	}
	
	/**
	 * @deprecated allocates a fresh RandomSource per call, use {@link #getRandomDirection(RandomSource)}
	 */
	@Deprecated
	public Direction getRandomDirection() {
		return getRandomDirection(RandomSource.create());
	}

	public Direction getRandomDirection(RandomSource source) {
		int random = source.nextInt(6);
		switch(random) {
			case 0: return Direction.DOWN;
			case 1: return Direction.UP;
			case 2: return Direction.NORTH;
			case 3: return Direction.EAST;
			case 4: return Direction.SOUTH;
			case 5: return Direction.WEST;
		}
		return Direction.UP;
	}
	
	/**
	 * @deprecated allocates a fresh RandomSource per call, use {@link #getRandomDirectionNotUp(RandomSource)}
	 */
	@Deprecated
	public Direction getRandomDirectionNotUp() {
		return getRandomDirectionNotUp(RandomSource.create());
	}

	public Direction getRandomDirectionNotUp(RandomSource source) {
		int random = source.nextInt(5);
		switch(random) {
			case 0: return Direction.DOWN;
			case 1: return Direction.NORTH;
			case 2: return Direction.EAST;
			case 3: return Direction.SOUTH;
			case 4: return Direction.WEST;
		}
		return Direction.DOWN;
	}
	
	/**
	 * @deprecated allocates a fresh RandomSource per call, use {@link #getRandomDirectionHorizontal(RandomSource)}
	 */
	@Deprecated
	public Direction getRandomDirectionHorizontal() {
		return getRandomDirectionHorizontal(RandomSource.create());
	}

	public Direction getRandomDirectionHorizontal(RandomSource source) {
		int random = source.nextInt(4);
		switch(random) {
			case 0: return Direction.NORTH;
			case 1: return Direction.EAST;
			case 2: return Direction.SOUTH;
			case 3: return Direction.WEST;
		}
		return Direction.NORTH;
	}
}

package luckytnt.tnteffects;

import java.util.Random;

import luckytnt.registry.BlockRegistry;
import luckytntlib.util.IExplosiveEntity;
import luckytntlib.util.explosions.ExplosionHelper;
import luckytntlib.util.explosions.IBlockExplosionCondition;
import luckytntlib.util.explosions.IForEachBlockExplosionEffect;
import luckytntlib.util.explosions.ImprovedExplosion;
import luckytntlib.util.tnteffects.PrimedTNTEffect;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.enums.BlockFace;
import net.minecraft.block.enums.ComparatorMode;
import net.minecraft.item.AutomaticItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class RedstoneTNTEffect extends PrimedTNTEffect{

	@Override
	public void serverExplosion(IExplosiveEntity entity) {
		ExplosionHelper.doTopBlockExplosion(entity.getLevel(), entity.getPos(), 10, new IBlockExplosionCondition() {
			
			@Override
			public boolean conditionMet(World level, BlockPos pos, BlockState state, double distance) {
				return (state.isFullCube(level, pos) || state.isSideSolidFullSquare(level, pos, Direction.UP)) && (level.getBlockState(pos.up()).isAir() || level.getBlockState(pos.up()).canReplace(new AutomaticItemPlacementContext(level, pos.up(), Direction.DOWN, ItemStack.EMPTY, Direction.UP)) || !level.getBlockState(pos.up()).isFullCube(level, pos.up()) || level.getBlockState(pos.up()).isIn(BlockTags.FLOWERS)) && (Math.random() < 0.4f && !state.isIn(BlockTags.LEAVES));
			}
		}, new IForEachBlockExplosionEffect() {
			
			@Override
			public void doBlockExplosion(World level, BlockPos pos, BlockState state, double distance) {
				int random = new Random().nextInt(17);
				BlockState replace = null;
				
				switch(random) {
					case 0: replace = Blocks.REDSTONE_BLOCK.getDefaultState(); break;
					case 1: replace = Blocks.REDSTONE_LAMP.getDefaultState(); break;
					case 2: replace = Blocks.NOTE_BLOCK.getDefaultState().with(Properties.NOTE, new Random().nextInt(25)); break;
					case 3: replace = Blocks.REDSTONE_TORCH.getDefaultState(); break;
					case 4: replace = Blocks.REDSTONE_WIRE.getDefaultState(); break;
					case 5: replace = Blocks.TARGET.getDefaultState(); break;
					case 6: replace = Blocks.SCULK_SENSOR.getDefaultState(); break;
					case 7: replace = Blocks.HOPPER.getDefaultState().with(Properties.HOPPER_FACING, getRandomDirectionNotUp()); break;
					case 8: replace = Blocks.PISTON.getDefaultState().with(Properties.FACING, getRandomDirection()); break;
					case 9: replace = Blocks.STICKY_PISTON.getDefaultState().with(Properties.FACING, getRandomDirection()); break;
					case 10: replace = Blocks.OBSERVER.getDefaultState().with(Properties.FACING, getRandomDirection()); break;
					case 11: replace = Blocks.DROPPER.getDefaultState().with(Properties.FACING, getRandomDirection()); break;
					case 12: replace = Blocks.DISPENSER.getDefaultState().with(Properties.FACING, getRandomDirection()); break;
					case 13: replace = Blocks.DAYLIGHT_DETECTOR.getDefaultState().with(Properties.INVERTED, Math.random() < 0.5f); break;
					case 14: replace = Blocks.LEVER.getDefaultState().with(Properties.POWERED, Math.random() < 0.5f).with(Properties.HORIZONTAL_FACING, getRandomDirectionHorizontal()).with(Properties.BLOCK_FACE, BlockFace.FLOOR); break;
					case 15: replace = Blocks.REPEATER.getDefaultState().with(Properties.HORIZONTAL_FACING, getRandomDirectionHorizontal()).with(Properties.DELAY, 1 + new Random().nextInt(4)).with(Properties.LOCKED, Math.random() < 0.5f); break;
					case 16: replace = Blocks.COMPARATOR.getDefaultState().with(Properties.HORIZONTAL_FACING, getRandomDirectionHorizontal()).with(Properties.COMPARATOR_MODE, Math.random() < 0.5f ? ComparatorMode.COMPARE : ComparatorMode.SUBTRACT); break;
				}
				Block block = state.getBlock();
				block.onDestroyedByExplosion(entity.getLevel(), pos, ImprovedExplosion.dummyExplosion(entity.getLevel()));
				entity.getLevel().setBlockState(pos, replace, 3);
			}
		});
	}
	
	@Override
	public void spawnParticles(IExplosiveEntity entity) {
		entity.getLevel().addParticle(DustParticleEffect.DEFAULT, entity.x(), entity.y() + 1f, entity.z(), 0, 0, 0);
	}
	
	@Override
	public Block getBlock() {
		return BlockRegistry.REDSTONE_TNT.get();
	}
	
	public Direction getRandomDirection() {
		int random = new Random().nextInt(6);
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
	
	public Direction getRandomDirectionNotUp() {
		int random = new Random().nextInt(5);
		switch(random) {
			case 0: return Direction.DOWN;
			case 1: return Direction.NORTH;
			case 2: return Direction.EAST;
			case 3: return Direction.SOUTH;
			case 4: return Direction.WEST;
		}
		return Direction.DOWN;
	}
	
	public Direction getRandomDirectionHorizontal() {
		int random = new Random().nextInt(4);
		switch(random) {
			case 0: return Direction.NORTH;
			case 1: return Direction.EAST;
			case 2: return Direction.SOUTH;
			case 3: return Direction.WEST;
		}
		return Direction.NORTH;
	}
}

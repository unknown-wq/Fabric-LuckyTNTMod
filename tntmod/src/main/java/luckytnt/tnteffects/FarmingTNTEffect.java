package luckytnt.tnteffects;

import java.util.Random;

import org.joml.Vector3f;

import luckytnt.registry.BlockRegistry;
import luckytntlib.util.IExplosiveEntity;
import luckytntlib.util.explosions.ExplosionHelper;
import luckytntlib.util.explosions.IBlockExplosionCondition;
import luckytntlib.util.explosions.IForEachBlockExplosionEffect;
import luckytntlib.util.tnteffects.PrimedTNTEffect;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FarmlandBlock;
import net.minecraft.item.AutomaticItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class FarmingTNTEffect extends PrimedTNTEffect{

	private final int radius;
	
	public FarmingTNTEffect(int radius) {
		this.radius = radius;
	}
	
	@Override
	public void serverExplosion(IExplosiveEntity entity) {
		ExplosionHelper.doTopBlockExplosion(entity.getLevel(), entity.getPos(), radius, new IBlockExplosionCondition() {
			
			@Override
			public boolean conditionMet(World level, BlockPos pos, BlockState state, double distance) {
				return (state.isFullCube(level, pos) || state.isSideSolidFullSquare(level, pos, Direction.UP)) && (level.getBlockState(pos.up()).isAir() || level.getBlockState(pos.up()).canReplace(new AutomaticItemPlacementContext(level, pos.up(), Direction.DOWN, ItemStack.EMPTY, Direction.UP)) || !level.getBlockState(pos.up()).isFullCube(level, pos.up()) || level.getBlockState(pos.up()).isIn(BlockTags.FLOWERS)) && (!state.isIn(BlockTags.LEAVES) && !state.isIn(BlockTags.LOGS) && state.getBlock().getBlastResistance() < 100);
			}
		}, new IForEachBlockExplosionEffect() {
			
			@Override
			public void doBlockExplosion(World level, BlockPos pos, BlockState state, double distance) {
				if (Math.random() < 0.7f) {
					BlockState crop = Blocks.POTATOES.getDefaultState();
					int rand = new Random().nextInt(6);
					switch (rand) {
					case 0: crop = Blocks.CARROTS.getDefaultState().with(Properties.AGE_7, new Random().nextInt(8)); break;
					case 1: crop = Blocks.POTATOES.getDefaultState().with(Properties.AGE_7, new Random().nextInt(8)); break;
					case 2: crop = Blocks.WHEAT.getDefaultState().with(Properties.AGE_7, new Random().nextInt(8)); break;
					case 3: crop = Blocks.BEETROOTS.getDefaultState().with(Properties.AGE_3, new Random().nextInt(4)); break;
					case 4: crop = Blocks.PUMPKIN_STEM.getDefaultState().with(Properties.AGE_7, new Random().nextInt(8)); break;
					case 5: crop = Blocks.MELON_STEM.getDefaultState().with(Properties.AGE_7, new Random().nextInt(8)); break;
					}
					if (Math.random() < 0.8f) {
						level.setBlockState(pos.down(), Blocks.FARMLAND.getDefaultState().with(Properties.MOISTURE, 7), 3);
						level.setBlockState(pos, crop, 3);
					} else {
						if ((level.getBlockState(pos.down().north()).isFullCube(level, pos.north()) || level.getBlockState(pos.north()).getBlock() instanceof FarmlandBlock) 
								&& (level.getBlockState(pos.down().south()).isFullCube(level, pos.south()) || level.getBlockState(pos.south()).getBlock() instanceof FarmlandBlock) 
								&& (level.getBlockState(pos.down().east()).isFullCube(level, pos.east()) || level.getBlockState(pos.east()).getBlock() instanceof FarmlandBlock) 
								&& (level.getBlockState(pos.down().west()).isFullCube(level, pos.west()) || level.getBlockState(pos.west()).getBlock() instanceof FarmlandBlock)) {
							level.setBlockState(pos.down(), Blocks.WATER.getDefaultState(), 3);
						}
					}
				}
			}
		});
	}
	
	@Override
	public void spawnParticles(IExplosiveEntity entity) {
		entity.getLevel().addParticle(new DustParticleEffect(new Vector3f(1f, 0.5f, 0.1f), 1f), entity.x(), entity.y() + 1f, entity.z(), 0, 0, 0);
	}
	
	@Override
	public Block getBlock() {
		return BlockRegistry.FARMING_TNT.get();
	}
}

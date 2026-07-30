package luckytnt.tnteffects;

import org.joml.Vector3f;

import luckytnt.registry.BlockRegistry;
import luckytntlib.util.IExplosiveEntity;
import luckytntlib.util.explosions.ExplosionHelper;
import luckytntlib.util.explosions.IBlockExplosionCondition;
import luckytntlib.util.explosions.IForEachBlockExplosionEffect;
import luckytntlib.util.tnteffects.PrimedTNTEffect;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FarmlandBlock;
import net.minecraft.world.item.context.DirectionalPlaceContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;

public class FarmingTNTEffect extends PrimedTNTEffect{

	private final int radius;
	
	public FarmingTNTEffect(int radius) {
		this.radius = radius;
	}
	
	@Override
	public void serverExplosion(IExplosiveEntity entity) {
		// up to two `new Random()` per placed crop, each one contending on java.util.Random's global seed
		// uniquifier; one RandomSource for the whole sweep instead (AnimalKingdomEffect:89 pattern)
		final RandomSource random = entity.getLevel().getRandom();
		ExplosionHelper.doTopBlockExplosion(entity.getLevel(), entity.getPos(), radius, new IBlockExplosionCondition() {
			
			@Override
			public boolean conditionMet(Level level, BlockPos pos, BlockState state, double distance) {
				return (state.isCollisionShapeFullBlock(level, pos) || state.isFaceSturdy(level, pos, Direction.UP)) && (level.getBlockState(pos.above()).isAir() || level.getBlockState(pos.above()).canBeReplaced(new DirectionalPlaceContext(level, pos.above(), Direction.DOWN, ItemStack.EMPTY, Direction.UP)) || !level.getBlockState(pos.above()).isCollisionShapeFullBlock(level, pos.above()) || level.getBlockState(pos.above()).is(BlockTags.FLOWERS)) && (!state.is(BlockTags.LEAVES) && !state.is(BlockTags.LOGS) && state.getBlock().getExplosionResistance() < 100);
			}
		}, new IForEachBlockExplosionEffect() {
			
			@Override
			public void doBlockExplosion(Level level, BlockPos pos, BlockState state, double distance) {
				if (Math.random() < 0.7f) {
					BlockState crop = Blocks.POTATOES.defaultBlockState();
					int rand = random.nextInt(6);
					switch (rand) {
					case 0: crop = Blocks.CARROTS.defaultBlockState().setValue(BlockStateProperties.AGE_7, random.nextInt(8)); break;
					case 1: crop = Blocks.POTATOES.defaultBlockState().setValue(BlockStateProperties.AGE_7, random.nextInt(8)); break;
					case 2: crop = Blocks.WHEAT.defaultBlockState().setValue(BlockStateProperties.AGE_7, random.nextInt(8)); break;
					case 3: crop = Blocks.BEETROOTS.defaultBlockState().setValue(BlockStateProperties.AGE_3, random.nextInt(4)); break;
					case 4: crop = Blocks.PUMPKIN_STEM.defaultBlockState().setValue(BlockStateProperties.AGE_7, random.nextInt(8)); break;
					case 5: crop = Blocks.MELON_STEM.defaultBlockState().setValue(BlockStateProperties.AGE_7, random.nextInt(8)); break;
					}
					if (Math.random() < 0.8f) {
						level.setBlock(pos.below(), Blocks.FARMLAND.defaultBlockState().setValue(BlockStateProperties.MOISTURE, 7), 3);
						level.setBlock(pos, crop, 3);
					} else {
						if ((level.getBlockState(pos.below().north()).isCollisionShapeFullBlock(level, pos.north()) || level.getBlockState(pos.north()).getBlock() instanceof FarmlandBlock) 
								&& (level.getBlockState(pos.below().south()).isCollisionShapeFullBlock(level, pos.south()) || level.getBlockState(pos.south()).getBlock() instanceof FarmlandBlock) 
								&& (level.getBlockState(pos.below().east()).isCollisionShapeFullBlock(level, pos.east()) || level.getBlockState(pos.east()).getBlock() instanceof FarmlandBlock) 
								&& (level.getBlockState(pos.below().west()).isCollisionShapeFullBlock(level, pos.west()) || level.getBlockState(pos.west()).getBlock() instanceof FarmlandBlock)) {
							level.setBlock(pos.below(), Blocks.WATER.defaultBlockState(), 3);
						}
					}
				}
			}
		});
	}
	
	@Override
	public void spawnParticles(IExplosiveEntity entity) {
		entity.getLevel().addParticle(new DustParticleOptions(((int)(1f*255)<<16)|((int)(0.5f*255)<<8)|(int)(0.1f*255), 1f), entity.x(), entity.y() + 1f, entity.z(), 0, 0, 0);
	}
	
	@Override
	public Block getBlock() {
		return BlockRegistry.FARMING_TNT.get();
	}
}

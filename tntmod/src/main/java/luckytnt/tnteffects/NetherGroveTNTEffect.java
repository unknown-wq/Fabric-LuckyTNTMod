package luckytnt.tnteffects;

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
import net.minecraft.world.item.AutomaticItemPlacementContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.registries.Registries;
import net.minecraft.core.Holder;
import net.minecraft.tags.BlockTags;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.NetherConfiguredFeatures;
import net.minecraft.world.gen.feature.TreeConfiguredFeatures;

public class NetherGroveTNTEffect extends PrimedTNTEffect{

	private final int radius;
	
	public NetherGroveTNTEffect(int radius) {
		this.radius = radius;
	}
	
	@Override
	public void serverExplosion(IExplosiveEntity entity) {
		Holder<ConfiguredFeature<?, ?>> tree;
		Holder<ConfiguredFeature<?, ?>> vegetation;
		Block topBlock;
		if (Math.random() < 0.5D) {
			tree = entity.getLevel().registryAccess().get(Registries.CONFIGURED_FEATURE).entryOf(TreeConfiguredFeatures.CRIMSON_FUNGUS);
			vegetation = entity.getLevel().registryAccess().get(Registries.CONFIGURED_FEATURE).entryOf(NetherConfiguredFeatures.CRIMSON_FOREST_VEGETATION_BONEMEAL);
			topBlock = Blocks.CRIMSON_NYLIUM;
		} else {
			tree = entity.getLevel().registryAccess().get(Registries.CONFIGURED_FEATURE).entryOf(TreeConfiguredFeatures.WARPED_FUNGUS);
			vegetation = entity.getLevel().registryAccess().get(Registries.CONFIGURED_FEATURE).entryOf(NetherConfiguredFeatures.WARPED_FOREST_VEGETATION_BONEMEAL);
			topBlock = Blocks.WARPED_NYLIUM;
		}
		ExplosionHelper.doTopBlockExplosion(entity.getLevel(), entity.getPos(), radius, new IBlockExplosionCondition() {
			
			@Override
			public boolean conditionMet(Level level, BlockPos pos, BlockState state, double distance) {
				if(state.is(BlockTags.LEAVES) || state.is(BlockTags.LOGS) || state.is(BlockTags.FLOWERS) || state.is(BlockTags.WART_BLOCKS) || (!state.isCollisionShapeFullBlock(level, pos) && state.getBlock().getExplosionResistance() < 100)) {
					state.getBlock().wasExploded(level, pos, ImprovedExplosion.dummyExplosion(entity.getLevel()));
					level.setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
					return false;
				}
				return (state.isCollisionShapeFullBlock(level, pos) || state.isFaceSturdy(level, pos, Direction.UP)) && (level.getBlockState(pos.above()).isAir() || level.getBlockState(pos.above()).canReplace(new AutomaticItemPlacementContext(level, pos.above(), Direction.DOWN, ItemStack.EMPTY, Direction.UP)) || !level.getBlockState(pos.above()).isCollisionShapeFullBlock(level, pos.above()) || level.getBlockState(pos.above()).is(BlockTags.FLOWERS));
			}
		}, new IForEachBlockExplosionEffect() {
			
			@Override
			public void doBlockExplosion(Level level, BlockPos pos, BlockState state, double distance) {
				BlockPos posBelow = pos.below();
				BlockState stateBelow = level.getBlockState(posBelow);
				if(stateBelow.getBlock().getExplosionResistance() < 100) {
					stateBelow.getBlock().wasExploded(level, posBelow, ImprovedExplosion.dummyExplosion(entity.getLevel()));
					level.setBlock(posBelow, topBlock.defaultBlockState());
				}
			}
		});
		ExplosionHelper.doTopBlockExplosion(entity.getLevel(), entity.getPos(), radius, new IForEachBlockExplosionEffect() {
			
			@Override
			public void doBlockExplosion(Level level, BlockPos pos, BlockState state, double distance) {
				if(level instanceof ServerLevel sLevel) {
					if(Math.random() < 0.05f) {
						tree.value().generate(sLevel, sLevel.getChunkSource().getChunkGenerator(), sLevel.random, pos);
					}
					if(Math.random() < 0.1f) {
						vegetation.value().generate(sLevel, sLevel.getChunkSource().getChunkGenerator(), sLevel.random, pos);
					}
				}
			}
		});
	}
	
	@Override
	public Block getBlock() {
		return BlockRegistry.NETHER_GROVE_TNT.get();
	}
}

package luckytnt.tnteffects;

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
import net.minecraft.item.AutomaticItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.NetherConfiguredFeatures;
import net.minecraft.world.gen.feature.TreeConfiguredFeatures;

public class NetherGroveTNTEffect extends PrimedTNTEffect{

	private final int radius;
	
	public NetherGroveTNTEffect(int radius) {
		this.radius = radius;
	}
	
	@Override
	public void serverExplosion(IExplosiveEntity entity) {
		RegistryEntry<ConfiguredFeature<?, ?>> tree;
		RegistryEntry<ConfiguredFeature<?, ?>> vegetation;
		Block topBlock;
		if (Math.random() < 0.5D) {
			tree = entity.getLevel().getRegistryManager().get(RegistryKeys.CONFIGURED_FEATURE).entryOf(TreeConfiguredFeatures.CRIMSON_FUNGUS);
			vegetation = entity.getLevel().getRegistryManager().get(RegistryKeys.CONFIGURED_FEATURE).entryOf(NetherConfiguredFeatures.CRIMSON_FOREST_VEGETATION_BONEMEAL);
			topBlock = Blocks.CRIMSON_NYLIUM;
		} else {
			tree = entity.getLevel().getRegistryManager().get(RegistryKeys.CONFIGURED_FEATURE).entryOf(TreeConfiguredFeatures.WARPED_FUNGUS);
			vegetation = entity.getLevel().getRegistryManager().get(RegistryKeys.CONFIGURED_FEATURE).entryOf(NetherConfiguredFeatures.WARPED_FOREST_VEGETATION_BONEMEAL);
			topBlock = Blocks.WARPED_NYLIUM;
		}
		ExplosionHelper.doTopBlockExplosion(entity.getLevel(), entity.getPos(), radius, new IBlockExplosionCondition() {
			
			@Override
			public boolean conditionMet(World level, BlockPos pos, BlockState state, double distance) {
				if(state.isIn(BlockTags.LEAVES) || state.isIn(BlockTags.LOGS) || state.isIn(BlockTags.FLOWERS) || state.isIn(BlockTags.WART_BLOCKS) || (!state.isFullCube(level, pos) && state.getBlock().getBlastResistance() < 100)) {
					state.getBlock().onDestroyedByExplosion(level, pos, ImprovedExplosion.dummyExplosion(entity.getLevel()));
					level.setBlockState(pos, Blocks.AIR.getDefaultState(), 3);
					return false;
				}
				return (state.isFullCube(level, pos) || state.isSideSolidFullSquare(level, pos, Direction.UP)) && (level.getBlockState(pos.up()).isAir() || level.getBlockState(pos.up()).canReplace(new AutomaticItemPlacementContext(level, pos.up(), Direction.DOWN, ItemStack.EMPTY, Direction.UP)) || !level.getBlockState(pos.up()).isFullCube(level, pos.up()) || level.getBlockState(pos.up()).isIn(BlockTags.FLOWERS));
			}
		}, new IForEachBlockExplosionEffect() {
			
			@Override
			public void doBlockExplosion(World level, BlockPos pos, BlockState state, double distance) {
				BlockPos posBelow = pos.down();
				BlockState stateBelow = level.getBlockState(posBelow);
				if(stateBelow.getBlock().getBlastResistance() < 100) {
					stateBelow.getBlock().onDestroyedByExplosion(level, posBelow, ImprovedExplosion.dummyExplosion(entity.getLevel()));
					level.setBlockState(posBelow, topBlock.getDefaultState());
				}
			}
		});
		ExplosionHelper.doTopBlockExplosion(entity.getLevel(), entity.getPos(), radius, new IForEachBlockExplosionEffect() {
			
			@Override
			public void doBlockExplosion(World level, BlockPos pos, BlockState state, double distance) {
				if(level instanceof ServerWorld sLevel) {
					if(Math.random() < 0.05f) {
						tree.value().generate(sLevel, sLevel.getChunkManager().getChunkGenerator(), sLevel.random, pos);
					}
					if(Math.random() < 0.1f) {
						vegetation.value().generate(sLevel, sLevel.getChunkManager().getChunkGenerator(), sLevel.random, pos);
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

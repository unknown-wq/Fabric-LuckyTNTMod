package luckytnt.tnteffects;


import luckytnt.registry.BlockRegistry;
import luckytntlib.util.IExplosiveEntity;
import luckytntlib.util.explosions.ExplosionHelper;
import luckytntlib.util.explosions.IForEachBlockExplosionEffect;
import luckytntlib.util.explosions.ImprovedExplosion;
import luckytntlib.util.tnteffects.PrimedTNTEffect;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.UndergroundConfiguredFeatures;

public class DripstoneTNTEffect extends PrimedTNTEffect{

	private final int radius;
	
	public DripstoneTNTEffect(int radius) {
		this.radius = radius;
	}
	
	@Override
	public void serverExplosion(IExplosiveEntity entity) {
		ExplosionHelper.doSphericalExplosion(entity.getLevel(), entity.getPos(), radius, new IForEachBlockExplosionEffect() {
			
			@Override
			public void doBlockExplosion(World level, BlockPos pos, BlockState state, double distance) {
				if(state.getBlock().getBlastResistance() < 100 && (!state.isFullCube(level, pos) || state.isIn(BlockTags.LEAVES) || state.isIn(BlockTags.LOGS))) {
					state.getBlock().onDestroyedByExplosion(level, pos, ImprovedExplosion.dummyExplosion(entity.getLevel()));
					level.setBlockState(pos, Blocks.AIR.getDefaultState(), 3);
				}
			}
		});
		ExplosionHelper.doSphericalExplosion(entity.getLevel(), entity.getPos(), radius, new IForEachBlockExplosionEffect() {

			@Override
			public void doBlockExplosion(World level, BlockPos pos, BlockState state, double distance) {
				if(level.getBlockState(pos.down()).isAir() && !state.isAir() && state.getBlock().getBlastResistance() < 100 && !state.isIn(BlockTags.DRIPSTONE_REPLACEABLE_BLOCKS)) {
					state.getBlock().onDestroyedByExplosion(level, pos, ImprovedExplosion.dummyExplosion(entity.getLevel()));
					level.setBlockState(pos, Blocks.STONE.getDefaultState());
				}
				else if(level.getBlockState(pos.down()).getBlock().getBlastResistance() < 100 && !level.getBlockState(pos.down()).isAir() && state.isAir() && !level.getBlockState(pos.down()).isIn(BlockTags.DRIPSTONE_REPLACEABLE_BLOCKS)) {
					state.getBlock().onDestroyedByExplosion(level, pos, ImprovedExplosion.dummyExplosion(entity.getLevel()));
					level.setBlockState(pos.down(), Blocks.STONE.getDefaultState());
				}
			}
		});
		if(entity.getLevel() instanceof ServerWorld sLevel) {
			ExplosionHelper.doSphericalExplosion(sLevel, entity.getPos(), Math.round(radius * 0.75f), new IForEachBlockExplosionEffect() {

				@Override
				public void doBlockExplosion(World level, BlockPos pos, BlockState state, double distance) {
					if(((level.getBlockState(pos.down()).isAir() && !state.isAir()) || (!level.getBlockState(pos.down()).isAir() && state.isAir())) && Math.random() < 0.1f) {
						RegistryEntry<ConfiguredFeature<?, ?>> feature = null;
						if(Math.random() < 0.9f) {
							feature = entity.getLevel().getRegistryManager().get(RegistryKeys.CONFIGURED_FEATURE).entryOf(UndergroundConfiguredFeatures.DRIPSTONE_CLUSTER);
							feature.value().generate(sLevel, sLevel.getChunkManager().getChunkGenerator(), sLevel.random, pos);
						}
						else {
							feature = entity.getLevel().getRegistryManager().get(RegistryKeys.CONFIGURED_FEATURE).entryOf(UndergroundConfiguredFeatures.LARGE_DRIPSTONE);
							feature.value().generate(sLevel, sLevel.getChunkManager().getChunkGenerator(), sLevel.random, pos);
						}
					}
				}
			});
		}
	}
	
	@Override
	public Block getBlock() {
		return BlockRegistry.DRIPSTONE_TNT.get();
	}
}

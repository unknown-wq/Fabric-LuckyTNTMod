package luckytnt.tnteffects;

import org.joml.Vector3f;

import luckytnt.registry.BlockRegistry;
import luckytntlib.util.IExplosiveEntity;
import luckytntlib.util.explosions.ExplosionHelper;
import luckytntlib.util.explosions.IForEachBlockExplosionEffect;
import luckytntlib.util.explosions.ImprovedExplosion;
import luckytntlib.util.tnteffects.PrimedTNTEffect;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.UndergroundConfiguredFeatures;

public class LushTNTEffect extends PrimedTNTEffect{

	private final int radius;
	
	public LushTNTEffect(int radius) {
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
		ExplosionHelper.doSphericalExplosion(entity.getLevel(), entity.getPos(), Math.round(radius * 0.75f), new IForEachBlockExplosionEffect() {

			@Override
			public void doBlockExplosion(World level, BlockPos pos, BlockState state, double distance) {
				if(level.getBlockState(pos.down()).isAir() && !state.isAir() && state.getBlock().getBlastResistance() < 100 && !state.isIn(BlockTags.LUSH_GROUND_REPLACEABLE)) {
					state.getBlock().onDestroyedByExplosion(level, pos, ImprovedExplosion.dummyExplosion(entity.getLevel()));
					level.setBlockState(pos, Blocks.STONE.getDefaultState());
				}
				else if(level.getBlockState(pos.down()).getBlock().getBlastResistance() < 100 && !level.getBlockState(pos.down()).isAir() && state.isAir() && !level.getBlockState(pos.down()).isIn(BlockTags.LUSH_GROUND_REPLACEABLE)) {
					state.getBlock().onDestroyedByExplosion(level, pos, ImprovedExplosion.dummyExplosion(entity.getLevel()));
					level.setBlockState(pos.down(), Blocks.STONE.getDefaultState());
				}
			}
		});
		if(entity.getLevel() instanceof ServerWorld sLevel) {
			ExplosionHelper.doSphericalExplosion(sLevel, entity.getPos(), Math.round(radius * 0.75f), new IForEachBlockExplosionEffect() {

				@Override
				public void doBlockExplosion(World level, BlockPos pos, BlockState state, double distance) {
					if((level.getBlockState(pos.down()).isAir() && !state.isAir()) && Math.random() < 0.025f) {
						RegistryEntry<ConfiguredFeature<?, ?>> feature = entity.getLevel().getRegistryManager().get(RegistryKeys.CONFIGURED_FEATURE).entryOf(UndergroundConfiguredFeatures.MOSS_PATCH_CEILING);
						feature.value().generate(sLevel, sLevel.getChunkManager().getChunkGenerator(), sLevel.random, pos);
					}
					if((!level.getBlockState(pos.down()).isAir() && state.isAir()) && Math.random() < 0.1f) {
						RegistryEntry<ConfiguredFeature<?, ?>> feature = null;
						if(Math.random() < 0.5f) {
							feature = entity.getLevel().getRegistryManager().get(RegistryKeys.CONFIGURED_FEATURE).entryOf(UndergroundConfiguredFeatures.LUSH_CAVES_CLAY);
							feature.value().generate(sLevel, sLevel.getChunkManager().getChunkGenerator(), sLevel.random, pos);
						}
						else {
							feature = entity.getLevel().getRegistryManager().get(RegistryKeys.CONFIGURED_FEATURE).entryOf(UndergroundConfiguredFeatures.MOSS_PATCH);
							feature.value().generate(sLevel, sLevel.getChunkManager().getChunkGenerator(), sLevel.random, pos);
						}
					}
				}
			});
		}
	}
	
	@Override
	public void spawnParticles(IExplosiveEntity entity) {
		for(int count = 0; count <= 20; count++) {
			entity.getLevel().addParticle(new DustParticleEffect(new Vector3f(0.36f, 0.27f, 0.11f), 0.75f), entity.x() + Math.random() * 0.0625D - Math.random() * 0.0625D, entity.y() + 1D + Math.random() * 0.375D, entity.z() + Math.random() * 0.0625D - Math.random() * 0.0625D, 0, 0, 0);
		}
		for(int count = 0; count <= 60; count++) {
			entity.getLevel().addParticle(new DustParticleEffect(new Vector3f(0.44f, 0.57f, 0.18f), 0.75f), entity.x() + Math.random() * 0.75D - Math.random() * 0.75D, entity.y() + 1D + 0.375D + Math.random() * 0.625D, entity.z() + Math.random() * 0.75D - Math.random() * 0.75D, 0, 0, 0);
		}
		for(int count = 0; count <= 10; count++) {
			entity.getLevel().addParticle(new DustParticleEffect(new Vector3f(0.82f, 0.48f, 0.89f), 0.75f), entity.x() + Math.random() * 0.75D - Math.random() * 0.75D, entity.y() + 1D + 0.375D + Math.random() * 0.625D, entity.z() + Math.random() * 0.75D - Math.random() * 0.75D, 0, 0, 0);
		}
	}
	
	@Override
	public Block getBlock() {
		return BlockRegistry.LUSH_TNT.get();
	}
}

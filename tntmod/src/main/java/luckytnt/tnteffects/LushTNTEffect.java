package luckytnt.tnteffects;

import org.joml.Vector3f;

import luckytnt.registry.BlockRegistry;
import luckytntlib.util.IExplosiveEntity;
import luckytntlib.util.explosions.ExplosionHelper;
import luckytntlib.util.explosions.IForEachBlockExplosionEffect;
import luckytntlib.util.explosions.ImprovedExplosion;
import luckytntlib.util.tnteffects.PrimedTNTEffect;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.registries.Registries;
import net.minecraft.core.Holder;
import net.minecraft.tags.BlockTags;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.data.worldgen.features.CaveFeatures;

public class LushTNTEffect extends PrimedTNTEffect{

	private final int radius;
	
	public LushTNTEffect(int radius) {
		this.radius = radius;
	}
	
	@Override
	public void serverExplosion(IExplosiveEntity entity) {
		//loop invariants: the level, the shared dummy explosion and the constant block states were
		//re-resolved for every single block of every sweep before
		final Level entLevel = entity.getLevel();
		final ImprovedExplosion dummy = ImprovedExplosion.dummyExplosion(entLevel);
		final BlockState air = Blocks.AIR.defaultBlockState();
		final BlockState stone = Blocks.STONE.defaultBlockState();
		ExplosionHelper.doSphericalExplosion(entLevel, entity.getPos(), radius, new IForEachBlockExplosionEffect() {

			@Override
			public void doBlockExplosion(Level level, BlockPos pos, BlockState state, double distance) {
				//identity check against the exact state that would be written: Level#setBlock bails out
				//on an identical state and Block#wasExploded is a no-op for air, so the body is skippable.
				//deliberately not isAir(), which would also swallow the cave_air -> air rewrite the original does.
				if(state == air) {
					return;
				}
				if(state.getBlock().getExplosionResistance() < 100 && (!state.isCollisionShapeFullBlock(level, pos) || state.is(BlockTags.LEAVES) || state.is(BlockTags.LOGS))) {
					state.getBlock().wasExploded((ServerLevel)level, pos, dummy);
					level.setBlock(pos, air, 3);
				}
			}
		});
		ExplosionHelper.doSphericalExplosion(entLevel, entity.getPos(), Math.round(radius * 0.75f), new IForEachBlockExplosionEffect() {

			@Override
			public void doBlockExplosion(Level level, BlockPos pos, BlockState state, double distance) {
				//the block below used to be built and read up to four times per position
				BlockPos below = pos.below();
				BlockState belowState = level.getBlockState(below);
				boolean stateAir = state.isAir();
				boolean belowAir = belowState.isAir();
				if(!stateAir && belowAir) {
					if(state.getBlock().getExplosionResistance() < 100 && !state.is(BlockTags.LUSH_GROUND_REPLACEABLE)) {
						state.getBlock().wasExploded((ServerLevel)level, pos, dummy);
						level.setBlockAndUpdate(pos, stone);
					}
				}
				else if(stateAir && !belowAir) {
					if(belowState.getBlock().getExplosionResistance() < 100 && !belowState.is(BlockTags.LUSH_GROUND_REPLACEABLE)) {
						state.getBlock().wasExploded((ServerLevel)level, pos, dummy);
						level.setBlockAndUpdate(below, stone);
					}
				}
			}
		});
		if(entLevel instanceof ServerLevel sLevel) {
			//these three registry lookups used to run once per placed feature
			final Holder<ConfiguredFeature<?, ?>> mossCeiling = entLevel.registryAccess().lookupOrThrow(Registries.CONFIGURED_FEATURE).getOrThrow(CaveFeatures.MOSS_PATCH_CEILING);
			final Holder<ConfiguredFeature<?, ?>> clay = entLevel.registryAccess().lookupOrThrow(Registries.CONFIGURED_FEATURE).getOrThrow(CaveFeatures.LUSH_CAVES_CLAY);
			final Holder<ConfiguredFeature<?, ?>> moss = entLevel.registryAccess().lookupOrThrow(Registries.CONFIGURED_FEATURE).getOrThrow(CaveFeatures.MOSS_PATCH);
			final var generator = sLevel.getChunkSource().getGenerator();
			final var random = sLevel.getRandom();
			ExplosionHelper.doSphericalExplosion(sLevel, entity.getPos(), Math.round(radius * 0.75f), new IForEachBlockExplosionEffect() {

				@Override
				public void doBlockExplosion(Level level, BlockPos pos, BlockState state, double distance) {
					//the two branches are mutually exclusive on state.isAir(), so that free test decides
					//which one to evaluate and the block below is read once instead of twice
					if(!state.isAir()) {
						if(level.getBlockState(pos.below()).isAir() && Math.random() < 0.025f) {
							mossCeiling.value().place(sLevel, generator, random, pos);
						}
					}
					else if(!level.getBlockState(pos.below()).isAir() && Math.random() < 0.1f) {
						if(Math.random() < 0.5f) {
							clay.value().place(sLevel, generator, random, pos);
						}
						else {
							moss.value().place(sLevel, generator, random, pos);
						}
					}
				}
			});
		}
	}
	
	@Override
	public void spawnParticles(IExplosiveEntity entity) {
		for(int count = 0; count <= 20; count++) {
			entity.getLevel().addParticle(new DustParticleOptions(((int)(0.36f*255)<<16)|((int)(0.27f*255)<<8)|(int)(0.11f*255), 0.75f), entity.x() + Math.random() * 0.0625D - Math.random() * 0.0625D, entity.y() + 1D + Math.random() * 0.375D, entity.z() + Math.random() * 0.0625D - Math.random() * 0.0625D, 0, 0, 0);
		}
		for(int count = 0; count <= 60; count++) {
			entity.getLevel().addParticle(new DustParticleOptions(((int)(0.44f*255)<<16)|((int)(0.57f*255)<<8)|(int)(0.18f*255), 0.75f), entity.x() + Math.random() * 0.75D - Math.random() * 0.75D, entity.y() + 1D + 0.375D + Math.random() * 0.625D, entity.z() + Math.random() * 0.75D - Math.random() * 0.75D, 0, 0, 0);
		}
		for(int count = 0; count <= 10; count++) {
			entity.getLevel().addParticle(new DustParticleOptions(((int)(0.82f*255)<<16)|((int)(0.48f*255)<<8)|(int)(0.89f*255), 0.75f), entity.x() + Math.random() * 0.75D - Math.random() * 0.75D, entity.y() + 1D + 0.375D + Math.random() * 0.625D, entity.z() + Math.random() * 0.75D - Math.random() * 0.75D, 0, 0, 0);
		}
	}
	
	@Override
	public Block getBlock() {
		return BlockRegistry.LUSH_TNT.get();
	}
}

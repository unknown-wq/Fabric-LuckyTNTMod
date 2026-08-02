package luckytnt.tnteffects;


import org.joml.Math;
import org.joml.Vector3f;

import luckytnt.config.LuckyTNTConfigValues;
import luckytnt.event.LevelEvents;
import luckytnt.registry.BlockRegistry;
import luckytntlib.util.IExplosiveEntity;
import luckytntlib.util.explosions.ExplosionHelper;
import luckytntlib.util.explosions.IForEachBlockExplosionEffect;
import luckytntlib.util.tnteffects.PrimedTNTEffect;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.BlockTags;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.data.worldgen.features.VegetationFeatures;

public class AetherTNTEffect extends PrimedTNTEffect {

	@Override
	public void serverExplosion(IExplosiveEntity ent) {
		final Level level = ent.getLevel();
		//the island offset is a config read that was repeated for every accepted block of the r=100 sphere
		final int islandOffset = LuckyTNTConfigValues.ISLAND_HEIGHT.get() * 2;
		final double entY = ent.y();
		ExplosionHelper.doModifiedSphericalExplosion(level, ent.getPos(), 100, new Vec3(1f, 0.5f, 1f), new IForEachBlockExplosionEffect() {

			@Override
			public void doBlockExplosion(Level level, BlockPos pos, BlockState state, double distance) {
				if(!state.isAir() && state.getBlock().getExplosionResistance() <= 200 && (entY - pos.getY()) <= 35) {
					if(state.is(BlockTags.LOGS) && state.hasProperty(BlockStateProperties.AXIS)) {
						level.setBlock(pos.above(islandOffset), Blocks.DARK_OAK_LOG.defaultBlockState().setValue(BlockStateProperties.AXIS, state.getValue(BlockStateProperties.AXIS)), 3);
					} else if(state.is(BlockTags.LEAVES)) {
						if(Math.random() < 0.9D) {
							level.setBlock(pos.above(islandOffset), Blocks.AZALEA_LEAVES.defaultBlockState(), 3);
						} else {
							level.setBlock(pos.above(islandOffset), Blocks.FLOWERING_AZALEA_LEAVES.defaultBlockState(), 3);
						}
					} else {
						level.setBlock(pos.above(islandOffset), state, 3);
					}
				}
			}
		});

		//Loop invariants that used to sit in the inner loop of a 201x201 sweep: the CONFIGURED_FEATURE
		//registry lookup alone ran 31417 times (once per in-circle column) for a value that never changes,
		//and so did the chunk generator lookup and a fresh RandomSource per placement.
		final ServerLevel sLevel = (ServerLevel) level;
		final Registry<ConfiguredFeature<?, ?>> features = level.registryAccess().lookupOrThrow(Registries.CONFIGURED_FEATURE);
		final ChunkGenerator generator = sLevel.getChunkSource().getGenerator();
		final RandomSource random = sLevel.getRandom();
		final ConfiguredFeature<?, ?> forestFlowers = features.getValue(VegetationFeatures.FOREST_FLOWERS);
		final ConfiguredFeature<?, ?> flowerForestFlowers = features.getValue(VegetationFeatures.FLOWER_FLOWER_FOREST);
		final int entX = Mth.floor(ent.x());
		//only used as the y of the isLoaded probe, which must stay inside the build height
		final int entYFloor = Mth.floor(ent.y());
		final int entZ = Mth.floor(ent.z());
		final BlockPos.MutableBlockPos mutable = new BlockPos.MutableBlockPos();
		for(int offX = -100; offX <= 100; offX++) {
			final int xSqr = offX * offX;
			for(int offZ = -100; offZ <= 100; offZ++) {
				//squared compare instead of a Math.sqrt per column
				if(xSqr + offZ * offZ > 10000) {
					continue;
				}
				//the 2.5% roll now runs *before* the top block lookup instead of after it. Only ~785 of the
				//31417 in-circle columns still pay for a full Y column scan: ~16M getBlockState -> ~400k.
				double roll = Math.random();
				ConfiguredFeature<?, ?> feature;
				if(roll > 0.1D && roll <= 0.1125D) {
					feature = forestFlowers;
				} else if(roll > 0.15D && roll <= 0.1625D) {
					feature = flowerForestFlowers;
				} else {
					continue;
				}
				mutable.set(entX + offX, entYFloor, entZ + offZ);
				//never force generate a chunk 100 blocks out just to read its surface
				if(!sLevel.isLoaded(mutable)) {
					continue;
				}
				double x = ent.x() + offX;
				double z = ent.z() + offZ;
				mutable.setY(LevelEvents.getTopBlock(level, x, z, true) + 1);
				//place() hands the position on to feature placement, so it must not be the reused mutable
				feature.place(sLevel, generator, random, mutable.immutable());
			}
		}
	}
	
	@Override
	public void spawnParticles(IExplosiveEntity ent) {
		if(ent.getTNTFuse() % 3 == 0) {
			for(double d = 0D; d <= 1.5D; d += 0.1D) {
				ent.getLevel().addParticle(new DustParticleOptions(((int)(0.97f*255)<<16)|((int)(0.84f*255)<<8)|(int)(0.45f*255), 0.75f), ent.x() + 0.5D, ent.y() + 1.1D + d, ent.z(), 0, 0, 0);
				ent.getLevel().addParticle(new DustParticleOptions(((int)(0.97f*255)<<16)|((int)(0.84f*255)<<8)|(int)(0.45f*255), 0.75f), ent.x() + 0.4D, ent.y() + 1.1D + d, ent.z(), 0, 0, 0);
				ent.getLevel().addParticle(new DustParticleOptions(((int)(0.97f*255)<<16)|((int)(0.84f*255)<<8)|(int)(0.45f*255), 0.75f), ent.x() - 0.5D, ent.y() + 1.1D + d, ent.z(), 0, 0, 0);
				ent.getLevel().addParticle(new DustParticleOptions(((int)(0.97f*255)<<16)|((int)(0.84f*255)<<8)|(int)(0.45f*255), 0.75f), ent.x() - 0.4D, ent.y() + 1.1D + d, ent.z(), 0, 0, 0);
			}
			for(double d = 0D; d <= 1D; d += 0.1D) {
				ent.getLevel().addParticle(new DustParticleOptions(((int)(0.97f*255)<<16)|((int)(0.84f*255)<<8)|(int)(0.45f*255), 0.75f), ent.x() + 0.5D - d, ent.y() + 1.1D, ent.z(), 0, 0, 0);
				ent.getLevel().addParticle(new DustParticleOptions(((int)(0.97f*255)<<16)|((int)(0.84f*255)<<8)|(int)(0.45f*255), 0.75f), ent.x() + 0.5D - d, ent.y() + 1.2D, ent.z(), 0, 0, 0);
				ent.getLevel().addParticle(new DustParticleOptions(((int)(0.97f*255)<<16)|((int)(0.84f*255)<<8)|(int)(0.45f*255), 0.75f), ent.x() + 0.5D - d, ent.y() + 2.6D, ent.z(), 0, 0, 0);
				ent.getLevel().addParticle(new DustParticleOptions(((int)(0.97f*255)<<16)|((int)(0.84f*255)<<8)|(int)(0.45f*255), 0.75f), ent.x() + 0.5D - d, ent.y() + 2.5D, ent.z(), 0, 0, 0);
			}
			for(double x = -0.3D; x <= 0.3D; x += 0.1D) {
				for(double y = 0.2D; y <= 1.3D; y += 0.1D) {
					ent.getLevel().addParticle(new DustParticleOptions(((int)(0.31f*255)<<16)|((int)(0.46f*255)<<8)|(int)(0.86f*255), 0.75f), ent.x() + x + 0.05D, ent.y() + 1.1D + y, ent.z(), 0, 0, 0);
				}
			}
		}
	}
	
	@Override
	public Block getBlock() {
		return BlockRegistry.AETHER_TNT.get();
	}
	
	@Override
	public int getDefaultFuse(IExplosiveEntity ent) {
		return 200;
	}
}

package luckytnt.tnteffects;

import java.util.List;
import java.util.Optional;
import java.util.Random;

import org.joml.Vector3f;

import luckytnt.registry.BlockRegistry;
import luckytnt.util.Materials;
import luckytntlib.util.IExplosiveEntity;
import luckytntlib.util.explosions.ExplosionHelper;
import luckytntlib.util.explosions.IForEachBlockExplosionEffect;
import luckytntlib.util.explosions.ImprovedExplosion;
import luckytntlib.util.tnteffects.PrimedTNTEffect;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BubbleColumnBlock;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.structure.NetherFossilGenerator;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.phys.Vec3;
import net.minecraft.util.math.intprovider.UniformIntProvider;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.gen.YOffset;
import net.minecraft.world.gen.blockpredicate.BlockPredicate;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.DiskFeatureConfig;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.gen.feature.NetherConfiguredFeatures;
import net.minecraft.world.gen.feature.TreeConfiguredFeatures;
import net.minecraft.world.gen.heightprovider.ConstantHeightProvider;
import net.minecraft.world.level.levelgen.heightproviders.HeightProvider;
import net.minecraft.world.gen.stateprovider.PredicatedStateProvider;
import net.minecraft.world.level.levelgen.structure.structures.NetherFossilStructure;
import net.minecraft.world.level.levelgen.structure.Structure;

public class NetherTNTEffect extends PrimedTNTEffect {
	public List<Block> list = List.of(Blocks.NETHERRACK, Blocks.CRIMSON_NYLIUM, Blocks.WARPED_NYLIUM, Blocks.SOUL_SAND, Blocks.SOUL_SOIL);
	
	@Override
	public void serverExplosion(IExplosiveEntity ent) {
		ExplosionHelper.doSphericalExplosion(ent.getLevel(), new Vec3(ent.x(), 0, ent.z()), 40, new IForEachBlockExplosionEffect() {
			
			@Override
			public void doBlockExplosion(Level level, BlockPos pos, BlockState state, double distance) {
				if(state.getBlock().getExplosionResistance() <= 200) {
					level.setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
				}
			}
		});
		
		ImprovedExplosion explosion = new ImprovedExplosion(ent.getLevel(), new Vec3(ent.x(), 0, ent.z()), 100);
		explosion.doBlockExplosion(1f, 0.8f, 1f, 0.2f, false, true);
		
		ExplosionHelper.doSphericalExplosion(ent.getLevel(), new Vec3(ent.x(), 0, ent.z()), 100, new IForEachBlockExplosionEffect() {
			
			@Override
			public void doBlockExplosion(Level level, BlockPos pos, BlockState state, double distance) {
				if((state.getBlock() instanceof LiquidBlock || state.getBlock() instanceof BubbleColumnBlock || Materials.isWaterPlant(state)) && pos.getY() <= 50) {
					level.setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
				}
				
				if(state.contains(BlockStateProperties.WATERLOGGED) && state.get(BlockStateProperties.WATERLOGGED) && pos.getY() <= 50) {
					level.setBlock(pos, state.setValue(BlockStateProperties.WATERLOGGED, false), 3);
				}
			}
		});
		
		ImprovedExplosion explosion3 = new ImprovedExplosion(ent.getLevel(), new Vec3(ent.x(), 0, ent.z()), 80);
		explosion3.doBlockExplosion(1f, 0.8f, 1f, 0.2f, true, new IForEachBlockExplosionEffect() {
			
			@Override
			public void doBlockExplosion(Level level, BlockPos pos, BlockState state, double distance) {
				if(state.getBlock().getExplosionResistance() <= 200 && !state.getCollisionShape(level, pos, CollisionContext.absent()).isEmpty()) {
					level.setBlock(pos, Blocks.NETHERRACK.defaultBlockState(), 3);
				}
			}
		});
		
		ExplosionHelper.doCylindricalExplosion(ent.getLevel(), new Vec3(ent.x(), -40, ent.z()), 40, 20, new IForEachBlockExplosionEffect() {
			
			@Override
			public void doBlockExplosion(Level level, BlockPos pos, BlockState state, double distance) {
				if(pos.getY() <= -44 && state.isAir()) {
					level.setBlock(pos, Blocks.LAVA.defaultBlockState(), 3);
				}
			}
		});
		
		int biome = new Random().nextInt(3);
		
		ImprovedExplosion explosion4 = new ImprovedExplosion(ent.getLevel(), new Vec3(ent.x(), 0, ent.z()), 80);
		explosion4.doBlockExplosion(1f, 0.8f, 1f, 0.2f, true, new IForEachBlockExplosionEffect() {
			
			@Override
			public void doBlockExplosion(Level level, BlockPos pos, BlockState state, double distance) {
				BlockPos posAbove = pos.above();
				BlockState stateAbove = level.getBlockState(posAbove);
				
				if(stateAbove.isAir() && state.getBlock() == Blocks.NETHERRACK && pos.getY() <= -10) {
					if(biome == 0) {
						level.setBlock(pos, Blocks.CRIMSON_NYLIUM.defaultBlockState(), 3);
					}
					
					if(biome == 1) {
						level.setBlock(pos, Blocks.WARPED_NYLIUM.defaultBlockState(), 3);
					}
					
					if(biome == 2) {
						level.setBlock(pos, Blocks.SOUL_SAND.defaultBlockState(), 3);
					}
				}
			}
		});
		
		ImprovedExplosion explosion5 = new ImprovedExplosion(ent.getLevel(), new Vec3(ent.x(), 0, ent.z()), 150);
		explosion5.doBlockExplosion(1f, 0.8f, 1f, 0.2f, true, new IForEachBlockExplosionEffect() {
			
			@Override
			public void doBlockExplosion(Level level, BlockPos pos, BlockState state, double distance) {
				BlockPos posBelow = pos.below();
				BlockState stateBelow = level.getBlockState(posBelow);
				BlockPos posAbove = pos.above();
				BlockState stateAbove = level.getBlockState(posAbove);
				
				if(list.contains(state.getBlock()) && stateAbove.isAir() && pos.getY() <= -10) {
					Registry<ConfiguredFeature<?, ?>> registry = ent.getLevel().registryAccess().get(Registries.CONFIGURED_FEATURE);
					
					if(biome == 0) {
						if(Math.random() < 0.02D) {
							registry.get(NetherConfiguredFeatures.PATCH_CRIMSON_ROOTS).generate((WorldGenLevel)ent.getLevel(), ((ServerLevel)ent.getLevel()).getChunkSource().getChunkGenerator(), ent.getLevel().getRandom(), posAbove);
						}
						if(Math.random() < 0.04D) {
							registry.get(TreeConfiguredFeatures.CRIMSON_FUNGUS).generate((WorldGenLevel)ent.getLevel(), ((ServerLevel)ent.getLevel()).getChunkSource().getChunkGenerator(), ent.getLevel().getRandom(), posAbove);
						}
						if(Math.random() < 0.02D) {
							registry.get(NetherConfiguredFeatures.CRIMSON_FOREST_VEGETATION_BONEMEAL).generate((WorldGenLevel)ent.getLevel(), ((ServerLevel)ent.getLevel()).getChunkSource().getChunkGenerator(), ent.getLevel().getRandom(), posAbove);
						}
					}
					
					if(biome == 1) {
						if(Math.random() < 0.02D) {
							registry.get(NetherConfiguredFeatures.NETHER_SPROUTS_BONEMEAL).generate((WorldGenLevel)ent.getLevel(), ((ServerLevel)ent.getLevel()).getChunkSource().getChunkGenerator(), ent.getLevel().getRandom(), posAbove);
						}
						if(Math.random() < 0.01D) {
							registry.get(NetherConfiguredFeatures.TWISTING_VINES_BONEMEAL).generate((WorldGenLevel)ent.getLevel(), ((ServerLevel)ent.getLevel()).getChunkSource().getChunkGenerator(), ent.getLevel().getRandom(), posAbove);
						}
						if(Math.random() < 0.04D) {
							registry.get(TreeConfiguredFeatures.WARPED_FUNGUS).generate((WorldGenLevel)ent.getLevel(), ((ServerLevel)ent.getLevel()).getChunkSource().getChunkGenerator(), ent.getLevel().getRandom(), posAbove);
						}
						if(Math.random() < 0.02D) {
							registry.get(NetherConfiguredFeatures.WARPED_FOREST_VEGETATION_BONEMEAL).generate((WorldGenLevel)ent.getLevel(), ((ServerLevel)ent.getLevel()).getChunkSource().getChunkGenerator(), ent.getLevel().getRandom(), posAbove);
						}
					}
					
					if(biome == 2) {
						if(Math.random() < 0.025D) {
							DiskFeatureConfig config = new DiskFeatureConfig(PredicatedStateProvider.of(Blocks.SOUL_SOIL), BlockPredicate.matchingBlocks(List.of(Blocks.NETHERRACK, Blocks.SOUL_SAND, Blocks.SOUL_SOIL)), UniformIntProvider.create(3, 6), 2);
							Feature.DISK.generateIfValid(config, (WorldGenLevel)ent.getLevel(), ((ServerLevel)ent.getLevel()).getChunkSource().getChunkGenerator(), ent.getLevel().getRandom(), pos);
						}
						if(Math.random() < 0.01D) {
							registry.get(NetherConfiguredFeatures.PATCH_SOUL_FIRE).generate((WorldGenLevel)ent.getLevel(), ((ServerLevel)ent.getLevel()).getChunkSource().getChunkGenerator(), ent.getLevel().getRandom(), posAbove);
						}
						if(Math.random() < 0.001D) {
							Structure structure = new NetherFossil(null, ConstantHeightProvider.create(YOffset.fixed(pos.getY())), level);
							StructureStart start = structure.createStructureStart(ent.getLevel().registryAccess(), ((ServerLevel)ent.getLevel()).getChunkSource().getChunkGenerator(), ((ServerLevel)ent.getLevel()).getChunkSource().getChunkGenerator().getBiomeSource(), ((ServerLevel)ent.getLevel()).getChunkSource().getNoiseConfig(), ((ServerLevel)ent.getLevel()).getStructureManager(), ((ServerLevel)ent.getLevel()).getSeed(), new ChunkPos(posAbove), 20, level, holder -> true);
							start.place((WorldGenLevel)ent.getLevel(), ((ServerLevel)ent.getLevel()).getStructureAccessor(), ((ServerLevel)ent.getLevel()).getChunkSource().getChunkGenerator(), ent.getLevel().getRandom(), new BlockBox(pos.getX() - 150, pos.getY() - 150, pos.getZ() - 150, pos.getX() + 150, pos.getY() + 150, pos.getZ() + 150), new ChunkPos(posAbove));
						}
					}
				}
				
				if(stateBelow.isAir() && state.getBlock() == Blocks.NETHERRACK && pos.getY() >= 10 && Math.random() < 0.005D) {
					Registry<ConfiguredFeature<?, ?>> registry = ent.getLevel().registryAccess().get(Registries.CONFIGURED_FEATURE);
					registry.get(NetherConfiguredFeatures.GLOWSTONE_EXTRA).generate((WorldGenLevel)ent.getLevel(), ((ServerLevel)ent.getLevel()).getChunkSource().getChunkGenerator(), ent.getLevel().getRandom(), posBelow);
				}
			}
		});
	}
	
	@Override
	public void spawnParticles(IExplosiveEntity ent) {
		if(ent.getTNTFuse() % 3 == 0) {
			for(double d = 0D; d <= 1.5D; d += 0.1D) {
				ent.getLevel().addParticle(new DustParticleOptions(((int)(0f*255)<<16)|((int)(0f*255)<<8)|(int)(0f*255), 0.75f), ent.x() + 0.5D, ent.y() + 1.1D + d, ent.z(), 0, 0, 0);
				ent.getLevel().addParticle(new DustParticleOptions(((int)(0f*255)<<16)|((int)(0f*255)<<8)|(int)(0f*255), 0.75f), ent.x() + 0.4D, ent.y() + 1.1D + d, ent.z(), 0, 0, 0);
				ent.getLevel().addParticle(new DustParticleOptions(((int)(0f*255)<<16)|((int)(0f*255)<<8)|(int)(0f*255), 0.75f), ent.x() - 0.5D, ent.y() + 1.1D + d, ent.z(), 0, 0, 0);
				ent.getLevel().addParticle(new DustParticleOptions(((int)(0f*255)<<16)|((int)(0f*255)<<8)|(int)(0f*255), 0.75f), ent.x() - 0.4D, ent.y() + 1.1D + d, ent.z(), 0, 0, 0);
			}
			for(double d = 0D; d <= 1D; d += 0.1D) {
				ent.getLevel().addParticle(new DustParticleOptions(((int)(0f*255)<<16)|((int)(0f*255)<<8)|(int)(0f*255), 0.75f), ent.x() + 0.5D - d, ent.y() + 1.1D, ent.z(), 0, 0, 0);
				ent.getLevel().addParticle(new DustParticleOptions(((int)(0f*255)<<16)|((int)(0f*255)<<8)|(int)(0f*255), 0.75f), ent.x() + 0.5D - d, ent.y() + 1.2D, ent.z(), 0, 0, 0);
				ent.getLevel().addParticle(new DustParticleOptions(((int)(0f*255)<<16)|((int)(0f*255)<<8)|(int)(0f*255), 0.75f), ent.x() + 0.5D - d, ent.y() + 2.6D, ent.z(), 0, 0, 0);
				ent.getLevel().addParticle(new DustParticleOptions(((int)(0f*255)<<16)|((int)(0f*255)<<8)|(int)(0f*255), 0.75f), ent.x() + 0.5D - d, ent.y() + 2.5D, ent.z(), 0, 0, 0);
			}
			for(double x = -0.3D; x <= 0.3D; x += 0.1D) {
				for(double y = 0.2D; y <= 1.3D; y += 0.1D) {
					ent.getLevel().addParticle(new DustParticleOptions(((int)(0.5f*255)<<16)|((int)(0f*255)<<8)|(int)(1f*255), 0.75f), ent.x() + x + 0.05D, ent.y() + 1.1D + y, ent.z(), 0, 0, 0);
				}
			}
		}
	} 
	
	@Override
	public Block getBlock() {
		return BlockRegistry.NETHER_TNT.get();
	}
	
	@Override
	public int getDefaultFuse(IExplosiveEntity ent) {
		return 180;
	}
	
	public class NetherFossil extends NetherFossilStructure {
		private final Level level;
		
		public NetherFossil(Structure.Config config, HeightProvider height, Level level) {
			super(config, height);
			this.level = level;
		}

		public Optional<Structure.StructurePosition> getStructurePosition(Structure.Context ctx) {
			ChunkRandom worldgenrandom = ctx.random();
			int i = ctx.chunkPos().getStartX() + worldgenrandom.nextInt(16);
			int j = ctx.chunkPos().getStartZ() + worldgenrandom.nextInt(16);
			int k = -64;
			int l = 0;
			
			while (l > k) {
				BlockState blockstate = level.getBlockState(new BlockPos(i, l, j));
				--l;
				BlockState blockstate1 = level.getBlockState(new BlockPos(i, l, j));
				if (blockstate.isAir() && (blockstate1.is(Blocks.SOUL_SAND) || blockstate1.is(Blocks.SOUL_SOIL) || blockstate1.is(Blocks.NETHERRACK))) {
					break;
				}
			}

			if (l <= k) {
				return Optional.empty();
			} else {
				BlockPos blockpos = new BlockPos(i, l, j);
				return Optional.of(new Structure.StructurePosition(blockpos, (accessor) -> {
					NetherFossilGenerator.addPieces(ctx.structureTemplateManager(), accessor, worldgenrandom, blockpos);
				}));
			}
		}
	}
}

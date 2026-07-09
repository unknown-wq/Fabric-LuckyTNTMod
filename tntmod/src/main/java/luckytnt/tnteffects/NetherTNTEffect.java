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
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.BubbleColumnBlock;
import net.minecraft.block.FluidBlock;
import net.minecraft.block.ShapeContext;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.property.Properties;
import net.minecraft.structure.NetherFossilGenerator;
import net.minecraft.structure.StructureStart;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.intprovider.UniformIntProvider;
import net.minecraft.util.math.random.ChunkRandom;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.World;
import net.minecraft.world.gen.YOffset;
import net.minecraft.world.gen.blockpredicate.BlockPredicate;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.DiskFeatureConfig;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.NetherConfiguredFeatures;
import net.minecraft.world.gen.feature.TreeConfiguredFeatures;
import net.minecraft.world.gen.heightprovider.ConstantHeightProvider;
import net.minecraft.world.gen.heightprovider.HeightProvider;
import net.minecraft.world.gen.stateprovider.PredicatedStateProvider;
import net.minecraft.world.gen.structure.NetherFossilStructure;
import net.minecraft.world.gen.structure.Structure;

public class NetherTNTEffect extends PrimedTNTEffect {
	public List<Block> list = List.of(Blocks.NETHERRACK, Blocks.CRIMSON_NYLIUM, Blocks.WARPED_NYLIUM, Blocks.SOUL_SAND, Blocks.SOUL_SOIL);
	
	@Override
	public void serverExplosion(IExplosiveEntity ent) {
		ExplosionHelper.doSphericalExplosion(ent.getLevel(), new Vec3d(ent.x(), 0, ent.z()), 40, new IForEachBlockExplosionEffect() {
			
			@Override
			public void doBlockExplosion(World level, BlockPos pos, BlockState state, double distance) {
				if(state.getBlock().getBlastResistance() <= 200) {
					level.setBlockState(pos, Blocks.AIR.getDefaultState(), 3);
				}
			}
		});
		
		ImprovedExplosion explosion = new ImprovedExplosion(ent.getLevel(), new Vec3d(ent.x(), 0, ent.z()), 100);
		explosion.doBlockExplosion(1f, 0.8f, 1f, 0.2f, false, true);
		
		ExplosionHelper.doSphericalExplosion(ent.getLevel(), new Vec3d(ent.x(), 0, ent.z()), 100, new IForEachBlockExplosionEffect() {
			
			@Override
			public void doBlockExplosion(World level, BlockPos pos, BlockState state, double distance) {
				if((state.getBlock() instanceof FluidBlock || state.getBlock() instanceof BubbleColumnBlock || Materials.isWaterPlant(state)) && pos.getY() <= 50) {
					level.setBlockState(pos, Blocks.AIR.getDefaultState(), 3);
				}
				
				if(state.contains(Properties.WATERLOGGED) && state.get(Properties.WATERLOGGED) && pos.getY() <= 50) {
					level.setBlockState(pos, state.with(Properties.WATERLOGGED, false), 3);
				}
			}
		});
		
		ImprovedExplosion explosion3 = new ImprovedExplosion(ent.getLevel(), new Vec3d(ent.x(), 0, ent.z()), 80);
		explosion3.doBlockExplosion(1f, 0.8f, 1f, 0.2f, true, new IForEachBlockExplosionEffect() {
			
			@Override
			public void doBlockExplosion(World level, BlockPos pos, BlockState state, double distance) {
				if(state.getBlock().getBlastResistance() <= 200 && !state.getCollisionShape(level, pos, ShapeContext.absent()).isEmpty()) {
					level.setBlockState(pos, Blocks.NETHERRACK.getDefaultState(), 3);
				}
			}
		});
		
		ExplosionHelper.doCylindricalExplosion(ent.getLevel(), new Vec3d(ent.x(), -40, ent.z()), 40, 20, new IForEachBlockExplosionEffect() {
			
			@Override
			public void doBlockExplosion(World level, BlockPos pos, BlockState state, double distance) {
				if(pos.getY() <= -44 && state.isAir()) {
					level.setBlockState(pos, Blocks.LAVA.getDefaultState(), 3);
				}
			}
		});
		
		int biome = new Random().nextInt(3);
		
		ImprovedExplosion explosion4 = new ImprovedExplosion(ent.getLevel(), new Vec3d(ent.x(), 0, ent.z()), 80);
		explosion4.doBlockExplosion(1f, 0.8f, 1f, 0.2f, true, new IForEachBlockExplosionEffect() {
			
			@Override
			public void doBlockExplosion(World level, BlockPos pos, BlockState state, double distance) {
				BlockPos posAbove = pos.up();
				BlockState stateAbove = level.getBlockState(posAbove);
				
				if(stateAbove.isAir() && state.getBlock() == Blocks.NETHERRACK && pos.getY() <= -10) {
					if(biome == 0) {
						level.setBlockState(pos, Blocks.CRIMSON_NYLIUM.getDefaultState(), 3);
					}
					
					if(biome == 1) {
						level.setBlockState(pos, Blocks.WARPED_NYLIUM.getDefaultState(), 3);
					}
					
					if(biome == 2) {
						level.setBlockState(pos, Blocks.SOUL_SAND.getDefaultState(), 3);
					}
				}
			}
		});
		
		ImprovedExplosion explosion5 = new ImprovedExplosion(ent.getLevel(), new Vec3d(ent.x(), 0, ent.z()), 150);
		explosion5.doBlockExplosion(1f, 0.8f, 1f, 0.2f, true, new IForEachBlockExplosionEffect() {
			
			@Override
			public void doBlockExplosion(World level, BlockPos pos, BlockState state, double distance) {
				BlockPos posBelow = pos.down();
				BlockState stateBelow = level.getBlockState(posBelow);
				BlockPos posAbove = pos.up();
				BlockState stateAbove = level.getBlockState(posAbove);
				
				if(list.contains(state.getBlock()) && stateAbove.isAir() && pos.getY() <= -10) {
					Registry<ConfiguredFeature<?, ?>> registry = ent.getLevel().getRegistryManager().get(RegistryKeys.CONFIGURED_FEATURE);
					
					if(biome == 0) {
						if(Math.random() < 0.02D) {
							registry.get(NetherConfiguredFeatures.PATCH_CRIMSON_ROOTS).generate((StructureWorldAccess)ent.getLevel(), ((ServerWorld)ent.getLevel()).getChunkManager().getChunkGenerator(), ent.getLevel().getRandom(), posAbove);
						}
						if(Math.random() < 0.04D) {
							registry.get(TreeConfiguredFeatures.CRIMSON_FUNGUS).generate((StructureWorldAccess)ent.getLevel(), ((ServerWorld)ent.getLevel()).getChunkManager().getChunkGenerator(), ent.getLevel().getRandom(), posAbove);
						}
						if(Math.random() < 0.02D) {
							registry.get(NetherConfiguredFeatures.CRIMSON_FOREST_VEGETATION_BONEMEAL).generate((StructureWorldAccess)ent.getLevel(), ((ServerWorld)ent.getLevel()).getChunkManager().getChunkGenerator(), ent.getLevel().getRandom(), posAbove);
						}
					}
					
					if(biome == 1) {
						if(Math.random() < 0.02D) {
							registry.get(NetherConfiguredFeatures.NETHER_SPROUTS_BONEMEAL).generate((StructureWorldAccess)ent.getLevel(), ((ServerWorld)ent.getLevel()).getChunkManager().getChunkGenerator(), ent.getLevel().getRandom(), posAbove);
						}
						if(Math.random() < 0.01D) {
							registry.get(NetherConfiguredFeatures.TWISTING_VINES_BONEMEAL).generate((StructureWorldAccess)ent.getLevel(), ((ServerWorld)ent.getLevel()).getChunkManager().getChunkGenerator(), ent.getLevel().getRandom(), posAbove);
						}
						if(Math.random() < 0.04D) {
							registry.get(TreeConfiguredFeatures.WARPED_FUNGUS).generate((StructureWorldAccess)ent.getLevel(), ((ServerWorld)ent.getLevel()).getChunkManager().getChunkGenerator(), ent.getLevel().getRandom(), posAbove);
						}
						if(Math.random() < 0.02D) {
							registry.get(NetherConfiguredFeatures.WARPED_FOREST_VEGETATION_BONEMEAL).generate((StructureWorldAccess)ent.getLevel(), ((ServerWorld)ent.getLevel()).getChunkManager().getChunkGenerator(), ent.getLevel().getRandom(), posAbove);
						}
					}
					
					if(biome == 2) {
						if(Math.random() < 0.025D) {
							DiskFeatureConfig config = new DiskFeatureConfig(PredicatedStateProvider.of(Blocks.SOUL_SOIL), BlockPredicate.matchingBlocks(List.of(Blocks.NETHERRACK, Blocks.SOUL_SAND, Blocks.SOUL_SOIL)), UniformIntProvider.create(3, 6), 2);
							Feature.DISK.generateIfValid(config, (StructureWorldAccess)ent.getLevel(), ((ServerWorld)ent.getLevel()).getChunkManager().getChunkGenerator(), ent.getLevel().getRandom(), pos);
						}
						if(Math.random() < 0.01D) {
							registry.get(NetherConfiguredFeatures.PATCH_SOUL_FIRE).generate((StructureWorldAccess)ent.getLevel(), ((ServerWorld)ent.getLevel()).getChunkManager().getChunkGenerator(), ent.getLevel().getRandom(), posAbove);
						}
						if(Math.random() < 0.001D) {
							Structure structure = new NetherFossil(null, ConstantHeightProvider.create(YOffset.fixed(pos.getY())), level);
							StructureStart start = structure.createStructureStart(ent.getLevel().getRegistryManager(), ((ServerWorld)ent.getLevel()).getChunkManager().getChunkGenerator(), ((ServerWorld)ent.getLevel()).getChunkManager().getChunkGenerator().getBiomeSource(), ((ServerWorld)ent.getLevel()).getChunkManager().getNoiseConfig(), ((ServerWorld)ent.getLevel()).getStructureTemplateManager(), ((ServerWorld)ent.getLevel()).getSeed(), new ChunkPos(posAbove), 20, level, holder -> true);
							start.place((StructureWorldAccess)ent.getLevel(), ((ServerWorld)ent.getLevel()).getStructureAccessor(), ((ServerWorld)ent.getLevel()).getChunkManager().getChunkGenerator(), ent.getLevel().getRandom(), new BlockBox(pos.getX() - 150, pos.getY() - 150, pos.getZ() - 150, pos.getX() + 150, pos.getY() + 150, pos.getZ() + 150), new ChunkPos(posAbove));
						}
					}
				}
				
				if(stateBelow.isAir() && state.getBlock() == Blocks.NETHERRACK && pos.getY() >= 10 && Math.random() < 0.005D) {
					Registry<ConfiguredFeature<?, ?>> registry = ent.getLevel().getRegistryManager().get(RegistryKeys.CONFIGURED_FEATURE);
					registry.get(NetherConfiguredFeatures.GLOWSTONE_EXTRA).generate((StructureWorldAccess)ent.getLevel(), ((ServerWorld)ent.getLevel()).getChunkManager().getChunkGenerator(), ent.getLevel().getRandom(), posBelow);
				}
			}
		});
	}
	
	@Override
	public void spawnParticles(IExplosiveEntity ent) {
		if(ent.getTNTFuse() % 3 == 0) {
			for(double d = 0D; d <= 1.5D; d += 0.1D) {
				ent.getLevel().addParticle(new DustParticleEffect(new Vector3f(0f, 0f, 0f), 0.75f), ent.x() + 0.5D, ent.y() + 1.1D + d, ent.z(), 0, 0, 0);
				ent.getLevel().addParticle(new DustParticleEffect(new Vector3f(0f, 0f, 0f), 0.75f), ent.x() + 0.4D, ent.y() + 1.1D + d, ent.z(), 0, 0, 0);
				ent.getLevel().addParticle(new DustParticleEffect(new Vector3f(0f, 0f, 0f), 0.75f), ent.x() - 0.5D, ent.y() + 1.1D + d, ent.z(), 0, 0, 0);
				ent.getLevel().addParticle(new DustParticleEffect(new Vector3f(0f, 0f, 0f), 0.75f), ent.x() - 0.4D, ent.y() + 1.1D + d, ent.z(), 0, 0, 0);
			}
			for(double d = 0D; d <= 1D; d += 0.1D) {
				ent.getLevel().addParticle(new DustParticleEffect(new Vector3f(0f, 0f, 0f), 0.75f), ent.x() + 0.5D - d, ent.y() + 1.1D, ent.z(), 0, 0, 0);
				ent.getLevel().addParticle(new DustParticleEffect(new Vector3f(0f, 0f, 0f), 0.75f), ent.x() + 0.5D - d, ent.y() + 1.2D, ent.z(), 0, 0, 0);
				ent.getLevel().addParticle(new DustParticleEffect(new Vector3f(0f, 0f, 0f), 0.75f), ent.x() + 0.5D - d, ent.y() + 2.6D, ent.z(), 0, 0, 0);
				ent.getLevel().addParticle(new DustParticleEffect(new Vector3f(0f, 0f, 0f), 0.75f), ent.x() + 0.5D - d, ent.y() + 2.5D, ent.z(), 0, 0, 0);
			}
			for(double x = -0.3D; x <= 0.3D; x += 0.1D) {
				for(double y = 0.2D; y <= 1.3D; y += 0.1D) {
					ent.getLevel().addParticle(new DustParticleEffect(new Vector3f(0.5f, 0f, 1f), 0.75f), ent.x() + x + 0.05D, ent.y() + 1.1D + y, ent.z(), 0, 0, 0);
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
		private final World level;
		
		public NetherFossil(Structure.Config config, HeightProvider height, World level) {
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
				if (blockstate.isAir() && (blockstate1.isOf(Blocks.SOUL_SAND) || blockstate1.isOf(Blocks.SOUL_SOIL) || blockstate1.isOf(Blocks.NETHERRACK))) {
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

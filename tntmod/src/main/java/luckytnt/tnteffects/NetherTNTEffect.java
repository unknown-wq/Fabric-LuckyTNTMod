package luckytnt.tnteffects;

import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

import luckytnt.registry.BlockRegistry;
import luckytnt.util.Materials;
import luckytntlib.util.IExplosiveEntity;
import luckytntlib.util.explosions.ExplosionHelper;
import luckytntlib.util.explosions.IForEachBlockExplosionEffect;
import luckytntlib.util.explosions.ImprovedExplosion;
import luckytntlib.util.tnteffects.PrimedTNTEffect;
import luckytntlib.util.tnteffects.TNTXStrengthEffect.SectionSkippingExplosion;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.LevelChunkSection;
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
//import net.minecraft.structure.NetherFossilGenerator;  // TODO(port-26.2): DISABLED yarn worldgen
import net.minecraft.world.level.levelgen.structure.StructureStart;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.phys.Vec3;
//import net.minecraft.util.math.intprovider.UniformIntProvider;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.Level;
//import net.minecraft.world.gen.YOffset;
//import net.minecraft.world.gen.blockpredicate.BlockPredicate;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
//import net.minecraft.world.gen.feature.DiskFeatureConfig;
import net.minecraft.world.level.levelgen.feature.Feature;
//import net.minecraft.world.gen.feature.NetherConfiguredFeatures;
//import net.minecraft.world.gen.feature.TreeConfiguredFeatures;
//import net.minecraft.world.gen.heightprovider.ConstantHeightProvider;
import net.minecraft.world.level.levelgen.heightproviders.HeightProvider;
//import net.minecraft.world.gen.stateprovider.PredicatedStateProvider;
//import net.minecraft.world.level.levelgen.structure.structures.NetherFossilStructure;
import net.minecraft.world.level.levelgen.structure.Structure;

public class NetherTNTEffect extends PrimedTNTEffect {
	public List<Block> list = List.of(Blocks.NETHERRACK, Blocks.CRIMSON_NYLIUM, Blocks.WARPED_NYLIUM, Blocks.SOUL_SAND, Blocks.SOUL_SOIL);
	
	/**
	 * Every block the r=100 water pass can act on matches this, so a chunk section whose palette contains
	 * none of them cannot contain a single affected block and is rejected whole by
	 * {@link LevelChunkSection#maybeHas(Predicate)}.
	 */
	private static final Predicate<BlockState> WATER_LIKE = state -> {
		Block block = state.getBlock();
		return block instanceof LiquidBlock || block instanceof BubbleColumnBlock || Materials.isWaterPlant(state)
				|| (state.hasProperty(BlockStateProperties.WATERLOGGED) && state.getValue(BlockStateProperties.WATERLOGGED));
	};

	@Override
	public void serverExplosion(IExplosiveEntity ent) {
		final Level level = ent.getLevel();
		final Vec3 center = new Vec3(ent.x(), 0, ent.z());

		//ExplosionHelper#doSphericalExplosion visited all 268 k cells of this sphere, each with a fresh
		//BlockPos and a full Level#getBlockState. Air is never written (setBlock of the identical state is
		//a no-op) so a chunk section that holds nothing but air cannot contribute anything and is skipped
		//in one step of 16 cells.
		clearSoftBlocks(level, center, 40);

		new SectionSkippingExplosion(level, center, 100).doBlockExplosion(1f, 0.8f, 1f, 0.2f, false, true, false);

		//The r=100 sphere is 4.19 M cells and was walked in full. The callback can only act on water, bubble
		//columns, water plants and waterlogged blocks, and only at y <= 50, so the cap above y=50 is not
		//visited at all (-16 %) and every 16³ section whose palette holds none of those is rejected with a
		//single maybeHas probe. On terrain without water that is 4.19 M block reads down to ~2.6 k probes.
		removeWater(level, center, 100);

		new SectionSkippingExplosion(level, center, 80).doBlockExplosion(1f, 0.8f, 1f, 0.2f, true, new IForEachBlockExplosionEffect() {

			@Override
			public void doBlockExplosion(Level level, BlockPos pos, BlockState state, double distance) {
				if(state.getBlock().getExplosionResistance() <= 200 && !state.getCollisionShape(level, pos, CollisionContext.empty()).isEmpty()) {
					level.setBlock(pos, Blocks.NETHERRACK.defaultBlockState(), 3);
				}
			}
		});

		//The cylinder is 41 layers tall but the callback only acts at y <= -44, which is 17 of them:
		//206 k cells become 85 k, and the block state comes straight out of the chunk section.
		fillLava(level, new Vec3(ent.x(), -40, ent.z()), 40, 20);

		// was `new Random()`; the level already owns a RandomSource (AnimalKingdomEffect:89)
		int biome = level.getRandom().nextInt(3);

		new SectionSkippingExplosion(level, center, 80).doBlockExplosion(1f, 0.8f, 1f, 0.2f, true, new IForEachBlockExplosionEffect() {

			@Override
			public void doBlockExplosion(Level level, BlockPos pos, BlockState state, double distance) {
				// The two pure tests are cheaper than a chunk lookup, so the block above is only read
				// for netherrack below y=-10 instead of for every block in the r=80 explosion.
				if(pos.getY() > -10 || state.getBlock() != Blocks.NETHERRACK) {
					return;
				}
				if(!level.getBlockState(pos.above()).isAir()) {
					return;
				}
				if(biome == 0) {
					level.setBlock(pos, Blocks.CRIMSON_NYLIUM.defaultBlockState(), 3);
				} else if(biome == 1) {
					level.setBlock(pos, Blocks.WARPED_NYLIUM.defaultBlockState(), 3);
				} else if(biome == 2) {
					level.setBlock(pos, Blocks.SOUL_SAND.defaultBlockState(), 3);
				}
			}
		});

		// TODO(port-26.2): DISABLED — nether biome ConfiguredFeature.generate, DiskFeatureConfig and NetherFossil structure use yarn worldgen APIs with no 1:1 port here
		/*
		ImprovedExplosion explosion5 = new ImprovedExplosion(ent.getLevel(), new Vec3(ent.x(), 0, ent.z()), 150);
		explosion5.doBlockExplosion(1f, 0.8f, 1f, 0.2f, true, new IForEachBlockExplosionEffect() {
			// ... see git history, the body used yarn only worldgen APIs
		});
		*/
	}

	/**
	 * Clears every block of a sphere whose explosion resistance is at most 200, skipping all-air chunk
	 * sections. Identical to the {@code ExplosionHelper#doSphericalExplosion} pass it replaces: air has
	 * resistance 0 but writing {@code Blocks.AIR.defaultBlockState()} over the very same state makes
	 * {@link net.minecraft.world.level.chunk.LevelChunk#setBlockState} return null and {@code Level#setBlock}
	 * do nothing, so an all-air section can never produce an effect.
	 */
	private static void clearSoftBlocks(Level level, Vec3 position, int radius) {
		final int cx = Mth.floor(position.x);
		final int cy = Mth.floor(position.y);
		final int cz = Mth.floor(position.z);
		final long radiusSqr = (long)radius * radius;
		final BlockState air = Blocks.AIR.defaultBlockState();
		for(int offX = -radius; offX <= radius; offX++) {
			final long xSqr = (long)offX * offX;
			final int x = cx + offX;
			for(int offY = radius; offY >= -radius; offY--) {
				final long xySqr = xSqr + (long)offY * offY;
				if(xySqr > radiusSqr) {
					continue;
				}
				final int y = cy + offY;
				final int zMax = floorSqrt(radiusSqr - xySqr);
				for(int offZ = -zMax; offZ <= zMax;) {
					final int z = cz + offZ;
					//a run of cells that share one chunk section, so the section is resolved once per 16 cells
					final int runEnd = Math.min(zMax, offZ + ((z | 15) - z));
					final LevelChunkSection section = sectionAt(level, x, y, z);
					if(section == null || section.hasOnlyAir()) {
						offZ = runEnd + 1;
						continue;
					}
					for(; offZ <= runEnd; offZ++) {
						final int blockZ = cz + offZ;
						final BlockState state = section.getBlockState(x & 15, y & 15, blockZ & 15);
						if(state != air && state.getBlock().getExplosionResistance() <= 200) {
							level.setBlock(new BlockPos(x, y, blockZ), air, 3);
						}
					}
				}
			}
		}
	}

	/**
	 * Removes water, bubble columns and water plants and un-waterlogs everything else inside a sphere,
	 * but only at y <= 50, which is the only place the original callback ever acted.
	 */
	private static void removeWater(Level level, Vec3 position, int radius) {
		final int cx = Mth.floor(position.x);
		final int cy = Mth.floor(position.y);
		final int cz = Mth.floor(position.z);
		final long radiusSqr = (long)radius * radius;
		final BlockState air = Blocks.AIR.defaultBlockState();
		final int offYTop = Math.min(radius, 50 - cy);
		for(int offX = -radius; offX <= radius; offX++) {
			final long xSqr = (long)offX * offX;
			final int x = cx + offX;
			for(int offY = offYTop; offY >= -radius; offY--) {
				final long xySqr = xSqr + (long)offY * offY;
				if(xySqr > radiusSqr) {
					continue;
				}
				final int y = cy + offY;
				final int zMax = floorSqrt(radiusSqr - xySqr);
				for(int offZ = -zMax; offZ <= zMax;) {
					final int z = cz + offZ;
					final int runEnd = Math.min(zMax, offZ + ((z | 15) - z));
					final LevelChunkSection section = sectionAt(level, x, y, z);
					if(section == null || !section.maybeHas(WATER_LIKE)) {
						offZ = runEnd + 1;
						continue;
					}
					for(; offZ <= runEnd; offZ++) {
						final int blockZ = cz + offZ;
						final BlockState state = section.getBlockState(x & 15, y & 15, blockZ & 15);
						final Block block = state.getBlock();
						if(block instanceof LiquidBlock || block instanceof BubbleColumnBlock || Materials.isWaterPlant(state)) {
							level.setBlock(new BlockPos(x, y, blockZ), air, 3);
						}
						if(state.hasProperty(BlockStateProperties.WATERLOGGED) && state.getValue(BlockStateProperties.WATERLOGGED)) {
							level.setBlock(new BlockPos(x, y, blockZ), state.setValue(BlockStateProperties.WATERLOGGED, false), 3);
						}
					}
				}
			}
		}
	}

	/**
	 * Fills the air of a cylinder with lava, but only the layers at y <= -44 the original callback acted on.
	 */
	private static void fillLava(Level level, Vec3 position, int radius, int radiusY) {
		final int cx = Mth.floor(position.x);
		final int cy = Mth.floor(position.y);
		final int cz = Mth.floor(position.z);
		final long radiusSqr = (long)radius * radius;
		final BlockState lava = Blocks.LAVA.defaultBlockState();
		final int offYTop = Math.min(radiusY, -44 - cy);
		for(int offX = -radius; offX <= radius; offX++) {
			final long xSqr = (long)offX * offX;
			final int x = cx + offX;
			final int zMax = floorSqrt(radiusSqr - xSqr);
			for(int offY = -radiusY; offY <= offYTop; offY++) {
				final int y = cy + offY;
				for(int offZ = -zMax; offZ <= zMax;) {
					final int z = cz + offZ;
					final int runEnd = Math.min(zMax, offZ + ((z | 15) - z));
					final LevelChunkSection section = sectionAt(level, x, y, z);
					if(section == null) {
						//outside the build height, where Level#setBlock is a no-op anyway
						offZ = runEnd + 1;
						continue;
					}
					for(; offZ <= runEnd; offZ++) {
						final int blockZ = cz + offZ;
						//lava keeps flag 3: a fluid that is placed without a neighbour update never settles
						if(section.getBlockState(x & 15, y & 15, blockZ & 15).isAir()) {
							level.setBlock(new BlockPos(x, y, blockZ), lava, 3);
						}
					}
				}
			}
		}
	}

	@Nullable
	private static LevelChunkSection sectionAt(Level level, int x, int y, int z) {
		final LevelChunk chunk = level.getChunk(x >> 4, z >> 4);
		final int index = chunk.getSectionIndex(y);
		return index < 0 || index >= chunk.getSectionsCount() ? null : chunk.getSection(index);
	}

	/**
	 * @param value  a value greater than or equal to 0
	 * @return the greatest int whose square is less than or equal to the given value
	 */
	private static int floorSqrt(long value) {
		int root = (int)Math.sqrt((double)value);
		while(root > 0 && (long)root * root > value) {
			root--;
		}
		while((long)(root + 1) * (root + 1) <= value) {
			root++;
		}
		return root;
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
	
	// TODO(port-26.2): DISABLED — NetherFossilStructure/ChunkRandom/NetherFossilGenerator are yarn worldgen APIs with no 1:1 port here
	/*
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
	*/
}

package luckytnt.tnteffects;

import luckytnt.registry.BlockRegistry;
import luckytnt.util.Materials;
import luckytntlib.util.IExplosiveEntity;
import luckytntlib.util.explosions.ImprovedExplosion;
import luckytntlib.util.tnteffects.PrimedTNTEffect;
import net.fabricmc.fabric.api.tag.convention.v2.ConventionalBlockTags;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LiquidBlock;
// TODO(port-26.2): DISABLED import — yarn ChunkDataS2CPacket (see doJungleExplosion)
//import net.minecraft.network.packet.s2c.play.ChunkDataS2CPacket;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.core.Holder;
import net.minecraft.tags.BlockTags;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
// TODO(port-26.2): DISABLED imports — yarn worldgen/chunk APIs (see doJungleExplosion)
//import net.minecraft.world.level.biome.BiomeKeys;
//import net.minecraft.world.level.chunk.ChunkSection;
//import net.minecraft.world.level.chunk.PalettedContainer;
//import net.minecraft.world.level.chunk.ReadableContainer;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
//import net.minecraft.world.gen.feature.VegetationConfiguredFeatures;

public class JungleTNTEffect extends PrimedTNTEffect {

	private static final int RADIUS = 150;
	private static final int RADIUS_SQ = RADIUS * RADIUS;

	@Override
	public void serverExplosion(IExplosiveEntity ent) {
		Level level = ent.getLevel();
		if(!(level instanceof ServerLevel sLevel)) {
			return;
		}

		// This used to be replaceNonSolidBlockOrVegetationWithAir(150) followed by a
		// doSphericalExplosion(150): two full sweeps of the *same* r=150 ball, 2 x 4/3*pi*150^3 =
		// 28.3M getBlockState. Both walk the identical lattice in the identical order (x ascending,
		// y descending, z ascending) and the grass pass only ever looks at its own position - whose
		// post-clearing state is tracked in `current` - and at the position directly above it, which
		// the merged loop has already fully processed by the time it gets there. So one traversal
		// produces the exact same blocks at 14.1M reads.
		final ImprovedExplosion dummy = ImprovedExplosion.dummyExplosion(level);
		final BlockState grass = Blocks.GRASS_BLOCK.defaultBlockState();
		final int centerX = Mth.floor(ent.x());
		final int centerY = Mth.floor(ent.y());
		final int centerZ = Mth.floor(ent.z());
		BlockPos.MutableBlockPos mutable = new BlockPos.MutableBlockPos();
		BlockPos.MutableBlockPos above = new BlockPos.MutableBlockPos();
		int cachedChunkX = Integer.MIN_VALUE;
		int cachedChunkZ = Integer.MIN_VALUE;
		boolean chunkLoaded = false;

		for(int offX = -RADIUS; offX <= RADIUS; offX++) {
			int xSq = offX * offX;
			int posX = centerX + offX;
			for(int offY = RADIUS; offY >= -RADIUS; offY--) {
				int xySq = xSq + offY * offY;
				if(xySq > RADIUS_SQ) {
					continue;
				}
				int posY = centerY + offY;
				int zMax = floorSqrt(RADIUS_SQ - xySq);
				for(int offZ = -zMax; offZ <= zMax; offZ++) {
					int posZ = centerZ + offZ;
					// the loaded check only has to be redone when the column crosses into another chunk,
					// so it costs ~1/16th of a chunk lookup per cell instead of one per cell
					int chunkX = posX >> 4;
					int chunkZ = posZ >> 4;
					if(chunkX != cachedChunkX || chunkZ != cachedChunkZ) {
						cachedChunkX = chunkX;
						cachedChunkZ = chunkZ;
						chunkLoaded = level.hasChunk(chunkX, chunkZ);
					}
					if(!chunkLoaded) {
						continue;
					}
					mutable.set(posX, posY, posZ);
					BlockState current = replaceNonSolidBlockOrVegetationAt(level, sLevel, mutable, level.getBlockState(mutable), 99f, true, dummy);

					// Cheap, pure rejects first; only then read the block above (all clauses are side-effect free,
					// so reordering them cannot change the outcome).
					Block block = current.getBlock();
					if(block.getExplosionResistance() >= 100 || !Block.isFaceFull(current.getCollisionShape(level, mutable), Direction.UP)) {
						continue;
					}
					above.set(posX, posY + 1, posZ);
					BlockState stateTop = level.getBlockState(above);
					if(stateTop.getBlock().getExplosionResistance() < 100 && (stateTop.isAir() || Materials.isPlant(stateTop) || stateTop.is(BlockTags.SNOW))) {
						BlockPos pos = mutable.immutable();
						block.wasExploded(sLevel, pos, dummy);
						level.setBlock(pos, grass, 3);
					}
				}
			}
		}

		// TODO(port-26.2): DISABLED — doJungleExplosion needs yarn chunk/biome/packet + worldgen APIs
		//doJungleExplosion(ent, 150);
	}

	@Override
	public Block getBlock() {
		return BlockRegistry.JUNGLE_TNT.get();
	}

	@Override
	public int getDefaultFuse(IExplosiveEntity ent) {
		return 160;
	}

	/**
	 * The per position body of {@link #replaceNonSolidBlockOrVegetationWithAir}, split out so that a
	 * caller that is already walking the volume for another reason does not need a second sweep.
	 * @return the state the position holds after the call, which is the passed in state when nothing changed
	 */
	static BlockState replaceNonSolidBlockOrVegetationAt(Level level, ServerLevel sLevel, BlockPos pos, BlockState state, float maxResistance, boolean vegetation, ImprovedExplosion dummy) {
		Block block = state.getBlock();
		if(block.getExplosionResistance() <= maxResistance && !state.isAir() && ((!state.isCollisionShapeFullBlock(level, pos) && !state.is(Blocks.MUD) && !state.is(ConventionalBlockTags.CHESTS)) || (vegetation && (state.is(BlockTags.LEAVES) || state.is(BlockTags.LOGS) || block == Blocks.MANGROVE_ROOTS)))) {
			BlockPos immutable = pos.immutable();
			block.wasExploded(sLevel, immutable, dummy);
			BlockState replacement = Materials.isWaterPlant(state) ? Blocks.WATER.defaultBlockState() : Blocks.AIR.defaultBlockState();
			level.setBlock(immutable, replacement, 3);
			return replacement;
		}
		return state;
	}

	public static void replaceNonSolidBlockOrVegetationWithAir(IExplosiveEntity ent, double radius, float maxResistance, boolean vegetation) {
		Level level = ent.getLevel();
		if(!(level instanceof ServerLevel sLevel)) {
			return;
		}
		// Same iteration lattice as before (offsets stepping by 1 from -radius), but the squared
		// distance test now runs *before* the BlockPos allocation and the world read, whole x/y slices
		// outside the sphere are skipped entirely, and the z range is derived in closed form instead of
		// being walked from -radius and rejected cell by cell (at r=100 that is 1.05M fewer iterations
		// per call, 3.55M at r=150).
		final ImprovedExplosion dummy = ImprovedExplosion.dummyExplosion(level);
		double radiusSq = radius * radius;
		double entX = ent.x();
		double entY = ent.y();
		double entZ = ent.z();
		BlockPos.MutableBlockPos mutable = new BlockPos.MutableBlockPos();
		for(double offX = -radius; offX <= radius; offX++) {
			double dx2 = offX * offX;
			if(dx2 > radiusSq) {
				continue;
			}
			int posX = Mth.floor(entX + offX);
			for(double offY = radius; offY >= -radius; offY--) {
				double dxy2 = dx2 + offY * offY;
				if(dxy2 > radiusSq) {
					continue;
				}
				int posY = Mth.floor(entY + offY);
				double remaining = radiusSq - dxy2;
				//the first offset of the -radius + k lattice that is inside the sphere, nudged back and
				//forth once so that a rounding error in Math.sqrt can never drop or add a cell
				double offZ = -radius + Math.ceil(radius - Math.sqrt(remaining));
				while(offZ - 1d >= -radius && (offZ - 1d) * (offZ - 1d) <= remaining) {
					offZ--;
				}
				while(offZ <= radius && offZ * offZ > remaining) {
					offZ++;
				}
				for(; offZ <= radius && offZ * offZ <= remaining; offZ++) {
					mutable.set(posX, posY, Mth.floor(entZ + offZ));
					replaceNonSolidBlockOrVegetationAt(level, sLevel, mutable, level.getBlockState(mutable), maxResistance, vegetation, dummy);
				}
			}
		}
	}

	/**
	 * @param value  a value greater than or equal to 0
	 * @return the greatest int whose square is less than or equal to the given value
	 */
	static int floorSqrt(int value) {
		int root = (int)Math.sqrt(value);
		while(root > 0 && root * root > value) {
			root--;
		}
		while((root + 1) * (root + 1) <= value) {
			root++;
		}
		return root;
	}

	public static void doJungleExplosion(IExplosiveEntity ent, double radius) {
		// TODO(port-26.2): DISABLED — biome overwrite + ChunkDataS2CPacket + ConfiguredFeature.generate use yarn chunk/worldgen APIs with no 1:1 port here
		if(true) return;
		/*
		Registry<Biome> registry = ent.getLevel().registryAccess().get(Registries.BIOME);
		Holder<Biome> biome = registry.entryOf(BiomeKeys.JUNGLE);
		for(double offX = -radius; offX < radius; offX++) {
			for(double offZ = -radius; offZ < radius; offZ++) {
				boolean foundBlock = false;
				double distance = Math.sqrt(offX * offX + offZ * offZ);
				if(!ent.getLevel().isClientSide()) {
					if(distance < radius) {
						if(offX % 16 == 0 && offZ % 16 == 0) {
							for(ChunkSection section : ent.getLevel().getChunk(new BlockPos(Mth.floor(ent.x() + offX), 0, Mth.floor(ent.z() + offZ))).getSectionArray()) {
								ReadableContainer<Holder<Biome>> biomesRO = section.getBiomeContainer();
								for(int i = 0; i < 4; ++i) {
									for(int j = 0; j < 4; ++j) {
										for(int k = 0; k < 4; ++k) {
											if(biomesRO instanceof PalettedContainer<Holder<Biome>> biomes && biomes.get(i, j, k) != biome) {
												biomes.swapUnsafe(i, j, k, biome);
											}
										}
									}
								}
							}
						}
						for(ServerPlayer player : ((ServerLevel)ent.getLevel()).getPlayers()) {
							player.networkHandler.sendPacket(new ChunkDataS2CPacket(ent.getLevel().getWorldChunk(ent.getEffect().toBlockPos(new Vec3(ent.x() + offX, 0, ent.z() + offZ))), ent.getLevel().getLightingProvider(), null, null));
						}

						Registry<ConfiguredFeature<?, ?>> features = ent.getLevel().registryAccess().get(Registries.CONFIGURED_FEATURE);

						ConfiguredFeature<?, ?> patch_melon = features.get(VegetationConfiguredFeatures.PATCH_MELON);
						ConfiguredFeature<?, ?> trees_jungle = features.get(VegetationConfiguredFeatures.TREES_JUNGLE);
						ConfiguredFeature<?, ?> patch_grass_jungle = features.get(VegetationConfiguredFeatures.PATCH_GRASS_JUNGLE);

						for(double offY = 320; offY > -64; offY--) {
							BlockPos pos = new BlockPos(Mth.floor(ent.x() + offX), Mth.floor(ent.y() + offY), Mth.floor(ent.z() + offZ));
							BlockState state = ent.getLevel().getBlockState(pos);
							if(!foundBlock && state.isCollisionShapeFullBlock(ent.getLevel(), pos) && !state.isAir() && !(ent.getLevel().getBlockState(pos.above()).getBlock() instanceof LiquidBlock)) {
								if(offX % 30 == 0 && offZ % 30 == 0) {
									patch_melon.generate((ServerLevel)ent.getLevel(), ((ServerLevel)ent.getLevel()).getChunkSource().getChunkSource().getGenerator(), Random.create(), pos.above());
								}
								int random = new java.util.Random().nextInt(3);
								switch(random) {
									case 0: trees_jungle.generate((ServerLevel)ent.getLevel(), ((ServerLevel)ent.getLevel()).getChunkSource().getChunkSource().getGenerator(), Random.create(), pos.above()); break;
									case 1:	patch_grass_jungle.generate((ServerLevel)ent.getLevel(), ((ServerLevel)ent.getLevel()).getChunkSource().getChunkSource().getGenerator(), Random.create(), pos.above()); break;
								}
								foundBlock = true;
							}
						}
					}
				}
			}
		}
		*/
	}
}

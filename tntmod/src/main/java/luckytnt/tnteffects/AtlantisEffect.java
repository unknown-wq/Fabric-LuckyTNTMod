package luckytnt.tnteffects;


import com.google.common.base.Predicate;

import luckytnt.registry.BlockRegistry;
import luckytnt.util.Materials;
import luckytntlib.util.IExplosiveEntity;
import luckytntlib.util.explosions.ImprovedExplosion;
import luckytntlib.util.tnteffects.PrimedTNTEffect;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.squid.Squid;
import net.minecraft.world.level.material.Fluids;
// TODO(port-26.2): DISABLED — see serverExplosion; chunk/structure imports removed
// import net.minecraft.network.packet.s2c.play.ChunkDataS2CPacket;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.phys.Vec3;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
// import net.minecraft.world.level.biome.BiomeKeys;
// import net.minecraft.world.level.chunk.ChunkSection;
// import net.minecraft.world.level.chunk.PalettedContainer;
// import net.minecraft.world.level.chunk.ReadableContainer;
// import net.minecraft.world.level.levelgen.structure.Structure;
// import net.minecraft.world.gen.structure.StructureKeys;
import net.minecraft.world.entity.EntityTypes;

public class AtlantisEffect extends PrimedTNTEffect {
	
	Predicate<Holder<Biome>> predicate = (holder) -> {
		return true;
	};
	
	@Override
	public void explosionTick(IExplosiveEntity ent) {
		if(ent.getTNTFuse() == 240) {
			if(ent.getLevel() instanceof ServerLevel s_Level) {
	      		s_Level.getServer().setWeatherParameters(0, 10000, true, true);
	      	}
	      	ent.getLevel().playSound(null, ent.x(), ent.y(), ent.z(), SoundEvents.LIGHTNING_BOLT_THUNDER, SoundSource.WEATHER, 1000, 1);
		}
	}

	@Override
	public void serverExplosion(IExplosiveEntity ent) {
		// TODO(port-26.2): DISABLED — biome overwrite (PalettedContainer.swapUnsafe), chunk
		// resync (ChunkDataS2CPacket), and Ocean Ruin structure generation
		// (Structure.createStructureStart / StructureStart.place with 26.2-rewritten
		// signatures, ChunkSection/ReadableContainer/StructureKeys/BlockBox renamed/removed).
		// Kept the portable water/sand terraforming + squid spawning below.
		/*
		Registry<Biome> registry = ent.getLevel().registryAccess().get(Registries.BIOME);
		Holder<Biome> biome = registry.getEntry(registry.get(BiomeKeys.WARM_OCEAN));
		for(double offX = -100; offX < 100; offX++) {
			for(double offZ = -100; offZ < 100; offZ++) {
				boolean foundBlock = false;
				double distance = Math.sqrt(offX * offX + offZ * offZ);				
				if(ent.getLevel() instanceof ServerLevel sLevel) {
					if(distance < 100) {
						if(offX % 16 == 0 && offZ % 16 == 0) {
							for(ChunkSection section : ent.getLevel().getChunk(toBlockPos(new Vec3(ent.x() + offX, 0, ent.z() + offZ))).getSectionArray()) {
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
					}
					for(ServerPlayer player : sLevel.players()) {
						player.networkHandler.sendPacket(new ChunkDataS2CPacket(ent.getLevel().getWorldChunk(toBlockPos(new Vec3(ent.x() + offX, 0, ent.z() + offZ))), ent.getLevel().getLightingProvider(), null, null));
					}
					if(distance < 50) {
						Registry<Structure> structures = ent.getLevel().registryAccess().get(Registries.STRUCTURE);
						
						Structure ocean_ruin = structures.get(StructureKeys.OCEAN_RUIN_WARM);
						
						for(double offY = ent.getLevel().getTopY(); offY > ent.getLevel().getBottomY(); offY--) {
							BlockPos pos = toBlockPos(new Vec3(ent.x() + offX, offY, ent.z() + offZ));
							BlockState state = ent.getLevel().getBlockState(pos);
							if(!foundBlock && state.isCollisionShapeFullBlock(ent.getLevel(), pos) && !state.isAir()) {
								if(Math.random() < 0.0005f) {
									StructureStart start = ocean_ruin.createStructureStart(sLevel.registryAccess(), sLevel.getChunkSource().getGenerator(), sLevel.getChunkSource().getGenerator().getBiomeSource(), sLevel.getChunkSource().getNoiseConfig(), sLevel.getStructureManager(), sLevel.getSeed(), new ChunkPos(pos), 20, ent.getLevel(), predicate);
									start.place(sLevel, sLevel.getStructureAccessor(), sLevel.getChunkSource().getGenerator(), Random.create(), new BlockBox((int)ent.x() - 150, (int)ent.y() - 150, (int)ent.z() - 150, (int)ent.x() + 150, (int)ent.y() + 150, (int)ent.z() + 150), new ChunkPos(pos));
								}
								foundBlock = true;
							}
						}
					}
				}
			}
		}
		*/
		
		Level level = ent.getLevel();
		if(!(level instanceof ServerLevel sLevel)) {
			return;
		}

		// This used to be replaceNonSolidBlockOrVegetationWithAir(100) followed by a
		// doSphericalExplosion(100) around a center 8 blocks higher: 2 x 4/3*pi*100^3 = 8.4M
		// getBlockState, and the second callback threw away every read outside a 50 block tall band on
		// its very first line. Both spheres share the same x/z center, so for every (x, y) column the
		// two admissible z ranges are symmetric intervals and their union is just the wider of the two -
		// which lets a single traversal cover both. The y band is now a loop bound instead of a reject,
		// so the water/sand pass visits 1.47M positions instead of 4.19M.
		// Total: 8.38M -> ~4.4M reads, with identical output (the water/sand pass only looks at its own
		// position - tracked in `current` across the clearing pass - and at the position above it, which
		// the merged loop has already fully processed, exactly as the two pass version did).
		final ImprovedExplosion dummy = ImprovedExplosion.dummyExplosion(level);
		final BlockState water = Blocks.WATER.defaultBlockState();
		final BlockState sand = Blocks.SAND.defaultBlockState();
		final int centerX = Mth.floor(ent.x());
		final int centerY = Mth.floor(ent.y());
		final int centerZ = Mth.floor(ent.z());
		// dy = (ent.y() + 8) - pos.getY() has to land in [0, 50]; with pos.getY() = centerY + offY that
		// is exactly offY in [ceil(frac - 42), 8], frac being the fractional part of ent.y()
		final double frac = ent.y() - centerY;
		final int minBandY = Mth.ceil(frac - 42d);
		final int maxBandY = 8;
		BlockPos.MutableBlockPos mutable = new BlockPos.MutableBlockPos();
		BlockPos.MutableBlockPos above = new BlockPos.MutableBlockPos();
		int cachedChunkX = Integer.MIN_VALUE;
		int cachedChunkZ = Integer.MIN_VALUE;
		boolean chunkLoaded = false;

		for(int offX = -100; offX <= 100; offX++) {
			int xSq = offX * offX;
			int posX = centerX + offX;
			for(int offY = 100; offY >= -100; offY--) {
				int clearSq = xSq + offY * offY;
				int zMaxClear = clearSq <= 10000 ? JungleTNTEffect.floorSqrt(10000 - clearSq) : -1;
				int zMaxFlood = -1;
				if(offY >= minBandY && offY <= maxBandY) {
					int floodY = offY - 8;
					int floodSq = xSq + floodY * floodY;
					if(floodSq <= 10000) {
						zMaxFlood = JungleTNTEffect.floorSqrt(10000 - floodSq);
					}
				}
				int zMax = Math.max(zMaxClear, zMaxFlood);
				if(zMax < 0) {
					continue;
				}
				int posY = centerY + offY;
				for(int offZ = -zMax; offZ <= zMax; offZ++) {
					int posZ = centerZ + offZ;
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
					int absZ = Math.abs(offZ);
					mutable.set(posX, posY, posZ);
					BlockState current = level.getBlockState(mutable);
					if(absZ <= zMaxClear) {
						current = JungleTNTEffect.replaceNonSolidBlockOrVegetationAt(level, sLevel, mutable, current, 100f, true, dummy);
					}
					if(absZ > zMaxFlood) {
						continue;
					}
					Block block = current.getBlock();
					if((block.getExplosionResistance() < 0 || block instanceof LiquidBlock || current.isAir()) && !Materials.isStone(current)) {
						BlockPos pos = mutable.immutable();
						block.wasExploded(sLevel, pos, dummy);
						level.setBlock(pos, water, 3);
					}
					// The block-identity test is pure and cheap; running it first means the block above is
					// only read for the handful of positions that can actually become sand. Note the branch
					// above can never have fired when this test passes (grass/stone/deepslate/dirt/gravel are
					// neither air, nor liquid, nor negative-resistance), so the state above is unchanged.
					if(!current.isAir() && (block == Blocks.GRASS_BLOCK || block == Blocks.STONE || block == Blocks.DEEPSLATE || block == Blocks.DIRT || block == Blocks.GRAVEL)) {
						above.set(posX, posY + 1, posZ);
						BlockState stateTop = level.getBlockState(above);
						if((stateTop.getFluidState().is(Fluids.WATER) || stateTop.getFluidState().is(Fluids.FLOWING_WATER)) && stateTop.getBlock() != Blocks.SAND) {
							BlockPos pos = mutable.immutable();
							block.wasExploded(sLevel, pos, dummy);
							level.setBlock(pos, sand, 3);
						}
					}
				}
			}
		}

		for(int count = 0; count < 40; count++) {
			Entity squid = new Squid(EntityTypes.SQUID, level);
			squid.setPos(ent.x() + 50 * Math.random() - 50 * Math.random(), ent.y() + 8, ent.z() + 50 * Math.random() - 50 * Math.random());
			level.addFreshEntity(squid);
		}
	}
	
	@Override
	public void spawnParticles(IExplosiveEntity ent) {
		ent.getLevel().addParticle(ParticleTypes.SPLASH, ent.x(), ent.y() + 1.5f, ent.z(), 0, 0, 0);
	}
	
	@Override
	public Block getBlock() {
		return BlockRegistry.ATLANTIS.get();
	}
	
	@Override
	public int getDefaultFuse(IExplosiveEntity ent) {
		return 240;
	}
}

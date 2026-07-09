package luckytnt.tnteffects;


import com.google.common.base.Predicate;

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
					for(ServerPlayer player : sLevel.getPlayers()) {
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
									StructureStart start = ocean_ruin.createStructureStart(sLevel.registryAccess(), sLevel.getChunkSource().getChunkSource().getGenerator(), sLevel.getChunkSource().getChunkSource().getGenerator().getBiomeSource(), sLevel.getChunkSource().getNoiseConfig(), sLevel.getStructureManager(), sLevel.getSeed(), new ChunkPos(pos), 20, ent.getLevel(), predicate);
									start.place(sLevel, sLevel.getStructureAccessor(), sLevel.getChunkSource().getChunkSource().getGenerator(), Random.create(), new BlockBox((int)ent.x() - 150, (int)ent.y() - 150, (int)ent.z() - 150, (int)ent.x() + 150, (int)ent.y() + 150, (int)ent.z() + 150), new ChunkPos(pos));
								}
								foundBlock = true;
							}
						}
					}
				}
			}
		}
		*/
		
		JungleTNTEffect.replaceNonSolidBlockOrVegetationWithAir(ent, 100, 100, true);
		
		ExplosionHelper.doSphericalExplosion(ent.getLevel(), ent.getPos().add(0, 8, 0), 100, new IForEachBlockExplosionEffect() {
			
			@Override
			public void doBlockExplosion(Level level, BlockPos pos, BlockState state, double distance) {
				BlockPos posTop = pos.offset(0, 1, 0);
				BlockState stateTop = level.getBlockState(posTop);
				
				if(((ent.y() + 8) - pos.getY()) >= 0 && ((ent.y() + 8) - pos.getY()) <= 50) {
					if((state.getBlock().getExplosionResistance() < 0 || state.getBlock() instanceof LiquidBlock || state.isAir()) && !Materials.isStone(state)) {
						state.getBlock().wasExploded(level, pos, ImprovedExplosion.dummyExplosion(ent.getLevel()));
						level.setBlock(pos, Blocks.WATER.defaultBlockState(), 3);
					}
					if((stateTop.getFluidState().is(Fluids.WATER) || stateTop.getFluidState().is(Fluids.FLOWING_WATER)) && !state.isAir() && (state.getBlock() == Blocks.GRASS_BLOCK || state.getBlock() == Blocks.STONE || state.getBlock() == Blocks.DEEPSLATE || state.getBlock() == Blocks.DIRT || state.getBlock() == Blocks.GRAVEL) && level.getBlockState(pos.above()).getBlock() != Blocks.SAND) {
						state.getBlock().wasExploded(level, pos, ImprovedExplosion.dummyExplosion(ent.getLevel()));
						level.setBlock(pos, Blocks.SAND.defaultBlockState(), 3);
					}
				}
			}
		});
		
		for(int count = 0; count < 40; count++) {
			Entity squid = new Squid(EntityTypes.SQUID, ent.getLevel());
			squid.setPos(ent.x() + 50 * Math.random() - 50 * Math.random(), ent.y() + 8, ent.z() + 50 * Math.random() - 50 * Math.random());
			ent.getLevel().addFreshEntity(squid);
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

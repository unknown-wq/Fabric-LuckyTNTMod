package luckytnt.tnteffects;


import com.google.common.base.Predicate;

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
import net.minecraft.block.FluidBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.SquidEntity;
import net.minecraft.fluid.Fluids;
import net.minecraft.network.packet.s2c.play.ChunkDataS2CPacket;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.structure.StructureStart;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeKeys;
import net.minecraft.world.chunk.ChunkSection;
import net.minecraft.world.chunk.PalettedContainer;
import net.minecraft.world.chunk.ReadableContainer;
import net.minecraft.world.gen.structure.Structure;
import net.minecraft.world.gen.structure.StructureKeys;

public class AtlantisEffect extends PrimedTNTEffect {
	
	Predicate<RegistryEntry<Biome>> predicate = (holder) -> {
		return true;
	};
	
	@Override
	public void explosionTick(IExplosiveEntity ent) {
		if(ent.getTNTFuse() == 240) {
			if(ent.getLevel() instanceof ServerWorld s_Level) {
	      		s_Level.setWeather(0, 10000, true, true);
	      	}
	      	ent.getLevel().playSound(null, ent.x(), ent.y(), ent.z(), SoundEvents.ENTITY_LIGHTNING_BOLT_THUNDER, SoundCategory.WEATHER, 1000, 1);
		}
	}

	@Override
	public void serverExplosion(IExplosiveEntity ent) {
		Registry<Biome> registry = ent.getLevel().getRegistryManager().get(RegistryKeys.BIOME);
		RegistryEntry<Biome> biome = registry.getEntry(registry.get(BiomeKeys.WARM_OCEAN));
		for(double offX = -100; offX < 100; offX++) {
			for(double offZ = -100; offZ < 100; offZ++) {
				boolean foundBlock = false;
				double distance = Math.sqrt(offX * offX + offZ * offZ);				
				if(ent.getLevel() instanceof ServerWorld sLevel) {
					if(distance < 100) {
						if(offX % 16 == 0 && offZ % 16 == 0) {
							for(ChunkSection section : ent.getLevel().getChunk(toBlockPos(new Vec3d(ent.x() + offX, 0, ent.z() + offZ))).getSectionArray()) {
								ReadableContainer<RegistryEntry<Biome>> biomesRO = section.getBiomeContainer();
								for(int i = 0; i < 4; ++i) {
									for(int j = 0; j < 4; ++j) {
										for(int k = 0; k < 4; ++k) {
											if(biomesRO instanceof PalettedContainer<RegistryEntry<Biome>> biomes && biomes.get(i, j, k) != biome) {
												biomes.swapUnsafe(i, j, k, biome);
											}
										}
									}
								}
							}
						}
					}
					for(ServerPlayerEntity player : sLevel.getPlayers()) {
						player.networkHandler.sendPacket(new ChunkDataS2CPacket(ent.getLevel().getWorldChunk(toBlockPos(new Vec3d(ent.x() + offX, 0, ent.z() + offZ))), ent.getLevel().getLightingProvider(), null, null));
					}
					if(distance < 50) {
						Registry<Structure> structures = ent.getLevel().getRegistryManager().get(RegistryKeys.STRUCTURE);
						
						Structure ocean_ruin = structures.get(StructureKeys.OCEAN_RUIN_WARM);
						
						for(double offY = ent.getLevel().getTopY(); offY > ent.getLevel().getBottomY(); offY--) {
							BlockPos pos = toBlockPos(new Vec3d(ent.x() + offX, offY, ent.z() + offZ));
							BlockState state = ent.getLevel().getBlockState(pos);
							if(!foundBlock && state.isFullCube(ent.getLevel(), pos) && !state.isAir()) {
								if(Math.random() < 0.0005f) {
									StructureStart start = ocean_ruin.createStructureStart(sLevel.getRegistryManager(), sLevel.getChunkManager().getChunkGenerator(), sLevel.getChunkManager().getChunkGenerator().getBiomeSource(), sLevel.getChunkManager().getNoiseConfig(), sLevel.getStructureTemplateManager(), sLevel.getSeed(), new ChunkPos(pos), 20, ent.getLevel(), predicate);
									start.place(sLevel, sLevel.getStructureAccessor(), sLevel.getChunkManager().getChunkGenerator(), Random.create(), new BlockBox((int)ent.x() - 150, (int)ent.y() - 150, (int)ent.z() - 150, (int)ent.x() + 150, (int)ent.y() + 150, (int)ent.z() + 150), new ChunkPos(pos));
								}
								foundBlock = true;
							}
						}
					}
				}
			}
		}
		
		JungleTNTEffect.replaceNonSolidBlockOrVegetationWithAir(ent, 100, 100, true);
		
		ExplosionHelper.doSphericalExplosion(ent.getLevel(), ent.getPos().add(0, 8, 0), 100, new IForEachBlockExplosionEffect() {
			
			@Override
			public void doBlockExplosion(World level, BlockPos pos, BlockState state, double distance) {
				BlockPos posTop = pos.add(0, 1, 0);
				BlockState stateTop = level.getBlockState(posTop);
				
				if(((ent.y() + 8) - pos.getY()) >= 0 && ((ent.y() + 8) - pos.getY()) <= 50) {
					if((state.getBlock().getBlastResistance() < 0 || state.getBlock() instanceof FluidBlock || state.isAir()) && !Materials.isStone(state)) {
						state.getBlock().onDestroyedByExplosion(level, pos, ImprovedExplosion.dummyExplosion(ent.getLevel()));
						level.setBlockState(pos, Blocks.WATER.getDefaultState(), 3);
					}
					if((stateTop.getFluidState().isOf(Fluids.WATER) || stateTop.getFluidState().isOf(Fluids.FLOWING_WATER)) && !state.isAir() && (state.getBlock() == Blocks.GRASS_BLOCK || state.getBlock() == Blocks.STONE || state.getBlock() == Blocks.DEEPSLATE || state.getBlock() == Blocks.DIRT || state.getBlock() == Blocks.GRAVEL) && level.getBlockState(pos.up()).getBlock() != Blocks.SAND) {
						state.getBlock().onDestroyedByExplosion(level, pos, ImprovedExplosion.dummyExplosion(ent.getLevel()));
						level.setBlockState(pos, Blocks.SAND.getDefaultState(), 3);
					}
				}
			}
		});
		
		for(int count = 0; count < 40; count++) {
			Entity squid = new SquidEntity(EntityType.SQUID, ent.getLevel());
			squid.setPosition(ent.x() + 50 * Math.random() - 50 * Math.random(), ent.y() + 8, ent.z() + 50 * Math.random() - 50 * Math.random());
			ent.getLevel().spawnEntity(squid);
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

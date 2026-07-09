package luckytnt.tnteffects;

import luckytnt.event.LevelEvents;
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
import net.minecraft.block.GrassBlock;
import net.minecraft.block.MyceliumBlock;
import net.minecraft.network.packet.s2c.play.ChunkDataS2CPacket;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeKeys;
import net.minecraft.world.chunk.ChunkSection;
import net.minecraft.world.chunk.PalettedContainer;
import net.minecraft.world.chunk.ReadableContainer;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.VegetationConfiguredFeatures;

public class FlowerForestTNTEffect extends PrimedTNTEffect {

	@Override
	public void serverExplosion(IExplosiveEntity ent) {
		ExplosionHelper.doCylindricalExplosion(ent.getLevel(), ent.getPos(), 75, 75, new IForEachBlockExplosionEffect() {
			
			@Override
			public void doBlockExplosion(World level, BlockPos pos, BlockState state, double distance) {
				if(distance <= 50 && state.getBlock().getBlastResistance() <= 200) {
					if((!state.isFullCube(level, pos) || state.isOf(Blocks.FIRE) || state.isOf(Blocks.SOUL_FIRE) 
					|| state.isIn(BlockTags.LEAVES) || Materials.isPlant(state) || state.isIn(BlockTags.SNOW)
					|| Materials.isWood(state)) && !(state.getBlock() instanceof GrassBlock) && !(state.getBlock() instanceof MyceliumBlock)) 
					{
						state.getBlock().onDestroyedByExplosion(level, pos, ImprovedExplosion.dummyExplosion(ent.getLevel()));
						level.setBlockState(pos, Blocks.AIR.getDefaultState(), 3);
					}
				}
			}
		});
		
		for(int offX = -75; offX <= 75; offX++) {
			for(int offZ = -75; offZ <= 75; offZ++) {
				double distance = Math.sqrt(offX * offX + offZ * offZ);
				int y = LevelEvents.getTopBlock(ent.getLevel(), ent.x() + offX, ent.z() + offZ, true);
				BlockPos pos = toBlockPos(new Vec3d(ent.x() + offX, y, ent.z() + offZ));
				if(distance <= 75 && ent.getLevel().getBlockState(pos).getBlock().getBlastResistance() <= 200 && ent.getLevel().getBlockState(pos.up()).isAir()) {
					ent.getLevel().setBlockState(pos, Blocks.GRASS_BLOCK.getDefaultState(), 3);
				}
			}
		}
		
		Registry<Biome> registry = ent.getLevel().getRegistryManager().get(RegistryKeys.BIOME);
		RegistryEntry<Biome> biome = registry.entryOf(BiomeKeys.FLOWER_FOREST);
		for(double offX = -75; offX < 75; offX++) {
			for(double offZ = -75; offZ < 75; offZ++) {
				boolean foundBlock = false;
				double distance = Math.sqrt(offX * offX + offZ * offZ);
				if(!ent.getLevel().isClient()) {
					if(distance < 75) {
						if(offX % 16 == 0 && offZ % 16 == 0) {
							for(ChunkSection section : ent.getLevel().getChunk(toBlockPos(new Vec3d(ent.x() + offX, 0, ent.z() + offZ))).getSectionArray()) {
								ReadableContainer<RegistryEntry<Biome>> biomesRO = section.getBiomeContainer();
								for(int i = 0; i < 4; ++i) {
									for(int j = 0; j < 4; ++j) {
										for(int k = 0; k < 4; ++k) {
											if(biomesRO instanceof PalettedContainer<RegistryEntry<Biome>> biomes && biomes.get(i, j, k) != registry.entryOf(BiomeKeys.FLOWER_FOREST)) {
												biomes.swapUnsafe(i, j, k, biome);
											}
										}
									}
								}
							}
						}
						for(ServerPlayerEntity player : ((ServerWorld)ent.getLevel()).getPlayers()) {
							player.networkHandler.sendPacket(new ChunkDataS2CPacket(ent.getLevel().getWorldChunk(toBlockPos(new Vec3d(ent.x() + offX, 0, ent.z() + offZ))), ent.getLevel().getLightingProvider(), null, null));
						}
						for(double offY = 320; offY > -64; offY--) {
							BlockPos pos = toBlockPos(new Vec3d(ent.x() + offX, ent.y() + offY, ent.z() + offZ));
							BlockState state = ent.getLevel().getBlockState(pos);
							Registry<ConfiguredFeature<?, ?>> features = ent.getLevel().getRegistryManager().get(RegistryKeys.CONFIGURED_FEATURE);
							if(!foundBlock && state.isFullCube(ent.getLevel(), pos) && !state.isAir() && !(ent.getLevel().getBlockState(pos.up()).getBlock() instanceof FluidBlock)) {
								double random = Math.random();
								if(random <= 0.1D) {
									features.get(VegetationConfiguredFeatures.TREES_FLOWER_FOREST).generate((StructureWorldAccess)ent.getLevel(), ((ServerWorld)ent.getLevel()).getChunkManager().getChunkGenerator(), Random.create(), pos.up());
								} else if(random > 0.1D && random <= 0.1125D) {
									features.get(VegetationConfiguredFeatures.FOREST_FLOWERS).generate((StructureWorldAccess)ent.getLevel(), ((ServerWorld)ent.getLevel()).getChunkManager().getChunkGenerator(), Random.create(), pos.up());
								} else if(random > 0.15D && random <= 0.1625D) {
									features.get(VegetationConfiguredFeatures.FLOWER_FLOWER_FOREST).generate((StructureWorldAccess)ent.getLevel(), ((ServerWorld)ent.getLevel()).getChunkManager().getChunkGenerator(), Random.create(), pos.up());
								} else if(random > 0.2D && random <= 0.2125D) {
									features.get(VegetationConfiguredFeatures.PATCH_GRASS).generate((StructureWorldAccess)ent.getLevel(), ((ServerWorld)ent.getLevel()).getChunkManager().getChunkGenerator(), Random.create(), pos.up());
								}
								foundBlock = true;
							}
						}
					}
				}
			}
		}
	}
	
	@Override
	public Block getBlock() {
		return BlockRegistry.FLOWER_FOREST_TNT.get();
	}
	
	@Override
	public int getDefaultFuse(IExplosiveEntity ent) {
		return 160;
	}
}

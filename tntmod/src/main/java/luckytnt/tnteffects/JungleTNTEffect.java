package luckytnt.tnteffects;

import luckytnt.registry.BlockRegistry;
import luckytnt.util.Materials;
import luckytntlib.util.IExplosiveEntity;
import luckytntlib.util.explosions.ExplosionHelper;
import luckytntlib.util.explosions.IForEachBlockExplosionEffect;
import luckytntlib.util.explosions.ImprovedExplosion;
import luckytntlib.util.tnteffects.PrimedTNTEffect;
import net.fabricmc.fabric.api.tag.convention.v2.ConventionalBlockTags;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FluidBlock;
import net.minecraft.network.packet.s2c.play.ChunkDataS2CPacket;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeKeys;
import net.minecraft.world.chunk.ChunkSection;
import net.minecraft.world.chunk.PalettedContainer;
import net.minecraft.world.chunk.ReadableContainer;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.VegetationConfiguredFeatures;

public class JungleTNTEffect extends PrimedTNTEffect {

	@Override
	public void serverExplosion(IExplosiveEntity ent) {
		replaceNonSolidBlockOrVegetationWithAir(ent, 150, 99, true);
		
		ExplosionHelper.doSphericalExplosion(ent.getLevel(), ent.getPos(), 150, new IForEachBlockExplosionEffect() {
			
			@Override
			public void doBlockExplosion(World level, BlockPos pos, BlockState state, double distance) {
				BlockPos posTop = pos.add(0, 1, 0);
				BlockState stateTop = level.getBlockState(posTop);
				
				if(state.getBlock().getBlastResistance() < 100 && stateTop.getBlock().getBlastResistance() < 100 && Block.isFaceFullSquare(state.getCollisionShape(level, pos), Direction.UP) && (stateTop.isAir() || Materials.isPlant(stateTop) || stateTop.isIn(BlockTags.SNOW))) {
					state.getBlock().onDestroyedByExplosion(level, pos, ImprovedExplosion.dummyExplosion(ent.getLevel()));
					level.setBlockState(pos, Blocks.GRASS_BLOCK.getDefaultState(), 3);
				}
			}
		});
		
		doJungleExplosion(ent, 150);
	}
	
	@Override
	public Block getBlock() {
		return BlockRegistry.JUNGLE_TNT.get();
	}
	
	@Override
	public int getDefaultFuse(IExplosiveEntity ent) {
		return 160;
	}
	
	public static void replaceNonSolidBlockOrVegetationWithAir(IExplosiveEntity ent, double radius, float maxResistance, boolean vegetation) {
		if(!ent.getLevel().isClient()) {
			for(double offX = -radius; offX <= radius; offX++) {
				for(double offY = radius; offY >= -radius; offY--) {
					for(double offZ = -radius; offZ <= radius; offZ++) {
						double distance = Math.sqrt(offX * offX + offY * offY + offZ * offZ);
						BlockPos pos = new BlockPos(MathHelper.floor(ent.x() + offX), MathHelper.floor(ent.y() + offY), MathHelper.floor(ent.z() + offZ));
						BlockState state = ent.getLevel().getBlockState(pos);
						if(distance <= radius) {					
							if(state.getBlock().getBlastResistance() <= maxResistance && !state.isAir() && ((!state.isFullCube(ent.getLevel(), pos) && !state.isOf(Blocks.MUD) && !state.isIn(ConventionalBlockTags.CHESTS)) || (vegetation && (state.isIn(BlockTags.LEAVES) || state.isIn(BlockTags.LOGS) || state.getBlock() == Blocks.MANGROVE_ROOTS)))) {
								if(Materials.isWaterPlant(state)) {
									Block block1 = state.getBlock();
									block1.onDestroyedByExplosion(ent.getLevel(), pos, ImprovedExplosion.dummyExplosion(ent.getLevel()));
									ent.getLevel().setBlockState(pos, Blocks.WATER.getDefaultState(), 3);
								} else {
									state.getBlock().onDestroyedByExplosion(ent.getLevel(), pos, ImprovedExplosion.dummyExplosion(ent.getLevel()));
									ent.getLevel().setBlockState(pos, Blocks.AIR.getDefaultState(), 3);
								}
							}
						}
					}
				}
			}
		}
	}
	
	public static void doJungleExplosion(IExplosiveEntity ent, double radius) {
		Registry<Biome> registry = ent.getLevel().getRegistryManager().get(RegistryKeys.BIOME);
		RegistryEntry<Biome> biome = registry.entryOf(BiomeKeys.JUNGLE);
		for(double offX = -radius; offX < radius; offX++) {
			for(double offZ = -radius; offZ < radius; offZ++) {
				boolean foundBlock = false;
				double distance = Math.sqrt(offX * offX + offZ * offZ);
				if(!ent.getLevel().isClient()) {
					if(distance < radius) {
						if(offX % 16 == 0 && offZ % 16 == 0) {
							for(ChunkSection section : ent.getLevel().getChunk(new BlockPos(MathHelper.floor(ent.x() + offX), 0, MathHelper.floor(ent.z() + offZ))).getSectionArray()) {
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
						for(ServerPlayerEntity player : ((ServerWorld)ent.getLevel()).getPlayers()) {
							player.networkHandler.sendPacket(new ChunkDataS2CPacket(ent.getLevel().getWorldChunk(ent.getEffect().toBlockPos(new Vec3d(ent.x() + offX, 0, ent.z() + offZ))), ent.getLevel().getLightingProvider(), null, null));
						}
						
						Registry<ConfiguredFeature<?, ?>> features = ent.getLevel().getRegistryManager().get(RegistryKeys.CONFIGURED_FEATURE);
						
						ConfiguredFeature<?, ?> patch_melon = features.get(VegetationConfiguredFeatures.PATCH_MELON);
						ConfiguredFeature<?, ?> trees_jungle = features.get(VegetationConfiguredFeatures.TREES_JUNGLE);
						ConfiguredFeature<?, ?> patch_grass_jungle = features.get(VegetationConfiguredFeatures.PATCH_GRASS_JUNGLE);
						
						for(double offY = 320; offY > -64; offY--) {
							BlockPos pos = new BlockPos(MathHelper.floor(ent.x() + offX), MathHelper.floor(ent.y() + offY), MathHelper.floor(ent.z() + offZ));
							BlockState state = ent.getLevel().getBlockState(pos);
							if(!foundBlock && state.isFullCube(ent.getLevel(), pos) && !state.isAir() && !(ent.getLevel().getBlockState(pos.up()).getBlock() instanceof FluidBlock)) {
								if(offX % 30 == 0 && offZ % 30 == 0) {
									patch_melon.generate((ServerWorld)ent.getLevel(), ((ServerWorld)ent.getLevel()).getChunkManager().getChunkGenerator(), Random.create(), pos.up());
								}
								int random = new java.util.Random().nextInt(3);
								switch(random) {
									case 0: trees_jungle.generate((ServerWorld)ent.getLevel(), ((ServerWorld)ent.getLevel()).getChunkManager().getChunkGenerator(), Random.create(), pos.up()); break;
									case 1:	patch_grass_jungle.generate((ServerWorld)ent.getLevel(), ((ServerWorld)ent.getLevel()).getChunkManager().getChunkGenerator(), Random.create(), pos.up()); break;
								}
								foundBlock = true;
							}
						}
					}
				}
			}
		}
	}
}

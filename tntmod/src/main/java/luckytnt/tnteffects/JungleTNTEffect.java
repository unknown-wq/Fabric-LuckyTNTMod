package luckytnt.tnteffects;

import luckytnt.registry.BlockRegistry;
import luckytnt.util.Materials;
import luckytntlib.util.IExplosiveEntity;
import luckytntlib.util.explosions.ExplosionHelper;
import luckytntlib.util.explosions.IForEachBlockExplosionEffect;
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

	@Override
	public void serverExplosion(IExplosiveEntity ent) {
		replaceNonSolidBlockOrVegetationWithAir(ent, 150, 99, true);
		
		ExplosionHelper.doSphericalExplosion(ent.getLevel(), ent.getPos(), 150, new IForEachBlockExplosionEffect() {
			
			@Override
			public void doBlockExplosion(Level level, BlockPos pos, BlockState state, double distance) {
				BlockPos posTop = pos.offset(0, 1, 0);
				BlockState stateTop = level.getBlockState(posTop);
				
				if(state.getBlock().getExplosionResistance() < 100 && stateTop.getBlock().getExplosionResistance() < 100 && Block.isFaceFull(state.getCollisionShape(level, pos), Direction.UP) && (stateTop.isAir() || Materials.isPlant(stateTop) || stateTop.is(BlockTags.SNOW))) {
					state.getBlock().wasExploded((ServerLevel)level, pos, ImprovedExplosion.dummyExplosion(ent.getLevel()));
					level.setBlock(pos, Blocks.GRASS_BLOCK.defaultBlockState(), 3);
				}
			}
		});
		
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
	
	public static void replaceNonSolidBlockOrVegetationWithAir(IExplosiveEntity ent, double radius, float maxResistance, boolean vegetation) {
		if(!ent.getLevel().isClientSide()) {
			for(double offX = -radius; offX <= radius; offX++) {
				for(double offY = radius; offY >= -radius; offY--) {
					for(double offZ = -radius; offZ <= radius; offZ++) {
						double distance = Math.sqrt(offX * offX + offY * offY + offZ * offZ);
						BlockPos pos = new BlockPos(Mth.floor(ent.x() + offX), Mth.floor(ent.y() + offY), Mth.floor(ent.z() + offZ));
						BlockState state = ent.getLevel().getBlockState(pos);
						if(distance <= radius) {					
							if(state.getBlock().getExplosionResistance() <= maxResistance && !state.isAir() && ((!state.isCollisionShapeFullBlock(ent.getLevel(), pos) && !state.is(Blocks.MUD) && !state.is(ConventionalBlockTags.CHESTS)) || (vegetation && (state.is(BlockTags.LEAVES) || state.is(BlockTags.LOGS) || state.getBlock() == Blocks.MANGROVE_ROOTS)))) {
								if(Materials.isWaterPlant(state)) {
									Block block1 = state.getBlock();
									block1.wasExploded((ServerLevel)ent.getLevel(), pos, ImprovedExplosion.dummyExplosion(ent.getLevel()));
									ent.getLevel().setBlock(pos, Blocks.WATER.defaultBlockState(), 3);
								} else {
									state.getBlock().wasExploded((ServerLevel)ent.getLevel(), pos, ImprovedExplosion.dummyExplosion(ent.getLevel()));
									ent.getLevel().setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
								}
							}
						}
					}
				}
			}
		}
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
									patch_melon.generate((ServerLevel)ent.getLevel(), ((ServerLevel)ent.getLevel()).getChunkSource().getChunkGenerator(), Random.create(), pos.above());
								}
								int random = new java.util.Random().nextInt(3);
								switch(random) {
									case 0: trees_jungle.generate((ServerLevel)ent.getLevel(), ((ServerLevel)ent.getLevel()).getChunkSource().getChunkGenerator(), Random.create(), pos.above()); break;
									case 1:	patch_grass_jungle.generate((ServerLevel)ent.getLevel(), ((ServerLevel)ent.getLevel()).getChunkSource().getChunkGenerator(), Random.create(), pos.above()); break;
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

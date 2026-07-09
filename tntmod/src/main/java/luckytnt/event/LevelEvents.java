package luckytnt.event;

import java.util.List;
import java.util.Random;

import luckytnt.LevelVariables;
import luckytnt.config.LuckyTNTConfigValues;
import luckytnt.registry.EntityRegistry;
import luckytnt.util.Materials;
import luckytntlib.entity.LExplosiveProjectile;
import luckytntlib.entity.LivingPrimedLTNT;
import luckytntlib.entity.PrimedLTNT;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LightningEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.network.packet.s2c.play.ChunkDataS2CPacket;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeKeys;
import net.minecraft.world.chunk.ChunkSection;
import net.minecraft.world.chunk.PalettedContainer;

public class LevelEvents {

	public static void onLevelUpdate(ServerWorld event) {
		World level = event;
		
		List<? extends PlayerEntity> players = level.getPlayers();
		LevelVariables variables = LevelVariables.get(level);
		if(level.getRegistryKey() == World.OVERWORLD) {
			if(level instanceof ServerWorld sLevel) {
				if(variables.doomsdayTime > 0)
					variables.doomsdayTime--;
				if(variables.toxicCloudsTime > 0)
					variables.toxicCloudsTime--;
				if(variables.iceAgeTime > 0)
					variables.iceAgeTime--;
				if(variables.heatDeathTime > 0)
					variables.heatDeathTime--;
				if(variables.tntRainTime > 0)
					variables.tntRainTime--;
				variables.sync(sLevel);
			}
			for(PlayerEntity player : players) {
				if(variables != null) {
					double x = player.getX();
					double y = player.getY();
					double z = player.getZ();
					if(variables.doomsdayTime > 0) {
						for(int count = 0; count < 6; count++) {
							Entity ent = EntityRegistry.HAILSTONE.get().create(level);
							ent.setPosition(x + Math.random() * 100 - Math.random() * 100, y + LuckyTNTConfigValues.DROP_HEIGHT.get() / 4 + Math.random() * LuckyTNTConfigValues.DROP_HEIGHT.get() / 4, z + Math.random() * 100 - Math.random() * 100);
							level.spawnEntity(ent);
						}
						if(Math.random() < 0.00675f * LuckyTNTConfigValues.AVERAGE_DIASTER_INTENSITY.get()) {
							LExplosiveProjectile ent = EntityRegistry.LITTLE_METEOR.get().create(level);
							ent.setPosition(x + Math.random() * 200 - Math.random() * 200, y + LuckyTNTConfigValues.DROP_HEIGHT.get(), z + Math.random() * 200 - Math.random() * 200);
							level.spawnEntity(ent);
						}
						if(Math.random() < 0.025f * LuckyTNTConfigValues.AVERAGE_DIASTER_INTENSITY.get()) {
							Entity ent = EntityRegistry.MINI_METEOR.get().create(level);
							ent.setPosition(x + Math.random() * 200 - Math.random() * 200, y + LuckyTNTConfigValues.DROP_HEIGHT.get(), z + Math.random() * 200 - Math.random() * 200);
							level.spawnEntity(ent);
						}
						if(Math.random() < 0.1f * LuckyTNTConfigValues.AVERAGE_DIASTER_INTENSITY.get()) {
							if(level instanceof ServerWorld) {
								double offX = Math.random() * 200 - Math.random() * 200;
								double offZ = Math.random() * 200 - Math.random() * 200;
								for(double offY = 320; offY > -64; offY--) {
									if(!level.getBlockState(new BlockPos(MathHelper.floor(x + offX), MathHelper.floor(offY), MathHelper.floor(z + offZ))).isAir()) {
										Entity lighting = new LightningEntity(EntityType.LIGHTNING_BOLT,  level);
										lighting.setPosition(x + offX, offY, z + offZ);
										level.spawnEntity(lighting);
										break;
									}
								}
							}
						}
					}
					if(variables.toxicCloudsTime > 0) {
						if(Math.random() < 0.005f * LuckyTNTConfigValues.AVERAGE_DIASTER_INTENSITY.get()) {
							BlockPos pos = new BlockPos(MathHelper.floor(x + Math.random() * 100 - Math.random() * 100), MathHelper.floor(y + Math.random() * 50 - Math.random() * 50), MathHelper.floor(z + Math.random() * 100 - Math.random() * 100));
							if(!level.getBlockState(pos).isFullCube(level, pos) || level.getBlockState(pos).isAir()) {
								PrimedLTNT cloud = EntityRegistry.TOXIC_CLOUD.get().create(level);
								cloud.setPosition(pos.getX(), pos.getY(), pos.getZ());
								level.spawnEntity(cloud);
							}						
						}
					}
					if(variables.iceAgeTime > 0) {
						Registry<Biome> registry = level.getRegistryManager().get(RegistryKeys.BIOME);
						RegistryEntry<Biome> biome = registry.entryOf(BiomeKeys.SNOWY_TAIGA);
						if(player instanceof ServerPlayerEntity sPlayer) {	
							for(double offX = -32; offX <= 32; offX += 16) {
								for(double offZ = -32; offZ <= 32; offZ += 16) {
									boolean needsUpdate = false;
									for(ChunkSection section : level.getChunk(new BlockPos(MathHelper.floor(x + offX), 0, MathHelper.floor(z + offZ))).getSectionArray()) {
										for(int i = 0; i < 4; ++i) {
											for(int j = 0; j < 4; ++j) {
												for(int k = 0; k < 4; ++k) {
													if(section.getBiomeContainer() instanceof PalettedContainer<RegistryEntry<Biome>> container && section.getBiomeContainer().get(i, j, k).value() != biome.value()) {
														container.swapUnsafe(i, j, k, biome);
														needsUpdate = true;
													}
												}
											}
										}
									}
									if(needsUpdate) {
										sPlayer.networkHandler.sendPacket(new ChunkDataS2CPacket(level.getWorldChunk(new BlockPos(MathHelper.floor(x + offX), 0, MathHelper.floor(z + offZ))), level.getLightingProvider(), null, null));
									}
								}
							}
						}
					}
					if(variables.heatDeathTime > 0) {
						Registry<Biome> registry = level.getRegistryManager().get(RegistryKeys.BIOME);
						RegistryEntry<Biome> biome = registry.entryOf(BiomeKeys.DESERT);
						if(player instanceof ServerPlayerEntity sPlayer) {	
							for(double offX = -32; offX <= 32; offX += 16) {
								for(double offZ = -32; offZ <= 32; offZ += 16) {
									boolean needsUpdate = false;
									for(ChunkSection section : level.getChunk(new BlockPos(MathHelper.floor(x + offX), 0, MathHelper.floor(z + offZ))).getSectionArray()) {
										for(int i = 0; i < 4; ++i) {
											for(int j = 0; j < 4; ++j) {
												for(int k = 0; k < 4; ++k) {
													if(section.getBiomeContainer() instanceof PalettedContainer<RegistryEntry<Biome>> container && section.getBiomeContainer().get(i, j, k).value() != biome.value()) {
														container.swapUnsafe(i, j, k, biome);
														needsUpdate = true;
													}
												}
											}
										}
									}
									if(needsUpdate) {
										sPlayer.networkHandler.sendPacket(new ChunkDataS2CPacket(level.getWorldChunk(new BlockPos(MathHelper.floor(x + offX), 0, MathHelper.floor(z + offZ))), level.getLightingProvider(), null, null));
									}
								}
							}
							for(int i = 0; i < 1 + (int)(0.5D * LuckyTNTConfigValues.AVERAGE_DIASTER_INTENSITY.get()); i++) {
								int offX = new Random().nextInt(60) - 30;
								int offZ = new Random().nextInt(60) - 30;
								int posY = getTopBlock(sPlayer.getWorld(), sPlayer.getX() + offX, sPlayer.getZ() + offZ, false);
								BlockPos pos = new BlockPos(MathHelper.floor(sPlayer.getX() + offX), MathHelper.floor(posY + 1), MathHelper.floor(sPlayer.getZ() + offZ));
								BlockState state = sPlayer.getWorld().getBlockState(pos);
								if((Materials.isPlant(state) || state.isAir()) && state.getBlock().getBlastResistance() <= 100) {
									if(Math.random() > 0.1D) {
										BlockHitResult result = new BlockHitResult(new Vec3d(sPlayer.getX(), sPlayer.getY(), sPlayer.getZ()), Direction.UP, pos, false);
										ItemPlacementContext ctx = new ItemPlacementContext(sPlayer, Hand.MAIN_HAND, sPlayer.getStackInHand(Hand.MAIN_HAND), result);
										level.setBlockState(pos, Blocks.FIRE.getPlacementState(ctx), 3);
									} else {
										level.setBlockState(pos, Blocks.LAVA.getDefaultState(), 3);
									}
								}
							}
							for(int i = 0; i < 1 + (int)(0.5D * LuckyTNTConfigValues.AVERAGE_DIASTER_INTENSITY.get()); i++) {
								int offX = new Random().nextInt(60) - 30;
								int offZ = new Random().nextInt(60) - 30;
								int posY = getTopBlock(sPlayer.getWorld(), sPlayer.getX() + offX, sPlayer.getZ() + offZ, true);
								BlockPos pos = new BlockPos(MathHelper.floor(sPlayer.getX() + offX), posY, MathHelper.floor(sPlayer.getZ() + offZ));
								BlockState state = sPlayer.getWorld().getBlockState(pos);
								if(state.isOf(Blocks.GRASS_BLOCK)) {
									level.setBlockState(pos, Math.random() > 0.5D ? Blocks.COARSE_DIRT.getDefaultState() : Blocks.DIRT.getDefaultState(), 3);
								} else if(sPlayer.getWorld().getBlockState(pos.up()).isOf(Blocks.WATER) && Math.random() > 0.6D) {
									level.setBlockState(pos, Blocks.MAGMA_BLOCK.getDefaultState(), 3);
								}
							}
							for(int offX = -30; offX < 30; offX += 2) {
								for(int offZ = -30; offZ < 30; offZ += 2) {
									int posY = getTopBlock(sPlayer.getWorld(), sPlayer.getX() + offX, sPlayer.getZ() + offZ, true);
									BlockPos pos = new BlockPos(MathHelper.floor(sPlayer.getX() + offX), posY + 1, MathHelper.floor(sPlayer.getZ() + offZ));
									BlockState state = sPlayer.getWorld().getBlockState(pos);
									if((Materials.isPlant(state) || state.isAir()) && Blocks.DEAD_BUSH.getDefaultState().canPlaceAt(level, pos) && state.getBlock().getBlastResistance() <= 100 && state.getBlock() != Blocks.DEAD_BUSH) {
										level.setBlockState(pos, Blocks.DEAD_BUSH.getDefaultState(), 3);
									}
								}
							}
						}
					}
					if(variables.tntRainTime > 0) {
						int i = 4;
						if(LuckyTNTConfigValues.AVERAGE_DIASTER_INTENSITY.get().intValue() > 5) {
							i = 3;
						} else if(LuckyTNTConfigValues.AVERAGE_DIASTER_INTENSITY.get().intValue() > 10) {
							i = 2;
						} else if(LuckyTNTConfigValues.AVERAGE_DIASTER_INTENSITY.get().intValue() > 15) {
							i = 1;
						}
						if (!level.isClient() && variables.tntRainTime % i == 0) {
							Entity ent;
							int rand = new Random().nextInt(100);
							if (rand == 0) {
								ent = EntityRegistry.TNT_X20.get().create(level);
							} else if (rand > 4 && rand <= 6) {
								ent = EntityRegistry.FIRE_TNT.get().create(level);
							} else if (rand > 6 && rand <= 8) {
								ent = EntityRegistry.SNOW_TNT.get().create(level);
							} else if (rand > 8 && rand <= 10) {
								ent = EntityRegistry.FREEZE_TNT.get().create(level);
							} else if (rand > 10 && rand <= 12) {
								ent = EntityRegistry.ATTACKING_TNT.get().create(level);
							} else if (rand > 12 && rand <= 14) {
								ent = EntityRegistry.BIG_TNT.get().create(level);
							} else if (rand > 14 && rand <= 16) {
								ent = EntityRegistry.WALKING_TNT.get().create(level);
							} else if (rand > 16 && rand <= 18) {
								ent = EntityRegistry.NUCLEAR_WASTE_TNT.get().create(level);
							} else if (rand > 18 && rand <= 20) {
								ent = EntityRegistry.BOUNCING_TNT.get().create(level);
							} else if (rand > 20 && rand <= 22) {
								ent = EntityRegistry.FARMING_TNT.get().create(level);
							} else if (rand > 22 && rand <= 24) {
								ent = EntityRegistry.GROVE_TNT.get().create(level);
							} else if (rand > 24 && rand <= 26) {
								ent = EntityRegistry.CUBIC_TNT.get().create(level);
							} else if (rand > 26 && rand <= 28) {
								ent = EntityRegistry.BUTTER_TNT.get().create(level);
							} else if (rand > 28 && rand <= 30) {
								ent = EntityRegistry.GROVE_TNT.get().create(level);
							} else if (rand > 30 && rand <= 31) {
								ent = EntityRegistry.COMPACT_TNT.get().create(level);
							} else if (rand > 31 && rand <= 32) {
								ent = EntityRegistry.RANDOM_TNT.get().create(level);
							} else if (rand > 32 && rand <= 47) {
								ent = EntityRegistry.TNT_X5.get().create(level);
							} else {
								ent = EntityRegistry.TNT.get().create(level);
							}
							ent.setPosition(player.getX() + (Math.random() * 80D - 40D), player.getY() + 20D + Math.random() * 10D, player.getZ() + (Math.random() * 80D - 40D));
							if(ent instanceof PrimedLTNT tnt) {
								tnt.setFuse(120);
							}
							if(ent instanceof LivingPrimedLTNT tnt) {
								tnt.setTNTFuse(120);
							}
							level.spawnEntity(ent);
						}
					}
				}
			}
		}
	}
	
	public static int getTopBlock(World level, double x, double z, boolean ignoreLeaves) {
		if(!level.isClient) {
			boolean blockFound = false;
			int y = 0;
			for(int offY = level.getTopY(); offY >= level.getBottomY(); offY--) {	
				BlockPos pos = new BlockPos(MathHelper.floor(x), offY, MathHelper.floor(z));
				BlockPos posUp = new BlockPos(MathHelper.floor(x), offY + 1, MathHelper.floor(z));
				BlockState state = level.getBlockState(pos);
				BlockState stateUp = level.getBlockState(posUp);				
				if(state.getBlock().getBlastResistance() < 200 && stateUp.getBlock().getBlastResistance() < 200 && !blockFound) {
					if(ignoreLeaves) {
						if(state.isFullCube(level, pos) && !stateUp.isFullCube(level, posUp) && !state.isIn(BlockTags.LEAVES)) {
							blockFound = true;
							y = offY;
						}	
					} else {
						if(state.isFullCube(level, pos) && !stateUp.isFullCube(level, posUp)) {
							blockFound = true;
							y = offY;
						}	
					}
				}
			}
			return y;
		} else {
			return 0;
		}
	}
}

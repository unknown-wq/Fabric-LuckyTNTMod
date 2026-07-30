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
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.network.protocol.game.ClientboundLevelChunkWithLightPacket;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.core.Holder;
import net.minecraft.tags.BlockTags;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.level.chunk.PalettedContainer;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.entity.EntityTypes;

public class LevelEvents {

	public static void onLevelUpdate(ServerLevel event) {
		Level level = event;
		
		List<? extends Player> players = level.players();
		LevelVariables variables = LevelVariables.get(level);
		if(level.dimension() == Level.OVERWORLD) {
			if(level instanceof ServerLevel sLevel) {
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
				variables.syncIfChanged(sLevel);
			}
			//the biome registry and the two biome holders do not depend on the player, so they are resolved once
			//per level tick instead of once per player per tick inside the loop below
			Registry<Biome> biomeRegistry = variables != null && (variables.iceAgeTime > 0 || variables.heatDeathTime > 0) ? level.registryAccess().lookupOrThrow(Registries.BIOME) : null;
			Holder<Biome> snowyTaiga = biomeRegistry != null && variables.iceAgeTime > 0 ? biomeRegistry.getOrThrow(Biomes.SNOWY_TAIGA) : null;
			Holder<Biome> desert = biomeRegistry != null && variables.heatDeathTime > 0 ? biomeRegistry.getOrThrow(Biomes.DESERT) : null;
			for(Player player : players) {
				if(variables != null) {
					double x = player.getX();
					double y = player.getY();
					double z = player.getZ();
					if(variables.doomsdayTime > 0) {
						for(int count = 0; count < 6; count++) {
							Entity ent = EntityRegistry.HAILSTONE.get().create(level, EntitySpawnReason.TRIGGERED);
							ent.setPos(x + Math.random() * 100 - Math.random() * 100, y + LuckyTNTConfigValues.DROP_HEIGHT.get() / 4 + Math.random() * LuckyTNTConfigValues.DROP_HEIGHT.get() / 4, z + Math.random() * 100 - Math.random() * 100);
							level.addFreshEntity(ent);
						}
						if(Math.random() < 0.00675f * LuckyTNTConfigValues.AVERAGE_DIASTER_INTENSITY.get()) {
							LExplosiveProjectile ent = EntityRegistry.LITTLE_METEOR.get().create(level, EntitySpawnReason.TRIGGERED);
							ent.setPos(x + Math.random() * 200 - Math.random() * 200, y + LuckyTNTConfigValues.DROP_HEIGHT.get(), z + Math.random() * 200 - Math.random() * 200);
							level.addFreshEntity(ent);
						}
						if(Math.random() < 0.025f * LuckyTNTConfigValues.AVERAGE_DIASTER_INTENSITY.get()) {
							Entity ent = EntityRegistry.MINI_METEOR.get().create(level, EntitySpawnReason.TRIGGERED);
							ent.setPos(x + Math.random() * 200 - Math.random() * 200, y + LuckyTNTConfigValues.DROP_HEIGHT.get(), z + Math.random() * 200 - Math.random() * 200);
							level.addFreshEntity(ent);
						}
						if(Math.random() < 0.1f * LuckyTNTConfigValues.AVERAGE_DIASTER_INTENSITY.get()) {
							if(level instanceof ServerLevel) {
								double offX = Math.random() * 200 - Math.random() * 200;
								double offZ = Math.random() * 200 - Math.random() * 200;
								for(double offY = 320; offY > -64; offY--) {
									if(!level.getBlockState(new BlockPos(Mth.floor(x + offX), Mth.floor(offY), Mth.floor(z + offZ))).isAir()) {
										Entity lighting = new LightningBolt(EntityTypes.LIGHTNING_BOLT,  level);
										lighting.setPos(x + offX, offY, z + offZ);
										level.addFreshEntity(lighting);
										break;
									}
								}
							}
						}
					}
					if(variables.toxicCloudsTime > 0) {
						if(Math.random() < 0.005f * LuckyTNTConfigValues.AVERAGE_DIASTER_INTENSITY.get()) {
							BlockPos pos = new BlockPos(Mth.floor(x + Math.random() * 100 - Math.random() * 100), Mth.floor(y + Math.random() * 50 - Math.random() * 50), Mth.floor(z + Math.random() * 100 - Math.random() * 100));
							if(!level.getBlockState(pos).isCollisionShapeFullBlock(level, pos) || level.getBlockState(pos).isAir()) {
								PrimedLTNT cloud = EntityRegistry.TOXIC_CLOUD.get().create(level, EntitySpawnReason.TRIGGERED);
								cloud.setPos(pos.getX(), pos.getY(), pos.getZ());
								level.addFreshEntity(cloud);
							}						
						}
					}
					if(variables.iceAgeTime > 0) {
						Holder<Biome> biome = snowyTaiga;
						if(player instanceof ServerPlayer sPlayer) {
							for(double offX = -32; offX <= 32; offX += 16) {
								for(double offZ = -32; offZ <= 32; offZ += 16) {
									boolean needsUpdate = false;
									for(LevelChunkSection section : level.getChunk(new BlockPos(Mth.floor(x + offX), 0, Mth.floor(z + offZ))).getSections()) {
										for(int i = 0; i < 4; ++i) {
											for(int j = 0; j < 4; ++j) {
												for(int k = 0; k < 4; ++k) {
													if(section.getBiomes() instanceof PalettedContainer<Holder<Biome>> container && section.getBiomes().get(i, j, k).value() != biome.value()) {
														container.set(i, j, k, biome);
														needsUpdate = true;
													}
												}
											}
										}
									}
									if(needsUpdate) {
										sPlayer.connection.send(new ClientboundLevelChunkWithLightPacket(level.getChunkAt(new BlockPos(Mth.floor(x + offX), 0, Mth.floor(z + offZ))), level.getLightEngine(), null, null));
									}
								}
							}
						}
					}
					if(variables.heatDeathTime > 0) {
						Holder<Biome> biome = desert;
						if(player instanceof ServerPlayer sPlayer) {
							for(double offX = -32; offX <= 32; offX += 16) {
								for(double offZ = -32; offZ <= 32; offZ += 16) {
									boolean needsUpdate = false;
									for(LevelChunkSection section : level.getChunk(new BlockPos(Mth.floor(x + offX), 0, Mth.floor(z + offZ))).getSections()) {
										for(int i = 0; i < 4; ++i) {
											for(int j = 0; j < 4; ++j) {
												for(int k = 0; k < 4; ++k) {
													if(section.getBiomes() instanceof PalettedContainer<Holder<Biome>> container && section.getBiomes().get(i, j, k).value() != biome.value()) {
														container.set(i, j, k, biome);
														needsUpdate = true;
													}
												}
											}
										}
									}
									if(needsUpdate) {
										sPlayer.connection.send(new ClientboundLevelChunkWithLightPacket(level.getChunkAt(new BlockPos(Mth.floor(x + offX), 0, Mth.floor(z + offZ))), level.getLightEngine(), null, null));
									}
								}
							}
							for(int i = 0; i < 1 + (int)(0.5D * LuckyTNTConfigValues.AVERAGE_DIASTER_INTENSITY.get()); i++) {
								int offX = new Random().nextInt(60) - 30;
								int offZ = new Random().nextInt(60) - 30;
								int posY = getTopBlock(sPlayer.level(), sPlayer.getX() + offX, sPlayer.getZ() + offZ, false);
								BlockPos pos = new BlockPos(Mth.floor(sPlayer.getX() + offX), Mth.floor(posY + 1), Mth.floor(sPlayer.getZ() + offZ));
								BlockState state = sPlayer.level().getBlockState(pos);
								if((Materials.isPlant(state) || state.isAir()) && state.getBlock().getExplosionResistance() <= 100) {
									if(Math.random() > 0.1D) {
										BlockHitResult result = new BlockHitResult(new Vec3(sPlayer.getX(), sPlayer.getY(), sPlayer.getZ()), Direction.UP, pos, false);
										BlockPlaceContext ctx = new BlockPlaceContext(sPlayer, InteractionHand.MAIN_HAND, sPlayer.getItemInHand(InteractionHand.MAIN_HAND), result);
										level.setBlock(pos, Blocks.FIRE.getStateForPlacement(ctx), 3);
									} else {
										level.setBlock(pos, Blocks.LAVA.defaultBlockState(), 3);
									}
								}
							}
							for(int i = 0; i < 1 + (int)(0.5D * LuckyTNTConfigValues.AVERAGE_DIASTER_INTENSITY.get()); i++) {
								int offX = new Random().nextInt(60) - 30;
								int offZ = new Random().nextInt(60) - 30;
								int posY = getTopBlock(sPlayer.level(), sPlayer.getX() + offX, sPlayer.getZ() + offZ, true);
								BlockPos pos = new BlockPos(Mth.floor(sPlayer.getX() + offX), posY, Mth.floor(sPlayer.getZ() + offZ));
								BlockState state = sPlayer.level().getBlockState(pos);
								if(state.is(Blocks.GRASS_BLOCK)) {
									level.setBlock(pos, Math.random() > 0.5D ? Blocks.COARSE_DIRT.defaultBlockState() : Blocks.DIRT.defaultBlockState(), 3);
								} else if(sPlayer.level().getBlockState(pos.above()).is(Blocks.WATER) && Math.random() > 0.6D) {
									level.setBlock(pos, Blocks.MAGMA_BLOCK.defaultBlockState(), 3);
								}
							}
							for(int offX = -30; offX < 30; offX += 2) {
								for(int offZ = -30; offZ < 30; offZ += 2) {
									int posY = getTopBlock(sPlayer.level(), sPlayer.getX() + offX, sPlayer.getZ() + offZ, true);
									BlockPos pos = new BlockPos(Mth.floor(sPlayer.getX() + offX), posY + 1, Mth.floor(sPlayer.getZ() + offZ));
									BlockState state = sPlayer.level().getBlockState(pos);
									if((Materials.isPlant(state) || state.isAir()) && Blocks.DEAD_BUSH.defaultBlockState().canSurvive(level, pos) && state.getBlock().getExplosionResistance() <= 100 && state.getBlock() != Blocks.DEAD_BUSH) {
										level.setBlock(pos, Blocks.DEAD_BUSH.defaultBlockState(), 3);
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
						if (!level.isClientSide() && variables.tntRainTime % i == 0) {
							Entity ent;
							int rand = new Random().nextInt(100);
							if (rand == 0) {
								ent = EntityRegistry.TNT_X20.get().create(level, EntitySpawnReason.TRIGGERED);
							} else if (rand > 4 && rand <= 6) {
								ent = EntityRegistry.FIRE_TNT.get().create(level, EntitySpawnReason.TRIGGERED);
							} else if (rand > 6 && rand <= 8) {
								ent = EntityRegistry.SNOW_TNT.get().create(level, EntitySpawnReason.TRIGGERED);
							} else if (rand > 8 && rand <= 10) {
								ent = EntityRegistry.FREEZE_TNT.get().create(level, EntitySpawnReason.TRIGGERED);
							} else if (rand > 10 && rand <= 12) {
								ent = EntityRegistry.ATTACKING_TNT.get().create(level, EntitySpawnReason.TRIGGERED);
							} else if (rand > 12 && rand <= 14) {
								ent = EntityRegistry.BIG_TNT.get().create(level, EntitySpawnReason.TRIGGERED);
							} else if (rand > 14 && rand <= 16) {
								ent = EntityRegistry.WALKING_TNT.get().create(level, EntitySpawnReason.TRIGGERED);
							} else if (rand > 16 && rand <= 18) {
								ent = EntityRegistry.NUCLEAR_WASTE_TNT.get().create(level, EntitySpawnReason.TRIGGERED);
							} else if (rand > 18 && rand <= 20) {
								ent = EntityRegistry.BOUNCING_TNT.get().create(level, EntitySpawnReason.TRIGGERED);
							} else if (rand > 20 && rand <= 22) {
								ent = EntityRegistry.FARMING_TNT.get().create(level, EntitySpawnReason.TRIGGERED);
							} else if (rand > 22 && rand <= 24) {
								ent = EntityRegistry.GROVE_TNT.get().create(level, EntitySpawnReason.TRIGGERED);
							} else if (rand > 24 && rand <= 26) {
								ent = EntityRegistry.CUBIC_TNT.get().create(level, EntitySpawnReason.TRIGGERED);
							} else if (rand > 26 && rand <= 28) {
								ent = EntityRegistry.BUTTER_TNT.get().create(level, EntitySpawnReason.TRIGGERED);
							} else if (rand > 28 && rand <= 30) {
								ent = EntityRegistry.GROVE_TNT.get().create(level, EntitySpawnReason.TRIGGERED);
							} else if (rand > 30 && rand <= 31) {
								ent = EntityRegistry.COMPACT_TNT.get().create(level, EntitySpawnReason.TRIGGERED);
							} else if (rand > 31 && rand <= 32) {
								ent = EntityRegistry.RANDOM_TNT.get().create(level, EntitySpawnReason.TRIGGERED);
							} else if (rand > 32 && rand <= 47) {
								ent = EntityRegistry.TNT_X5.get().create(level, EntitySpawnReason.TRIGGERED);
							} else {
								ent = EntityRegistry.TNT.get().create(level, EntitySpawnReason.TRIGGERED);
							}
							ent.setPos(player.getX() + (Math.random() * 80D - 40D), player.getY() + 20D + Math.random() * 10D, player.getZ() + (Math.random() * 80D - 40D));
							if(ent instanceof PrimedLTNT tnt) {
								tnt.setFuse(120);
							}
							if(ent instanceof LivingPrimedLTNT tnt) {
								tnt.setTNTFuse(120);
							}
							level.addFreshEntity(ent);
						}
					}
				}
			}
		}
	}
	
	/**
	 * Finds the highest block of a column that has a full collision shape and is not covered by another block with
	 * a full collision shape, or 0 if there is none.
	 * <p>
	 * This used to walk the whole column from {@link Level#getMaxY()} downwards, allocating two {@link BlockPos} and
	 * reading two block states per step - and every position was read twice, once as {@code pos} in step n and once
	 * as {@code posUp} in step n + 1. At 384 steps per call that is ~768 block state lookups for a value that is
	 * usually found after ~256 steps, and the heat death disaster runs 900 of these per player per server tick.
	 * <p>
	 * The condition cannot be expressed as a heightmap lookup - a full collision shape is neither implied by nor
	 * implies {@link Heightmap.Types#MOTION_BLOCKING} (that one also accepts fluids, slabs and fences, and it
	 * rejects a few blocks that do have a full collision shape, snow at 8 layers among them). A block with a full
	 * collision shape is however always non-air, so the answer can never be above
	 * {@link Heightmap.Types#WORLD_SURFACE}. The walk is therefore seeded with that instead of with the world
	 * ceiling, and the state read one block down is carried into the next step. That is ~2 lookups per call in
	 * the common case instead of ~512, for exactly the same result. If the chunk is not loaded the heightmap
	 * reports the minimum height; in that case the old full scan is used, whose first {@code getBlockState}
	 * loads the chunk just like before.
	 * @param level  the current level
	 * @param x  the x coordinate of the column
	 * @param z  the z coordinate of the column
	 * @param ignoreLeaves  whether leaves are rejected as a result
	 * @return the y coordinate of the top block, or 0 if there is none
	 */
	public static int getTopBlock(Level level, double x, double z, boolean ignoreLeaves) {
		if(!level.isClientSide()) {
			final int blockX = Mth.floor(x);
			final int blockZ = Mth.floor(z);
			final int minY = level.getMinY();
			final int maxY = level.getMaxY();
			int startY = level.getHeight(Heightmap.Types.WORLD_SURFACE, blockX, blockZ);
			if(startY <= minY || startY > maxY) {
				startY = maxY;
			}
			final BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
			final BlockPos.MutableBlockPos posUp = new BlockPos.MutableBlockPos(blockX, startY + 1, blockZ);
			BlockState stateUp = level.getBlockState(posUp);
			for(int offY = startY; offY >= minY; offY--) {
				pos.set(blockX, offY, blockZ);
				BlockState state = level.getBlockState(pos);
				if(state.getBlock().getExplosionResistance() < 200 && stateUp.getBlock().getExplosionResistance() < 200) {
					if(state.isCollisionShapeFullBlock(level, pos) && !stateUp.isCollisionShapeFullBlock(level, posUp) && (!ignoreLeaves || !state.is(BlockTags.LEAVES))) {
						return offY;
					}
				}
				//the position of this step is the position above the next one, so its state is carried over
				posUp.set(blockX, offY, blockZ);
				stateUp = state;
			}
			return 0;
		} else {
			return 0;
		}
	}
}

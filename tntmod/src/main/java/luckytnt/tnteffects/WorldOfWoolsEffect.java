package luckytnt.tnteffects;

import java.util.List;
import java.util.Random;

import org.joml.Math;
import org.joml.Vector3f;

import luckytnt.event.LevelEvents;
import luckytnt.registry.BlockRegistry;
import luckytntlib.util.IExplosiveEntity;
import luckytntlib.util.explosions.ExplosionHelper;
import luckytntlib.util.explosions.IForEachBlockExplosionEffect;
import luckytntlib.util.tnteffects.PrimedTNTEffect;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BaseCoralPlantTypeBlock;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.animal.sheep.Sheep;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.item.DyeColor;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.AABB;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.entity.EntityTypes;

public class WorldOfWoolsEffect extends PrimedTNTEffect {
	// TODO(port-26.2): DISABLED — the map-color→wool-shade classification uses ~60 yarn MapColor
	// constant names (WHITE, OFF_WHITE, IRON_GRAY, STONE_GRAY, PALE_YELLOW, EMERALD_GREEN,
	// BRIGHT_TEAL, DIAMOND_BLUE, WATER_BLUE, LAPIS_BLUE, ...) that have no verified 1:1 Mojang
	// mapping (Mojang uses SNOW / COLOR_* / TERRACOTTA_* / DEEPSLATE). The rest of the effect
	// (water/coral/seagrass/waterlogged/lava → glass & wool, rings, legs, sheep) is preserved.
	/*
	public static List<MapColor> WHITE = List.of(MapColor.WHITE, MapColor.OFF_WHITE, MapColor.TERRACOTTA_WHITE, MapColor.WHITE_GRAY);
	public static List<MapColor> LIGHT_GRAY = List.of(MapColor.IRON_GRAY, MapColor.LIGHT_BLUE_GRAY, MapColor.LIGHT_GRAY);
	public static List<MapColor> GRAY = List.of(MapColor.STONE_GRAY, MapColor.GRAY, MapColor.TERRACOTTA_CYAN, MapColor.DEEPSLATE_GRAY);
	public static List<MapColor> BLACK = List.of(MapColor.BLACK);
	public static List<MapColor> BROWN = List.of(MapColor.DIRT_BROWN, MapColor.OAK_TAN, MapColor.BROWN, MapColor.SPRUCE_BROWN, MapColor.TERRACOTTA_BLACK, MapColor.TERRACOTTA_BROWN, MapColor.TERRACOTTA_GRAY, MapColor.TERRACOTTA_LIGHT_GRAY, MapColor.RAW_IRON_PINK);
	public static List<MapColor> RED = List.of(MapColor.BRIGHT_RED, MapColor.RED, MapColor.DARK_RED, MapColor.TERRACOTTA_RED, MapColor.DULL_RED, MapColor.DARK_CRIMSON, MapColor.TERRACOTTA_PINK);
	public static List<MapColor> ORANGE = List.of(MapColor.ORANGE, MapColor.TERRACOTTA_ORANGE);
	public static List<MapColor> YELLOW = List.of(MapColor.PALE_YELLOW, MapColor.YELLOW, MapColor.GOLD, MapColor.TERRACOTTA_YELLOW);
	public static List<MapColor> LIME = List.of(MapColor.PALE_GREEN, MapColor.LIME, MapColor.EMERALD_GREEN, MapColor.LICHEN_GREEN);
	public static List<MapColor> GREEN = List.of(MapColor.DARK_GREEN, MapColor.GREEN, MapColor.TERRACOTTA_LIME, MapColor.TERRACOTTA_GREEN);
	public static List<MapColor> CYAN = List.of(MapColor.CYAN, MapColor.TEAL, MapColor.DARK_AQUA, MapColor.BRIGHT_TEAL);
	public static List<MapColor> LIGHT_BLUE = List.of(MapColor.PALE_PURPLE, MapColor.LIGHT_BLUE, MapColor.DIAMOND_BLUE);
	public static List<MapColor> BLUE = List.of(MapColor.WATER_BLUE, MapColor.BLUE, MapColor.LAPIS_BLUE, MapColor.TERRACOTTA_LIGHT_BLUE);
	public static List<MapColor> PURPLE = List.of(MapColor.PURPLE, MapColor.TERRACOTTA_BLUE, MapColor.DARK_DULL_PINK);
	public static List<MapColor> MAGENTA = List.of(MapColor.MAGENTA, MapColor.TERRACOTTA_MAGENTA, MapColor.TERRACOTTA_PURPLE, MapColor.DULL_PINK);
	public static List<MapColor> PINK = List.of(MapColor.PINK);
	*/

	@Override
	public void serverExplosion(IExplosiveEntity ent) {
		// The deferred list used to hold one Pair + BlockPos per matching block, i.e. millions of live
		// objects when this goes off in an ocean. Nothing read the world after the sweep, and the sphere
		// visits every position exactly once (so no read can observe an earlier write), which makes
		// writing straight from the callback produce the same final blocks.
		ExplosionHelper.doSphericalExplosion(ent.getLevel(), ent.getPos(), 100, new IForEachBlockExplosionEffect() {
			
			@Override
			public void doBlockExplosion(Level level, BlockPos pos, BlockState state, double distance) {
				// TODO(port-26.2): DISABLED — map-color→wool-shade classification (see field block above).
				/*
				MapColor color = state.getMapColor(level, pos);
				if(color != MapColor.CLEAR & !state.getCollisionShape(level, pos, CollisionContext.empty()).isEmpty() && state.getBlock().getExplosionResistance() <= 200) {
					if(WHITE.contains(color)) {
						blocks.add(Pair.of(pos, Blocks.WHITE_WOOL));
					} else if(LIGHT_GRAY.contains(color)) {
						blocks.add(Pair.of(pos, Blocks.LIGHT_GRAY_WOOL));
					} else if(GRAY.contains(color)) {
						blocks.add(Pair.of(pos, Blocks.GRAY_WOOL));
					} else if(BLACK.contains(color)) {
						blocks.add(Pair.of(pos, Blocks.BLACK_WOOL));
					} else if(BROWN.contains(color)) {
						blocks.add(Pair.of(pos, Blocks.BROWN_WOOL));
					} else if(RED.contains(color)) {
						blocks.add(Pair.of(pos, Blocks.RED_WOOL));
					} else if(ORANGE.contains(color)) {
						blocks.add(Pair.of(pos, Blocks.ORANGE_WOOL));
					} else if(YELLOW.contains(color)) {
						blocks.add(Pair.of(pos, Blocks.YELLOW_WOOL));
					} else if(LIME.contains(color)) {
						blocks.add(Pair.of(pos, Blocks.LIME_WOOL));
					} else if(GREEN.contains(color)) {
						blocks.add(Pair.of(pos, Blocks.WOOL.green()));
					} else if(CYAN.contains(color)) {
						blocks.add(Pair.of(pos, Blocks.CYAN_WOOL));
					} else if(LIGHT_BLUE.contains(color)) {
						blocks.add(Pair.of(pos, Blocks.LIGHT_BLUE_WOOL));
					} else if(BLUE.contains(color)) {
						blocks.add(Pair.of(pos, Blocks.BLUE_WOOL));
					} else if(PURPLE.contains(color)) {
						blocks.add(Pair.of(pos, Blocks.PURPLE_WOOL));
					} else if(MAGENTA.contains(color)) {
						blocks.add(Pair.of(pos, Blocks.MAGENTA_WOOL));
					} else if(PINK.contains(color)) {
						blocks.add(Pair.of(pos, Blocks.PINK_WOOL));
					}
				}
				*/

				// NOTE: these are deliberately still four independent ifs, not an else-if chain. A
				// waterlogged sea pickle matches both the seagrass/kelp test and the waterlogged test, and
				// the later (blue glass) write is what the original produced. Chaining them would turn
				// waterlogged sea pickles into green wool instead - a visible behaviour change.
				Block block = state.getBlock();
				if((state.is(Blocks.WATER) || state.is(Blocks.BUBBLE_COLUMN) || block instanceof BaseCoralPlantTypeBlock) && block.getExplosionResistance() <= 200) {
					level.setBlock(pos, Blocks.STAINED_GLASS.blue().defaultBlockState(), 3);
				}

				if(block == Blocks.SEAGRASS || block == Blocks.TALL_SEAGRASS || block == Blocks.KELP || block == Blocks.SEA_PICKLE || block == Blocks.KELP_PLANT) {
					level.setBlock(pos, Blocks.WOOL.green().defaultBlockState(), 3);
				}

				if(state.hasProperty(BlockStateProperties.WATERLOGGED) && state.getValue(BlockStateProperties.WATERLOGGED) && block.getExplosionResistance() <= 200) {
					level.setBlock(pos, Blocks.STAINED_GLASS.blue().defaultBlockState(), 3);
				}

				if(state.is(Blocks.LAVA) && block.getExplosionResistance() <= 200) {
					level.setBlock(pos, Blocks.STAINED_GLASS.orange().defaultBlockState(), 3);
				}
			}
		});

		// new Random() sat in the loop *condition*, so the bound was re-rolled on every iteration.
		// It is now rolled once, which is the intended "3 + rand(6) towers" semantics.
		Random random = new Random();
		int towers = 3 + random.nextInt(6);
		for(int i = 0; i < towers; i++) {
			int x = random.nextInt(151) - 75;
			int z = random.nextInt(151) - 75;

			BlockPos origin = new BlockPos(Mth.floor(ent.x() + x), Mth.floor(LevelEvents.getTopBlock(ent.getLevel(), ent.x() + x, ent.z() + z, true) + 1), Mth.floor(ent.z() + z));
			boolean xOrZ = random.nextBoolean();
			int rr = 16 + random.nextInt(11);
			Block block = Blocks.CONCRETE.red();

			for(int j = 0; j < 6; j++) {
				placeRing(ent, origin, block, rr, xOrZ);
				placeLegs(ent, origin, block, rr--, xOrZ);
				
				if(j == 0) {
					block = Blocks.CONCRETE.orange();
				} else if(j == 1) {
					block = Blocks.CONCRETE.yellow();
				} else if(j == 2) {
					block = Blocks.CONCRETE.lime();
				} else if(j == 3) {
					block = Blocks.CONCRETE.blue();
				} else if(j == 4) {
					block = Blocks.CONCRETE.purple();
				}
			}
		}
		
		// Same fix as above: the sheep count was re-rolled every iteration by the loop condition.
		Level level = ent.getLevel();
		ServerLevel sLevel = (ServerLevel)level;
		int sheepCount = 60 + random.nextInt(21);
		for(int i = 0; i <= sheepCount; i++) {
			Sheep sheep = new Sheep(EntityTypes.SHEEP, level);

			int x = random.nextInt(151) - 75;
			int z = random.nextInt(151) - 75;

			sheep.setPos(ent.x() + x, LevelEvents.getTopBlock(level, ent.x() + x, ent.z() + z, true) + 1, ent.z() + z);
			sheep.finalizeSpawn(sLevel, sLevel.getCurrentDifficultyAt(toBlockPos(ent.getPos())), EntitySpawnReason.MOB_SUMMONED, null);
			level.addFreshEntity(sheep);
		}
		
		BlockPos min = toBlockPos(ent.getPos()).offset(100, 100, 100);
		BlockPos max = toBlockPos(ent.getPos()).offset(-100, -100, -100);
		List<Sheep> list = ent.getLevel().getEntitiesOfClass(Sheep.class, new AABB(min.getX(), min.getY(), min.getZ(), max.getX(), max.getY(), max.getZ()));
		for(Sheep sheep : list) {
			sheep.setColor(randomColor());
		}
	}
	
	@Override
	public void spawnParticles(IExplosiveEntity ent) {
		for(int i = 0; i < 50; i++) {
			ent.getLevel().addParticle(new DustParticleOptions(((int)(20f*255)<<16)|((int)(20f*255)<<8)|(int)(20f*255), 1f), ent.x() + Math.random() * 2 - Math.random() * 2, ent.y() + 1D + Math.random() * 2 - Math.random() * 2, ent.z() + Math.random() * 2 - Math.random() * 2, 0, 0, 0);
		}
	}
	
	@Override
	public Block getBlock() {
		return BlockRegistry.WORLD_OF_WOOLS.get();
	}
	
	@Override
	public int getDefaultFuse(IExplosiveEntity ent) {
		return 150;
	}
	
	public DyeColor randomColor() {
		int random = new Random().nextInt(DyeColor.values().length);
		return DyeColor.values()[random];
	}
	
	public void placeRing(IExplosiveEntity ent, BlockPos origin, Block block, int radius, boolean xOrZ) {
		// Squared comparison (offX/offY are ints, so offX*offX + offY*offY is exact) and the position is
		// only built for the cells that are actually on the ring.
		Level level = ent.getLevel();
		BlockState state = block.defaultBlockState();
		int inner = radius * radius;
		int outer = (radius + 1) * (radius + 1);
		int stepX = xOrZ ? 1 : 0;
		int stepZ = xOrZ ? 0 : 1;
		for(int off = -radius - 1; off <= radius + 1; off++) {
			int off2 = off * off;
			if(off2 > outer) {
				continue;
			}
			for(int offY = 0; offY <= radius + 1; offY++) {
				int d2 = off2 + offY * offY;
				if(d2 <= inner || d2 > outer) {
					continue;
				}
				BlockPos pos = origin.offset(off * stepX, offY, off * stepZ);
				if(level.getBlockState(pos).getBlock().getExplosionResistance() <= 100) {
					level.setBlock(pos, state, 3);
				}
			}
		}
	}

	public void placeLegs(IExplosiveEntity ent, BlockPos origin, Block block, int radius, boolean xOrZ) {
		int off = radius + 1;
		if(xOrZ) {
			placeLeg(ent, origin, block, off, 0);
			placeLeg(ent, origin, block, -off, 0);
		} else {
			placeLeg(ent, origin, block, 0, off);
			placeLeg(ent, origin, block, 0, -off);
		}
	}

	private void placeLeg(IExplosiveEntity ent, BlockPos origin, Block block, int offX, int offZ) {
		Level level = ent.getLevel();
		BlockState blockState = block.defaultBlockState();
		for(int offY = -1; offY > -200; offY--) {
			BlockPos pos = origin.offset(offX, offY, offZ);
			BlockState state = level.getBlockState(pos);
			if(state.getCollisionShape(level, pos, CollisionContext.empty()).isEmpty() && state.getBlock().getExplosionResistance() <= 100) {
				level.setBlock(pos, blockState, 3);
			} else {
				break;
			}
		}
	}
}

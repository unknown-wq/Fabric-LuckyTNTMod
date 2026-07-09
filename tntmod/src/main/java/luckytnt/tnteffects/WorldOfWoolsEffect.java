package luckytnt.tnteffects;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.joml.Math;
import org.joml.Vector3f;

import com.mojang.datafixers.util.Pair;

import luckytnt.event.LevelEvents;
import luckytnt.registry.BlockRegistry;
import luckytntlib.util.IExplosiveEntity;
import luckytntlib.util.explosions.ExplosionHelper;
import luckytntlib.util.explosions.IForEachBlockExplosionEffect;
import luckytntlib.util.tnteffects.PrimedTNTEffect;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.CoralParentBlock;
import net.minecraft.block.MapColor;
import net.minecraft.block.ShapeContext;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.passive.SheepEntity;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.property.Properties;
import net.minecraft.util.DyeColor;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

public class WorldOfWoolsEffect extends PrimedTNTEffect {
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
	
	@Override
	public void serverExplosion(IExplosiveEntity ent) {
		List<Pair<BlockPos, Block>> blocks = new ArrayList<>();
		
		ExplosionHelper.doSphericalExplosion(ent.getLevel(), ent.getPos(), 100, new IForEachBlockExplosionEffect() {
			
			@Override
			public void doBlockExplosion(World level, BlockPos pos, BlockState state, double distance) {
				MapColor color = state.getMapColor(level, pos);
				if(color != MapColor.CLEAR & !state.getCollisionShape(level, pos, ShapeContext.absent()).isEmpty() && state.getBlock().getBlastResistance() <= 200) {
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
						blocks.add(Pair.of(pos, Blocks.GREEN_WOOL));
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
				
				if((state.isOf(Blocks.WATER) || state.isOf(Blocks.BUBBLE_COLUMN) || state.getBlock() instanceof CoralParentBlock) && state.getBlock().getBlastResistance() <= 200) {
					blocks.add(Pair.of(pos, Blocks.BLUE_STAINED_GLASS));
				}
				
				if(state.getBlock() == Blocks.SEAGRASS || state.getBlock() == Blocks.TALL_SEAGRASS || state.getBlock() == Blocks.KELP || state.getBlock() == Blocks.SEA_PICKLE || state.getBlock() == Blocks.KELP_PLANT) {
					blocks.add(Pair.of(pos, Blocks.GREEN_WOOL));
				}
				
				if(state.contains(Properties.WATERLOGGED) && state.get(Properties.WATERLOGGED) && state.getBlock().getBlastResistance() <= 200) {
					blocks.add(Pair.of(pos, Blocks.BLUE_STAINED_GLASS));
				}
				
				if(state.isOf(Blocks.LAVA) && state.getBlock().getBlastResistance() <= 200) {
					blocks.add(Pair.of(pos, Blocks.ORANGE_STAINED_GLASS));
				}
			}
		});
		
		for(Pair<BlockPos, Block> pair : blocks) {
			ent.getLevel().setBlockState(pair.getFirst(), pair.getSecond().getDefaultState(), 3);
		}
		
		for(int i = 0; i < 3 + new Random().nextInt(6); i++) {
			int x = new Random().nextInt(151) - 75;
			int z = new Random().nextInt(151) - 75;
			
			BlockPos origin = new BlockPos(MathHelper.floor(ent.x() + x), MathHelper.floor(LevelEvents.getTopBlock(ent.getLevel(), ent.x() + x, ent.z() + z, true) + 1), MathHelper.floor(ent.z() + z));
			boolean xOrZ = new Random().nextBoolean();
			int rr = 16 + new Random().nextInt(11);
			Block block = Blocks.RED_CONCRETE;
			
			for(int j = 0; j < 6; j++) {
				placeRing(ent, origin, block, rr, xOrZ);
				placeLegs(ent, origin, block, rr--, xOrZ);
				
				if(j == 0) {
					block = Blocks.ORANGE_CONCRETE;
				} else if(j == 1) {
					block = Blocks.YELLOW_CONCRETE;
				} else if(j == 2) {
					block = Blocks.LIME_CONCRETE;
				} else if(j == 3) {
					block = Blocks.BLUE_CONCRETE;
				} else if(j == 4) {
					block = Blocks.PURPLE_CONCRETE;
				}
			}
		}
		
		for(int i = 0; i <= 60 + new Random().nextInt(21); i++) {
			SheepEntity sheep = new SheepEntity(EntityType.SHEEP, ent.getLevel());
			
			int x = new Random().nextInt(151) - 75;
			int z = new Random().nextInt(151) - 75;
			
			sheep.setPosition(ent.x() + x, LevelEvents.getTopBlock(ent.getLevel(), ent.x() + x, ent.z() + z, true) + 1, ent.z() + z);
			sheep.initialize((ServerWorld)ent.getLevel(), ent.getLevel().getLocalDifficulty(toBlockPos(ent.getPos())), SpawnReason.MOB_SUMMONED, null);
			ent.getLevel().spawnEntity(sheep);
		}
		
		BlockPos min = toBlockPos(ent.getPos()).add(100, 100, 100);
		BlockPos max = toBlockPos(ent.getPos()).add(-100, -100, -100);
		List<SheepEntity> list = ent.getLevel().getNonSpectatingEntities(SheepEntity.class, new Box(min.getX(), min.getY(), min.getZ(), max.getX(), max.getY(), max.getZ()));
		for(SheepEntity sheep : list) {
			sheep.setColor(randomColor());
		}
	}
	
	@Override
	public void spawnParticles(IExplosiveEntity ent) {
		for(int i = 0; i < 50; i++) {
			ent.getLevel().addParticle(new DustParticleEffect(new Vector3f(20f, 20f, 20f), 1f), ent.x() + Math.random() * 2 - Math.random() * 2, ent.y() + 1D + Math.random() * 2 - Math.random() * 2, ent.z() + Math.random() * 2 - Math.random() * 2, 0, 0, 0);
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
		if(xOrZ) {
			for(int offX = -radius - 1; offX <= radius + 1; offX++) {
				for(int offY = 0; offY <= radius + 1; offY++) {
					BlockPos pos = origin.add(offX, offY, 0);
					double distance = Math.sqrt(offX * offX + offY * offY);
					if(distance > radius && distance <= (radius + 1) && ent.getLevel().getBlockState(pos).getBlock().getBlastResistance() <= 100) {
						ent.getLevel().setBlockState(pos, block.getDefaultState(), 3);
					}
				}
			}
		} else {
			for(int offZ = -radius - 1; offZ <= radius + 1; offZ++) {
				for(int offY = 0; offY <= radius + 1; offY++) {
					BlockPos pos = origin.add(0, offY, offZ);
					double distance = Math.sqrt(offZ * offZ + offY * offY);
					if(distance > radius && distance <= (radius + 1) && ent.getLevel().getBlockState(pos).getBlock().getBlastResistance() <= 100) {
						ent.getLevel().setBlockState(pos, block.getDefaultState(), 3);
					}
				}
			}
		}
	}
	
	public void placeLegs(IExplosiveEntity ent, BlockPos origin, Block block, int radius, boolean xOrZ) {
		if(xOrZ) {
			for(int offY = -1; offY > -200; offY--) {
				BlockPos pos = origin.add(radius + 1, offY, 0);
				if(ent.getLevel().getBlockState(pos).getCollisionShape(ent.getLevel(), pos, ShapeContext.absent()).isEmpty() && ent.getLevel().getBlockState(pos).getBlock().getBlastResistance() <= 100) {
					ent.getLevel().setBlockState(pos, block.getDefaultState(), 3);
				} else {
					break;
				}
			}
			
			for(int offY = -1; offY > -200; offY--) {
				BlockPos pos = origin.add(-radius - 1, offY, 0);
				if(ent.getLevel().getBlockState(pos).getCollisionShape(ent.getLevel(), pos, ShapeContext.absent()).isEmpty() && ent.getLevel().getBlockState(pos).getBlock().getBlastResistance() <= 100) {
					ent.getLevel().setBlockState(pos, block.getDefaultState(), 3);
				} else {
					break;
				}
			}
		} else {
			for(int offY = -1; offY > -200; offY--) {
				BlockPos pos = origin.add(0, offY, radius + 1);
				if(ent.getLevel().getBlockState(pos).getCollisionShape(ent.getLevel(), pos, ShapeContext.absent()).isEmpty() && ent.getLevel().getBlockState(pos).getBlock().getBlastResistance() <= 100) {
					ent.getLevel().setBlockState(pos, block.getDefaultState(), 3);
				} else {
					break;
				}
			}
			
			for(int offY = -1; offY > -200; offY--) {
				BlockPos pos = origin.add(0, offY, -radius - 1);
				if(ent.getLevel().getBlockState(pos).getCollisionShape(ent.getLevel(), pos, ShapeContext.absent()).isEmpty() && ent.getLevel().getBlockState(pos).getBlock().getBlastResistance() <= 100) {
					ent.getLevel().setBlockState(pos, block.getDefaultState(), 3);
				} else {
					break;
				}
			}
		}
	}
}

package luckytnt.feature;

import java.time.LocalDate;
import java.time.temporal.ChronoField;

import com.mojang.serialization.Codec;

import luckytnt.config.LuckyTNTConfigValues;
import net.fabricmc.fabric.api.tag.convention.v2.ConventionalBiomeTags;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.state.properties.Half;
import net.minecraft.world.level.block.state.properties.ChestType;
import net.minecraft.world.level.block.state.properties.SlabType;
import net.minecraft.world.level.block.state.properties.StairsShape;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.resources.ResourceKey;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;

public class Grave extends Feature<NoneFeatureConfiguration>{

	public static final BlockState DEFAULT_GROUND = Blocks.GRASS_BLOCK.defaultBlockState();

	//LocalDate.now() resolves the system clock and the time zone rules on every call; place() runs on the
	//parallel worldgen threads, so the seasonal check is cached and refreshed at most once a minute.
	private static volatile long seasonCheckedAt = 0L;
	private static volatile boolean seasonActive = false;

	private static boolean isSeasonActive() {
		long now = System.currentTimeMillis();
		if(now - seasonCheckedAt >= 60000L) {
			seasonActive = LocalDate.now().get(ChronoField.MONTH_OF_YEAR) == 10;
			seasonCheckedAt = now;
		}
		return seasonActive;
	}

	public BlockState slab = Blocks.STONE_BRICK_SLAB.defaultBlockState().setValue(SlabBlock.TYPE, SlabType.BOTTOM);
	public BlockState mossySlab = Blocks.MOSSY_STONE_BRICK_SLAB.defaultBlockState().setValue(SlabBlock.TYPE, SlabType.BOTTOM);
	
	public BlockState stairSouth = Blocks.STONE_BRICK_STAIRS.defaultBlockState().setValue(StairBlock.FACING, Direction.SOUTH).setValue(StairBlock.HALF, Half.BOTTOM).setValue(StairBlock.SHAPE, StairsShape.STRAIGHT);
	public BlockState mossyStairSouth = Blocks.MOSSY_STONE_BRICK_STAIRS.defaultBlockState().setValue(StairBlock.FACING, Direction.SOUTH).setValue(StairBlock.HALF, Half.BOTTOM).setValue(StairBlock.SHAPE, StairsShape.STRAIGHT);
	public BlockState stairNorth = Blocks.STONE_BRICK_STAIRS.defaultBlockState().setValue(StairBlock.FACING, Direction.NORTH).setValue(StairBlock.HALF, Half.BOTTOM).setValue(StairBlock.SHAPE, StairsShape.STRAIGHT);
	public BlockState mossyStairNorth = Blocks.MOSSY_STONE_BRICK_STAIRS.defaultBlockState().setValue(StairBlock.FACING, Direction.NORTH).setValue(StairBlock.HALF, Half.BOTTOM).setValue(StairBlock.SHAPE, StairsShape.STRAIGHT);
	
	public BlockState stairSouthTop = Blocks.STONE_BRICK_STAIRS.defaultBlockState().setValue(StairBlock.FACING, Direction.SOUTH).setValue(StairBlock.HALF, Half.TOP).setValue(StairBlock.SHAPE, StairsShape.STRAIGHT);
	public BlockState mossyStairSouthTop = Blocks.MOSSY_STONE_BRICK_STAIRS.defaultBlockState().setValue(StairBlock.FACING, Direction.SOUTH).setValue(StairBlock.HALF, Half.TOP).setValue(StairBlock.SHAPE, StairsShape.STRAIGHT);
	public BlockState stairNorthTop = Blocks.STONE_BRICK_STAIRS.defaultBlockState().setValue(StairBlock.FACING, Direction.NORTH).setValue(StairBlock.HALF, Half.TOP).setValue(StairBlock.SHAPE, StairsShape.STRAIGHT);
	public BlockState mossyStairNorthTop = Blocks.MOSSY_STONE_BRICK_STAIRS.defaultBlockState().setValue(StairBlock.FACING, Direction.NORTH).setValue(StairBlock.HALF, Half.TOP).setValue(StairBlock.SHAPE, StairsShape.STRAIGHT);

	public BlockState chestNorthLeft = Blocks.CHEST.defaultBlockState().setValue(ChestBlock.FACING, Direction.NORTH).setValue(ChestBlock.TYPE, ChestType.LEFT);
	public BlockState chestNorthRight = Blocks.CHEST.defaultBlockState().setValue(ChestBlock.FACING, Direction.NORTH).setValue(ChestBlock.TYPE, ChestType.RIGHT);
	public BlockState chestSouthLeft = Blocks.CHEST.defaultBlockState().setValue(ChestBlock.FACING, Direction.SOUTH).setValue(ChestBlock.TYPE, ChestType.LEFT);
	public BlockState chestSouthRight = Blocks.CHEST.defaultBlockState().setValue(ChestBlock.FACING, Direction.SOUTH).setValue(ChestBlock.TYPE, ChestType.RIGHT);
	
	public static final Identifier GRAVE_LOOT_1_LOCATION = Identifier.parse("luckytntmod:chests/grave_loot_1");
	public static final Identifier GRAVE_LOOT_2_LOCATION = Identifier.parse("luckytntmod:chests/grave_loot_2");
	public static final Identifier GRAVE_LOOT_RARE_LOCATION = Identifier.parse("luckytntmod:chests/grave_loot_rare");

	public static final ResourceKey<LootTable> GRAVE_LOOT_1 = ResourceKey.create(Registries.LOOT_TABLE, GRAVE_LOOT_1_LOCATION);
	public static final ResourceKey<LootTable> GRAVE_LOOT_2 = ResourceKey.create(Registries.LOOT_TABLE, GRAVE_LOOT_2_LOCATION);
	public static final ResourceKey<LootTable> GRAVE_LOOT_RARE = ResourceKey.create(Registries.LOOT_TABLE, GRAVE_LOOT_RARE_LOCATION);
	
	public Grave(Codec<NoneFeatureConfiguration> codec) {
		super(codec);
	}

	@Override
	public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> ctx) {
		if(!LuckyTNTConfigValues.SEASON_EVENTS_ALWAYS_ACTIVE.get().booleanValue() && !isSeasonActive()) {
			return false;
		}

		WorldGenLevel level = ctx.level();
		BlockPos pos = ctx.origin();
		RandomSource random = level.getRandom();
		int rand = random.nextInt(3);

		//local, not a field: Feature instances are registry singletons shared by all parallel worldgen threads
		BlockState ground = DEFAULT_GROUND;
		if(level.getBiome(pos).is(ConventionalBiomeTags.IS_MUSHROOM)) {
			ground = Blocks.MYCELIUM.defaultBlockState();
		}
		
		for(int offX = -2; offX <= 2; offX++) {
			for(int offY = -2; offY <= -1; offY++) {
				for(int offZ = -1; offZ <= 2; offZ++) {
					if(level.getBlockState(pos.offset(offX, offY, offZ)).isAir()) {
						if(offY == -2) {
							level.setBlock(pos.offset(offX, offY, offZ), Blocks.DIRT.defaultBlockState(), 3);
						} else {
							level.setBlock(pos.offset(offX, offY, offZ), ground, 3);
						}
					}
				}
			}
		}
		
		level.setBlock(pos, random.nextDouble() > 0.4D ? slab : mossySlab, 3);
		level.setBlock(pos.offset(1, 0, 0), random.nextDouble() > 0.4D ? slab : mossySlab, 3);
		level.setBlock(pos.offset(0, 0, 1), random.nextDouble() > 0.4D ? slab : mossySlab, 3);
		level.setBlock(pos.offset(1, 0, 1), random.nextDouble() > 0.4D ? slab : mossySlab, 3);
		
		level.setBlock(pos.offset(0, -1, 0), chestNorthLeft, 3);
		level.setBlock(pos.offset(1, -1, 0), chestNorthRight, 3);
		level.setBlock(pos.offset(0, -1, 1), chestSouthRight, 3);
		level.setBlock(pos.offset(1, -1, 1), chestSouthLeft, 3);
		
		if(level.getBlockEntity(pos.offset(0, -1, 0)) instanceof ChestBlockEntity && level.getBlockEntity(pos.offset(1, -1, 0)) instanceof ChestBlockEntity) {
			double d = random.nextDouble();
			ChestBlockEntity tile1 = (ChestBlockEntity)level.getBlockEntity(pos.offset(0, -1, 0));
			ChestBlockEntity tile2 = (ChestBlockEntity)level.getBlockEntity(pos.offset(1, -1, 0));
			if(d < 0.45D) {
				tile1.setLootTable(GRAVE_LOOT_1, random.nextLong());
				tile2.setLootTable(GRAVE_LOOT_1, random.nextLong());
			} else if(d >= 0.45D && d < 0.9D) {
				tile1.setLootTable(GRAVE_LOOT_2, random.nextLong());
				tile2.setLootTable(GRAVE_LOOT_2, random.nextLong());
			} else if(d >= 0.9D) {
				tile1.setLootTable(GRAVE_LOOT_RARE, random.nextLong());
				tile2.setLootTable(GRAVE_LOOT_RARE, random.nextLong());
			}
		}
		
		if(level.getBlockEntity(pos.offset(0, -1, 1)) instanceof ChestBlockEntity && level.getBlockEntity(pos.offset(1, -1, 1)) instanceof ChestBlockEntity) {
			double d = random.nextDouble();
			ChestBlockEntity tile1 = (ChestBlockEntity)level.getBlockEntity(pos.offset(0, -1, 1));
			ChestBlockEntity tile2 = (ChestBlockEntity)level.getBlockEntity(pos.offset(1, -1, 1));
			if(d < 0.45D) {
				tile1.setLootTable(GRAVE_LOOT_1, random.nextLong());
				tile2.setLootTable(GRAVE_LOOT_1, random.nextLong());
			} else if(d >= 0.45D && d < 0.9D) {
				tile1.setLootTable(GRAVE_LOOT_2, random.nextLong());
				tile2.setLootTable(GRAVE_LOOT_2, random.nextLong());
			} else if(d >= 0.9D) {
				tile1.setLootTable(GRAVE_LOOT_RARE, random.nextLong());
				tile2.setLootTable(GRAVE_LOOT_RARE, random.nextLong());
			}
		}
		
		switch(rand) {
			case 0: level.setBlock(pos.offset(-1, 0, 0), Blocks.STONE_BRICKS.defaultBlockState(), 3);
					level.setBlock(pos.offset(-1, 0, 1), Blocks.STONE_BRICKS.defaultBlockState(), 3);
					level.setBlock(pos.offset(-1, 1, 0), random.nextDouble() > 0.4D ? stairSouth : mossyStairSouth, 3);
					level.setBlock(pos.offset(-1, 1, 1), random.nextDouble() > 0.4D ? stairNorth : mossyStairNorth, 3);
					break;
					
			case 1: level.setBlock(pos.offset(-1, 0, 0), random.nextDouble() > 0.4D ? stairSouth : mossyStairSouth, 3);
					level.setBlock(pos.offset(-1, 0, 1), random.nextDouble() > 0.4D ? stairNorth : mossyStairNorth, 3);
					level.setBlock(pos.offset(-1, 1, 0), random.nextDouble() > 0.4D ? stairSouth : mossyStairSouth, 3);
					level.setBlock(pos.offset(-1, 1, 1), random.nextDouble() > 0.4D ? stairNorth : mossyStairNorth, 3);
					break;
					
			case 2: level.setBlock(pos.offset(-1, 0, 0), random.nextDouble() > 0.4D ? stairSouth : mossyStairSouth, 3);
					level.setBlock(pos.offset(-1, 0, 1), random.nextDouble() > 0.4D ? stairNorth : mossyStairNorth, 3);
					level.setBlock(pos.offset(-1, 1, 0), random.nextDouble() > 0.4D ? stairSouthTop : mossyStairSouthTop, 3);
					level.setBlock(pos.offset(-1, 1, 1), random.nextDouble() > 0.4D ? stairNorthTop : mossyStairNorthTop, 3);
					level.setBlock(pos.offset(-1, 2, 0), random.nextDouble() > 0.4D ? stairSouth : mossyStairSouth, 3);
					level.setBlock(pos.offset(-1, 2, 1), random.nextDouble() > 0.4D ? stairNorth : mossyStairNorth, 3);
					break;
				
			default:level.setBlock(pos.offset(-1, 0, 0), Blocks.STONE_BRICKS.defaultBlockState(), 3);
					level.setBlock(pos.offset(-1, 0, 1), Blocks.STONE_BRICKS.defaultBlockState(), 3);
					level.setBlock(pos.offset(-1, 1, 0), random.nextDouble() > 0.4D ? stairSouth : mossyStairSouth, 3);
					level.setBlock(pos.offset(-1, 1, 1), random.nextDouble() > 0.4D ? stairNorth : mossyStairNorth, 3);
					break;
		}
		
		return true;
	}
}

package luckytnt.feature;

import java.time.LocalDate;
import java.time.temporal.ChronoField;

import com.mojang.serialization.Codec;

import luckytnt.config.LuckyTNTConfigValues;
import net.fabricmc.fabric.api.tag.convention.v2.ConventionalBiomeTags;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.block.ChestBlock;
import net.minecraft.block.SlabBlock;
import net.minecraft.block.StairsBlock;
import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.block.enums.BlockHalf;
import net.minecraft.block.enums.ChestType;
import net.minecraft.block.enums.SlabType;
import net.minecraft.block.enums.StairShape;
import net.minecraft.loot.LootTable;
import net.minecraft.registry.RegistryKey;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.gen.feature.DefaultFeatureConfig;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.util.FeatureContext;

public class Grave extends Feature<DefaultFeatureConfig>{

	public BlockState ground = Blocks.GRASS_BLOCK.getDefaultState();
	
	public BlockState slab = Blocks.STONE_BRICK_SLAB.getDefaultState().with(SlabBlock.TYPE, SlabType.BOTTOM);
	public BlockState mossySlab = Blocks.MOSSY_STONE_BRICK_SLAB.getDefaultState().with(SlabBlock.TYPE, SlabType.BOTTOM);
	
	public BlockState stairSouth = Blocks.STONE_BRICK_STAIRS.getDefaultState().with(StairsBlock.FACING, Direction.SOUTH).with(StairsBlock.HALF, BlockHalf.BOTTOM).with(StairsBlock.SHAPE, StairShape.STRAIGHT);
	public BlockState mossyStairSouth = Blocks.MOSSY_STONE_BRICK_STAIRS.getDefaultState().with(StairsBlock.FACING, Direction.SOUTH).with(StairsBlock.HALF, BlockHalf.BOTTOM).with(StairsBlock.SHAPE, StairShape.STRAIGHT);
	public BlockState stairNorth = Blocks.STONE_BRICK_STAIRS.getDefaultState().with(StairsBlock.FACING, Direction.NORTH).with(StairsBlock.HALF, BlockHalf.BOTTOM).with(StairsBlock.SHAPE, StairShape.STRAIGHT);
	public BlockState mossyStairNorth = Blocks.MOSSY_STONE_BRICK_STAIRS.getDefaultState().with(StairsBlock.FACING, Direction.NORTH).with(StairsBlock.HALF, BlockHalf.BOTTOM).with(StairsBlock.SHAPE, StairShape.STRAIGHT);
	
	public BlockState stairSouthTop = Blocks.STONE_BRICK_STAIRS.getDefaultState().with(StairsBlock.FACING, Direction.SOUTH).with(StairsBlock.HALF, BlockHalf.TOP).with(StairsBlock.SHAPE, StairShape.STRAIGHT);
	public BlockState mossyStairSouthTop = Blocks.MOSSY_STONE_BRICK_STAIRS.getDefaultState().with(StairsBlock.FACING, Direction.SOUTH).with(StairsBlock.HALF, BlockHalf.TOP).with(StairsBlock.SHAPE, StairShape.STRAIGHT);
	public BlockState stairNorthTop = Blocks.STONE_BRICK_STAIRS.getDefaultState().with(StairsBlock.FACING, Direction.NORTH).with(StairsBlock.HALF, BlockHalf.TOP).with(StairsBlock.SHAPE, StairShape.STRAIGHT);
	public BlockState mossyStairNorthTop = Blocks.MOSSY_STONE_BRICK_STAIRS.getDefaultState().with(StairsBlock.FACING, Direction.NORTH).with(StairsBlock.HALF, BlockHalf.TOP).with(StairsBlock.SHAPE, StairShape.STRAIGHT);

	public BlockState chestNorthLeft = Blocks.CHEST.getDefaultState().with(ChestBlock.FACING, Direction.NORTH).with(ChestBlock.CHEST_TYPE, ChestType.LEFT);
	public BlockState chestNorthRight = Blocks.CHEST.getDefaultState().with(ChestBlock.FACING, Direction.NORTH).with(ChestBlock.CHEST_TYPE, ChestType.RIGHT);
	public BlockState chestSouthLeft = Blocks.CHEST.getDefaultState().with(ChestBlock.FACING, Direction.SOUTH).with(ChestBlock.CHEST_TYPE, ChestType.LEFT);
	public BlockState chestSouthRight = Blocks.CHEST.getDefaultState().with(ChestBlock.FACING, Direction.SOUTH).with(ChestBlock.CHEST_TYPE, ChestType.RIGHT);
	
	public static final Identifier GRAVE_LOOT_1_LOCATION = Identifier.fromNamespaceAndPath("luckytntmod:chests/grave_loot_1");
	public static final Identifier GRAVE_LOOT_2_LOCATION = Identifier.fromNamespaceAndPath("luckytntmod:chests/grave_loot_2");
	public static final Identifier GRAVE_LOOT_RARE_LOCATION = Identifier.fromNamespaceAndPath("luckytntmod:chests/grave_loot_rare");
	
	public static final RegistryKey<LootTable> GRAVE_LOOT_1 = RegistryKey.of(Registries.LOOT_TABLE, GRAVE_LOOT_1_LOCATION);
	public static final RegistryKey<LootTable> GRAVE_LOOT_2 = RegistryKey.of(Registries.LOOT_TABLE, GRAVE_LOOT_2_LOCATION);
	public static final RegistryKey<LootTable> GRAVE_LOOT_RARE = RegistryKey.of(Registries.LOOT_TABLE, GRAVE_LOOT_RARE_LOCATION);
	
	public Grave(Codec<DefaultFeatureConfig> codec) {
		super(codec);
	}

	@Override
	public boolean generate(FeatureContext<DefaultFeatureConfig> ctx) {
		if(!LuckyTNTConfigValues.SEASON_EVENTS_ALWAYS_ACTIVE.get().booleanValue()) {
	        int m = LocalDate.now().get(ChronoField.MONTH_OF_YEAR);
	        if(m != 10) {
	        	return false;
	        }
		}
		
		StructureWorldAccess level = ctx.level();
		BlockPos pos = ctx.getOrigin();
		Random random = ctx.level().getRandom();
		int rand = random.nextInt(3);
		
		if(level.getBiome(pos).isIn(ConventionalBiomeTags.IS_MUSHROOM)) {
			ground = Blocks.MYCELIUM.getDefaultState();
		}
		
		for(int offX = -2; offX <= 2; offX++) {
			for(int offY = -2; offY <= -1; offY++) {
				for(int offZ = -1; offZ <= 2; offZ++) {
					if(level.getBlockState(pos.add(offX, offY, offZ)).isAir()) {
						if(offY == -2) {
							level.setBlockState(pos.add(offX, offY, offZ), Blocks.DIRT.getDefaultState(), 3);
						} else {
							level.setBlockState(pos.add(offX, offY, offZ), ground, 3);
						}
					}
				}
			}
		}
		
		level.setBlockState(pos, Math.random() > 0.4D ? slab : mossySlab, 3);
		level.setBlockState(pos.add(1, 0, 0), Math.random() > 0.4D ? slab : mossySlab, 3);
		level.setBlockState(pos.add(0, 0, 1), Math.random() > 0.4D ? slab : mossySlab, 3);
		level.setBlockState(pos.add(1, 0, 1), Math.random() > 0.4D ? slab : mossySlab, 3);
		
		level.setBlockState(pos.add(0, -1, 0), chestNorthLeft, 3);
		level.setBlockState(pos.add(1, -1, 0), chestNorthRight, 3);
		level.setBlockState(pos.add(0, -1, 1), chestSouthRight, 3);
		level.setBlockState(pos.add(1, -1, 1), chestSouthLeft, 3);
		
		if(level.getBlockEntity(pos.add(0, -1, 0)) instanceof ChestBlockEntity && level.getBlockEntity(pos.add(1, -1, 0)) instanceof ChestBlockEntity) {
			double d = Math.random();
			ChestBlockEntity tile1 = (ChestBlockEntity)level.getBlockEntity(pos.add(0, -1, 0));
			ChestBlockEntity tile2 = (ChestBlockEntity)level.getBlockEntity(pos.add(1, -1, 0));
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
		
		if(level.getBlockEntity(pos.add(0, -1, 1)) instanceof ChestBlockEntity && level.getBlockEntity(pos.add(1, -1, 1)) instanceof ChestBlockEntity) {
			double d = Math.random();
			ChestBlockEntity tile1 = (ChestBlockEntity)level.getBlockEntity(pos.add(0, -1, 1));
			ChestBlockEntity tile2 = (ChestBlockEntity)level.getBlockEntity(pos.add(1, -1, 1));
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
			case 0: level.setBlockState(pos.add(-1, 0, 0), Blocks.STONE_BRICKS.getDefaultState(), 3);
					level.setBlockState(pos.add(-1, 0, 1), Blocks.STONE_BRICKS.getDefaultState(), 3);
					level.setBlockState(pos.add(-1, 1, 0), Math.random() > 0.4D ? stairSouth : mossyStairSouth, 3);
					level.setBlockState(pos.add(-1, 1, 1), Math.random() > 0.4D ? stairNorth : mossyStairNorth, 3);
					break;
					
			case 1: level.setBlockState(pos.add(-1, 0, 0), Math.random() > 0.4D ? stairSouth : mossyStairSouth, 3);
					level.setBlockState(pos.add(-1, 0, 1), Math.random() > 0.4D ? stairNorth : mossyStairNorth, 3);
					level.setBlockState(pos.add(-1, 1, 0), Math.random() > 0.4D ? stairSouth : mossyStairSouth, 3);
					level.setBlockState(pos.add(-1, 1, 1), Math.random() > 0.4D ? stairNorth : mossyStairNorth, 3);
					break;
					
			case 2: level.setBlockState(pos.add(-1, 0, 0), Math.random() > 0.4D ? stairSouth : mossyStairSouth, 3);
					level.setBlockState(pos.add(-1, 0, 1), Math.random() > 0.4D ? stairNorth : mossyStairNorth, 3);
					level.setBlockState(pos.add(-1, 1, 0), Math.random() > 0.4D ? stairSouthTop : mossyStairSouthTop, 3);
					level.setBlockState(pos.add(-1, 1, 1), Math.random() > 0.4D ? stairNorthTop : mossyStairNorthTop, 3);
					level.setBlockState(pos.add(-1, 2, 0), Math.random() > 0.4D ? stairSouth : mossyStairSouth, 3);
					level.setBlockState(pos.add(-1, 2, 1), Math.random() > 0.4D ? stairNorth : mossyStairNorth, 3);
					break;
				
			default:level.setBlockState(pos.add(-1, 0, 0), Blocks.STONE_BRICKS.getDefaultState(), 3);
					level.setBlockState(pos.add(-1, 0, 1), Blocks.STONE_BRICKS.getDefaultState(), 3);
					level.setBlockState(pos.add(-1, 1, 0), Math.random() > 0.4D ? stairSouth : mossyStairSouth, 3);
					level.setBlockState(pos.add(-1, 1, 1), Math.random() > 0.4D ? stairNorth : mossyStairNorth, 3);
					break;
		}
		
		return true;
	}
}

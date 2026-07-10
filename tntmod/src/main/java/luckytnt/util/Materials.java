package luckytnt.util;

import java.util.List;

import net.fabricmc.fabric.api.tag.convention.v2.ConventionalBlockTags;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CoralPlantBlock;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;

public class Materials {

	//WOOD
	static List<Block> WOOD = List.of(Blocks.BAMBOO_MOSAIC, Blocks.BAMBOO_MOSAIC_SLAB, Blocks.BAMBOO_MOSAIC_STAIRS, Blocks.BOOKSHELF, Blocks.CHISELED_BOOKSHELF, Blocks.CRAFTING_TABLE, Blocks.MUSHROOM_STEM, Blocks.BROWN_MUSHROOM_BLOCK, Blocks.RED_MUSHROOM_BLOCK, Blocks.CHEST, Blocks.TRAPPED_CHEST, Blocks.NOTE_BLOCK, Blocks.JUKEBOX, Blocks.SMITHING_TABLE, Blocks.LECTERN, Blocks.FLETCHING_TABLE, Blocks.COMPOSTER, Blocks.BARREL, Blocks.LOOM, Blocks.CARTOGRAPHY_TABLE);
	static List<TagKey<Block>> WOOD_TAGS = List.of(BlockTags.LOGS, BlockTags.PLANKS, BlockTags.ALL_SIGNS, BlockTags.WOODEN_TRAPDOORS, BlockTags.WOODEN_DOORS, BlockTags.WOODEN_SLABS, BlockTags.WOODEN_STAIRS, BlockTags.WOODEN_BUTTONS, BlockTags.WOODEN_PRESSURE_PLATES, BlockTags.WOODEN_FENCES, BlockTags.FENCE_GATES, BlockTags.BAMBOO_BLOCKS, BlockTags.CAMPFIRES, BlockTags.BEEHIVES, BlockTags.BANNERS);
	
	//PLANTS
	static List<Block> EXCLUDED_PLANTS = List.of(Blocks.PUMPKIN, Blocks.CARVED_PUMPKIN, Blocks.JACK_O_LANTERN, Blocks.MELON);
	static List<Block> INCLUDED_PLANTS = List.of(Blocks.CACTUS, Blocks.BAMBOO, Blocks.BAMBOO_SAPLING);
	
	//WATER_PLANTS
	static List<Block> WATER_PLANTS = List.of(Blocks.SEA_PICKLE, Blocks.KELP, Blocks.KELP_PLANT, Blocks.SEAGRASS, Blocks.TALL_SEAGRASS);
	
	//STONE
	static List<TagKey<Block>> STONE_TAGS = List.of(BlockTags.BASE_STONE_OVERWORLD, BlockTags.BASE_STONE_NETHER, ConventionalBlockTags.ORES, ConventionalBlockTags.STONES, ConventionalBlockTags.COBBLESTONES, ConventionalBlockTags.QUARTZ_ORES, BlockTags.STONE_BRICKS, BlockTags.STONE_BUTTONS, BlockTags.STONE_PRESSURE_PLATES, ConventionalBlockTags.SANDSTONE_BLOCKS, ConventionalBlockTags.SANDSTONE_SLABS, ConventionalBlockTags.SANDSTONE_STAIRS, BlockTags.WALLS, BlockTags.TERRACOTTA);
	static List<Block> STONE = List.of(Blocks.PRISMARINE, Blocks.PRISMARINE_BRICK_SLAB, Blocks.PRISMARINE_BRICK_STAIRS, Blocks.PRISMARINE_BRICKS, Blocks.PRISMARINE_SLAB, Blocks.PRISMARINE_STAIRS, Blocks.DARK_PRISMARINE, Blocks.DARK_PRISMARINE_SLAB, Blocks.DARK_PRISMARINE_STAIRS,
									   Blocks.ICE, Blocks.PACKED_ICE, Blocks.BLUE_ICE, Blocks.MAGMA_BLOCK, Blocks.OBSIDIAN, Blocks.CRYING_OBSIDIAN, Blocks.CALCITE, Blocks.DRIPSTONE_BLOCK, Blocks.SMOOTH_STONE, Blocks.SMOOTH_STONE_SLAB, Blocks.STONE_SLAB, Blocks.STONE_STAIRS, Blocks.DIORITE_SLAB, Blocks.DIORITE_STAIRS, Blocks.POLISHED_DIORITE_SLAB, Blocks.POLISHED_DIORITE_STAIRS, Blocks.ANDESITE_SLAB, Blocks.ANDESITE_STAIRS, Blocks.POLISHED_ANDESITE_SLAB, Blocks.POLISHED_ANDESITE_STAIRS, Blocks.GRANITE_SLAB, Blocks.GRANITE_STAIRS, Blocks.POLISHED_GRANITE_SLAB, Blocks.POLISHED_GRANITE_STAIRS,
									   Blocks.MUD_BRICK_SLAB, Blocks.MUD_BRICK_STAIRS, Blocks.MUD_BRICKS, Blocks.BRICKS, Blocks.BRICK_SLAB, Blocks.BRICK_STAIRS, Blocks.CRACKED_DEEPSLATE_BRICKS, Blocks.DEEPSLATE_BRICKS, Blocks.DEEPSLATE_BRICK_SLAB, Blocks.DEEPSLATE_BRICK_STAIRS, Blocks.DEEPSLATE_TILE_SLAB, Blocks.DEEPSLATE_TILES, Blocks.DEEPSLATE_TILE_STAIRS, Blocks.POLISHED_DEEPSLATE_SLAB, Blocks.CRACKED_DEEPSLATE_TILES, Blocks.CRACKED_DEEPSLATE_BRICKS, Blocks.SCULK, Blocks.SCULK_CATALYST, Blocks.SCULK_SENSOR, Blocks.SCULK_SHRIEKER, Blocks.SCULK_VEIN, Blocks.CALIBRATED_SCULK_SENSOR, 
									   Blocks.CRACKED_NETHER_BRICKS, Blocks.CRACKED_POLISHED_BLACKSTONE_BRICKS, Blocks.BLACKSTONE, Blocks.BLACKSTONE_SLAB, Blocks.BLACKSTONE_STAIRS, Blocks.CHISELED_POLISHED_BLACKSTONE, Blocks.POLISHED_BLACKSTONE, Blocks.POLISHED_BLACKSTONE_BRICK_SLAB, Blocks.POLISHED_BLACKSTONE_BRICK_STAIRS, Blocks.POLISHED_BLACKSTONE_BRICKS, Blocks.POLISHED_BLACKSTONE_SLAB, Blocks.POLISHED_BLACKSTONE_STAIRS, Blocks.GILDED_BLACKSTONE, Blocks.NETHER_BRICK_FENCE, Blocks.NETHER_BRICK_SLAB, Blocks.NETHER_BRICK_STAIRS, Blocks.NETHER_BRICKS, Blocks.NETHERRACK, Blocks.CHISELED_NETHER_BRICKS, Blocks.RED_NETHER_BRICK_SLAB, Blocks.RED_NETHER_BRICK_STAIRS, Blocks.BASALT, Blocks.POLISHED_BASALT, Blocks.SMOOTH_BASALT, Blocks.SOUL_SOIL,
									   Blocks.END_STONE, Blocks.END_STONE_BRICK_SLAB, Blocks.END_STONE_BRICK_STAIRS, Blocks.END_STONE_BRICKS, Blocks.PURPUR_BLOCK, Blocks.PURPUR_PILLAR, Blocks.PURPUR_SLAB, Blocks.PURPUR_STAIRS,
									   Blocks.CONCRETE.black(), Blocks.CONCRETE.blue(), Blocks.CONCRETE.brown(), Blocks.CONCRETE.cyan(), Blocks.CONCRETE.gray(), Blocks.CONCRETE.green(), Blocks.CONCRETE.lightBlue(), Blocks.CONCRETE.lightGray(), Blocks.CONCRETE.lime(), Blocks.CONCRETE.magenta(), Blocks.CONCRETE.orange(), Blocks.CONCRETE.pink(), Blocks.CONCRETE.purple(), Blocks.CONCRETE.red(), Blocks.CONCRETE.white(), Blocks.CONCRETE.yellow(),
									   Blocks.GLAZED_TERRACOTTA.black(), Blocks.GLAZED_TERRACOTTA.blue(), Blocks.GLAZED_TERRACOTTA.brown(), Blocks.GLAZED_TERRACOTTA.cyan(), Blocks.GLAZED_TERRACOTTA.gray(), Blocks.GLAZED_TERRACOTTA.green(), Blocks.GLAZED_TERRACOTTA.lightBlue(), Blocks.GLAZED_TERRACOTTA.lightGray(), Blocks.GLAZED_TERRACOTTA.lime(), Blocks.GLAZED_TERRACOTTA.magenta(), Blocks.GLAZED_TERRACOTTA.orange(), Blocks.GLAZED_TERRACOTTA.pink(), Blocks.GLAZED_TERRACOTTA.purple(), Blocks.GLAZED_TERRACOTTA.red(), Blocks.GLAZED_TERRACOTTA.white(), Blocks.GLAZED_TERRACOTTA.yellow(),
									   Blocks.INFESTED_CHISELED_STONE_BRICKS, Blocks.INFESTED_CRACKED_STONE_BRICKS, Blocks.INFESTED_STONE_BRICKS, Blocks.INFESTED_MOSSY_STONE_BRICKS,
									   Blocks.PISTON, Blocks.STICKY_PISTON, Blocks.OBSERVER, Blocks.DISPENSER, Blocks.DROPPER, Blocks.FURNACE, Blocks.BLAST_FURNACE, Blocks.SMOKER, Blocks.BREWING_STAND, Blocks.LODESTONE, Blocks.STONECUTTER, Blocks.GRINDSTONE, Blocks.RESPAWN_ANCHOR);
	
	public static boolean isWood(BlockState state) {
		for(TagKey<Block> tag : WOOD_TAGS) {
			if(state.is(tag)) {
				return true;
			}
		}
		return WOOD.contains(state.getBlock());
	}
	
	public static boolean isPlant(BlockState state) {
		if(state.is(BlockTags.SWORD_EFFICIENT) && !EXCLUDED_PLANTS.contains(state.getBlock())) {
			return true;
		}
		return INCLUDED_PLANTS.contains(state.getBlock());
	}
	
	public static boolean isWaterPlant(BlockState state) {
		return WATER_PLANTS.contains(state.getBlock()) || state.getBlock() instanceof CoralPlantBlock;
	}
	
	public static boolean isStone(BlockState state) {
		for(TagKey<Block> tag : STONE_TAGS) {
			if(state.is(tag)) {
				return true;
			}
		}
		return STONE.contains(state.getBlock());
	}
}

package luckytnt.feature;

import java.time.LocalDate;
import java.time.temporal.ChronoField;

import com.mojang.serialization.Codec;

import luckytnt.config.LuckyTNTConfigValues;
import luckytnt.registry.BlockRegistry;
import net.fabricmc.fabric.api.tag.convention.v2.ConventionalBiomeTags;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.SlabBlock;
import net.minecraft.block.enums.SlabType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.gen.feature.DefaultFeatureConfig;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.util.FeatureContext;

public class Altar extends Feature<DefaultFeatureConfig>{
	public BlockState ground = Blocks.GRASS_BLOCK.getDefaultState();
	
	public BlockState slab = Blocks.STONE_BRICK_SLAB.getDefaultState().with(SlabBlock.TYPE, SlabType.BOTTOM);
	public BlockState mossySlab = Blocks.MOSSY_STONE_BRICK_SLAB.getDefaultState().with(SlabBlock.TYPE, SlabType.BOTTOM);
	
	public BlockState wall = Blocks.STONE_BRICK_WALL.getDefaultState();
	public BlockState mossyWall = Blocks.MOSSY_STONE_BRICK_WALL.getDefaultState();
	
	public Altar(Codec<DefaultFeatureConfig> codec) {
		super(codec);
	}
	
	public BlockState randomBrick() {
		double random = Math.random();
		if(random < 0.5D) {
			return Blocks.STONE_BRICKS.getDefaultState();
		} else if(random >= 0.5D && random < 0.8D) {
			return Blocks.MOSSY_STONE_BRICKS.getDefaultState();
		} else if(random >= 0.8D) {
			return Blocks.CRACKED_STONE_BRICKS.getDefaultState();
		}
		
		return Blocks.STONE_BRICKS.getDefaultState();
	}
	
	public BlockState randomTNT() {
		double random = Math.random();
		if(random < 0.4D) {
			return BlockRegistry.PUMPKIN_BOMB.get().getDefaultState();
		} else if(random >= 0.4D && random < 0.8D) {
			return BlockRegistry.ZOMBIE_APOCALYPSE.get().getDefaultState();
		} else if(random >= 0.8D) {
			return BlockRegistry.GRAVEYARD_TNT.get().getDefaultState();
		}
		
		return BlockRegistry.PUMPKIN_BOMB.get().getDefaultState();
	}
	
	public BlockState randomSlab() {
		return Math.random() > 0.4D ? slab : mossySlab;
	}

	@Override
	public boolean generate(FeatureContext<DefaultFeatureConfig> ctx) {
		if(!LuckyTNTConfigValues.SEASON_EVENTS_ALWAYS_ACTIVE.get().booleanValue()) {
	        int m = LocalDate.now().get(ChronoField.MONTH_OF_YEAR);
	        if(m != 10) {
	        	return false;
	        }
		}
		
		StructureWorldAccess level = ctx.getWorld();
		BlockPos pos = ctx.getOrigin();
		
		if(level.getBiome(pos).isIn(ConventionalBiomeTags.IS_MUSHROOM)) {
			ground = Blocks.MYCELIUM.getDefaultState();
		}
		
		for(int offX = -3; offX <= 3; offX++) {
			for(int offY = -2; offY <= -1; offY++) {
				for(int offZ = -3; offZ <= 3; offZ++) {
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
		
		for(int offX = -1; offX <= 1; offX++) {
			for(int offZ = -1; offZ <= 1; offZ++) {
				level.setBlockState(pos.add(offX, 0, offZ), randomBrick(), 3);
			}
		}
		
		for(int offX = -1; offX <= 1; offX++) {
			level.setBlockState(pos.add(offX, 0, 2), randomSlab(), 3);
			level.setBlockState(pos.add(offX, 0, -2), randomSlab(), 3);
		}
		
		for(int offZ = -1; offZ <= 1; offZ++) {
			level.setBlockState(pos.add(2, 0, offZ), randomSlab(), 3);
			level.setBlockState(pos.add(-2, 0, offZ), randomSlab(), 3);
		}
		
		level.setBlockState(pos.add(1, 1, 0), randomSlab(), 3);
		level.setBlockState(pos.add(0, 1, -1), randomSlab(), 3);
		level.setBlockState(pos.add(1, 1, -1), Math.random() > 0.4D ? wall : mossyWall, 3);
		level.setBlockState(pos.add(1, 2, -1), randomSlab(), 3);
		
		for(int offY = 1; offY <= 3; offY++) {
			if(offY <= 2) {
				level.setBlockState(pos.add(1, offY, 1), Math.random() > 0.4D ? wall : mossyWall, 3);
				level.setBlockState(pos.add(-1, offY, 1), Math.random() > 0.4D ? wall : mossyWall, 3);
				level.setBlockState(pos.add(-1, offY, -1), Math.random() > 0.4D ? wall : mossyWall, 3);
			} else {
				level.setBlockState(pos.add(1, offY, 1), Math.random() > 0.4D ? wall : mossyWall, 3);
				level.setBlockState(pos.add(-1, offY, 1), randomSlab(), 3);
				level.setBlockState(pos.add(-1, offY, -1), Math.random() > 0.4D ? wall : mossyWall, 3);
				
				level.setBlockState(pos.add(0, offY, 1), randomSlab().with(SlabBlock.TYPE, SlabType.TOP), 3);
				level.setBlockState(pos.add(-1, offY, 0), randomSlab().with(SlabBlock.TYPE, SlabType.TOP), 3);
			}
		}
		
		level.setBlockState(pos.add(0, 4, 0), randomBrick(), 3);
		level.setBlockState(pos.add(1, 4, 1), randomSlab(), 3);
		level.setBlockState(pos.add(-1, 4, -1), randomSlab(), 3);
		
		level.setBlockState(pos.add(0, 1, 0), randomTNT(), 3);
		
		
		return false;
	}
}

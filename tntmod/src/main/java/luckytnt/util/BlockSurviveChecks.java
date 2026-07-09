package luckytnt.util;

import luckytnt.util.mixin.FireBlockExtension;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SnowLayerBlock;
import net.minecraft.tags.BlockTags;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.LevelReader;

public class BlockSurviveChecks {
	
	public static boolean canSnowPlaceAt(BlockState state, LevelReader reader, BlockPos pos) {
		BlockState blockstate = reader.getBlockState(pos.below());
		if (blockstate.is(BlockTags.SNOW_LAYER_CANNOT_SURVIVE_ON)) {
			return false;
		} else {
			return blockstate.is(BlockTags.SNOW_LAYER_CAN_SURVIVE_ON) ? true : Block.isFaceFull(blockstate.getCollisionShape(reader, pos.below()), Direction.UP) || blockstate.is(Blocks.SNOW) && blockstate.getValue(SnowLayerBlock.LAYERS) == 8;
		}
	}
	
	public static boolean canFirePlaceAt(BlockState state, LevelReader reader, BlockPos pos) {
        BlockPos blockpos = pos.below();
        return reader.getBlockState(blockpos).isFaceSturdy(reader, blockpos, Direction.UP) || isValidFireLocation(reader, pos);
    }
	
	private static boolean isValidFireLocation(LevelReader reader, BlockPos pos) {
        for (Direction direction : Direction.values()) {
            if (Blocks.FIRE instanceof FireBlockExtension efire && efire.canBurn(reader.getBlockState(pos.relative(direction)))) {
                return true;
            }
        }

        return false;
    }
}

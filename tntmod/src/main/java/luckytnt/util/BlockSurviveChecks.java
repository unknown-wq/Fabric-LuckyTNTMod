package luckytnt.util;

import luckytnt.util.mixin.FireBlockExtension;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SnowLayerBlock;
import net.minecraft.tags.BlockTags;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.WorldView;

public class BlockSurviveChecks {
	
	public static boolean canSnowPlaceAt(BlockState state, WorldView reader, BlockPos pos) {
		BlockState blockstate = reader.getBlockState(pos.down());
		if (blockstate.isIn(BlockTags.SNOW_LAYER_CANNOT_SURVIVE_ON)) {
			return false;
		} else {
			return blockstate.isIn(BlockTags.SNOW_LAYER_CAN_SURVIVE_ON) ? true : Block.isFaceFullSquare(blockstate.getCollisionShape(reader, pos.down()), Direction.UP) || blockstate.isOf(Blocks.SNOW) && blockstate.get(SnowLayerBlock.LAYERS) == 8;
		}
	}
	
	public static boolean canFirePlaceAt(BlockState state, WorldView reader, BlockPos pos) {
        BlockPos blockpos = pos.down();
        return reader.getBlockState(blockpos).isSideSolidFullSquare(reader, blockpos, Direction.UP) || isValidFireLocation(reader, pos);
    }
	
	private static boolean isValidFireLocation(WorldView reader, BlockPos pos) {
        for (Direction direction : Direction.values()) {
            if (Blocks.FIRE instanceof FireBlockExtension efire && efire.canBurn(reader.getBlockState(pos.offset(direction)))) {
                return true;
            }
        }

        return false;
    }
}

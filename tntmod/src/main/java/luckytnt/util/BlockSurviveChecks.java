package luckytnt.util;

import luckytnt.util.mixin.FireBlockExtension;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.SnowBlock;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.WorldView;

public class BlockSurviveChecks {
	
	public static boolean canSnowPlaceAt(BlockState state, WorldView reader, BlockPos pos) {
		BlockState blockstate = reader.getBlockState(pos.down());
		if (blockstate.isIn(BlockTags.SNOW_LAYER_CANNOT_SURVIVE_ON)) {
			return false;
		} else {
			return blockstate.isIn(BlockTags.SNOW_LAYER_CAN_SURVIVE_ON) ? true : Block.isFaceFullSquare(blockstate.getCollisionShape(reader, pos.down()), Direction.UP) || blockstate.isOf(Blocks.SNOW) && blockstate.get(SnowBlock.LAYERS) == 8;
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

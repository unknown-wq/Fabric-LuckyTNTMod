package luckytnt.block;

import luckytnt.registry.BlockRegistry;
import luckytnt.registry.EntityRegistry;
import luckytntlib.block.LTNTBlock;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class TrollTNTBlock extends LTNTBlock{
   
	public TrollTNTBlock(AbstractBlock.Settings properties) {
        super(properties, EntityRegistry.TROLL_TNT, false);
    }

	@Override
	public BlockState onBreak(World level, BlockPos pos, BlockState state, PlayerEntity player) {
    	if(level.getBlockState(pos.up()).getBlock().getBlastResistance() < 200) {
    		level.setBlockState(pos.up(), BlockRegistry.TROLL_TNT.get().getDefaultState(), 3);
    	}
    	if(level.getBlockState(pos.down()).getBlock().getBlastResistance() < 200) {
    		level.setBlockState(pos.down(), BlockRegistry.TROLL_TNT.get().getDefaultState(), 3);
    	}
    	if(level.getBlockState(pos.north()).getBlock().getBlastResistance() < 200) {
    		level.setBlockState(pos.north(), BlockRegistry.TROLL_TNT.get().getDefaultState(), 3);
    	}
    	if(level.getBlockState(pos.east()).getBlock().getBlastResistance() < 200) {
    		level.setBlockState(pos.east(), BlockRegistry.TROLL_TNT.get().getDefaultState(), 3);
    	}
    	if(level.getBlockState(pos.south()).getBlock().getBlastResistance() < 200) {
    		level.setBlockState(pos.south(), BlockRegistry.TROLL_TNT.get().getDefaultState(), 3);
    	}
    	if(level.getBlockState(pos.west()).getBlock().getBlastResistance() < 200) {
    		level.setBlockState(pos.west(), BlockRegistry.TROLL_TNT.get().getDefaultState(), 3);
    	}
    	return super.onBreak(level, pos, state, player);
    }
}

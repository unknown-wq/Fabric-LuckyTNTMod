package luckytnt.block;

import luckytnt.registry.BlockRegistry;
import luckytnt.registry.EntityRegistry;
import luckytntlib.block.LTNTBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.player.Player;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

public class TrollTNTBlock extends LTNTBlock{
   
	public TrollTNTBlock(BlockBehaviour.Properties properties) {
        super(properties, EntityRegistry.TROLL_TNT, false);
    }

	@Override
	public BlockState onBreak(Level level, BlockPos pos, BlockState state, Player player) {
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

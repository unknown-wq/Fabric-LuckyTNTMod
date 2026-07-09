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
	public BlockState playerWillDestroy(Level level, BlockPos pos, BlockState state, Player player) {
    	if(level.getBlockState(pos.above()).getBlock().getExplosionResistance() < 200) {
    		level.setBlock(pos.above(), BlockRegistry.TROLL_TNT.get().defaultBlockState(), 3);
    	}
    	if(level.getBlockState(pos.below()).getBlock().getExplosionResistance() < 200) {
    		level.setBlock(pos.below(), BlockRegistry.TROLL_TNT.get().defaultBlockState(), 3);
    	}
    	if(level.getBlockState(pos.north()).getBlock().getExplosionResistance() < 200) {
    		level.setBlock(pos.north(), BlockRegistry.TROLL_TNT.get().defaultBlockState(), 3);
    	}
    	if(level.getBlockState(pos.east()).getBlock().getExplosionResistance() < 200) {
    		level.setBlock(pos.east(), BlockRegistry.TROLL_TNT.get().defaultBlockState(), 3);
    	}
    	if(level.getBlockState(pos.south()).getBlock().getExplosionResistance() < 200) {
    		level.setBlock(pos.south(), BlockRegistry.TROLL_TNT.get().defaultBlockState(), 3);
    	}
    	if(level.getBlockState(pos.west()).getBlock().getExplosionResistance() < 200) {
    		level.setBlock(pos.west(), BlockRegistry.TROLL_TNT.get().defaultBlockState(), 3);
    	}
    	return super.playerWillDestroy(level, pos, state, player);
    }
}

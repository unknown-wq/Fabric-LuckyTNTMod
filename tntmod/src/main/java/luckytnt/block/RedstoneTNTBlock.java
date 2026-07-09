package luckytnt.block;

import luckytnt.registry.EntityRegistry;
import luckytntlib.block.LTNTBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;

public class RedstoneTNTBlock extends LTNTBlock {

	public RedstoneTNTBlock(BlockBehaviour.Properties properties) {
		super(properties, EntityRegistry.REDSTONE_TNT, true);
	}
	
    @Override
    public void onPlace(BlockState state, Level world, BlockPos pos, BlockState oldState, boolean notify) {
    }
    
    @Override
    public void neighborUpdate(BlockState state, Level world, BlockPos pos, Block sourceBlock, BlockPos sourcePos, boolean notify) {
    }
    
    @Override
    public boolean emitsRedstonePower(BlockState state) {
        return true;
    }

    @Override
    public int getWeakRedstonePower(BlockState state, BlockGetter world, BlockPos pos, Direction direction) {
        return 15;
    }
}

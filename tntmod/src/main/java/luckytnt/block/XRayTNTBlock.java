package luckytnt.block;

import luckytnt.registry.EntityRegistry;
import luckytntlib.block.LTNTBlock;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;

public class XRayTNTBlock extends LTNTBlock{

	public XRayTNTBlock(AbstractBlock.Settings properties) {
		super(properties, EntityRegistry.XRAY_TNT, true);
	}
	
    @Override
    public VoxelShape getCameraCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
    	return VoxelShapes.empty();
    }
    
    @Override
    public float getAmbientOcclusionLightLevel(BlockState state, BlockView world, BlockPos pos) {
    	return 1f;
    }
    
    @Override
    public boolean isTransparent(BlockState state, BlockView world, BlockPos pos) {
    	return true;
    }
    
    @Override
    public boolean isSideInvisible(BlockState state, BlockState stateFrom, Direction direction) {
    	return stateFrom.isOf(this) ? true : super.isSideInvisible(state, stateFrom, direction);
    }
}

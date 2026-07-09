package luckytnt.block;

import luckytnt.registry.EntityRegistry;
import luckytntlib.block.LTNTBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.level.BlockGetter;

public class XRayTNTBlock extends LTNTBlock{

	public XRayTNTBlock(BlockBehaviour.Properties properties) {
		super(properties, EntityRegistry.XRAY_TNT, true);
	}
	
    @Override
    public VoxelShape getCameraCollisionShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
    	return VoxelShapes.empty();
    }
    
    @Override
    public float getAmbientOcclusionLightLevel(BlockState state, BlockGetter world, BlockPos pos) {
    	return 1f;
    }
    
    @Override
    public boolean isTransparent(BlockState state, BlockGetter world, BlockPos pos) {
    	return true;
    }
    
    @Override
    public boolean isSideInvisible(BlockState state, BlockState stateFrom, Direction direction) {
    	return stateFrom.isOf(this) ? true : super.isSideInvisible(state, stateFrom, direction);
    }
}

package luckytnt.block;

import luckytnt.registry.EntityRegistry;
import luckytntlib.block.LTNTBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.level.BlockGetter;

public class XRayTNTBlock extends LTNTBlock{

	public XRayTNTBlock(BlockBehaviour.Properties properties) {
		super(properties, EntityRegistry.XRAY_TNT, true);
	}

    @Override
    public VoxelShape getVisualShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
    	return Shapes.empty();
    }

    @Override
    public float getShadeBrightness(BlockState state, BlockGetter world, BlockPos pos) {
    	return 1f;
    }

    @Override
    public boolean propagatesSkylightDown(BlockState state) {
    	return true;
    }

    @Override
    public boolean skipRendering(BlockState state, BlockState stateFrom, Direction direction) {
    	return stateFrom.is(this) ? true : super.skipRendering(state, stateFrom, direction);
    }
}

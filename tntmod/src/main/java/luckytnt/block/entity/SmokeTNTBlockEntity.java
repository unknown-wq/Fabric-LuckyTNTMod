package luckytnt.block.entity;

import luckytnt.registry.EntityRegistry;
import luckytntlib.block.entity.LTNTBlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;

public class SmokeTNTBlockEntity extends LTNTBlockEntity {
	
	public SmokeTNTBlockEntity(BlockPos pos, BlockState state) {
		super(EntityRegistry.SMOKE_TNT_BLOCK_ENTITY.get(), pos, state);
	}
}

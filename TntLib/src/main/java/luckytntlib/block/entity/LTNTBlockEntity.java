package luckytntlib.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

public class LTNTBlockEntity extends BlockEntity {

	protected CompoundTag persistentData = new CompoundTag();
	private String PERSISTENT_DATA_TAG = "PersistentData";

	public LTNTBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
	}

	@Override
	protected void saveAdditional(ValueOutput output) {
		super.saveAdditional(output);
		output.store(PERSISTENT_DATA_TAG, CompoundTag.CODEC, persistentData);
	}

	@Override
	protected void loadAdditional(ValueInput input) {
		super.loadAdditional(input);
		persistentData = input.read(PERSISTENT_DATA_TAG, CompoundTag.CODEC).orElse(new CompoundTag());
	}

	public CompoundTag getPersistentData() {
		return persistentData;
	}
}

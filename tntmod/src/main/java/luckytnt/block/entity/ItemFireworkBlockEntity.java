package luckytnt.block.entity;

import luckytnt.registry.EntityRegistry;
import luckytntlib.block.entity.LTNTBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.BlockPos;

public class ItemFireworkBlockEntity extends LTNTBlockEntity {

	public Item item;
	public ItemStack stack;
	
	public ItemFireworkBlockEntity(BlockEntityType<ItemFireworkBlockEntity> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
	}
	
	public ItemFireworkBlockEntity(BlockPos pos, BlockState state) {
		this(EntityRegistry.ITEM_FIREWORK_BLOCK_ENTITY.get(), pos, state);
	}
}

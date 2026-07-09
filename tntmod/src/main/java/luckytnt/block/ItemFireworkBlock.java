package luckytnt.block;

import org.jetbrains.annotations.Nullable;

import luckytnt.block.entity.ItemFireworkBlockEntity;
import luckytnt.entity.PrimedItemFirework;
import luckytnt.registry.EntityRegistry;
import luckytntlib.block.LTNTBlock;
import luckytntlib.entity.PrimedLTNT;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundSource;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.util.ItemActionResult;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;

public class ItemFireworkBlock extends LTNTBlock implements BlockEntityProvider {

	public ItemFireworkBlock(BlockBehaviour.Properties properties) {
		super(properties, EntityRegistry.ITEM_FIREWORK, false);
	}
	
	@Override
	public PrimedLTNT explode(Level level, boolean exploded, double x, double y, double z, @Nullable LivingEntity igniter) throws NullPointerException {
		if(TNT != null) {
			BlockEntity blockEntity = level.getBlockEntity(new BlockPos(Mth.floor(x), Mth.floor(y), Mth.floor(z)));
			PrimedItemFirework tnt = new PrimedItemFirework(EntityRegistry.ITEM_FIREWORK.get(), level);
			tnt.setFuse(40);
			tnt.setPosition(x + 0.5f, y, z + 0.5f);
			tnt.setOwner(igniter);
			if(blockEntity != null && blockEntity instanceof ItemFireworkBlockEntity block) {
				tnt.item = block.item;
				tnt.stack = block.stack;
				CompoundTag tag = tnt.getPersistentData();
				tag.putInt("itemID", block.getPersistentData().getInt("itemID"));
				tnt.setPersistentData(tag);
			}
			level.addFreshEntity(tnt);
			level.playSound(null, new BlockPos(Mth.floor(x), Mth.floor(y), Mth.floor(z)), SoundEvents.ENTITY_TNT_PRIMED, SoundSource.MASTER, 1, 1);
			if(level.getBlockState(new BlockPos(Mth.floor(x), Mth.floor(y), Mth.floor(z))).getBlock() == this) {
				level.setBlockState(new BlockPos(Mth.floor(x), Mth.floor(y), Mth.floor(z)), Blocks.AIR.getDefaultState(), 3);
			}
			return tnt;
		}
		throw new NullPointerException("No TNT entity present. Make sure it is registered before the block is registered");
	}

	@Override
	public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
		return EntityRegistry.ITEM_FIREWORK_BLOCK_ENTITY.get().instantiate(pos, state);
	}

	@Override
	public ItemActionResult onUseWithItem(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult result) {
		Item item = stack.getItem();
		if(stack != ItemStack.EMPTY && item != Items.FLINT_AND_STEEL && level.getBlockEntity(pos) != null && level.getBlockEntity(pos) instanceof ItemFireworkBlockEntity block) {
			block.item = item;
			block.stack = stack.copy();
			block.getPersistentData().putInt("itemID", Item.getRawId(item));
			if(!player.isCreative()) {
				stack.shrink(1);
			}
			player.incrementStat(Stats.USED.getOrCreateStat(item));
			return ItemActionResult.SUCCESS;
		}
		return super.onUseWithItem(stack, state, level, pos, player, hand, result);
	}
}

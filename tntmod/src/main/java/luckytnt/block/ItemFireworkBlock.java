package luckytnt.block;

import org.jetbrains.annotations.Nullable;

import luckytnt.block.entity.ItemFireworkBlockEntity;
import luckytnt.entity.PrimedItemFirework;
import luckytnt.registry.EntityRegistry;
import luckytntlib.block.LTNTBlock;
import luckytntlib.entity.PrimedLTNT;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.util.Hand;
import net.minecraft.util.ItemActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

public class ItemFireworkBlock extends LTNTBlock implements BlockEntityProvider {

	public ItemFireworkBlock(AbstractBlock.Settings properties) {
		super(properties, EntityRegistry.ITEM_FIREWORK, false);
	}
	
	@Override
	public PrimedLTNT explode(World level, boolean exploded, double x, double y, double z, @Nullable LivingEntity igniter) throws NullPointerException {
		if(TNT != null) {
			BlockEntity blockEntity = level.getBlockEntity(new BlockPos(MathHelper.floor(x), MathHelper.floor(y), MathHelper.floor(z)));
			PrimedItemFirework tnt = new PrimedItemFirework(EntityRegistry.ITEM_FIREWORK.get(), level);
			tnt.setFuse(40);
			tnt.setPosition(x + 0.5f, y, z + 0.5f);
			tnt.setOwner(igniter);
			if(blockEntity != null && blockEntity instanceof ItemFireworkBlockEntity block) {
				tnt.item = block.item;
				tnt.stack = block.stack;
				NbtCompound tag = tnt.getPersistentData();
				tag.putInt("itemID", block.getPersistentData().getInt("itemID"));
				tnt.setPersistentData(tag);
			}
			level.spawnEntity(tnt);
			level.playSound(null, new BlockPos(MathHelper.floor(x), MathHelper.floor(y), MathHelper.floor(z)), SoundEvents.ENTITY_TNT_PRIMED, SoundCategory.MASTER, 1, 1);
			if(level.getBlockState(new BlockPos(MathHelper.floor(x), MathHelper.floor(y), MathHelper.floor(z))).getBlock() == this) {
				level.setBlockState(new BlockPos(MathHelper.floor(x), MathHelper.floor(y), MathHelper.floor(z)), Blocks.AIR.getDefaultState(), 3);
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
	public ItemActionResult onUseWithItem(ItemStack stack, BlockState state, World level, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult result) {
		Item item = stack.getItem();
		if(stack != ItemStack.EMPTY && item != Items.FLINT_AND_STEEL && level.getBlockEntity(pos) != null && level.getBlockEntity(pos) instanceof ItemFireworkBlockEntity block) {
			block.item = item;
			block.stack = stack.copy();
			block.getPersistentData().putInt("itemID", Item.getRawId(item));
			if(!player.isCreative()) {
				stack.decrement(1);
			}
			player.incrementStat(Stats.USED.getOrCreateStat(item));
			return ItemActionResult.SUCCESS;
		}
		return super.onUseWithItem(stack, state, level, pos, player, hand, result);
	}
}

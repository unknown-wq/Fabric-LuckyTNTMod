package luckytnt.block;


import org.jetbrains.annotations.Nullable;

import luckytnt.registry.EntityRegistry;
import luckytnt.registry.ItemRegistry;
import luckytntlib.block.LTNTBlock;
import luckytntlib.entity.PrimedLTNT;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.HorizontalFacingBlock;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.util.Hand;
import net.minecraft.util.ItemActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

public class GotthardTunnelBlock extends LTNTBlock {
	public static final DirectionProperty FACING = HorizontalFacingBlock.FACING;
	public static final BooleanProperty STREETS = BooleanProperty.of("streets");

	public GotthardTunnelBlock(AbstractBlock.Settings properties) {
		super(properties, EntityRegistry.GOTTHARD_TUNNEL, false);
	}

	@Override
    public void appendProperties(StateManager.Builder<Block, BlockState> definition) {
    	super.appendProperties(definition);
    	definition.add(FACING);
    	definition.add(STREETS);
    }
	
	@Override
    public BlockState getPlacementState(ItemPlacementContext context) {
    	return getDefaultState().with(FACING, context.getHorizontalPlayerFacing()).with(STREETS, false);
    }
	
	@Override
	public PrimedLTNT explode(World level, boolean exploded, double x, double y, double z, @Nullable LivingEntity igniter) throws NullPointerException {
		if(TNT != null) {
			BlockState state = level.getBlockState(new BlockPos(MathHelper.floor(x), MathHelper.floor(y), MathHelper.floor(z)));
			PrimedLTNT tnt = EntityRegistry.GOTTHARD_TUNNEL.get().create(level);
			tnt.setFuse(40);
			tnt.setPosition(x + 0.5f, y, z + 0.5f);
			tnt.setOwner(igniter);
			tnt.setTNTFuse(200);
			NbtCompound tag = tnt.getPersistentData();
			if(state.contains(FACING)) {
				tag.putString("direction", state.get(FACING).getName());
			}
			if(state.contains(STREETS)) {
				tag.putBoolean("streets", state.get(STREETS));
			}
			tnt.setPersistentData(tag);
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
	public ItemActionResult onUseWithItem(ItemStack stack, BlockState state, World level, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult result) {
		ItemStack itemstack = player.getStackInHand(hand);
		if(itemstack.isOf(Items.FLINT_AND_STEEL) || itemstack.isOf(Items.FIRE_CHARGE)) {
			explode(level, false, pos.getX(), pos.getY(), pos.getZ(), player);
			Item item = itemstack.getItem();
			if (!player.isCreative()) {
				if (itemstack.isOf(Items.FLINT_AND_STEEL)) {
					itemstack.damage(1, player, LivingEntity.getSlotForHand(hand));
				} else {
					itemstack.decrement(1);
				}
			}

			player.incrementStat(Stats.USED.getOrCreateStat(item));
			return ItemActionResult.success(level.isClient);
		} else if(itemstack.isOf(ItemRegistry.CONFIGURATION_WAND.get())) {
			if(state.contains(STREETS)) {
    			if(state.get(STREETS)) {
    				level.setBlockState(pos, state.with(STREETS, false), 3);
    			} else {
    				level.setBlockState(pos, state.with(STREETS, true), 3);
    			}
    		}
    		return ItemActionResult.SUCCESS;
		} else {
			return ItemActionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
		}
	}
}

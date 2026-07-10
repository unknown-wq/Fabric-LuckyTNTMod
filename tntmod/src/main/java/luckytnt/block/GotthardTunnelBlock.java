package luckytnt.block;


import org.jetbrains.annotations.Nullable;

import luckytnt.registry.EntityRegistry;
import luckytnt.registry.ItemRegistry;
import luckytntlib.block.LTNTBlock;
import luckytntlib.entity.PrimedLTNT;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundSource;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.stats.Stats;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;

public class GotthardTunnelBlock extends LTNTBlock {
	public static final EnumProperty<Direction> FACING = HorizontalDirectionalBlock.FACING;
	public static final BooleanProperty STREETS = BooleanProperty.create("streets");

	public GotthardTunnelBlock(BlockBehaviour.Properties properties) {
		super(properties, EntityRegistry.GOTTHARD_TUNNEL, false);
	}

	@Override
    public void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> definition) {
    	super.createBlockStateDefinition(definition);
    	definition.add(FACING);
    	definition.add(STREETS);
    }

	@Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
    	return defaultBlockState().setValue(FACING, context.getHorizontalDirection()).setValue(STREETS, false);
    }

	@Override
	public PrimedLTNT explode(Level level, boolean exploded, double x, double y, double z, @Nullable LivingEntity igniter) throws NullPointerException {
		if(TNT != null) {
			BlockState state = level.getBlockState(new BlockPos(Mth.floor(x), Mth.floor(y), Mth.floor(z)));
			PrimedLTNT tnt = EntityRegistry.GOTTHARD_TUNNEL.get().create(level, EntitySpawnReason.TRIGGERED);
			tnt.setFuse(40);
			tnt.setPos(x + 0.5f, y, z + 0.5f);
			tnt.setOwner(igniter);
			tnt.setTNTFuse(200);
			CompoundTag tag = tnt.getPersistentData();
			if(state.hasProperty(FACING)) {
				tag.putString("direction", state.getValue(FACING).getName());
			}
			if(state.hasProperty(STREETS)) {
				tag.putBoolean("streets", state.getValue(STREETS));
			}
			tnt.setPersistentData(tag);
			level.addFreshEntity(tnt);
			level.playSound(null, new BlockPos(Mth.floor(x), Mth.floor(y), Mth.floor(z)), SoundEvents.TNT_PRIMED, SoundSource.MASTER, 1, 1);
			if(level.getBlockState(new BlockPos(Mth.floor(x), Mth.floor(y), Mth.floor(z))).getBlock() == this) {
				level.setBlock(new BlockPos(Mth.floor(x), Mth.floor(y), Mth.floor(z)), Blocks.AIR.defaultBlockState(), 3);
			}
			return tnt;
		}
		throw new NullPointerException("No TNT entity present. Make sure it is registered before the block is registered");
	}

	@Override
	public InteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult result) {
		ItemStack itemstack = player.getItemInHand(hand);
		if(itemstack.is(Items.FLINT_AND_STEEL) || itemstack.is(Items.FIRE_CHARGE)) {
			explode(level, false, pos.getX(), pos.getY(), pos.getZ(), player);
			Item item = itemstack.getItem();
			if (!player.isCreative()) {
				if (itemstack.is(Items.FLINT_AND_STEEL)) {
					itemstack.hurtAndBreak(1, player, hand);
				} else {
					itemstack.shrink(1);
				}
			}

			player.awardStat(Stats.ITEM_USED.get(item));
			return InteractionResult.SUCCESS;
		} else if(itemstack.is(ItemRegistry.CONFIGURATION_WAND.get())) {
			if(state.hasProperty(STREETS)) {
    			if(state.getValue(STREETS)) {
    				level.setBlock(pos, state.setValue(STREETS, false), 3);
    			} else {
    				level.setBlock(pos, state.setValue(STREETS, true), 3);
    			}
    		}
    		return InteractionResult.SUCCESS;
		} else {
			return InteractionResult.TRY_WITH_EMPTY_HAND;
		}
	}
}

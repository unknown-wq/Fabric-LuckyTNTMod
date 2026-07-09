package luckytnt.block;

import org.jetbrains.annotations.Nullable;

import luckytnt.registry.EntityRegistry;
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
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.util.BlockRotation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;

public class TunnelingTNTBlock extends LTNTBlock{
	
	public static final EnumProperty FACING = HorizontalDirectionalBlock.FACING;    
	
	public TunnelingTNTBlock(BlockBehaviour.Properties properties) {
		super(properties, EntityRegistry.TUNNELING_TNT, true);
	}
	
    @Override
    public BlockState rotate(BlockState state, BlockRotation rotation) {  	
    	return state.setValue(FACING, rotation.rotate(state.get(FACING)));
    }
    
    @Override
    public BlockState getPlacementState(BlockPlaceContext context) {
    	return getDefaultState().setValue(FACING, context.getHorizontalPlayerFacing());
    }

    @Override
    public void appendProperties(StateDefinition.Builder<Block, BlockState> definition) {
    	super.appendProperties(definition);
    	definition.add(FACING);
    }
    
	@Override
	public InteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult result) {
		ItemStack itemstack = player.getItemInHand(hand);
		if (!itemstack.is(Items.FLINT_AND_STEEL) && !itemstack.is(Items.FIRE_CHARGE)) {
			return super.useItemOn(stack, state, level, pos, player, hand, result);
		} else {
			explode(level, false, pos.getX(), pos.getY(), pos.getZ(), player);
			Item item = itemstack.getItem();
			if (!player.isCreative()) {
				if (itemstack.is(Items.FLINT_AND_STEEL)) {
					itemstack.damage(1, player, LivingEntity.getSlotForHand(hand));
				} else {
					itemstack.shrink(1);
				}
			}

			player.incrementStat(Stats.USED.getOrCreateStat(item));
			return InteractionResult.success(level.isClientSide);
		}
	}

    @Override
    public PrimedLTNT explode(Level level, boolean exploded, double x, double y, double z, @Nullable LivingEntity igniter) throws NullPointerException {
		if(TNT != null) {
			PrimedLTNT tnt = TNT.get().create(level);
			tnt.setFuse(exploded && randomizedFuseUponExploded() ? tnt.getEffect().getDefaultFuse(tnt) / 8 + random.nextInt(Mth.clamp(tnt.getEffect().getDefaultFuse(tnt) / 4, 1, Integer.MAX_VALUE)) : tnt.getEffect().getDefaultFuse(tnt));
			tnt.setPos(x + 0.5f, y, z + 0.5f);
			tnt.setOwner(igniter);
			CompoundTag tag = tnt.getPersistentData();
			tag.putString("direction", level.getBlockState(new BlockPos(Mth.floor(x), Mth.floor(y), Mth.floor(z))).getBlock() instanceof TunnelingTNTBlock ? level.getBlockState(new BlockPos(Mth.floor(x), Mth.floor(y), Mth.floor(z))).get(FACING).getName() : "east");
			tnt.setPersistentData(tag);
			level.addFreshEntity(tnt);
			level.playSound(null, new BlockPos(Mth.floor(x), Mth.floor(y), Mth.floor(z)), SoundEvents.ENTITY_TNT_PRIMED, SoundSource.MASTER, 1, 1);
			if(level.getBlockState(new BlockPos(Mth.floor(x), Mth.floor(y), Mth.floor(z))).getBlock() == this) {
				level.setBlock(new BlockPos(Mth.floor(x), Mth.floor(y), Mth.floor(z)), Blocks.AIR.defaultBlockState(), 3);
			}
			return tnt;
		}
		throw new NullPointerException("No TNT entity present. Make sure it is registered before the block is registered");
    }
}

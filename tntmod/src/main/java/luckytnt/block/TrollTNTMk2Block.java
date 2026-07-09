package luckytnt.block;

import org.jetbrains.annotations.Nullable;

import luckytnt.registry.BlockRegistry;
import luckytnt.registry.EntityRegistry;
import luckytntlib.block.LTNTBlock;
import luckytntlib.entity.PrimedLTNT;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.PiglinBrain;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.util.Hand;
import net.minecraft.util.ItemActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;

public class TrollTNTMk2Block extends LTNTBlock{

	public TrollTNTMk2Block(AbstractBlock.Settings properties) {
		super(properties, EntityRegistry.TROLL_TNT_MK2, false);
	}
	
	@Override
	public void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState, boolean notify) {
		if (oldState.isOf(state.getBlock())) {
			return;
		}
		if (world.isReceivingRedstonePower(pos)) {
			placeSurroundingBlocks(world, pos.getX(), pos.getY(), pos.getZ());
			world.removeBlock(pos, false);
		}
	}

	@Override
	public void neighborUpdate(BlockState state, World world, BlockPos pos, Block sourceBlock, BlockPos sourcePos, boolean notify) {
		if (world.isReceivingRedstonePower(pos)) {
			placeSurroundingBlocks(world, pos.getX(), pos.getY(), pos.getZ());
			world.removeBlock(pos, false);
		}
	}

	@Override
	public BlockState onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player) {
		if (!world.isClient()) {
			explode(world, false, pos.getX(), pos.getY(), pos.getZ(), null);
		}
		
		spawnBreakParticles(world, player, pos, state);
        if (state.isIn(BlockTags.GUARDED_BY_PIGLINS)) {
            PiglinBrain.onGuardedBlockInteracted(player, false);
        }
        world.emitGameEvent(GameEvent.BLOCK_DESTROY, pos, GameEvent.Emitter.of(player, state));
        return state;
	}
	
	@Nullable
	public PrimedLTNT explode(World level, boolean exploded, double x, double y, double z, @Nullable LivingEntity igniter) throws NullPointerException {
		if(TNT != null) {
			PrimedLTNT tnt = TNT.get().create(level);
			tnt.setFuse(exploded && randomizedFuseUponExploded() ? tnt.getEffect().getDefaultFuse(tnt) / 8 + random.nextInt(MathHelper.clamp(tnt.getEffect().getDefaultFuse(tnt) / 4, 1, Integer.MAX_VALUE)) : tnt.getEffect().getDefaultFuse(tnt));
			tnt.setPosition(x + 0.5f, y, z + 0.5f);
			tnt.setOwner(igniter);
			level.spawnEntity(tnt);
			level.playSound(null, new BlockPos((int)x, (int)y, (int)z), SoundEvents.ENTITY_TNT_PRIMED, SoundCategory.MASTER, 1, 1);
			if(level.getBlockState(new BlockPos((int)x, (int)y, (int)z)).getBlock() == this) {
				level.setBlockState(new BlockPos((int)x, (int)y, (int)z), Blocks.AIR.getDefaultState(), 3);
			}
			return tnt;
		}
		throw new NullPointerException("TNT entity type is null");
	}
	
	@Override
	public ItemActionResult onUseWithItem(ItemStack stack, BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult result) {
		ItemStack itemStack = player.getStackInHand(hand);
		if (itemStack.isOf(Items.FLINT_AND_STEEL) || itemStack.isOf(Items.FIRE_CHARGE)) {
			placeSurroundingBlocks(world, pos.getX(), pos.getY(), pos.getZ());
			world.setBlockState(pos, Blocks.AIR.getDefaultState(), Block.NOTIFY_ALL_AND_REDRAW);
			Item item = itemStack.getItem();
			if (!player.isCreative()) {
				if (itemStack.isOf(Items.FLINT_AND_STEEL)) {
					itemStack.damage(1, player, LivingEntity.getSlotForHand(hand));
				} else {
					itemStack.decrement(1);
				}
			}
			player.incrementStat(Stats.USED.getOrCreateStat(item));
			return ItemActionResult.success(world.isClient);
		}
		return ItemActionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
	}

	@Override
	public void onProjectileHit(World world, BlockState state, BlockHitResult hit, ProjectileEntity projectile) {
		if (!world.isClient) {
			BlockPos blockPos = hit.getBlockPos();
			if (projectile.isOnFire() && projectile.canModifyAt(world, blockPos)) {
				placeSurroundingBlocks(world, blockPos.getX(), blockPos.getY(), blockPos.getZ());
				world.removeBlock(blockPos, false);
			}
		}
	}
	
	public void placeSurroundingBlocks(World level, double x, double y, double z) {
		BlockPos pos = new BlockPos(MathHelper.floor(x), MathHelper.floor(y), MathHelper.floor(z));
    	if(level.getBlockState(pos.up()).getBlock().getBlastResistance() < 200) {
    		level.setBlockState(pos.up(), BlockRegistry.TROLL_TNT_MK2.get().getDefaultState(), 3);
    	}
    	if(level.getBlockState(pos.down()).getBlock().getBlastResistance() < 200) {
    		level.setBlockState(pos.down(), BlockRegistry.TROLL_TNT_MK2.get().getDefaultState(), 3);
    	}
    	if(level.getBlockState(pos.north()).getBlock().getBlastResistance() < 200) {
    		level.setBlockState(pos.north(), BlockRegistry.TROLL_TNT_MK2.get().getDefaultState(), 3);
    	}
    	if(level.getBlockState(pos.east()).getBlock().getBlastResistance() < 200) {
    		level.setBlockState(pos.east(), BlockRegistry.TROLL_TNT_MK2.get().getDefaultState(), 3);
    	}
    	if(level.getBlockState(pos.south()).getBlock().getBlastResistance() < 200) {
    		level.setBlockState(pos.south(), BlockRegistry.TROLL_TNT_MK2.get().getDefaultState(), 3);
    	}
    	if(level.getBlockState(pos.west()).getBlock().getBlastResistance() < 200) {
    		level.setBlockState(pos.west(), BlockRegistry.TROLL_TNT_MK2.get().getDefaultState(), 3);
    	}
	}
}

package luckytnt.block;

import org.jetbrains.annotations.Nullable;

import luckytnt.registry.BlockRegistry;
import luckytnt.registry.EntityRegistry;
import luckytntlib.block.LTNTBlock;
import luckytntlib.entity.PrimedLTNT;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.piglin.PiglinAi;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.tags.BlockTags;
import net.minecraft.sounds.SoundSource;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.util.ItemActionResult;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;

public class TrollTNTMk2Block extends LTNTBlock{

	public TrollTNTMk2Block(BlockBehaviour.Properties properties) {
		super(properties, EntityRegistry.TROLL_TNT_MK2, false);
	}
	
	@Override
	public void onBlockAdded(BlockState state, Level world, BlockPos pos, BlockState oldState, boolean notify) {
		if (oldState.isOf(state.getBlock())) {
			return;
		}
		if (world.isReceivingRedstonePower(pos)) {
			placeSurroundingBlocks(world, pos.getX(), pos.getY(), pos.getZ());
			world.removeBlock(pos, false);
		}
	}

	@Override
	public void neighborUpdate(BlockState state, Level world, BlockPos pos, Block sourceBlock, BlockPos sourcePos, boolean notify) {
		if (world.isReceivingRedstonePower(pos)) {
			placeSurroundingBlocks(world, pos.getX(), pos.getY(), pos.getZ());
			world.removeBlock(pos, false);
		}
	}

	@Override
	public BlockState onBreak(Level world, BlockPos pos, BlockState state, Player player) {
		if (!world.isClient()) {
			explode(world, false, pos.getX(), pos.getY(), pos.getZ(), null);
		}
		
		spawnBreakParticles(world, player, pos, state);
        if (state.isIn(BlockTags.GUARDED_BY_PIGLINS)) {
            PiglinAi.onGuardedBlockInteracted(player, false);
        }
        world.emitGameEvent(GameEvent.BLOCK_DESTROY, pos, GameEvent.Emitter.of(player, state));
        return state;
	}
	
	@Nullable
	public PrimedLTNT explode(Level level, boolean exploded, double x, double y, double z, @Nullable LivingEntity igniter) throws NullPointerException {
		if(TNT != null) {
			PrimedLTNT tnt = TNT.get().create(level);
			tnt.setFuse(exploded && randomizedFuseUponExploded() ? tnt.getEffect().getDefaultFuse(tnt) / 8 + random.nextInt(Mth.clamp(tnt.getEffect().getDefaultFuse(tnt) / 4, 1, Integer.MAX_VALUE)) : tnt.getEffect().getDefaultFuse(tnt));
			tnt.setPosition(x + 0.5f, y, z + 0.5f);
			tnt.setOwner(igniter);
			level.addFreshEntity(tnt);
			level.playSound(null, new BlockPos((int)x, (int)y, (int)z), SoundEvents.ENTITY_TNT_PRIMED, SoundSource.MASTER, 1, 1);
			if(level.getBlockState(new BlockPos((int)x, (int)y, (int)z)).getBlock() == this) {
				level.setBlockState(new BlockPos((int)x, (int)y, (int)z), Blocks.AIR.getDefaultState(), 3);
			}
			return tnt;
		}
		throw new NullPointerException("TNT entity type is null");
	}
	
	@Override
	public ItemActionResult onUseWithItem(ItemStack stack, BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult result) {
		ItemStack itemStack = player.getItemInHand(hand);
		if (itemStack.isOf(Items.FLINT_AND_STEEL) || itemStack.isOf(Items.FIRE_CHARGE)) {
			placeSurroundingBlocks(world, pos.getX(), pos.getY(), pos.getZ());
			world.setBlockState(pos, Blocks.AIR.getDefaultState(), Block.NOTIFY_ALL_AND_REDRAW);
			Item item = itemStack.getItem();
			if (!player.isCreative()) {
				if (itemStack.isOf(Items.FLINT_AND_STEEL)) {
					itemStack.damage(1, player, LivingEntity.getSlotForHand(hand));
				} else {
					itemStack.shrink(1);
				}
			}
			player.incrementStat(Stats.USED.getOrCreateStat(item));
			return ItemActionResult.success(world.isClient);
		}
		return ItemActionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
	}

	@Override
	public void onProjectileHit(Level world, BlockState state, BlockHitResult hit, Projectile projectile) {
		if (!world.isClient) {
			BlockPos blockPos = hit.getBlockPos();
			if (projectile.isOnFire() && projectile.canModifyAt(world, blockPos)) {
				placeSurroundingBlocks(world, blockPos.getX(), blockPos.getY(), blockPos.getZ());
				world.removeBlock(blockPos, false);
			}
		}
	}
	
	public void placeSurroundingBlocks(Level level, double x, double y, double z) {
		BlockPos pos = new BlockPos(Mth.floor(x), Mth.floor(y), Mth.floor(z));
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

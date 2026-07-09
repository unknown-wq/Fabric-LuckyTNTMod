package luckytntlib.block;

import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.function.Supplier;

import org.jetbrains.annotations.Nullable;

import luckytntlib.entity.PrimedLTNT;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.TntBlock;
import net.minecraft.block.dispenser.DispenserBehavior;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.TntEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.PiglinBrain;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.loot.context.LootContextParameterSet;
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
import net.minecraft.world.explosion.Explosion;

/**
 * The {@link LTNTBlock} is an extension of the {@link TntBlock} and it spawns a {@link PrimedLTNT} instead of a {@link TntEntity}.
 * If a {@link DispenserBehavior} has been registered dispensers can also spawn the TNT.
 */
public class LTNTBlock extends TntBlock {

	@Nullable
	protected Supplier<EntityType<PrimedLTNT>> TNT;
	protected Random random = new Random();
	protected boolean randomizedFuseUponExploded = true;
	
	public LTNTBlock(AbstractBlock.Settings properties, @Nullable Supplier<EntityType<PrimedLTNT>> TNT, boolean randomizedFuseUponExploded) {
		super(properties);
		this.TNT = TNT;
		this.randomizedFuseUponExploded = randomizedFuseUponExploded;
	}
	
	@Override
	public void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState, boolean notify) {
		if (oldState.isOf(state.getBlock())) {
			return;
		}
		if (world.isReceivingRedstonePower(pos)) {
			explode(world, false, pos.getX(), pos.getY(), pos.getZ(), null);
			world.removeBlock(pos, false);
		}
	}

	@Override
	public void neighborUpdate(BlockState state, World world, BlockPos pos, Block sourceBlock, BlockPos sourcePos, boolean notify) {
		if (world.isReceivingRedstonePower(pos)) {
			explode(world, false, pos.getX(), pos.getY(), pos.getZ(), null);
			world.removeBlock(pos, false);
		}
	}

	@Override
	public BlockState onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player) {
		if (!world.isClient() && !player.isCreative() && state.get(UNSTABLE).booleanValue()) {
			explode(world, false, pos.getX(), pos.getY(), pos.getZ(), null);
		}
		
		spawnBreakParticles(world, player, pos, state);
        if (state.isIn(BlockTags.GUARDED_BY_PIGLINS)) {
            PiglinBrain.onGuardedBlockInteracted(player, false);
        }
        world.emitGameEvent(GameEvent.BLOCK_DESTROY, pos, GameEvent.Emitter.of(player, state));
        return state;
	}
	
	@Override
	public float getBlastResistance() {
		return 0f;
	}
	
	@Override
	public List<ItemStack> getDroppedStacks(BlockState state, LootContextParameterSet.Builder builder) {
		return Collections.singletonList(new ItemStack(this));
	}
	
	@Override
	public void onDestroyedByExplosion(World level, BlockPos pos, Explosion explosion) {
		if(!level.isClient()) {
			explode(level, true, pos.getX(), pos.getY(), pos.getZ(), explosion.getCausingEntity());
		}
	}
	
	/**
	 * Spawns a new {@link PrimedLTNT} held by this block
	 * @param level  the current level
	 * @param exploded  whether or not the block was destroyed by another explosion (used for randomized fuse)
	 * @param x  the x position
	 * @param y  the y position
	 * @param z  the z position
	 * @param igniter  the owner for the spawned TNT (used primarely for the {@link DamageSource})
	 * @return {@link PrimedLTNT} or null
	 * @throws NullPointerException
	 */
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
	public ItemActionResult onUseWithItem(ItemStack stack, BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
		ItemStack itemStack = player.getStackInHand(hand);
		if (itemStack.isOf(Items.FLINT_AND_STEEL) || itemStack.isOf(Items.FIRE_CHARGE)) {
			explode(world, false, pos.getX(), pos.getY(), pos.getZ(), player);
			world.setBlockState(pos, Blocks.AIR.getDefaultState(), Block.NOTIFY_ALL_AND_REDRAW);
			Item item = itemStack.getItem();
			if (!player.isCreative()) {
				if (itemStack.isOf(Items.FLINT_AND_STEEL)) {
					itemStack.damage(1, player, hand == Hand.MAIN_HAND ? EquipmentSlot.MAINHAND : EquipmentSlot.OFFHAND);
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
			Entity entity = projectile.getOwner();
			if (projectile.isOnFire() && projectile.canModifyAt(world, blockPos)) {
				explode(world, false, blockPos.getX(), blockPos.getY(), blockPos.getZ(), entity instanceof LivingEntity ? (LivingEntity) entity : null);
				world.removeBlock(blockPos, false);
			}
		}
	}
	
	public boolean randomizedFuseUponExploded() {
		return randomizedFuseUponExploded;
	}
}

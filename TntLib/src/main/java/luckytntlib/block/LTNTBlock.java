package luckytntlib.block;

import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.function.Supplier;

import org.jetbrains.annotations.Nullable;

import luckytntlib.entity.PrimedLTNT;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.PrimedTnt;
import net.minecraft.world.entity.monster.piglin.PiglinAi;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.TntBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.phys.BlockHitResult;

/**
 * The {@link LTNTBlock} is an extension of the {@link TntBlock} and it spawns a {@link PrimedLTNT} instead of a {@link PrimedTnt}.
 * If a dispense behavior has been registered dispensers can also spawn the TNT.
 */
public class LTNTBlock extends TntBlock {

	@Nullable
	protected Supplier<EntityType<PrimedLTNT>> TNT;
	protected Random random = new Random();
	protected boolean randomizedFuseUponExploded = true;

	public LTNTBlock(BlockBehaviour.Properties properties, @Nullable Supplier<EntityType<PrimedLTNT>> TNT, boolean randomizedFuseUponExploded) {
		super(properties);
		this.TNT = TNT;
		this.randomizedFuseUponExploded = randomizedFuseUponExploded;
	}

	@Override
	protected void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean movedByPiston) {
		if (oldState.is(state.getBlock())) {
			return;
		}
		if (level.hasNeighborSignal(pos)) {
			explode(level, false, pos.getX(), pos.getY(), pos.getZ(), null);
			level.removeBlock(pos, false);
		}
	}

	@Override
	protected void neighborChanged(BlockState state, Level level, BlockPos pos, Block sourceBlock, @Nullable net.minecraft.world.level.redstone.Orientation orientation, boolean movedByPiston) {
		if (level.hasNeighborSignal(pos)) {
			explode(level, false, pos.getX(), pos.getY(), pos.getZ(), null);
			level.removeBlock(pos, false);
		}
	}

	@Override
	public BlockState playerWillDestroy(Level level, BlockPos pos, BlockState state, Player player) {
		if (!level.isClientSide() && !player.getAbilities().instabuild && state.getValue(UNSTABLE)) {
			explode(level, false, pos.getX(), pos.getY(), pos.getZ(), null);
		}

		spawnDestroyParticles(level, player, pos, state);
		if (state.is(BlockTags.GUARDED_BY_PIGLINS) && level instanceof ServerLevel serverLevel) {
			PiglinAi.angerNearbyPiglins(serverLevel, player, false);
		}
		level.gameEvent(GameEvent.BLOCK_DESTROY, pos, GameEvent.Context.of(player, state));
		return state;
	}

	@Override
	public float getExplosionResistance() {
		return 0f;
	}

	@Override
	protected List<ItemStack> getDrops(BlockState state, LootParams.Builder builder) {
		return Collections.singletonList(new ItemStack(this));
	}

	@Override
	public void wasExploded(ServerLevel level, BlockPos pos, Explosion explosion) {
		explode(level, true, pos.getX(), pos.getY(), pos.getZ(), explosion.getIndirectSourceEntity());
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
	public PrimedLTNT explode(Level level, boolean exploded, double x, double y, double z, @Nullable LivingEntity igniter) throws NullPointerException {
		if(TNT != null) {
			PrimedLTNT tnt = TNT.get().create(level, EntitySpawnReason.TRIGGERED);
			tnt.setFuse(exploded && randomizedFuseUponExploded() ? tnt.getEffect().getDefaultFuse(tnt) / 8 + level.getRandom().nextInt(Mth.clamp(tnt.getEffect().getDefaultFuse(tnt) / 4, 1, Integer.MAX_VALUE)) : tnt.getEffect().getDefaultFuse(tnt));
			tnt.setPos(x + 0.5f, y, z + 0.5f);
			tnt.setOwner(igniter);
			level.addFreshEntity(tnt);
			BlockPos pos = new BlockPos((int)x, (int)y, (int)z);
			level.playSound(null, pos, SoundEvents.TNT_PRIMED, SoundSource.MASTER, 1, 1);
			if(level.getBlockState(pos).getBlock() == this) {
				level.setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
			}
			return tnt;
		}
		throw new NullPointerException("TNT entity type is null");
	}

	@Override
	protected InteractionResult useItemOn(ItemStack stack, BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
		ItemStack itemStack = player.getItemInHand(hand);
		if (itemStack.is(Items.FLINT_AND_STEEL) || itemStack.is(Items.FIRE_CHARGE)) {
			explode(world, false, pos.getX(), pos.getY(), pos.getZ(), player);
			world.setBlock(pos, Blocks.AIR.defaultBlockState(), Block.UPDATE_ALL_IMMEDIATE);
			Item item = itemStack.getItem();
			if (!player.getAbilities().instabuild) {
				if (itemStack.is(Items.FLINT_AND_STEEL)) {
					itemStack.hurtAndBreak(1, player, hand.asEquipmentSlot());
				} else {
					itemStack.consume(1, player);
				}
			}
			player.awardStat(Stats.ITEM_USED.get(item));
			return InteractionResult.SUCCESS;
		}
		return InteractionResult.TRY_WITH_EMPTY_HAND;
	}

	@Override
	protected void onProjectileHit(Level world, BlockState state, BlockHitResult hit, Projectile projectile) {
		if (world instanceof ServerLevel serverLevel) {
			BlockPos blockPos = hit.getBlockPos();
			Entity entity = projectile.getOwner();
			if (projectile.isOnFire() && projectile.mayInteract(serverLevel, blockPos)) {
				explode(world, false, blockPos.getX(), blockPos.getY(), blockPos.getZ(), entity instanceof LivingEntity ? (LivingEntity) entity : null);
				world.removeBlock(blockPos, false);
			}
		}
	}

	public boolean randomizedFuseUponExploded() {
		return randomizedFuseUponExploded;
	}
}

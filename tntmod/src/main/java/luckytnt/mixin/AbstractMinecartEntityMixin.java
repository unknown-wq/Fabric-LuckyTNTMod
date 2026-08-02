package luckytnt.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import luckytnt.registry.BlockRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.vehicle.minecart.AbstractMinecart;
import net.minecraft.world.level.block.PoweredRailBlock;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Gives the mod's blast-resistant obsidian rails their vanilla counterparts' behavior.
 * <p>
 * NOTE (port-26.2): In 26.2 minecart movement was moved out of {@code AbstractMinecart} and into the
 * {@code MinecartBehavior} implementations ({@code OldMinecartBehavior}/{@code NewMinecartBehavior}).
 * The old {@code moveOnRail} target no longer exists on {@code AbstractMinecart}. The activator-rail
 * hook is re-implemented here against the still-existing {@code AbstractMinecart.tick} +
 * {@code activateMinecart(ServerLevel,int,int,int,boolean)}. The powered-rail acceleration override
 * (old {@code injectMoveOnRail}) now lives entirely inside the behavior classes and cannot be reached
 * from an {@code AbstractMinecart} mixin — see the disabled block at the bottom of this file.
 */
@Mixin(AbstractMinecart.class)
public abstract class AbstractMinecartEntityMixin {

	/*
	 * A "this cart is not on an obsidian activator rail" result stays valid for 20 ticks while the cart does not
	 * change its block position. That bounds the cost for the overwhelmingly common case (a parked cart or a cart on
	 * vanilla rails) without a per-tick BlockPos allocation + block lookup. A cart that IS on an obsidian activator
	 * rail is still re-checked every tick, so powering/unpowering the rail keeps working immediately.
	 */
	@Unique
	private int luckytnt$lastX = Integer.MIN_VALUE;
	@Unique
	private int luckytnt$lastY = Integer.MIN_VALUE;
	@Unique
	private int luckytnt$lastZ = Integer.MIN_VALUE;
	@Unique
	private int luckytnt$recheckIn = 0;
	@Unique
	private boolean luckytnt$onObsidianActivatorRail = false;

	@Inject(method = "tick", at = @At("TAIL"))
	private void injectTick(CallbackInfo info) {
		AbstractMinecart cart = (AbstractMinecart)(Object)this;

		if (!(cart.level() instanceof ServerLevel level)) {
			return;
		}

		//cheap early-out: floored position only, no BlockPos allocation and no chunk lookup
		int x = Mth.floor(cart.getX());
		int y = Mth.floor(cart.getY());
		int z = Mth.floor(cart.getZ());
		boolean moved = x != luckytnt$lastX || y != luckytnt$lastY || z != luckytnt$lastZ;

		if (!moved && !luckytnt$onObsidianActivatorRail && luckytnt$recheckIn > 0) {
			luckytnt$recheckIn--;
			return;
		}

		luckytnt$lastX = x;
		luckytnt$lastY = y;
		luckytnt$lastZ = z;
		luckytnt$recheckIn = 20;

		BlockPos pos = cart.getCurrentBlockPosOrRailBelow();
		BlockState blockState = level.getBlockState(pos);

		luckytnt$onObsidianActivatorRail = blockState.is(BlockRegistry.OBSIDIAN_ACTIVATOR_RAIL.get());

		if (luckytnt$onObsidianActivatorRail) {
			cart.activateMinecart(level, pos.getX(), pos.getY(), pos.getZ(), blockState.getValue(PoweredRailBlock.POWERED));
		}
	}

	/*
	 * TODO(port-26.2): DISABLED — obsidian powered-rail acceleration override.
	 * The original mixin injected into AbstractMinecartEntity#moveOnRail and mutated the local
	 * powerTrack/haltTrack booleans so OBSIDIAN_POWERED_RAIL accelerated/halted carts like a vanilla
	 * powered rail. In 26.2 that logic lives in MinecartBehavior#moveAlongTrack
	 * (OldMinecartBehavior/NewMinecartBehavior) and is gated on hardcoded `state.is(Blocks.POWERED_RAIL)`
	 * checks. It is not reachable from an AbstractMinecart mixin, so this behavior is dropped for now.
	 * Reinstating it requires new mixins targeting the two MinecartBehavior implementations.
	 *
	 * @Inject(method = "moveOnRail", ...) // method no longer exists on AbstractMinecart
	 * private void injectMoveOnRail(BlockPos pos, BlockState state, CallbackInfo info,
	 *         @Local(ordinal = 0) LocalBooleanRef bl, @Local(ordinal = 1) LocalBooleanRef bl2) {
	 *     if (state.isOf(BlockRegistry.OBSIDIAN_POWERED_RAIL.get())) {
	 *         bl.set(state.get(PoweredRailBlock.POWERED));
	 *         bl2.set(!bl.get());
	 *     }
	 * }
	 */
}

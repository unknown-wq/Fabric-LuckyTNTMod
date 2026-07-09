package luckytnt.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalBooleanRef;

import luckytnt.registry.BlockRegistry;
import net.minecraft.block.BlockState;
import net.minecraft.block.PoweredRailBlock;
import net.minecraft.entity.vehicle.AbstractMinecartEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;

@Mixin(AbstractMinecartEntity.class)
public abstract class AbstractMinecartEntityMixin {

	@Shadow
	public abstract void onActivatorRail(int x, int y, int z, boolean powered);
	
	@Inject(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/vehicle/AbstractMinecartEntity;moveOnRail(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;)V", shift = At.Shift.AFTER), cancellable = true)
	private void injectTick(CallbackInfo info) {
		AbstractMinecartEntity ent = (AbstractMinecartEntity)(Object)this;
		
		int i = MathHelper.floor(ent.getX());
        int j = MathHelper.floor(ent.getY());
        int k = MathHelper.floor(ent.getZ());
        
        BlockPos blockPos = new BlockPos(i, j, k);
        BlockState blockState = ent.getWorld().getBlockState(blockPos);
        
        if (blockState.isOf(BlockRegistry.OBSIDIAN_ACTIVATOR_RAIL.get())) {
            onActivatorRail(i, j, k, blockState.get(PoweredRailBlock.POWERED));
        }
	}
	
	@Inject(method = "moveOnRail", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/AbstractRailBlock;getShapeProperty()Lnet/minecraft/state/property/Property;", shift = At.Shift.BEFORE), cancellable = true)
	private void injectMoveOnRail(BlockPos pos, BlockState state, CallbackInfo info, @Local(ordinal = 0) LocalBooleanRef bl, @Local(ordinal = 1) LocalBooleanRef bl2) {
		if (state.isOf(BlockRegistry.OBSIDIAN_POWERED_RAIL.get())) {
			bl.set(state.get(PoweredRailBlock.POWERED));
			bl2.set(!bl.get());
        }
	}
}

package luckytnt.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import luckytnt.registry.EventRegistry;
import net.minecraft.entity.LivingEntity;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {

	@Inject(method = "tick", at = @At("HEAD"), cancellable = true)
	private void tickInject(CallbackInfo info) {
		EventRegistry.LIVING_ENTITY_TICK.invoker().onLivingTick((LivingEntity)(Object)this);
	}
}

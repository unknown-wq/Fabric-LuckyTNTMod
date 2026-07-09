package luckytnt.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import luckytnt.util.mixin.HungerManagerExtension;
import net.minecraft.entity.player.HungerManager;

@Mixin(HungerManager.class)
public abstract class HungerManagerMixin implements HungerManagerExtension {

	@Shadow
    private int foodTickTimer;

	@Unique
	public void setFoodTickTimerRaw(int timer) {
		foodTickTimer = timer;
	}
}

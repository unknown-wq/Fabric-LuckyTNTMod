package luckytnt.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import luckytnt.util.mixin.HungerManagerExtension;
import net.minecraft.world.food.FoodData;

@Mixin(FoodData.class)
public abstract class HungerManagerMixin implements HungerManagerExtension {

	@Shadow
	private int tickTimer;

	@Unique
	public void setFoodTickTimerRaw(int timer) {
		tickTimer = timer;
	}
}

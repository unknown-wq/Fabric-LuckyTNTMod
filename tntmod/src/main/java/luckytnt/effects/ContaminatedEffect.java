package luckytnt.effects;

import luckytnt.util.mixin.HungerManagerExtension;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.damagesource.DamageSources;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;

public class ContaminatedEffect extends MobEffect {
	
	public ContaminatedEffect(MobEffectCategory category, int id) {
		super(category, id);
	}

	@Override
	public Component getName() {
		return Component.translatable("effect.contaminated_effect");
	}
	
	@Override
	public boolean canApplyUpdateEffect(int duration, int amplifier) {
		return true;
	}

	@Override
	public boolean applyUpdateEffect(LivingEntity entity, int amplifier) {
		MobEffectInstance instance = entity.getActiveStatusEffects().get(Holder.of(this));
		int duration = instance == null ? 0 : instance.getDuration();
		DamageSources sources = entity.level().getDamageSources();
		
		if(entity instanceof Player player && player.getHungerManager() instanceof HungerManagerExtension hunger) {
			hunger.setFoodTickTimerRaw(0);
		}
		
		int i = 40 >> duration;
		if (i > 0) {
			if(amplifier % i == 0) {
				if (entity.getHealth() > 4.0F) {
					entity.damage(sources.magic(), 1.0F);
				}
			}
		}
		return true;
	}
}

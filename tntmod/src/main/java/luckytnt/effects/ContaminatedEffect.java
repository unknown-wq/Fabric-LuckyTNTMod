package luckytnt.effects;

import luckytnt.util.mixin.HungerManagerExtension;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSources;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.text.Text;

public class ContaminatedEffect extends StatusEffect {
	
	public ContaminatedEffect(StatusEffectCategory category, int id) {
		super(category, id);
	}

	@Override
	public Text getName() {
		return Text.translatable("effect.contaminated_effect");
	}
	
	@Override
	public boolean canApplyUpdateEffect(int duration, int amplifier) {
		return true;
	}

	@Override
	public boolean applyUpdateEffect(LivingEntity entity, int amplifier) {
		StatusEffectInstance instance = entity.getActiveStatusEffects().get(RegistryEntry.of(this));
		int duration = instance == null ? 0 : instance.getDuration();
		DamageSources sources = entity.getWorld().getDamageSources();
		
		if(entity instanceof PlayerEntity player && player.getHungerManager() instanceof HungerManagerExtension hunger) {
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

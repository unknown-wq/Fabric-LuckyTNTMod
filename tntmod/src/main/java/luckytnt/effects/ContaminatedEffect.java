package luckytnt.effects;

import luckytnt.registry.EffectRegistry;
import luckytnt.util.mixin.HungerManagerExtension;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.damagesource.DamageSources;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;

public class ContaminatedEffect extends MobEffect {

	/**
	 * The registered {@link Holder.Reference} for this effect. This used to be {@code Holder.direct(this)},
	 * built fresh on every effect tick of every affected entity - and since the entity's effect map is keyed by
	 * the registry's Holder.Reference (which uses identity equality), the lookup never matched and the duration
	 * read below was always 0. Resolved lazily because the holder does not exist yet while the effect is being
	 * constructed inside EffectRegistry.
	 */
	private Holder<MobEffect> holder;

	public ContaminatedEffect(MobEffectCategory category, int id) {
		super(category, id);
	}

	private Holder<MobEffect> holder() {
		if(holder == null) {
			holder = BuiltInRegistries.MOB_EFFECT.getOrThrow(EffectRegistry.CONTAMINATED);
		}
		return holder;
	}

	@Override
	public Component getDisplayName() {
		return Component.translatable("effect.contaminated_effect");
	}

	@Override
	public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
		return true;
	}

	@Override
	public boolean applyEffectTick(ServerLevel level, LivingEntity entity, int amplifier) {
		MobEffectInstance instance = entity.getEffect(holder());
		int duration = instance == null ? 0 : instance.getDuration();
		DamageSources sources = entity.level().damageSources();

		if(entity instanceof Player player && player.getFoodData() instanceof HungerManagerExtension hunger) {
			hunger.setFoodTickTimerRaw(0);
		}

		int i = 40 >> duration;
		if (i > 0) {
			if(amplifier % i == 0) {
				if (entity.getHealth() > 4.0F) {
					entity.hurtServer(level, sources.magic(), 1.0F);
				}
			}
		}
		return true;
	}
}

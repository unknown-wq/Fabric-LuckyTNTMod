package luckytnt.registry;

import java.util.function.Supplier;

import luckytnt.LuckyTNTMod;
import luckytnt.effects.ContaminatedEffect;
import luckytnt.effects.MidasTouchEffect;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.Identifier;

public class EffectRegistry {
	
	public static final RegistryKey<StatusEffect> CONTAMINATED = RegistryKey.of(Registries.STATUS_EFFECT.getKey(), Identifier.of(LuckyTNTMod.MODID, "contaminated"));
	public static final RegistryKey<StatusEffect> MIDAS_TOUCH = RegistryKey.of(Registries.STATUS_EFFECT.getKey(), Identifier.of(LuckyTNTMod.MODID, "midas_touch"));

	public static final Supplier<StatusEffect> CONTAMINATED_EFFECT = registerEffect(() -> new ContaminatedEffect(StatusEffectCategory.HARMFUL, 0xB9C300), "contaminated");
	public static final Supplier<StatusEffect> MIDAS_TOUCH_EFFECT = registerEffect(() -> new MidasTouchEffect(StatusEffectCategory.NEUTRAL, 0xDFB93E), "midas_touch");
	
	public static Supplier<StatusEffect> registerEffect(Supplier<StatusEffect> effect, String name) {		
		StatusEffect reffect = Registry.register(Registries.STATUS_EFFECT, Identifier.of(LuckyTNTMod.MODID, name), effect.get());
		return () -> reffect;
	}
	
	public static void init() {}
}

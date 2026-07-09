package luckytnt.registry;

import java.util.function.Supplier;

import luckytnt.LuckyTNTMod;
import luckytnt.effects.ContaminatedEffect;
import luckytnt.effects.MidasTouchEffect;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.resources.Identifier;

public class EffectRegistry {
	
	public static final RegistryKey<StatusEffect> CONTAMINATED = RegistryKey.of(BuiltInRegistries.STATUS_EFFECT.getKey(), Identifier.fromNamespaceAndPath(LuckyTNTMod.MODID, "contaminated"));
	public static final RegistryKey<StatusEffect> MIDAS_TOUCH = RegistryKey.of(BuiltInRegistries.STATUS_EFFECT.getKey(), Identifier.fromNamespaceAndPath(LuckyTNTMod.MODID, "midas_touch"));

	public static final Supplier<StatusEffect> CONTAMINATED_EFFECT = registerEffect(() -> new ContaminatedEffect(StatusEffectCategory.HARMFUL, 0xB9C300), "contaminated");
	public static final Supplier<StatusEffect> MIDAS_TOUCH_EFFECT = registerEffect(() -> new MidasTouchEffect(StatusEffectCategory.NEUTRAL, 0xDFB93E), "midas_touch");
	
	public static Supplier<StatusEffect> registerEffect(Supplier<StatusEffect> effect, String name) {		
		StatusEffect reffect = Registry.register(BuiltInRegistries.STATUS_EFFECT, Identifier.fromNamespaceAndPath(LuckyTNTMod.MODID, name), effect.get());
		return () -> reffect;
	}
	
	public static void init() {}
}

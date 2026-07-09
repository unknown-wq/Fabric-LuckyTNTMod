package luckytnt.registry;

import java.util.function.Supplier;

import luckytnt.LuckyTNTMod;
import luckytnt.effects.ContaminatedEffect;
import luckytnt.effects.MidasTouchEffect;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;

public class EffectRegistry {
	
	public static final ResourceKey<MobEffect> CONTAMINATED = ResourceKey.of(BuiltInRegistries.STATUS_EFFECT.getKey(), Identifier.fromNamespaceAndPath(LuckyTNTMod.MODID, "contaminated"));
	public static final ResourceKey<MobEffect> MIDAS_TOUCH = ResourceKey.of(BuiltInRegistries.STATUS_EFFECT.getKey(), Identifier.fromNamespaceAndPath(LuckyTNTMod.MODID, "midas_touch"));

	public static final Supplier<MobEffect> CONTAMINATED_EFFECT = registerEffect(() -> new ContaminatedEffect(MobEffectCategory.HARMFUL, 0xB9C300), "contaminated");
	public static final Supplier<MobEffect> MIDAS_TOUCH_EFFECT = registerEffect(() -> new MidasTouchEffect(MobEffectCategory.NEUTRAL, 0xDFB93E), "midas_touch");
	
	public static Supplier<MobEffect> registerEffect(Supplier<MobEffect> effect, String name) {		
		MobEffect reffect = Registry.register(BuiltInRegistries.STATUS_EFFECT, Identifier.fromNamespaceAndPath(LuckyTNTMod.MODID, name), effect.get());
		return () -> reffect;
	}
	
	public static void init() {}
}

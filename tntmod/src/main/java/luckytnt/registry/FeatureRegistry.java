package luckytnt.registry;

import java.util.function.Supplier;

import luckytnt.LuckyTNTMod;
import luckytnt.feature.Altar;
import luckytnt.feature.Grave;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.Registry;
import net.minecraft.resources.Identifier;
import net.minecraft.world.gen.feature.DefaultFeatureConfig;
import net.minecraft.world.gen.feature.Feature;

public class FeatureRegistry {

	public static final Supplier<Feature<?>> graves = registerFeature("graves", () -> new Grave(DefaultFeatureConfig.CODEC));
	public static final Supplier<Feature<?>> altar = registerFeature("altar", () -> new Altar(DefaultFeatureConfig.CODEC));
	
	public static Supplier<Feature<?>> registerFeature(String name, Supplier<Feature<?>> featureSupplier) {
		Feature<?> feature = Registry.register(BuiltInRegistries.FEATURE, Identifier.fromNamespaceAndPath(LuckyTNTMod.MODID, name), featureSupplier.get());
		return () -> feature;
	}
	
	public static void init() {}
}

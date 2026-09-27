package luckytnt.registry;

import com.mojang.serialization.MapCodec;

import luckytnt.LuckyTNTMod;
import luckytnt.feature.Altar;
import luckytnt.feature.Grave;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.levelgen.feature.Feature;

/**
 * 26.3: the old {@code Feature<C>} + {@code ConfiguredFeature} pair was merged. A feature type is now only a
 * {@link MapCodec} in {@code BuiltInRegistries.FEATURE_TYPE}; the configured instances live in the data-driven
 * {@code worldgen/feature} registry ({@code data/luckytntmod/worldgen/feature/*.json}), which the placed features
 * reference as before.
 */
public class FeatureRegistry {

	public static final MapCodec<Grave> graves = registerFeatureType("graves", Grave.CODEC);
	public static final MapCodec<Altar> altar = registerFeatureType("altar", Altar.CODEC);

	public static <T extends Feature> MapCodec<T> registerFeatureType(String name, MapCodec<T> codec) {
		return Registry.register(BuiltInRegistries.FEATURE_TYPE, Identifier.fromNamespaceAndPath(LuckyTNTMod.MODID, name), codec);
	}

	public static void init() {}
}

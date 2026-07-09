package luckytnt.registry;

import java.util.function.Predicate;

import luckytnt.LuckyTNTMod;
import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectionContext;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.minecraft.resources.ResourceKey;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.BiomeTags;
import net.minecraft.resources.Identifier;
import net.minecraft.world.gen.GenerationStep;

public class BiomeModificationRegistry {
	
	public static final Predicate<BiomeSelectionContext> GENERAL = context -> {
		return context.hasTag(BiomeTags.IS_JUNGLE) || context.hasTag(BiomeTags.IS_SAVANNA) || context.hasTag(BiomeTags.IS_FOREST) || context.hasTag(BiomeTags.IS_TAIGA);
	};

	public static void init() {
		BiomeModifications.addFeature(BiomeSelectors.foundInOverworld(), GenerationStep.Feature.UNDERGROUND_ORES, ResourceKey.of(Registries.PLACED_FEATURE, Identifier.fromNamespaceAndPath(LuckyTNTMod.MODID, "gunpowder_ores")));
		BiomeModifications.addFeature(BiomeSelectors.foundInOverworld(), GenerationStep.Feature.UNDERGROUND_ORES, ResourceKey.of(Registries.PLACED_FEATURE, Identifier.fromNamespaceAndPath(LuckyTNTMod.MODID, "uranium_ores")));
		
		BiomeModifications.addFeature(GENERAL, GenerationStep.Feature.SURFACE_STRUCTURES, ResourceKey.of(Registries.PLACED_FEATURE, Identifier.fromNamespaceAndPath(LuckyTNTMod.MODID, "graves")));
		BiomeModifications.addFeature(GENERAL, GenerationStep.Feature.SURFACE_STRUCTURES, ResourceKey.of(Registries.PLACED_FEATURE, Identifier.fromNamespaceAndPath(LuckyTNTMod.MODID, "altar")));
	}
}

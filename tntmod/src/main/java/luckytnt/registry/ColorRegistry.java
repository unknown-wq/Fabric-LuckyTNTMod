package luckytnt.registry;

import java.util.List;

import net.fabricmc.fabric.api.client.rendering.v1.BlockColorRegistry;
import net.minecraft.client.color.block.BlockTintSources;

public class ColorRegistry {

	public static void init() {
		// Grass-biome tint for the custom firework block. In 26.2 block tints are supplied
		// through BlockTintSource; BlockTintSources.grass() reproduces the old biome grass color.
		// Item colors are now driven by item model tints (JSON), so the old ITEM provider is gone.
		BlockColorRegistry.register(List.of(BlockTintSources.grass()), BlockRegistry.CUSTOM_FIREWORK.get());
	}
}

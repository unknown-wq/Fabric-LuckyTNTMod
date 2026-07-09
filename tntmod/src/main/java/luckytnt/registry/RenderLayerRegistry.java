package luckytnt.registry;

import net.fabricmc.fabric.impl.blockrenderlayer.BlockRenderLayerMapImpl;
import net.minecraft.client.renderer.entity.layers.RenderLayer;

public class RenderLayerRegistry {

	public static void init() {
		BlockRenderLayerMapImpl.INSTANCE.putBlock(BlockRegistry.CUSTOM_FIREWORK.get(), RenderLayer.getCutoutMipped());
		BlockRenderLayerMapImpl.INSTANCE.putBlock(BlockRegistry.XRAY_TNT.get(), RenderLayer.getCutoutMipped());
		BlockRenderLayerMapImpl.INSTANCE.putBlock(BlockRegistry.OBSIDIAN_ACTIVATOR_RAIL.get(), RenderLayer.getCutoutMipped());
		BlockRenderLayerMapImpl.INSTANCE.putBlock(BlockRegistry.OBSIDIAN_DETECTOR_RAIL.get(), RenderLayer.getCutoutMipped());
		BlockRenderLayerMapImpl.INSTANCE.putBlock(BlockRegistry.OBSIDIAN_POWERED_RAIL.get(), RenderLayer.getCutoutMipped());
		BlockRenderLayerMapImpl.INSTANCE.putBlock(BlockRegistry.OBSIDIAN_RAIL.get(), RenderLayer.getCutoutMipped());
	}
}

package luckytnt.registry;

public class RenderLayerRegistry {

	public static void init() {
		// TODO(port-26.2): DISABLED — Fabric's BlockRenderLayerMap API was removed in 26.x.
		// A block's render type is now declared in its block model JSON via "render_type"
		// (e.g. "minecraft:cutout_mipped"). The blocks below should carry that in their data:
		// CUSTOM_FIREWORK, XRAY_TNT, OBSIDIAN_ACTIVATOR_RAIL, OBSIDIAN_DETECTOR_RAIL,
		// OBSIDIAN_POWERED_RAIL, OBSIDIAN_RAIL.
		/*
		BlockRenderLayerMapImpl.INSTANCE.putBlock(BlockRegistry.CUSTOM_FIREWORK.get(), RenderLayer.getCutoutMipped());
		BlockRenderLayerMapImpl.INSTANCE.putBlock(BlockRegistry.XRAY_TNT.get(), RenderLayer.getCutoutMipped());
		BlockRenderLayerMapImpl.INSTANCE.putBlock(BlockRegistry.OBSIDIAN_ACTIVATOR_RAIL.get(), RenderLayer.getCutoutMipped());
		BlockRenderLayerMapImpl.INSTANCE.putBlock(BlockRegistry.OBSIDIAN_DETECTOR_RAIL.get(), RenderLayer.getCutoutMipped());
		BlockRenderLayerMapImpl.INSTANCE.putBlock(BlockRegistry.OBSIDIAN_POWERED_RAIL.get(), RenderLayer.getCutoutMipped());
		BlockRenderLayerMapImpl.INSTANCE.putBlock(BlockRegistry.OBSIDIAN_RAIL.get(), RenderLayer.getCutoutMipped());
		*/
	}
}

// TODO(port-26.2): DISABLED — custom HUD overlay layer.
// The original mixin injected into InGameHud.<init> and called
// `layeredDrawer.addLayer(OverlayTick::onOverlayRender)` to register the mod's freeze/contaminated
// screen overlays. In 26.2 the yarn `InGameHud` is `net.minecraft.client.gui.Gui`, `LayeredDraw`
// and its `addLayer` API are GONE, and HUD rendering moved to a render-state extraction model
// (`Gui.extractRenderState` / `Hud`). OverlayTick#onOverlayRender itself no longer compiles either
// (it uses the pre-26.2 RenderSystem/GlStateManager/GuiGraphics.drawTexture pipeline).
// Re-registering the overlay is client render-state work (Agent C) and should use the Fabric API
// HUD hook (e.g. HudLayerRegistrationCallback / HudElementRegistry) rather than a mixin.
// This class is left as a no-op and removed from luckytntmod.mixins.json so nothing broken is loaded.
package luckytnt.mixin;

import org.spongepowered.asm.mixin.Mixin;

import net.minecraft.client.gui.Gui;

@Mixin(Gui.class)
public abstract class InGameHudMixin {
}

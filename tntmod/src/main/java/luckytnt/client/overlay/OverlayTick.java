package luckytnt.client.overlay;

import luckytnt.config.LuckyTNTConfigValues;
import luckytnt.registry.EffectRegistry;
import luckytntlib.util.LuckyTNTEntityExtension;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.util.ARGB;
import net.minecraft.util.Mth;

public class OverlayTick {

	private static final Identifier POWDER_SNOW_OUTLINE = Identifier.fromNamespaceAndPath("luckytntmod", "textures/powder_snow_outline.png");
	private static final Identifier CONTAMINATED_OUTLINE = Identifier.fromNamespaceAndPath("luckytntmod", "textures/contaminated_outline.png");

	private static float contaminatedAmount = 0;

	//resolved once instead of once per HUD frame; lazily, because this class may load before the registries are frozen
	private static Holder<MobEffect> contaminatedHolder;

	private static Holder<MobEffect> contaminatedHolder() {
		if (contaminatedHolder == null) {
			contaminatedHolder = BuiltInRegistries.MOB_EFFECT.getOrThrow(EffectRegistry.CONTAMINATED);
		}
		return contaminatedHolder;
	}

	/**
	 * Registered as a {@link net.fabricmc.fabric.api.client.rendering.v1.hud.HudElement};
	 * in 26.2 HUD elements draw during render-state extraction via {@link GuiGraphicsExtractor#blit}.
	 */
	public static void onOverlayRender(GuiGraphicsExtractor graphics, DeltaTracker deltaTracker) {
		LocalPlayer player = Minecraft.getInstance().player;
		if (player == null) {
			return;
		}
		int w = graphics.guiWidth();
		int h = graphics.guiHeight();
		boolean contaminated = player.hasEffect(contaminatedHolder());
		if (player instanceof LuckyTNTEntityExtension lplayer) {
			int freezeTime = lplayer.getAdditionalPersistentData().getIntOr("freezeTime", 0);
			if (freezeTime > 0 && !contaminated) {
				drawOverlay(graphics, POWDER_SNOW_OUTLINE, freezeTime / 1200f, w, h);
			} else if (contaminated && LuckyTNTConfigValues.RENDER_CONTAMINATED_OVERLAY.get()) {
				drawOverlay(graphics, CONTAMINATED_OUTLINE, contaminatedAmount, w, h);
				contaminatedAmount = Mth.clamp(contaminatedAmount + 0.025f, 0f, 1f);
			} else if (contaminatedAmount > 0) {
				drawOverlay(graphics, CONTAMINATED_OUTLINE, contaminatedAmount, w, h);
				contaminatedAmount = Mth.clamp(contaminatedAmount - 0.025f, 0f, 1f);
			}
		}
	}

	private static void drawOverlay(GuiGraphicsExtractor graphics, Identifier texture, float alpha, int w, int h) {
		graphics.blit(RenderPipelines.GUI_TEXTURED, texture, 0, 0, 0.0F, 0.0F, w, h, w, h, ARGB.white(Mth.clamp(alpha, 0f, 1f)));
	}
}

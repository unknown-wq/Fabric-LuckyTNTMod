package luckytnt.client.overlay;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;

import luckytnt.config.LuckyTNTConfigValues;
import luckytnt.registry.EffectRegistry;
import luckytntlib.util.LuckyTNTEntityExtension;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;

public class OverlayTick {

	private static float contaminatedAmount = 0;
	
	@SuppressWarnings("resource")
	public static void onOverlayRender(GuiGraphics graphics, RenderTickCounter tickCounter) {
		if(Minecraft.getInstance().player != null) {
			ClientPlayerEntity player = Minecraft.getInstance().player;
			int w = graphics.getScaledWindowWidth();
			int h = graphics.getScaledWindowHeight();
			RenderSystem.disableDepthTest();
			RenderSystem.depthMask(false);
			RenderSystem.enableBlend();
			RenderSystem.blendFuncSeparate(GlStateManager.SrcFactor.SRC_ALPHA, GlStateManager.DstFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SrcFactor.ONE, GlStateManager.DstFactor.ZERO);
			if(player instanceof LuckyTNTEntityExtension lplayer) {
				if(lplayer.getAdditionalPersistentData().getInt("freezeTime") > 0 && !player.hasStatusEffect(BuiltInRegistries.STATUS_EFFECT.entryOf(EffectRegistry.CONTAMINATED))) {
					RenderSystem.setShaderColor(1f, 1f, 1f, (float)(lplayer.getAdditionalPersistentData().getInt("freezeTime")) / 1200f);
					RenderSystem.setShaderTexture(0, Identifier.fromNamespaceAndPath("luckytntmod:textures/powder_snow_outline.png"));
					graphics.drawTexture(Identifier.fromNamespaceAndPath("luckytntmod:textures/powder_snow_outline.png"), 0, 0, 0, 0, w, h, w, h);
				} else if(player.hasStatusEffect(BuiltInRegistries.STATUS_EFFECT.entryOf(EffectRegistry.CONTAMINATED)) && LuckyTNTConfigValues.RENDER_CONTAMINATED_OVERLAY.get()) {
					RenderSystem.setShaderColor(1f, 1f, 1f, contaminatedAmount);
					RenderSystem.setShaderTexture(0, Identifier.fromNamespaceAndPath("luckytntmod:textures/contaminated_outline.png"));
					graphics.drawTexture(Identifier.fromNamespaceAndPath("luckytntmod:textures/contaminated_outline.png"), 0, 0, 0, 0, w, h, w, h);
					contaminatedAmount = Mth.clamp(contaminatedAmount + 0.025f, 0f, 1f);
				} else if(contaminatedAmount > 0){
					RenderSystem.setShaderColor(1f, 1f, 1f, contaminatedAmount);
					RenderSystem.setShaderTexture(0, Identifier.fromNamespaceAndPath("luckytntmod:textures/contaminated_outline.png"));
					graphics.drawTexture(Identifier.fromNamespaceAndPath("luckytntmod:textures/contaminated_outline.png"), 0, 0, 0, 0, w, h, w, h);
					contaminatedAmount = Mth.clamp(contaminatedAmount - 0.025f, 0f, 1f);
				}
			}
			RenderSystem.depthMask(true);
			RenderSystem.defaultBlendFunc();
			RenderSystem.disableBlend();
			RenderSystem.enableDepthTest();
			RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
		}
	}
}

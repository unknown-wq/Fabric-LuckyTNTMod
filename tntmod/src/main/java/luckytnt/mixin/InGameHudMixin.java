package luckytnt.mixin;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import luckytnt.client.overlay.OverlayTick;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.LayeredDrawer;
import net.minecraft.client.gui.hud.InGameHud;

@Mixin(InGameHud.class)
public abstract class InGameHudMixin {

	@Shadow
	@Final
	private LayeredDrawer layeredDrawer;
	
	@Inject(method = "<init>", at = @At("TAIL"))
	private void constructorInject(MinecraftClient client, CallbackInfo info) {
		layeredDrawer.addLayer(OverlayTick::onOverlayRender);
	}
}

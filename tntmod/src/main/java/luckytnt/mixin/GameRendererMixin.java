package luckytnt.mixin;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import luckytnt.util.mixin.CameraExtension;
import luckytntlib.util.LuckyTNTEntityExtension;
import net.minecraft.client.Camera;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.GameRenderer;

/**
 * Applies the LuckyTNTMod camera "screen shake" effect. In 26.2 the camera rotation is baked into
 * a {@code CameraRenderState} during {@code GameRenderer.extractCamera}, so we perturb the main
 * camera's rotation at the HEAD of that method (before {@code Camera.extractRenderState} reads it).
 * The perturbation goes through {@link CameraExtension} which calls {@code Camera.setRotation} so the
 * internal rotation quaternion (source of the view matrix) is kept in sync.
 * <p>
 * 26.3: {@code extractCamera} lost its third parameter (camera-entity partial ticks) and is now
 * {@code extractCamera(DeltaTracker, float)}; the handler signature follows it. The order is unchanged:
 * {@code Camera.update} runs before {@code extractCamera}, which calls {@code Camera.extractRenderState} first.
 */
@Mixin(GameRenderer.class)
public abstract class GameRendererMixin {

	@Shadow
	@Final
	private Camera mainCamera;

	@Inject(method = "extractCamera", at = @At("HEAD"))
	private void extractCameraInject(DeltaTracker deltaTracker, float worldPartialTicks, CallbackInfo info) {
		LocalPlayer player = Minecraft.getInstance().player;

		if (player instanceof LuckyTNTEntityExtension lplayer && lplayer.getAdditionalPersistentData().getIntOr("shakeTime", 0) >= 1) {
			int shakeTime = lplayer.getAdditionalPersistentData().getIntOr("shakeTime", 0);
			float shakeAmount = 4f;
			float yaw = mainCamera.yRot();
			float pitch = mainCamera.xRot();

			yaw += shakeAmount * (float)Math.cos((Math.random() * 5f + 1f) * 3d * shakeTime / 20f);
			pitch += shakeAmount * (float)Math.cos((Math.random() * 3f + 1f) * 3d * shakeTime / 20f);

			if (mainCamera instanceof CameraExtension ecamera) {
				ecamera.setPitchRaw(pitch);
				ecamera.setYawRaw(yaw);
			}
		}
	}
}

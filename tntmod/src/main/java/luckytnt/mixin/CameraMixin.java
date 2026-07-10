package luckytnt.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import luckytnt.util.mixin.CameraExtension;
import net.minecraft.client.Camera;

@Mixin(Camera.class)
public abstract class CameraMixin implements CameraExtension {

	@Shadow
	private float xRot;
	@Shadow
	private float yRot;

	@Shadow
	protected abstract void setRotation(float yRot, float xRot);

	@Unique
	public void setYawRaw(float yaw) {
		this.setRotation(yaw, this.xRot);
	}

	@Unique
	public void setPitchRaw(float pitch) {
		this.setRotation(this.yRot, pitch);
	}
}

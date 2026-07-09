package luckytnt.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import luckytnt.util.mixin.CameraExtension;
import net.minecraft.client.render.Camera;

@Mixin(Camera.class)
public abstract class CameraMixin implements CameraExtension {

	@Shadow
	private float pitch;
	@Shadow
    private float yaw;
	
	@Unique
	public void setYawRaw(float yaw) {
		this.yaw = yaw;
	}
	
	@Unique
	public void setPitchRaw(float pitch) {
		this.pitch = pitch;
	}
}

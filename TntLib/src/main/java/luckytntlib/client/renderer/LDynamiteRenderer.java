package luckytntlib.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;

import luckytntlib.entity.LExplosiveProjectile;
import luckytntlib.util.tnteffects.PrimedTNTEffect;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.entity.projectile.ItemSupplier;
import net.minecraft.world.item.ItemDisplayContext;

/**
 * The LDynamiteRenderer is similar to the {@link ThrownItemRenderer}, but the item is also scaled by using
 * the size given by the {@link PrimedTNTEffect} of the {@link LExplosiveProjectile}.
 * @param <T>  is an instance of {@link LExplosiveProjectile} and implements {@link ItemSupplier}
 */
@Environment(value=EnvType.CLIENT)
public class LDynamiteRenderer<T extends LExplosiveProjectile & ItemSupplier> extends EntityRenderer<T, LDynamiteRenderer.LDynamiteRenderState> {

	private final ItemModelResolver itemModelResolver;

	public LDynamiteRenderer(EntityRendererProvider.Context context) {
		super(context);
		this.itemModelResolver = context.getItemModelResolver();
	}

	@Override
	public void submit(LDynamiteRenderState state, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, CameraRenderState camera) {
		if (state.ageInTicks >= 2.0F || state.distanceToCameraSq >= 12.25D) {
			poseStack.pushPose();
			poseStack.scale(state.size, state.size, state.size);
			poseStack.mulPose(camera.orientation);
			state.item.submit(poseStack, submitNodeCollector, state.lightCoords, OverlayTexture.NO_OVERLAY, state.outlineColor);
			poseStack.popPose();
		}
		super.submit(state, poseStack, submitNodeCollector, camera);
	}

	@Override
	public LDynamiteRenderState createRenderState() {
		return new LDynamiteRenderState();
	}

	@Override
	public void extractRenderState(T entity, LDynamiteRenderState state, float partialTicks) {
		super.extractRenderState(entity, state, partialTicks);
		state.size = entity.getEffect().getSize(entity);
		this.itemModelResolver.updateForNonLiving(state.item, entity.getItem(), ItemDisplayContext.GROUND, entity);
	}

	@Environment(value=EnvType.CLIENT)
	public static class LDynamiteRenderState extends EntityRenderState {
		public final ItemStackRenderState item = new ItemStackRenderState();
		public float size = 1.0F;
	}
}

package luckytnt.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;

import luckytnt.LuckyTNTMod;
import luckytnt.client.model.BombModel;
import luckytntlib.entity.LExplosiveProjectile;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.Identifier;

@Environment(value = EnvType.CLIENT)
public class BombRenderer extends EntityRenderer<LExplosiveProjectile, BombRenderer.BombRenderState> {
	private static final Identifier TEXTURE = Identifier.fromNamespaceAndPath(LuckyTNTMod.MODID, "textures/tsarbomb.png");

	private final EntityModel<EntityRenderState> model;

	public BombRenderer(EntityRendererProvider.Context context) {
		super(context);
		this.model = new BombModel(context.bakeLayer(BombModel.LAYER_LOCATION));
	}

	@Override
	public void submit(BombRenderState state, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, CameraRenderState camera) {
		poseStack.pushPose();
		poseStack.scale(state.size, state.size, state.size);
		RenderType renderType = model.renderType(TEXTURE);
		submitNodeCollector.submitModel(model, state, poseStack, renderType, state.lightCoords, OverlayTexture.NO_OVERLAY, -1, null, state.outlineColor, null);
		poseStack.popPose();
		super.submit(state, poseStack, submitNodeCollector, camera);
	}

	@Override
	public BombRenderState createRenderState() {
		return new BombRenderState();
	}

	@Override
	public void extractRenderState(LExplosiveProjectile entity, BombRenderState state, float partialTicks) {
		super.extractRenderState(entity, state, partialTicks);
		state.size = entity.getEffect().getSize(entity);
	}

	@Environment(value = EnvType.CLIENT)
	public static class BombRenderState extends EntityRenderState {
		public float size = 1.0F;
	}
}

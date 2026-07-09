package luckytnt.client.renderer;

import luckytnt.client.model.BombModel;
import luckytntlib.entity.LExplosiveProjectile;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.model.EntityModel;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.resources.Identifier;

@Environment(value=EnvType.CLIENT)
public class BombRenderer extends EntityRenderer<LExplosiveProjectile>{

	public EntityModel<LExplosiveProjectile> model;
	
	public BombRenderer(EntityRendererFactory.Context context) {
		super(context);
		model = new BombModel<LExplosiveProjectile>(context.getPart(BombModel.LAYER_LOCATION));
	}
	
	@Override
	public void render(LExplosiveProjectile entity, float rotY, float partialTicks, PoseStack stack, MultiBufferSource buffer, int light) {
		stack.push();
		stack.scale(entity.getEffect().getSize(entity), entity.getEffect().getSize(entity), entity.getEffect().getSize(entity));
		VertexConsumer vc = buffer.getBuffer(RenderLayer.getEntityCutout(getTexture(entity)));
		model.render(stack, vc, light, OverlayTexture.DEFAULT_UV);
		super.render(entity, rotY, partialTicks, stack, buffer, light);
		stack.pop();
	}

	@Override
	public Identifier getTexture(LExplosiveProjectile entity) {
		return Identifier.fromNamespaceAndPath("luckytntmod:textures/tsarbomb.png");
	}
}

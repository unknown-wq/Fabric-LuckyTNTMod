package luckytntlib.client.renderer;

import luckytntlib.entity.LExplosiveProjectile;
import luckytntlib.util.tnteffects.PrimedTNTEffect;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.FlyingItemEntityRenderer;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.FlyingItemEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.RotationAxis;

/**
 * The LDynamiteRenderer is similar to the {@link FlyingItemEntityRenderer}, but the item is also scaled by using
 * the size given by the {@link PrimedTNTEffect} of the {@link LExplosiveProjectile}.
 * @param <T>  is an instance of {@link LExplosiveProjectile} and implements {@link FlyingItemEntity}
 */
@Environment(value=EnvType.CLIENT)
public class LDynamiteRenderer<T extends LExplosiveProjectile & FlyingItemEntity> extends EntityRenderer<T>{
	
	private final ItemRenderer itemRenderer;
	
	public LDynamiteRenderer(EntityRendererFactory.Context context) {
		super(context);
		itemRenderer = context.getItemRenderer();
	}
	
	@Override
	public void render(T entity, float yaw, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {
		if (entity.age >= 2 || !(dispatcher.camera.getFocusedEntity().squaredDistanceTo(entity) < 12.25D)) {
			matrices.push();
			matrices.scale(entity.getEffect().getSize(entity), entity.getEffect().getSize(entity), entity.getEffect().getSize(entity));
			matrices.multiply(dispatcher.getRotation());
			matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(180.0F));
			itemRenderer.renderItem(entity.getStack(), ModelTransformationMode.GROUND, light, OverlayTexture.DEFAULT_UV, matrices, vertexConsumers, entity.getWorld(), entity.getId());
			matrices.pop();
			super.render(entity, yaw, tickDelta, matrices, vertexConsumers, light);
		}
	}
	
	@SuppressWarnings("deprecation")
	public Identifier getTexture(T entity) {
		return SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE;
	}
}

package luckytnt.client.renderer;

import luckytntlib.block.LTNTBlock;
import luckytntlib.util.IExplosiveEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.BlockRenderManager;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.TntMinecartEntityRenderer;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

@Environment(value=EnvType.CLIENT)
public class BouncingTNTRenderer extends EntityRenderer<Entity>{
	private BlockRenderManager blockRenderer;
	
	public BouncingTNTRenderer(EntityRendererFactory.Context context) {
		super(context);
		this.blockRenderer = context.getBlockRenderManager();
	}
	
	public void render(Entity entity, float yaw, float partialTicks, MatrixStack posestack, VertexConsumerProvider buffer, int light) {
    	if(entity instanceof IExplosiveEntity ent) {
			posestack.push();
	        posestack.translate(0, 0, 0);	        
	        float scaleMul = (float)MathHelper.clamp(entity.getVelocity().length() * 1.5f, 0.85f, 1.35f);
	        posestack.scale(1 / scaleMul, scaleMul, 1 / scaleMul);	        
	        int i = ent.getTNTFuse();
	        if ((float)i - partialTicks + 1.0F < 10.0F && ent.getEffect().getBlock() instanceof LTNTBlock) {
	           float f = 1.0F - ((float)i - partialTicks + 1.0F) / 10.0F;
	           f = MathHelper.clamp(f, 0.0F, 1.0F);
	           f *= f;
	           f *= f;
	           float f1 = 1.0F + f * 0.3F;
	           posestack.scale(f1, f1, f1);
	        }
	        posestack.scale(ent.getEffect().getSize((IExplosiveEntity)entity), ent.getEffect().getSize((IExplosiveEntity)entity), ent.getEffect().getSize((IExplosiveEntity)entity));
	        posestack.translate(-0.5d, 0, -0.5d);
	        TntMinecartEntityRenderer.renderFlashingBlock(blockRenderer, ent.getEffect().getBlockState((IExplosiveEntity)entity), posestack, buffer, light, ent.getEffect().getBlock() instanceof LTNTBlock ? i / 5 % 2 == 0 : false);
	        posestack.pop();
    	}
        super.render(entity, yaw, partialTicks, posestack, buffer, light);
    }

	@SuppressWarnings("deprecation")
	@Override
	public Identifier getTexture(Entity entity) {
		return SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE;
	}
}

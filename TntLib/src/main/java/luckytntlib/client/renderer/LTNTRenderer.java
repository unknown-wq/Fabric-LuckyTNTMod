package luckytntlib.client.renderer;

import luckytntlib.util.IExplosiveEntity;
import luckytntlib.util.tnteffects.PrimedTNTEffect;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.TntBlock;
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

/**
 * The LTNTRenderer renders an {@link IExplosiveEntity} as a block.
 * The block can be a type of TNT, in which case it will also be animated, or any other block,
 * in which case it is rendered like a normal block.
 * The block is also scaled using the size of its {@link PrimedTNTEffect}.
 */
@Environment(value=EnvType.CLIENT)
public class LTNTRenderer extends EntityRenderer<Entity>{
	private BlockRenderManager blockRenderer;
	
	public LTNTRenderer(EntityRendererFactory.Context context) {
		super(context);
		this.blockRenderer = context.getBlockRenderManager();
	}
	
	public void render(Entity entity, float yaw, float partialTicks, MatrixStack posestack, VertexConsumerProvider buffer, int light) {
    	if(entity instanceof IExplosiveEntity ent) {
			posestack.push();
	        posestack.translate(0, 0, 0);
	        int i = ent.getTNTFuse();
	        if ((float)i - partialTicks + 1.0F < 10.0F && ent.getEffect().getBlockState((IExplosiveEntity)entity).getBlock() instanceof TntBlock) {
	           float f = 1.0F - ((float)i - partialTicks + 1.0F) / 10.0F;
	           f = MathHelper.clamp(f, 0.0F, 1.0F);
	           f *= f;
	           f *= f;
	           float f1 = 1.0F + f * 0.3F;
	           posestack.scale(f1, f1, f1);
	        }
	        posestack.scale(ent.getEffect().getSize((IExplosiveEntity)entity), ent.getEffect().getSize((IExplosiveEntity)entity), ent.getEffect().getSize((IExplosiveEntity)entity));
	        posestack.translate(-0.5d, 0, -0.5d);
	        TntMinecartEntityRenderer.renderFlashingBlock(blockRenderer, ent.getEffect().getBlockState((IExplosiveEntity)entity), posestack, buffer, light, ent.getEffect().getBlockState((IExplosiveEntity)entity).getBlock() instanceof TntBlock ? i / 5 % 2 == 0 : false);
	        posestack.pop();
    	}
        super.render(entity, yaw, partialTicks, posestack, buffer, light);
    }
	
	@SuppressWarnings("deprecation")
	@Override
    public Identifier getTexture(Entity tntEntity) {
        return SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE;
    }
}

package luckytntlib.client.renderer;

import luckytntlib.entity.LTNTMinecart;
import luckytntlib.util.IExplosiveEntity;
import luckytntlib.util.tnteffects.PrimedTNTEffect;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MinecartEntityRenderer;
import net.minecraft.client.render.entity.TntMinecartEntityRenderer;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.MathHelper;

/**
 * The LTNTMinecartRenderer renders a Minecart with a TNT inside of it.
 * The TNT is scaled using the size parameter of its {@link PrimedTNTEffect}.
 */
@Environment(value=EnvType.CLIENT)
public class LTNTMinecartRenderer extends MinecartEntityRenderer<LTNTMinecart>{
	
	public LTNTMinecartRenderer(EntityRendererFactory.Context context) {
		super(context, EntityModelLayers.TNT_MINECART);
	}
	
	@Override
	public void renderBlock(LTNTMinecart entity, float partialTicks, BlockState state, MatrixStack stack, VertexConsumerProvider buffer, int light) {
		int fuse = entity.getTNTFuse();
		if(fuse > -1 && (float) fuse - partialTicks + 1f < 10f) {
			float scaleMult = 1f - ((float)fuse - partialTicks + 1f) / 10f;
			scaleMult = MathHelper.clamp(scaleMult, 0f, 1f);
			scaleMult *= scaleMult;
			scaleMult *= scaleMult;
			float scale = 1f + scaleMult * 0.3f;
			stack.scale(scale, scale, scale);
		}
		stack.translate((-entity.getEffect().getSize(entity) + 1) / 2f, 0, (-entity.getEffect().getSize(entity) + 1) / 2f);
		stack.scale(entity.getEffect().getSize((IExplosiveEntity)entity), entity.getEffect().getSize((IExplosiveEntity)entity), entity.getEffect().getSize((IExplosiveEntity)entity));
		TntMinecartEntityRenderer.renderFlashingBlock(MinecraftClient.getInstance().getBlockRenderManager(), state, stack, buffer, light, fuse > -1 && fuse / 5 % 2 == 0);
	}
}

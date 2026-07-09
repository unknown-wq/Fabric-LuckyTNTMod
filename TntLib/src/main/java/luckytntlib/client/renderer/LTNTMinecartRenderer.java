package luckytntlib.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;

import luckytntlib.entity.LTNTMinecart;
import luckytntlib.util.tnteffects.PrimedTNTEffect;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.block.BlockModelRenderState;
import net.minecraft.client.renderer.entity.AbstractMinecartRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.TntMinecartRenderer;
import net.minecraft.client.renderer.entity.TntRenderer;
import net.minecraft.client.renderer.entity.state.MinecartRenderState;

/**
 * The LTNTMinecartRenderer renders a Minecart with a TNT inside of it.
 * The TNT is scaled using the size parameter of its {@link PrimedTNTEffect}.
 */
@Environment(value=EnvType.CLIENT)
public class LTNTMinecartRenderer extends AbstractMinecartRenderer<LTNTMinecart, LTNTMinecartRenderer.LTNTMinecartRenderState> {

	public LTNTMinecartRenderer(EntityRendererProvider.Context context) {
		super(context, ModelLayers.TNT_MINECART);
	}

	@Override
	protected void submitMinecartContents(LTNTMinecartRenderState state, BlockModelRenderState blockModel, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, int lightCoords) {
		float fuse = state.fuseRemainingInTicks;
		if (fuse > -1.0F && fuse < 10.0F) {
			float scale = 1.0F + TntRenderer.getSwellAmount(fuse);
			poseStack.scale(scale, scale, scale);
		}
		poseStack.translate((-state.size + 1.0F) / 2.0F, 0.0F, (-state.size + 1.0F) / 2.0F);
		poseStack.scale(state.size, state.size, state.size);
		TntMinecartRenderer.submitWhiteSolidBlock(blockModel, poseStack, submitNodeCollector, lightCoords, fuse > -1.0F && TntRenderer.isLit(fuse), state.outlineColor);
	}

	@Override
	public LTNTMinecartRenderState createRenderState() {
		return new LTNTMinecartRenderState();
	}

	@Override
	public void extractRenderState(LTNTMinecart entity, LTNTMinecartRenderState state, float partialTicks) {
		super.extractRenderState(entity, state, partialTicks);
		int fuse = entity.getTNTFuse();
		state.fuseRemainingInTicks = fuse > -1 ? fuse - partialTicks + 1.0F : -1.0F;
		state.size = entity.getEffect().getSize(entity);
	}

	@Environment(value=EnvType.CLIENT)
	public static class LTNTMinecartRenderState extends MinecartRenderState {
		public float fuseRemainingInTicks = -1.0F;
		public float size = 1.0F;
	}
}

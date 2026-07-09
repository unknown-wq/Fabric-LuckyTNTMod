package luckytntlib.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;

import luckytntlib.entity.PrimedLTNT;
import luckytntlib.util.IExplosiveEntity;
import luckytntlib.util.tnteffects.PrimedTNTEffect;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.block.BlockModelRenderState;
import net.minecraft.client.renderer.block.BlockModelResolver;
import net.minecraft.client.renderer.block.model.BlockDisplayContext;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.TntMinecartRenderer;
import net.minecraft.client.renderer.entity.TntRenderer;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.world.level.block.TntBlock;
import net.minecraft.world.level.block.state.BlockState;

/**
 * The LTNTRenderer renders an {@link IExplosiveEntity} as a block.
 * The block can be a type of TNT, in which case it will also be animated, or any other block,
 * in which case it is rendered like a normal block.
 * The block is also scaled using the size of its {@link PrimedTNTEffect}.
 */
@Environment(value=EnvType.CLIENT)
public class LTNTRenderer extends EntityRenderer<PrimedLTNT, LTNTRenderer.LTNTRenderState> {
	public static final BlockDisplayContext BLOCK_DISPLAY_CONTEXT = BlockDisplayContext.create();
	private final BlockModelResolver blockModelResolver;

	public LTNTRenderer(EntityRendererProvider.Context context) {
		super(context);
		this.blockModelResolver = context.getBlockModelResolver();
	}

	@Override
	public void submit(LTNTRenderState state, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, CameraRenderState camera) {
		poseStack.pushPose();
		poseStack.translate(0.0F, 0.5F, 0.0F);
		float fuse = state.fuseRemainingInTicks;
		if (fuse < 10.0F && state.isTNT) {
			float scale = 1.0F + TntRenderer.getSwellAmount(fuse);
			poseStack.scale(scale, scale, scale);
		}
		poseStack.scale(state.size, state.size, state.size);
		poseStack.mulPose(Axis.YP.rotationDegrees(-90.0F));
		poseStack.translate(-0.5F, -0.5F, 0.5F);
		poseStack.mulPose(Axis.YP.rotationDegrees(90.0F));
		if (!state.blockState.isEmpty()) {
			TntMinecartRenderer.submitWhiteSolidBlock(state.blockState, poseStack, submitNodeCollector, state.lightCoords, state.isTNT && TntRenderer.isLit(fuse), state.outlineColor);
		}
		poseStack.popPose();
		super.submit(state, poseStack, submitNodeCollector, camera);
	}

	@Override
	public LTNTRenderState createRenderState() {
		return new LTNTRenderState();
	}

	@Override
	public void extractRenderState(PrimedLTNT entity, LTNTRenderState state, float partialTicks) {
		super.extractRenderState(entity, state, partialTicks);
		state.fuseRemainingInTicks = entity.getTNTFuse() - partialTicks + 1.0F;
		BlockState blockState = entity.getEffect().getBlockState(entity);
		state.isTNT = blockState.getBlock() instanceof TntBlock;
		state.size = entity.getEffect().getSize(entity);
		this.blockModelResolver.update(state.blockState, blockState, BLOCK_DISPLAY_CONTEXT);
	}

	@Environment(value=EnvType.CLIENT)
	public static class LTNTRenderState extends EntityRenderState {
		public float fuseRemainingInTicks;
		public boolean isTNT;
		public float size = 1.0F;
		public final BlockModelRenderState blockState = new BlockModelRenderState();
	}
}

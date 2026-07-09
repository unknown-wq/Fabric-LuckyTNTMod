package luckytnt.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;

import luckytntlib.block.LTNTBlock;
import luckytntlib.entity.PrimedLTNT;
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
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Renders an {@link PrimedLTNT} as a block like the {@link luckytntlib.client.renderer.LTNTRenderer},
 * but also squashes/stretches the block based on the entity's velocity.
 */
@Environment(value = EnvType.CLIENT)
public class BouncingTNTRenderer extends EntityRenderer<PrimedLTNT, BouncingTNTRenderer.BouncingTNTRenderState> {
	public static final BlockDisplayContext BLOCK_DISPLAY_CONTEXT = BlockDisplayContext.create();
	private final BlockModelResolver blockModelResolver;

	public BouncingTNTRenderer(EntityRendererProvider.Context context) {
		super(context);
		this.blockModelResolver = context.getBlockModelResolver();
	}

	@Override
	public void submit(BouncingTNTRenderState state, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, CameraRenderState camera) {
		poseStack.pushPose();
		float scaleMul = (float) Mth.clamp(state.velocityLength * 1.5f, 0.85f, 1.35f);
		poseStack.scale(1 / scaleMul, scaleMul, 1 / scaleMul);
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
	public BouncingTNTRenderState createRenderState() {
		return new BouncingTNTRenderState();
	}

	@Override
	public void extractRenderState(PrimedLTNT entity, BouncingTNTRenderState state, float partialTicks) {
		super.extractRenderState(entity, state, partialTicks);
		state.fuseRemainingInTicks = entity.getTNTFuse() - partialTicks + 1.0F;
		state.velocityLength = (float) entity.getDeltaMovement().length();
		BlockState blockState = entity.getEffect().getBlockState(entity);
		state.isTNT = blockState.getBlock() instanceof LTNTBlock;
		state.size = entity.getEffect().getSize(entity);
		this.blockModelResolver.update(state.blockState, blockState, BLOCK_DISPLAY_CONTEXT);
	}

	@Environment(value = EnvType.CLIENT)
	public static class BouncingTNTRenderState extends EntityRenderState {
		public float fuseRemainingInTicks;
		public boolean isTNT;
		public float size = 1.0F;
		public float velocityLength;
		public final BlockModelRenderState blockState = new BlockModelRenderState();
	}
}

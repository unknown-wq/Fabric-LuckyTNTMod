package luckytnt.client.model;

import luckytnt.LuckyTNTMod;
import net.minecraft.client.model.Dilation;
import net.minecraft.client.model.ModelData;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.model.ModelPartBuilder;
import net.minecraft.client.model.ModelPartData;
import net.minecraft.client.model.ModelTransform;
import net.minecraft.client.model.TexturedModelData;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.util.Identifier;

public class BombModel <T extends Entity> extends EntityModel<T> {
	public static final EntityModelLayer LAYER_LOCATION = new EntityModelLayer(Identifier.of(LuckyTNTMod.MODID, "tsar_bomb_model"), "main");
	private final ModelPart TsarBomb;

	public BombModel(ModelPart root) {
		this.TsarBomb = root.getChild("TsarBomb");
	}

	public static TexturedModelData getTexturedModelData() {
        ModelData meshdefinition = new ModelData();
        ModelPartData partdefinition = meshdefinition.getRoot();

		partdefinition.addChild("TsarBomb", ModelPartBuilder.create().uv(76, 26).cuboid(14.8344F, -9.9988F, -0.998F, 5.0F, 6.0F, 2.0F, Dilation.NONE)
		.uv(76, 16).cuboid(14.8344F, 4.0012F, -0.998F, 5.0F, 6.0F, 2.0F, Dilation.NONE)
		.uv(20, 67).cuboid(14.8344F, -0.9988F, 4.002F, 5.0F, 2.0F, 6.0F, Dilation.NONE)
		.uv(60, 62).cuboid(14.8344F, -0.9988F, -9.998F, 5.0F, 2.0F, 6.0F, Dilation.NONE)
		.uv(0, 36).cuboid(8.0844F, -2.9988F, -2.998F, 11.0F, 6.0F, 6.0F, Dilation.NONE)
		.uv(36, 18).cuboid(2.0844F, -3.9988F, -3.998F, 6.0F, 8.0F, 8.0F, Dilation.NONE)
		.uv(0, 0).cuboid(-17.9156F, -3.9988F, -3.998F, 20.0F, 8.0F, 8.0F, Dilation.NONE)
		.uv(56, 52).cuboid(2.0844F, -4.9988F, -3.998F, 6.0F, 1.0F, 8.0F, Dilation.NONE)
		.uv(0, 26).cuboid(-11.9156F, -5.9988F, -3.998F, 14.0F, 2.0F, 8.0F, Dilation.NONE)
		.uv(0, 16).cuboid(-11.9156F, 4.0012F, -3.998F, 14.0F, 2.0F, 8.0F, Dilation.NONE)
		.uv(4, 21).cuboid(8.0844F, -3.9988F, 3.002F, 1.0F, 1.0F, 1.0F, Dilation.NONE)
		.uv(0, 21).cuboid(8.0844F, -3.9988F, -3.998F, 1.0F, 1.0F, 1.0F, Dilation.NONE)
		.uv(4, 5).cuboid(8.0844F, 3.0012F, -3.998F, 1.0F, 1.0F, 1.0F, Dilation.NONE)
		.uv(0, 5).cuboid(8.0844F, 3.0012F, 3.002F, 1.0F, 1.0F, 1.0F, Dilation.NONE)
		.uv(56, 26).cuboid(2.0844F, 4.0012F, -3.998F, 6.0F, 1.0F, 8.0F, Dilation.NONE)
		.uv(28, 43).cuboid(8.0844F, -3.9988F, -2.998F, 12.0F, 1.0F, 6.0F, Dilation.NONE)
		.uv(34, 36).cuboid(8.0844F, 3.0012F, -2.998F, 12.0F, 1.0F, 6.0F, Dilation.NONE)
		.uv(64, 35).cuboid(8.0844F, -2.9988F, -3.998F, 12.0F, 6.0F, 1.0F, Dilation.NONE)
		.uv(40, 61).cuboid(8.0844F, -2.9988F, 3.002F, 12.0F, 6.0F, 1.0F, Dilation.NONE)
		.uv(0, 76).cuboid(2.0844F, -3.9988F, 4.002F, 6.0F, 8.0F, 1.0F, Dilation.NONE)
		.uv(19, 75).cuboid(2.0844F, -3.9988F, -4.998F, 6.0F, 8.0F, 1.0F, Dilation.NONE)
		.uv(48, 8).cuboid(-17.9156F, -4.9988F, -3.998F, 6.0F, 1.0F, 8.0F, Dilation.NONE)
		.uv(0, 60).cuboid(-19.9156F, -3.9988F, -3.998F, 2.0F, 8.0F, 8.0F, Dilation.NONE)
		.uv(33, 75).cuboid(-21.9156F, -1.9988F, -1.998F, 2.0F, 4.0F, 4.0F, Dilation.NONE)
		.uv(76, 52).cuboid(-22.9156F, -1.9988F, -1.998F, 1.0F, 4.0F, 4.0F, Dilation.NONE)
		.uv(71, 0).cuboid(-21.9156F, -2.9988F, -1.998F, 2.0F, 1.0F, 4.0F, Dilation.NONE)
		.uv(28, 36).cuboid(-21.9156F, 2.0012F, -1.998F, 2.0F, 1.0F, 4.0F, Dilation.NONE)
		.uv(0, 16).cuboid(-21.9156F, -1.9988F, -2.998F, 2.0F, 4.0F, 1.0F, Dilation.NONE)
		.uv(0, 0).cuboid(-21.9156F, -1.9988F, 2.002F, 2.0F, 4.0F, 1.0F, Dilation.NONE)
		.uv(68, 7).cuboid(-17.9156F, -3.9988F, -4.998F, 6.0F, 8.0F, 1.0F, Dilation.NONE)
		.uv(70, 70).cuboid(-17.9156F, -3.9988F, 4.002F, 6.0F, 8.0F, 1.0F, Dilation.NONE)
		.uv(56, 17).cuboid(-17.9156F, 4.0012F, -3.998F, 6.0F, 1.0F, 8.0F, Dilation.NONE)
		.uv(32, 50).cuboid(-11.9156F, -3.9988F, -5.998F, 14.0F, 8.0F, 2.0F, Dilation.NONE)
		.uv(0, 50).cuboid(-11.9156F, -3.9988F, 4.002F, 14.0F, 8.0F, 2.0F, Dilation.NONE)
		.uv(62, 50).cuboid(-11.9156F, -4.9988F, 4.002F, 14.0F, 1.0F, 1.0F, Dilation.NONE)
		.uv(58, 47).cuboid(-11.9156F, -4.9988F, -4.998F, 14.0F, 1.0F, 1.0F, Dilation.NONE)
		.uv(58, 45).cuboid(-11.9156F, 4.0012F, -4.998F, 14.0F, 1.0F, 1.0F, Dilation.NONE)
		.uv(58, 43).cuboid(-11.9156F, 4.0012F, 4.002F, 14.0F, 1.0F, 1.0F, Dilation.NONE), ModelTransform.of(-0.0012F, 4.0844F, -0.002F, 0.0F, 0.0F, 1.5708F));
		return TexturedModelData.of(meshdefinition, 128, 128);
	}

	@Override
	public void setAngles(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {

	}

	@Override
	public void render(MatrixStack poseStack, VertexConsumer buffer, int packedLight, int packedOverlay, int i) {
		TsarBomb.render(poseStack, buffer, packedLight, packedOverlay);
	}
}

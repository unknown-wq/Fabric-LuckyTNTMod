package luckytnt.client.model;

import luckytnt.LuckyTNTMod;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.resources.Identifier;

public class BombModel extends EntityModel<EntityRenderState> {
	public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(Identifier.fromNamespaceAndPath(LuckyTNTMod.MODID, "tsar_bomb_model"), "main");

	public BombModel(ModelPart root) {
		super(root);
	}

	public static LayerDefinition getTexturedModelData() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		partdefinition.addOrReplaceChild("TsarBomb", CubeListBuilder.create().texOffs(76, 26).addBox(14.8344F, -9.9988F, -0.998F, 5.0F, 6.0F, 2.0F, CubeDeformation.NONE)
		.texOffs(76, 16).addBox(14.8344F, 4.0012F, -0.998F, 5.0F, 6.0F, 2.0F, CubeDeformation.NONE)
		.texOffs(20, 67).addBox(14.8344F, -0.9988F, 4.002F, 5.0F, 2.0F, 6.0F, CubeDeformation.NONE)
		.texOffs(60, 62).addBox(14.8344F, -0.9988F, -9.998F, 5.0F, 2.0F, 6.0F, CubeDeformation.NONE)
		.texOffs(0, 36).addBox(8.0844F, -2.9988F, -2.998F, 11.0F, 6.0F, 6.0F, CubeDeformation.NONE)
		.texOffs(36, 18).addBox(2.0844F, -3.9988F, -3.998F, 6.0F, 8.0F, 8.0F, CubeDeformation.NONE)
		.texOffs(0, 0).addBox(-17.9156F, -3.9988F, -3.998F, 20.0F, 8.0F, 8.0F, CubeDeformation.NONE)
		.texOffs(56, 52).addBox(2.0844F, -4.9988F, -3.998F, 6.0F, 1.0F, 8.0F, CubeDeformation.NONE)
		.texOffs(0, 26).addBox(-11.9156F, -5.9988F, -3.998F, 14.0F, 2.0F, 8.0F, CubeDeformation.NONE)
		.texOffs(0, 16).addBox(-11.9156F, 4.0012F, -3.998F, 14.0F, 2.0F, 8.0F, CubeDeformation.NONE)
		.texOffs(4, 21).addBox(8.0844F, -3.9988F, 3.002F, 1.0F, 1.0F, 1.0F, CubeDeformation.NONE)
		.texOffs(0, 21).addBox(8.0844F, -3.9988F, -3.998F, 1.0F, 1.0F, 1.0F, CubeDeformation.NONE)
		.texOffs(4, 5).addBox(8.0844F, 3.0012F, -3.998F, 1.0F, 1.0F, 1.0F, CubeDeformation.NONE)
		.texOffs(0, 5).addBox(8.0844F, 3.0012F, 3.002F, 1.0F, 1.0F, 1.0F, CubeDeformation.NONE)
		.texOffs(56, 26).addBox(2.0844F, 4.0012F, -3.998F, 6.0F, 1.0F, 8.0F, CubeDeformation.NONE)
		.texOffs(28, 43).addBox(8.0844F, -3.9988F, -2.998F, 12.0F, 1.0F, 6.0F, CubeDeformation.NONE)
		.texOffs(34, 36).addBox(8.0844F, 3.0012F, -2.998F, 12.0F, 1.0F, 6.0F, CubeDeformation.NONE)
		.texOffs(64, 35).addBox(8.0844F, -2.9988F, -3.998F, 12.0F, 6.0F, 1.0F, CubeDeformation.NONE)
		.texOffs(40, 61).addBox(8.0844F, -2.9988F, 3.002F, 12.0F, 6.0F, 1.0F, CubeDeformation.NONE)
		.texOffs(0, 76).addBox(2.0844F, -3.9988F, 4.002F, 6.0F, 8.0F, 1.0F, CubeDeformation.NONE)
		.texOffs(19, 75).addBox(2.0844F, -3.9988F, -4.998F, 6.0F, 8.0F, 1.0F, CubeDeformation.NONE)
		.texOffs(48, 8).addBox(-17.9156F, -4.9988F, -3.998F, 6.0F, 1.0F, 8.0F, CubeDeformation.NONE)
		.texOffs(0, 60).addBox(-19.9156F, -3.9988F, -3.998F, 2.0F, 8.0F, 8.0F, CubeDeformation.NONE)
		.texOffs(33, 75).addBox(-21.9156F, -1.9988F, -1.998F, 2.0F, 4.0F, 4.0F, CubeDeformation.NONE)
		.texOffs(76, 52).addBox(-22.9156F, -1.9988F, -1.998F, 1.0F, 4.0F, 4.0F, CubeDeformation.NONE)
		.texOffs(71, 0).addBox(-21.9156F, -2.9988F, -1.998F, 2.0F, 1.0F, 4.0F, CubeDeformation.NONE)
		.texOffs(28, 36).addBox(-21.9156F, 2.0012F, -1.998F, 2.0F, 1.0F, 4.0F, CubeDeformation.NONE)
		.texOffs(0, 16).addBox(-21.9156F, -1.9988F, -2.998F, 2.0F, 4.0F, 1.0F, CubeDeformation.NONE)
		.texOffs(0, 0).addBox(-21.9156F, -1.9988F, 2.002F, 2.0F, 4.0F, 1.0F, CubeDeformation.NONE)
		.texOffs(68, 7).addBox(-17.9156F, -3.9988F, -4.998F, 6.0F, 8.0F, 1.0F, CubeDeformation.NONE)
		.texOffs(70, 70).addBox(-17.9156F, -3.9988F, 4.002F, 6.0F, 8.0F, 1.0F, CubeDeformation.NONE)
		.texOffs(56, 17).addBox(-17.9156F, 4.0012F, -3.998F, 6.0F, 1.0F, 8.0F, CubeDeformation.NONE)
		.texOffs(32, 50).addBox(-11.9156F, -3.9988F, -5.998F, 14.0F, 8.0F, 2.0F, CubeDeformation.NONE)
		.texOffs(0, 50).addBox(-11.9156F, -3.9988F, 4.002F, 14.0F, 8.0F, 2.0F, CubeDeformation.NONE)
		.texOffs(62, 50).addBox(-11.9156F, -4.9988F, 4.002F, 14.0F, 1.0F, 1.0F, CubeDeformation.NONE)
		.texOffs(58, 47).addBox(-11.9156F, -4.9988F, -4.998F, 14.0F, 1.0F, 1.0F, CubeDeformation.NONE)
		.texOffs(58, 45).addBox(-11.9156F, 4.0012F, -4.998F, 14.0F, 1.0F, 1.0F, CubeDeformation.NONE)
		.texOffs(58, 43).addBox(-11.9156F, 4.0012F, 4.002F, 14.0F, 1.0F, 1.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(-0.0012F, 4.0844F, -0.002F, 0.0F, 0.0F, 1.5708F));
		return LayerDefinition.create(meshdefinition, 128, 128);
	}
}

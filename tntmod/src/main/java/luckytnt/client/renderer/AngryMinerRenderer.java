package luckytnt.client.renderer;

import luckytnt.entity.AngryMiner;
import net.minecraft.client.render.entity.BipedEntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.feature.ArmorFeatureRenderer;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.resources.Identifier;

public class AngryMinerRenderer extends BipedEntityRenderer<AngryMiner, BipedEntityModel<AngryMiner>>{

	public AngryMinerRenderer(EntityRendererFactory.Context context) {
		super(context, new BipedEntityModel<AngryMiner>(context.getPart(EntityModelLayers.PLAYER)), 0.5f);
		addFeature(new ArmorFeatureRenderer<>(this, new BipedEntityModel<AngryMiner>(context.getPart(EntityModelLayers.PLAYER_INNER_ARMOR)), new BipedEntityModel<AngryMiner>(context.getPart(EntityModelLayers.PLAYER_OUTER_ARMOR)), context.getModelManager()));
	}

	@Override
	public Identifier getTexture(AngryMiner entity) {
		return Identifier.fromNamespaceAndPath("luckytntmod:textures/angryminer.png");
	}
}

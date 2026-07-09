package luckytnt.registry;

import luckytnt.client.model.BombModel;
import net.fabricmc.fabric.api.client.rendering.v1.ModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.ModelLayerRegistry.TexturedLayerDefinitionProvider;
import net.minecraft.client.model.geom.builders.LayerDefinition;

public class ModelRegistry {

	public static void init() {
		ModelLayerRegistry.registerModelLayer(BombModel.LAYER_LOCATION, new TexturedLayerDefinitionProvider() {

			@Override
			public LayerDefinition createLayerDefinition() {
				return BombModel.getTexturedModelData();
			}
		});
	}
}

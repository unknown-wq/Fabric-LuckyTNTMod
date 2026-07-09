package luckytnt.registry;

import luckytnt.client.model.BombModel;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry.TexturedModelDataProvider;
import net.minecraft.client.model.TexturedModelData;

public class ModelRegistry {

	public static void init() {
		EntityModelLayerRegistry.registerModelLayer(BombModel.LAYER_LOCATION, new TexturedModelDataProvider() {
			
			@Override
			public TexturedModelData createModelData() {
				return BombModel.getTexturedModelData();
			}
		});
	}
}

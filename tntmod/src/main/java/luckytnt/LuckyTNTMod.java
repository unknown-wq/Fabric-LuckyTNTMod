package luckytnt;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import luckytnt.client.ClientAccess;
import luckytnt.config.LuckyTNTConfigValues;
import luckytnt.registry.AttributeRegistry;
import luckytnt.registry.BiomeModificationRegistry;
import luckytnt.registry.BlockRegistry;
import luckytnt.registry.ColorRegistry;
import luckytnt.registry.CommandRegistry;
import luckytnt.registry.EffectRegistry;
import luckytnt.registry.EntityRegistry;
import luckytnt.registry.EventRegistry;
import luckytnt.registry.FeatureRegistry;
import luckytnt.registry.ItemRegistry;
import luckytnt.registry.LuckyTNTTabs;
import luckytnt.registry.ModelRegistry;
import luckytnt.registry.NetworkRegistry;
import luckytnt.registry.RenderLayerRegistry;
import luckytnt.registry.RendererRegistry;
import luckytnt.registry.SoundRegistry;
import luckytntlib.registry.RegistryHelper;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.text.Text;

public class LuckyTNTMod implements ModInitializer {

	public static final String MODID = "luckytntmod";
    public static final Logger LOGGER = LoggerFactory.getLogger(MODID);
	public static final RegistryHelper RH = new RegistryHelper(MODID);

	@Override
	public void onInitialize() {
		SoundRegistry.init();
		EntityRegistry.init();
		AttributeRegistry.init();
		BlockRegistry.init();
		ItemRegistry.init();
		LuckyTNTTabs.init();
		EffectRegistry.init();
		FeatureRegistry.init();
		CommandRegistry.init();
		NetworkRegistry.init();
		EventRegistry.init();
		BiomeModificationRegistry.init();
		
		if(FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT) {
			ColorRegistry.init();
			ModelRegistry.init();
			RendererRegistry.init();
			RenderLayerRegistry.init();
			
			RH.registerConfigScreenFactory(Text.literal("Lucky TNT Mod"), ClientAccess.getFactory());
		}
		
		LuckyTNTConfigValues.registerConfig();
	}
}
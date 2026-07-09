package luckytntlib.config.common;

import java.io.File;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

import org.jetbrains.annotations.Nullable;

import luckytntlib.LuckyTNTLib;
import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.world.World;

/**
 * An extension of {@link Config} that should only be used on {@link EnvType#CLIENT} and will neither init nor save on {@link EnvType#SERVER}
 */
public class ClientConfig extends Config {

	ClientConfig(String modid, List<ConfigValue<?>> configValues, Optional<UpdatePacketCreator> packetCreator) {
		super(modid, configValues, packetCreator);
	}

	public void init() {
		if(FabricLoader.getInstance().getEnvironmentType() == EnvType.SERVER) {
			return;
		}
		
		Path path = FabricLoader.getInstance().getConfigDir();
		File file = new File(path.toString() + "\\" + modid + "-client-config.json");

		LuckyTNTLib.LOGGER.info("Init client config for " + modid + " from file " + file.toString());
		
		if(!file.exists()) {
			createConfigFile(file);
		} else {
			loadConfigValues(file);
		}
	}
	
	public void save(@Nullable World world) {
		if(FabricLoader.getInstance().getEnvironmentType() == EnvType.SERVER) {
			return;
		}
		
		Path path = FabricLoader.getInstance().getConfigDir();
		File file = new File(path.toString() + "\\" + modid + "-client-config.json");
		
		LuckyTNTLib.LOGGER.info("Saving client config for " + modid + " to file " + file.toString());
		
		if(!file.exists()) {
			createConfigFile(file);
		} else {
			file.delete();
			createConfigFile(file);
		}
	}
}

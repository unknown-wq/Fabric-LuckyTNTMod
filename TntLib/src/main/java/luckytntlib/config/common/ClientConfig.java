package luckytntlib.config.common;

import java.io.File;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

import org.jetbrains.annotations.Nullable;

import luckytntlib.LuckyTNTLib;
import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.world.level.Level;

/**
 * An extension of {@link Config} that should only be used on {@link EnvType#CLIENT} and will neither init nor save on {@link EnvType#SERVER}
 */
public class ClientConfig extends Config {

	ClientConfig(String modid, List<ConfigValue<?>> configValues, Optional<UpdatePacketCreator> packetCreator) {
		super(modid, configValues, packetCreator);
	}

	/**
	 * @return the file this config is saved to, which lies inside the config directory of the game
	 */
	private File getConfigFile() {
		Path path = FabricLoader.getInstance().getConfigDir();
		return path.resolve(modid + "-client-config.json").toFile();
	}

	public void init() {
		if(FabricLoader.getInstance().getEnvironmentType() == EnvType.SERVER) {
			return;
		}

		File file = getConfigFile();

		LuckyTNTLib.LOGGER.info("Init client config for " + modid + " from file " + file.toString());

		if(!file.exists()) {
			createConfigFile(file);
		} else {
			loadConfigValues(file);
		}
	}

	public void save(@Nullable Level world) {
		if(FabricLoader.getInstance().getEnvironmentType() == EnvType.SERVER) {
			return;
		}

		File file = getConfigFile();

		LuckyTNTLib.LOGGER.info("Saving client config for " + modid + " to file " + file.toString());

		//no need to delete the file first, writing to it truncates it anyway
		createConfigFile(file);
	}
}

package luckytntlib.config.common;

import java.io.File;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

import org.jetbrains.annotations.Nullable;

import luckytntlib.LuckyTNTLib;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;

/**
 * An extension of {@link Config} that should only be used on the server and will not save on the client. <br>
 * If specified, this type of {@link Config} will brodcast all changes to any client connected to server.
 */
public class ServerConfig extends Config {

	ServerConfig(String modid, List<ConfigValue<?>> configValues, Optional<UpdatePacketCreator> packetCreator) {
		super(modid, configValues, packetCreator);
	}

	public void init() {
		Path path = FabricLoader.getInstance().getConfigDir();
		File file = new File(path.toString() + "\\" + modid + "-server-config.json");

		LuckyTNTLib.LOGGER.info("Init server config for " + modid + " from file " + file.toString());

		if(!file.exists()) {
			createConfigFile(file);
		} else {
			loadConfigValues(file);
		}
	}

	public void save(@Nullable Level world) {
		if(world != null && world instanceof ServerLevel sworld) {
			Path path = FabricLoader.getInstance().getConfigDir();
			File file = new File(path.toString() + "\\" + modid + "-server-config.json");

			LuckyTNTLib.LOGGER.info("Saving server config for " + modid + " to file " + file.toString());

			if(!file.exists()) {
				createConfigFile(file);
			} else {
				file.delete();
				createConfigFile(file);
			}

			if(!packetCreator.isEmpty()) {
				for(ServerLevel sw : sworld.getServer().getAllLevels()) {
					for(ServerPlayer player : sw.players()) {
						ServerPlayNetworking.send(player, packetCreator.get().getPacket(configValues));
					}
				}
			}
		}
	}
}

package luckytntlib.config;

import java.util.List;

import luckytntlib.LuckyTNTLib;
import luckytntlib.config.common.Config;
import luckytntlib.config.common.Config.ConfigValue;
import luckytntlib.config.common.Config.UpdatePacketCreator;
import luckytntlib.config.common.ServerConfig;
import luckytntlib.network.UpdateConfigValuesPacket;
import net.minecraft.network.packet.CustomPayload;

public class LuckyTNTLibConfigValues {
	
	public static ServerConfig CONFIG;
	
	public static Config.BooleanValue PERFORMANT_EXPLOSION = new Config.BooleanValue(true, "performantExplosion");
	public static Config.DoubleValue EXPLOSION_PERFORMANCE_FACTOR = new Config.DoubleValue(0.3d, 0.3d, 0.6d, "explosionPerformanceFactor");
	
	private static final UpdatePacketCreator CREATOR = new UpdatePacketCreator() {
		
		@Override
		public CustomPayload getPacket(List<ConfigValue<?>> configValues) {
			return new UpdateConfigValuesPacket(configValues);
		}
	};
	
	public static void registerConfig() {
		CONFIG = Config.Builder.of(LuckyTNTLib.MODID).addConfigValue(PERFORMANT_EXPLOSION).addConfigValue(EXPLOSION_PERFORMANCE_FACTOR).setPacketCreator(CREATOR).buildServer();
		CONFIG.init();
	}
}

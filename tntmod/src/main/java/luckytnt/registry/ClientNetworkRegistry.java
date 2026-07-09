package luckytnt.registry;

import luckytnt.LevelVariables;
import luckytnt.client.ClientAccess;
import luckytnt.config.LuckyTNTConfigValues;
import luckytnt.network.HydrogenBombS2CPacket;
import luckytnt.network.LevelVariablesS2CPacket;
import luckytnt.network.LuckyTNTUpdateConfigValuesPacket;
import luckytntlib.config.common.Config;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking.Context;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking.PlayPayloadHandler;
import net.minecraft.nbt.NbtCompound;

public class ClientNetworkRegistry {
	
	private static final PlayPayloadHandler<LuckyTNTUpdateConfigValuesPacket> UPDATE_S2C = new PlayPayloadHandler<LuckyTNTUpdateConfigValuesPacket>() {
		
		@Override
		public void receive(LuckyTNTUpdateConfigValuesPacket payload, Context context) {
			NbtCompound tag = payload.data;
			
			context.client().execute(() -> {
				Config.writeToValues(tag, LuckyTNTConfigValues.CONFIG.getConfigValues());
			});
		}
	};
	private static final PlayPayloadHandler<HydrogenBombS2CPacket> HYDROGEN_BOMB_S2C = new PlayPayloadHandler<HydrogenBombS2CPacket>() {
		
		@Override
		public void receive(HydrogenBombS2CPacket payload, Context context) {
			int entId = payload.entityId;
			
			context.client().execute(() -> {
				ClientAccess.displayHydrogenBombParticles(entId);
			});
		}
	};
	private static final PlayPayloadHandler<LevelVariablesS2CPacket> LEVEL_VARIABLES_S2C = new PlayPayloadHandler<LevelVariablesS2CPacket>() {
		
		@Override
		public void receive(LevelVariablesS2CPacket payload, Context context) {
			LevelVariables variables = payload.variables;
			
			context.client().execute(() -> {
				ClientAccess.syncLevelVariables(variables);
			});
		}
	};
	
	
	public static void init() {
		ClientPlayNetworking.registerGlobalReceiver(LuckyTNTUpdateConfigValuesPacket.ID, UPDATE_S2C);
		ClientPlayNetworking.registerGlobalReceiver(HydrogenBombS2CPacket.ID, HYDROGEN_BOMB_S2C);
		ClientPlayNetworking.registerGlobalReceiver(LevelVariablesS2CPacket.ID, LEVEL_VARIABLES_S2C);
	}
}

package luckytntlib.registry;

import luckytntlib.config.LuckyTNTLibConfigValues;
import luckytntlib.config.common.Config;
import luckytntlib.network.UpdateConfigValuesPacket;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking.Context;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking.PlayPayloadHandler;
import net.minecraft.nbt.NbtCompound;

public class ClientNetworkRegistry {

	private static final PlayPayloadHandler<UpdateConfigValuesPacket> UPDATE_S2C = new PlayPayloadHandler<UpdateConfigValuesPacket>() {
		
		@Override
		public void receive(UpdateConfigValuesPacket payload, Context ctx) {
			NbtCompound tag = payload.data;
			
			ctx.client().execute(() -> {
				Config.writeToValues(tag, LuckyTNTLibConfigValues.CONFIG.getConfigValues());
			});
		}
	};
	
	public static void init() {
		ClientPlayNetworking.registerGlobalReceiver(UpdateConfigValuesPacket.ID, UPDATE_S2C);
	}
}

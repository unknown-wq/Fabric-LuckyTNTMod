package luckytnt.registry;

import luckytnt.LuckyTNTMod;
import luckytnt.network.LuckyTNTClientReadyC2SPacket;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents.Join;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;

public class ClientEventRegistry {

	private static final Join PLAYER_JOIN_CLIENT = new Join() {
		
		@Override
		public void onPlayReady(ClientPlayNetworkHandler handler, PacketSender sender, MinecraftClient client) {
			LuckyTNTMod.RH.sendC2SPacket(new LuckyTNTClientReadyC2SPacket());
		}
	};
	
	public static void init() {
		ClientPlayConnectionEvents.JOIN.register(PLAYER_JOIN_CLIENT);
	}
}

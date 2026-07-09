package luckytntlib.registry;

import luckytntlib.LuckyTNTLib;
import luckytntlib.network.ClientReadyC2SPacket;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents.Join;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;

public class EventRegistry {
	
	private static final Join PLAYER_JOIN_CLIENT = new Join() {
		
		@Override
		public void onPlayReady(ClientPlayNetworkHandler handler, PacketSender sender, MinecraftClient client) {
			LuckyTNTLib.RH.sendC2SPacket(new ClientReadyC2SPacket());
		}
	};
	
	public static void init() {
		ClientPlayConnectionEvents.JOIN.register(PLAYER_JOIN_CLIENT);
	}
}

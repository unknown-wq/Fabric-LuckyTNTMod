package luckytntlib.registry;

import luckytntlib.LuckyTNTLib;
import luckytntlib.config.LuckyTNTLibConfigValues;
import luckytntlib.config.common.Config;
import luckytntlib.network.ClientReadyC2SPacket;
import luckytntlib.network.UpdateConfigValuesPacket;
import net.fabricmc.api.EnvType;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking.Context;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking.PlayPayloadHandler;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;

public class NetworkRegistry {
	
	private static final PlayPayloadHandler<UpdateConfigValuesPacket> UPDATE_C2S = new PlayPayloadHandler<UpdateConfigValuesPacket>() {
		
		@Override
		public void receive(UpdateConfigValuesPacket payload, Context context) {
			NbtCompound tag = payload.data;
			
			Config.writeToValues(tag, LuckyTNTLibConfigValues.CONFIG.getConfigValues());
			
			LuckyTNTLibConfigValues.CONFIG.save(context.server().getWorld(World.OVERWORLD));
		}
	};
	private static final PlayPayloadHandler<ClientReadyC2SPacket> READY_C2S = new PlayPayloadHandler<ClientReadyC2SPacket>() {
		
		@Override
		public void receive(ClientReadyC2SPacket payload, Context context) {
			for(ServerWorld world : context.server().getWorlds()) {
				for(ServerPlayerEntity entity : world.getPlayers()) {
					LuckyTNTLib.RH.sendS2CPacket(entity, new UpdateConfigValuesPacket(LuckyTNTLibConfigValues.CONFIG.getConfigValues()));
				}
			}
		}
	};

	public static void init() {
		PayloadTypeRegistry.playC2S().register(UpdateConfigValuesPacket.ID, UpdateConfigValuesPacket.CODEC);
		PayloadTypeRegistry.playC2S().register(ClientReadyC2SPacket.ID, ClientReadyC2SPacket.CODEC);
		
		PayloadTypeRegistry.playS2C().register(UpdateConfigValuesPacket.ID, UpdateConfigValuesPacket.CODEC);
		
		if(FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT) {
			ClientNetworkRegistry.init();
		}
		
		ServerPlayNetworking.registerGlobalReceiver(UpdateConfigValuesPacket.ID, UPDATE_C2S);
		ServerPlayNetworking.registerGlobalReceiver(ClientReadyC2SPacket.ID, READY_C2S);
	}
}

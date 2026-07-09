package luckytnt.registry;

import luckytnt.LuckyTNTMod;
import luckytnt.config.LuckyTNTConfigValues;
import luckytnt.network.HydrogenBombS2CPacket;
import luckytnt.network.LevelVariablesS2CPacket;
import luckytnt.network.LuckyTNTClientReadyC2SPacket;
import luckytnt.network.LuckyTNTUpdateConfigValuesPacket;
import luckytntlib.config.common.Config;
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

	private static final PlayPayloadHandler<LuckyTNTUpdateConfigValuesPacket> UPDATE_C2S = new PlayPayloadHandler<LuckyTNTUpdateConfigValuesPacket>() {
		
		@Override
		public void receive(LuckyTNTUpdateConfigValuesPacket payload, Context context) {
			NbtCompound tag = payload.data;
			
			Config.writeToValues(tag, LuckyTNTConfigValues.CONFIG.getConfigValues());
			
			LuckyTNTConfigValues.CONFIG.save(context.server().getWorld(World.OVERWORLD));
		}
	};
	private static final PlayPayloadHandler<LuckyTNTClientReadyC2SPacket> READY_C2S = new PlayPayloadHandler<LuckyTNTClientReadyC2SPacket>() {
		
		@Override
		public void receive(LuckyTNTClientReadyC2SPacket payload, Context context) {
			for(ServerWorld world : context.server().getWorlds()) {
				for(ServerPlayerEntity entity : world.getPlayers()) {
					LuckyTNTMod.RH.sendS2CPacket(entity, new LuckyTNTUpdateConfigValuesPacket(LuckyTNTConfigValues.CONFIG.getConfigValues()));
				}
			}
		}
	};

	public static void init() {
		PayloadTypeRegistry.playC2S().register(LuckyTNTUpdateConfigValuesPacket.ID, LuckyTNTUpdateConfigValuesPacket.CODEC);
		PayloadTypeRegistry.playC2S().register(LuckyTNTClientReadyC2SPacket.ID, LuckyTNTClientReadyC2SPacket.CODEC);

		PayloadTypeRegistry.playS2C().register(LuckyTNTUpdateConfigValuesPacket.ID, LuckyTNTUpdateConfigValuesPacket.CODEC);
		PayloadTypeRegistry.playS2C().register(HydrogenBombS2CPacket.ID, HydrogenBombS2CPacket.CODEC);
		PayloadTypeRegistry.playS2C().register(LevelVariablesS2CPacket.ID, LevelVariablesS2CPacket.CODEC);
		
		if(FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT) {
			ClientNetworkRegistry.init();
		}
		
		ServerPlayNetworking.registerGlobalReceiver(LuckyTNTUpdateConfigValuesPacket.ID, UPDATE_C2S);
		ServerPlayNetworking.registerGlobalReceiver(LuckyTNTClientReadyC2SPacket.ID, READY_C2S);
	}
}

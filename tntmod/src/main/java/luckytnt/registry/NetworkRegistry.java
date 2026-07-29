package luckytnt.registry;

import luckytnt.LevelVariables;
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
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;

public class NetworkRegistry {

	private static final PlayPayloadHandler<LuckyTNTUpdateConfigValuesPacket> UPDATE_C2S = new PlayPayloadHandler<LuckyTNTUpdateConfigValuesPacket>() {
		
		@Override
		public void receive(LuckyTNTUpdateConfigValuesPacket payload, Context context) {
			CompoundTag tag = payload.data;
			
			Config.writeToValues(tag, LuckyTNTConfigValues.CONFIG.getConfigValues());
			
			LuckyTNTConfigValues.CONFIG.save(context.server().getLevel(Level.OVERWORLD));
		}
	};
	private static final PlayPayloadHandler<LuckyTNTClientReadyC2SPacket> READY_C2S = new PlayPayloadHandler<LuckyTNTClientReadyC2SPacket>() {
		
		@Override
		public void receive(LuckyTNTClientReadyC2SPacket payload, Context context) {
			ServerPlayer player = context.player();
			if(player == null) {
				return;
			}
			//only the client that just announced itself needs the config, not every player on the server
			LuckyTNTMod.RH.sendS2CPacket(player, new LuckyTNTUpdateConfigValuesPacket(LuckyTNTConfigValues.CONFIG.getConfigValues()));
			//initial state for the joining client: the level variables used to arrive through the (now conditional) every tick broadcast
			LevelVariables.get(context.server().overworld()).syncTo(player);
		}
	};

	public static void init() {
		PayloadTypeRegistry.serverboundPlay().register(LuckyTNTUpdateConfigValuesPacket.ID, LuckyTNTUpdateConfigValuesPacket.CODEC);
		PayloadTypeRegistry.serverboundPlay().register(LuckyTNTClientReadyC2SPacket.ID, LuckyTNTClientReadyC2SPacket.CODEC);

		PayloadTypeRegistry.clientboundPlay().register(LuckyTNTUpdateConfigValuesPacket.ID, LuckyTNTUpdateConfigValuesPacket.CODEC);
		PayloadTypeRegistry.clientboundPlay().register(HydrogenBombS2CPacket.ID, HydrogenBombS2CPacket.CODEC);
		PayloadTypeRegistry.clientboundPlay().register(LevelVariablesS2CPacket.ID, LevelVariablesS2CPacket.CODEC);
		
		if(FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT) {
			ClientNetworkRegistry.init();
		}
		
		ServerPlayNetworking.registerGlobalReceiver(LuckyTNTUpdateConfigValuesPacket.ID, UPDATE_C2S);
		ServerPlayNetworking.registerGlobalReceiver(LuckyTNTClientReadyC2SPacket.ID, READY_C2S);
	}
}

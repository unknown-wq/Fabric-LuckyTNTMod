package luckytnt.network;

import java.util.List;

import luckytnt.LuckyTNTMod;
import luckytntlib.config.common.Config.ConfigValue;
import luckytntlib.network.UpdateConfigValuesPacket;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

public class LuckyTNTUpdateConfigValuesPacket extends UpdateConfigValuesPacket {

	public static final Identifier NAME = Identifier.fromNamespaceAndPath(LuckyTNTMod.MODID, "lucky_tnt_update_config_values");
	public static final CustomPacketPayload.Type<LuckyTNTUpdateConfigValuesPacket> ID = new CustomPacketPayload.Type<>(NAME);
    public static final StreamCodec<RegistryFriendlyByteBuf, LuckyTNTUpdateConfigValuesPacket> CODEC = StreamCodec.ofMember(UpdateConfigValuesPacket::write, LuckyTNTUpdateConfigValuesPacket::new);
	
	public LuckyTNTUpdateConfigValuesPacket(List<ConfigValue<?>> configValues) {
		super(configValues);
	}
	
	public LuckyTNTUpdateConfigValuesPacket(RegistryFriendlyByteBuf buf) {
		super(buf);
	}
	
	@Override
	public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
		return ID;
	}
}

package luckytnt.network;

import java.util.List;

import luckytnt.LuckyTNTMod;
import luckytntlib.config.common.Config.ConfigValue;
import luckytntlib.network.UpdateConfigValuesPacket;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

public class LuckyTNTUpdateConfigValuesPacket extends UpdateConfigValuesPacket {

	public static final Identifier NAME = Identifier.of(LuckyTNTMod.MODID, "lucky_tnt_update_config_values");
	public static final CustomPayload.Id<LuckyTNTUpdateConfigValuesPacket> ID = new CustomPayload.Id<>(NAME);
    public static final PacketCodec<RegistryByteBuf, LuckyTNTUpdateConfigValuesPacket> CODEC = PacketCodec.of(UpdateConfigValuesPacket::write, LuckyTNTUpdateConfigValuesPacket::new);
	
	public LuckyTNTUpdateConfigValuesPacket(List<ConfigValue<?>> configValues) {
		super(configValues);
	}
	
	public LuckyTNTUpdateConfigValuesPacket(PacketByteBuf buf) {
		super(buf);
	}
	
	@Override
	public Id<? extends CustomPayload> getId() {
		return ID;
	}
}

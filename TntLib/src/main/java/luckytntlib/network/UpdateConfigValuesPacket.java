package luckytntlib.network;

import java.util.List;

import luckytntlib.LuckyTNTLib;
import luckytntlib.config.common.Config;
import luckytntlib.config.common.Config.ConfigValue;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

public class UpdateConfigValuesPacket implements CustomPayload {
	
	public static final Identifier NAME = Identifier.of(LuckyTNTLib.MODID, "update_config_values");
	public static final CustomPayload.Id<UpdateConfigValuesPacket> ID = new CustomPayload.Id<>(NAME);
    public static final PacketCodec<RegistryByteBuf, UpdateConfigValuesPacket> CODEC = PacketCodec.of(UpdateConfigValuesPacket::write, UpdateConfigValuesPacket::new);
	
	public final NbtCompound data;

	public UpdateConfigValuesPacket(List<ConfigValue<?>> configValues) {
		data = Config.valuesToNbtCompound(configValues);
	}
	
	public UpdateConfigValuesPacket(PacketByteBuf buf) {
		data = buf.readNbt();
	}
	
	public void write(PacketByteBuf buf) {
		buf.writeNbt(data);
	}

	@Override
	public Id<? extends CustomPayload> getId() {
		return ID;
	}
}

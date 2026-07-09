package luckytntlib.network;

import java.util.List;

import luckytntlib.LuckyTNTLib;
import luckytntlib.config.common.Config;
import luckytntlib.config.common.Config.ConfigValue;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

public class UpdateConfigValuesPacket implements CustomPacketPayload {

	public static final Identifier NAME = Identifier.fromNamespaceAndPath(LuckyTNTLib.MODID, "update_config_values");
	public static final CustomPacketPayload.Type<UpdateConfigValuesPacket> ID = new CustomPacketPayload.Type<>(NAME);
	public static final StreamCodec<RegistryFriendlyByteBuf, UpdateConfigValuesPacket> CODEC = StreamCodec.ofMember(UpdateConfigValuesPacket::write, UpdateConfigValuesPacket::new);

	public final CompoundTag data;

	public UpdateConfigValuesPacket(List<ConfigValue<?>> configValues) {
		data = Config.valuesToNbtCompound(configValues);
	}

	public UpdateConfigValuesPacket(RegistryFriendlyByteBuf buf) {
		data = buf.readNbt();
	}

	public void write(FriendlyByteBuf buf) {
		buf.writeNbt(data);
	}

	@Override
	public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
		return ID;
	}
}

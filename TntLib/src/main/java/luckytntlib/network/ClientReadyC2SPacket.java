package luckytntlib.network;

import luckytntlib.LuckyTNTLib;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

public class ClientReadyC2SPacket implements CustomPacketPayload {

	public static final Identifier NAME = Identifier.fromNamespaceAndPath(LuckyTNTLib.MODID, "client_ready_c2s");
	public static final CustomPacketPayload.Type<ClientReadyC2SPacket> ID = new CustomPacketPayload.Type<>(NAME);
	public static final StreamCodec<RegistryFriendlyByteBuf, ClientReadyC2SPacket> CODEC = StreamCodec.ofMember(ClientReadyC2SPacket::write, ClientReadyC2SPacket::new);

	public ClientReadyC2SPacket() {
	}

	public ClientReadyC2SPacket(RegistryFriendlyByteBuf buf) {
	}

	public void write(FriendlyByteBuf buf) {
	}

	@Override
	public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
		return ID;
	}
}

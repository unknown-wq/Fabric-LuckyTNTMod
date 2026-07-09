package luckytntlib.network;

import luckytntlib.LuckyTNTLib;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

public class ClientReadyC2SPacket implements CustomPayload {
	
	public static final Identifier NAME = Identifier.of(LuckyTNTLib.MODID, "client_ready_c2s");
	public static final CustomPayload.Id<ClientReadyC2SPacket> ID = new CustomPayload.Id<>(NAME);
    public static final PacketCodec<RegistryByteBuf, ClientReadyC2SPacket> CODEC = PacketCodec.of(ClientReadyC2SPacket::write, ClientReadyC2SPacket::new);
	
	public ClientReadyC2SPacket() {
	}
	
	public ClientReadyC2SPacket(PacketByteBuf buf) {
	}
	
	public void write(PacketByteBuf buf) {
	}

	@Override
	public Id<? extends CustomPayload> getId() {
		return ID;
	}
}

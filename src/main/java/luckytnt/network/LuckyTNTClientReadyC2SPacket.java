package luckytnt.network;

import luckytnt.LuckyTNTMod;
import luckytntlib.network.ClientReadyC2SPacket;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

public class LuckyTNTClientReadyC2SPacket extends ClientReadyC2SPacket {

	public static final Identifier NAME = Identifier.of(LuckyTNTMod.MODID, "lucky_tnt_client_ready_c2s");
	public static final CustomPayload.Id<LuckyTNTClientReadyC2SPacket> ID = new CustomPayload.Id<>(NAME);
    public static final PacketCodec<RegistryByteBuf, LuckyTNTClientReadyC2SPacket> CODEC = PacketCodec.of(ClientReadyC2SPacket::write, LuckyTNTClientReadyC2SPacket::new);
	
	public LuckyTNTClientReadyC2SPacket() {
	}
	
	public LuckyTNTClientReadyC2SPacket(PacketByteBuf buf) {
	}
	
	@Override
	public Id<? extends CustomPayload> getId() {
		return ID;
	}
}

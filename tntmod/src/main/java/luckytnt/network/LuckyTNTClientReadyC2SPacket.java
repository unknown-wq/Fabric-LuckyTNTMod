package luckytnt.network;

import luckytnt.LuckyTNTMod;
import luckytntlib.network.ClientReadyC2SPacket;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

public class LuckyTNTClientReadyC2SPacket extends ClientReadyC2SPacket {

	public static final Identifier NAME = Identifier.fromNamespaceAndPath(LuckyTNTMod.MODID, "lucky_tnt_client_ready_c2s");
	public static final CustomPacketPayload.Type<LuckyTNTClientReadyC2SPacket> ID = new CustomPacketPayload.Type<>(NAME);
    public static final StreamCodec<RegistryFriendlyByteBuf, LuckyTNTClientReadyC2SPacket> CODEC = StreamCodec.ofMember(ClientReadyC2SPacket::write, LuckyTNTClientReadyC2SPacket::new);
	
	public LuckyTNTClientReadyC2SPacket() {
	}
	
	public LuckyTNTClientReadyC2SPacket(FriendlyByteBuf buf) {
	}
	
	@Override
	public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
		return ID;
	}
}

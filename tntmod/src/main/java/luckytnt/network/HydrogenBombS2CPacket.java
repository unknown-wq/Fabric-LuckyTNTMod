package luckytnt.network;

import luckytnt.LuckyTNTMod;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

public class HydrogenBombS2CPacket implements CustomPacketPayload {
	
	public static final Identifier NAME = Identifier.fromNamespaceAndPath(LuckyTNTMod.MODID, "hydrogen_bomb_s2c");
	public static final CustomPacketPayload.Type<HydrogenBombS2CPacket> ID = new CustomPacketPayload.Type<>(NAME);
    public static final StreamCodec<RegistryFriendlyByteBuf, HydrogenBombS2CPacket> CODEC = StreamCodec.ofMember(HydrogenBombS2CPacket::write, HydrogenBombS2CPacket::new);
	
	public final int entityId;
	
	public HydrogenBombS2CPacket(int entityId) {
		this.entityId = entityId;
	}
	
	public HydrogenBombS2CPacket(FriendlyByteBuf buf) {
		entityId = buf.readInt();
	}
	
	public void write(FriendlyByteBuf buf) {
		buf.writeInt(entityId);
	}

	@Override
	public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
		return ID;
	}
}

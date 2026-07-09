package luckytnt.network;

import luckytnt.LuckyTNTMod;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

public class HydrogenBombS2CPacket implements CustomPayload {
	
	public static final Identifier NAME = Identifier.of(LuckyTNTMod.MODID, "hydrogen_bomb_s2c");
	public static final CustomPayload.Id<HydrogenBombS2CPacket> ID = new CustomPayload.Id<>(NAME);
    public static final PacketCodec<RegistryByteBuf, HydrogenBombS2CPacket> CODEC = PacketCodec.of(HydrogenBombS2CPacket::write, HydrogenBombS2CPacket::new);
	
	public final int entityId;
	
	public HydrogenBombS2CPacket(int entityId) {
		this.entityId = entityId;
	}
	
	public HydrogenBombS2CPacket(PacketByteBuf buf) {
		entityId = buf.readInt();
	}
	
	public void write(PacketByteBuf buf) {
		buf.writeInt(entityId);
	}

	@Override
	public Id<? extends CustomPayload> getId() {
		return ID;
	}
}

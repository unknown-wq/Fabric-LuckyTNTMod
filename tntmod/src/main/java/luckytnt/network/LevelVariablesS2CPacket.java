package luckytnt.network;

import luckytnt.LevelVariables;
import luckytnt.LuckyTNTMod;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

public class LevelVariablesS2CPacket implements CustomPacketPayload {

	public static final Identifier NAME = Identifier.fromNamespaceAndPath(LuckyTNTMod.MODID, "level_variables_s2c");
	public static final CustomPacketPayload.Type<LevelVariablesS2CPacket> ID = new CustomPacketPayload.Type<>(NAME);
    public static final StreamCodec<RegistryFriendlyByteBuf, LevelVariablesS2CPacket> CODEC = StreamCodec.ofMember(LevelVariablesS2CPacket::write, LevelVariablesS2CPacket::new);
	
	public final LevelVariables variables;
	
	public LevelVariablesS2CPacket(LevelVariables variables) {
		this.variables = variables;
	}
	
	public LevelVariablesS2CPacket(FriendlyByteBuf buf) {
		variables = LevelVariables.load(buf.readNbt());
	}
	
	public void write(FriendlyByteBuf buf) {
		buf.writeNbt(variables.writeNbt(new CompoundTag()));
	}

	@Override
	public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
		return ID;
	}
	
	
	
}

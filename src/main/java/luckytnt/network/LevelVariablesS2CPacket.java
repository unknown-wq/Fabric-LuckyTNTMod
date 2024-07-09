package luckytnt.network;

import luckytnt.LevelVariables;
import luckytnt.LuckyTNTMod;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

public class LevelVariablesS2CPacket implements CustomPayload {

	public static final Identifier NAME = Identifier.of(LuckyTNTMod.MODID, "level_variables_s2c");
	public static final CustomPayload.Id<LevelVariablesS2CPacket> ID = new CustomPayload.Id<>(NAME);
    public static final PacketCodec<RegistryByteBuf, LevelVariablesS2CPacket> CODEC = PacketCodec.of(LevelVariablesS2CPacket::write, LevelVariablesS2CPacket::new);
	
	public final LevelVariables variables;
	
	public LevelVariablesS2CPacket(LevelVariables variables) {
		this.variables = variables;
	}
	
	public LevelVariablesS2CPacket(PacketByteBuf buf) {
		variables = LevelVariables.load(buf.readNbt());
	}
	
	public void write(PacketByteBuf buf) {
		buf.writeNbt(variables.writeNbt(new NbtCompound(), null));
	}

	@Override
	public Id<? extends CustomPayload> getId() {
		return ID;
	}
	
	
	
}

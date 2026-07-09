package luckytnt;

import luckytnt.network.LevelVariablesS2CPacket;
import net.minecraft.datafixer.DataFixTypes;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.PersistentState;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.WorldAccess;

public class LevelVariables extends PersistentState {

	public int doomsdayTime = 0;
	public int toxicCloudsTime = 0;
	public int iceAgeTime = 0;
	public int heatDeathTime = 0;
	public int tntRainTime = 0;
	
	public static LevelVariables clientSide = new LevelVariables();
	
	@Override
	public NbtCompound writeNbt(NbtCompound tag, RegistryWrapper.WrapperLookup registryLookup) {
		tag.putInt("doomsdayTime", doomsdayTime);
		tag.putInt("toxicCloudsTime", toxicCloudsTime);
		tag.putInt("iceAgeTime", iceAgeTime);
		tag.putInt("heatDeathTime", heatDeathTime);
		tag.putInt("tntRainTime", tntRainTime);
		return tag;
	}
	
	public static LevelVariables load(NbtCompound tag) {
		LevelVariables variables = new LevelVariables();
		variables.read(tag);
		return variables;
	}
	
	public void read(NbtCompound tag) {
		doomsdayTime = tag.getInt("doomsdayTime");
		toxicCloudsTime = tag.getInt("toxicCloudsTime");
		iceAgeTime = tag.getInt("iceAgeTime");
		heatDeathTime = tag.getInt("heatDeathTime");
		tntRainTime = tag.getInt("tntRainTime");
	}
	
	public static LevelVariables get(WorldAccess level) {
		if(level instanceof ServerWorldAccess sLevel)
			return sLevel.toServerWorld().getServer().getOverworld().getPersistentStateManager().getOrCreate(new PersistentState.Type<LevelVariables>(() -> {return new LevelVariables();}, (f, w) -> LevelVariables.load(f), DataFixTypes.LEVEL), "ltm_level_variables");
		else
			return clientSide;
	}
	
	public void sync(ServerWorld level) {
		markDirty();
		for(ServerWorld world : level.getServer().getWorlds()) {
			for(ServerPlayerEntity player : world.getPlayers()) {
				LuckyTNTMod.RH.sendS2CPacket(player, new LevelVariablesS2CPacket(this));
			}
		}
	}
}

package luckytnt;

import luckytnt.network.LevelVariablesS2CPacket;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.core.HolderLookup;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.PersistentState;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.level.LevelAccessor;

public class LevelVariables extends PersistentState {

	public int doomsdayTime = 0;
	public int toxicCloudsTime = 0;
	public int iceAgeTime = 0;
	public int heatDeathTime = 0;
	public int tntRainTime = 0;
	
	public static LevelVariables clientSide = new LevelVariables();
	
	@Override
	public CompoundTag writeNbt(CompoundTag tag, HolderLookup.WrapperLookup registryLookup) {
		tag.putInt("doomsdayTime", doomsdayTime);
		tag.putInt("toxicCloudsTime", toxicCloudsTime);
		tag.putInt("iceAgeTime", iceAgeTime);
		tag.putInt("heatDeathTime", heatDeathTime);
		tag.putInt("tntRainTime", tntRainTime);
		return tag;
	}
	
	public static LevelVariables load(CompoundTag tag) {
		LevelVariables variables = new LevelVariables();
		variables.read(tag);
		return variables;
	}
	
	public void read(CompoundTag tag) {
		doomsdayTime = tag.getInt("doomsdayTime");
		toxicCloudsTime = tag.getInt("toxicCloudsTime");
		iceAgeTime = tag.getInt("iceAgeTime");
		heatDeathTime = tag.getInt("heatDeathTime");
		tntRainTime = tag.getInt("tntRainTime");
	}
	
	public static LevelVariables get(LevelAccessor level) {
		if(level instanceof ServerWorldAccess sLevel)
			return sLevel.toServerWorld().getServer().getOverworld().getPersistentStateManager().getOrCreate(new PersistentState.Type<LevelVariables>(() -> {return new LevelVariables();}, (f, w) -> LevelVariables.load(f), DataFixTypes.LEVEL), "ltm_level_variables");
		else
			return clientSide;
	}
	
	public void sync(ServerLevel level) {
		markDirty();
		for(ServerLevel world : level.getServer().getWorlds()) {
			for(ServerPlayer player : world.getPlayers()) {
				LuckyTNTMod.RH.sendS2CPacket(player, new LevelVariablesS2CPacket(this));
			}
		}
	}
}

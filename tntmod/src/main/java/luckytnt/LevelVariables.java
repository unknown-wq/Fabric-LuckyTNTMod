package luckytnt;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import luckytnt.network.LevelVariablesS2CPacket;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.SavedDataType;

public class LevelVariables extends SavedData {

	public int doomsdayTime = 0;
	public int toxicCloudsTime = 0;
	public int iceAgeTime = 0;
	public int heatDeathTime = 0;
	public int tntRainTime = 0;

	/**
	 * Snapshot of the values as of the last {@link #sync(ServerLevel)}. Initialized to a value that can never
	 * occur so that the very first {@link #syncIfChanged(ServerLevel)} always performs one broadcast.
	 * Not serialized (the codec only covers the five timers).
	 */
	private int syncedDoomsdayTime = -1;
	private int syncedToxicCloudsTime = -1;
	private int syncedIceAgeTime = -1;
	private int syncedHeatDeathTime = -1;
	private int syncedTntRainTime = -1;

	public static LevelVariables clientSide = new LevelVariables();

	public static final Codec<LevelVariables> CODEC = RecordCodecBuilder.create(
		i -> i.group(
				Codec.INT.fieldOf("doomsdayTime").forGetter(v -> v.doomsdayTime),
				Codec.INT.fieldOf("toxicCloudsTime").forGetter(v -> v.toxicCloudsTime),
				Codec.INT.fieldOf("iceAgeTime").forGetter(v -> v.iceAgeTime),
				Codec.INT.fieldOf("heatDeathTime").forGetter(v -> v.heatDeathTime),
				Codec.INT.fieldOf("tntRainTime").forGetter(v -> v.tntRainTime)
			)
			.apply(i, LevelVariables::new)
	);

	public static final SavedDataType<LevelVariables> TYPE = new SavedDataType<>(
		Identifier.fromNamespaceAndPath(LuckyTNTMod.MODID, "ltm_level_variables"), LevelVariables::new, CODEC, DataFixTypes.LEVEL
	);

	public LevelVariables() {
	}

	public LevelVariables(int doomsdayTime, int toxicCloudsTime, int iceAgeTime, int heatDeathTime, int tntRainTime) {
		this.doomsdayTime = doomsdayTime;
		this.toxicCloudsTime = toxicCloudsTime;
		this.iceAgeTime = iceAgeTime;
		this.heatDeathTime = heatDeathTime;
		this.tntRainTime = tntRainTime;
	}

	public static LevelVariables load(CompoundTag tag) {
		LevelVariables variables = new LevelVariables();
		variables.read(tag);
		return variables;
	}

	public void read(CompoundTag tag) {
		doomsdayTime = tag.getIntOr("doomsdayTime", 0);
		toxicCloudsTime = tag.getIntOr("toxicCloudsTime", 0);
		iceAgeTime = tag.getIntOr("iceAgeTime", 0);
		heatDeathTime = tag.getIntOr("heatDeathTime", 0);
		tntRainTime = tag.getIntOr("tntRainTime", 0);
	}

	public CompoundTag writeNbt(CompoundTag tag) {
		tag.putInt("doomsdayTime", doomsdayTime);
		tag.putInt("toxicCloudsTime", toxicCloudsTime);
		tag.putInt("iceAgeTime", iceAgeTime);
		tag.putInt("heatDeathTime", heatDeathTime);
		tag.putInt("tntRainTime", tntRainTime);
		return tag;
	}

	public static LevelVariables get(LevelAccessor level) {
		if(level instanceof ServerLevelAccessor sLevel)
			return sLevel.getLevel().getServer().overworld().getDataStorage().computeIfAbsent(TYPE);
		else
			return clientSide;
	}

	/**
	 * Broadcasts the current state to every player on the server and marks this SavedData dirty.
	 * Only call this when something actually changed - use {@link #syncIfChanged(ServerLevel)} from
	 * per tick code.
	 */
	public void sync(ServerLevel level) {
		setDirty();
		syncedDoomsdayTime = doomsdayTime;
		syncedToxicCloudsTime = toxicCloudsTime;
		syncedIceAgeTime = iceAgeTime;
		syncedHeatDeathTime = heatDeathTime;
		syncedTntRainTime = tntRainTime;
		LevelVariablesS2CPacket packet = new LevelVariablesS2CPacket(this);
		for(ServerLevel world : level.getServer().getAllLevels()) {
			for(ServerPlayer player : world.players()) {
				LuckyTNTMod.RH.sendS2CPacket(player, packet);
			}
		}
	}

	/**
	 * Broadcasts the current state, but only if any of the five timers changed since the last sync.
	 * Called every tick, so the no disaster case must stay allocation free.
	 */
	public void syncIfChanged(ServerLevel level) {
		if(doomsdayTime != syncedDoomsdayTime
			|| toxicCloudsTime != syncedToxicCloudsTime
			|| iceAgeTime != syncedIceAgeTime
			|| heatDeathTime != syncedHeatDeathTime
			|| tntRainTime != syncedTntRainTime) {
			sync(level);
		}
	}

	/**
	 * Sends the current state to a single player. Used to give a joining client its initial state,
	 * which used to be a side effect of the (now conditional) every tick broadcast.
	 */
	public void syncTo(ServerPlayer player) {
		LuckyTNTMod.RH.sendS2CPacket(player, new LevelVariablesS2CPacket(this));
	}
}

package luckytntlib.util;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.syncher.EntityDataSerializer;
import net.minecraft.network.syncher.EntityDataSerializers;

/**
 * Holds custom {@link EntityDataSerializer}s used by Lucky TNT Lib.
 * <p>
 * Minecraft 26.2 removed the built-in {@code NbtCompound}/{@code CompoundTag} tracked-data handler, so the synchronized
 * "persistent data" mechanism (see {@link IExplosiveEntity#getPersistentData()} and {@link LuckyTNTEntityExtension})
 * needs its own serializer. {@link #register()} must be called before any entity type that uses it is registered.
 */
public class LTNTDataSerializers {

	/**
	 * A tracked-data serializer for a synchronized {@link CompoundTag}.
	 */
	public static final EntityDataSerializer<CompoundTag> COMPOUND_TAG = EntityDataSerializer.forValueType(ByteBufCodecs.COMPOUND_TAG);

	/**
	 * Registers all custom serializers. Must run once during mod initialization, before entity types are registered.
	 */
	public static void register() {
		EntityDataSerializers.registerSerializer(COMPOUND_TAG);
	}
}

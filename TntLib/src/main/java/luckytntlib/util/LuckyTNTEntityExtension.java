package luckytntlib.util;

import luckytntlib.mixin.EntityMixin;
import net.minecraft.nbt.CompoundTag;

/**
 * LuckyTNTEntityExtension is used in {@link EntityMixin} to add the possibility for any mod to store additional data that is being
 * synchronized and saved
 */
public interface LuckyTNTEntityExtension {

	/**
	 * Gets the additionally stored {@link CompoundTag}
	 * @return a {@link CompoundTag}
	 */
	CompoundTag getAdditionalPersistentData();

	/**
	 * Sets the {@link CompoundTag} that is stored additionally to a new {@link CompoundTag}
	 * @param nbt  the {@link CompoundTag} that will be the new stored data
	 */
	void setAdditionalPersistentData(CompoundTag nbt);
}

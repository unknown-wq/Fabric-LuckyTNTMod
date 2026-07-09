package luckytntlib.util;

import luckytntlib.mixin.EntityMixin;
import net.minecraft.nbt.NbtCompound;

/**
 * LuckyTNTEntityExtension is used in {@link EntityMixin} to add the possibility for any mod to store additional data that is being
 * synchronized and saved
 */
public interface LuckyTNTEntityExtension {
	
	/**
	 * Gets the additionally stored {@link NbtCompound}
	 * @return a {@link NbtCompound}
	 */
	NbtCompound getAdditionalPersistentData();
	
	/**
	 * Sets the {@link NbtCompound} that is stored additionally to a new {@link NbtCompound}
	 * @param nbt  the {@link NbtCompound} that will be the new stored data
	 */
	void setAdditionalPersistentData(NbtCompound nbt);
}

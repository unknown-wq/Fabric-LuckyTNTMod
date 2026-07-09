package luckytntlib.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import luckytntlib.util.LuckyTNTEntityExtension;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

/**
 * This mixin basically exists to replace the missing {@code CompoundTag Entity.persistentData} that was provided by default by Forge
 * and was commonly used by the LuckyTNTMod to store addditional data.
 * <p>
 * In 26.2 there is no built-in tracked-data serializer for {@link CompoundTag} and {@link Entity} is class-loaded before mod
 * initialization, so this stores the additional data in a plain, save-backed field rather than as synchronized tracked data.
 */
@Mixin(Entity.class)
public abstract class EntityMixin implements LuckyTNTEntityExtension {

	@Unique
	private CompoundTag luckytntlib$additionalData = new CompoundTag();

	@Inject(method = "load", at = @At("HEAD"))
	private void injectionReadNbt(ValueInput input, CallbackInfo info) {
		setAdditionalPersistentData(input.read("AdditionalData", CompoundTag.CODEC).orElse(new CompoundTag()));
	}

	@Inject(method = "saveWithoutId", at = @At("HEAD"))
	private void injectionWriteNbt(ValueOutput output, CallbackInfo info) {
		output.store("AdditionalData", CompoundTag.CODEC, getAdditionalPersistentData());
	}

	@Override
	public CompoundTag getAdditionalPersistentData() {
		return luckytntlib$additionalData;
	}

	@Override
	public void setAdditionalPersistentData(CompoundTag nbt) {
		luckytntlib$additionalData = nbt;
	}
}

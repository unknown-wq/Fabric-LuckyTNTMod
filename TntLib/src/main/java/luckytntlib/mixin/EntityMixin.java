package luckytntlib.mixin;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;

import luckytntlib.util.LuckyTNTEntityExtension;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.world.World;

/**
 * This mixin basically exists to replace the missing {@code NbtCompound Entity.persistentData} that was provided by default by Forge 
 * and was commonly used by the LuckyTNTMod to store addditional data
 */
@Mixin(Entity.class)
public abstract class EntityMixin implements LuckyTNTEntityExtension {

	@Shadow
	@Final
	protected DataTracker dataTracker;
	
	private static final TrackedData<NbtCompound> PERSISTENT_DATA = DataTracker.registerData(Entity.class, TrackedDataHandlerRegistry.NBT_COMPOUND);
	
	@Inject(method = "<init>", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;initDataTracker(Lnet/minecraft/entity/data/DataTracker$Builder;)V"))
	private void injectionConstructor(EntityType<?> type, World world, CallbackInfo info, @Local(ordinal = 0) LocalRef<DataTracker.Builder> builder) {
		DataTracker.Builder build = builder.get();
		build.add(PERSISTENT_DATA, new NbtCompound());
		builder.set(build);
	}
	
	@Inject(method = "readNbt", at = @At("HEAD"))
	private void injectionReadNbt(NbtCompound tag, CallbackInfo info) {
		setAdditionalPersistentData(tag.getCompound("AdditionalData"));
	}
	
	@Inject(method = "writeNbt", at = @At("HEAD"))
	private void injectionWriteNbt(NbtCompound tag, CallbackInfoReturnable<NbtCompound> info) {
		tag.put("AdditionalData", getAdditionalPersistentData());
	}

	@Unique
	public NbtCompound getAdditionalPersistentData() {
		return dataTracker.get(PERSISTENT_DATA);
	}

	@Unique
	public void setAdditionalPersistentData(NbtCompound nbt) {
		dataTracker.set(PERSISTENT_DATA, nbt, true);
	}
}

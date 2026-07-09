package luckytntlib.entity;

import org.jetbrains.annotations.Nullable;

import luckytntlib.util.IExplosiveEntity;
import luckytntlib.util.LTNTDataSerializers;
import luckytntlib.util.tnteffects.PrimedTNTEffect;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.item.PrimedTnt;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.Vec3;

/**
 * A PrimedLTNT is an extension of Minecraft's {@link PrimedTnt}
 * and uses a {@link PrimedTNTEffect} to easily customize the explosion effect and other parameters.
 * It implements {@link IExplosiveEntity}.
 */
public class PrimedLTNT extends PrimedTnt implements IExplosiveEntity{

	@Nullable
	private LivingEntity igniter;
	private PrimedTNTEffect effect;
	private static final EntityDataAccessor<CompoundTag> PERSISTENT_DATA = SynchedEntityData.defineId(PrimedLTNT.class, LTNTDataSerializers.COMPOUND_TAG);

	public PrimedLTNT(EntityType<PrimedLTNT> type, Level level, PrimedTNTEffect effect) {
		super(type, level);
		this.effect = effect;
		double movement = level.getRandom().nextDouble() * (double)(Math.PI * 2F);
		this.setDeltaMovement(-Math.sin(movement) * 0.02D, 0.2F, -Math.cos(movement) * 0.02D);
		this.setTNTFuse(effect.getDefaultFuse(this));
	}

	@Override
	public SoundSource getSoundSource() {
		return SoundSource.MASTER;
	}

	public void setOwner(@Nullable LivingEntity igniter) {
		this.igniter = igniter;
	}

	@Override
	public void defineSynchedData(SynchedEntityData.Builder builder) {
		builder.define(PERSISTENT_DATA, new CompoundTag());
		super.defineSynchedData(builder);
	}

	@Override
	@Nullable
	public LivingEntity getOwner() {
		return igniter;
	}

	@Override
	public PrimedTNTEffect getEffect() {
		return effect;
	}

	@Override
	public void tick() {
		if (!isNoGravity()) {
			setDeltaMovement(getDeltaMovement().add(0.0D, -0.04D, 0.0D));
			updateFluidInteraction();
		}
		move(MoverType.SELF, getDeltaMovement());
		setDeltaMovement(getDeltaMovement().scale(0.98D));
		if (onGround()) {
			setDeltaMovement(getDeltaMovement().multiply(0.7D, -0.5D, 0.7D));
		}
		effect.baseTick(this);
	}

	@Override
	public void addAdditionalSaveData(ValueOutput output) {
		if(igniter != null) {
			output.putInt("igniterID", igniter.getId());
		}
		output.store("PersistentData", CompoundTag.CODEC, getPersistentData());
		super.addAdditionalSaveData(output);
	}

	@Override
	public void readAdditionalSaveData(ValueInput input) {
		if(level().getEntity(input.getIntOr("igniterID", 0)) instanceof LivingEntity lEnt) {
			igniter = lEnt;
		}
		setPersistentData(input.read("PersistentData", CompoundTag.CODEC).orElse(new CompoundTag()));
		super.readAdditionalSaveData(input);
	}

	@Override
	public void setTNTFuse(int fuse) {
		setFuse(fuse);
	}

	@Override
	public int getTNTFuse() {
		return getFuse();
	}

	@Override
	public Vec3 getPos() {
		return position();
	}

	@Override
	public void destroy() {
		discard();
	}

	@Override
	public Level getLevel() {
		return level();
	}

	@Override
	public double x() {
		return getX();
	}

	@Override
	public double y() {
		return getY();
	}

	@Override
	public double z() {
		return getZ();
	}

	@Override
	public LivingEntity owner() {
		return getOwner();
	}

	@Override
	public CompoundTag getPersistentData() {
		return entityData.get(PERSISTENT_DATA);
	}

	@Override
	public void setPersistentData(CompoundTag tag) {
		entityData.set(PERSISTENT_DATA, tag, true);
	}
}

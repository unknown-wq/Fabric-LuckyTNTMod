package luckytntlib.entity;

import org.jetbrains.annotations.Nullable;

import luckytntlib.util.IExplosiveEntity;
import luckytntlib.util.LTNTDataSerializers;
import luckytntlib.util.tnteffects.PrimedTNTEffect;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.item.PrimedTnt;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.Vec3;

/**
 * A LivingPrimedLTNT is a type of TNT designed to have health points,
 * attack damage and be able to use an AI to wander around and interact with the world,
 * while still retaining the abilities of a TNT through its {@link PrimedTNTEffect}.
 * It has to be registered independetly of the {@link PrimedLTNT}
 * because Minecraft's {@link PrimedTnt} does not extend any form of a {@link LivingEntity}.
 * It implements {@link IExplosiveEntity}.
 */
public class LivingPrimedLTNT extends PathfinderMob implements IExplosiveEntity{

	@Nullable
	private LivingEntity igniter;
	private PrimedTNTEffect effect;
	private static final EntityDataAccessor<Integer> DATA_FUSE_ID = SynchedEntityData.defineId(LivingPrimedLTNT.class, EntityDataSerializers.INT);
	private static final EntityDataAccessor<CompoundTag> PERSISTENT_DATA = SynchedEntityData.defineId(LivingPrimedLTNT.class, LTNTDataSerializers.COMPOUND_TAG);

	public LivingPrimedLTNT(EntityType<? extends PathfinderMob> type, Level level, @Nullable PrimedTNTEffect effect) {
		super(type, level);
		this.effect = effect;
		this.setTNTFuse(effect.getDefaultFuse(this));
	}

	@Override
	public void tick() {
		super.tick();
		effect.baseTick(this);
	}

	@Override
	public void defineSynchedData(SynchedEntityData.Builder builder) {
		builder.define(DATA_FUSE_ID, -1);
		builder.define(PERSISTENT_DATA, new CompoundTag());
		super.defineSynchedData(builder);
	}

	@Override
	public void addAdditionalSaveData(ValueOutput output) {
		if(igniter != null) {
			output.putInt("throwerID", igniter.getId());
		}
		output.putShort("Fuse", (short)getTNTFuse());
		output.store("PersistentData", CompoundTag.CODEC, getPersistentData());
		super.addAdditionalSaveData(output);
	}

	@Override
	public void readAdditionalSaveData(ValueInput input) {
		if(level().getEntity(input.getIntOr("throwerID", 0)) instanceof LivingEntity lEnt) {
			igniter = lEnt;
		}
		setTNTFuse(input.getShortOr("Fuse", (short)0));
		setPersistentData(input.read("PersistentData", CompoundTag.CODEC).orElse(new CompoundTag()));
		super.readAdditionalSaveData(input);
	}

	@Override
	public boolean removeWhenFarAway(double distance) {
		return false;
	}

	@Override
	protected void pushEntities() {
	}

	@Override
	public void push(double x, double y, double z) {
	}

	public void setOwner(@Nullable LivingEntity thrower) {
		this.igniter = thrower;
	}

	@Override
	public boolean hurtServer(ServerLevel level, DamageSource source, float amount) {
		return source.is(DamageTypes.FELL_OUT_OF_WORLD);
	}

	@Override
	public int getTNTFuse() {
		return entityData.get(DATA_FUSE_ID);
	}

	@Override
	public void setTNTFuse(int fuse) {
		entityData.set(DATA_FUSE_ID, fuse);
	}

	@Override
	public Level getLevel() {
		return level();
	}

	@Override
	public Vec3 getPos() {
		return position();
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
	public void destroy() {
		discard();
	}

	@Override
	public PrimedTNTEffect getEffect() {
		return effect;
	}

	@Override
	public LivingEntity owner() {
		return igniter;
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

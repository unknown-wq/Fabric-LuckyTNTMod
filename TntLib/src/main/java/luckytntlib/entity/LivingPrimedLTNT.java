package luckytntlib.entity;

import org.jetbrains.annotations.Nullable;

import luckytntlib.util.IExplosiveEntity;
import luckytntlib.util.tnteffects.PrimedTNTEffect;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.TntEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageTypes;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

/**
 * A LivingPrimedLTNT is a type of TNT designed to have health points,
 * attack damage and be able to use an AI to wander around and interact with the world,
 * while still retaining the abilities of a TNT through its {@link PrimedTNTEffect}.
 * It has to be registered independetly of the {@link PrimedLTNT} 
 * because Minecraft's {@link TntEntity} does not extend any form of a {@link LivingEntity}.
 * It implements {@link IExplosiveEntity}.
 */
public class LivingPrimedLTNT extends PathAwareEntity implements IExplosiveEntity{
	
	@Nullable 
	private LivingEntity igniter;
	private PrimedTNTEffect effect;
	private static final TrackedData<Integer> DATA_FUSE_ID = DataTracker.registerData(LivingPrimedLTNT.class, TrackedDataHandlerRegistry.INTEGER);
	private static final TrackedData<NbtCompound> PERSISTENT_DATA = DataTracker.registerData(LivingPrimedLTNT.class, TrackedDataHandlerRegistry.NBT_COMPOUND);
	
	public LivingPrimedLTNT(EntityType<? extends PathAwareEntity> type, World level, @Nullable PrimedTNTEffect effect) {
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
	public void initDataTracker(DataTracker.Builder builder) {
		builder.add(DATA_FUSE_ID, -1);
		builder.add(PERSISTENT_DATA, new NbtCompound());
		super.initDataTracker(builder);
	}
	
	@Override
	public void writeCustomDataToNbt(NbtCompound tag) {
		if(igniter != null) {
			tag.putInt("throwerID", igniter.getId());
		}
		tag.putShort("Fuse", (short)getTNTFuse());
		tag.put("PersistentData", getPersistentData());
		super.writeCustomDataToNbt(tag);
	}
	
	@Override
	public void readCustomDataFromNbt(NbtCompound tag) {
		if(getWorld().getEntityById(tag.getInt("throwerID")) instanceof LivingEntity lEnt) {
			igniter = lEnt;
		}
		setTNTFuse(tag.getShort("Fuse"));
		setPersistentData(tag.getCompound("PersistentData"));
		super.readCustomDataFromNbt(tag);
	}
	
	@Override
	public boolean canImmediatelyDespawn(double distance) {
		return false;
	}
	
	@Override
	public void tickCramming() {
	}
	
	@Override
	public void addVelocity(double x, double y, double z) {
	}
	
	public void setOwner(@Nullable LivingEntity thrower) {
		this.igniter = thrower;
	}

	@Override
	public boolean damage(DamageSource source, float amount) {
		if(source.isOf(DamageTypes.OUT_OF_WORLD)) {
			return true;
		}
		return false;
	}

	@Override
	public int getTNTFuse() {
		return dataTracker.get(DATA_FUSE_ID);
	}

	@Override
	public void setTNTFuse(int fuse) {
		dataTracker.set(DATA_FUSE_ID, fuse);
	}

	@Override
	public World getLevel() {
		return getWorld();
	}

	@Override
	public Vec3d getPos() {
		return getLerpedPos(1);
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
	public NbtCompound getPersistentData() {
		return dataTracker.get(PERSISTENT_DATA);
	}
	
	@Override
	public void setPersistentData(NbtCompound tag) {
		dataTracker.set(PERSISTENT_DATA, tag, true);
	}
}

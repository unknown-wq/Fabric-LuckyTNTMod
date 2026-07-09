package luckytntlib.entity;

import org.jetbrains.annotations.Nullable;

import luckytntlib.util.IExplosiveEntity;
import luckytntlib.util.tnteffects.PrimedTNTEffect;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.TntEntity;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

/**
 * A PrimedLTNT is an extension of Minecraft's {@link TntEntity}
 * and uses a {@link PrimedTNTEffect} to easily customize the explosion effect and other parameters.
 * It implements {@link IExplosiveEntity}.
 */
public class PrimedLTNT extends TntEntity implements IExplosiveEntity{

	@Nullable
	private LivingEntity igniter;
	private PrimedTNTEffect effect;
	private static final TrackedData<NbtCompound> PERSISTENT_DATA = DataTracker.registerData(PrimedLTNT.class, TrackedDataHandlerRegistry.NBT_COMPOUND);
	
	public PrimedLTNT(EntityType<PrimedLTNT> type, World level, PrimedTNTEffect effect) {
		super(type, level);
		this.effect = effect;
	    double movement = level.random.nextDouble() * (double)(Math.PI * 2F);
	    this.setVelocity(-Math.sin(movement) * 0.02D, 0.2F, -Math.cos(movement) * 0.02D);
	    this.setTNTFuse(effect.getDefaultFuse(this));
	}
	
	@Override
	public SoundCategory getSoundCategory() {
		return SoundCategory.MASTER;
	}
	
	public void setOwner(@Nullable LivingEntity igniter) {
		this.igniter = igniter;
	}
	
	@Override
    public void initDataTracker(DataTracker.Builder builder) {
		builder.add(PERSISTENT_DATA, new NbtCompound());
		super.initDataTracker(builder);
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
		if (!hasNoGravity()) {
			setVelocity(getVelocity().add(0.0D, -0.04D, 0.0D));
			updateWaterState();
		}
		move(MovementType.SELF, getVelocity());
		setVelocity(getVelocity().multiply(0.98D));
		if (isOnGround()) {
			setVelocity(getVelocity().multiply(0.7D, -0.5D, 0.7D));
		}
		effect.baseTick(this);
	}
	
	@Override
	public void writeCustomDataToNbt(NbtCompound tag) {
		if(igniter != null) {
			tag.putInt("igniterID", igniter.getId());
		}
		tag.put("PersistentData", getPersistentData());
		super.writeCustomDataToNbt(tag);
	}
	
	@Override
	public void readCustomDataFromNbt(NbtCompound tag) {
		if(getWorld().getEntityById(tag.getInt("igniterID")) instanceof LivingEntity lEnt) {
			igniter = lEnt;
		}
		setPersistentData(tag.getCompound("PersistentData"));
		super.readCustomDataFromNbt(tag);
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
	public Vec3d getPos() {
		return getLerpedPos(1);
	}

	@Override
	public void destroy() {
		discard();
	}
	
	@Override
	public World getLevel() {
		return getWorld();
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
	public NbtCompound getPersistentData() {
		return dataTracker.get(PERSISTENT_DATA);
	}

	@Override
	public void setPersistentData(NbtCompound tag) {
		dataTracker.set(PERSISTENT_DATA, tag, true);
	}
}

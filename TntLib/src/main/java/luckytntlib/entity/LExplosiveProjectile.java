package luckytntlib.entity;

import org.jetbrains.annotations.Nullable;

import luckytntlib.util.IExplosiveEntity;
import luckytntlib.util.tnteffects.PrimedTNTEffect;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.FlyingItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

/**
 * The LExplosiveProjectile is an extension of Minecraft's {@link PersistentProjectileEntity} 
 * and represents a projectile that holds a {@link PrimedTNTEffect}.
 * Unlike a {@link PrimedLTNT} a LExplosiveProjectile has access to other types of logic specifically designed
 * for entities that travel through the world with high speeds and hit blocks or entities, while still retaining the abilities of a TNT
 * through its {@link PrimedTNTEffect}.
 * It implements {@link IExplosiveEntity} and {@link FlyingItemEntity}.
 */
public class LExplosiveProjectile extends PersistentProjectileEntity implements IExplosiveEntity, FlyingItemEntity{
	
	private static final TrackedData<Integer> DATA_FUSE_ID = DataTracker.registerData(LExplosiveProjectile.class, TrackedDataHandlerRegistry.INTEGER);
	private static final TrackedData<NbtCompound> PERSISTENT_DATA = DataTracker.registerData(LExplosiveProjectile.class, TrackedDataHandlerRegistry.NBT_COMPOUND);
	@Nullable
	private LivingEntity thrower;
	private boolean hitEntity = false;
	private PrimedTNTEffect effect;
	
	public LExplosiveProjectile(EntityType<LExplosiveProjectile> type, World level, PrimedTNTEffect effect) {
		super(type, 0, 0, 0, level, new ItemStack(Items.CARROT), null);
		setTNTFuse(effect.getDefaultFuse(this));
		pickupType = PersistentProjectileEntity.PickupPermission.DISALLOWED;
		this.effect = effect;
		setStack(getDefaultItemStack());
	}
	
	@Override
	public void onBlockHit(BlockHitResult hitResult) {
		Vec3d pos = hitResult.getPos().subtract(this.getX(), this.getY(), this.getZ());
		setVelocity(pos);
		Vec3d pos2 = pos.normalize().multiply((double) 0.05F);
		setPos(this.getX() - pos2.x, this.getY() - pos2.y, this.getZ() - pos2.z);
	    inGround = true;
	}
	
	@Override
	public void onEntityHit(EntityHitResult hitResult) {
		if(hitResult.getEntity() instanceof PlayerEntity player) {
			if(!(player.isCreative() || player.isSpectator())) {
				hitEntity = true;
			}
		}
		else {
			hitEntity = true;
		}
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
		if(thrower != null) {
			tag.putInt("throwerID", thrower.getId());
		}
		tag.putShort("Fuse", (short)getTNTFuse());
		tag.put("PersistentData", getPersistentData());
		super.writeCustomDataToNbt(tag);
	}
	
	@Override
	public void readCustomDataFromNbt(NbtCompound tag) {
		if(getWorld().getEntityById(tag.getInt("throwerID")) instanceof LivingEntity lEnt) {
			thrower = lEnt;
		}
		setTNTFuse(tag.getShort("Fuse"));
		setPersistentData(tag.getCompound("PersistentData"));
		super.readCustomDataFromNbt(tag);
	}
	
	public PrimedTNTEffect getEffect() {
		return effect;
	}
	
	public boolean inGround() {
		return inGround;
	}
	
	public boolean hitEntity() {
		return hitEntity;
	}
	
	@Override
	public void setTNTFuse(int fuse) {
		dataTracker.set(DATA_FUSE_ID, fuse);
	}
	
	public void setOwner(@Nullable LivingEntity thrower) {
		this.thrower = thrower;
	}
	
	@Override
	public void setOwner(Entity entity) {
		thrower = entity instanceof LivingEntity ? (LivingEntity) entity : thrower;
	}
	
	@Override
	@Nullable
	public LivingEntity getOwner() {
		return thrower;
	}
	
	@Override
	public ItemStack asItemStack() {
		return getStack();
	}
	
	@Override
	public int getTNTFuse() {
		return dataTracker.get(DATA_FUSE_ID);
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
	public ItemStack getStack() {
		return effect == null ? new ItemStack(Items.CARROT) : effect.getItemStack();
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

	@Override
	protected ItemStack getDefaultItemStack() {
		return new ItemStack(Items.CARROT);
	}
}

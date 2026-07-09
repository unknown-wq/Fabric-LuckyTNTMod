package luckytntlib.entity;

import org.jetbrains.annotations.Nullable;

import luckytntlib.util.IExplosiveEntity;
import luckytntlib.util.LTNTDataSerializers;
import luckytntlib.util.tnteffects.PrimedTNTEffect;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ItemSupplier;
import net.minecraft.world.entity.projectile.arrow.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;

/**
 * The LExplosiveProjectile is an extension of Minecraft's {@link AbstractArrow}
 * and represents a projectile that holds a {@link PrimedTNTEffect}.
 * Unlike a {@link PrimedLTNT} a LExplosiveProjectile has access to other types of logic specifically designed
 * for entities that travel through the world with high speeds and hit blocks or entities, while still retaining the abilities of a TNT
 * through its {@link PrimedTNTEffect}.
 * It implements {@link IExplosiveEntity} and {@link ItemSupplier}.
 */
public class LExplosiveProjectile extends AbstractArrow implements IExplosiveEntity, ItemSupplier{

	private static final EntityDataAccessor<Integer> DATA_FUSE_ID = SynchedEntityData.defineId(LExplosiveProjectile.class, EntityDataSerializers.INT);
	private static final EntityDataAccessor<CompoundTag> PERSISTENT_DATA = SynchedEntityData.defineId(LExplosiveProjectile.class, LTNTDataSerializers.COMPOUND_TAG);
	@Nullable
	private LivingEntity thrower;
	private boolean hitEntity = false;
	private PrimedTNTEffect effect;

	public LExplosiveProjectile(EntityType<LExplosiveProjectile> type, Level level, PrimedTNTEffect effect) {
		super(type, 0, 0, 0, level, new ItemStack(Items.CARROT), null);
		setTNTFuse(effect.getDefaultFuse(this));
		pickup = AbstractArrow.Pickup.DISALLOWED;
		this.effect = effect;
	}

	@Override
	public void onHitBlock(BlockHitResult hitResult) {
		Vec3 pos = hitResult.getLocation().subtract(this.getX(), this.getY(), this.getZ());
		setDeltaMovement(pos);
		Vec3 pos2 = pos.normalize().scale((double) 0.05F);
		setPos(this.getX() - pos2.x, this.getY() - pos2.y, this.getZ() - pos2.z);
		setInGround(true);
	}

	@Override
	public void onHitEntity(EntityHitResult hitResult) {
		if(hitResult.getEntity() instanceof Player player) {
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
	public void defineSynchedData(SynchedEntityData.Builder builder) {
		builder.define(DATA_FUSE_ID, -1);
		builder.define(PERSISTENT_DATA, new CompoundTag());
		super.defineSynchedData(builder);
	}

	@Override
	public void addAdditionalSaveData(ValueOutput output) {
		if(thrower != null) {
			output.putInt("throwerID", thrower.getId());
		}
		output.putShort("Fuse", (short)getTNTFuse());
		output.store("PersistentData", CompoundTag.CODEC, getPersistentData());
		super.addAdditionalSaveData(output);
	}

	@Override
	public void readAdditionalSaveData(ValueInput input) {
		if(level().getEntity(input.getIntOr("throwerID", 0)) instanceof LivingEntity lEnt) {
			thrower = lEnt;
		}
		setTNTFuse(input.getShortOr("Fuse", (short)0));
		setPersistentData(input.read("PersistentData", CompoundTag.CODEC).orElse(new CompoundTag()));
		super.readAdditionalSaveData(input);
	}

	public PrimedTNTEffect getEffect() {
		return effect;
	}

	public boolean inGround() {
		return isInGround();
	}

	public boolean hitEntity() {
		return hitEntity;
	}

	@Override
	public void setTNTFuse(int fuse) {
		entityData.set(DATA_FUSE_ID, fuse);
	}

	public void setOwner(@Nullable LivingEntity thrower) {
		this.thrower = thrower;
	}

	@Override
	public void setOwner(@Nullable Entity entity) {
		thrower = entity instanceof LivingEntity ? (LivingEntity) entity : thrower;
	}

	@Override
	@Nullable
	public LivingEntity getOwner() {
		return thrower;
	}

	@Override
	public int getTNTFuse() {
		return entityData.get(DATA_FUSE_ID);
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
	public ItemStack getItem() {
		return effect == null ? new ItemStack(Items.CARROT) : effect.getItemStack();
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

	@Override
	protected ItemStack getDefaultPickupItem() {
		return new ItemStack(Items.CARROT);
	}
}

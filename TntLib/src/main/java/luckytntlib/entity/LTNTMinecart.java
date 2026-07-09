package luckytntlib.entity;

import java.util.function.Supplier;

import org.jetbrains.annotations.Nullable;

import luckytntlib.item.LTNTMinecartItem;
import luckytntlib.util.IExplosiveEntity;
import luckytntlib.util.tnteffects.PrimedTNTEffect;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageTypes;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.entity.vehicle.AbstractMinecartEntity;
import net.minecraft.entity.vehicle.MinecartEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;

/**
 * The LTNTMinecart is an extension of Minecraft's {@link AbstractMinecartEntity}
 * and can hold an already existing {@link PrimedLTNT} and its {@link PrimedTNTEffect}.
 * It implements {@link IExplosiveEntity}.
 */
public class LTNTMinecart extends MinecartEntity implements IExplosiveEntity{

	private static final TrackedData<Integer> DATA_FUSE_ID = DataTracker.registerData(LTNTMinecart.class, TrackedDataHandlerRegistry.INTEGER);
	private static final TrackedData<NbtCompound> PERSISTENT_DATA = DataTracker.registerData(LTNTMinecart.class, TrackedDataHandlerRegistry.NBT_COMPOUND);
	private boolean explodeInstantly;
	protected PrimedTNTEffect effect;
	protected Supplier<Supplier<LTNTMinecartItem>> pickItem;
	public LivingEntity placer;
	
	public LTNTMinecart(EntityType<LTNTMinecart> type, World level, Supplier<EntityType<PrimedLTNT>> TNT, Supplier<Supplier<LTNTMinecartItem>> pickItem, boolean explodeInstantly) {
		super(type, level);
		if(TNT != null) {
			PrimedLTNT tnt = TNT.get().create(level);
			this.effect = tnt.getEffect();
			tnt.discard();
		}
		else if(!(this instanceof LuckyTNTMinecart)) {
			discard();
		}
		this.explodeInstantly = explodeInstantly;
		this.pickItem = pickItem;
		setTNTFuse(-1);
	}
	
	@Override
	public void tick() {
		super.tick();
		if(getTNTFuse() >= 0) {
			getEffect().baseTick(this);
		}
		if(horizontalCollision && getVelocity().horizontalLengthSquared() >= 0.01f && getTNTFuse() < 0) {
			if(explodesInstantly()) {
				fuse();
				setTNTFuse(0);
			}
			else {
				fuse();
			}
		}
	}

	@Override
	public ActionResult interact(PlayerEntity player, Hand hand) {
		return ActionResult.PASS;
	}
	
	@Override
	public boolean damage(DamageSource source, float amount) {
		if (!getWorld().isClient() && !isRemoved()) {
			Entity entity = source.getSource();
			if (entity instanceof PersistentProjectileEntity abstractarrow) {
				if (abstractarrow.isOnFire() && getTNTFuse() < 0) {
					fuse();
				}
			}
			if(source.isOf(DamageTypes.LIGHTNING_BOLT) && getTNTFuse() >= 0) {
				return false;
			}
			if (isInvulnerableTo(source)) {
				return false;
			} else {
				setDamageWobbleSide(-getDamageWobbleSide());
				setDamageWobbleTicks(10);
				scheduleVelocityUpdate();
				setDamageWobbleStrength(getDamageWobbleStrength() + amount * 10.0F);
				emitGameEvent(GameEvent.ENTITY_DAMAGE, source.getAttacker());
				boolean flag = source.getAttacker() instanceof PlayerEntity && ((PlayerEntity) source.getAttacker()).getAbilities().creativeMode;
				if (flag || getDamageWobbleStrength() > 40.0F) {
					removeAllPassengers();
					if (flag && !hasCustomName()) {
						discard();
					} else {
					}
				}

				return true;
			}
		} else {
			return true;
		}
	}
	
	@Override
	public void killAndDropSelf(DamageSource source) {
		double speed = getVelocity().horizontalLengthSquared();
		if (!source.isOf(DamageTypes.ON_FIRE) && !source.isOf(DamageTypes.EXPLOSION) && !(speed >= 0.01f)) {
			super.killAndDropSelf(source);
		} else {
			if(getTNTFuse() < 0) {
				if(explodesInstantly()) {
					fuse();
					World level = getWorld();
					setTNTFuse(getEffect().getDefaultFuse(this) / 4 + level.random.nextInt(getEffect().getDefaultFuse(this)) / 4);
				}
				else {
					fuse();
				}
			}
		}
	}
	
	@Override
	public boolean handleFallDamage(float ditance, float damage, DamageSource source) {
		if (ditance >= 3.0F && getTNTFuse() < 0) {
			if(explodesInstantly()) {
				fuse();
				setTNTFuse(0);
			}
			else {
				fuse();
			}
		}

		return super.handleFallDamage(ditance, damage, source);
	}
	
	@Override
	public void onActivatorRail(int x, int y, int z, boolean active) {
		if(active && getTNTFuse() < 0) {
			fuse();
		}
	}

	public void fuse() {
		setTNTFuse(getEffect().getDefaultFuse(this));	
		getWorld().playSound(null, new BlockPos((int)getLerpedPos(1).x, (int)getLerpedPos(1).y, (int)getLerpedPos(1).z), SoundEvents.ENTITY_TNT_PRIMED, getSoundCategory(), 1f, 1f);
	}
	
	@Override
	public void initDataTracker(DataTracker.Builder builder) {
		builder.add(DATA_FUSE_ID, -1);
		builder.add(PERSISTENT_DATA, new NbtCompound());
		super.initDataTracker(builder);
	}
	
	@Nullable
	public LivingEntity getOwner() {
		return placer;
	}
	
	public void setOwner(LivingEntity owner) {
		this.placer = owner;
	}
	
	@Override
	public ItemStack getPickBlockStack() {
		return new ItemStack(pickItem.get().get());
	}
	
	@Override
	protected Item asItem() {
		return pickItem.get().get();
	}
	
	@Override
	public BlockState getContainedBlock() {
		return getEffect().getBlockState(this);
	}
	
	@Override
	public void writeCustomDataToNbt(NbtCompound tag) {
		if(placer != null) {
			tag.putInt("placerID", placer.getId());
		}
		tag.putShort("Fuse", (short)getTNTFuse());
		tag.put("PersistentData", getPersistentData());
		super.writeCustomDataToNbt(tag);
	}
	
	@Override
	public void readCustomDataFromNbt(NbtCompound tag) {
		if(getWorld().getEntityById(tag.getInt("placerID")) instanceof LivingEntity lEnt) {
			placer = lEnt;
		}
		setTNTFuse(tag.getShort("Fuse"));
		setPersistentData(tag.getCompound("PersistentData"));
		super.readCustomDataFromNbt(tag);
	}
	
	@Override
	public Type getMinecartType() {
		return AbstractMinecartEntity.Type.TNT;
	}
		
	public boolean explodesInstantly() {
		return explodeInstantly;
	}
	
	public PrimedTNTEffect getEffect() {
		return effect;
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

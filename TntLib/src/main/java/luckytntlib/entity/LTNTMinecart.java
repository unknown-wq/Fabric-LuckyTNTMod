package luckytntlib.entity;

import java.util.function.Supplier;

import org.jetbrains.annotations.Nullable;

import luckytntlib.item.LTNTMinecartItem;
import luckytntlib.util.IExplosiveEntity;
import luckytntlib.util.LTNTDataSerializers;
import luckytntlib.util.tnteffects.PrimedTNTEffect;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.arrow.AbstractArrow;
import net.minecraft.world.entity.vehicle.minecart.Minecart;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.Vec3;

/**
 * The LTNTMinecart is an extension of Minecraft's {@link Minecart}
 * and can hold an already existing {@link PrimedLTNT} and its {@link PrimedTNTEffect}.
 * It implements {@link IExplosiveEntity}.
 */
public class LTNTMinecart extends Minecart implements IExplosiveEntity{

	private static final EntityDataAccessor<Integer> DATA_FUSE_ID = SynchedEntityData.defineId(LTNTMinecart.class, EntityDataSerializers.INT);
	private static final EntityDataAccessor<CompoundTag> PERSISTENT_DATA = SynchedEntityData.defineId(LTNTMinecart.class, LTNTDataSerializers.COMPOUND_TAG);
	private boolean explodeInstantly;
	protected PrimedTNTEffect effect;
	protected Supplier<Supplier<LTNTMinecartItem>> pickItem;
	public LivingEntity placer;

	public LTNTMinecart(EntityType<LTNTMinecart> type, Level level, Supplier<EntityType<PrimedLTNT>> TNT, Supplier<Supplier<LTNTMinecartItem>> pickItem, boolean explodeInstantly) {
		super(type, level);
		if(TNT != null) {
			PrimedLTNT tnt = TNT.get().create(level, EntitySpawnReason.TRIGGERED);
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
		if(horizontalCollision && getDeltaMovement().horizontalDistanceSqr() >= 0.01f && getTNTFuse() < 0) {
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
	public InteractionResult interact(Player player, InteractionHand hand, Vec3 location) {
		return InteractionResult.PASS;
	}

	@Override
	public boolean hurtServer(ServerLevel level, DamageSource source, float amount) {
		if (!isRemoved()) {
			Entity entity = source.getDirectEntity();
			if (entity instanceof AbstractArrow abstractarrow) {
				if (abstractarrow.isOnFire() && getTNTFuse() < 0) {
					fuse();
				}
			}
			if(source.is(DamageTypes.LIGHTNING_BOLT) && getTNTFuse() >= 0) {
				return false;
			}
			if (isInvulnerableToBase(source)) {
				return false;
			} else {
				setHurtDir(-getHurtDir());
				setHurtTime(10);
				markHurt();
				setDamage(getDamage() + amount * 10.0F);
				gameEvent(GameEvent.ENTITY_DAMAGE, source.getEntity());
				boolean flag = source.getEntity() instanceof Player && ((Player) source.getEntity()).getAbilities().instabuild;
				if (flag || getDamage() > 40.0F) {
					ejectPassengers();
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
	protected void destroy(ServerLevel level, DamageSource source) {
		double speed = getDeltaMovement().horizontalDistanceSqr();
		if (!source.is(DamageTypes.ON_FIRE) && !source.is(DamageTypes.EXPLOSION) && !(speed >= 0.01f)) {
			super.destroy(level, source);
		} else {
			if(getTNTFuse() < 0) {
				if(explodesInstantly()) {
					fuse();
					setTNTFuse(getEffect().getDefaultFuse(this) / 4 + level.getRandom().nextInt(getEffect().getDefaultFuse(this)) / 4);
				}
				else {
					fuse();
				}
			}
		}
	}

	@Override
	public boolean causeFallDamage(double distance, float damage, DamageSource source) {
		if (distance >= 3.0F && getTNTFuse() < 0) {
			if(explodesInstantly()) {
				fuse();
				setTNTFuse(0);
			}
			else {
				fuse();
			}
		}

		return super.causeFallDamage(distance, damage, source);
	}

	@Override
	public void activateMinecart(ServerLevel level, int x, int y, int z, boolean active) {
		if(active && getTNTFuse() < 0) {
			fuse();
		}
	}

	public void fuse() {
		setTNTFuse(getEffect().getDefaultFuse(this));
		level().playSound(null, new BlockPos((int)position().x, (int)position().y, (int)position().z), SoundEvents.TNT_PRIMED, getSoundSource(), 1f, 1f);
	}

	@Override
	public void defineSynchedData(SynchedEntityData.Builder builder) {
		builder.define(DATA_FUSE_ID, -1);
		builder.define(PERSISTENT_DATA, new CompoundTag());
		super.defineSynchedData(builder);
	}

	@Nullable
	public LivingEntity getOwner() {
		return placer;
	}

	public void setOwner(LivingEntity owner) {
		this.placer = owner;
	}

	@Override
	public ItemStack getPickResult() {
		return new ItemStack(pickItem.get().get());
	}

	@Override
	protected Item getDropItem() {
		return pickItem.get().get();
	}

	@Override
	public BlockState getDisplayBlockState() {
		return getEffect().getBlockState(this);
	}

	@Override
	public void addAdditionalSaveData(ValueOutput output) {
		if(placer != null) {
			output.putInt("placerID", placer.getId());
		}
		output.putShort("Fuse", (short)getTNTFuse());
		output.store("PersistentData", CompoundTag.CODEC, getPersistentData());
		super.addAdditionalSaveData(output);
	}

	@Override
	public void readAdditionalSaveData(ValueInput input) {
		if(level().getEntity(input.getIntOr("placerID", 0)) instanceof LivingEntity lEnt) {
			placer = lEnt;
		}
		setTNTFuse(input.getShortOr("Fuse", (short)0));
		setPersistentData(input.read("PersistentData", CompoundTag.CODEC).orElse(new CompoundTag()));
		super.readAdditionalSaveData(input);
	}

	public boolean explodesInstantly() {
		return explodeInstantly;
	}

	public PrimedTNTEffect getEffect() {
		return effect;
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

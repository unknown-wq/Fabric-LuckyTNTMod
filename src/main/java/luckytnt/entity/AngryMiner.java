package luckytnt.entity;

import luckytnt.registry.ItemRegistry;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.RangedAttackMob;
import net.minecraft.entity.ai.goal.ActiveTargetGoal;
import net.minecraft.entity.ai.goal.EscapeDangerGoal;
import net.minecraft.entity.ai.goal.LookAroundGoal;
import net.minecraft.entity.ai.goal.ProjectileAttackGoal;
import net.minecraft.entity.ai.goal.RevengeGoal;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.ai.goal.WanderAroundFarGoal;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class AngryMiner extends HostileEntity implements RangedAttackMob {
	
	public AngryMiner(EntityType<AngryMiner> type, World level) {
		super(type, level);
		equipStack(EquipmentSlot.MAINHAND, new ItemStack(ItemRegistry.DYNAMITE.get()));
	}
	
	@Override
	public void initGoals() {
		super.initGoals();
		targetSelector.add(0, new ActiveTargetGoal<PlayerEntity>(this, PlayerEntity.class, false, false));
		targetSelector.add(1, new ActiveTargetGoal<IronGolemEntity>(this, IronGolemEntity.class, false, false));
		targetSelector.add(2, new RevengeGoal(this, PlayerEntity.class).setGroupRevenge(getClass()));
		goalSelector.add(3, new EscapeDangerGoal(this, 1.2f));
		goalSelector.add(4, new WanderAroundFarGoal(this, 1));
		goalSelector.add(5, new LookAroundGoal(this));
		goalSelector.add(6, new SwimGoal(this));
		goalSelector.add(0, new ProjectileAttackGoal(this, 1.25f, 20, 10));
	}
	
	@Override
	public void shootAt(LivingEntity entity, float strength) {
		double xVel = entity.getX() - getX();
		double yVel = entity.getY() + getStandingEyeHeight() - 1.1f;
		double zVel = entity.getZ() - getZ();
		ItemRegistry.DYNAMITE.get().shoot(getWorld(), getX(), getY() + getStandingEyeHeight(), getZ(), new Vec3d(xVel, yVel - getY() - getStandingEyeHeight() + Math.sqrt(xVel * xVel + zVel * zVel) * 0.2f, zVel), 2, this);
	}
	
	@Override
	public void dropEquipment(ServerWorld world, DamageSource source, boolean causedByPlayer) {
		super.dropEquipment(world, source, causedByPlayer);
		dropItem(ItemRegistry.DYNAMITE.get());
	}
		
	@Override
	public boolean canImmediatelyDespawn(double distance) {
		return false;
	}
	
	public static DefaultAttributeContainer.Builder createAttributes() {
		return MobEntity.createMobAttributes()
			.add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.3f)
			.add(EntityAttributes.GENERIC_MAX_HEALTH, 40);
	}
}

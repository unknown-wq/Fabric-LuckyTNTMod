package luckytnt.entity;

import luckytnt.registry.ItemRegistry;
import net.minecraft.world.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
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
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.Level;

public class AngryMiner extends HostileEntity implements RangedAttackMob {
	
	public AngryMiner(EntityType<AngryMiner> type, Level level) {
		super(type, level);
		equipStack(EquipmentSlot.MAINHAND, new ItemStack(ItemRegistry.DYNAMITE.get()));
	}
	
	@Override
	public void initGoals() {
		super.initGoals();
		targetSelector.add(0, new ActiveTargetGoal<Player>(this, Player.class, false, false));
		targetSelector.add(1, new ActiveTargetGoal<IronGolemEntity>(this, IronGolemEntity.class, false, false));
		targetSelector.add(2, new RevengeGoal(this, Player.class).setGroupRevenge(getClass()));
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
		ItemRegistry.DYNAMITE.get().shoot(getWorld(), getX(), getY() + getStandingEyeHeight(), getZ(), new Vec3(xVel, yVel - getY() - getStandingEyeHeight() + Math.sqrt(xVel * xVel + zVel * zVel) * 0.2f, zVel), 2, this);
	}
	
	@Override
	public void dropEquipment(ServerLevel world, DamageSource source, boolean causedByPlayer) {
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

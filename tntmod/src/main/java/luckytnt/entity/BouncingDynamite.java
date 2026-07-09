package luckytnt.entity;

import luckytnt.tnteffects.projectile.BouncingDynamiteEffect;
import luckytntlib.entity.LExplosiveProjectile;
import net.minecraft.world.entity.EntityType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.Level;

public class BouncingDynamite extends LExplosiveProjectile {

	public BouncingDynamite(EntityType<LExplosiveProjectile> type, Level level) {
		super(type, level, new BouncingDynamiteEffect());
	}
	
	@Override
	public void onHitBlock(BlockHitResult hitResult) {
		Vec3 flyDir = getDeltaMovement();
		if(hitResult != null) {
			if(getPersistentData().getIntOr("bounces", 0) >= 12) {
				if(level() instanceof ServerLevel) {
					getEffect().serverExplosion(this);
					level().playSound(this, new BlockPos(Mth.floor(getX()), Mth.floor(getY()), Mth.floor(getZ())), SoundEvents.GENERIC_EXPLODE.value(), SoundSource.BLOCKS, 4f, (1f + (level().getRandom().nextFloat() - level().getRandom().nextFloat()) * 0.2f) * 0.7f);
				}
				discard();
			}
			Vec3 normalVec = new Vec3(hitResult.getDirection().getUnitVec3i().getX(), hitResult.getDirection().getUnitVec3i().getY(), hitResult.getDirection().getUnitVec3i().getZ());
			double num = normalVec.dot(flyDir);
			double denom = normalVec.dot(normalVec);
			Vec3 result = normalVec.scale(num/denom);
			Vec3 bounceDir = flyDir.subtract(result.scale(2f));
			setDeltaMovement(bounceDir.scale(0.5f + Math.random() * 0.25f));
			CompoundTag nbt = getPersistentData();
			nbt.putInt("bounces", getPersistentData().getIntOr("bounces", 0) + 1);
			setPersistentData(nbt);
			level().playSound(null, x(), y(), z(), SoundEvents.SLIME_JUMP, SoundSource.MASTER, 1, 1);
		}
	}
}

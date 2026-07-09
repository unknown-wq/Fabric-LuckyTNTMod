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
	public void onBlockHit(BlockHitResult hitResult) {
		Vec3 flyDir = getVelocity();
		if(hitResult != null) {
			if(getPersistentData().getInt("bounces") >= 12) {
				if(getWorld() instanceof ServerLevel) {
					getEffect().serverExplosion(this);
					getWorld().playSound(this, new BlockPos(Mth.floor(getX()), Mth.floor(getY()), Mth.floor(getZ())), SoundEvents.GENERIC_EXPLODE.value(), SoundSource.BLOCKS, 4f, (1f + (getWorld().getRandom().nextFloat() - getWorld().getRandom().nextFloat()) * 0.2f) * 0.7f);
				}
				discard();
			}
			Vec3 normalVec = new Vec3(hitResult.getSide().getVector().getX(), hitResult.getSide().getVector().getY(), hitResult.getSide().getVector().getZ());
			double num = normalVec.dotProduct(flyDir);
			double denom = normalVec.dotProduct(normalVec);
			Vec3 result = normalVec.multiply(num/denom);
			Vec3 bounceDir = flyDir.subtract(result.multiply(2f));
			setVelocity(bounceDir.multiply(0.5f + Math.random() * 0.25f));
			CompoundTag nbt = getPersistentData();
			nbt.putInt("bounces", getPersistentData().getInt("bounces") + 1);
			setPersistentData(nbt);
			getWorld().playSound(null, x(), y(), z(), SoundEvents.ENTITY_SLIME_JUMP, SoundSource.MASTER, 1, 1);		
		}
	}
}

package luckytnt.entity;

import luckytnt.tnteffects.projectile.BouncingDynamiteEffect;
import luckytntlib.entity.LExplosiveProjectile;
import net.minecraft.entity.EntityType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class BouncingDynamite extends LExplosiveProjectile {

	public BouncingDynamite(EntityType<LExplosiveProjectile> type, World level) {
		super(type, level, new BouncingDynamiteEffect());
	}
	
	@Override
	public void onBlockHit(BlockHitResult hitResult) {
		Vec3d flyDir = getVelocity();
		if(hitResult != null) {
			if(getPersistentData().getInt("bounces") >= 12) {
				if(getWorld() instanceof ServerWorld) {
					getEffect().serverExplosion(this);
					getWorld().playSound(this, new BlockPos(MathHelper.floor(getX()), MathHelper.floor(getY()), MathHelper.floor(getZ())), SoundEvents.ENTITY_GENERIC_EXPLODE.value(), SoundCategory.BLOCKS, 4f, (1f + (getWorld().getRandom().nextFloat() - getWorld().getRandom().nextFloat()) * 0.2f) * 0.7f);
				}
				discard();
			}
			Vec3d normalVec = new Vec3d(hitResult.getSide().getVector().getX(), hitResult.getSide().getVector().getY(), hitResult.getSide().getVector().getZ());
			double num = normalVec.dotProduct(flyDir);
			double denom = normalVec.dotProduct(normalVec);
			Vec3d result = normalVec.multiply(num/denom);
			Vec3d bounceDir = flyDir.subtract(result.multiply(2f));
			setVelocity(bounceDir.multiply(0.5f + Math.random() * 0.25f));
			NbtCompound nbt = getPersistentData();
			nbt.putInt("bounces", getPersistentData().getInt("bounces") + 1);
			setPersistentData(nbt);
			getWorld().playSound(null, x(), y(), z(), SoundEvents.ENTITY_SLIME_JUMP, SoundCategory.MASTER, 1, 1);		
		}
	}
}

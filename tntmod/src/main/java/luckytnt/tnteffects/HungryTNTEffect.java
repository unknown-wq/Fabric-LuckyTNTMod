package luckytnt.tnteffects;

import java.util.List;

import luckytnt.registry.BlockRegistry;
import luckytntlib.util.IExplosiveEntity;
import luckytntlib.util.explosions.ImprovedExplosion;
import luckytntlib.util.tnteffects.PrimedTNTEffect;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSources;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public class HungryTNTEffect extends PrimedTNTEffect {

	@Override
	public void explosionTick(IExplosiveEntity ent) {
		Entity target = null;
		double distance = 2000;
		List<LivingEntity> list = ent.getLevel().getNonSpectatingEntities(LivingEntity.class, new Box(ent.x() - 50, ent.y() - 50, ent.z() - 50, ent.x() + 50, ent.y() + 50, ent.z() + 50));

		for(LivingEntity living : list) {
			double x = living.getX() - ent.x();
			double y = living.getEyeY() - ent.y();
			double z = living.getZ() - ent.z();
			double magnitude = Math.sqrt(x * x + y * y + z * z);

			if(magnitude < distance) {
				distance = magnitude;
				target = living;
			}
		}
		if(target != null) {
			double x = ent.x() - target.getX();
			double y = ent.y() - target.getY();
			double z = ent.z() - target.getZ();
			double magnitude = Math.sqrt(x * x + y * y + z * z);

			if(magnitude > 2) {
				Vec3d vec3d = new Vec3d(x, y + 0.1D, z).normalize();
				if(!(target instanceof PlayerEntity)) {
					target.setVelocity(vec3d);
				} else if(target instanceof PlayerEntity) {
					target.setVelocity(vec3d.multiply(0.3D));
				}
			} else if(magnitude <= 2) {
				if(!(target instanceof PlayerEntity)) {
					NbtCompound tag = ent.getPersistentData();
					tag.putInt("amount", ent.getPersistentData().getInt("amount") + 1);
					ent.setPersistentData(tag);
        			target.discard();
				} else if(target instanceof PlayerEntity) {
					DamageSources sources = ent.getLevel().getDamageSources();
					
					target.damage(sources.outOfWorld(), 4f);
					Vec3d vec3d = new Vec3d(target.getX() - ent.x(), target.getY() - ent.y(), target.getZ() - ent.z()).normalize().multiply(10);
					target.setVelocity(vec3d);
				}
			}
		}
	}
	
	@Override
	public void serverExplosion(IExplosiveEntity ent) {
		int amount = ent.getPersistentData().getInt("amount");
		if(amount < 0) {
			amount = 0;
		}
		if(amount > 20) {
			amount = 20;
		}

		float size = 80f + ((80f / 20f) * amount);
		float yStrength = 1.3f - ((0.3f / 20f) * amount);
		float resistanceImpact = 1f - ((0.833f / 20f) * amount);
		float knockback = 5f + ((10f / 20f) * amount);
		
		ImprovedExplosion explosion = new ImprovedExplosion(ent.getLevel(), (Entity)ent, ent.getPos(), MathHelper.floor((double)size));
		explosion.doEntityExplosion(knockback, true);
		explosion.doBlockExplosion(1f, yStrength, resistanceImpact, size >= 110f ? 0.05f : 1f, false, size >= 110f ? true : false);
	}
	
	@Override
	public float getSize(IExplosiveEntity ent) {
		int amount = ent.getPersistentData().getInt("amount");
		if(amount < 0) {
			amount = 0;
		}
		if(amount > 20) {
			amount = 20;
		}

		return 1f + (3f / 20f) * amount;
	}
	
	@Override
	public Block getBlock() {
		return BlockRegistry.HUNGRY_TNT.get();
	}
	
	@Override
	public int getDefaultFuse(IExplosiveEntity ent) {
		return 600;
	}
}

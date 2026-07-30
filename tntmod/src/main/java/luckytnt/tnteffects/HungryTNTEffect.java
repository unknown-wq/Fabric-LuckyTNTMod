package luckytnt.tnteffects;

import java.util.List;

import luckytnt.registry.BlockRegistry;
import luckytntlib.util.IExplosiveEntity;
import luckytntlib.util.tnteffects.PrimedTNTEffect;
import luckytntlib.util.tnteffects.TNTXStrengthEffect.SectionSkippingExplosion;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.entity.Entity;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.damagesource.DamageSources;
import net.minecraft.world.entity.player.Player;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.phys.AABB;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;

public class HungryTNTEffect extends PrimedTNTEffect {

	@Override
	public void explosionTick(IExplosiveEntity ent) {
		// This 100 wide query ran on both logical sides every tick of a 600 tick fuse. Everything it
		// does (discarding the target, the "amount" counter, the damage) is server authoritative;
		// players are hurtMarked so the server still pushes the velocity down to them.
		if(!(ent.getLevel() instanceof ServerLevel)) {
			return;
		}
		Entity target = null;
		double distance = 2000;
		AABB range = new AABB(ent.x() - 50, ent.y() - 50, ent.z() - 50, ent.x() + 50, ent.y() + 50, ent.z() + 50);
		List<LivingEntity> list = ent.getLevel().getEntitiesOfClass(LivingEntity.class, range);

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
				Vec3 vec3d = new Vec3(x, y + 0.1D, z).normalize();
				if(!(target instanceof Player)) {
					target.setDeltaMovement(vec3d);
				} else if(target instanceof Player player) {
					target.setDeltaMovement(vec3d.scale(0.3D));
					player.hurtMarked = true;
				}
			} else if(magnitude <= 2) {
				if(!(target instanceof Player)) {
					CompoundTag tag = ent.getPersistentData();
					tag.putInt("amount", ent.getPersistentData().getIntOr("amount", 0) + 1);
					ent.setPersistentData(tag);
        			target.discard();
				} else if(target instanceof Player player) {
					DamageSources sources = ent.getLevel().damageSources();

					if(ent.getLevel() instanceof ServerLevel sLevel) {
						target.hurtServer(sLevel, sources.fellOutOfWorld(), 4f);
					}
					Vec3 vec3d = new Vec3(target.getX() - ent.x(), target.getY() - ent.y(), target.getZ() - ent.z()).normalize().scale(10);
					target.setDeltaMovement(vec3d);
					player.hurtMarked = true;
				}
			}
		}
	}
	
	@Override
	public void serverExplosion(IExplosiveEntity ent) {
		int amount = ent.getPersistentData().getIntOr("amount", 0);
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

		//969 ms of the 981 ms this TNT costs is the r=80..160 ray walk. SectionSkippingExplosion walks the
		//same rays but jumps over all-air chunk sections and over the sky above a chunk's WORLD_SURFACE.
		//The affected positions are never read back, so saveBlockPos is off.
		SectionSkippingExplosion explosion = new SectionSkippingExplosion(ent.getLevel(), (Entity)ent, ent.getPos(), Mth.floor((double)size));
		explosion.doEntityExplosion(knockback, true);
		explosion.doBlockExplosion(1f, yStrength, resistanceImpact, size >= 110f ? 0.05f : 1f, false, size >= 110f ? true : false, false);
	}
	
	@Override
	public float getSize(IExplosiveEntity ent) {
		int amount = ent.getPersistentData().getIntOr("amount", 0);
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

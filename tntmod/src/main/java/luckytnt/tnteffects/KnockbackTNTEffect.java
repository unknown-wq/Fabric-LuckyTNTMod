package luckytnt.tnteffects;


import java.util.List;

import org.joml.Vector3f;

import luckytnt.registry.BlockRegistry;
import luckytntlib.util.IExplosiveEntity;
import luckytntlib.util.LuckyTNTEntityExtension;
import luckytntlib.util.explosions.ImprovedExplosion;
import luckytntlib.util.tnteffects.PrimedTNTEffect;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class KnockbackTNTEffect extends PrimedTNTEffect {

	@Override
	public void explosionTick(IExplosiveEntity ent) {
		// explosionTick runs on both logical sides, so this 150 wide query used to be walked twice a
		// tick for 300 ticks. Players now get hurtMarked so the server actually pushes the velocity
		// down to them, which is what the client side pass used to do implicitly (same as
		// serverExplosion below already did).
		if(ent.getTNTFuse() > 1 && ent.getLevel() instanceof ServerLevel) {
			List<LivingEntity> ents = ent.getLevel().getEntitiesOfClass(LivingEntity.class, new AABB(ent.x() - 75, ent.y() - 75, ent.z() - 75, ent.x() + 75, ent.y() + 75, ent.z() + 75));
			for (LivingEntity lent : ents) {
				if(lent instanceof LuckyTNTEntityExtension elent) {
					if (elent.getAdditionalPersistentData().getIntOr("knockbacktime", 0) > 0) {
						CompoundTag tag = elent.getAdditionalPersistentData();
						tag.putInt("knockbacktime", elent.getAdditionalPersistentData().getIntOr("knockbacktime", 0) - 1);
						elent.setAdditionalPersistentData(tag);
					}
				}
				double x = ent.x() - lent.getX();
				double y = ent.y() - lent.getY();
				double z = ent.z() - lent.getZ();
				double distance = Math.sqrt(x * x + y * y + z * z) + 0.1D;
				Vec3 vec = new Vec3(x, y, z).normalize().scale(1D / (distance * 0.2D) + 0.5D).add(0, 0.1D, 0);
				if (distance > 2.1D && distance <= 75D && lent instanceof LuckyTNTEntityExtension elent && elent.getAdditionalPersistentData().getIntOr("knockbacktime", 0) <= 0) {
					if (lent instanceof Player player) {
						if (!player.isCreative()) {
							lent.setDeltaMovement(vec);
							player.hurtMarked = true;
						}
					} else {
						lent.setDeltaMovement(vec);
					}
				} else if (distance <= 2.1D && lent instanceof LuckyTNTEntityExtension elent) {
					if (lent instanceof Player player) {
						if (!player.isCreative()) {
							CompoundTag tag = elent.getAdditionalPersistentData();
							tag.putInt("knockbacktime", 40);
							elent.setAdditionalPersistentData(tag);
							lent.setDeltaMovement(vec.reverse().normalize().scale(5D).add(0, 0.5D, 0));
							player.hurtMarked = true;
						}
					} else {
						CompoundTag tag = elent.getAdditionalPersistentData();
						tag.putInt("knockbacktime", 40);
						elent.setAdditionalPersistentData(tag);
						lent.setDeltaMovement(vec.reverse().normalize().scale(5D).add(0, 0.5D, 0));
					}
				}
			}
		}
	}
	
	@Override
	public void serverExplosion(IExplosiveEntity ent) {
		List<Entity> entities = ent.getLevel().getEntities((Entity)ent, new AABB(ent.x() - 75, ent.y() - 75, ent.z() - 75, ent.x() + 75, ent.y() + 75, ent.z() + 75));
		for(Entity entity : entities) {
			if(!entity.ignoreExplosion(ImprovedExplosion.dummyExplosion(ent.getLevel()))) {
				double distance = Math.sqrt(entity.distanceToSqr(ent.getPos())) / (75 * 2);
				if(distance <= 1f) {
					double offX = (entity.getX() - ent.x());
					double offY = (entity.getEyeY() - ent.y());
					double offZ = (entity.getZ() - ent.z());
					double distance2 = Math.sqrt(offX * offX + offY * offY + offZ * offZ);
					offX /= distance2;
					offY /= distance2;
					offZ /= distance2;
					float damage = (1f - (float)distance);
					entity.setDeltaMovement(entity.getDeltaMovement().add(offX * damage * 15, offY * damage * 15, offZ * damage * 15));
					if(entity instanceof Player player) {
						player.hurtMarked = true;
					}
				}
			}
		}
	}
	
	@Override
	public void spawnParticles(IExplosiveEntity ent) {
		double phi = Math.PI * (3D - Math.sqrt(5D));
		for(int i = 0; i < 600; i++) {
			double y = 1D - ((double)i / (600D - 1D)) * 2D;
			double radius = Math.sqrt(1D - y * y);
			
			double theta = phi * i;
			
			double x = Math.cos(theta) * radius;
			double z = Math.sin(theta) * radius;
			
			ent.getLevel().addParticle(new DustParticleOptions(((int)(0.2f*255)<<16)|((int)(0.8f*255)<<8)|(int)(0.2f*255), 0.75f), ent.x() + x * 2, ent.y() + 0.5D + y * 2, ent.z() + 2 * z, 0, 0, 0);
		}
	}
	
	@Override
	public Block getBlock() {
		return BlockRegistry.KNOCKBACK_TNT.get();
	}
	
	@Override
	public int getDefaultFuse(IExplosiveEntity ent) {
		return 300;
	}
}

package luckytnt.tnteffects.projectile;


import org.joml.Vector3f;

import luckytnt.registry.ItemRegistry;
import luckytntlib.entity.LExplosiveProjectile;
import luckytntlib.util.IExplosiveEntity;
import luckytntlib.util.explosions.ImprovedExplosion;
import luckytntlib.util.tnteffects.PrimedTNTEffect;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.world.World;

public class PulseDynamiteEffect extends PrimedTNTEffect{

	@Override
	public void baseTick(IExplosiveEntity entity) {
		World level = entity.getLevel();
		if(entity instanceof LExplosiveProjectile ent) {
			if(ent.inGround()) {
				NbtCompound tag = ent.getPersistentData();
				tag.putBoolean("hitBefore", true);
				ent.setPersistentData(tag);
			}
			if(ent.getTNTFuse() == 0) {
				ent.destroy();
			}
			if(ent.inGround() || ent.getPersistentData().getBoolean("hitBefore")) {
				explosionTick(ent);
				ent.setTNTFuse(ent.getTNTFuse() - 1);
			}
			if(level.isClient) {
				spawnParticles(entity);
			}
		}
	}
	
	@Override
	public void explosionTick(IExplosiveEntity entity) {
		World level = entity.getLevel();
		if (entity.getTNTFuse() <= 185) {
			((Entity)entity).setVelocity(0, 0, 0);
			((Entity)entity).setPosition(((Entity) entity).getLerpedPos(0f));
			if (entity.getTNTFuse() % 20 == 0) {
				if (entity.getLevel() instanceof ServerWorld) {
					ImprovedExplosion explosion = new ImprovedExplosion(entity.getLevel(), (Entity)entity, entity.getPos(), entity.getPersistentData().getInt("strength"));
					explosion.doEntityExplosion(1f, true);
					explosion.doBlockExplosion(1f, 1f, 1f, 1.25f, false, false);
					level.playSound((Entity)entity, toBlockPos(entity.getPos()), SoundEvents.ENTITY_GENERIC_EXPLODE.value(), SoundCategory.BLOCKS, 4f, (1f + (level.random.nextFloat() - level.random.nextFloat()) * 0.2f) * 0.7f);
					NbtCompound tag = entity.getPersistentData();
					tag.putInt("strength", entity.getPersistentData().getInt("strength") + 1);
					entity.setPersistentData(tag);
				}
			}
		}
	}
	
	@Override
	public void spawnParticles(IExplosiveEntity entity) {
		double phi = Math.PI * (3f - Math.sqrt(5f));
		for(int i = 0; i < 200; i++) {
			double y = 1f - ((double)i / (200f - 1f)) * 2f;
			double radius = Math.sqrt(1f - y * y);
			
			double theta = phi * i;
			
			double x = Math.cos(theta) * radius;
			double z = Math.sin(theta) * radius;
			
			entity.getLevel().addParticle(new DustParticleEffect(new Vector3f(0.4f, 0.4f, 1f), 0.75f), entity.x() + x, entity.y() + y + 0.5f, entity.z() + z, 0, 0, 0);
		}
	}
	
	@Override
	public boolean explodesOnImpact() {
		return false;
	}
	
	@Override
	public Item getItem() {
		return ItemRegistry.PULSE_DYNAMITE.get();
	}
	
	@Override
	public int getDefaultFuse(IExplosiveEntity entity) {
		return 300;
	}
}

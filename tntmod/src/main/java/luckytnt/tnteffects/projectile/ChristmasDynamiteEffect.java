package luckytnt.tnteffects.projectile;

import java.util.Random;

import org.joml.Math;

import luckytnt.registry.EntityRegistry;
import luckytnt.registry.ItemRegistry;
import luckytnt.tnteffects.SnowTNTEffect;
import luckytntlib.entity.LExplosiveProjectile;
import luckytntlib.util.IExplosiveEntity;
import luckytntlib.util.tnteffects.PrimedTNTEffect;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class ChristmasDynamiteEffect extends PrimedTNTEffect{

	@Override
	public void baseTick(IExplosiveEntity entity) {
		World level = entity.getLevel();
		if(entity instanceof LExplosiveProjectile ent) {
			if(ent.inGround() && ent.getTNTFuse() < 60) {
				if(level instanceof ServerWorld) {
					level.playSound((Entity)entity, toBlockPos(entity.getPos()), SoundEvents.ENTITY_GENERIC_EXPLODE.value(), SoundCategory.BLOCKS, 4f, (1f + (level.random.nextFloat() - level.random.nextFloat()) * 0.2f) * 0.7f);
					serverExplosion(ent);
				}
				ent.destroy();
			}
			if(ent.getTNTFuse() > 0) {
				explosionTick(ent);
				ent.setTNTFuse(ent.getTNTFuse() - 1);
			}
			if(level.isClient) {
				spawnParticles(entity);
			}
		}
	}
	
	@Override
	public void serverExplosion(IExplosiveEntity entity) {
		SnowTNTEffect snowEffect = new SnowTNTEffect(25);
		snowEffect.serverExplosion(entity);
		((ServerWorld)entity.getLevel()).spawnParticles(ParticleTypes.WAX_OFF, entity.x() + Math.random() - 0.5f, entity.y() + Math.random() - 0.5f, entity.z() + Math.random() - 0.5f, 500, 0.5f, 0.5f, 0.5f, 0f);
	}
	
	@Override
	public void explosionTick(IExplosiveEntity entity) {
		if(entity.getTNTFuse() == 220) {
			NbtCompound tag = entity.getPersistentData();
			tag.putDouble("vecx", ((Entity)entity).getVelocity().x);
			tag.putDouble("vecz", ((Entity)entity).getVelocity().z);
			entity.setPersistentData(tag);
		}
		if(entity.getTNTFuse() <= 220 && entity.getTNTFuse() > 60) {
			((Entity)entity).setVelocity(new Vec3d(entity.getPersistentData().getDouble("vecx"), 0, entity.getPersistentData().getDouble("vecz")).normalize().multiply(0.25f));
			if(entity.getTNTFuse() % 20 == 0) {
				LExplosiveProjectile dynamite = EntityRegistry.CHRISTMAS_DYNAMITE_PROJECTILE.get().create(entity.getLevel());
				dynamite.setPosition(entity.getPos());
				dynamite.setOwner(entity.owner());
				double randomX = Math.random();
				randomX *= new Random().nextBoolean() ? 1 : -1;
				double randomZ = Math.random();
				randomZ *= new Random().nextBoolean() ? 1 : -1;
				dynamite.setVelocity(randomX, -Math.random() * 0.5f, randomZ);
				entity.getLevel().spawnEntity(dynamite);
			}
		}
		else if(entity.getTNTFuse() > 60){
			((Entity)entity).setVelocity(((Entity)entity).getVelocity().add(0f, 0.08f, 0f));
		}
	}
	
	@Override
	public void spawnParticles(IExplosiveEntity entity) {
		for(int i = 0; i < 7; i++) {
			entity.getLevel().addParticle(ParticleTypes.WAX_OFF, true, entity.x() + Math.random() - 0.5f, entity.y() + Math.random() - 0.5f, entity.z() + Math.random() - 0.5f, 0, 0, 0);
		}
	}
	
	@Override
	public boolean explodesOnImpact() {
		return false;
	}
	
	@Override
	public boolean airFuse() {
		return true;
	}
	
	@Override
	public Item getItem() {
		return ItemRegistry.CHRISTMAS_DYNAMITE.get();
	}
	
	@Override
	public int getDefaultFuse(IExplosiveEntity entity) {
		return 260;
	}
}

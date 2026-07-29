package luckytnt.tnteffects.projectile;

import net.minecraft.world.entity.EntitySpawnReason;

import org.joml.Math;

import luckytnt.registry.EntityRegistry;
import luckytnt.registry.ItemRegistry;
import luckytnt.tnteffects.SnowTNTEffect;
import luckytntlib.entity.LExplosiveProjectile;
import luckytntlib.util.IExplosiveEntity;
import luckytntlib.util.tnteffects.PrimedTNTEffect;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.Item;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.RandomSource;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.Level;

public class ChristmasDynamiteEffect extends PrimedTNTEffect{

	/**
	 * Ticks between two child projectiles. The dynamite drops children over the window 220..60,
	 * so this directly controls how many entities a single throw creates.
	 */
	private static final int CHILD_SPAWN_INTERVAL = 40;

	@Override
	public void baseTick(IExplosiveEntity entity) {
		Level level = entity.getLevel();
		if(entity instanceof LExplosiveProjectile ent) {
			if(ent.inGround() && ent.getTNTFuse() < 60) {
				if(level instanceof ServerLevel) {
					level.playSound((Entity)entity, toBlockPos(entity.getPos()), SoundEvents.GENERIC_EXPLODE.value(), SoundSource.BLOCKS, 4f, (1f + (level.getRandom().nextFloat() - level.getRandom().nextFloat()) * 0.2f) * 0.7f);
					serverExplosion(ent);
				}
				ent.destroy();
			}
			if(ent.getTNTFuse() > 0) {
				explosionTick(ent);
				ent.setTNTFuse(ent.getTNTFuse() - 1);
			}
			if(level.isClientSide()) {
				spawnParticles(entity);
			}
		}
	}
	
	@Override
	public void serverExplosion(IExplosiveEntity entity) {
		SnowTNTEffect snowEffect = new SnowTNTEffect(25);
		snowEffect.serverExplosion(entity);
		((ServerLevel)entity.getLevel()).sendParticles(ParticleTypes.WAX_OFF, entity.x() + Math.random() - 0.5f, entity.y() + Math.random() - 0.5f, entity.z() + Math.random() - 0.5f, 500, 0.5f, 0.5f, 0.5f, 0f);
	}
	
	@Override
	public void explosionTick(IExplosiveEntity entity) {
		Entity ent = (Entity)entity;
		int fuse = entity.getTNTFuse();
		// The synched NBT write and the child spawn are server-only; the motion writes stay on both
		// sides so the client keeps moving smoothly between tracker updates.
		boolean server = entity.getLevel() instanceof ServerLevel;
		if(fuse == 220 && server) {
			CompoundTag tag = entity.getPersistentData();
			tag.putDouble("vecx", ent.getDeltaMovement().x);
			tag.putDouble("vecz", ent.getDeltaMovement().z);
			entity.setPersistentData(tag);
		}
		if(fuse <= 220 && fuse > 60) {
			ent.setDeltaMovement(new Vec3(entity.getPersistentData().getDoubleOr("vecx", 0), 0, entity.getPersistentData().getDoubleOr("vecz", 0)).normalize().scale(0.25f));
			if(server && fuse % CHILD_SPAWN_INTERVAL == 0) {
				RandomSource random = entity.getLevel().getRandom();
				LExplosiveProjectile dynamite = EntityRegistry.CHRISTMAS_DYNAMITE_PROJECTILE.get().create(entity.getLevel(), EntitySpawnReason.MOB_SUMMONED);
				dynamite.setPos(entity.getPos());
				dynamite.setOwner(entity.owner());
				double randomX = random.nextDouble() * (random.nextBoolean() ? 1 : -1);
				double randomZ = random.nextDouble() * (random.nextBoolean() ? 1 : -1);
				dynamite.setDeltaMovement(randomX, -random.nextDouble() * 0.5f, randomZ);
				entity.getLevel().addFreshEntity(dynamite);
			}
		}
		else if(fuse > 60){
			ent.setDeltaMovement(ent.getDeltaMovement().add(0f, 0.08f, 0f));
		}
	}
	
	@Override
	public void spawnParticles(IExplosiveEntity entity) {
		for(int i = 0; i < 7; i++) {
			entity.getLevel().addParticle(ParticleTypes.WAX_OFF, entity.x() + Math.random() - 0.5f, entity.y() + Math.random() - 0.5f, entity.z() + Math.random() - 0.5f, 0, 0, 0);
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

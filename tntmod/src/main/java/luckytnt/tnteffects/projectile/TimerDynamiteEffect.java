package luckytnt.tnteffects.projectile;

import org.joml.Vector3f;

import luckytnt.registry.ItemRegistry;
import luckytntlib.entity.LExplosiveProjectile;
import luckytntlib.util.IExplosiveEntity;
import luckytntlib.util.explosions.ImprovedExplosion;
import luckytntlib.util.tnteffects.PrimedTNTEffect;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.Item;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.level.Level;

public class TimerDynamiteEffect extends PrimedTNTEffect{

	@Override
	public void baseTick(IExplosiveEntity entity) {
		Level level = entity.getLevel();
		if(entity instanceof LExplosiveProjectile ent) {
			if(ent.inGround() || ent.hitEntity()) {
				CompoundTag tag = ent.getPersistentData();
				tag.putBoolean("hitBefore", true);
				ent.setPersistentData(tag);
			}
			if(ent.getTNTFuse() == 0) {
				if(ent.level() instanceof ServerLevel) {
					entity.getLevel().playSound((Entity)entity, toBlockPos(entity.getPos()), SoundEvents.GENERIC_EXPLODE.value(), SoundSource.BLOCKS, 4f, (1f + (level.getRandom().nextFloat() - level.getRandom().nextFloat()) * 0.2f) * 0.7f);
					serverExplosion(entity);
				}
				ent.destroy();
			}
			if(ent.inGround() || ent.getPersistentData().getBooleanOr("hitBefore", false)) {
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
		ImprovedExplosion explosion = new ImprovedExplosion(entity.getLevel(), (Entity)entity, entity.getPos(), 5);
		explosion.doEntityExplosion(1f, true);
		explosion.doBlockExplosion();
	}
	
	@Override
	public void spawnParticles(IExplosiveEntity entity) {
		float r = entity.getTNTFuse() < 200 ? 1f : 2f - 0.005f * entity.getTNTFuse();
		float g = entity.getTNTFuse() >= 200 ? 1f : 0.005f * entity.getTNTFuse();
		entity.getLevel().addParticle(new DustParticleOptions(((int)(r*255)<<16)|((int)(g*255)<<8)|(int)(0*255), 1f), entity.x(), entity.y(), entity.z(), 0, 0, 0);
	}
	
	@Override
	public Item getItem() {
		return ItemRegistry.TIMER_DYNAMITE.get();
	}
	
	@Override
	public int getDefaultFuse(IExplosiveEntity entity) {
		return 400;
	}
}

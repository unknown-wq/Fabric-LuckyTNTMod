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
import net.minecraft.sounds.SoundSource;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.RandomSource;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.Level;

public class ReactionDynamiteEffect extends PrimedTNTEffect{

	@Override
	public void baseTick(IExplosiveEntity entity) {
		Level level = entity.getLevel();
		if(entity instanceof LExplosiveProjectile ent) {
			if(ent.inGround()) {
				CompoundTag tag = entity.getPersistentData();
				tag.putBoolean("hitBefore", true);
				entity.setPersistentData(tag);
			}
			if(ent.getTNTFuse() == 0) {
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
	public void explosionTick(IExplosiveEntity entity){
		Level level = entity.getLevel();
		if(!level.isClientSide()) {
			int nextExplosion = entity.getPersistentData().getIntOr("nextExplosion", 0);
			if(nextExplosion == 0) {
				RandomSource random = level.getRandom();
				Vec3 randomPos = new Vec3(random.nextDouble() * 20 - 10, random.nextDouble() * 10 - 5, random.nextDouble() * 20 - 10);
				float explosionSize = 5 + random.nextFloat() * 5;
				ImprovedExplosion explosion = new ImprovedExplosion(entity.getLevel(), (Entity)entity, entity.getPos().add(randomPos), Math.round(explosionSize));
				explosion.doEntityExplosion(1f + 0.05f * explosionSize, true);
				explosion.doBlockExplosion(1f, 1f, 0.75f, 1.25f, false, false);
				level.playSound((Entity)entity, toBlockPos(entity.getPos().add(randomPos)), SoundEvents.GENERIC_EXPLODE.value(), SoundSource.BLOCKS, 4f, (1f + (random.nextFloat() - random.nextFloat()) * 0.2f) * 0.7f);
				CompoundTag tag = entity.getPersistentData();
				tag.putInt("nextExplosion", (2 + random.nextInt(3)) - 1);
				entity.setPersistentData(tag);
			}
			else {
				// getPersistentData() hands back the live CompoundTag held by the entity's synched data, so
				// mutating it in place already updates (and persists) the countdown. setPersistentData
				// additionally forces a full CompoundTag packet to every tracking client, and nothing on the
				// client ever reads "nextExplosion" - so the plain countdown does not need to sync at all.
				entity.getPersistentData().putInt("nextExplosion", nextExplosion - 1);
			}
		}
	}
	
	@Override
	public void spawnParticles(IExplosiveEntity entity) {
		if(Math.random() < 0.15f) {
			entity.getLevel().addParticle(new DustParticleOptions(((int)(0.37f*255)<<16)|((int)(1f*255)<<8)|(int)(1f*255), 1), entity.x(), entity.y(), entity.z(), 0, 0, 0);
		}
		if(Math.random() < 0.15f) {
			entity.getLevel().addParticle(new DustParticleOptions(((int)(0.59f*255)<<16)|((int)(1f*255)<<8)|(int)(0f*255), 1), entity.x(), entity.y(), entity.z(), 0, 0, 0);
		}
		if(Math.random() < 0.15f) {
			entity.getLevel().addParticle(new DustParticleOptions(((int)(0.11f*255)<<16)|((int)(0.26f*255)<<8)|(int)(0.11f*255), 1), entity.x(), entity.y(), entity.z(), 0, 0, 0);
		}
		if(Math.random() < 0.15f) {
			entity.getLevel().addParticle(new DustParticleOptions(((int)(0.16f*255)<<16)|((int)(0.42f*255)<<8)|(int)(0.15f*255), 1), entity.x(), entity.y(), entity.z(), 0, 0, 0);
		}
	}
	
	@Override
	public Item getItem() {
		return ItemRegistry.REACTION_DYNAMITE.get();
	}
	
	@Override
	public int getDefaultFuse(IExplosiveEntity entity) {
		return 60;
	}
}

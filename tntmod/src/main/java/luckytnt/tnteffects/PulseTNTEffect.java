package luckytnt.tnteffects;

import org.joml.Vector3f;

import luckytnt.registry.BlockRegistry;
import luckytntlib.util.IExplosiveEntity;
import luckytntlib.util.explosions.ImprovedExplosion;
import luckytntlib.util.tnteffects.PrimedTNTEffect;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.entity.Entity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.sounds.SoundSource;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.level.Level;

public class PulseTNTEffect extends PrimedTNTEffect{

	@Override
	public void explosionTick(IExplosiveEntity entity) {
		Level level = entity.getLevel();
		if(!level.isClientSide()) {
			if(entity.getTNTFuse() < 205) {
				if(entity.getTNTFuse() % 20 == 0) {		      		
					ImprovedExplosion explosion = new ImprovedExplosion(entity.getLevel(), (Entity)entity, entity.getPos(), entity.getPersistentData().getIntOr("strength", 0));
					explosion.doEntityExplosion(1.5f, true);
					explosion.doBlockExplosion();
		      		
					if(entity.getTNTFuse() > 0) {
						level.playSound(null, toBlockPos(entity.getPos()), SoundEvents.GENERIC_EXPLODE.value(), SoundSource.BLOCKS, 4,(1.0F + (level.getRandom().nextFloat() - level.getRandom().nextFloat()) * 0.2F) * 0.7F);
					}
					
					CompoundTag tag = entity.getPersistentData();
					tag.putInt("strength", entity.getPersistentData().getIntOr("strength", 0) + 2);
					entity.setPersistentData(tag);
				}
			}
		}
		if(entity.getTNTFuse() < 205) {
			((Entity)entity).setDeltaMovement(0, 0, 0);
			((Entity)entity).setPos(((Entity)entity).xo, ((Entity)entity).yo, ((Entity)entity).zo);
		}
	}
	
	@Override
	public void spawnParticles(IExplosiveEntity entity) {
		for(double angle = 0; angle < 360; angle += 6D) {
			entity.getLevel().addParticle(new DustParticleOptions(((int)(0.4f*255)<<16)|((int)(0.4f*255)<<8)|(int)(1f*255), 0.75f), entity.x() + 0.75f * Math.cos(angle * Math.PI / 180), entity.y(), entity.z() + 0.75f * Math.sin(angle * Math.PI / 180), 0, 0, 0);
			entity.getLevel().addParticle(new DustParticleOptions(((int)(0.4f*255)<<16)|((int)(0.4f*255)<<8)|(int)(1f*255), 0.75f), entity.x() + Math.cos(angle * Math.PI / 180), entity.y() + 0.5f, entity.z() + Math.sin(angle * Math.PI / 180), 0, 0, 0);
			entity.getLevel().addParticle(new DustParticleOptions(((int)(0.4f*255)<<16)|((int)(0.4f*255)<<8)|(int)(1f*255), 0.75f), entity.x() + 0.75f * Math.cos(angle * Math.PI / 180), entity.y() + 1f, entity.z() + 0.75f * Math.sin(angle * Math.PI / 180), 0, 0, 0);
		}
	}
	
	@Override
	public Block getBlock() {
		return BlockRegistry.PULSE_TNT.get();
	}
	
	@Override
	public int getDefaultFuse(IExplosiveEntity entity) {
		return 300;
	}
}

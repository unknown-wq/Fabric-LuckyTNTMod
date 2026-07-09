package luckytnt.tnteffects;

import org.joml.Vector3f;

import luckytnt.registry.BlockRegistry;
import luckytntlib.util.IExplosiveEntity;
import luckytntlib.util.explosions.ImprovedExplosion;
import luckytntlib.util.tnteffects.PrimedTNTEffect;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.world.World;

public class PulseTNTEffect extends PrimedTNTEffect{

	@Override
	public void explosionTick(IExplosiveEntity entity) {
		World level = entity.getLevel();
		if(!level.isClient) {
			if(entity.getTNTFuse() < 205) {
				if(entity.getTNTFuse() % 20 == 0) {		      		
					ImprovedExplosion explosion = new ImprovedExplosion(entity.getLevel(), (Entity)entity, entity.getPos(), entity.getPersistentData().getInt("strength"));
					explosion.doEntityExplosion(1.5f, true);
					explosion.doBlockExplosion();
		      		
					if(entity.getTNTFuse() > 0) {
						level.playSound(null, toBlockPos(entity.getPos()), SoundEvents.ENTITY_GENERIC_EXPLODE.value(), SoundCategory.BLOCKS, 4,(1.0F + (level.random.nextFloat() - level.random.nextFloat()) * 0.2F) * 0.7F);
					}
					
					NbtCompound tag = entity.getPersistentData();
					tag.putInt("strength", entity.getPersistentData().getInt("strength") + 2);
					entity.setPersistentData(tag);
				}
			}
		}
		if(entity.getTNTFuse() < 205) {
			((Entity)entity).setVelocity(0, 0, 0);
			((Entity)entity).setPosition(((Entity)entity).prevX, ((Entity)entity).prevY, ((Entity)entity).prevZ);
		}
	}
	
	@Override
	public void spawnParticles(IExplosiveEntity entity) {
		for(double angle = 0; angle < 360; angle += 6D) {
			entity.getLevel().addParticle(new DustParticleEffect(new Vector3f(0.4f, 0.4f, 1f), 0.75f), entity.x() + 0.75f * Math.cos(angle * Math.PI / 180), entity.y(), entity.z() + 0.75f * Math.sin(angle * Math.PI / 180), 0, 0, 0);
			entity.getLevel().addParticle(new DustParticleEffect(new Vector3f(0.4f, 0.4f, 1f), 0.75f), entity.x() + Math.cos(angle * Math.PI / 180), entity.y() + 0.5f, entity.z() + Math.sin(angle * Math.PI / 180), 0, 0, 0);
			entity.getLevel().addParticle(new DustParticleEffect(new Vector3f(0.4f, 0.4f, 1f), 0.75f), entity.x() + 0.75f * Math.cos(angle * Math.PI / 180), entity.y() + 1f, entity.z() + 0.75f * Math.sin(angle * Math.PI / 180), 0, 0, 0);
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

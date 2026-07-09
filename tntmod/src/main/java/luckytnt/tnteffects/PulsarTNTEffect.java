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
import net.minecraft.util.math.MathHelper;

public class PulsarTNTEffect extends PrimedTNTEffect {

	@Override
	public void explosionTick(IExplosiveEntity ent) {
		if(ent.getTNTFuse() == 399) {
			NbtCompound tag = ent.getPersistentData();
			tag.putFloat("size", 30f);
			ent.setPersistentData(tag);
		}
		if(ent.getTNTFuse() < 305) {
			if(ent.getTNTFuse() % 30 == 0 && !ent.getLevel().isClient()) {
				ImprovedExplosion explosion = new ImprovedExplosion(ent.getLevel(), (Entity)ent, ent.getPos(), MathHelper.floor(ent.getPersistentData().getFloat("size")));
				explosion.doEntityExplosion(4f, true);
				explosion.doBlockExplosion(1f, ent.getPersistentData().getFloat("size") > 45f ? 1.3f : 1f, 1f, ent.getPersistentData().getFloat("size") <= 80f ? 1.25f : 0.05f, false, ent.getPersistentData().getFloat("size") > 80f ? true : false);
			
				NbtCompound tag = ent.getPersistentData();
				tag.putFloat("size", ent.getPersistentData().getFloat("size") + 7f);
				ent.setPersistentData(tag);
			}
			((Entity)ent).setVelocity(0, 0, 0);
			((Entity)ent).setPosition(((Entity)ent).prevX, ((Entity)ent).prevY, ((Entity)ent).prevZ);
		}
	}
	
	@Override
	public void spawnParticles(IExplosiveEntity ent) {
		for(double offX = -2; offX <= 2; offX+=0.1) {
     		for(double offZ = -2; offZ <= 2; offZ+=0.1) {
     			double offY = Math.sqrt(offX * offX + offZ * offZ);
     			if(offY <= 1.2) {
     				ent.getLevel().addParticle(new DustParticleEffect(new Vector3f(0.4f, 0f, 0.8f), 1f), ent.x() + offX, ent.y() + 1 + (offY * 4), ent.z() + offZ, 0, 0, 0);
     				ent.getLevel().addParticle(new DustParticleEffect(new Vector3f(0.4f, 0f, 0.8f), 1f), ent.x() + offX, ent.y() + (offY * -4), ent.z() + offZ, 0, 0, 0);
     			}
     		}
     	}
	}
	
	@Override
	public Block getBlock() {
		return BlockRegistry.PULSAR_TNT.get();
	}
	
	@Override
	public int getDefaultFuse(IExplosiveEntity ent) {
		return 400;
	}
}

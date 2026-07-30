package luckytnt.tnteffects;

import org.joml.Vector3f;

import luckytnt.registry.BlockRegistry;
import luckytntlib.util.IExplosiveEntity;
import luckytntlib.util.tnteffects.PrimedTNTEffect;
import luckytntlib.util.tnteffects.TNTXStrengthEffect.SectionSkippingExplosion;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.entity.Entity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.util.Mth;

public class PulsarTNTEffect extends PrimedTNTEffect {

	@Override
	public void explosionTick(IExplosiveEntity ent) {
		if(ent.getTNTFuse() == 399) {
			CompoundTag tag = ent.getPersistentData();
			tag.putFloat("size", 30f);
			ent.setPersistentData(tag);
		}
		if(ent.getTNTFuse() < 305) {
			if(ent.getTNTFuse() % 30 == 0 && !ent.getLevel().isClientSide()) {
				//"size" was read out of the persistent data five times per pulse. The pulses themselves are
				//11 concentric ray walks of growing radius (30..100), each of which spends most of its length
				//re-walking the crater the previous pulses already dug; SectionSkippingExplosion jumps over
				//those now empty chunk sections and over the open sky, which is what the 849 ms peak was.
				final float size = ent.getPersistentData().getFloatOr("size", 0f);
				SectionSkippingExplosion explosion = new SectionSkippingExplosion(ent.getLevel(), (Entity)ent, ent.getPos(), Mth.floor(size));
				explosion.doEntityExplosion(4f, true);
				explosion.doBlockExplosion(1f, size > 45f ? 1.3f : 1f, 1f, size <= 80f ? 1.25f : 0.05f, false, size > 80f, false);

				CompoundTag tag = ent.getPersistentData();
				tag.putFloat("size", size + 7f);
				ent.setPersistentData(tag);
			}
			((Entity)ent).setDeltaMovement(0, 0, 0);
			((Entity)ent).setPos(((Entity)ent).xo, ((Entity)ent).yo, ((Entity)ent).zo);
		}
	}
	
	@Override
	public void spawnParticles(IExplosiveEntity ent) {
		for(double offX = -2; offX <= 2; offX+=0.1) {
     		for(double offZ = -2; offZ <= 2; offZ+=0.1) {
     			double offY = Math.sqrt(offX * offX + offZ * offZ);
     			if(offY <= 1.2) {
     				ent.getLevel().addParticle(new DustParticleOptions(((int)(0.4f*255)<<16)|((int)(0f*255)<<8)|(int)(0.8f*255), 1f), ent.x() + offX, ent.y() + 1 + (offY * 4), ent.z() + offZ, 0, 0, 0);
     				ent.getLevel().addParticle(new DustParticleOptions(((int)(0.4f*255)<<16)|((int)(0f*255)<<8)|(int)(0.8f*255), 1f), ent.x() + offX, ent.y() + (offY * -4), ent.z() + offZ, 0, 0, 0);
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

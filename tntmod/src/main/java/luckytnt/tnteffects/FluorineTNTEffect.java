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
import net.minecraft.sounds.SoundSource;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class FluorineTNTEffect extends PrimedTNTEffect {

	@Override
	public void explosionTick(IExplosiveEntity ent) {
		Level level = ent.getLevel();
		//F20: everything below is server authoritative. The logical client used to run the same 46 rolls of
		//Math.random, keep its own "nextExplosion" counter and play a second, local copy of the explosion
		//sound on top of the one the server broadcasts to it.
		if(!(level instanceof ServerLevel)) {
			return;
		}
		RandomSource random = level.getRandom();
		if(ent.getTNTFuse() == 310) {
			//~46 explosions of radius 50..80 are fired over the 300 tick tail of the fuse and they overlap
			//heavily, so from the second one on most of every ray runs through the craters the earlier ones
			//left. SectionSkippingExplosion jumps over those emptied chunk sections and over the open sky.
			SectionSkippingExplosion explosion = new SectionSkippingExplosion(level, (Entity)ent, ent.getPos(), 50 + random.nextInt(31));
			explosion.doEntityExplosion(7f, true);
			explosion.doBlockExplosion(1f, 1f, 0.75f, 0.5f, false, false, false);
		}
		if(ent.getTNTFuse() < 300) {
			if(ent.getPersistentData().getIntOr("nextExplosion", 0) <= 0) {
				//F16: six Math.random() calls per firing replaced by the level's own RandomSource
				double x = ent.x() + random.nextDouble() * 80 - random.nextDouble() * 80;
				double y = ent.y() + random.nextDouble() * 30 - random.nextDouble() * 30;
				double z = ent.z() + random.nextDouble() * 80 - random.nextDouble() * 80;
				SectionSkippingExplosion explosion = new SectionSkippingExplosion(level, (Entity)ent, new Vec3(x, y, z), 50 + random.nextInt(31));
				explosion.doEntityExplosion(7f, true);
				explosion.doBlockExplosion(1f, 1f, 0.75f, 0.5f, false, false, false);
				level.playSound(null, new BlockPos(Mth.floor(x), Mth.floor(y), Mth.floor(z)), SoundEvents.GENERIC_EXPLODE.value(), SoundSource.BLOCKS, 4, (1.0F + (random.nextFloat() - random.nextFloat()) * 0.2F) * 0.7F);
				CompoundTag tag = ent.getPersistentData();
				tag.putInt("nextExplosion", 5 + (int)Math.round(random.nextDouble() * 2));
				ent.setPersistentData(tag);
			}
			CompoundTag tag = ent.getPersistentData();
			tag.putInt("nextExplosion", ent.getPersistentData().getIntOr("nextExplosion", 0) - 1);
			ent.setPersistentData(tag);
		}
	}
	
	@Override
	public void spawnParticles(IExplosiveEntity ent) {
		for (double i = 0D; i < 1D; i += 0.05D) {
			ent.getLevel().addParticle(new DustParticleOptions(((int)(1f*255)<<16)|((int)(1f*255)<<8)|(int)(0.65f*255), 1f), ent.x() + 0.5D, ent.y() + i, ent.z() + 0.5D, 0, 0, 0);
			ent.getLevel().addParticle(new DustParticleOptions(((int)(1f*255)<<16)|((int)(1f*255)<<8)|(int)(0.65f*255), 1f), ent.x() - 0.5D, ent.y() + i, ent.z() + 0.5D, 0, 0, 0);
			ent.getLevel().addParticle(new DustParticleOptions(((int)(1f*255)<<16)|((int)(1f*255)<<8)|(int)(0.65f*255), 1f), ent.x() + 0.5D, ent.y() + i, ent.z() - 0.5D, 0, 0, 0);
			ent.getLevel().addParticle(new DustParticleOptions(((int)(1f*255)<<16)|((int)(1f*255)<<8)|(int)(0.65f*255), 1f), ent.x() - 0.5D, ent.y() + i, ent.z() - 0.5D, 0, 0, 0);
			ent.getLevel().addParticle(new DustParticleOptions(((int)(1f*255)<<16)|((int)(1f*255)<<8)|(int)(0.65f*255), 1f), ent.x() + 0.5D, ent.y() + i, ent.z(), 0, 0, 0);
			ent.getLevel().addParticle(new DustParticleOptions(((int)(1f*255)<<16)|((int)(1f*255)<<8)|(int)(0.65f*255), 1f), ent.x() - 0.5D, ent.y() + i, ent.z(), 0, 0, 0);
			ent.getLevel().addParticle(new DustParticleOptions(((int)(1f*255)<<16)|((int)(1f*255)<<8)|(int)(0.65f*255), 1f), ent.x(), ent.y() + i, ent.z() + 0.5D, 0, 0, 0);
			ent.getLevel().addParticle(new DustParticleOptions(((int)(1f*255)<<16)|((int)(1f*255)<<8)|(int)(0.65f*255), 1f), ent.x(), ent.y() + i, ent.z() - 0.5D, 0, 0, 0);
		}
	}
	
	@Override
	public Block getBlock() {
		return BlockRegistry.FLUORINE_TNT.get();
	}
	
	@Override
	public int getDefaultFuse(IExplosiveEntity ent) {
		return 400;
	}
}

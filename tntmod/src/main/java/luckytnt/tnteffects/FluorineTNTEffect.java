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
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class FluorineTNTEffect extends PrimedTNTEffect {

	@Override
	public void explosionTick(IExplosiveEntity ent) {
		Level level = ent.getLevel();
		RandomSource random = level.getRandom();
		if(ent.getTNTFuse() == 310) {
			if(!level.isClientSide()) {
				ImprovedExplosion explosion = new ImprovedExplosion(level, (Entity)ent, ent.getPos(), 50 + random.nextInt(31));
				explosion.doEntityExplosion(7f, true);
				explosion.doBlockExplosion(1f, 1f, 0.75f, 0.5f, false, false);
			}
		}
		if(ent.getTNTFuse() < 300) {
			if(ent.getPersistentData().getIntOr("nextExplosion", 0) <= 0) {
				double x = ent.x() + Math.random() * 80 - Math.random() * 80;
				double y = ent.y() + Math.random() * 30 - Math.random() * 30;
				double z = ent.z() + Math.random() * 80 - Math.random() * 80;
				if(!level.isClientSide()) {
					ImprovedExplosion explosion = new ImprovedExplosion(level, (Entity)ent, new Vec3(x, y, z), 50 + random.nextInt(31));
					explosion.doEntityExplosion(7f, true);
					explosion.doBlockExplosion(1f, 1f, 0.75f, 0.5f, false, false);
				}
				level.playSound(null, new BlockPos(Mth.floor(x), Mth.floor(y), Mth.floor(z)), SoundEvents.GENERIC_EXPLODE.value(), SoundSource.BLOCKS, 4, (1.0F + (random.nextFloat() - random.nextFloat()) * 0.2F) * 0.7F);
				CompoundTag tag = ent.getPersistentData();
				tag.putInt("nextExplosion", 5 + (int)Math.round(Math.random() * 2));
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

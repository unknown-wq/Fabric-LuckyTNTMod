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
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.Level;

public class ReactionTNTEffect extends PrimedTNTEffect{
	
	@Override
	public void explosionTick(IExplosiveEntity entity) {
		Level level = entity.getLevel();
		if(!level.isClientSide() && entity.getTNTFuse() < 100) {
			if(entity.getPersistentData().getInt("nextExplosion") == 0) {
				Vec3 randomPos = new Vec3(Math.random() * 40 - 20, Math.random() * 20 - 10, Math.random() * 40 - 20);
				float explosionSize = 10 + level.getRandom().nextFloat() * 10;
				ImprovedExplosion explosion = new ImprovedExplosion(entity.getLevel(), (Entity)entity, entity.getPos().add(randomPos), Math.round(explosionSize));
				explosion.doEntityExplosion(1f + 0.05f * explosionSize, true);
				explosion.doBlockExplosion(1f, 1f, 0.75f, 1.25f, false, false);
				level.playSound((Entity)entity, toBlockPos(entity.getPos()).add(toBlockPos(randomPos)), SoundEvents.GENERIC_EXPLODE.value(), SoundSource.BLOCKS, 4f, (1f + (level.getRandom().nextFloat() - level.getRandom().nextFloat()) * 0.2f) * 0.7f);
				CompoundTag tag = entity.getPersistentData();
				tag.putInt("nextExplosion", 2 + level.getRandom().nextInt(3));
				entity.setPersistentData(tag);
			}
			CompoundTag tag = entity.getPersistentData();
			tag.putInt("nextExplosion", entity.getPersistentData().getInt("nextExplosion") - 1);
			entity.setPersistentData(tag);
		}
	}
	
	@Override
	public void spawnParticles(IExplosiveEntity entity) {
		if(Math.random() < 0.15f) {
			entity.getLevel().addParticle(new DustParticleOptions(((int)(0.37f*255)<<16)|((int)(1f*255)<<8)|(int)(1f*255), 1), entity.x() + Math.random() * 0.5f - Math.random() * 0.5f, entity.y() + 1f + Math.random() * 0.35f, entity.z() + Math.random() * 0.5f - Math.random() * 0.5f, 0, 0, 0);
		}
		if(Math.random() < 0.15f) {
			entity.getLevel().addParticle(new DustParticleOptions(((int)(0.59f*255)<<16)|((int)(1f*255)<<8)|(int)(0f*255), 1), entity.x() + Math.random() * 0.5f - Math.random() * 0.5f, entity.y() + 1f + Math.random() * 0.35f, entity.z() + Math.random() * 0.5f - Math.random() * 0.5f, 0, 0, 0);
		}
		if(Math.random() < 0.15f) {
			entity.getLevel().addParticle(new DustParticleOptions(((int)(0.11f*255)<<16)|((int)(0.26f*255)<<8)|(int)(0.11f*255), 1), entity.x() + Math.random() * 0.5f - Math.random() * 0.5f, entity.y() + 1f + Math.random() * 0.35f, entity.z() + Math.random() * 0.5f - Math.random() * 0.5f, 0, 0, 0);
		}
		if(Math.random() < 0.15f) {
			entity.getLevel().addParticle(new DustParticleOptions(((int)(0.16f*255)<<16)|((int)(0.42f*255)<<8)|(int)(0.15f*255), 1), entity.x() + Math.random() * 0.5f - Math.random() * 0.5f, entity.y() + 1f + Math.random() * 0.35f, entity.z() + Math.random() * 0.5f - Math.random() * 0.5f, 0, 0, 0);
		}
	}
	
	@Override
	public Block getBlock() {
		return BlockRegistry.REACTION_TNT.get();
	}
	
	@Override
	public int getDefaultFuse(IExplosiveEntity entity) {
		return 240;
	}
}

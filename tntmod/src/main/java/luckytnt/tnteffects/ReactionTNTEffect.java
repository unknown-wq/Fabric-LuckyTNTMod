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
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class ReactionTNTEffect extends PrimedTNTEffect{
	
	@Override
	public void explosionTick(IExplosiveEntity entity) {
		World level = entity.getLevel();
		if(!level.isClient && entity.getTNTFuse() < 100) {
			if(entity.getPersistentData().getInt("nextExplosion") == 0) {
				Vec3d randomPos = new Vec3d(Math.random() * 40 - 20, Math.random() * 20 - 10, Math.random() * 40 - 20);
				float explosionSize = 10 + level.random.nextFloat() * 10;
				ImprovedExplosion explosion = new ImprovedExplosion(entity.getLevel(), (Entity)entity, entity.getPos().add(randomPos), Math.round(explosionSize));
				explosion.doEntityExplosion(1f + 0.05f * explosionSize, true);
				explosion.doBlockExplosion(1f, 1f, 0.75f, 1.25f, false, false);
				level.playSound((Entity)entity, toBlockPos(entity.getPos()).add(toBlockPos(randomPos)), SoundEvents.ENTITY_GENERIC_EXPLODE.value(), SoundCategory.BLOCKS, 4f, (1f + (level.random.nextFloat() - level.random.nextFloat()) * 0.2f) * 0.7f);
				NbtCompound tag = entity.getPersistentData();
				tag.putInt("nextExplosion", 2 + level.random.nextInt(3));
				entity.setPersistentData(tag);
			}
			NbtCompound tag = entity.getPersistentData();
			tag.putInt("nextExplosion", entity.getPersistentData().getInt("nextExplosion") - 1);
			entity.setPersistentData(tag);
		}
	}
	
	@Override
	public void spawnParticles(IExplosiveEntity entity) {
		if(Math.random() < 0.15f) {
			entity.getLevel().addParticle(new DustParticleEffect(new Vector3f(0.37f, 1f, 1f), 1), entity.x() + Math.random() * 0.5f - Math.random() * 0.5f, entity.y() + 1f + Math.random() * 0.35f, entity.z() + Math.random() * 0.5f - Math.random() * 0.5f, 0, 0, 0);
		}
		if(Math.random() < 0.15f) {
			entity.getLevel().addParticle(new DustParticleEffect(new Vector3f(0.59f, 1f, 0f), 1), entity.x() + Math.random() * 0.5f - Math.random() * 0.5f, entity.y() + 1f + Math.random() * 0.35f, entity.z() + Math.random() * 0.5f - Math.random() * 0.5f, 0, 0, 0);
		}
		if(Math.random() < 0.15f) {
			entity.getLevel().addParticle(new DustParticleEffect(new Vector3f(0.11f, 0.26f, 0.11f), 1), entity.x() + Math.random() * 0.5f - Math.random() * 0.5f, entity.y() + 1f + Math.random() * 0.35f, entity.z() + Math.random() * 0.5f - Math.random() * 0.5f, 0, 0, 0);
		}
		if(Math.random() < 0.15f) {
			entity.getLevel().addParticle(new DustParticleEffect(new Vector3f(0.16f, 0.42f, 0.15f), 1), entity.x() + Math.random() * 0.5f - Math.random() * 0.5f, entity.y() + 1f + Math.random() * 0.35f, entity.z() + Math.random() * 0.5f - Math.random() * 0.5f, 0, 0, 0);
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

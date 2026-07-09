package luckytnt.tnteffects;

import org.joml.Vector3f;

import luckytnt.registry.SoundRegistry;
import luckytntlib.util.IExplosiveEntity;
import luckytntlib.util.explosions.ImprovedExplosion;
import luckytntlib.util.tnteffects.PrimedTNTEffect;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.entity.Entity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.sounds.SoundSource;
import net.minecraft.core.BlockPos;

public class DeathRayEffect extends PrimedTNTEffect {

	@Override
	public void explosionTick(IExplosiveEntity ent) {
		if(ent.getTNTFuse() == 480) {
			CompoundTag tag = ent.getPersistentData();
			tag.putInt("explosionSize", 1);
			tag.putInt("particleSize", 1);
			ent.setPersistentData(tag);
			ent.getLevel().playSound(null, ent.x(), ent.y(), ent.z(), SoundRegistry.DEATH_RAY.get(), SoundSource.HOSTILE, 20, 1);
			((Entity)ent).setDeltaMovement(0, 0, 0);
		}
		
		if(ent.getTNTFuse() < 80) {
			((Entity)ent).setDeltaMovement(0, 0, 0);
			((Entity)ent).setPos(((Entity)ent).xo, ((Entity)ent).yo, ((Entity)ent).zo);
			
			int size = ent.getPersistentData().getIntOr("explosionSize", 0);
			
			for(int offX = -size; offX <= size; offX++) {
				for(int offY = size; offY >= -size; offY--) {
					for(int offZ = -size; offZ <= size; offZ++) {
						double distance = Math.sqrt(offX * offX + offY * offY + offZ * offZ);
						if(distance <= size && distance > size - 3) {
							BlockPos pos = new BlockPos((int)ent.getPos().x, (int)ent.getPos().y, (int)ent.getPos().z).offset(offX, offY, offZ);
							BlockState state = ent.getLevel().getBlockState(pos);
							if(distance >= 75) {
								if(state.getBlock().getExplosionResistance() < 2000 && !state.isAir()) {
									if(Math.random() < 0.1f) {
										ent.getLevel().setBlock(pos, Blocks.LAVA.defaultBlockState(), 3);
									} else if(Math.random() < 0.8f) {
										ent.getLevel().setBlock(pos, Blocks.OBSIDIAN.defaultBlockState(), 3);
									}
								}
							} else if(state.getBlock().getExplosionResistance() < 2000 && !state.isAir()) {
								state.getBlock().wasExploded(ent.getLevel(), pos, ImprovedExplosion.dummyExplosion(ent.getLevel()));
								ent.getLevel().setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
							}
						}
					}
				}
			}
			
			CompoundTag tag = ent.getPersistentData();
			tag.putInt("explosionSize", ent.getPersistentData().getIntOr("explosionSize", 0) + 1);
			ent.setPersistentData(tag);
		}
	}
	
	@Override
	public void spawnParticles(IExplosiveEntity ent) {
		if(ent.getTNTFuse() > 120) {
			ent.getLevel().addParticle(new DustParticleOptions(((int)(0.8f*255)<<16)|((int)(0f*255)<<8)|(int)(0f*255), 1f), ent.x(), ent.y(), ent.z(), 0, 0, 0);
		}
		if(ent.getTNTFuse() < 140) {
			for(int count = 0; count < 200; count++) {
				ent.getLevel().addParticle(new DustParticleOptions(((int)(0.5f*255)<<16)|((int)(0f*255)<<8)|(int)(2f*255), 10f), ent.x() + Math.random() - Math.random(), ent.y() + 135f - Math.random() * ent.getPersistentData().getIntOr("particleSize", 0), ent.z() + Math.random() - Math.random(), 0, 0, 0);
			}
			CompoundTag tag = ent.getPersistentData();
			tag.putInt("particleSize", ent.getPersistentData().getIntOr("particleSize", 0) + 2);
			ent.setPersistentData(tag);
		}
	}
	
	@Override
	public Block getBlock() {
		return Blocks.AIR;
	}
	
	@Override
	public int getDefaultFuse(IExplosiveEntity ent) {
		return 480;
	}
}

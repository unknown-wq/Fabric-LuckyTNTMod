package luckytnt.tnteffects;
import net.minecraft.server.level.ServerLevel;


import org.joml.Vector3f;

import luckytnt.registry.BlockRegistry;
import luckytnt.registry.EntityRegistry;
import luckytntlib.entity.PrimedLTNT;
import luckytntlib.util.IExplosiveEntity;
import luckytntlib.util.explosions.IForEachBlockExplosionEffect;
import luckytntlib.util.explosions.ImprovedExplosion;
import luckytntlib.util.tnteffects.PrimedTNTEffect;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.entity.Entity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.sounds.SoundSource;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.entity.EntitySpawnReason;

public class EasterEggEffect extends PrimedTNTEffect{
	
	@Override
	public void baseTick(IExplosiveEntity entity) {
		super.baseTick(entity);
		if(((Entity)entity).onGround() && entity.getPersistentData().getIntOr("level", 0) > 0) {
			serverExplosion(entity);
			Level level = entity.getLevel();
			entity.getLevel().playSound((Entity)entity, toBlockPos(entity.getPos()), SoundEvents.GENERIC_EXPLODE.value(), SoundSource.BLOCKS, 4f, (1f + (level.getRandom().nextFloat() - level.getRandom().nextFloat()) * 0.2f) * 0.7f);
			entity.destroy();
		}
	}
	
	@Override
	public void serverExplosion(IExplosiveEntity entity) {
		int level = entity.getPersistentData().getIntOr("level", 0);
		ImprovedExplosion explosion = new ImprovedExplosion(entity.getLevel(), (Entity)entity, entity.getPos(), 15);
		explosion.doBlockExplosion(1f, 1f, 1f, 1.25f, false, false);
		explosion.doBlockExplosion(new IForEachBlockExplosionEffect() {		
			@Override
			public void doBlockExplosion(Level level, BlockPos pos, BlockState state, double distance) {
				if(Math.random() < 0.66f && !state.isAir()) {
					state.getBlock().wasExploded((ServerLevel) level, pos, explosion);
					if(Math.random() < 0.5f) {
						entity.getLevel().setBlockAndUpdate(pos, Blocks.MELON.defaultBlockState());
					}
					else {
						entity.getLevel().setBlockAndUpdate(pos, Blocks.PUMPKIN.defaultBlockState());
					}
				}
			}
		});
		if(level + 1 == 4) {
			entity.destroy();
		}
		else {
			for(int count = 0; count < 4; count++) {
				PrimedLTNT tnt = EntityRegistry.EASTER_EGG.get().create(entity.getLevel(), EntitySpawnReason.MOB_SUMMONED);
				tnt.setPos(entity.getPos());
				tnt.setOwner(entity.owner());
				tnt.setDeltaMovement(Math.random() * 2 - 1, 1 + Math.random(), Math.random() * 2 - 1);
				CompoundTag tag = tnt.getPersistentData();
				tag.putInt("level", level + 1);
				tnt.setPersistentData(tag);
				entity.getLevel().addFreshEntity(tnt);
			}
		}
	}
	
	@Override
	public void spawnParticles(IExplosiveEntity entity) {
		entity.getLevel().addParticle(new DustParticleOptions(((int)(0f*255)<<16)|((int)(0.5f*255)<<8)|(int)(0f*255), 1), entity.x(), entity.y() + 1f, entity.z(), 0, 0, 0);
		entity.getLevel().addParticle(new DustParticleOptions(((int)(1f*255)<<16)|((int)(0.5f*255)<<8)|(int)(0f*255), 1), entity.x() + 0.2f, entity.y() + 1f, entity.z() + 0.2f, 0, 0, 0);
		entity.getLevel().addParticle(new DustParticleOptions(((int)(1f*255)<<16)|((int)(0.5f*255)<<8)|(int)(0f*255), 1), entity.x() - 0.2f, entity.y() + 1f, entity.z() - 0.2f, 0, 0, 0);
		entity.getLevel().addParticle(new DustParticleOptions(((int)(1f*255)<<16)|((int)(0.5f*255)<<8)|(int)(0f*255), 1), entity.x() + 0.2f, entity.y() + 1f, entity.z() - 0.2f, 0, 0, 0);
		entity.getLevel().addParticle(new DustParticleOptions(((int)(1f*255)<<16)|((int)(0.5f*255)<<8)|(int)(0f*255), 1), entity.x() - 0.2f, entity.y() + 1f, entity.z() + 0.2f, 0, 0, 0);
	}
	
	@Override
	public Block getBlock() {
		return BlockRegistry.EASTER_EGG.get();
	}
	
	@Override
	public int getDefaultFuse(IExplosiveEntity entity) {
		return 120;
	}
}

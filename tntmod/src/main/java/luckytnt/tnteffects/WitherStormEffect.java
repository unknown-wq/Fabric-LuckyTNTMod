package luckytnt.tnteffects;

import org.joml.Vector3f;

import luckytnt.registry.BlockRegistry;
import luckytntlib.util.IExplosiveEntity;
import luckytntlib.util.explosions.IForEachBlockExplosionEffect;
import luckytntlib.util.explosions.ImprovedExplosion;
import luckytntlib.util.tnteffects.PrimedTNTEffect;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.boss.WitherEntity;
import net.minecraft.entity.mob.WitherSkeletonEntity;
import net.minecraft.core.particles.DustParticleEffect;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;

public class WitherStormEffect extends PrimedTNTEffect {
	
	@Override
	public void serverExplosion(IExplosiveEntity ent) {
		ImprovedExplosion explosion = new ImprovedExplosion(ent.getLevel(), (Entity)ent, ent.getPos(), 50);
		explosion.doEntityExplosion(3f, true);
		explosion.doBlockExplosion(1f, 1.3f, 1f, 1f, false, false);
		
		ImprovedExplosion explosion2 = new ImprovedExplosion(ent.getLevel(), (Entity)ent, ent.getPos(), 50);
		explosion2.doBlockExplosion(new IForEachBlockExplosionEffect() {
			
			@Override
			public void doBlockExplosion(Level level, BlockPos pos, BlockState state, double distance) {
				if(state.getBlock().getBlastResistance() < 100 && !state.isAir()) {
					if(Math.random() < 0.7D) {
						state.getBlock().onDestroyedByExplosion(level, pos, explosion);
						level.setBlockState(pos, Blocks.SOUL_SAND.getDefaultState(), 3);
					} else {
						state.getBlock().onDestroyedByExplosion(level, pos, explosion);
						level.setBlockState(pos, Blocks.SOUL_SOIL.getDefaultState(), 3);
					}
				}
			}
		});
		
		WitherEntity wither = new WitherEntity(EntityType.WITHER, ent.getLevel());
		wither.setPosition(ent.getPos());
		ent.getLevel().addFreshEntity(wither);
		
		for(int i = 0; i < 160; i++) {
			int offX = (int)Math.round(Math.random() * 140D - 70D);
			int offZ = (int)Math.round(Math.random() * 140D - 70D);
			WitherSkeletonEntity skeleton = new WitherSkeletonEntity(EntityType.WITHER_SKELETON, ent.getLevel());
			if(ent.getLevel() instanceof ServerLevel sl) {
				skeleton.initialize(sl, ent.getLevel().getLocalDifficulty(toBlockPos(ent.getPos())), SpawnReason.MOB_SUMMONED, null);
			}
			for(int y = ent.getLevel().getTopY(); y >= ent.getLevel().getBottomY(); y--) {
				BlockPos pos = new BlockPos(Mth.floor(ent.x() + offX), y, Mth.floor(ent.z() + offZ));
				BlockState state = ent.getLevel().getBlockState(pos);
				if(!Block.isFaceFullSquare(state.getCollisionShape(ent.getLevel(), pos), Direction.UP) && Block.isFaceFullSquare(ent.getLevel().getBlockState(pos.down()).getCollisionShape(ent.getLevel(), pos.down()), Direction.UP)) {
					skeleton.setPosition(pos.getX() + 0.5D, y, pos.getZ() + 0.5D);
					break;
				}
			}
			ent.getLevel().addFreshEntity(skeleton);
		}
		
		ent.getLevel().playSound(null, ent.x(), ent.y(), ent.z(), SoundEvents.ENTITY_WITHER_SPAWN, SoundSource.HOSTILE, 3, 1);
	}
	
	@Override
	public void explosionTick(IExplosiveEntity ent) {
		if(ent.getLevel() instanceof ServerLevel sl) {
			sl.spawnParticles(new DustParticleEffect(new Vector3f(0.2f, 0.2f, 0.2f), 1f), ent.x(), ent.y() + 2.25f, ent.z(), 20, 0.1f, 0.5f, 0.1f, 0);
			sl.spawnParticles(new DustParticleEffect(new Vector3f(0.2f, 0.2f, 0.2f), 1f), ent.x(), ent.y() + 3f, ent.z(), 20, 0.05f, 0.05f, 0.5f, 0);
			sl.spawnParticles(new DustParticleEffect(new Vector3f(0.2f, 0.2f, 0.2f), 1f), ent.x(), ent.y() + 2.5f, ent.z(), 20, 0.05f, 0.05f, 0.3f, 0);
			sl.spawnParticles(new DustParticleEffect(new Vector3f(0.2f, 0.2f, 0.2f), 1f), ent.x(), ent.y() + 2f, ent.z(), 20, 0.05f, 0.05f, 0.2f, 0);
			sl.spawnParticles(new DustParticleEffect(new Vector3f(0.2f, 0.2f, 0.2f), 1f), ent.x(), ent.y() + 3.5f, ent.z(), 20, 0.2f, 0.2f, 0.2f, 0);
			sl.spawnParticles(new DustParticleEffect(new Vector3f(0.2f, 0.2f, 0.2f), 1f), ent.x(), ent.y() + 3.25f, ent.z() + 1, 20, 0.15f, 0.15f, 0.15f, 0);
			sl.spawnParticles(new DustParticleEffect(new Vector3f(0.2f, 0.2f, 0.2f), 1f), ent.x(), ent.y() + 3.25f, ent.z() - 1, 20, 0.15f, 0.15f, 0.15f, 0);
		}
	}
	
	@Override
	public void spawnParticles(IExplosiveEntity ent) {
		
	}
	
	@Override
	public Block getBlock() {
		return BlockRegistry.WITHER_STORM.get();
	}
	
	@Override
	public int getDefaultFuse(IExplosiveEntity ent) {
		return 160;
	}

}

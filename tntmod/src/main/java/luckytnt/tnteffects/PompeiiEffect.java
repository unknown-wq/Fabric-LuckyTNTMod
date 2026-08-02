package luckytnt.tnteffects;

import net.minecraft.world.entity.EntitySpawnReason;
import luckytnt.registry.BlockRegistry;
import luckytnt.registry.EntityRegistry;
import luckytntlib.entity.LExplosiveProjectile;
import luckytntlib.entity.PrimedLTNT;
import luckytntlib.util.IExplosiveEntity;
import luckytntlib.util.tnteffects.PrimedTNTEffect;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.sounds.SoundEvents;

public class PompeiiEffect extends PrimedTNTEffect{

	@SuppressWarnings("resource")
	@Override
	public void explosionTick(IExplosiveEntity entity) {
		// explosionTick runs on both logical sides and addFreshEntity is a no-op on the client, so the
		// client used to build and discard 30 projectiles per batch as well (300 over the fuse).
		if(!(entity.getLevel() instanceof ServerLevel level)) {
			return;
		}
		if(entity instanceof PrimedLTNT) {
			if(entity.getTNTFuse() < 150) {
				if(entity.getTNTFuse() % 15 == 0) {
					for(int i = 0; i < 30; i++) {
						LExplosiveProjectile pompeii = EntityRegistry.POMPEII_PROJECTILE.get().create(level, EntitySpawnReason.MOB_SUMMONED);
						pompeii.setPos(entity.getPos());
						pompeii.setOwner(entity.owner());
						pompeii.shoot((Math.random() * 3D - 1.5D) * 0.1f, 0.6f + Math.random() * 0.4f, (Math.random() * 3D - 1.5D) * 0.1f, 3f + level.getRandom().nextFloat() * 2f, 0f);
						pompeii.igniteForTicks(20000);
						level.addFreshEntity(pompeii);
					}
					// was inside the loop: 30 identical explosion sounds broadcast from the same coordinate
					// per batch, i.e. 300 sound packets per detonation for 10 audible events
					level.playSound(null, toBlockPos(entity.getPos()), SoundEvents.GENERIC_EXPLODE.value(), SoundSource.MASTER, 3, 1);
				}
			}
		}
	}
	
	@Override
	public void serverExplosion(IExplosiveEntity ent) {
		if(ent instanceof LExplosiveProjectile) {
			if(ent.getLevel().getBlockState(toBlockPos(ent.getPos()).above()).getBlock().getExplosionResistance() <= 200) {
				ent.getLevel().setBlock(toBlockPos(ent.getPos()).above(), Blocks.LAVA.defaultBlockState(), 3);
			}
		}
	}
	
	@Override
	public void spawnParticles(IExplosiveEntity ent) {
		if(ent instanceof PrimedLTNT) {
			ent.getLevel().addParticle(ParticleTypes.FLAME, ent.x() + 0.5f, ent.y() + 1f, ent.z() + 0.5f, 0.05f, 0.2f, 0.05f);
			ent.getLevel().addParticle(ParticleTypes.FLAME, ent.x() - 0.5f, ent.y() + 1f, ent.z() - 0.5f, -0.05f, 0.2f, -0.05f);
			ent.getLevel().addParticle(ParticleTypes.FLAME, ent.x() + 0.5f, ent.y() + 1f, ent.z() - 0.5f, 0.05f, 0.2f, -0.05f);
			ent.getLevel().addParticle(ParticleTypes.FLAME, ent.x() - 0.5f, ent.y() + 1f, ent.z() + 0.5f, -0.05f, 0.2f, 0.05f);
			ent.getLevel().addParticle(ParticleTypes.LAVA, ent.x(), ent.y() + 1f, ent.z(), 0, 0, 0);
		}
		else {
			ent.getLevel().addParticle(ParticleTypes.LARGE_SMOKE, ent.x(), ent.y() + 0.5f, ent.z(), 0, 0.1f, 0);
		}
	}
	
	@Override
	public BlockState getBlockState(IExplosiveEntity ent) {
		return ent instanceof PrimedLTNT ? BlockRegistry.POMPEII.get().defaultBlockState() : Blocks.MAGMA_BLOCK.defaultBlockState();
	}
	
	@Override
	public boolean airFuse() {
		return true;
	}
	
	@Override
	public int getDefaultFuse(IExplosiveEntity entity) {
		return entity instanceof PrimedLTNT ? 220 : 100000;
	}
}

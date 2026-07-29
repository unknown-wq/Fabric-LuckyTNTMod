package luckytnt.tnteffects;

import net.minecraft.world.entity.EntitySpawnReason;
import luckytnt.registry.BlockRegistry;
import luckytnt.registry.EntityRegistry;
import luckytntlib.entity.LExplosiveProjectile;
import luckytntlib.util.IExplosiveEntity;
import luckytntlib.util.tnteffects.PrimedTNTEffect;
import net.minecraft.world.level.block.Block;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.sounds.SoundEvents;

public class SolarEruptionEffect extends PrimedTNTEffect {

	@Override
	public void explosionTick(IExplosiveEntity ent) {
		// explosionTick runs on both logical sides; addFreshEntity is a no-op on the client, so the
		// 40 projectile allocations per batch were pure waste there.
		if(ent.getTNTFuse() < 260 && ent.getTNTFuse() % 20 == 0 && ent.getLevel() instanceof ServerLevel sLevel) {
			for(int count = 0; count < 40; count++) {
				LExplosiveProjectile tnt = EntityRegistry.SOLAR_ERUPTION_PROJECTILE.get().create(sLevel, EntitySpawnReason.MOB_SUMMONED);
				tnt.setPos(ent.getPos());
				tnt.setOwner(ent.owner());
				tnt.setDeltaMovement(Math.random() * 3f - Math.random() * 3f, 5 + Math.random() * 2, Math.random() * 3f - Math.random() * 3f);
				tnt.igniteForTicks(20000);
				sLevel.addFreshEntity(tnt);
			}
			// was inside the loop: 40 identical sounds broadcast from the same position per batch
			sLevel.playSound(null, toBlockPos(ent.getPos()), SoundEvents.TNT_PRIMED, SoundSource.MASTER, 3, 1);
		}
	}
	
	@Override
	public void spawnParticles(IExplosiveEntity ent) {
		ent.getLevel().addParticle(ParticleTypes.FLAME, ent.x() + 0.5f, ent.y() + 0.5f, ent.z() + 0.5f, 0.1f, 0.4f, 0.1f);
		ent.getLevel().addParticle(ParticleTypes.FLAME, ent.x() - 0.5f, ent.y() + 0.5f, ent.z() - 0.5f, -0.1f, 0.4f, -0.1f);
		ent.getLevel().addParticle(ParticleTypes.FLAME, ent.x() + 0.5f, ent.y() + 0.5f, ent.z() - 0.5f, 0.1f, 0.4f, -0.1f);
		ent.getLevel().addParticle(ParticleTypes.FLAME, ent.x() - 0.5f, ent.y() + 0.5f, ent.z() + 0.5f, -0.1f, 0.4f, 0.1f);
		ent.getLevel().addParticle(ParticleTypes.FLAME, ent.x() + 0.5f, ent.y() + 0.5f, ent.z() + 0.5f, 0.05f, 0f, 0.05f);
		ent.getLevel().addParticle(ParticleTypes.FLAME, ent.x() - 0.5f, ent.y() + 0.5f, ent.z() - 0.5f, -0.05f, 0f, -0.05f);
		ent.getLevel().addParticle(ParticleTypes.FLAME, ent.x() + 0.5f, ent.y() + 0.5f, ent.z() - 0.5f, 0.05f, 0f, -0.05f);
		ent.getLevel().addParticle(ParticleTypes.FLAME, ent.x() - 0.5f, ent.y() + 0.5f, ent.z() + 0.5f, -0.05f, 0f, 0.05f);
		ent.getLevel().addParticle(ParticleTypes.LAVA, ent.x() + 0.5f, ent.y() + 1f, ent.z() + 0.5f, 0, 0, 0);
		ent.getLevel().addParticle(ParticleTypes.LAVA, ent.x() - 0.5f, ent.y() + 1f, ent.z() - 0.5f, 0, 0, 0);
		ent.getLevel().addParticle(ParticleTypes.LAVA, ent.x() + 0.5f, ent.y() + 1f, ent.z() - 0.5f, 0, 0, 0);
		ent.getLevel().addParticle(ParticleTypes.LAVA, ent.x() - 0.5f, ent.y() + 1f, ent.z() + 0.5f, 0, 0, 0);
	}
	
	@Override
	public Block getBlock() {
		return BlockRegistry.SOLAR_ERUPTION.get();
	}
	
	@Override
	public int getDefaultFuse(IExplosiveEntity ent) {
		return 360;
	}
}

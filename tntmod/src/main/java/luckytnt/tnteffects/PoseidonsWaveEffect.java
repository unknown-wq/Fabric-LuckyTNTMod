package luckytnt.tnteffects;

import luckytnt.registry.BlockRegistry;
import luckytntlib.util.IExplosiveEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;

public class PoseidonsWaveEffect extends OceanTNTEffect {

	public PoseidonsWaveEffect() {
		super(() -> BlockRegistry.POSEIDONS_WAVE, 60, 20, 20);
	}
	
	@Override
	public void baseTick(IExplosiveEntity entity) {
		super.baseTick(entity);
		if(entity.getTNTFuse() == 179) {
			if(entity.getLevel() instanceof ServerWorld sl) {
				sl.setWeather(0, 10000, true, true);
			}
		}
	}

	@Override
	public void spawnParticles(IExplosiveEntity ent) {
		ent.getLevel().addParticle(ParticleTypes.SPLASH, ent.x(), ent.y() + 1f, ent.z(), 0, 0, 0);
	}
	
	@Override
	public int getDefaultFuse(IExplosiveEntity entity) {
		return 180;
	}
}

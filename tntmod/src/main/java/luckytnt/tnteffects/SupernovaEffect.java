package luckytnt.tnteffects;

import luckytnt.registry.BlockRegistry;
import luckytntlib.util.IExplosiveEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.level.Level;

public class SupernovaEffect extends SphereTNTEffect {

	public SupernovaEffect() {
		super(() -> BlockRegistry.SUPERNOVA, 200);
	}

	@Override
	public void explosionTick(IExplosiveEntity ent) {
		if(ent.getTNTFuse() == 300) {
			Level level = ent.getLevel();
			Entity lighting = new LightningBolt(EntityTypes.LIGHTNING_BOLT, level);
			lighting.setPos(ent.x(), ent.y(), ent.z());
			level.addFreshEntity(lighting);
		}
	}

	@Override
	public void spawnParticles(IExplosiveEntity ent) {
		//this runs every tick for 300 ticks, so the level, the position and the trigonometry
		//(2 calls instead of 6 per step) are only evaluated once per ring position
		final Level level = ent.getLevel();
		final double x = ent.x();
		final double y = ent.y() + 0.5f;
		final double z = ent.z();
		for(int step = 0; step < 60; step++) {
			double angle = step * 6D * Math.PI / 180;
			double cos = 2 * Math.cos(angle);
			double sin = 2 * Math.sin(angle);
			level.addParticle(ParticleTypes.FLAME, x + cos, y, z + sin, 0, 0, 0);
			level.addParticle(ParticleTypes.FLAME, x + cos, y + sin, z, 0, 0, 0);
			level.addParticle(ParticleTypes.FLAME, x, y + cos, z + sin, 0, 0, 0);
		}
	}
	
	@Override
	public int getDefaultFuse(IExplosiveEntity ent) {
		return 300;
	}
}

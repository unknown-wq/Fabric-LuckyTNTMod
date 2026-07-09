package luckytnt.tnteffects;

import luckytnt.registry.BlockRegistry;
import luckytntlib.util.IExplosiveEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LightningEntity;
import net.minecraft.particle.ParticleTypes;

public class SupernovaEffect extends SphereTNTEffect {

	public SupernovaEffect() {
		super(() -> BlockRegistry.SUPERNOVA, 200);
	}

	@Override
	public void explosionTick(IExplosiveEntity ent) {
		if(ent.getTNTFuse() == 300) {
			Entity lighting = new LightningEntity(EntityType.LIGHTNING_BOLT, ent.getLevel());
			lighting.setPosition(ent.x(), ent.y(), ent.z());
			ent.getLevel().spawnEntity(lighting);
		}
	}
	
	@Override
	public void spawnParticles(IExplosiveEntity ent) {
		for(double angle = 0; angle < 360; angle += 6D) {
			ent.getLevel().addParticle(ParticleTypes.FLAME, ent.x() + 2 * Math.cos(angle * Math.PI / 180), ent.y() + 0.5f, ent.z() + 2 * Math.sin(angle * Math.PI / 180), 0, 0, 0);
			ent.getLevel().addParticle(ParticleTypes.FLAME, ent.x() + 2 * Math.cos(angle * Math.PI / 180), ent.y() + 0.5f + 2 * Math.sin(angle * Math.PI / 180), ent.z(), 0, 0, 0);
			ent.getLevel().addParticle(ParticleTypes.FLAME, ent.x(), ent.y() + 0.5f + 2 * Math.cos(angle * Math.PI / 180), ent.z() + 2 * Math.sin(angle * Math.PI / 180), 0, 0, 0);
		}
	}
	
	@Override
	public int getDefaultFuse(IExplosiveEntity ent) {
		return 300;
	}
}

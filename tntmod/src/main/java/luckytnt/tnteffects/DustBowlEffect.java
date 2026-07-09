package luckytnt.tnteffects;

import luckytnt.registry.BlockRegistry;
import luckytntlib.util.IExplosiveEntity;
import luckytntlib.util.tnteffects.PrimedTNTEffect;
import net.minecraft.block.Block;
import net.minecraft.particle.ParticleTypes;

public class DustBowlEffect extends PrimedTNTEffect {

	@Override
	public void serverExplosion(IExplosiveEntity entity) {
		WastelandTNTEffect.doVaporizeExplosion(entity, 25, true);
	}
	
	@Override
	public void spawnParticles(IExplosiveEntity ent) {
		ent.getLevel().addParticle(ParticleTypes.CLOUD, ent.x() + Math.random() * 6 - Math.random() * 6, ent.y() + 0.5f, ent.z() + Math.random() * 6 - Math.random() * 6, 0, 0, 0);
	}

	@Override
	public Block getBlock() {
		return BlockRegistry.DUST_BOWL.get();
	}
	
	@Override
	public int getDefaultFuse(IExplosiveEntity ent) {
		return 120;
	}
}

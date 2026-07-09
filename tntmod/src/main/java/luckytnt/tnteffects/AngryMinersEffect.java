package luckytnt.tnteffects;

import org.joml.Vector3f;

import luckytnt.entity.AngryMiner;
import luckytnt.registry.BlockRegistry;
import luckytnt.registry.EntityRegistry;
import luckytntlib.util.IExplosiveEntity;
import luckytntlib.util.tnteffects.PrimedTNTEffect;
import net.minecraft.block.Block;
import net.minecraft.particle.DustParticleEffect;

public class AngryMinersEffect extends PrimedTNTEffect{

	@Override
	public void serverExplosion(IExplosiveEntity entity) {
		for(int count = 0; count <= 8; count++) {
			AngryMiner miner = EntityRegistry.ANGRY_MINER.get().create(entity.getLevel());
			miner.setPosition(entity.getPos());
			entity.getLevel().spawnEntity(miner);
		}
	}
	
	@Override
	public void spawnParticles(IExplosiveEntity entity) {
		for(int count = 0; count < 8; count++) {
			entity.getLevel().addParticle(new DustParticleEffect(new Vector3f(0.5f, 0.3f, 0f), 1f), entity.x() + Math.random() * 0.25f -Math.random() * 0.25f, entity.y() + 1f + Math.random(), entity.z() + Math.random() * 0.25f - Math.random() * 0.25f, 0, 0, 0);
		}
		for(int count = 0; count < 8; count++) {
			entity.getLevel().addParticle(new DustParticleEffect(new Vector3f(1f, 1f, 0f), 1f), entity.x() + Math.random() * 0.25f -Math.random() * 0.25f, entity.y() + 2f + Math.random() * 0.25f, entity.z() + Math.random() * 0.25f -Math.random() * 0.25f, 0, 0, 0);
		}
	}
	
	@Override
	public Block getBlock() {
		return BlockRegistry.ANGRY_MINERS.get();
	}
}

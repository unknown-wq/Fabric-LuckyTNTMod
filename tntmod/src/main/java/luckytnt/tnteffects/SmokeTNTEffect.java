package luckytnt.tnteffects;

import org.joml.Vector3f;

import luckytnt.registry.BlockRegistry;
import luckytntlib.util.IExplosiveEntity;
import luckytntlib.util.tnteffects.PrimedTNTEffect;
import net.minecraft.block.Block;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.server.world.ServerWorld;

public class SmokeTNTEffect extends PrimedTNTEffect{

	@Override
	public void explosionTick(IExplosiveEntity entity) {
		spawnParticles(entity);
		if(entity.getTNTFuse() < 460 && entity.getLevel() instanceof ServerWorld sLevel) {
			sLevel.spawnParticles(new DustParticleEffect(new Vector3f(entity.getPersistentData().getFloat("r"), entity.getPersistentData().getFloat("g"), entity.getPersistentData().getFloat("b")), 10f), entity.x(), entity.y(), entity.z(), 30, 2.5f, 2.5f, 2.5f, 0);
		}
	}
	
	@Override
	public void spawnParticles(IExplosiveEntity entity) {
		if(entity.getLevel() instanceof ServerWorld sLevel) {
			sLevel.spawnParticles(new DustParticleEffect(new Vector3f(entity.getPersistentData().getFloat("r"), entity.getPersistentData().getFloat("g"), entity.getPersistentData().getFloat("b")), 1f), entity.x(), entity.y() + 1f, entity.z(), 1, 0, 0, 0, 0);
		}
	}
	
	@Override
	public int getDefaultFuse(IExplosiveEntity entity) {
		return 520;
	}
	
	@Override
	public Block getBlock() {
		return BlockRegistry.SMOKE_TNT.get();
	}
}

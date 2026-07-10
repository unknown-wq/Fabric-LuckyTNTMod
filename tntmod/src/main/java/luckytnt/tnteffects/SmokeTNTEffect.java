package luckytnt.tnteffects;

import org.joml.Vector3f;

import luckytnt.registry.BlockRegistry;
import luckytntlib.util.IExplosiveEntity;
import luckytntlib.util.tnteffects.PrimedTNTEffect;
import net.minecraft.world.level.block.Block;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.server.level.ServerLevel;

public class SmokeTNTEffect extends PrimedTNTEffect{

	@Override
	public void explosionTick(IExplosiveEntity entity) {
		spawnParticles(entity);
		if(entity.getTNTFuse() < 460 && entity.getLevel() instanceof ServerLevel sLevel) {
			sLevel.sendParticles(new DustParticleOptions((((int)(entity.getPersistentData().getFloatOr("r", 0f) * 255)) << 16) | (((int)(entity.getPersistentData().getFloatOr("g", 0f) * 255)) << 8) | ((int)(entity.getPersistentData().getFloatOr("b", 0f) * 255)), 10f), entity.x(), entity.y(), entity.z(), 30, 2.5f, 2.5f, 2.5f, 0);
		}
	}
	
	@Override
	public void spawnParticles(IExplosiveEntity entity) {
		if(entity.getLevel() instanceof ServerLevel sLevel) {
			sLevel.sendParticles(new DustParticleOptions((((int)(entity.getPersistentData().getFloatOr("r", 0f) * 255)) << 16) | (((int)(entity.getPersistentData().getFloatOr("g", 0f) * 255)) << 8) | ((int)(entity.getPersistentData().getFloatOr("b", 0f) * 255)), 1f), entity.x(), entity.y() + 1f, entity.z(), 1, 0, 0, 0, 0);
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

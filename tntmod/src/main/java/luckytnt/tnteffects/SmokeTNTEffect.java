package luckytnt.tnteffects;

import luckytnt.registry.BlockRegistry;
import luckytntlib.util.IExplosiveEntity;
import luckytntlib.util.tnteffects.PrimedTNTEffect;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Block;

public class SmokeTNTEffect extends PrimedTNTEffect{

	@Override
	public void explosionTick(IExplosiveEntity entity) {
		//explosionTick runs on both logical sides, but every particle of this effect is a server side broadcast,
		//so the client half of the work was pure overhead
		if(!(entity.getLevel() instanceof ServerLevel sLevel)) {
			return;
		}
		double x = entity.x();
		double y = entity.y();
		double z = entity.z();
		//the colour used to be decoded twice per tick: 6 synched data reads and 6 tag lookups for one value
		int color = color(entity);
		sLevel.sendParticles(new DustParticleOptions(color, 1f), x, y + 1f, z, 1, 0, 0, 0, 0);
		if(entity.getTNTFuse() < 460) {
			sLevel.sendParticles(new DustParticleOptions(color, 10f), x, y, z, 30, 2.5f, 2.5f, 2.5f, 0);
		}
	}

	@Override
	public void spawnParticles(IExplosiveEntity entity) {
		if(entity.getLevel() instanceof ServerLevel sLevel) {
			sLevel.sendParticles(new DustParticleOptions(color(entity), 1f), entity.x(), entity.y() + 1f, entity.z(), 1, 0, 0, 0, 0);
		}
	}

	/**
	 * Reads the synched persistent data once and packs the three colour channels.
	 */
	private static int color(IExplosiveEntity entity) {
		CompoundTag data = entity.getPersistentData();
		return (((int)(data.getFloatOr("r", 0f) * 255)) << 16) | (((int)(data.getFloatOr("g", 0f) * 255)) << 8) | ((int)(data.getFloatOr("b", 0f) * 255));
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

package luckytnt.tnteffects;

import org.joml.Vector3f;

import luckytnt.registry.BlockRegistry;
import luckytntlib.util.IExplosiveEntity;
import luckytntlib.util.tnteffects.PrimedTNTEffect;
import net.minecraft.world.level.block.Block;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.server.level.ServerLevel;

public class NightTNTEffect extends PrimedTNTEffect{

	@Override
	public void serverExplosion(IExplosiveEntity entity) {
		if(entity.getLevel() instanceof ServerLevel sLevel) {
			// TODO(port-26.2): DISABLED — the day-time API was reworked into the WorldClock /
			// ServerClockManager system in 26.2; ServerLevel#setDayTime/#setTimeOfDay no longer exist.
			// Setting time now requires the dimension's default WorldClock holder and
			// server.clockManager().setTotalTicks(clock, 18000). Restore once that path is ported.
			// sLevel.setTimeOfDay(18000);
		}
	}
	
	@Override
	public void spawnParticles(IExplosiveEntity entity) {
		entity.getLevel().addParticle(new DustParticleOptions(((int)(0f*255)<<16)|((int)(0f*255)<<8)|(int)(0f*255), 1f), entity.x(), entity.y() + 1f, entity.z(), 0, -0.1f, 0);
	}
	
	@Override
	public Block getBlock() {
		return BlockRegistry.NIGHT_TNT.get();
	}
}

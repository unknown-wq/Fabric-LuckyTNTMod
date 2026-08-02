package luckytnt.tnteffects;

import net.minecraft.world.entity.EntitySpawnReason;

import luckytnt.config.LuckyTNTConfigValues;
import luckytnt.registry.BlockRegistry;
import luckytnt.registry.EntityRegistry;
import luckytntlib.entity.LExplosiveProjectile;
import luckytntlib.util.IExplosiveEntity;
import luckytntlib.util.tnteffects.PrimedTNTEffect;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Block;

public class MeteorStormEffect extends PrimedTNTEffect {

	@Override
	public void explosionTick(IExplosiveEntity ent) {
		// explosionTick runs on both logical sides and addFreshEntity is a no-op on the client, so the
		// client used to build and discard 6 meteors every 40th tick as well (108 over the fuse).
		if(!(ent.getLevel() instanceof ServerLevel level)) {
			return;
		}
		if(ent.getTNTFuse() % 40 == 0) {
			for(int count = 0; count < 6; count++) {
				LExplosiveProjectile meteor = EntityRegistry.LITTLE_METEOR.get().create(level, EntitySpawnReason.MOB_SUMMONED);
				meteor.setOwner(ent.owner());
				meteor.setPos(ent.x() + 400 * Math.random() - 200, ent.y() + LuckyTNTConfigValues.DROP_HEIGHT.get() / 2 * Math.random() + LuckyTNTConfigValues.DROP_HEIGHT.get() / 2, ent.z() + 400 * Math.random() - 200);
				level.addFreshEntity(meteor);
			}
		}
	}
	
	@Override
	public Block getBlock() {
		return BlockRegistry.METEOR_STORM.get();
	}
	
	@Override
	public int getDefaultFuse(IExplosiveEntity ent) {
		return 720;
	}
}

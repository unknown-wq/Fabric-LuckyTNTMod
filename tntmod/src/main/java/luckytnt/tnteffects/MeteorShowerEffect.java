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

public class MeteorShowerEffect extends PrimedTNTEffect{

	@Override
	public void explosionTick(IExplosiveEntity entity) {
		// explosionTick runs on both logical sides and addFreshEntity is a no-op on the client, so the
		// client used to build and discard 6 meteors every 10th tick as well (384 over the fuse).
		if(!(entity.getLevel() instanceof ServerLevel level)) {
			return;
		}
		if(entity.getTNTFuse() <= 640 && entity.getTNTFuse() % 10 == 0) {
			for(int count = 0; count <= 5; count++) {
				LExplosiveProjectile meteor = EntityRegistry.MINI_METEOR.get().create(level, EntitySpawnReason.MOB_SUMMONED);
				meteor.setOwner(entity.owner());
				meteor.setPos(entity.getPos().add(Math.random() * 400 - 200, LuckyTNTConfigValues.DROP_HEIGHT.get() + Math.random() * 50, Math.random() * 400 - 200));
				level.addFreshEntity(meteor);
			}
		}
	}
	
	@Override
	public Block getBlock() {
		return BlockRegistry.METEOR_SHOWER.get();
	}
	
	@Override
	public int getDefaultFuse(IExplosiveEntity entity) {
		return 720;
	}
}

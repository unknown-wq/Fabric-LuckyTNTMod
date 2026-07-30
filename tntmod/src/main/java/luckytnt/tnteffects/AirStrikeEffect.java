package luckytnt.tnteffects;

import luckytnt.config.LuckyTNTConfigValues;
import luckytnt.registry.BlockRegistry;
import luckytnt.registry.EntityRegistry;
import luckytntlib.entity.LExplosiveProjectile;
import luckytntlib.util.IExplosiveEntity;
import luckytntlib.util.tnteffects.PrimedTNTEffect;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.entity.EntitySpawnReason;

public class AirStrikeEffect extends PrimedTNTEffect{

	@Override
	public void explosionTick(IExplosiveEntity entity) {
		// explosionTick runs on both logical sides and addFreshEntity is a no-op on the client, so the
		// client used to build and discard 6 bombs every 5th tick as well (384 over the fuse).
		if(!(entity.getLevel() instanceof ServerLevel level)) {
			return;
		}
		if(entity.getTNTFuse() <= 320 && entity.getTNTFuse() % 5 == 0) {
			for(int count = 0; count <= 5; count++) {
				LExplosiveProjectile bomb = EntityRegistry.BOMB.get().create(level, EntitySpawnReason.MOB_SUMMONED);
				bomb.setPos(entity.getPos().add(Math.random() * 100 - 50, LuckyTNTConfigValues.DROP_HEIGHT.get() + Math.random() * 50, Math.random() * 100 - 50));
				bomb.setOwner(entity.owner());
				level.addFreshEntity(bomb);
			}
		}
	}
	
	@Override
	public Block getBlock() {
		return BlockRegistry.AIR_STRIKE.get();
	}
	
	@Override
	public int getDefaultFuse(IExplosiveEntity entity) {
		return 360;
	}
}

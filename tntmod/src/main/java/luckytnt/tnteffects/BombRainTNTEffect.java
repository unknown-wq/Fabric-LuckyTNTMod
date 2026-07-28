package luckytnt.tnteffects;

import luckytnt.registry.BlockRegistry;
import luckytnt.registry.EntityRegistry;
import luckytntlib.entity.LExplosiveProjectile;
import luckytntlib.util.IExplosiveEntity;
import luckytntlib.util.tnteffects.PrimedTNTEffect;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.entity.EntitySpawnReason;

public class BombRainTNTEffect extends PrimedTNTEffect {

	public static final int SPAWN_HEIGHT = 100;

	@Override
	public void serverExplosion(IExplosiveEntity entity) {
		LExplosiveProjectile bomb = EntityRegistry.BOMB.get().create(entity.getLevel(), EntitySpawnReason.MOB_SUMMONED);
		bomb.setPos(entity.x(), entity.y() + SPAWN_HEIGHT, entity.z());
		bomb.setOwner(entity.owner());
		entity.getLevel().addFreshEntity(bomb);
	}

	@Override
	public Block getBlock() {
		return BlockRegistry.BOMB_RAIN_TNT.get();
	}
}

package luckytnt.tnteffects.projectile;

import luckytnt.registry.EntityRegistry;
import luckytnt.registry.ItemRegistry;
import luckytnt.tnteffects.BombRainTNTEffect;
import luckytntlib.entity.LExplosiveProjectile;
import luckytntlib.util.IExplosiveEntity;
import luckytntlib.util.tnteffects.PrimedTNTEffect;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.item.Item;

public class BombRainDynamiteEffect extends PrimedTNTEffect {

	@Override
	public void serverExplosion(IExplosiveEntity entity) {
		LExplosiveProjectile bomb = EntityRegistry.BOMB.get().create(entity.getLevel(), EntitySpawnReason.MOB_SUMMONED);
		bomb.setPos(entity.x(), entity.y() + BombRainTNTEffect.SPAWN_HEIGHT, entity.z());
		bomb.setOwner(entity.owner());
		entity.getLevel().addFreshEntity(bomb);
	}

	@Override
	public Item getItem() {
		return ItemRegistry.BOMB_RAIN_DYNAMITE.get();
	}
}

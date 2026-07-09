package luckytnt.tnteffects.projectile;

import luckytnt.config.LuckyTNTConfigValues;
import luckytnt.registry.EntityRegistry;
import luckytnt.registry.ItemRegistry;
import luckytntlib.entity.LExplosiveProjectile;
import luckytntlib.util.IExplosiveEntity;
import luckytntlib.util.tnteffects.PrimedTNTEffect;
import net.minecraft.item.Item;
import net.minecraft.particle.ParticleTypes;

public class MeteorDynamiteEffect extends PrimedTNTEffect{

	@Override
	public void serverExplosion(IExplosiveEntity entity) {
		LExplosiveProjectile meteor = EntityRegistry.LITTLE_METEOR.get().create(entity.getLevel());
		meteor.setPosition(entity.getPos().add(0, LuckyTNTConfigValues.DROP_HEIGHT.get(), 0));
		meteor.setOwner(entity.owner());
		entity.getLevel().spawnEntity(meteor);
	}
	
	@Override
	public void spawnParticles(IExplosiveEntity entity) {
		entity.getLevel().addParticle(ParticleTypes.LAVA, entity.x(), entity.y(), entity.z(), 0, 0, 0);
	}
	
	@Override
	public Item getItem() {
		return ItemRegistry.METEOR_DYNAMITE.get();
	}
}

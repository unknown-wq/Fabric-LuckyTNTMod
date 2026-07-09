package luckytnt.tnteffects.projectile;

import luckytnt.registry.ItemRegistry;
import luckytntlib.util.IExplosiveEntity;
import luckytntlib.util.explosions.ImprovedExplosion;
import luckytntlib.util.tnteffects.PrimedTNTEffect;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;

public class ChristmasDynamiteProjectileEffect extends PrimedTNTEffect{

	@Override
	public void serverExplosion(IExplosiveEntity entity) {
		ImprovedExplosion explosion = new ImprovedExplosion(entity.getLevel(), (Entity)entity, entity.getPos(), 10);
		explosion.doEntityExplosion(0.75f, true);
		explosion.doBlockExplosion();
		((ServerWorld)entity.getLevel()).spawnParticles(ParticleTypes.WAX_OFF, entity.x() + Math.random() - 0.5f, entity.y() + Math.random() - 0.5f, entity.z() + Math.random() - 0.5f, 100, 0.5f, 0.5f, 0.5f, 0f);
	}
	
	@Override
	public void spawnParticles(IExplosiveEntity entity) {
		for(int i = 0; i < 7; i++) {
			entity.getLevel().addParticle(ParticleTypes.WAX_OFF, true, entity.x() + Math.random() - 0.5f, entity.y() + Math.random() - 0.5f, entity.z() + Math.random() - 0.5f, 0, 0, 0);
		}
	}
	
	@Override
	public Item getItem() {
		return ItemRegistry.CHRISTMAS_DYNAMITE.get();
	}
}

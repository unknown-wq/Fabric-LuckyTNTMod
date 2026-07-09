package luckytnt.tnteffects.projectile;

import luckytnt.registry.EntityRegistry;
import luckytnt.registry.ItemRegistry;
import luckytntlib.entity.LExplosiveProjectile;
import luckytntlib.util.IExplosiveEntity;
import luckytntlib.util.tnteffects.PrimedTNTEffect;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.particle.ParticleTypes;

public class DynamiteFireworkEffect extends PrimedTNTEffect{

	@Override
	public void serverExplosion(IExplosiveEntity entity) {
		for(int count = 0; count <= 300; count++) {
			LExplosiveProjectile dynamite = EntityRegistry.DYNAMITE.get().create(entity.getLevel());
			dynamite.setPosition(entity.getPos());
			dynamite.setOwner(dynamite.owner());
			dynamite.setVelocity(Math.random() * 2f - 1f, Math.random() * 2f - 1f, Math.random() * 2f - 1f);
			entity.getLevel().spawnEntity(dynamite);
		}
	}
	
	@Override
	public void explosionTick(IExplosiveEntity entity) {
		((Entity)entity).setVelocity(((Entity)entity).getVelocity().add(0f, 0.08f, 0f));
	}
	
	@Override
	public void spawnParticles(IExplosiveEntity entity) {
		entity.getLevel().addParticle(ParticleTypes.FLAME, entity.x(), entity.y(), entity.z(), 0, 0, 0);
	}
	
	@Override
	public boolean airFuse() {
		return true;
	}
	
	@Override
	public Item getItem() {
		return ItemRegistry.DYNAMITE_FIREWORK.get();
	}
	
	@Override
	public int getDefaultFuse(IExplosiveEntity entity) {
		return 40;
	}
}

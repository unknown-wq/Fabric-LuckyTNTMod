package luckytnt.tnteffects.projectile;

import luckytnt.registry.ItemRegistry;
import luckytntlib.util.IExplosiveEntity;
import luckytntlib.util.explosions.ImprovedExplosion;
import luckytntlib.util.tnteffects.PrimedTNTEffect;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.Item;

public class FloatingDynamiteEffect extends PrimedTNTEffect{

	/**
	 * BALANCE CHANGE: was 25. ImprovedExplosion cost grows roughly with the cube of the size, so this is
	 * a ~2.7x cheaper detonation on an item that can be thrown five times a second with no cooldown.
	 */
	private static final int EXPLOSION_SIZE = 18;

	@Override
	public void serverExplosion(IExplosiveEntity entity) {
		ImprovedExplosion explosion = new ImprovedExplosion(entity.getLevel(), (Entity)entity, entity.getPos(), EXPLOSION_SIZE);
		explosion.doEntityExplosion(2.5f, true);
		explosion.doBlockExplosion(1f, 1f, 1f, 1.5f, false, false);
	}
	
	@Override
	public void explosionTick(IExplosiveEntity entity) {
		// Deliberately not side-guarded: this is the only statement in the method and it has to keep
		// running on the client so the dynamite keeps floating smoothly between tracker updates.
		((Entity)entity).setDeltaMovement(((Entity)entity).getDeltaMovement().add(0f, 0.08f, 0f));
	}
	
	@Override
	public boolean airFuse() {
		return true;
	}
	
	@Override
	public Item getItem() {
		return ItemRegistry.FLOATING_DYNAMITE.get();
	}
	
	@Override
	public int getDefaultFuse(IExplosiveEntity entity) {
		return 40;
	}
}

package luckytnt.tnteffects.projectile;

import luckytnt.registry.ItemRegistry;
import luckytntlib.util.IExplosiveEntity;
import luckytntlib.util.explosions.ImprovedExplosion;
import luckytntlib.util.tnteffects.PrimedTNTEffect;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.Item;
import net.minecraft.nbt.CompoundTag;

public class UltralightDynamiteEffect extends PrimedTNTEffect{

	@Override
	public void serverExplosion(IExplosiveEntity entity) {
		ImprovedExplosion explosion = new ImprovedExplosion(entity.getLevel(), (Entity)entity, entity.getPos(), 10);
		explosion.doEntityExplosion(1.5f, true);
		explosion.doBlockExplosion(1f, 1f, 1f, 1.25f, false, false);
	}
	
	@Override
	public void explosionTick(IExplosiveEntity entity) {
		if(!((Entity)entity).isNoGravity()) {
			((Entity)entity).setNoGravity(true);
			CompoundTag tag = entity.getPersistentData();
			tag.putDouble("vecx", ((Entity)entity).getDeltaMovement().x);
			tag.putDouble("vecy", ((Entity)entity).getDeltaMovement().y);
			tag.putDouble("vecz", ((Entity)entity).getDeltaMovement().z);
			entity.setPersistentData(tag);
		}
		((Entity)entity).setDeltaMovement(entity.getPersistentData().getDoubleOr("vecx", 0), entity.getPersistentData().getDoubleOr("vecy", 0), entity.getPersistentData().getDoubleOr("vecz", 0));
	}
	
	@Override
	public boolean airFuse() {
		return true;
	}
	
	@Override
	public Item getItem() {
		return ItemRegistry.ULTRALIGHT_DYNAMITE.get();
	}
	
	@Override
	public int getDefaultFuse(IExplosiveEntity entity) {
		// BALANCE CHANGE: was 200. With setNoGravity(true) the projectile flies dead straight at its
		// launch speed for the whole fuse, so this is the maximum flight time before it self-destructs.
		return 100;
	}
}

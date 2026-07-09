package luckytnt.tnteffects.projectile;

import luckytnt.registry.ItemRegistry;
import luckytntlib.util.IExplosiveEntity;
import luckytntlib.util.explosions.ImprovedExplosion;
import luckytntlib.util.tnteffects.PrimedTNTEffect;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.nbt.NbtCompound;

public class UltralightDynamiteEffect extends PrimedTNTEffect{

	@Override
	public void serverExplosion(IExplosiveEntity entity) {
		ImprovedExplosion explosion = new ImprovedExplosion(entity.getLevel(), (Entity)entity, entity.getPos(), 10);
		explosion.doEntityExplosion(1.5f, true);
		explosion.doBlockExplosion(1f, 1f, 1f, 1.25f, false, false);
	}
	
	@Override
	public void explosionTick(IExplosiveEntity entity) {
		if(!((Entity)entity).hasNoGravity()) {
			((Entity)entity).setNoGravity(true);
			NbtCompound tag = entity.getPersistentData();
			tag.putDouble("vecx", ((Entity)entity).getVelocity().x);
			tag.putDouble("vecy", ((Entity)entity).getVelocity().y);
			tag.putDouble("vecz", ((Entity)entity).getVelocity().z);
			entity.setPersistentData(tag);
		}
		((Entity)entity).setVelocity(entity.getPersistentData().getDouble("vecx"), entity.getPersistentData().getDouble("vecy"), entity.getPersistentData().getDouble("vecz"));
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
		return 200;
	}
}

package luckytnt.tnteffects.projectile;

import luckytnt.registry.ItemRegistry;
import luckytntlib.util.IExplosiveEntity;
import luckytntlib.util.explosions.ImprovedExplosion;
import luckytntlib.util.tnteffects.PrimedTNTEffect;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.MathHelper;

public class AcceleratingDynamiteEffect extends PrimedTNTEffect{

	@Override
	public void serverExplosion(IExplosiveEntity entity) {
		ImprovedExplosion explosion = new ImprovedExplosion(entity.getLevel(), (Entity)entity, entity.getPos(), (int)Math.round(2f * MathHelper.clamp(entity.getPersistentData().getDouble("speed"), 1f, 20f)));
		explosion.doEntityExplosion(1.5f, true);
		explosion.doBlockExplosion(1f, 1f, 1f, 1.25f, false, false);
	}
	
	@Override
	public void explosionTick(IExplosiveEntity entity) {
		((Entity)entity).setVelocity(((Entity)entity).getVelocity().add(((Entity)entity).getVelocity().multiply(0.05f)));
		if(((Entity)entity).getVelocity().length() > entity.getPersistentData().getDouble("speed")) {
			NbtCompound nbt = entity.getPersistentData();
			nbt.putDouble("speed", ((Entity)entity).getVelocity().length());
			entity.setPersistentData(nbt);
		}
	}
	
	@Override
	public boolean airFuse() {
		return true;
	}
	
	@Override
	public Item getItem() {
		return ItemRegistry.ACCELERATING_DYNAMITE.get();
	}
	
	@Override
	public int getDefaultFuse(IExplosiveEntity entity) {
		return 10000;
	}
}

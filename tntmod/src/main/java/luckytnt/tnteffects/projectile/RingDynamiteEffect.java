package luckytnt.tnteffects.projectile;

import net.minecraft.world.entity.EntitySpawnReason;


import luckytnt.registry.EntityRegistry;
import luckytnt.registry.ItemRegistry;
import luckytntlib.entity.LExplosiveProjectile;
import luckytntlib.util.IExplosiveEntity;
import luckytntlib.util.tnteffects.PrimedTNTEffect;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.Item;
import net.minecraft.world.phys.Vec3;

public class RingDynamiteEffect extends PrimedTNTEffect {

	@Override
	public void serverExplosion(IExplosiveEntity ent) {
		Vec3 vec = ((Entity)ent).getDeltaMovement().normalize().scale(4D);
		
		Vec3 left = (int)Math.round(vec.x) == 0 && (int)Math.round(vec.z) == 0 ? new Vec3(1, 0, 0) : new Vec3(vec.x * Math.cos(0.5 * Math.PI) + vec.z * Math.sin(0.5 * Math.PI), 0, -vec.x * Math.sin(0.5 * Math.PI) + vec.z * Math.cos(0.5 * Math.PI)).normalize();
		Vec3 right = left.reverse().normalize();
		Vec3 up = left.cross(vec).normalize();
		Vec3 down = up.reverse().normalize();
		
		Vec3 rightup = right.add(up).normalize();
		Vec3 rightdown = right.add(down).normalize();
		Vec3 leftup = left.add(up).normalize();
		Vec3 leftdown = left.add(down).normalize();
		
		Vec3[] array = new Vec3[] {
			vec.add(right).normalize(),
			vec.add(left).normalize(),
			vec.add(up).normalize(),
			vec.add(down).normalize(),
			vec.add(rightdown).normalize(),
			vec.add(rightup).normalize(),
			vec.add(leftdown).normalize(),
			vec.add(leftup).normalize()
		};

		for(Vec3 direction : array) {
			LExplosiveProjectile dynamite = EntityRegistry.DYNAMITE.get().create(ent.getLevel(), EntitySpawnReason.MOB_SUMMONED);
			dynamite.setOwner(ent.owner());
			dynamite.setPos(ent.getPos());
			dynamite.setDeltaMovement(direction.scale(2D));
			ent.getLevel().addFreshEntity(dynamite);
		}
	}
	
	@Override
	public Item getItem() {
		return ItemRegistry.RING_DYNAMITE.get();
	}
	
	@Override
	public int getDefaultFuse(IExplosiveEntity ent) {
		return 1;
	}
	
	@Override
	public boolean playsSound() {
		return false;
	}
	
	@Override
	public boolean airFuse() {
		return true;
	}
	
	@Override
	public boolean explodesOnImpact() {
		return false;
	}
}

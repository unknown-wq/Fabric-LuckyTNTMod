package luckytnt.tnteffects.projectile;


import luckytnt.registry.EntityRegistry;
import luckytnt.registry.ItemRegistry;
import luckytntlib.entity.LExplosiveProjectile;
import luckytntlib.util.IExplosiveEntity;
import luckytntlib.util.tnteffects.PrimedTNTEffect;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.util.math.Vec3d;

public class RingDynamiteEffect extends PrimedTNTEffect {

	@Override
	public void serverExplosion(IExplosiveEntity ent) {
		Vec3d vec = ((Entity)ent).getVelocity().normalize().multiply(4D);
		
		Vec3d left = (int)Math.round(vec.x) == 0 && (int)Math.round(vec.z) == 0 ? new Vec3d(1, 0, 0) : new Vec3d(vec.x * Math.cos(0.5 * Math.PI) + vec.z * Math.sin(0.5 * Math.PI), 0, -vec.x * Math.sin(0.5 * Math.PI) + vec.z * Math.cos(0.5 * Math.PI)).normalize();
		Vec3d right = left.negate().normalize();
		Vec3d up = left.crossProduct(vec).normalize();
		Vec3d down = up.negate().normalize();
		
		Vec3d rightup = right.add(up).normalize();
		Vec3d rightdown = right.add(down).normalize();
		Vec3d leftup = left.add(up).normalize();
		Vec3d leftdown = left.add(down).normalize();
		
		Vec3d[] array = new Vec3d[10];
		
		array[1] = vec.add(right).normalize();
		array[2] = vec.add(left).normalize();
		array[3] = vec.add(up).normalize();
		array[4] = vec.add(down).normalize();
		array[5] = vec.add(rightdown).normalize();
		array[6] = vec.add(rightup).normalize();
		array[7] = vec.add(leftdown).normalize();
		array[8] = vec.add(leftup).normalize();
		
		for(int i = 1; i <= 8; i++) {
			LExplosiveProjectile dynamite = EntityRegistry.DYNAMITE.get().create(ent.getLevel());
			dynamite.setOwner(ent.owner());
			dynamite.setPosition(ent.getPos());
			dynamite.setVelocity(array[i].multiply(2D));
			ent.getLevel().spawnEntity(dynamite);
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

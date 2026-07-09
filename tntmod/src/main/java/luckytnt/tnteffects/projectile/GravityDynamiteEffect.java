package luckytnt.tnteffects.projectile;

import java.util.List;

import luckytnt.registry.ItemRegistry;
import luckytntlib.util.IExplosiveEntity;
import luckytntlib.util.tnteffects.PrimedTNTEffect;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;

public class GravityDynamiteEffect extends PrimedTNTEffect{

	@Override
	public void explosionTick(IExplosiveEntity entity) {
		((Entity)entity).setVelocity(((Entity)entity).getVelocity().add(0f, 0.08f, 0f));
		List<Entity> ents = entity.getLevel().getOtherEntities((Entity)entity, new Box(entity.getPos().add(-10f, -10f, -10f), entity.getPos().add(10f, 10f, 10f)));
		for(Entity ent : ents) {
			if(!ent.equals(entity.owner()) && !(ent instanceof IExplosiveEntity)) {
				Vec3d direction = entity.getPos().subtract(ent.getLerpedPos(1f)).normalize();
				ent.setVelocity(direction.multiply(1.5f));
			}
		}
	}
	
	@Override
	public boolean explodesOnImpact() {
		return false;
	}
	
	@Override
	public boolean airFuse() {
		return true;
	}
	
	@Override
	public Item getItem() {
		return ItemRegistry.GRAVITY_DYNAMITE.get();
	}
	
	@Override
	public int getDefaultFuse(IExplosiveEntity entity) {
		return 60;
	}
}

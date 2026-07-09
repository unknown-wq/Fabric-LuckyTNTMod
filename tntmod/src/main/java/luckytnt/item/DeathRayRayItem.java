package luckytnt.item;

import org.jetbrains.annotations.Nullable;

import luckytnt.registry.EntityRegistry;
import luckytntlib.entity.LExplosiveProjectile;
import luckytntlib.item.LDynamiteItem;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class DeathRayRayItem extends LDynamiteItem{

	public DeathRayRayItem() {
		super(new Item.Settings(), EntityRegistry.DEATH_RAY_RAY);
	}
	
	@Override
	public LExplosiveProjectile shoot(World level, double x, double y, double z, Vec3d direction, float power, @Nullable LivingEntity thrower){
		LExplosiveProjectile dyn = dynamite.get().create(level);
		dyn.setPosition(x, y, z);
		dyn.setVelocity(direction.x, direction.y, direction.z, 4, 0);
		dyn.setOwner(thrower);
		level.spawnEntity(dyn);
		level.playSound(null, new BlockPos(MathHelper.floor(x), MathHelper.floor(y), MathHelper.floor(z)), SoundEvents.ENTITY_GENERIC_EXPLODE.value(), SoundCategory.MASTER, 1, 0.5f);
		return dyn;
	}
}

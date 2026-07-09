package luckytnt.item;

import org.jetbrains.annotations.Nullable;

import luckytnt.registry.EntityRegistry;
import luckytntlib.entity.LExplosiveProjectile;
import luckytntlib.item.LDynamiteItem;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.sounds.SoundSource;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.Level;

public class DeathRayRayItem extends LDynamiteItem{

	public DeathRayRayItem() {
		super(new Item.Properties(), EntityRegistry.DEATH_RAY_RAY);
	}
	
	@Override
	public LExplosiveProjectile shoot(Level level, double x, double y, double z, Vec3 direction, float power, @Nullable LivingEntity thrower){
		LExplosiveProjectile dyn = dynamite.get().create(level);
		dyn.setPosition(x, y, z);
		dyn.setDeltaMovement(direction.x, direction.y, direction.z, 4, 0);
		dyn.setOwner(thrower);
		level.addFreshEntity(dyn);
		level.playSound(null, new BlockPos(Mth.floor(x), Mth.floor(y), Mth.floor(z)), SoundEvents.ENTITY_GENERIC_EXPLODE.value(), SoundSource.MASTER, 1, 0.5f);
		return dyn;
	}
}

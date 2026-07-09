package luckytnt.tnteffects.projectile;

import luckytnt.registry.EntityRegistry;
import luckytnt.registry.ItemRegistry;
import luckytntlib.entity.LExplosiveProjectile;
import luckytntlib.util.IExplosiveEntity;
import luckytntlib.util.tnteffects.PrimedTNTEffect;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.MathHelper;

public class SpiralDynamiteEffect extends PrimedTNTEffect{

	@Override
	public void explosionTick(IExplosiveEntity entity) {
		Entity ent = (Entity)entity;
		ent.setVelocity(ent.getVelocity().add(0f, 0.08f, 0f));
		if(entity.getTNTFuse() < 30) {
			if(entity.getTNTFuse() % 3 == 0) {
				ent.setPitch(180f);
				ent.setYaw(entity.getPersistentData().getFloat("angle") + 30f);
				NbtCompound tag = entity.getPersistentData();
				tag.putFloat("angle", ent.getYaw());
				tag.putFloat("spiral_power", MathHelper.clamp(entity.getPersistentData().getFloat("spiral_power") + 0.12f, 0.2f, Float.MAX_VALUE));
				entity.setPersistentData(tag);
				LExplosiveProjectile spiral_tnt = EntityRegistry.SPIRAL_PROJECTILE.get().create(entity.getLevel());
				spiral_tnt.setPosition(entity.x(), entity.y(), entity.z());
				spiral_tnt.setOwner(entity.owner());
				spiral_tnt.setVelocity(ent.getRotationVector().x, ent.getRotationVector().y, ent.getRotationVector().z, entity.getPersistentData().getFloat("spiral_power"), 0);
				entity.getLevel().playSound(null, toBlockPos(entity.getPos()), SoundEvents.BLOCK_DISPENSER_LAUNCH, SoundCategory.MASTER, 3, 1);
				entity.getLevel().spawnEntity(spiral_tnt);
			}
		}
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
	
	@Override
	public Item getItem() {
		return ItemRegistry.SPIRAL_DYNAMITE.get();
	}
	
	@Override
	public int getDefaultFuse(IExplosiveEntity entity) {
		return 60;
	}
}

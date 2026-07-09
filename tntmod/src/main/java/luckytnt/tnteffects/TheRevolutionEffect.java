package luckytnt.tnteffects;

import luckytnt.registry.BlockRegistry;
import luckytnt.registry.EntityRegistry;
import luckytntlib.entity.PrimedLTNT;
import luckytntlib.util.IExplosiveEntity;
import luckytntlib.util.tnteffects.PrimedTNTEffect;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.MathHelper;

public class TheRevolutionEffect extends PrimedTNTEffect {
	
	@Override
	public void explosionTick(IExplosiveEntity entity) {
		if(entity instanceof PrimedLTNT) {
			Entity ent = (Entity)entity;
			ent.setVelocity(ent.getVelocity().x, 0.15f, ent.getVelocity().z);
			if(entity.getTNTFuse() < 60) {
				if(entity.getTNTFuse() % 6 == 0) {
					NbtCompound tag = entity.getPersistentData();
					tag.putFloat("spiral_power", MathHelper.clamp(entity.getPersistentData().getFloat("spiral_power") + 0.15f, 0.15f, Float.MAX_VALUE));
					entity.setPersistentData(tag);
					PrimedLTNT spiral_tnt = EntityRegistry.SPIRAL_TNT.get().create(entity.getLevel());
					spiral_tnt.setTNTFuse(140);
					spiral_tnt.setPosition(entity.x(), entity.y(), entity.z());
					spiral_tnt.setOwner(entity.owner());
					spiral_tnt.setVelocity(ent.getRotationVector().normalize().multiply((double)entity.getPersistentData().getFloat("spiral_power")));
					entity.getLevel().playSound(null, toBlockPos(entity.getPos()), SoundEvents.BLOCK_DISPENSER_LAUNCH, SoundCategory.MASTER, 3, 1);
					entity.getLevel().spawnEntity(spiral_tnt);
					ent.setYaw(ent.getYaw() + 60f);
				}
			}
		}
	}
	
	@Override
	public Block getBlock() {
		return BlockRegistry.THE_REVOLUTION.get();
	}
	
	@Override
	public int getDefaultFuse(IExplosiveEntity entity) {
		return 140;
	}
}

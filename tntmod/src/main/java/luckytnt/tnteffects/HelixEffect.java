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
import net.minecraft.util.math.Vec3d;

public class HelixEffect extends PrimedTNTEffect {

	@Override
	public void explosionTick(IExplosiveEntity ent) {
		((Entity)ent).setVelocity(((Entity)ent).getVelocity().x, 0.2f, ((Entity)ent).getVelocity().z);
		if(ent.getTNTFuse() == 140) {
			NbtCompound tag = ent.getPersistentData();
			tag.putFloat("power", 0.35f);
			ent.setPersistentData(tag);
		}
		if(ent.getTNTFuse() < 60) {
			if(ent.getTNTFuse() % 6 == 0) {
				PrimedLTNT spiral = EntityRegistry.THE_REVOLUTION.get().create(ent.getLevel());
				spiral.setPosition(ent.getPos());
				spiral.setOwner(ent.owner());
				spiral.setTNTFuse(140);
				spiral.setVelocity(new Vec3d(((Entity)ent).getRotationVector().x, ((Entity)ent).getRotationVector().y, ((Entity)ent).getRotationVector().z).normalize().multiply(ent.getPersistentData().getFloat("power")));
				ent.getLevel().spawnEntity(spiral);
				ent.getLevel().playSound(null, toBlockPos(ent.getPos()), SoundEvents.BLOCK_DISPENSER_LAUNCH, SoundCategory.MASTER, 3, 1);
				NbtCompound tag = ent.getPersistentData();
				tag.putFloat("power", ent.getPersistentData().getFloat("power") + 0.35f);
				ent.setPersistentData(tag);
				((Entity)ent).setYaw(((Entity)ent).getYaw() + 60);
			}
		}
	}
	
	@Override
	public Block getBlock() {
		return BlockRegistry.HELIX.get();
	}
	
	@Override
	public int getDefaultFuse(IExplosiveEntity ent) {
		return 140;
	}
}

package luckytnt.tnteffects;

import luckytnt.registry.BlockRegistry;
import luckytnt.registry.EntityRegistry;
import luckytntlib.entity.PrimedLTNT;
import luckytntlib.util.IExplosiveEntity;
import luckytntlib.util.tnteffects.PrimedTNTEffect;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.entity.Entity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundSource;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.phys.Vec3;

public class HelixEffect extends PrimedTNTEffect {

	@Override
	public void explosionTick(IExplosiveEntity ent) {
		((Entity)ent).setDeltaMovement(((Entity)ent).getDeltaMovement().x, 0.2f, ((Entity)ent).getDeltaMovement().z);
		if(ent.getTNTFuse() == 140) {
			CompoundTag tag = ent.getPersistentData();
			tag.putFloat("power", 0.35f);
			ent.setPersistentData(tag);
		}
		if(ent.getTNTFuse() < 60) {
			if(ent.getTNTFuse() % 6 == 0) {
				PrimedLTNT spiral = EntityRegistry.THE_REVOLUTION.get().create(ent.getLevel());
				spiral.setPosition(ent.getPos());
				spiral.setOwner(ent.owner());
				spiral.setTNTFuse(140);
				spiral.setDeltaMovement(new Vec3(((Entity)ent).getRotationVector().x, ((Entity)ent).getRotationVector().y, ((Entity)ent).getRotationVector().z).normalize().multiply(ent.getPersistentData().getFloat("power")));
				ent.getLevel().addFreshEntity(spiral);
				ent.getLevel().playSound(null, toBlockPos(ent.getPos()), SoundEvents.BLOCK_DISPENSER_LAUNCH, SoundSource.MASTER, 3, 1);
				CompoundTag tag = ent.getPersistentData();
				tag.putFloat("power", ent.getPersistentData().getFloat("power") + 0.35f);
				ent.setPersistentData(tag);
				((Entity)ent).setYRot(((Entity)ent).getYRot() + 60);
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

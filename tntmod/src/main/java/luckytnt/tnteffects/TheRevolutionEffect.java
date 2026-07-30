package luckytnt.tnteffects;

import net.minecraft.world.entity.EntitySpawnReason;
import luckytnt.registry.BlockRegistry;
import luckytnt.registry.EntityRegistry;
import luckytntlib.entity.PrimedLTNT;
import luckytntlib.util.IExplosiveEntity;
import luckytntlib.util.tnteffects.PrimedTNTEffect;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.entity.Entity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;

public class TheRevolutionEffect extends PrimedTNTEffect {
	
	@Override
	public void explosionTick(IExplosiveEntity entity) {
		if(entity instanceof PrimedLTNT) {
			Entity ent = (Entity)entity;
			ent.setDeltaMovement(ent.getDeltaMovement().x, 0.15f, ent.getDeltaMovement().z);
			// explosionTick runs on both logical sides and addFreshEntity is a no-op on the client, so the
			// spiral TNT was built and discarded there too. The delta movement above stays unguarded so
			// the client keeps predicting the rise.
			if(entity.getTNTFuse() < 60 && entity.getLevel() instanceof ServerLevel) {
				if(entity.getTNTFuse() % 6 == 0) {
					CompoundTag tag = entity.getPersistentData();
					tag.putFloat("spiral_power", Mth.clamp(entity.getPersistentData().getFloatOr("spiral_power", 0f) + 0.15f, 0.15f, Float.MAX_VALUE));
					entity.setPersistentData(tag);
					PrimedLTNT spiral_tnt = EntityRegistry.SPIRAL_TNT.get().create(entity.getLevel(), EntitySpawnReason.MOB_SUMMONED);
					spiral_tnt.setTNTFuse(140);
					spiral_tnt.setPos(entity.x(), entity.y(), entity.z());
					spiral_tnt.setOwner(entity.owner());
					spiral_tnt.setDeltaMovement(ent.getLookAngle().normalize().scale((double)entity.getPersistentData().getFloatOr("spiral_power", 0f)));
					entity.getLevel().playSound(null, toBlockPos(entity.getPos()), SoundEvents.DISPENSER_LAUNCH, SoundSource.MASTER, 3, 1);
					entity.getLevel().addFreshEntity(spiral_tnt);
					ent.setYRot(ent.getYRot() + 60f);
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

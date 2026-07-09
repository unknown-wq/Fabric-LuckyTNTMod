package luckytnt.tnteffects;

import luckytnt.registry.BlockRegistry;
import luckytntlib.util.IExplosiveEntity;
import luckytntlib.util.explosions.ImprovedExplosion;
import luckytntlib.util.tnteffects.PrimedTNTEffect;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.entity.Entity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.sounds.SoundEvents;

public class BouncingTNTEffect extends PrimedTNTEffect{

	@Override
	public void serverExplosion(IExplosiveEntity entity) {
		ImprovedExplosion explosion = new ImprovedExplosion(entity.getLevel(), (Entity)entity, entity.getPos(), 10);
		explosion.doEntityExplosion(1.25f, true);
		explosion.doBlockExplosion(1f, 1f, 1f, 1.25f, false, false);
	}
	
	@Override
	public void explosionTick(IExplosiveEntity entity) {
		if(((Entity)entity).onGround()) {
			CompoundTag tag = entity.getPersistentData();
			tag.putInt("bounces", entity.getPersistentData().getIntOr("bounces", 0) + 1);
			entity.setPersistentData(tag);
			((Entity)entity).setDeltaMovement(Math.random() - Math.random(), Math.random() * 1.5f, Math.random() - Math.random());
			entity.getLevel().playSound(null, entity.x(), entity.y(), entity.z(), SoundEvents.SLIME_JUMP, SoundSource.MASTER, 1, 1);
		}
		if(entity.getPersistentData().getIntOr("bounces", 0) >= 12) {
			entity.getLevel().playSound((Entity)entity, toBlockPos(entity.getPos()), SoundEvents.GENERIC_EXPLODE.value(), SoundSource.BLOCKS, 4f, (1f + (entity.getLevel().getRandom().nextFloat() - entity.getLevel().getRandom().nextFloat()) * 0.2f) * 0.7f);
			if(entity.getLevel() instanceof ServerLevel) {
				serverExplosion(entity);
				entity.destroy();
			}
		}
	}
	
	@Override
	public Block getBlock() {
		return BlockRegistry.BOUNCING_TNT.get();
	}
	
	@Override
	public int getDefaultFuse(IExplosiveEntity entity) {
		return 100000;
	}
}

package luckytnt.tnteffects;

import luckytnt.registry.BlockRegistry;
import luckytntlib.util.IExplosiveEntity;
import luckytntlib.util.explosions.ImprovedExplosion;
import luckytntlib.util.tnteffects.PrimedTNTEffect;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;

public class LeapingTNTEffect extends PrimedTNTEffect{

	@Override
	public void serverExplosion(IExplosiveEntity entity) {
		if(entity.getPersistentData().getInt("bounces") < 24) {
			ImprovedExplosion explosion = new ImprovedExplosion(entity.getLevel(), (Entity)entity, entity.getPos(), 10);
			explosion.doEntityExplosion(1.5f, true);
			explosion.doBlockExplosion(1f, 1f, 1f, 1.25f, false, false);
		} else {
			ImprovedExplosion explosion = new ImprovedExplosion(entity.getLevel(), (Entity)entity, entity.getPos(), 20);
			explosion.doEntityExplosion(2f, true);
			explosion.doBlockExplosion(1f, 1f, 1f, 1.5f, false, false);
		}
	}
	
	@Override
	public void explosionTick(IExplosiveEntity entity) {
		if(((Entity)entity).isOnGround()) {
			NbtCompound tag = entity.getPersistentData();
			tag.putInt("bounces", entity.getPersistentData().getInt("bounces") + 1);
			entity.setPersistentData(tag);
			((Entity)entity).setVelocity(Math.random() * 1.5D - Math.random() * 1.5D, Math.random() * 2f, Math.random() * 1.5D - Math.random() * 1.5D);
			entity.getLevel().playSound(null, entity.x(), entity.y(), entity.z(), SoundEvents.ENTITY_SLIME_JUMP, SoundCategory.MASTER, 1, 1);
			entity.getLevel().playSound((Entity)entity, toBlockPos(entity.getPos()), SoundEvents.ENTITY_GENERIC_EXPLODE.value(), SoundCategory.BLOCKS, 4f, (1f + (entity.getLevel().getRandom().nextFloat() - entity.getLevel().getRandom().nextFloat()) * 0.2f) * 0.7f);
			
			if(entity.getPersistentData().getInt("bounces") >= 24) {
				if(entity.getLevel() instanceof ServerWorld) {
					serverExplosion(entity);
				}
				entity.destroy();
			}
			if(entity.getPersistentData().getInt("bounces") >= 1 && entity.getPersistentData().getInt("bounces") < 24 && entity.getLevel() instanceof ServerWorld) {
				serverExplosion(entity);
			}
		}
	}
	
	@Override
	public Block getBlock() {
		return BlockRegistry.LEAPING_TNT.get();
	}
	
	@Override
	public int getDefaultFuse(IExplosiveEntity entity) {
		return 100000;
	}
}

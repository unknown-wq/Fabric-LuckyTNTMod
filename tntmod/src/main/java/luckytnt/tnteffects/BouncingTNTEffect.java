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

public class BouncingTNTEffect extends PrimedTNTEffect{

	@Override
	public void serverExplosion(IExplosiveEntity entity) {
		ImprovedExplosion explosion = new ImprovedExplosion(entity.getLevel(), (Entity)entity, entity.getPos(), 10);
		explosion.doEntityExplosion(1.25f, true);
		explosion.doBlockExplosion(1f, 1f, 1f, 1.25f, false, false);
	}
	
	@Override
	public void explosionTick(IExplosiveEntity entity) {
		if(((Entity)entity).isOnGround()) {
			NbtCompound tag = entity.getPersistentData();
			tag.putInt("bounces", entity.getPersistentData().getInt("bounces") + 1);
			entity.setPersistentData(tag);
			((Entity)entity).setVelocity(Math.random() - Math.random(), Math.random() * 1.5f, Math.random() - Math.random());
			entity.getLevel().playSound(null, entity.x(), entity.y(), entity.z(), SoundEvents.ENTITY_SLIME_JUMP, SoundCategory.MASTER, 1, 1);
		}
		if(entity.getPersistentData().getInt("bounces") >= 12) {
			entity.getLevel().playSound((Entity)entity, toBlockPos(entity.getPos()), SoundEvents.ENTITY_GENERIC_EXPLODE.value(), SoundCategory.BLOCKS, 4f, (1f + (entity.getLevel().getRandom().nextFloat() - entity.getLevel().getRandom().nextFloat()) * 0.2f) * 0.7f);
			if(entity.getLevel() instanceof ServerWorld) {
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

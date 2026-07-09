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

public class JumpingTNTEffect extends PrimedTNTEffect{

	@Override
	public void serverExplosion(IExplosiveEntity ent) {
		PrimedLTNT tnt = EntityRegistry.LEAPING_TNT.get().create(ent.getLevel(), EntitySpawnReason.MOB_SUMMONED);
		tnt.setPos(ent.getPos());
		tnt.setOwner(ent.owner());
		tnt.setTNTFuse(1000000);
		ent.getLevel().addFreshEntity(tnt);
		EntityRegistry.TNT_X20_EFFECT.build().serverExplosion(ent);
	}
	
	@Override
	public void explosionTick(IExplosiveEntity entity) {
		if(((Entity)entity).onGround()) {
			CompoundTag tag = entity.getPersistentData();
			tag.putInt("bounces", entity.getPersistentData().getIntOr("bounces", 0) + 1);
			entity.setPersistentData(tag);
			((Entity)entity).setDeltaMovement(Math.random() * 2D - Math.random() * 2D, Math.random() * 3f, Math.random() * 2D - Math.random() * 2D);
			entity.getLevel().playSound(null, entity.x(), entity.y(), entity.z(), SoundEvents.ENTITY_SLIME_JUMP, SoundSource.MASTER, 1, 1);
			entity.getLevel().playSound((Entity)entity, toBlockPos(entity.getPos()), SoundEvents.GENERIC_EXPLODE.value(), SoundSource.BLOCKS, 4f, (1f + (entity.getLevel().getRandom().nextFloat() - entity.getLevel().getRandom().nextFloat()) * 0.2f) * 0.7f);
			
			if(entity.getPersistentData().getIntOr("bounces", 0) >= 10) {
				if(entity.getLevel() instanceof ServerLevel) {
					serverExplosion(entity);
				}
				entity.destroy();
			}
			if(entity.getPersistentData().getIntOr("bounces", 0) >= 1 && entity.getLevel() instanceof ServerLevel) {
				serverExplosion(entity);
			}
		}
	}
	
	@Override
	public Block getBlock() {
		return BlockRegistry.JUMPING_TNT.get();
	}
	
	@Override
	public int getDefaultFuse(IExplosiveEntity entity) {
		return 100000;
	}
}

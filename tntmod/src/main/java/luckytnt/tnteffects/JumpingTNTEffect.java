package luckytnt.tnteffects;

import luckytnt.registry.BlockRegistry;
import luckytnt.registry.EntityRegistry;
import luckytntlib.entity.PrimedLTNT;
import luckytntlib.util.IExplosiveEntity;
import luckytntlib.util.tnteffects.PrimedTNTEffect;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;

public class JumpingTNTEffect extends PrimedTNTEffect{

	@Override
	public void serverExplosion(IExplosiveEntity ent) {
		PrimedLTNT tnt = EntityRegistry.LEAPING_TNT.get().create(ent.getLevel());
		tnt.setPosition(ent.getPos());
		tnt.setOwner(ent.owner());
		tnt.setTNTFuse(1000000);
		ent.getLevel().spawnEntity(tnt);
		EntityRegistry.TNT_X20_EFFECT.build().serverExplosion(ent);
	}
	
	@Override
	public void explosionTick(IExplosiveEntity entity) {
		if(((Entity)entity).isOnGround()) {
			NbtCompound tag = entity.getPersistentData();
			tag.putInt("bounces", entity.getPersistentData().getInt("bounces") + 1);
			entity.setPersistentData(tag);
			((Entity)entity).setVelocity(Math.random() * 2D - Math.random() * 2D, Math.random() * 3f, Math.random() * 2D - Math.random() * 2D);
			entity.getLevel().playSound(null, entity.x(), entity.y(), entity.z(), SoundEvents.ENTITY_SLIME_JUMP, SoundCategory.MASTER, 1, 1);
			entity.getLevel().playSound((Entity)entity, toBlockPos(entity.getPos()), SoundEvents.ENTITY_GENERIC_EXPLODE.value(), SoundCategory.BLOCKS, 4f, (1f + (entity.getLevel().getRandom().nextFloat() - entity.getLevel().getRandom().nextFloat()) * 0.2f) * 0.7f);
			
			if(entity.getPersistentData().getInt("bounces") >= 10) {
				if(entity.getLevel() instanceof ServerWorld) {
					serverExplosion(entity);
				}
				entity.destroy();
			}
			if(entity.getPersistentData().getInt("bounces") >= 1 && entity.getLevel() instanceof ServerWorld) {
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

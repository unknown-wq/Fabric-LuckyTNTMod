package luckytnt.tnteffects;

import java.util.List;

import luckytnt.registry.BlockRegistry;
import luckytntlib.util.IExplosiveEntity;
import luckytntlib.util.explosions.ImprovedExplosion;
import luckytntlib.util.tnteffects.PrimedTNTEffect;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.phys.AABB;
import net.minecraft.util.Mth;

public class EatingTNTEffect extends PrimedTNTEffect{

	@Override
	public void serverExplosion(IExplosiveEntity entity) {
		ImprovedExplosion explosion = new ImprovedExplosion(entity.getLevel(), (Entity)entity, entity.getPos(), 10 + Mth.floor((1f/7.5f) * entity.getPersistentData().getInt("eatLevel")));
		explosion.doEntityExplosion(1.5f + entity.getPersistentData().getInt("eatLevel") / 300f, true);
		explosion.doBlockExplosion();
	}
	
	@Override
	public void explosionTick(IExplosiveEntity entity) {
		if(entity.getPersistentData().getInt("eatLevel") < 300) {
			List<ItemEntity> items = entity.getLevel().getNonSpectatingEntities(ItemEntity.class, new Box(entity.getPos().add(-10, -10, -10), entity.getPos().add(10, 10, 10)));
			for(ItemEntity item : items) {
				item.setDeltaMovement(entity.getPos().add(item.getLerpedPos(1).multiply(-1)).normalize());
				if(entity.getPos().distanceTo(item.getLerpedPos(1)) < 1) {
					CompoundTag tag = entity.getPersistentData();
					tag.putInt("eatLevel", Mth.clamp(entity.getPersistentData().getInt("eatLevel") + item.getStack().getCount(), 0, 300));
					entity.setPersistentData(tag);
					item.discard();
				}
			}
		}
	}
	
	@Override
	public Block getBlock() {
		return BlockRegistry.EATING_TNT.get();
	}
	
	@Override
	public float getSize(IExplosiveEntity entity) {
		return 1f + entity.getPersistentData().getInt("eatLevel") / 300f;
	}
		
	@Override
	public int getDefaultFuse(IExplosiveEntity entity) {
		return 400;
	}
}

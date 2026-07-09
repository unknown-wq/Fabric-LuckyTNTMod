package luckytnt.tnteffects;

import java.util.List;

import luckytnt.registry.BlockRegistry;
import luckytntlib.util.IExplosiveEntity;
import luckytntlib.util.explosions.ImprovedExplosion;
import luckytntlib.util.tnteffects.PrimedTNTEffect;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;

public class EatingTNTEffect extends PrimedTNTEffect{

	@Override
	public void serverExplosion(IExplosiveEntity entity) {
		ImprovedExplosion explosion = new ImprovedExplosion(entity.getLevel(), (Entity)entity, entity.getPos(), 10 + MathHelper.floor((1f/7.5f) * entity.getPersistentData().getInt("eatLevel")));
		explosion.doEntityExplosion(1.5f + entity.getPersistentData().getInt("eatLevel") / 300f, true);
		explosion.doBlockExplosion();
	}
	
	@Override
	public void explosionTick(IExplosiveEntity entity) {
		if(entity.getPersistentData().getInt("eatLevel") < 300) {
			List<ItemEntity> items = entity.getLevel().getNonSpectatingEntities(ItemEntity.class, new Box(entity.getPos().add(-10, -10, -10), entity.getPos().add(10, 10, 10)));
			for(ItemEntity item : items) {
				item.setVelocity(entity.getPos().add(item.getLerpedPos(1).multiply(-1)).normalize());
				if(entity.getPos().distanceTo(item.getLerpedPos(1)) < 1) {
					NbtCompound tag = entity.getPersistentData();
					tag.putInt("eatLevel", MathHelper.clamp(entity.getPersistentData().getInt("eatLevel") + item.getStack().getCount(), 0, 300));
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

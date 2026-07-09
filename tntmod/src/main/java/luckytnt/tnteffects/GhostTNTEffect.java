package luckytnt.tnteffects;

import luckytntlib.util.IExplosiveEntity;
import luckytntlib.util.explosions.ImprovedExplosion;
import luckytntlib.util.tnteffects.PrimedTNTEffect;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.TargetPredicate;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Box;

public class GhostTNTEffect extends PrimedTNTEffect {

	@Override
	public void explosionTick(IExplosiveEntity ent) {
		if(ent.getTNTFuse() == 60) {
			PlayerEntity player = ent.getLevel().getClosestPlayer((Entity)ent, 100);
			if(player != null && player != ent.owner()) {
				((Entity)ent).setPosition(player.getX(), player.getY(), player.getZ());
			} else {
				LivingEntity entity = ent.getLevel().getClosestEntity(ent.getLevel().getNonSpectatingEntities(LivingEntity.class, new Box(ent.x() - 100, ent.y() - 100, ent.z() - 100, ent.x() + 100, ent.y() + 100, ent.z() + 100)), TargetPredicate.createNonAttackable().setBaseMaxDistance(5).ignoreVisibility().ignoreDistanceScalingFactor(), null, ent.x(), ent.y(), ent.z());
				if(entity != null && entity != ent.owner()) {
					((Entity)ent).setPosition(entity.getX(), entity.getY(), entity.getZ());
				}
			}
		}
	}
	
	@Override
	public void serverExplosion(IExplosiveEntity ent) {
		ImprovedExplosion explosion = new ImprovedExplosion(ent.getLevel(), (Entity)ent, ent.getPos(), 30);
		explosion.doEntityExplosion(1.5f, true);
		explosion.doBlockExplosion(1f, 1.1f, 1f, 1.2f, false, false);
	}
	
	@Override
	public void spawnParticles(IExplosiveEntity ent) {
		
	}
	
	@Override
	public Block getBlock() {
		return Blocks.AIR;
	}
	
	@Override
	public int getDefaultFuse(IExplosiveEntity ent) {
		return 150;
	}
}

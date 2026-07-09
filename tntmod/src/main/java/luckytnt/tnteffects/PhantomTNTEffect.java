package luckytnt.tnteffects;

import luckytntlib.util.IExplosiveEntity;
import luckytntlib.util.explosions.ImprovedExplosion;
import luckytntlib.util.tnteffects.PrimedTNTEffect;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;

public class PhantomTNTEffect extends PrimedTNTEffect{
	
	@Override
	public void serverExplosion(IExplosiveEntity entity) {
		ImprovedExplosion explosion = new ImprovedExplosion(entity.getLevel(), (Entity)entity, entity.getPos(), 20);
		explosion.doEntityExplosion(2f, true);
		explosion.doBlockExplosion(1f, 1f, 1f, 1.5f, false, false);
	}
	
	public void explosionTick(IExplosiveEntity entity) {
		if(entity.getTNTFuse() == 5) {
			double offX = Math.random() * 90 - 45;
			double offZ = Math.random() * 90 - 45;
			boolean foundBlock = false;
			for(int offY = 320; offY > -64; offY--) {
	      		BlockPos pos = new BlockPos(MathHelper.floor(entity.x() + offX), offY, MathHelper.floor(entity.z() + offZ));
	      		BlockState state = entity.getLevel().getBlockState(pos);
	      		if(state.isFullCube(entity.getLevel(), pos) && !state.isAir() && !foundBlock) {
	      			((Entity)entity).setPosition(entity.x() + offX, offY + 1, entity.z() + offZ);
	      			foundBlock = true;
	      		}
			}
		}
	}
	
	@Override
	public void spawnParticles(IExplosiveEntity entity) {		
	}
	
	@Override
	public Block getBlock() {
		return Blocks.AIR;
	}
}

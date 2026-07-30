package luckytnt.tnteffects;

import luckytntlib.util.IExplosiveEntity;
import luckytntlib.util.explosions.ImprovedExplosion;
import luckytntlib.util.tnteffects.PrimedTNTEffect;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.entity.Entity;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;

public class PhantomTNTEffect extends PrimedTNTEffect{
	
	@Override
	public void serverExplosion(IExplosiveEntity entity) {
		ImprovedExplosion explosion = new ImprovedExplosion(entity.getLevel(), (Entity)entity, entity.getPos(), 20);
		explosion.doEntityExplosion(2f, true);
		explosion.doBlockExplosion(1f, 1f, 1f, 1.5f, false, false);
	}
	
	public void explosionTick(IExplosiveEntity entity) {
		// explosionTick runs on both logical sides; the teleport is server authoritative and gets synced,
		// so the client used to run the whole 384 step column scan for a position it then had overwritten.
		if(!(entity.getLevel() instanceof ServerLevel level) || entity.getTNTFuse() != 5) {
			return;
		}
		double offX = Math.random() * 90 - 45;
		double offZ = Math.random() * 90 - 45;
		// "foundBlock" only suppressed further assignments, so the scan always ran all 384 steps with a
		// fresh BlockPos and a getBlockState each. Breaking at the hit stops at the first surface, and the
		// position is reused instead of reallocated: 384 reads + 384 allocations -> ~250 reads + 1.
		final BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
		final int blockX = Mth.floor(entity.x() + offX);
		final int blockZ = Mth.floor(entity.z() + offZ);
		for(int offY = 320; offY > -64; offY--) {
			pos.set(blockX, offY, blockZ);
			BlockState state = level.getBlockState(pos);
			if(state.isCollisionShapeFullBlock(level, pos) && !state.isAir()) {
				((Entity)entity).setPos(entity.x() + offX, offY + 1, entity.z() + offZ);
				break;
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

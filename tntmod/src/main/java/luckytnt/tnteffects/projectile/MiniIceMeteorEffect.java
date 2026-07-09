package luckytnt.tnteffects.projectile;

import luckytntlib.util.IExplosiveEntity;
import luckytntlib.util.explosions.ImprovedExplosion;
import luckytntlib.util.tnteffects.PrimedTNTEffect;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.math.BlockPos;

public class MiniIceMeteorEffect extends PrimedTNTEffect {

	@Override
	public void serverExplosion(IExplosiveEntity ent) {
		ImprovedExplosion explosion = new ImprovedExplosion(ent.getLevel(), (Entity)ent, ent.getPos(), 7);
		explosion.doEntityExplosion(1f, true);
		explosion.doBlockExplosion(1f, 1f, 1f, 1.25f, false, false);
		
		for (BlockPos pos : explosion.getAffectedBlocks()) {
			if (Math.random() > 0.75f && ent.getLevel().getBlockState(pos).isAir() && ent.getLevel().getBlockState(pos.down()).isOpaqueFullCube(ent.getLevel(), pos)) {
				ent.getLevel().setBlockState(pos, Math.random() < 0.5f ? Blocks.BLUE_ICE.getDefaultState() : Blocks.PACKED_ICE.getDefaultState());
			}
		}
	}
	
	@Override
	public void spawnParticles(IExplosiveEntity entity) {
		entity.getLevel().addParticle(ParticleTypes.ITEM_SNOWBALL, entity.x(), entity.y() + 1D, entity.z(), 0, 0, 0);
	}

	@Override
	public float getSize(IExplosiveEntity entity) {
		return 1f;
	}

	@Override
	public Block getBlock() {
		return Blocks.PACKED_ICE;
	}
}

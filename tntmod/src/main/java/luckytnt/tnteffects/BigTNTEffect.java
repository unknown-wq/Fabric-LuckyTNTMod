package luckytnt.tnteffects;

import luckytnt.registry.BlockRegistry;
import luckytntlib.entity.LTNTMinecart;
import luckytntlib.util.IExplosiveEntity;
import luckytntlib.util.explosions.ImprovedExplosion;
import luckytntlib.util.tnteffects.PrimedTNTEffect;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.Entity;
import net.minecraft.core.particles.ParticleTypes;

public class BigTNTEffect extends PrimedTNTEffect{

	@Override
	public void serverExplosion(IExplosiveEntity entity) {
		ImprovedExplosion explosion = new ImprovedExplosion(entity.getLevel(), (Entity)entity, entity.getPos(), 15);
		explosion.doEntityExplosion(2f, true);
		//same parameters as the no-arg doBlockExplosion, but the affected positions are not copied into
		//ImprovedExplosion#affectedBlocks: this explosion is discarded right away and never reads them back
		explosion.doBlockExplosion(1f, 1f, 1f, 1f, false, false, false);
	}
	
	@Override
	public void spawnParticles(IExplosiveEntity entity) {
		entity.getLevel().addParticle(ParticleTypes.SMOKE, entity.x(), entity.y() + 2f, entity.z(), 0, 0, 0);
	}
	
	@Override
	public BlockState getBlockState(IExplosiveEntity ent) {
		if(ent instanceof LTNTMinecart) {
			if(ent.getTNTFuse() > -1) {
				return BlockRegistry.TNT.get().defaultBlockState();
			} else {
				return BlockRegistry.BIG_TNT.get().defaultBlockState();
			}
		} else {
			return BlockRegistry.TNT.get().defaultBlockState();
		}
	}
	
	@Override
	public float getSize(IExplosiveEntity ent) {
		if(ent instanceof LTNTMinecart) {
			if(ent.getTNTFuse() > -1) {
				return 2f;
			} else {
				return 1f;
			}
		} else {
			return 2f;
		}
	}
	
	@Override
	public int getDefaultFuse(IExplosiveEntity entity) {
		return 120;
	}
}

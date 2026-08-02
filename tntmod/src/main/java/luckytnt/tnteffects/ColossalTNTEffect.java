package luckytnt.tnteffects;

import luckytntlib.util.IExplosiveEntity;
import luckytntlib.util.tnteffects.PrimedTNTEffect;
import luckytntlib.util.tnteffects.TNTXStrengthEffect.SectionSkippingExplosion;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.entity.Entity;
import net.minecraft.core.particles.ParticleTypes;

public class ColossalTNTEffect extends PrimedTNTEffect {

	@Override
	public void serverExplosion(IExplosiveEntity ent) {
		//The whole 6601 ms of this detonation is the ray walk of the r=190 explosion: 4*PI*190² rays of
		//190*0.715/0.225 steps, 2.7e8 iterations. SectionSkippingExplosion walks the identical rays but
		//jumps over all-air chunk sections and over the open sky above a chunk's WORLD_SURFACE, which is
		//16x fewer iterations at this radius on flat ground. The affected positions are never read back,
		//so they are not copied into the explosion either (saveBlockPos = false).
		SectionSkippingExplosion explosion = new SectionSkippingExplosion(ent.getLevel(), (Entity)ent, ent.getPos(), 190);
		explosion.doEntityExplosion(20f, true);
		explosion.doBlockExplosion(1f, 1f, 0.167f, 0.05f, false, true, false);
	}
	
	@Override
	public void spawnParticles(IExplosiveEntity ent) {
		ent.getLevel().addParticle(ParticleTypes.SMOKE, ent.x(), ent.y() + 19.5f, ent.z(), 0, 0, 0);
	}
	
	@Override
	public Block getBlock() {
		return Blocks.TNT;
	}
	
	@Override
	public int getDefaultFuse(IExplosiveEntity ent) {
		return 400;
	}
	
	@Override
	public float getSize(IExplosiveEntity ent) {
		return 20f;
	}
}

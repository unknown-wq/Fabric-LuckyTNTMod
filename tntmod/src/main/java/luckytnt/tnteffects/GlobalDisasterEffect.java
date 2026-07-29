package luckytnt.tnteffects;

import luckytnt.registry.BlockRegistry;
import luckytntlib.util.IExplosiveEntity;
import luckytntlib.util.explosions.ExplosionHelper;
import luckytntlib.util.explosions.ImprovedExplosion;
import luckytntlib.util.tnteffects.PrimedTNTEffect;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public class GlobalDisasterEffect extends PrimedTNTEffect{

	/**
	 * {@link DustParticleOptions} is immutable, so the same instance can be handed to every addParticle call
	 * instead of allocating 180 of them per tick.
	 */
	private static final DustParticleOptions DUST = new DustParticleOptions(((int)(0.2f*255)<<16)|((int)(0.2f*255)<<8)|(int)(0.2f*255), 0.75f);

	@Override
	public void serverExplosion(IExplosiveEntity entity) {
		Level level = entity.getLevel();
		if(!(level instanceof ServerLevel sLevel)) {
			return;
		}
		//a radius of 50 is roughly 524k positions, so everything that does not depend on the position is hoisted out
		//of the loop: the level, the dummy explosion (previously re-resolved for every single block) and the air state
		final ImprovedExplosion dummy = ImprovedExplosion.dummyExplosion(level);
		final BlockState air = Blocks.AIR.defaultBlockState();
		ExplosionHelper.doSphericalExplosion(level, entity.getPos(), 50, (lvl, pos, state, distance) -> {
			//isAir first: it is a cached flag on the state and rejects the majority of the sphere before
			//getBlock(), the resistance lookup and the two world writes are ever touched
			if(state.isAir()) {
				return;
			}
			Block block = state.getBlock();
			if(block.getExplosionResistance() < 200) {
				block.wasExploded(sLevel, pos, dummy);
				//UPDATE_CLIENTS instead of flag 3: the whole sphere is annihilated in one go, so the 6 neighbour
				//updates per block would be spent notifying blocks that are being deleted in the same pass
				lvl.setBlock(pos, air, Block.UPDATE_CLIENTS);
			}
		});
	}

	@Override
	public void spawnParticles(IExplosiveEntity ent) {
		Level level = ent.getLevel();
		double x = ent.x();
		double y = ent.y();
		double z = ent.z();
		for(double angle = 0; angle < 360; angle += 6D) {
			//cos and sin were evaluated 6 times per step for 2 distinct values
			double cos = Math.cos(angle * Math.PI / 180);
			double sin = Math.sin(angle * Math.PI / 180);
			level.addParticle(DUST, x + 2 * cos, y + 0.5f, z + 2 * sin, 0, 0, 0);
			level.addParticle(DUST, x + 2 * cos, y + 0.5f + 2 * sin, z, 0, 0, 0);
			level.addParticle(DUST, x, y + 0.5f + 2 * cos, z + 2 * sin, 0, 0, 0);
		}
	}

	@Override
	public Block getBlock() {
		return BlockRegistry.GLOBAL_DISASTER.get();
	}

	@Override
	public int getDefaultFuse(IExplosiveEntity entity) {
		return 240;
	}
}

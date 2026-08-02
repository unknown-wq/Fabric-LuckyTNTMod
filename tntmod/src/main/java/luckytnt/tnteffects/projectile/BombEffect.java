package luckytnt.tnteffects.projectile;

import luckytntlib.util.IExplosiveEntity;
import luckytntlib.util.explosions.ExplosionHelper;
import luckytntlib.util.explosions.IForEachBlockExplosionEffect;
import luckytntlib.util.explosions.ImprovedExplosion;
import luckytntlib.util.tnteffects.PrimedTNTEffect;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseFireBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.core.particles.ParticleTypes;

public class BombEffect extends PrimedTNTEffect{

	public static final int RADIUS = 12;

	@Override
	public void serverExplosion(IExplosiveEntity entity) {
		ImprovedExplosion explosion = new ImprovedExplosion(entity.getLevel(), (Entity)entity, entity.getPos(), RADIUS);
		explosion.doEntityExplosion(1.25f, true);
		explosion.doBlockExplosion(1f, 1f, 1f, 1f, false, false);

		ExplosionHelper.doSphericalExplosion(entity.getLevel(), entity.getPos(), RADIUS, new IForEachBlockExplosionEffect() {

			@Override
			public void doBlockExplosion(Level level, BlockPos pos, BlockState state, double distance) {
				if(state.isAir() && level.getBlockState(pos.below()).isSolidRender()) {
					level.setBlock(pos, BaseFireBlock.getState(level, pos), 3);
				}
			}
		});
	}

	@Override
	public void spawnParticles(IExplosiveEntity entity) {
		entity.getLevel().addParticle(ParticleTypes.CLOUD, entity.x(), entity.y(), entity.z(), 0f, 0f, 0f);
	}
}

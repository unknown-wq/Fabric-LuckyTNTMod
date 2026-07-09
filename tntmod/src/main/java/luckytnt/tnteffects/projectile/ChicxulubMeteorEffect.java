package luckytnt.tnteffects.projectile;


import luckytnt.registry.EntityRegistry;
import luckytntlib.entity.LExplosiveProjectile;
import luckytntlib.util.IExplosiveEntity;
import luckytntlib.util.explosions.ExplosionHelper;
import luckytntlib.util.explosions.IForEachBlockExplosionEffect;
import luckytntlib.util.explosions.ImprovedExplosion;
import luckytntlib.util.tnteffects.PrimedTNTEffect;
import net.minecraft.block.AbstractFireBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class ChicxulubMeteorEffect extends PrimedTNTEffect {
	
	@Override
	public void serverExplosion(IExplosiveEntity entity) {
		ImprovedExplosion explosion = new ImprovedExplosion(entity.getLevel(), (Entity)entity, entity.getPos(), 80);
		explosion.doEntityExplosion(3, true);
		ExplosionHelper.doSphericalExplosion(entity.getLevel(), entity.getPos(), 60, new IForEachBlockExplosionEffect() {
			
			@Override
			public void doBlockExplosion(World level, BlockPos pos, BlockState state, double distance) {
				if(distance <= 55 && state.getBlock().getBlastResistance() <= 100) {
					state.getBlock().onDestroyedByExplosion(level, pos, explosion);
					level.setBlockState(pos, Blocks.AIR.getDefaultState(), 3);
				} else if(Math.random() < 0.6f && state.getBlock().getBlastResistance() <= 100) {
					state.getBlock().onDestroyedByExplosion(level, pos, explosion);
					level.setBlockState(pos, Blocks.AIR.getDefaultState(), 3);
					if(Math.random() < 0.25f && level.getBlockState(pos.down()).isSideSolidFullSquare(level, pos, Direction.UP)) {
						level.setBlockState(pos, AbstractFireBlock.getState(level, pos));
					}
				}
			}
		});
		
		for(int count = 0; count < 300; count++) {
			LExplosiveProjectile pompeii = EntityRegistry.POMPEII_PROJECTILE.get().create(entity.getLevel());
			pompeii.setPosition(entity.getPos());
			pompeii.setOwner(entity.owner());
			pompeii.setVelocity(Math.random() * 8D - 4D, 3 + Math.random() * 2, Math.random() * 8D - 4D);
			pompeii.setTNTFuse(100000);
			entity.getLevel().spawnEntity(pompeii);
		}
	}
	
	@Override
	public void spawnParticles(IExplosiveEntity entity) {
		entity.getLevel().addParticle(ParticleTypes.EXPLOSION, entity.x(), entity.y() + 1f, entity.z(), 0, 0, 0);
	}

	@Override
	public float getSize(IExplosiveEntity entity) {
		return 4f;
	}

	@Override
	public Block getBlock() {
		return Blocks.MAGMA_BLOCK;
	}
}

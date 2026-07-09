package luckytnt.tnteffects.projectile;


import luckytnt.registry.EntityRegistry;
import luckytntlib.entity.LExplosiveProjectile;
import luckytntlib.util.IExplosiveEntity;
import luckytntlib.util.explosions.ExplosionHelper;
import luckytntlib.util.explosions.IForEachBlockExplosionEffect;
import luckytntlib.util.explosions.ImprovedExplosion;
import luckytntlib.util.tnteffects.PrimedTNTEffect;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class VredefortProjectileEffect extends PrimedTNTEffect {

	@Override
	public void serverExplosion(IExplosiveEntity ent) {
		ImprovedExplosion explosion = new ImprovedExplosion(ent.getLevel(), (Entity)ent, ent.getPos(), 140);
		explosion.doEntityExplosion(3f, true);
		
		ExplosionHelper.doSphericalExplosion(ent.getLevel(), ent.getPos(), 120, new IForEachBlockExplosionEffect() {
			
			@Override
			public void doBlockExplosion(World level, BlockPos pos, BlockState state, double distance) {
				BlockPos posDown = pos.add(0, -1, 0);
				BlockState stateDown = level.getBlockState(posDown);
				
				if(state.getBlock().getBlastResistance() < 800 && distance < 120) {
					if(distance >= 115) {
						if(Math.random() < 0.6f) {
							state.getBlock().onDestroyedByExplosion(level, pos, ImprovedExplosion.dummyExplosion(ent.getLevel()));
							level.setBlockState(pos, Blocks.AIR.getDefaultState(), 3);
						}
					}
					else if(distance < 115) {
						state.getBlock().onDestroyedByExplosion(level, pos, ImprovedExplosion.dummyExplosion(ent.getLevel()));
						level.setBlockState(pos, Blocks.AIR.getDefaultState(), 3);
					}	
					if(Block.isFaceFullSquare(stateDown.getCollisionShape(level, posDown), Direction.UP)) {
						if(Math.random() < 0.05f && state.getBlock().getBlastResistance() < 800) {
							state.getBlock().onDestroyedByExplosion(level, pos, ImprovedExplosion.dummyExplosion(ent.getLevel()));
							level.setBlockState(pos, Blocks.FIRE.getDefaultState(), 3);
						}
					}
				}

			}
		});
		
		for(int count = 0; count < 300; count++) {
			LExplosiveProjectile projectile = EntityRegistry.SOLAR_ERUPTION_PROJECTILE.get().create(ent.getLevel());
			projectile.setPosition(ent.getPos());
			projectile.setOwner(ent.owner());
			projectile.setVelocity(Math.random() * 4 - Math.random() * 4, 3 + Math.random() * 2, Math.random() * 4 - Math.random() * 4);
			ent.getLevel().spawnEntity(projectile);
		}
		for(int count = 0; count < 6; count++) {
			LExplosiveProjectile projectile = EntityRegistry.LITTLE_METEOR.get().create(ent.getLevel());
			projectile.setPosition(ent.getPos());
			projectile.setOwner(ent.owner());
			projectile.setVelocity(Math.random() * 2 - Math.random() * 2, 3 + Math.random() * 2, Math.random() * 2 - Math.random() * 2);
			ent.getLevel().spawnEntity(projectile);
		}
	}
	
	@Override
	public void spawnParticles(IExplosiveEntity ent) {
		ent.getLevel().addParticle(ParticleTypes.EXPLOSION, ent.x(), ent.y(), ent.z(), 0, 0, 0);
	}
	
	@Override
	public Block getBlock() {
		return Blocks.MAGMA_BLOCK;
	}
	
	@Override
	public float getSize(IExplosiveEntity ent) {
		return 6f;
	}
}

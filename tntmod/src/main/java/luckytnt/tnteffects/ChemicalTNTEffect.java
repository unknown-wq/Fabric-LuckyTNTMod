package luckytnt.tnteffects;

import org.joml.Vector3f;

import luckytnt.registry.BlockRegistry;
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
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ChemicalTNTEffect extends PrimedTNTEffect{

	@SuppressWarnings("resource")
	@Override
	public void baseTick(IExplosiveEntity entity) {
		if(entity instanceof LExplosiveProjectile) {
			if(!entity.getLevel().isClient) {
				explosionTick(entity);
			}
			else {
				spawnParticles(entity);
			}
			entity.setTNTFuse(entity.getTNTFuse() - 1);
			if(entity.getTNTFuse() <= 0) {
				entity.destroy();
			}
		}
		else {
			super.baseTick(entity);
		}
	}
	
	@Override
	public void explosionTick(IExplosiveEntity entity) {
		if(entity instanceof LExplosiveProjectile) {
		ImprovedExplosion dummyExplosion = new ImprovedExplosion(entity.getLevel(), (Entity)entity, entity.getPos(), 4);
			ExplosionHelper.doSphericalExplosion(entity.getLevel(), entity.getPos(), 4, new IForEachBlockExplosionEffect() {		
				@Override
				public void doBlockExplosion(World level, BlockPos pos, BlockState state, double distance) {
					if(distance + Math.random() < 4f && state.getBlock().getBlastResistance() < 100) {
						state.getBlock().onDestroyedByExplosion(level, pos, dummyExplosion);
						level.setBlockState(pos, Blocks.AIR.getDefaultState(), 3);
					}
				}
			});
		}
	}
	
	@Override
	public void serverExplosion(IExplosiveEntity entity) {
		for(int count = 0; count < 30; count++) {
			LExplosiveProjectile projectile = EntityRegistry.CHEMICAL_PROJECTILE.get().create(entity.getLevel());
			projectile.setPosition(entity.getPos());
			projectile.setOwner(entity.owner());
			projectile.setVelocity(Math.random() * 1.5f - Math.random() * 1.5f, 0.2f, Math.random() * 1.5f - Math.random() * 1.5f);
			entity.getLevel().spawnEntity(projectile);
		}
	}
	
	@Override
	public void spawnParticles(IExplosiveEntity entity) {
		entity.getLevel().addParticle(new DustParticleEffect(new Vector3f(0.1f, 1f, 0.6f), 1), entity.x() + 0.2f, entity.y() + 1f, entity.z(), 0, 0, 0);
		entity.getLevel().addParticle(new DustParticleEffect(new Vector3f(0.6f, 0.8f, 0.4f), 1), entity.x() - 0.2f, entity.y() + 1f, entity.z(), 0, 0, 0);
		entity.getLevel().addParticle(new DustParticleEffect(new Vector3f(0.8f, 1f, 0.8f), 1), entity.x(),+ entity.y() + 1f, entity.z() + 0.2f, 0, 0, 0);
		entity.getLevel().addParticle(new DustParticleEffect(new Vector3f(0.1f, 1f, 0.2f), 1), entity.x(),+ entity.y() + 1f, entity.z() - 0.2f, 0, 0, 0);
	}
	
	@Override
	public Block getBlock() {
		return BlockRegistry.CHEMICAL_TNT.get();
	}
}

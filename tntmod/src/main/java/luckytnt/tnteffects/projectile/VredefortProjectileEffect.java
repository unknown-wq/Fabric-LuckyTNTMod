package luckytnt.tnteffects.projectile;

import net.minecraft.server.level.ServerLevel;

import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;


import luckytnt.registry.EntityRegistry;
import luckytntlib.entity.LExplosiveProjectile;
import luckytntlib.util.IExplosiveEntity;
import luckytntlib.util.explosions.ExplosionHelper;
import luckytntlib.util.explosions.IForEachBlockExplosionEffect;
import luckytntlib.util.explosions.ImprovedExplosion;
import luckytntlib.util.tnteffects.PrimedTNTEffect;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.entity.Entity;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class VredefortProjectileEffect extends PrimedTNTEffect {

	@Override
	public void serverExplosion(IExplosiveEntity ent) {
		ImprovedExplosion explosion = new ImprovedExplosion(ent.getLevel(), (Entity)ent, ent.getPos(), 140);
		explosion.doEntityExplosion(3f, true);

		//the dummy explosion is a cached singleton, but it was looked up up to three times for every one of the
		//~7.2 million positions of the sphere
		final ImprovedExplosion dummy = ImprovedExplosion.dummyExplosion(ent.getLevel());

		ExplosionHelper.doSphericalExplosion(ent.getLevel(), ent.getPos(), 120, new IForEachBlockExplosionEffect() {

			@Override
			public void doBlockExplosion(Level level, BlockPos pos, BlockState state, double distance) {
				//The two cheap pure tests come first. Previously the position below was allocated and its block
				//state was read for every single position of the radius 120 sphere, before anything was tested,
				//which is ~7.2 million wasted BlockPos allocations and block state lookups per explosion.
				if(distance >= 120 || state.getBlock().getExplosionResistance() >= 800) {
					return;
				}
				//The 5% draw is made before the block below is looked at, so the position allocation, the block
				//state lookup and the collision shape test only happen for the 5% of positions that can still
				//become fire. Both are pure tests, so the set of positions that ends up on fire is unchanged.
				boolean placeFire = false;
				if(Math.random() < 0.05f) {
					BlockPos posDown = pos.below();
					placeFire = Block.isFaceFull(level.getBlockState(posDown).getCollisionShape(level, posDown), Direction.UP);
				}
				//Air can not be destroyed, so it is rejected before the 0.6 draw is made. Setting air onto air
				//was a no-op write either way.
				boolean destroy = !state.isAir() && (distance < 115 || Math.random() < 0.6f);
				if(destroy) {
					state.getBlock().wasExploded((ServerLevel) level, pos, dummy);
				}
				if(placeFire) {
					state.getBlock().wasExploded((ServerLevel) level, pos, dummy);
					//when the position is destroyed and set on fire the air is no longer written first,
					//the fire directly replaces the block, halving the writes on those positions
					level.setBlock(pos, Blocks.FIRE.defaultBlockState(), Block.UPDATE_ALL);
				}
				else if(destroy) {
					level.setBlock(pos, Blocks.AIR.defaultBlockState(), Block.UPDATE_ALL);
				}
			}
		});

		final Level level = ent.getLevel();
		final Vec3 pos = ent.getPos();
		final LivingEntity owner = ent.owner();
		final EntityType<LExplosiveProjectile> solarEruption = EntityRegistry.SOLAR_ERUPTION_PROJECTILE.get();
		final EntityType<LExplosiveProjectile> littleMeteor = EntityRegistry.LITTLE_METEOR.get();
		for(int count = 0; count < 300; count++) {
			LExplosiveProjectile projectile = solarEruption.create(level, EntitySpawnReason.MOB_SUMMONED);
			projectile.setPos(pos);
			projectile.setOwner(owner);
			projectile.setDeltaMovement(Math.random() * 4 - Math.random() * 4, 3 + Math.random() * 2, Math.random() * 4 - Math.random() * 4);
			level.addFreshEntity(projectile);
		}
		for(int count = 0; count < 6; count++) {
			LExplosiveProjectile projectile = littleMeteor.create(level, EntitySpawnReason.MOB_SUMMONED);
			projectile.setPos(pos);
			projectile.setOwner(owner);
			projectile.setDeltaMovement(Math.random() * 2 - Math.random() * 2, 3 + Math.random() * 2, Math.random() * 2 - Math.random() * 2);
			level.addFreshEntity(projectile);
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

package luckytnt.tnteffects;


import org.joml.Vector3f;

import luckytnt.registry.BlockRegistry;
import luckytntlib.util.IExplosiveEntity;
import luckytntlib.util.explosions.ExplosionHelper;
import luckytntlib.util.explosions.IForEachBlockExplosionEffect;
import luckytntlib.util.explosions.ImprovedExplosion;
import luckytntlib.util.tnteffects.PrimedTNTEffect;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.entity.projectile.thrown.SnowballEntity;
import net.minecraft.core.particles.DustParticleEffect;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.Level;

public class SnowstormTNTEffect extends PrimedTNTEffect {

	@Override
	public void serverExplosion(IExplosiveEntity ent) {
		ExplosionHelper.doSphericalExplosion(ent.getLevel(), ent.getPos(), 50, new IForEachBlockExplosionEffect() {
			
			@Override
			public void doBlockExplosion(Level level, BlockPos pos, BlockState state, double distance) {
				if(distance <= 10 && state.getBlock().getBlastResistance() < 200 && Block.isFaceFullSquare(state.getCollisionShape(level, pos), Direction.UP)) {
					state.getBlock().onDestroyedByExplosion(level, pos, ImprovedExplosion.dummyExplosion(ent.getLevel()));
					level.setBlockState(pos, Blocks.BLUE_ICE.getDefaultState(), 3);
				} else if(distance > 10 && state.getBlock().getBlastResistance() < 200 && state.getBlock() == Blocks.WATER) {
					level.setBlockState(pos, Blocks.ICE.getDefaultState(), 3);
				}
			}
		});
		
		ExplosionHelper.doTopBlockExplosionForAll(ent.getLevel(), ent.getPos(), 50, new IForEachBlockExplosionEffect() {
			
			@Override
			public void doBlockExplosion(Level level, BlockPos pos, BlockState state, double distance) {
				level.setBlockState(pos, Blocks.SNOW.getDefaultState(), 3);
			}
		});
	}
	
	@Override
	public void explosionTick(IExplosiveEntity ent) {
		if(ent.getTNTFuse() % 4 == 0) {
			Vec3 vec31 = new Vec3(0.5D, 1D, 0D).normalize();
			SnowballEntity ball1 = new SnowballEntity(ent.getLevel(), ent.x() + 0.5D, ent.y() + 1D, ent.z());
			ball1.setDeltaMovement(vec31.x, vec31.y, vec31.z, 1f, 5f);
			ent.getLevel().addFreshEntity(ball1);
			
			Vec3 vec32 = new Vec3(-0.5D, 1D, 0D).normalize();
			SnowballEntity ball2 = new SnowballEntity(ent.getLevel(), ent.x() - 0.5D, ent.y() + 1D, ent.z());
			ball2.setDeltaMovement(vec32.x, vec32.y, vec32.z, 1f, 5f);
			ent.getLevel().addFreshEntity(ball2);
			
			Vec3 vec33 = new Vec3(0D, 1D, 0.5D).normalize();
			SnowballEntity ball3 = new SnowballEntity(ent.getLevel(), ent.x(), ent.y() + 1D, ent.z() + 0.5D);
			ball3.setDeltaMovement(vec33.x, vec33.y, vec33.z, 1f, 5f);
			ent.getLevel().addFreshEntity(ball3);
			
			Vec3 vec34 = new Vec3(0D, 1D, -0.5D).normalize();
			SnowballEntity ball4 = new SnowballEntity(ent.getLevel(), ent.x(), ent.y() + 1D, ent.z() - 0.5D);
			ball4.setDeltaMovement(vec34.x, vec34.y, vec34.z, 1f, 5f);
			ent.getLevel().addFreshEntity(ball4);
			
			Vec3 vec35 = new Vec3(0.5D, 1D, 0.5D).normalize();
			SnowballEntity ball5 = new SnowballEntity(ent.getLevel(), ent.x() + 0.5D, ent.y() + 1D, ent.z() + 0.5D);
			ball5.setDeltaMovement(vec35.x, vec35.y, vec35.z, 1f, 5f);
			ent.getLevel().addFreshEntity(ball5);
			
			Vec3 vec36 = new Vec3(-0.5D, 1D, 0.5D).normalize();
			SnowballEntity ball6 = new SnowballEntity(ent.getLevel(), ent.x() - 0.5D, ent.y() + 1D, ent.z() + 0.5D);
			ball6.setDeltaMovement(vec36.x, vec36.y, vec36.z, 1f, 5f);
			ent.getLevel().addFreshEntity(ball6);
			
			Vec3 vec37 = new Vec3(0.5D, 1D, -0.5D).normalize();
			SnowballEntity ball7 = new SnowballEntity(ent.getLevel(), ent.x() + 0.5D, ent.y() + 1D, ent.z() - 0.5D);
			ball7.setDeltaMovement(vec37.x, vec37.y, vec37.z, 1f, 5f);
			ent.getLevel().addFreshEntity(ball7);
			
			Vec3 vec38 = new Vec3(-0.5D, 1D, -0.5D).normalize();
			SnowballEntity ball8 = new SnowballEntity(ent.getLevel(), ent.x() - 0.5D, ent.y() + 1D, ent.z() - 0.5D);
			ball8.setDeltaMovement(vec38.x, vec38.y, vec38.z, 1f, 5f);
			ent.getLevel().addFreshEntity(ball8);
		}
	}
	
	@Override
	public void spawnParticles(IExplosiveEntity ent) {
		ent.getLevel().addParticle(new DustParticleEffect(new Vector3f(1f, 1f, 1f), 1f), ent.x(), ent.y() + 1D, ent.z(), 0, 0, 0);
	}

	@Override
	public Block getBlock() {
		return BlockRegistry.SNOWSTORM_TNT.get();
	}

	@Override
	public int getDefaultFuse(IExplosiveEntity ent) {
		return 160;
	}
}

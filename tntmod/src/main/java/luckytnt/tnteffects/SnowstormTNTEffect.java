package luckytnt.tnteffects;


import org.joml.Vector3f;

import luckytnt.registry.BlockRegistry;
import luckytntlib.util.IExplosiveEntity;
import luckytntlib.util.explosions.ExplosionHelper;
import luckytntlib.util.explosions.IForEachBlockExplosionEffect;
import luckytntlib.util.explosions.ImprovedExplosion;
import luckytntlib.util.tnteffects.PrimedTNTEffect;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.projectile.thrown.SnowballEntity;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class SnowstormTNTEffect extends PrimedTNTEffect {

	@Override
	public void serverExplosion(IExplosiveEntity ent) {
		ExplosionHelper.doSphericalExplosion(ent.getLevel(), ent.getPos(), 50, new IForEachBlockExplosionEffect() {
			
			@Override
			public void doBlockExplosion(World level, BlockPos pos, BlockState state, double distance) {
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
			public void doBlockExplosion(World level, BlockPos pos, BlockState state, double distance) {
				level.setBlockState(pos, Blocks.SNOW.getDefaultState(), 3);
			}
		});
	}
	
	@Override
	public void explosionTick(IExplosiveEntity ent) {
		if(ent.getTNTFuse() % 4 == 0) {
			Vec3d vec31 = new Vec3d(0.5D, 1D, 0D).normalize();
			SnowballEntity ball1 = new SnowballEntity(ent.getLevel(), ent.x() + 0.5D, ent.y() + 1D, ent.z());
			ball1.setVelocity(vec31.x, vec31.y, vec31.z, 1f, 5f);
			ent.getLevel().spawnEntity(ball1);
			
			Vec3d vec32 = new Vec3d(-0.5D, 1D, 0D).normalize();
			SnowballEntity ball2 = new SnowballEntity(ent.getLevel(), ent.x() - 0.5D, ent.y() + 1D, ent.z());
			ball2.setVelocity(vec32.x, vec32.y, vec32.z, 1f, 5f);
			ent.getLevel().spawnEntity(ball2);
			
			Vec3d vec33 = new Vec3d(0D, 1D, 0.5D).normalize();
			SnowballEntity ball3 = new SnowballEntity(ent.getLevel(), ent.x(), ent.y() + 1D, ent.z() + 0.5D);
			ball3.setVelocity(vec33.x, vec33.y, vec33.z, 1f, 5f);
			ent.getLevel().spawnEntity(ball3);
			
			Vec3d vec34 = new Vec3d(0D, 1D, -0.5D).normalize();
			SnowballEntity ball4 = new SnowballEntity(ent.getLevel(), ent.x(), ent.y() + 1D, ent.z() - 0.5D);
			ball4.setVelocity(vec34.x, vec34.y, vec34.z, 1f, 5f);
			ent.getLevel().spawnEntity(ball4);
			
			Vec3d vec35 = new Vec3d(0.5D, 1D, 0.5D).normalize();
			SnowballEntity ball5 = new SnowballEntity(ent.getLevel(), ent.x() + 0.5D, ent.y() + 1D, ent.z() + 0.5D);
			ball5.setVelocity(vec35.x, vec35.y, vec35.z, 1f, 5f);
			ent.getLevel().spawnEntity(ball5);
			
			Vec3d vec36 = new Vec3d(-0.5D, 1D, 0.5D).normalize();
			SnowballEntity ball6 = new SnowballEntity(ent.getLevel(), ent.x() - 0.5D, ent.y() + 1D, ent.z() + 0.5D);
			ball6.setVelocity(vec36.x, vec36.y, vec36.z, 1f, 5f);
			ent.getLevel().spawnEntity(ball6);
			
			Vec3d vec37 = new Vec3d(0.5D, 1D, -0.5D).normalize();
			SnowballEntity ball7 = new SnowballEntity(ent.getLevel(), ent.x() + 0.5D, ent.y() + 1D, ent.z() - 0.5D);
			ball7.setVelocity(vec37.x, vec37.y, vec37.z, 1f, 5f);
			ent.getLevel().spawnEntity(ball7);
			
			Vec3d vec38 = new Vec3d(-0.5D, 1D, -0.5D).normalize();
			SnowballEntity ball8 = new SnowballEntity(ent.getLevel(), ent.x() - 0.5D, ent.y() + 1D, ent.z() - 0.5D);
			ball8.setVelocity(vec38.x, vec38.y, vec38.z, 1f, 5f);
			ent.getLevel().spawnEntity(ball8);
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

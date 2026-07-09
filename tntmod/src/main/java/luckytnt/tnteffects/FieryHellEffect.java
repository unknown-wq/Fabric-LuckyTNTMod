package luckytnt.tnteffects;

import luckytnt.registry.BlockRegistry;
import luckytnt.registry.EntityRegistry;
import luckytnt.util.Materials;
import luckytntlib.util.IExplosiveEntity;
import luckytntlib.util.explosions.ExplosionHelper;
import luckytntlib.util.explosions.IForEachBlockExplosionEffect;
import luckytntlib.util.explosions.ImprovedExplosion;
import luckytntlib.util.tnteffects.PrimedTNTEffect;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FluidBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.mob.GhastEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class FieryHellEffect extends PrimedTNTEffect {

	@Override
	public void serverExplosion(IExplosiveEntity ent) {
		ExplosionHelper.doSphericalExplosion(ent.getLevel(), ent.getPos(), 50, new IForEachBlockExplosionEffect() {
			
			@Override
			public void doBlockExplosion(World level, BlockPos pos, BlockState state, double distance) {
				if((Math.round(ent.y()) - pos.getY()) >= 0 && (Math.round(ent.y()) - pos.getY()) <= 20) {
					if((state.getBlock().getBlastResistance() < 100 || state.getBlock() instanceof FluidBlock || state.isAir()) && !Materials.isStone(state)) {
						state.getBlock().onDestroyedByExplosion(level, pos, ImprovedExplosion.dummyExplosion(ent.getLevel()));
						level.setBlockState(pos, Blocks.LAVA.getDefaultState(), 3);
					} 
				}
			}
		});
		
		ExplosionHelper.doSphericalExplosion(ent.getLevel(), ent.getPos(), 90, new IForEachBlockExplosionEffect() {
			
			@Override
			public void doBlockExplosion(World level, BlockPos pos, BlockState state, double distance) {
				BlockPos posTop = pos.add(0, 1, 0);
				BlockState stateTop = level.getBlockState(posTop);
				
				if(state.getBlock().getBlastResistance() < 100 && !state.isAir()) {
					if(Math.random() < 0.9f) {
						state.getBlock().onDestroyedByExplosion(level, pos, ImprovedExplosion.dummyExplosion(ent.getLevel()));
						level.setBlockState(pos, Blocks.NETHERRACK.getDefaultState(), 3);
						if(Math.random() < 0.1f) {
							if(!Block.isFaceFullSquare(stateTop.getCollisionShape(level, posTop), Direction.UP)) {
								level.setBlockState(posTop, Blocks.FIRE.getDefaultState(), 3);
							}
						}
					} else if(Math.random() < 0.3f) {
						state.getBlock().onDestroyedByExplosion(level, pos, ImprovedExplosion.dummyExplosion(ent.getLevel()));
						level.setBlockState(pos, Blocks.LAVA.getDefaultState(), 3);
					}								
				}
			}
		});
		
		EntityRegistry.TNT_X20_EFFECT.build().serverExplosion(ent);
		
		for(int count = 0; count < 15; count++) {
			Entity ghast = new GhastEntity(EntityType.GHAST, ent.getLevel());
			ghast.setPosition(ent.x() + 20 * Math.random() - 20 * Math.random(), ent.y() + 50 / 2 * Math.random() + 50 / 2, ent.z() + 20 * Math.random() - 20 * Math.random());
			ent.getLevel().spawnEntity(ghast);
		}
	}
	
	@Override
	public void spawnParticles(IExplosiveEntity ent) {
		ent.getLevel().addParticle(ParticleTypes.FLAME, ent.x(), ent.y(), ent.z(), 0D, 0.3D, 0D);
		ent.getLevel().addParticle(ParticleTypes.FLAME, ent.x(), ent.y(), ent.z(), 0.1D, 0.2D, 0D);
		ent.getLevel().addParticle(ParticleTypes.FLAME, ent.x(), ent.y(), ent.z(), -0.1D, 0.2D, 0D);
		ent.getLevel().addParticle(ParticleTypes.FLAME, ent.x(), ent.y(), ent.z(), 0D, 0.2D, 0.1D);
		ent.getLevel().addParticle(ParticleTypes.FLAME, ent.x(), ent.y(), ent.z(), 0D, 0.2D, -0.1D);
		ent.getLevel().addParticle(ParticleTypes.FLAME, ent.x(), ent.y(), ent.z(), 0.2D, 0.2D, 0D);
		ent.getLevel().addParticle(ParticleTypes.FLAME, ent.x(), ent.y(), ent.z(), -0.2D, 0.2D, 0D);
		ent.getLevel().addParticle(ParticleTypes.FLAME, ent.x(), ent.y(), ent.z(), 0D, 0.2D, 0.2D);
		ent.getLevel().addParticle(ParticleTypes.FLAME, ent.x(), ent.y(), ent.z(), 0D, 0.2D, -0.2D);
		ent.getLevel().addParticle(ParticleTypes.FLAME, ent.x(), ent.y(), ent.z(), 0.3D, 0.2D, 0.3D);
		ent.getLevel().addParticle(ParticleTypes.FLAME, ent.x(), ent.y(), ent.z(), -0.3D, 0.2D, -0.3D);
		ent.getLevel().addParticle(ParticleTypes.FLAME, ent.x(), ent.y(), ent.z(), -0.3D, 0.2D, 0.3D);
		ent.getLevel().addParticle(ParticleTypes.FLAME, ent.x(), ent.y(), ent.z(), 0.3D, 0.2D, -0.3D);
	}
	
	@Override
	public Block getBlock() {
		return BlockRegistry.FIERY_HELL.get();
	}
	
	@Override
	public int getDefaultFuse(IExplosiveEntity ent) {
		return 240;
	}
}

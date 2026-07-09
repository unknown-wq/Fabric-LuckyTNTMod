package luckytnt.tnteffects;

import luckytnt.config.LuckyTNTConfigValues;
import luckytnt.registry.BlockRegistry;
import luckytnt.util.Materials;
import luckytntlib.util.IExplosiveEntity;
import luckytntlib.util.explosions.ExplosionHelper;
import luckytntlib.util.explosions.IForEachBlockExplosionEffect;
import luckytntlib.util.tnteffects.PrimedTNTEffect;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FluidBlock;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class HellsGateEffect extends PrimedTNTEffect{

	@Override
	public void serverExplosion(IExplosiveEntity entity) {
		ExplosionHelper.doSphericalExplosion(entity.getLevel(), entity.getPos(), 30, new IForEachBlockExplosionEffect() {
		
			@Override
			public void doBlockExplosion(World level, BlockPos pos, BlockState state, double distance) {
				BlockPos posTop = pos.add(0, LuckyTNTConfigValues.ISLAND_HEIGHT.get(), 0);
				BlockState stateTop = level.getBlockState(posTop);
				
				if(state.getBlock().getBlastResistance() < 200 && stateTop.isAir() && !state.isAir() && Math.abs(entity.y() - pos.getY()) <= 20) {
					level.setBlockState(pos, Blocks.AIR.getDefaultState(), 3);
					if(Materials.isWood(state)) {
						level.setBlockState(posTop, Blocks.OBSIDIAN.getDefaultState(), 3);
					} else if(state.isIn(BlockTags.LEAVES)) {
						level.setBlockState(posTop, Blocks.NETHER_BRICKS.getDefaultState(), 3);
					} else if(state.getBlock() instanceof FluidBlock) {
						level.setBlockState(posTop, Blocks.LAVA.getDefaultState(), 3);
					} else {
						level.setBlockState(posTop, Blocks.NETHERRACK.getDefaultState(), 3);
					}
				}
			}
		});
		
		ExplosionHelper.doSphericalExplosion(entity.getLevel(), entity.getPos(), 30, new IForEachBlockExplosionEffect() {
			
			@Override
			public void doBlockExplosion(World level, BlockPos pos, BlockState state, double distance) {
				BlockPos posTop = pos.add(0, LuckyTNTConfigValues.ISLAND_HEIGHT.get(), 0);
				BlockState stateTop = level.getBlockState(posTop);
				BlockPos posAbove = pos.add(0, LuckyTNTConfigValues.ISLAND_HEIGHT.get() + 1, 0);
				BlockState stateAbove = level.getBlockState(posAbove);
				
				if(stateAbove.isAir() && Block.isFaceFullSquare(stateTop.getCollisionShape(level, posTop), Direction.UP) && Math.random() <= 0.1D) {
					level.setBlockState(posAbove, Blocks.FIRE.getDefaultState(), 3);
				}
			}
		});
	}
	
	@Override
	public void spawnParticles(IExplosiveEntity ent) {
		ent.getLevel().addParticle(ParticleTypes.FLAME, ent.x(), ent.y() + 0.5f, ent.z(), 0.1f, 0, 0.1f);
		ent.getLevel().addParticle(ParticleTypes.FLAME, ent.x(), ent.y() + 0.5f, ent.z(), -0.1f, 0, -0.1f);
		ent.getLevel().addParticle(ParticleTypes.FLAME, ent.x(), ent.y() + 0.5f, ent.z(), -0.1f, 0, 0.1f);
		ent.getLevel().addParticle(ParticleTypes.FLAME, ent.x(), ent.y() + 0.5f, ent.z(), 0.1f, 0, -0.1f);
		
		ent.getLevel().addParticle(ParticleTypes.FLAME, ent.x(), ent.y() + 0.5f, ent.z(), 0.1f, 0.1f, 0.1f);
		ent.getLevel().addParticle(ParticleTypes.FLAME, ent.x(), ent.y() + 0.5f, ent.z(), -0.1f, 0.1f, -0.1f);
		ent.getLevel().addParticle(ParticleTypes.FLAME, ent.x(), ent.y() + 0.5f, ent.z(), -0.1f, 0.1f, 0.1f);
		ent.getLevel().addParticle(ParticleTypes.FLAME, ent.x(), ent.y() + 0.5f, ent.z(), 0.1f, 0.1f, -0.1f);
		
		ent.getLevel().addParticle(ParticleTypes.FLAME, ent.x(), ent.y() + 0.5f, ent.z(), 0.1f, 0.2f, 0.1f);
		ent.getLevel().addParticle(ParticleTypes.FLAME, ent.x(), ent.y() + 0.5f, ent.z(), -0.1f, 0.2f, -0.1f);
		ent.getLevel().addParticle(ParticleTypes.FLAME, ent.x(), ent.y() + 0.5f, ent.z(), -0.1f, 0.2f, 0.1f);
		ent.getLevel().addParticle(ParticleTypes.FLAME, ent.x(), ent.y() + 0.5f, ent.z(), 0.1f, 0.2f, -0.1f);
		
		ent.getLevel().addParticle(ParticleTypes.FLAME, ent.x(), ent.y() + 0.5f, ent.z(), 0.1f, -0.1f, 0.1f);
		ent.getLevel().addParticle(ParticleTypes.FLAME, ent.x(), ent.y() + 0.5f, ent.z(), -0.1f, -0.1f, -0.1f);
		ent.getLevel().addParticle(ParticleTypes.FLAME, ent.x(), ent.y() + 0.5f, ent.z(), -0.1f, -0.1f, 0.1f);
		ent.getLevel().addParticle(ParticleTypes.FLAME, ent.x(), ent.y() + 0.5f, ent.z(), 0.1f, -0.1f, -0.1f);
		
		ent.getLevel().addParticle(ParticleTypes.FLAME, ent.x(), ent.y() + 0.5f, ent.z(), 0.1f, -0.2f, 0.1f);
		ent.getLevel().addParticle(ParticleTypes.FLAME, ent.x(), ent.y() + 0.5f, ent.z(), -0.1f, -0.2f, -0.1f);
		ent.getLevel().addParticle(ParticleTypes.FLAME, ent.x(), ent.y() + 0.5f, ent.z(), -0.1f, -0.2f, 0.1f);
		ent.getLevel().addParticle(ParticleTypes.FLAME, ent.x(), ent.y() + 0.5f, ent.z(), 0.1f, -0.2f, -0.1f);
	}

	@Override
	public Block getBlock() {
		return BlockRegistry.HELLS_GATE.get();
	}
	
	@Override
	public int getDefaultFuse(IExplosiveEntity entity) {
		return 140;
	}
}

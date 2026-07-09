package luckytnt.tnteffects;

import luckytnt.registry.BlockRegistry;
import luckytntlib.util.IExplosiveEntity;
import luckytntlib.util.explosions.ExplosionHelper;
import luckytntlib.util.explosions.IForEachBlockExplosionEffect;
import luckytntlib.util.explosions.ImprovedExplosion;
import luckytntlib.util.tnteffects.PrimedTNTEffect;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.CoralFanBlock;
import net.minecraft.block.FluidBlock;
import net.minecraft.block.KelpPlantBlock;
import net.minecraft.block.SeagrassBlock;
import net.minecraft.block.TallSeagrassBlock;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class VaporizeTNTEffect extends PrimedTNTEffect {

	private final int strength;
	
	public VaporizeTNTEffect(int strength) {
		this.strength = strength;
	}
	
	@Override
	public void serverExplosion(IExplosiveEntity entity) {
		ExplosionHelper.doSphericalExplosion(entity.getLevel(), entity.getPos(), strength, new IForEachBlockExplosionEffect() {
			
			@Override
			public void doBlockExplosion(World level, BlockPos pos, BlockState state, double distance) {
				if(state.getBlock() instanceof FluidBlock  || state.getBlock() instanceof KelpPlantBlock || state.getBlock() instanceof SeagrassBlock || state.getBlock() instanceof TallSeagrassBlock || state.getBlock() instanceof CoralFanBlock) {
					state.getBlock().onDestroyedByExplosion(level, pos, ImprovedExplosion.dummyExplosion(entity.getLevel()));
					level.setBlockState(pos, Blocks.AIR.getDefaultState(), 3);
				}
			}
		});
	}
	
	@Override
	public void spawnParticles(IExplosiveEntity entity) {
		entity.getLevel().addParticle(ParticleTypes.SMOKE, entity.x() + 1.3f, entity.y() + 0.5f, entity.z(), -0.1f, 0, 0);
		entity.getLevel().addParticle(ParticleTypes.SMOKE, entity.x() - 1.3f, entity.y() + 0.5f, entity.z(), 0.1f, 0, 0);
		entity.getLevel().addParticle(ParticleTypes.SMOKE, entity.x(), entity.y() + 0.5f, entity.z() + 1.3f, 0, 0, -0.1f);
		entity.getLevel().addParticle(ParticleTypes.SMOKE, entity.x(), entity.y() + 0.5f, entity.z() - 1.3f, 0, 0, 0.1f);
	}
	
	@Override
	public Block getBlock() {
		return BlockRegistry.VAPORIZE_TNT.get();
	}
}

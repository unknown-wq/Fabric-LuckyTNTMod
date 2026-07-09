package luckytnt.tnteffects;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import luckytnt.registry.BlockRegistry;
import luckytntlib.util.IExplosiveEntity;
import luckytntlib.util.explosions.ImprovedExplosion;
import luckytntlib.util.tnteffects.PrimedTNTEffect;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.fluid.FluidState;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.explosion.ExplosionBehavior;

public class DiggingTNTEffect extends PrimedTNTEffect{

	private static final float VEC_LENGTH = 60f;
	private static final float MAX_RESISTANCE = 100f;
	private final ExplosionBehavior damageCalculator = new ExplosionBehavior();

	@Override
	public void serverExplosion(IExplosiveEntity entity) {
		Set<BlockPos> blocks = new HashSet<>();
		ImprovedExplosion dummyExplosion = new ImprovedExplosion(entity.getLevel(), (Entity)entity, entity.getPos(), 4);
		for(float y = 0; y < VEC_LENGTH; y += 0.3f) {
			BlockPos pos = toBlockPos(entity.getPos().add(0, -y, 0));
			BlockState blockState = entity.getLevel().getBlockState(pos);
			FluidState fluidState = entity.getLevel().getFluidState(pos);
			Optional<Float> explosionResistance = damageCalculator.getBlastResistance(dummyExplosion, entity.getLevel(), pos, blockState, fluidState);
			if(explosionResistance.isPresent() && explosionResistance.get() > MAX_RESISTANCE) {
				y += 100f;
			}
			else {
				blocks.add(pos);
			}
		}
		for(BlockPos pos : blocks) {
			entity.getLevel().getBlockState(pos).getBlock().onDestroyedByExplosion(entity.getLevel(), pos, dummyExplosion);
			entity.getLevel().setBlockState(pos, Blocks.AIR.getDefaultState(), 3);
		}
	}
	
	@Override
	public void spawnParticles(IExplosiveEntity entity) {
		entity.getLevel().addParticle(ParticleTypes.SMOKE, entity.x(), entity.y() + 1.4f, entity.z(), 0, -0.1f, 0);
	}
	
	@Override
	public Block getBlock() {
		return BlockRegistry.DIGGING_TNT.get();
	}
}

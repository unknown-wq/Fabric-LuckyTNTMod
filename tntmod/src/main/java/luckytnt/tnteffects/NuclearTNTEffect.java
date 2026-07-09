package luckytnt.tnteffects;

import org.joml.Vector3f;

import luckytnt.registry.BlockRegistry;
import luckytnt.registry.EffectRegistry;
import luckytntlib.util.IExplosiveEntity;
import luckytntlib.util.explosions.ExplosionHelper;
import luckytntlib.util.explosions.IForEachBlockExplosionEffect;
import luckytntlib.util.explosions.IForEachEntityExplosionEffect;
import luckytntlib.util.explosions.ImprovedExplosion;
import luckytntlib.util.tnteffects.PrimedTNTEffect;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.LeavesBlock;
import net.minecraft.block.PlantBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.registry.Registries;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class NuclearTNTEffect extends PrimedTNTEffect{

	private final int strength;
	
	public NuclearTNTEffect(int strength) {
		this.strength = strength;
	}
	
	@Override
	public void serverExplosion(IExplosiveEntity entity) {
		ImprovedExplosion explosion = new ImprovedExplosion(entity.getLevel(), (Entity) entity, entity.getPos(), strength);
		explosion.doEntityExplosion(strength / 10f, true);
		explosion.doEntityExplosion(new IForEachEntityExplosionEffect() {		
			@Override
			public void doEntityExplosion(Entity entity, double distance) {
				if(entity instanceof LivingEntity living) {
					living.addStatusEffect(new StatusEffectInstance(Registries.STATUS_EFFECT.entryOf(EffectRegistry.CONTAMINATED), 48 * strength));
				}
			}
		});
		explosion.doBlockExplosion(1f, 1.3f, 1f, 1f, false, false);
		ExplosionHelper.doSphericalExplosion(entity.getLevel(), entity.getPos(), strength * 3, new IForEachBlockExplosionEffect() {		
			@Override
			public void doBlockExplosion(World level, BlockPos pos, BlockState state, double distance) {
				if(state.getBlock() instanceof PlantBlock || state.getBlock() instanceof LeavesBlock) {
					state.getBlock().onDestroyedByExplosion(level, pos, explosion);
					level.setBlockState(pos, Blocks.AIR.getDefaultState(), 3);
				}
			}
		});
		explosion.doBlockExplosion(new IForEachBlockExplosionEffect() {
			
			@Override
			public void doBlockExplosion(World level, BlockPos pos, BlockState state, double distance) {
				BlockState stateAbove = level.getBlockState(pos.up());
				if(stateAbove.isAir() && !state.isAir() && Math.random() < 0.33f) {
					level.setBlockState(pos.up(), BlockRegistry.NUCLEAR_WASTE.get().getDefaultState());
				}
			}
		});
	}
	
	@Override
	public void spawnParticles(IExplosiveEntity entity) {
		entity.getLevel().addParticle(new DustParticleEffect(new Vector3f(0.9f, 1f, 0f), 1), entity.x(), entity.y() + 1f, entity.z(), 0, 0, 0);
	}
	
	@Override
	public Block getBlock() {
		return BlockRegistry.NUCLEAR_TNT.get();
	}
	
	@Override
	public int getDefaultFuse(IExplosiveEntity entity) {
		return 200;
	}
}

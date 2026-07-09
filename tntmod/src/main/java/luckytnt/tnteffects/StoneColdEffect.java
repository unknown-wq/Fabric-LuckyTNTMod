package luckytnt.tnteffects;

import java.util.List;

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
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

public class StoneColdEffect extends PrimedTNTEffect {

	@Override
	public void explosionTick(IExplosiveEntity ent) {
		if(ent.getLevel() instanceof ServerWorld s_Level) {
			s_Level.setTimeOfDay(s_Level.getTimeOfDay() + 200);
		}
		for(int count = 0; count < 7; count++) {
			double offX = Math.random() * 15 - Math.random() * 15;
			double offY = Math.random() * 15 - Math.random() * 15;
			double offZ = Math.random() * 15 - Math.random() * 15;
			BlockPos pos = new BlockPos(MathHelper.floor(ent.x() + offX), MathHelper.floor(ent.y() + offY), MathHelper.floor(ent.z() + offZ));
			BlockState state = ent.getLevel().getBlockState(pos);
			if(state.getBlock().getBlastResistance() < 100 && state.isFullCube(ent.getLevel(), pos) && !state.isAir()) {
				state.getBlock().onDestroyedByExplosion(ent.getLevel(), pos, ImprovedExplosion.dummyExplosion(ent.getLevel()));
				ent.getLevel().setBlockState(pos, Blocks.BLUE_ICE.getDefaultState(), 3);
			}
		}
		ent.getLevel().playSound(null, ent.x(), ent.y(), ent.z(), SoundEvents.BLOCK_STONE_PLACE, SoundCategory.BLOCKS, 0.5f, 1);
	}
	
	@Override
	public void serverExplosion(IExplosiveEntity ent) {
		ExplosionHelper.doSphericalExplosion(ent.getLevel(), ent.getPos(), 90, new IForEachBlockExplosionEffect() {
			
			@Override
			public void doBlockExplosion(World level, BlockPos pos, BlockState state, double distance) {
				if(state.getBlock().getBlastResistance() < 200 && Block.isFaceFullSquare(state.getCollisionShape(level, pos), Direction.UP) && state != Blocks.BLUE_ICE.getDefaultState()) {
					state.getBlock().onDestroyedByExplosion(level, pos, ImprovedExplosion.dummyExplosion(ent.getLevel()));
					level.setBlockState(pos, Blocks.BLUE_ICE.getDefaultState(), 3);
				}
			}
		});
		
		ExplosionHelper.doSphericalExplosion(ent.getLevel(), ent.getPos(), 130, new IForEachBlockExplosionEffect() {
			
			@Override
			public void doBlockExplosion(World level, BlockPos pos, BlockState state, double distance) {
				if(state.getBlock() == Blocks.WATER && state != Blocks.ICE.getDefaultState()) {
					state.getBlock().onDestroyedByExplosion(level, pos, ImprovedExplosion.dummyExplosion(ent.getLevel()));
					level.setBlockState(pos, Blocks.ICE.getDefaultState(), 3);
				}
			}
		});
		
		ExplosionHelper.doTopBlockExplosionForAll(ent.getLevel(), ent.getPos(), 130, new IForEachBlockExplosionEffect() {
			
			@Override
			public void doBlockExplosion(World level, BlockPos pos, BlockState state, double distance) {
				if(state.getBlock().getBlastResistance() < 100) {
					state.getBlock().onDestroyedByExplosion(level, pos, ImprovedExplosion.dummyExplosion(ent.getLevel()));
					level.setBlockState(pos, Blocks.SNOW.getDefaultState(), 3);
				}
			}
		});
		
		List<LivingEntity> entities = ent.getLevel().getNonSpectatingEntities(LivingEntity.class, new Box(ent.x() - 90, ent.y() - 90, ent.z() - 90, ent.x() + 90, ent.y() + 90, ent.z() + 90));
		for(LivingEntity lEnt : entities) {
			lEnt.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, 800, 2));
		}
	}
	
	@Override
	public void spawnParticles(IExplosiveEntity ent) {
		ent.getLevel().addParticle(new DustParticleEffect(new Vector3f(0.2f, 0.9f, 1f), 1f), ent.x(), ent.y() + 1f, ent.z(), 0, 0, 0);
	}
	
	@Override
	public Block getBlock() {
		return BlockRegistry.STONE_COLD.get();
	}
	
	@Override
	public int getDefaultFuse(IExplosiveEntity ent) {
		return 140;
	}
}

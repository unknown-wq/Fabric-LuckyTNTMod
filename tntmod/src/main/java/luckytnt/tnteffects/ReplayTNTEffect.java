package luckytnt.tnteffects;

import java.util.HashMap;

import org.joml.Vector3f;

import luckytnt.entity.PrimedReplayTNT;
import luckytnt.registry.BlockRegistry;
import luckytntlib.block.LTNTBlock;
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
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class ReplayTNTEffect extends PrimedTNTEffect {

	@Override
	public void explosionTick(IExplosiveEntity entity) {
		if(entity.getLevel() instanceof ServerWorld sLevel && entity instanceof PrimedReplayTNT tnt) {
			if(tnt.getTNTFuse() == 400) {
				ExplosionHelper.doSphericalExplosion(sLevel, tnt.getPos(), 10, new IForEachBlockExplosionEffect() {
					
					@Override
					public void doBlockExplosion(World level, BlockPos pos, BlockState state, double distance) {
						if(state.getBlock() instanceof LTNTBlock) {
							state.getBlock().onDestroyedByExplosion(sLevel, pos, ImprovedExplosion.dummyExplosion(entity.getLevel()));
							level.setBlockState(pos, Blocks.AIR.getDefaultState(), 3);
						}
						tnt.blocks.put(pos, state);
					}
				});
			}
			if(tnt.getTNTFuse() > 200) {
				HashMap<BlockPos, BlockState> list = new HashMap<>();
				ExplosionHelper.doSphericalExplosion(sLevel, tnt.getPos(), 10, new IForEachBlockExplosionEffect() {
					
					@Override
					public void doBlockExplosion(World level, BlockPos pos, BlockState state, double distance) {
						if(tnt.blocks.get(pos) != null) {
							if(!tnt.blocks.get(pos).equals(state)) {
								list.put(pos, state);
							}
						}
					}
				});
				tnt.blockChanges.set(tnt.getTNTFuse() - 200, list);
			}
			if(tnt.getTNTFuse() == 200) {
				for(BlockPos pos : tnt.blocks.keySet()) {
					sLevel.setBlockState(pos, tnt.blocks.get(pos));
				}
			}
			if(tnt.getTNTFuse() < 200 && tnt.blockChanges.get(tnt.getTNTFuse()) != null) {
				HashMap<BlockPos, BlockState> list = tnt.blockChanges.get(tnt.getTNTFuse());
				for(BlockPos pos : list.keySet()) {
					sLevel.setBlockState(pos, list.get(pos));
				}
			}
		}
		((Entity)entity).setVelocity(0, 0, 0);
		((Entity)entity).setPosition(((Entity)entity).getLerpedPos(0f));
	}
	
	@Override
	public void spawnParticles(IExplosiveEntity entity) {
		if(entity.getTNTFuse() > 200) {
			entity.getLevel().addParticle(new DustParticleEffect(new Vector3f(1f, 0f, 0f), 0.5f), entity.x(), entity.y() + 1.5D, entity.z(), 0, 0, 0);
			for(double angle = 0; angle < 360; angle += 36D) {
				entity.getLevel().addParticle(new DustParticleEffect(new Vector3f(1f, 0, 0), 0.5f), entity.x() + 0.125 * Math.cos(angle * Math.PI / 180), entity.y() + 1.5f + 0.125 * Math.sin(angle * Math.PI / 180), entity.z(), 0, 0, 0);
				entity.getLevel().addParticle(new DustParticleEffect(new Vector3f(1f, 0, 0), 0.5f), entity.x() + 0.0675 * Math.cos(angle * Math.PI / 180), entity.y() + 1.5f + 0.0675 * Math.sin(angle * Math.PI / 180), entity.z(), 0, 0, 0);
			}
			for(double angle = 0; angle < 360; angle += 12D) {
				entity.getLevel().addParticle(new DustParticleEffect(new Vector3f(1f, 0, 0), 0.5f), entity.x() + 0.175 * Math.cos(angle * Math.PI / 180), entity.y() + 1.5f + 0.175 * Math.sin(angle * Math.PI / 180), entity.z(), 0, 0, 0);
			}
		}
		if(entity.getTNTFuse() <= 200) {
			Vec3d vec31 = new Vec3d((entity.x() + 0.175D) - (entity.x() - 0.175D), (entity.y() + 1.5D) - (entity.y() + 1.5D + 0.175D), 0);
			Vec3d vec32 = new Vec3d((entity.x() + 0.175D) - (entity.x() - 0.175D), (entity.y() + 1.5D) - (entity.y() + 1.5D - 0.175D), 0);
			
			entity.getLevel().addParticle(new DustParticleEffect(new Vector3f(0f, 0f, 0f), 0.5f), entity.x(), entity.y() + 1.5D, entity.z(), 0, 0, 0);
			entity.getLevel().addParticle(new DustParticleEffect(new Vector3f(0f, 0f, 0f), 0.5f), entity.x() - 0.0875D, entity.y() + 1.5D, entity.z(), 0, 0, 0);
			entity.getLevel().addParticle(new DustParticleEffect(new Vector3f(0f, 0f, 0f), 0.5f), entity.x() - 0.0875D, entity.y() + 1.5D + 0.08D, entity.z(), 0, 0, 0);
			entity.getLevel().addParticle(new DustParticleEffect(new Vector3f(0f, 0f, 0f), 0.5f), entity.x() - 0.0875D, entity.y() + 1.5D - 0.08D, entity.z(), 0, 0, 0);
			for(double i = 0D; i <= 0.35D; i += 0.05D) {
				entity.getLevel().addParticle(new DustParticleEffect(new Vector3f(0f, 0f, 0f), 0.5f), entity.x() - 0.175D, entity.y() + 1.5D - 0.175D + i, entity.z(), 0, 0, 0);
			}
			for(double i = 0; i <= 1; i += 0.1D) {
				entity.getLevel().addParticle(new DustParticleEffect(new Vector3f(0f, 0f, 0f), 0.5f), entity.x() - 0.175D + i * vec31.x, entity.y() + 1.5D + 0.175D + i * vec31.y, entity.z(), 0, 0, 0);
				entity.getLevel().addParticle(new DustParticleEffect(new Vector3f(0f, 0f, 0f), 0.5f), entity.x() - 0.175D + i * vec32.x, entity.y() + 1.5D - 0.175D + i * vec32.y, entity.z(), 0, 0, 0);
			}
		}
	}
	
	@Override
	public Block getBlock() {
		return BlockRegistry.REPLAY_TNT.get();
	}
	
	@Override
	public int getDefaultFuse(IExplosiveEntity entity) {
		return 400;
	}
}

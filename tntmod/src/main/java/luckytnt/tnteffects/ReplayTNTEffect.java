package luckytnt.tnteffects;

import java.util.HashMap;
import java.util.Map;

import org.joml.Vector3f;

import luckytnt.entity.PrimedReplayTNT;
import luckytnt.registry.BlockRegistry;
import luckytntlib.block.LTNTBlock;
import luckytntlib.util.IExplosiveEntity;
import luckytntlib.util.explosions.ExplosionHelper;
import luckytntlib.util.explosions.IForEachBlockExplosionEffect;
import luckytntlib.util.explosions.ImprovedExplosion;
import luckytntlib.util.tnteffects.PrimedTNTEffect;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.entity.Entity;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.Level;

public class ReplayTNTEffect extends PrimedTNTEffect {

	@Override
	public void explosionTick(IExplosiveEntity entity) {
		if(entity.getLevel() instanceof ServerLevel sLevel && entity instanceof PrimedReplayTNT tnt) {
			if(tnt.getTNTFuse() == 400) {
				ExplosionHelper.doSphericalExplosion(sLevel, tnt.getPos(), 10, new IForEachBlockExplosionEffect() {
					
					@Override
					public void doBlockExplosion(Level level, BlockPos pos, BlockState state, double distance) {
						if(state.getBlock() instanceof LTNTBlock) {
							state.getBlock().wasExploded(sLevel, pos, ImprovedExplosion.dummyExplosion(entity.getLevel()));
							level.setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
						}
						tnt.blocks.put(pos, state);
					}
				});
			}
			// Recording every tick meant 200 full r=10 sphere sweeps and 200 retained HashMaps.
			// Every snapshot is a full cumulative diff against tnt.blocks (not an incremental delta),
			// so recording only every 4th tick still replays the complete change set, just at a
			// 4 tick granularity. 400 % 4 == 0 and 200 % 4 == 0, so record and playback stay aligned.
			if(tnt.getTNTFuse() > 200 && tnt.getTNTFuse() % 4 == 0) {
				HashMap<BlockPos, BlockState> original = tnt.blocks;
				HashMap<BlockPos, BlockState> list = new HashMap<>();
				ExplosionHelper.doSphericalExplosion(sLevel, tnt.getPos(), 10, new IForEachBlockExplosionEffect() {

					@Override
					public void doBlockExplosion(Level level, BlockPos pos, BlockState state, double distance) {
						BlockState old = original.get(pos);
						if(old != null && !old.equals(state)) {
							list.put(pos, state);
						}
					}
				});
				tnt.blockChanges.set(tnt.getTNTFuse() - 200, list);
			}
			if(tnt.getTNTFuse() == 200) {
				for(Map.Entry<BlockPos, BlockState> entry : tnt.blocks.entrySet()) {
					sLevel.setBlockAndUpdate(entry.getKey(), entry.getValue());
				}
			}
			if(tnt.getTNTFuse() < 200 && tnt.blockChanges.get(tnt.getTNTFuse()) != null) {
				HashMap<BlockPos, BlockState> list = tnt.blockChanges.get(tnt.getTNTFuse());
				for(Map.Entry<BlockPos, BlockState> entry : list.entrySet()) {
					sLevel.setBlockAndUpdate(entry.getKey(), entry.getValue());
				}
			}
		}
		((Entity)entity).setDeltaMovement(0, 0, 0);
		((Entity)entity).setPos(((Entity)entity).getPosition(0f));
	}
	
	@Override
	public void spawnParticles(IExplosiveEntity entity) {
		if(entity.getTNTFuse() > 200) {
			entity.getLevel().addParticle(new DustParticleOptions(((int)(1f*255)<<16)|((int)(0f*255)<<8)|(int)(0f*255), 0.5f), entity.x(), entity.y() + 1.5D, entity.z(), 0, 0, 0);
			for(double angle = 0; angle < 360; angle += 36D) {
				entity.getLevel().addParticle(new DustParticleOptions(((int)(1f*255)<<16)|((int)(0*255)<<8)|(int)(0*255), 0.5f), entity.x() + 0.125 * Math.cos(angle * Math.PI / 180), entity.y() + 1.5f + 0.125 * Math.sin(angle * Math.PI / 180), entity.z(), 0, 0, 0);
				entity.getLevel().addParticle(new DustParticleOptions(((int)(1f*255)<<16)|((int)(0*255)<<8)|(int)(0*255), 0.5f), entity.x() + 0.0675 * Math.cos(angle * Math.PI / 180), entity.y() + 1.5f + 0.0675 * Math.sin(angle * Math.PI / 180), entity.z(), 0, 0, 0);
			}
			for(double angle = 0; angle < 360; angle += 12D) {
				entity.getLevel().addParticle(new DustParticleOptions(((int)(1f*255)<<16)|((int)(0*255)<<8)|(int)(0*255), 0.5f), entity.x() + 0.175 * Math.cos(angle * Math.PI / 180), entity.y() + 1.5f + 0.175 * Math.sin(angle * Math.PI / 180), entity.z(), 0, 0, 0);
			}
		}
		if(entity.getTNTFuse() <= 200) {
			Vec3 vec31 = new Vec3((entity.x() + 0.175D) - (entity.x() - 0.175D), (entity.y() + 1.5D) - (entity.y() + 1.5D + 0.175D), 0);
			Vec3 vec32 = new Vec3((entity.x() + 0.175D) - (entity.x() - 0.175D), (entity.y() + 1.5D) - (entity.y() + 1.5D - 0.175D), 0);
			
			entity.getLevel().addParticle(new DustParticleOptions(((int)(0f*255)<<16)|((int)(0f*255)<<8)|(int)(0f*255), 0.5f), entity.x(), entity.y() + 1.5D, entity.z(), 0, 0, 0);
			entity.getLevel().addParticle(new DustParticleOptions(((int)(0f*255)<<16)|((int)(0f*255)<<8)|(int)(0f*255), 0.5f), entity.x() - 0.0875D, entity.y() + 1.5D, entity.z(), 0, 0, 0);
			entity.getLevel().addParticle(new DustParticleOptions(((int)(0f*255)<<16)|((int)(0f*255)<<8)|(int)(0f*255), 0.5f), entity.x() - 0.0875D, entity.y() + 1.5D + 0.08D, entity.z(), 0, 0, 0);
			entity.getLevel().addParticle(new DustParticleOptions(((int)(0f*255)<<16)|((int)(0f*255)<<8)|(int)(0f*255), 0.5f), entity.x() - 0.0875D, entity.y() + 1.5D - 0.08D, entity.z(), 0, 0, 0);
			for(double i = 0D; i <= 0.35D; i += 0.05D) {
				entity.getLevel().addParticle(new DustParticleOptions(((int)(0f*255)<<16)|((int)(0f*255)<<8)|(int)(0f*255), 0.5f), entity.x() - 0.175D, entity.y() + 1.5D - 0.175D + i, entity.z(), 0, 0, 0);
			}
			for(double i = 0; i <= 1; i += 0.1D) {
				entity.getLevel().addParticle(new DustParticleOptions(((int)(0f*255)<<16)|((int)(0f*255)<<8)|(int)(0f*255), 0.5f), entity.x() - 0.175D + i * vec31.x, entity.y() + 1.5D + 0.175D + i * vec31.y, entity.z(), 0, 0, 0);
				entity.getLevel().addParticle(new DustParticleOptions(((int)(0f*255)<<16)|((int)(0f*255)<<8)|(int)(0f*255), 0.5f), entity.x() - 0.175D + i * vec32.x, entity.y() + 1.5D - 0.175D + i * vec32.y, entity.z(), 0, 0, 0);
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

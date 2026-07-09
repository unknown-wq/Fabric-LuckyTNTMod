package luckytnt.tnteffects;

import java.util.List;

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
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.AABB;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;

public class StoneColdEffect extends PrimedTNTEffect {

	@Override
	public void explosionTick(IExplosiveEntity ent) {
		// TODO(port-26.2): DISABLED — advance-time-of-day. 26.2 rewrote the time system
		// (ServerClockManager/WorldClock); ServerLevel.setTimeOfDay/getTimeOfDay are gone with no
		// clean drop-in. The rest of the freezing effect is preserved.
		/*
		if(ent.getLevel() instanceof ServerLevel s_Level) {
			s_Level.setTimeOfDay(s_Level.getTimeOfDay() + 200);
		}
		*/
		for(int count = 0; count < 7; count++) {
			double offX = Math.random() * 15 - Math.random() * 15;
			double offY = Math.random() * 15 - Math.random() * 15;
			double offZ = Math.random() * 15 - Math.random() * 15;
			BlockPos pos = new BlockPos(Mth.floor(ent.x() + offX), Mth.floor(ent.y() + offY), Mth.floor(ent.z() + offZ));
			BlockState state = ent.getLevel().getBlockState(pos);
			if(state.getBlock().getExplosionResistance() < 100 && state.isCollisionShapeFullBlock(ent.getLevel(), pos) && !state.isAir()) {
				state.getBlock().wasExploded((ServerLevel)ent.getLevel(), pos, ImprovedExplosion.dummyExplosion(ent.getLevel()));
				ent.getLevel().setBlock(pos, Blocks.BLUE_ICE.defaultBlockState(), 3);
			}
		}
		ent.getLevel().playSound(null, ent.x(), ent.y(), ent.z(), SoundEvents.STONE_PLACE, SoundSource.BLOCKS, 0.5f, 1);
	}
	
	@Override
	public void serverExplosion(IExplosiveEntity ent) {
		ExplosionHelper.doSphericalExplosion(ent.getLevel(), ent.getPos(), 90, new IForEachBlockExplosionEffect() {
			
			@Override
			public void doBlockExplosion(Level level, BlockPos pos, BlockState state, double distance) {
				if(state.getBlock().getExplosionResistance() < 200 && Block.isFaceFull(state.getCollisionShape(level, pos), Direction.UP) && state != Blocks.BLUE_ICE.defaultBlockState()) {
					state.getBlock().wasExploded((ServerLevel)level, pos, ImprovedExplosion.dummyExplosion(ent.getLevel()));
					level.setBlock(pos, Blocks.BLUE_ICE.defaultBlockState(), 3);
				}
			}
		});
		
		ExplosionHelper.doSphericalExplosion(ent.getLevel(), ent.getPos(), 130, new IForEachBlockExplosionEffect() {
			
			@Override
			public void doBlockExplosion(Level level, BlockPos pos, BlockState state, double distance) {
				if(state.getBlock() == Blocks.WATER && state != Blocks.ICE.defaultBlockState()) {
					state.getBlock().wasExploded((ServerLevel)level, pos, ImprovedExplosion.dummyExplosion(ent.getLevel()));
					level.setBlock(pos, Blocks.ICE.defaultBlockState(), 3);
				}
			}
		});
		
		ExplosionHelper.doTopBlockExplosionForAll(ent.getLevel(), ent.getPos(), 130, new IForEachBlockExplosionEffect() {
			
			@Override
			public void doBlockExplosion(Level level, BlockPos pos, BlockState state, double distance) {
				if(state.getBlock().getExplosionResistance() < 100) {
					state.getBlock().wasExploded((ServerLevel)level, pos, ImprovedExplosion.dummyExplosion(ent.getLevel()));
					level.setBlock(pos, Blocks.SNOW.defaultBlockState(), 3);
				}
			}
		});
		
		List<LivingEntity> entities = ent.getLevel().getEntitiesOfClass(LivingEntity.class, new AABB(ent.x() - 90, ent.y() - 90, ent.z() - 90, ent.x() + 90, ent.y() + 90, ent.z() + 90));
		for(LivingEntity lEnt : entities) {
			lEnt.addEffect(new MobEffectInstance(MobEffects.SLOWNESS, 800, 2));
		}
	}
	
	@Override
	public void spawnParticles(IExplosiveEntity ent) {
		ent.getLevel().addParticle(new DustParticleOptions(((int)(0.2f*255)<<16)|((int)(0.9f*255)<<8)|(int)(1f*255), 1f), ent.x(), ent.y() + 1f, ent.z(), 0, 0, 0);
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

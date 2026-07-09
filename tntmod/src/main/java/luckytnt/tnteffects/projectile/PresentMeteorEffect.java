package luckytnt.tnteffects.projectile;

import java.util.Random;

import luckytnt.block.PresentBlock;
import luckytnt.config.LuckyTNTConfigValues;
import luckytnt.registry.BlockRegistry;
import luckytnt.util.BlockSurviveChecks;
import luckytntlib.util.IExplosiveEntity;
import luckytntlib.util.explosions.ExplosionHelper;
import luckytntlib.util.explosions.IForEachBlockExplosionEffect;
import luckytntlib.util.explosions.ImprovedExplosion;
import luckytntlib.util.tnteffects.PrimedTNTEffect;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.SnowBlock;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class PresentMeteorEffect extends PrimedTNTEffect {
	
	@Override
	public void serverExplosion(IExplosiveEntity entity) {
		((ServerWorld)entity.getLevel()).spawnParticles(ParticleTypes.WAX_OFF, entity.x(), entity.y() + 2, entity.z(), 500, 3f, 3f, 3f, 0f);
		Random random = new Random();
		if(LuckyTNTConfigValues.PRESENT_DROP_DESTROY_BLOCKS.get()) {
			ImprovedExplosion explosion = new ImprovedExplosion(entity.getLevel(), (Entity)entity, entity.getPos(), 40);
			explosion.doEntityExplosion(3, true);
			ExplosionHelper.doSphericalExplosion(entity.getLevel(), entity.getPos(), 40, new IForEachBlockExplosionEffect() {
				
				@Override
				public void doBlockExplosion(World level, BlockPos pos, BlockState state, double distance) {
					if(distance <= (35) && state.getBlock().getBlastResistance() <= 100) {
						state.getBlock().onDestroyedByExplosion(level, pos, explosion);
						level.setBlockState(pos, Blocks.AIR.getDefaultState(), 3);
					}
					else if(!state.isAir() && Math.random() < 0.6f && state.getBlock().getBlastResistance() <= 100) {
						state.getBlock().onDestroyedByExplosion(level, pos, explosion);
						level.setBlockState(pos, Blocks.AIR.getDefaultState(), 3);
						if(Math.random() < 0.25f) {
							level.setBlockState(pos, Math.random() < 0.5f ? Blocks.BLUE_ICE.getDefaultState() : Blocks.PACKED_ICE.getDefaultState());
						}
					}
				}
			});
		}
		ExplosionHelper.doTopBlockExplosionForAll(entity.getLevel(), entity.getPos(), 70, new IForEachBlockExplosionEffect() {
			
			@Override
			public void doBlockExplosion(World level, BlockPos pos, BlockState state, double distance) {
				if(Math.random() < 0.025f) {
					Direction dir = Direction.NORTH;
					switch(random.nextInt(4)) {
						case 1: dir = Direction.EAST; break;
						case 2: dir = Direction.SOUTH; break;
						case 3: dir = Direction.WEST; break;
					}
					level.setBlockState(pos, BlockRegistry.PRESENT.get().getDefaultState().with(PresentBlock.FACING, dir).with(PresentBlock.TYPE, random.nextInt(4)));
				}
				else if(BlockSurviveChecks.canSnowPlaceAt(state, level, pos) && (distance < 60 || Math.random() < 0.7f)) {
					level.setBlockState(pos, Blocks.SNOW.getDefaultState().with(SnowBlock.LAYERS, random.nextInt(1, 3)));
				}
			}
		});
	}
	
	@Override
	public void spawnParticles(IExplosiveEntity entity) {
		entity.getLevel().addParticle(ParticleTypes.SNOWFLAKE, entity.x(), entity.y() + 4, entity.z(), 0, 0, 0);
	}

	@Override
	public float getSize(IExplosiveEntity entity) {
		return 4;
	}

	@Override
	public BlockState getBlockState(IExplosiveEntity entity) {
		if(!entity.getPersistentData().getBoolean("has_present")) {
			NbtCompound tag = entity.getPersistentData();
			tag.putBoolean("has_present", true);
			tag.putInt("type", new Random().nextInt(4));
			entity.setPersistentData(tag);
		}
		return BlockRegistry.PRESENT.get().getDefaultState().with(PresentBlock.TYPE, entity.getPersistentData().getInt("type"));
	}
}

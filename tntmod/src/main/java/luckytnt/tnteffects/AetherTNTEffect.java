package luckytnt.tnteffects;


import org.joml.Math;
import org.joml.Vector3f;

import luckytnt.config.LuckyTNTConfigValues;
import luckytnt.event.LevelEvents;
import luckytnt.registry.BlockRegistry;
import luckytntlib.util.IExplosiveEntity;
import luckytntlib.util.explosions.ExplosionHelper;
import luckytntlib.util.explosions.IForEachBlockExplosionEffect;
import luckytntlib.util.tnteffects.PrimedTNTEffect;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.VegetationConfiguredFeatures;

public class AetherTNTEffect extends PrimedTNTEffect {

	@Override
	public void serverExplosion(IExplosiveEntity ent) {
		ExplosionHelper.doModifiedSphericalExplosion(ent.getLevel(), ent.getPos(), 100, new Vec3d(1f, 0.5f, 1f), new IForEachBlockExplosionEffect() {
			
			@Override
			public void doBlockExplosion(World level, BlockPos pos, BlockState state, double distance) {
				if(!state.isAir() && state.getBlock().getBlastResistance() <= 200 && (ent.y() - pos.getY()) <= 35) {
					if(state.isIn(BlockTags.LOGS) && state.contains(Properties.AXIS)) {
						level.setBlockState(pos.up(LuckyTNTConfigValues.ISLAND_HEIGHT.get() * 2), Blocks.DARK_OAK_LOG.getDefaultState().with(Properties.AXIS, state.get(Properties.AXIS)), 3);
					} else if(state.isIn(BlockTags.LEAVES)) {
						if(Math.random() < 0.9D) {
							level.setBlockState(pos.up(LuckyTNTConfigValues.ISLAND_HEIGHT.get() * 2), Blocks.AZALEA_LEAVES.getDefaultState(), 3);
						} else {
							level.setBlockState(pos.up(LuckyTNTConfigValues.ISLAND_HEIGHT.get() * 2), Blocks.FLOWERING_AZALEA_LEAVES.getDefaultState(), 3);
						}
					} else {
						level.setBlockState(pos.up(LuckyTNTConfigValues.ISLAND_HEIGHT.get() * 2), state, 3);
					}
				}
			}
		});
		
		for(int offX = -100; offX <= 100; offX++) {
			for(int offZ = -100; offZ <= 100; offZ++) {
				double distance = Math.sqrt(offX * offX + offZ * offZ);
				double x = ent.x() + offX;
				double z = ent.z() + offZ;
				if(distance <= 100) {
					BlockPos pos = new BlockPos(MathHelper.floor(x), LevelEvents.getTopBlock(ent.getLevel(), x, z, true), MathHelper.floor(z)).up();
					Registry<ConfiguredFeature<?, ?>> features = ent.getLevel().getRegistryManager().get(RegistryKeys.CONFIGURED_FEATURE);
					double random = Math.random();
					
					if(random > 0.1D && random <= 0.1125D) {
						features.get(VegetationConfiguredFeatures.FOREST_FLOWERS).generate((StructureWorldAccess) ent.getLevel(), ((ServerWorld) ent.getLevel()).getChunkManager().getChunkGenerator(), Random.create(), pos);
					} else if(random > 0.15D && random <= 0.1625D) {
						features.get(VegetationConfiguredFeatures.FLOWER_FLOWER_FOREST).generate((StructureWorldAccess) ent.getLevel(), ((ServerWorld) ent.getLevel()).getChunkManager().getChunkGenerator(), Random.create(), pos);
					}
				}
			}
		}
	}
	
	@Override
	public void spawnParticles(IExplosiveEntity ent) {
		if(ent.getTNTFuse() % 3 == 0) {
			for(double d = 0D; d <= 1.5D; d += 0.1D) {
				ent.getLevel().addParticle(new DustParticleEffect(new Vector3f(0.97f, 0.84f, 0.45f), 0.75f), ent.x() + 0.5D, ent.y() + 1.1D + d, ent.z(), 0, 0, 0);
				ent.getLevel().addParticle(new DustParticleEffect(new Vector3f(0.97f, 0.84f, 0.45f), 0.75f), ent.x() + 0.4D, ent.y() + 1.1D + d, ent.z(), 0, 0, 0);
				ent.getLevel().addParticle(new DustParticleEffect(new Vector3f(0.97f, 0.84f, 0.45f), 0.75f), ent.x() - 0.5D, ent.y() + 1.1D + d, ent.z(), 0, 0, 0);
				ent.getLevel().addParticle(new DustParticleEffect(new Vector3f(0.97f, 0.84f, 0.45f), 0.75f), ent.x() - 0.4D, ent.y() + 1.1D + d, ent.z(), 0, 0, 0);
			}
			for(double d = 0D; d <= 1D; d += 0.1D) {
				ent.getLevel().addParticle(new DustParticleEffect(new Vector3f(0.97f, 0.84f, 0.45f), 0.75f), ent.x() + 0.5D - d, ent.y() + 1.1D, ent.z(), 0, 0, 0);
				ent.getLevel().addParticle(new DustParticleEffect(new Vector3f(0.97f, 0.84f, 0.45f), 0.75f), ent.x() + 0.5D - d, ent.y() + 1.2D, ent.z(), 0, 0, 0);
				ent.getLevel().addParticle(new DustParticleEffect(new Vector3f(0.97f, 0.84f, 0.45f), 0.75f), ent.x() + 0.5D - d, ent.y() + 2.6D, ent.z(), 0, 0, 0);
				ent.getLevel().addParticle(new DustParticleEffect(new Vector3f(0.97f, 0.84f, 0.45f), 0.75f), ent.x() + 0.5D - d, ent.y() + 2.5D, ent.z(), 0, 0, 0);
			}
			for(double x = -0.3D; x <= 0.3D; x += 0.1D) {
				for(double y = 0.2D; y <= 1.3D; y += 0.1D) {
					ent.getLevel().addParticle(new DustParticleEffect(new Vector3f(0.31f, 0.46f, 0.86f), 0.75f), ent.x() + x + 0.05D, ent.y() + 1.1D + y, ent.z(), 0, 0, 0);
				}
			}
		}
	}
	
	@Override
	public Block getBlock() {
		return BlockRegistry.AETHER_TNT.get();
	}
	
	@Override
	public int getDefaultFuse(IExplosiveEntity ent) {
		return 200;
	}
}

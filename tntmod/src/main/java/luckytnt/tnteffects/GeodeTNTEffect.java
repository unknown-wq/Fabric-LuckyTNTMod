package luckytnt.tnteffects;

import java.util.ArrayList;
import java.util.HashMap;
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
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.UndergroundConfiguredFeatures;

public class GeodeTNTEffect extends PrimedTNTEffect{

	@Override
	public void serverExplosion(IExplosiveEntity entity) {
		HashMap<BlockPos, BlockState> blocks = new HashMap<>();
		ExplosionHelper.doSphericalExplosion(entity.getLevel(), entity.getPos(), 10, new IForEachBlockExplosionEffect() {
			
			@Override
			public void doBlockExplosion(World level, BlockPos pos, BlockState state, double distance) {
				blocks.put(pos, state);
				state.getBlock().onDestroyedByExplosion(level, pos, ImprovedExplosion.dummyExplosion(entity.getLevel()));
				level.setBlockState(pos, Blocks.STONE.getDefaultState());
			}
		});
		if(entity.getLevel() instanceof ServerWorld sLevel) {
			RegistryEntry<ConfiguredFeature<?, ?>> feature = entity.getLevel().getRegistryManager().get(RegistryKeys.CONFIGURED_FEATURE).entryOf(UndergroundConfiguredFeatures.AMETHYST_GEODE);
			feature.value().generate(sLevel, sLevel.getChunkManager().getChunkGenerator(), sLevel.random, toBlockPos(entity.getPos()));
		}
		for(int i = blocks.size() - 1; i > 0; i--) {
			List<BlockPos> poses = new ArrayList<>(blocks.keySet());
			BlockPos pos = poses.get(i);
			if(entity.getLevel().getBlockState(pos).isOf(Blocks.STONE)) {
				entity.getLevel().setBlockState(pos, blocks.get(pos));
			}
		}
	}
	
	@Override
	public void spawnParticles(IExplosiveEntity entity) {
		entity.getLevel().addParticle(new DustParticleEffect(new Vector3f(0.6f, 0.1f, 1f), 1f), entity.x(), entity.y() + 1f, entity.z(), 0, 0, 0);
		entity.getLevel().addParticle(new DustParticleEffect(new Vector3f(0.3f, 0.3f, 0.3f), 1f), entity.x(), entity.y() + 1f, entity.z(), 0, 0, 0);
	}
	
	@Override
	public Block getBlock() {
		return BlockRegistry.GEODE_TNT.get();
	}
}

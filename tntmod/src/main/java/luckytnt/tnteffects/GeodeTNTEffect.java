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
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.core.particles.DustParticleEffect;
import net.minecraft.core.registries.Registries;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.UndergroundConfiguredFeatures;

public class GeodeTNTEffect extends PrimedTNTEffect{

	@Override
	public void serverExplosion(IExplosiveEntity entity) {
		HashMap<BlockPos, BlockState> blocks = new HashMap<>();
		ExplosionHelper.doSphericalExplosion(entity.getLevel(), entity.getPos(), 10, new IForEachBlockExplosionEffect() {
			
			@Override
			public void doBlockExplosion(Level level, BlockPos pos, BlockState state, double distance) {
				blocks.put(pos, state);
				state.getBlock().onDestroyedByExplosion(level, pos, ImprovedExplosion.dummyExplosion(entity.getLevel()));
				level.setBlockState(pos, Blocks.STONE.getDefaultState());
			}
		});
		if(entity.getLevel() instanceof ServerLevel sLevel) {
			RegistryEntry<ConfiguredFeature<?, ?>> feature = entity.getLevel().getRegistryManager().get(Registries.CONFIGURED_FEATURE).entryOf(UndergroundConfiguredFeatures.AMETHYST_GEODE);
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

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
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.registries.Registries;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.data.worldgen.features.CaveFeatures;

public class GeodeTNTEffect extends PrimedTNTEffect{

	@Override
	public void serverExplosion(IExplosiveEntity entity) {
		HashMap<BlockPos, BlockState> blocks = new HashMap<>();
		ExplosionHelper.doSphericalExplosion(entity.getLevel(), entity.getPos(), 10, new IForEachBlockExplosionEffect() {
			
			@Override
			public void doBlockExplosion(Level level, BlockPos pos, BlockState state, double distance) {
				blocks.put(pos, state);
				state.getBlock().wasExploded((ServerLevel)level, pos, ImprovedExplosion.dummyExplosion(entity.getLevel()));
				level.setBlockAndUpdate(pos, Blocks.STONE.defaultBlockState());
			}
		});
		if(entity.getLevel() instanceof ServerLevel sLevel) {
			Holder<ConfiguredFeature<?, ?>> feature = entity.getLevel().registryAccess().lookupOrThrow(Registries.CONFIGURED_FEATURE).getOrThrow(CaveFeatures.AMETHYST_GEODE);
			feature.value().place(sLevel, sLevel.getChunkSource().getChunkSource().getGenerator(), sLevel.getRandom(), toBlockPos(entity.getPos()));
		}
		for(int i = blocks.size() - 1; i > 0; i--) {
			List<BlockPos> poses = new ArrayList<>(blocks.keySet());
			BlockPos pos = poses.get(i);
			if(entity.getLevel().getBlockState(pos).is(Blocks.STONE)) {
				entity.getLevel().setBlockAndUpdate(pos, blocks.get(pos));
			}
		}
	}
	
	@Override
	public void spawnParticles(IExplosiveEntity entity) {
		entity.getLevel().addParticle(new DustParticleOptions(((int)(0.6f*255)<<16)|((int)(0.1f*255)<<8)|(int)(1f*255), 1f), entity.x(), entity.y() + 1f, entity.z(), 0, 0, 0);
		entity.getLevel().addParticle(new DustParticleOptions(((int)(0.3f*255)<<16)|((int)(0.3f*255)<<8)|(int)(0.3f*255), 1f), entity.x(), entity.y() + 1f, entity.z(), 0, 0, 0);
	}
	
	@Override
	public Block getBlock() {
		return BlockRegistry.GEODE_TNT.get();
	}
}

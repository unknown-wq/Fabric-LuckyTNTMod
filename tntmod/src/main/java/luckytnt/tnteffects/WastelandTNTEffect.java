package luckytnt.tnteffects;

import java.util.List;

import luckytnt.registry.BlockRegistry;
import luckytnt.util.Materials;
import luckytntlib.util.IExplosiveEntity;
import luckytntlib.util.tnteffects.PrimedTNTEffect;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FluidBlock;
import net.minecraft.block.WetSpongeBlock;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;

public class WastelandTNTEffect extends PrimedTNTEffect {
	
	public static List<Block> GRASS = List.of(Blocks.GRASS_BLOCK, Blocks.PODZOL, Blocks.MYCELIUM, Blocks.MUD, Blocks.MUDDY_MANGROVE_ROOTS);
	public static List<Block> DIRT = List.of(Blocks.DIRT, Blocks.COARSE_DIRT, Blocks.ROOTED_DIRT);

	@Override
	public void serverExplosion(IExplosiveEntity ent) {
		doVaporizeExplosion(ent, 75, true);
	}
	
	@Override
	public void spawnParticles(IExplosiveEntity ent) {
		for (int count = 0; count < 100; count++) {
			ent.getLevel().addParticle(ParticleTypes.CLOUD, true, ent.x() + Math.random() * 30 - Math.random() * 30, ent.y() + 0.5f, ent.z() + Math.random() * 30 - Math.random() * 30, 0, 0, 0);
		}
	}
	
	@Override
	public Block getBlock() {
		return BlockRegistry.WASTELAND_TNT.get();
	}
	
	@Override
	public int getDefaultFuse(IExplosiveEntity ent) {
		return 180;
	}
	
	public static void doVaporizeExplosion(IExplosiveEntity ent, double radius, boolean dryArea) {
		if(!ent.getLevel().isClient()) {
			for(double offX = -radius; offX <= radius; offX++) {
				for(double offY = -radius; offY <= radius; offY++) {
					for(double offZ = -radius; offZ <= radius; offZ++) {
						double distance = Math.sqrt(offX * offX + offY * offY + offZ * offZ);
						BlockPos pos = new BlockPos(MathHelper.floor(ent.x() + offX), MathHelper.floor(ent.y() + offY), MathHelper.floor(ent.z() + offZ));
						BlockState state = ent.getLevel().getBlockState(pos);
						
						if(distance <= radius) {
							if(state.getBlock() instanceof FluidBlock || Materials.isWaterPlant(state) || state.isOf(Blocks.BUBBLE_COLUMN)) {
								ent.getLevel().setBlockState(pos, Blocks.AIR.getDefaultState(), 3);
							}
							if(state.contains(Properties.WATERLOGGED) && state.get(Properties.WATERLOGGED)) {
								ent.getLevel().setBlockState(pos, state.with(Properties.WATERLOGGED, false), 3);
							}
							if(dryArea) {
								if(Materials.isPlant(state)) {
									if(Blocks.DEAD_BUSH.getDefaultState().canPlaceAt(ent.getLevel(), pos)) {
										ent.getLevel().setBlockState(pos, Blocks.DEAD_BUSH.getDefaultState(), 3);
									}
								}
								if(GRASS.contains(state.getBlock())) {
									ent.getLevel().setBlockState(pos, Blocks.DIRT.getDefaultState(), 3);
								} else if(DIRT.contains(state.getBlock())) {
									ent.getLevel().setBlockState(pos, Blocks.SAND.getDefaultState(), 3);
								} else if(state.isIn(BlockTags.WOOL)) {
									ent.getLevel().setBlockState(pos, Blocks.WHITE_WOOL.getDefaultState(), 3);
								} else if(state.getBlock() instanceof WetSpongeBlock) {
									ent.getLevel().setBlockState(pos, Blocks.SPONGE.getDefaultState(), 3);
								} else if(state.isIn(BlockTags.ICE) || state.isIn(BlockTags.SNOW) || state.isIn(BlockTags.LEAVES)) {
									ent.getLevel().setBlockState(pos, Blocks.AIR.getDefaultState(), 3);
								}
							}
						}
					}
				}
			}
		}
	}
}

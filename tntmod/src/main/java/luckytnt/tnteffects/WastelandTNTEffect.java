package luckytnt.tnteffects;

import java.util.List;

import luckytnt.registry.BlockRegistry;
import luckytnt.util.Materials;
import luckytntlib.util.IExplosiveEntity;
import luckytntlib.util.tnteffects.PrimedTNTEffect;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.WetSpongeBlock;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;

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
			ent.getLevel().addParticle(ParticleTypes.CLOUD, ent.x() + Math.random() * 30 - Math.random() * 30, ent.y() + 0.5f, ent.z() + Math.random() * 30 - Math.random() * 30, 0, 0, 0);
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
		//loop invariants: the level (it was resolved up to nine times per block) and every constant block state
		final Level level = ent.getLevel();
		if(level.isClientSide()) {
			return;
		}
		final double centerX = ent.x();
		final double centerY = ent.y();
		final double centerZ = ent.z();
		final BlockState air = Blocks.AIR.defaultBlockState();
		final BlockState deadBush = Blocks.DEAD_BUSH.defaultBlockState();
		final BlockState dirt = Blocks.DIRT.defaultBlockState();
		final BlockState sand = Blocks.SAND.defaultBlockState();
		final BlockState whiteWool = Blocks.WOOL.white().defaultBlockState();
		final BlockState sponge = Blocks.SPONGE.defaultBlockState();
		//conservative column cull: a small tolerance guarantees no column that could still contain an
		//accepted block is skipped, every surviving block is still tested with the exact original check
		final double cullSqr = radius * radius * 1.000001d + 1d;
		for(double offX = -radius; offX <= radius; offX++) {
			final double xSqr = offX * offX;
			if(xSqr > cullSqr) {
				continue;
			}
			for(double offY = -radius; offY <= radius; offY++) {
				final double xySqr = xSqr + offY * offY;
				if(xySqr > cullSqr) {
					continue;
				}
				//the x/y components of the position no longer depend on the inner loop
				final int x = Mth.floor(centerX + offX);
				final int y = Mth.floor(centerY + offY);
				for(double offZ = -radius; offZ <= radius; offZ++) {
					//reject on the distance before allocating a BlockPos and reading the world
					if(Math.sqrt(xySqr + offZ * offZ) > radius) {
						continue;
					}
					BlockPos pos = new BlockPos(x, y, Mth.floor(centerZ + offZ));
					BlockState state = level.getBlockState(pos);
					//no branch below can ever match air (no liquid, no water plant, no waterlogged
					//property, not a plant, not in GRASS/DIRT/WOOL/ICE/SNOW/LEAVES), so bail out early
					if(state.isAir()) {
						continue;
					}

					if(state.getBlock() instanceof LiquidBlock || Materials.isWaterPlant(state) || state.is(Blocks.BUBBLE_COLUMN)) {
						level.setBlock(pos, air, 3);
					}
					if(state.hasProperty(BlockStateProperties.WATERLOGGED) && state.getValue(BlockStateProperties.WATERLOGGED)) {
						level.setBlock(pos, state.setValue(BlockStateProperties.WATERLOGGED, false), 3);
					}
					if(dryArea) {
						//canSurvive is the expensive test here, so it stays behind the cheap isPlant check
						if(Materials.isPlant(state) && deadBush.canSurvive(level, pos)) {
							level.setBlock(pos, deadBush, 3);
						}
						if(GRASS.contains(state.getBlock())) {
							level.setBlock(pos, dirt, 3);
						} else if(DIRT.contains(state.getBlock())) {
							level.setBlock(pos, sand, 3);
						} else if(state.is(BlockTags.WOOL)) {
							level.setBlock(pos, whiteWool, 3);
						} else if(state.getBlock() instanceof WetSpongeBlock) {
							level.setBlock(pos, sponge, 3);
						} else if(state.is(BlockTags.ICE) || state.is(BlockTags.SNOW) || state.is(BlockTags.LEAVES)) {
							level.setBlock(pos, air, 3);
						}
					}
				}
			}
		}
	}
}

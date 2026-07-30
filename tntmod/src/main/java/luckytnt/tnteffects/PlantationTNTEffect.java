package luckytnt.tnteffects;

import net.minecraft.server.level.ServerLevel;

import org.joml.Vector3f;

import luckytnt.event.LevelEvents;
import luckytnt.registry.BlockRegistry;
import luckytnt.util.Materials;
import luckytntlib.util.IExplosiveEntity;
import luckytntlib.util.explosions.ExplosionHelper;
import luckytntlib.util.explosions.IForEachBlockExplosionEffect;
import luckytntlib.util.explosions.ImprovedExplosion;
import luckytntlib.util.tnteffects.PrimedTNTEffect;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FarmlandBlock;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.GrassBlock;
import net.minecraft.world.level.block.MyceliumBlock;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.Heightmap;

public class PlantationTNTEffect extends PrimedTNTEffect {

	@Override
	public void serverExplosion(IExplosiveEntity ent) {
		Level level = ent.getLevel();
		if(!(level instanceof ServerLevel sLevel)) {
			return;
		}
		final ImprovedExplosion dummy = ImprovedExplosion.dummyExplosion(level);
		final BlockState air = Blocks.AIR.defaultBlockState();
		ExplosionHelper.doSphericalExplosion(level, ent.getPos(), 41, new IForEachBlockExplosionEffect() {

			@Override
			public void doBlockExplosion(Level level, BlockPos pos, BlockState state, double distance) {
				Block block = state.getBlock();
				if(block.getExplosionResistance() <= 200) {
					if((!state.isCollisionShapeFullBlock(level, pos) || state.is(Blocks.FIRE) || state.is(Blocks.SOUL_FIRE)
					|| state.is(BlockTags.LEAVES) || Materials.isPlant(state) || state.is(BlockTags.SNOW)
					|| Materials.isWood(state)) && !(block instanceof GrassBlock) && !(block instanceof MyceliumBlock))
					{
						block.wasExploded(sLevel, pos, dummy);
						level.setBlock(pos, air, 3);
					}
				}
			}
		});

		final BlockState grass = Blocks.GRASS_BLOCK.defaultBlockState();
		int baseX = Mth.floor(ent.x());
		int baseY = Mth.floor(ent.y());
		int baseZ = Mth.floor(ent.z());
		BlockPos.MutableBlockPos mutable = new BlockPos.MutableBlockPos();
		BlockPos.MutableBlockPos mutableUp = new BlockPos.MutableBlockPos();

		for(int offX = -42; offX <= 42; offX++) {
			int dx2 = offX * offX;
			if(dx2 > 1764) {
				continue;
			}
			int posX = baseX + offX;
			for(int offZ = -42; offZ <= 42; offZ++) {
				if(dx2 + offZ * offZ > 1764) {
					continue;
				}
				int posZ = baseZ + offZ;
				if(!level.hasChunk(posX >> 4, posZ >> 4)) {
					continue;
				}
				int y = LevelEvents.getTopBlock(level, ent.x() + offX, ent.z() + offZ, true);
				BlockPos pos = mutable.set(posX, y, posZ).immutable();
				level.getBlockState(pos).getBlock().wasExploded(sLevel, pos, dummy);
				level.setBlock(pos, grass, 3);
			}
		}

		BlockPos posBelow = toBlockPos(ent.getPos()).below();
		placeWater(posBelow, ent);

		// Columns outside r=41 fell through every distance band without ever placing anything, so they
		// are skipped outright; and the surface scan now stops at the first block found instead of
		// running all 384 y levels with the flag merely disabling the body.
		// It also *starts* at the game's own WORLD_SURFACE heightmap instead of at ent.y() + 320, which
		// is ~320 blocks above the terrain and usually above the world ceiling: the loop body can only
		// ever fire on a block with a full collision shape, and every such block is by definition at or
		// below the highest non air block of its column, so the skipped prefix was guaranteed air.
		// 5 281 columns x 384 steps = 2.03M getBlockState -> ~5 281 x a handful = ~20k.
		for(int offX = -41; offX <= 41; offX++) {
			int dx2 = offX * offX;
			if(dx2 > 1681) {
				continue;
			}
			int posX = baseX + offX;
			for(int offZ = -41; offZ <= 41; offZ++) {
				int d2 = dx2 + offZ * offZ;
				if(d2 > 1681) {
					continue;
				}
				int posZ = baseZ + offZ;
				if(!level.hasChunk(posX >> 4, posZ >> 4)) {
					continue;
				}
				double distance = Math.sqrt(d2);
				int startOff = Math.min(320, level.getHeight(Heightmap.Types.WORLD_SURFACE, posX, posZ) - 1 - baseY);
				BlockState stateUp = level.getBlockState(mutable.set(posX, baseY + startOff + 1, posZ));
				for(int offY = startOff; offY > -64; offY--) {
					int posY = baseY + offY;
					mutable.set(posX, posY, posZ);
					BlockState state = level.getBlockState(mutable);
					mutableUp.set(posX, posY + 1, posZ);
					BlockState stateUpper = stateUp;
					stateUp = state;

					if(state.getBlock().getExplosionResistance() < 200 && stateUpper.getBlock().getExplosionResistance() < 200) {
						if(state.isCollisionShapeFullBlock(level, mutable) && !stateUpper.isCollisionShapeFullBlock(level, mutableUp) && !state.is(BlockTags.LEAVES) && !stateUpper.is(Blocks.WATER) && !stateUpper.is(Blocks.LAVA)) {
							BlockPos pos = mutable.immutable();
							if(distance > 40 && distance <= 41) {
								placeCropsAndFarmland(pos, true, ent);
							} else if(distance > 39 && distance <= 40) {
								placeWater(pos, ent);
							} else if(distance > 32 && distance <= 39) {
								placeCropsAndFarmland(pos, false, ent);
							} else if(distance > 31 && distance <= 32) {
								placeWater(pos, ent);
							} else if(distance > 24 && distance <= 31) {
								placeCropsAndFarmland(pos, false, ent);
							} else if(distance > 23 && distance <= 24) {
								placeWater(pos, ent);
							} else if(distance > 16 && distance <= 23) {
								placeCropsAndFarmland(pos, false, ent);
							} else if(distance > 15 && distance <= 16) {
								placeWater(pos, ent);
							} else if(distance > 8 && distance <= 15) {
								placeCropsAndFarmland(pos, false, ent);
							} else if(distance > 7 && distance <= 8) {
								placeWater(pos, ent);
							} else if(distance <= 7) {
								placeCropsAndFarmland(pos, false, ent);
							}
							break;
						}
					}
				}
			}
		}
	}

	@Override
	public void spawnParticles(IExplosiveEntity ent) {
		ent.getLevel().addParticle(new DustParticleOptions(((int)(1f*255)<<16)|((int)(0.5f*255)<<8)|(int)(0.1f*255), 1f), ent.x(), ent.y() + 1f, ent.z(), 0, 0, 0);
	}

	@Override
	public Block getBlock() {
		return BlockRegistry.PLANTATION_TNT.get();
	}

	@Override
	public int getDefaultFuse(IExplosiveEntity ent) {
		return 160;
	}

	public void placeCropsAndFarmland(BlockPos pos, boolean melonOrPumpkin, IExplosiveEntity ent) {
		Level level = ent.getLevel();
		ServerLevel sLevel = (ServerLevel)level;
		// up to two `new Random()` per crop (each hitting the global seed uniquifier CAS) replaced by the
		// level's own RandomSource; identical distributions, thousands of allocations fewer per detonation
		RandomSource random = level.getRandom();
		ImprovedExplosion dummy = ImprovedExplosion.dummyExplosion(level);
		BlockState crop;
		if(!melonOrPumpkin) {
			level.getBlockState(pos).getBlock().wasExploded(sLevel, pos, dummy);
			level.setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
			int rand = random.nextInt(4);
			crop = Blocks.POTATOES.defaultBlockState();
			switch(rand) {
				case 0: crop = Blocks.CARROTS.defaultBlockState().setValue(BlockStateProperties.AGE_7, random.nextInt(8)); break;
				case 1: crop = Blocks.POTATOES.defaultBlockState().setValue(BlockStateProperties.AGE_7, random.nextInt(8)); break;
				case 2: crop = Blocks.WHEAT.defaultBlockState().setValue(BlockStateProperties.AGE_7, random.nextInt(8)); break;
				case 3: crop = Blocks.BEETROOTS.defaultBlockState().setValue(BlockStateProperties.AGE_3, random.nextInt(4)); break;
			}
		} else {
			level.getBlockState(pos).getBlock().wasExploded(sLevel, pos, dummy);
			level.setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
			int rand = random.nextInt(2);
			crop = Blocks.POTATOES.defaultBlockState();
			switch(rand) {
				case 0: crop = Blocks.PUMPKIN_STEM.defaultBlockState().setValue(BlockStateProperties.AGE_7, random.nextInt(8)); break;
				case 1: crop = Blocks.MELON_STEM.defaultBlockState().setValue(BlockStateProperties.AGE_7, random.nextInt(8)); break;
			}
		}
		level.setBlock(pos, Blocks.FARMLAND.defaultBlockState().setValue(FarmlandBlock.MOISTURE, 7), 3);
		// pos.above() used to be rebuilt three times and read twice, with nothing mutating the world in between.
		BlockPos above = pos.above();
		BlockState aboveState = level.getBlockState(above);
		if(!aboveState.isCollisionShapeFullBlock(level, above) && !(aboveState.getBlock() instanceof FarmlandBlock)) {
			level.setBlock(above, crop, 3);
		}
	}

	/**
	 * Note: the four neighbour tests must be re-read after the placeCropsAndFarmland calls above them,
	 * because those calls turn the neighbour into farmland and that feeds the second condition. Only the
	 * three duplicate reads *inside* each individual condition are collapsed here (24 reads -> 8).
	 */
	public void placeWater(BlockPos pos, IExplosiveEntity ent) {
		Level level = ent.getLevel();
		ServerLevel sLevel = (ServerLevel)level;
		ImprovedExplosion dummy = ImprovedExplosion.dummyExplosion(level);
		BlockPos north = pos.north();
		BlockPos south = pos.south();
		BlockPos east = pos.east();
		BlockPos west = pos.west();

		BlockState state = level.getBlockState(north);
		if(!state.isCollisionShapeFullBlock(level, north) && !(state.getBlock() instanceof FarmlandBlock) && !(state.getBlock() instanceof LiquidBlock)) {
			placeCropsAndFarmland(north, false, ent);
		}
		state = level.getBlockState(south);
		if(!state.isCollisionShapeFullBlock(level, south) && !(state.getBlock() instanceof FarmlandBlock) && !(state.getBlock() instanceof LiquidBlock)) {
			placeCropsAndFarmland(south, false, ent);
		}
		state = level.getBlockState(east);
		if(!state.isCollisionShapeFullBlock(level, east) && !(state.getBlock() instanceof FarmlandBlock) && !(state.getBlock() instanceof LiquidBlock)) {
			placeCropsAndFarmland(east, false, ent);
		}
		state = level.getBlockState(west);
		if(!state.isCollisionShapeFullBlock(level, west) && !(state.getBlock() instanceof FarmlandBlock) && !(state.getBlock() instanceof LiquidBlock)) {
			placeCropsAndFarmland(west, false, ent);
		}

		BlockState northState = level.getBlockState(north);
		BlockState southState = level.getBlockState(south);
		BlockState eastState = level.getBlockState(east);
		BlockState westState = level.getBlockState(west);
		if((northState.isCollisionShapeFullBlock(level, north) || northState.getBlock() instanceof FarmlandBlock || northState.getBlock() instanceof LiquidBlock)
			&& (southState.isCollisionShapeFullBlock(level, south) || southState.getBlock() instanceof FarmlandBlock || southState.getBlock() instanceof LiquidBlock)
			&& (eastState.isCollisionShapeFullBlock(level, east) || eastState.getBlock() instanceof FarmlandBlock || eastState.getBlock() instanceof LiquidBlock)
			&& (westState.isCollisionShapeFullBlock(level, west) || westState.getBlock() instanceof FarmlandBlock || westState.getBlock() instanceof LiquidBlock))
		{
			level.getBlockState(pos).getBlock().wasExploded(sLevel, pos, dummy);
			level.setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
			level.setBlock(pos, Blocks.WATER.defaultBlockState(), 3);
			BlockPos below = pos.below();
			BlockState belowState = level.getBlockState(below);
			if(!belowState.isCollisionShapeFullBlock(level, below)) {
				belowState.getBlock().wasExploded(sLevel, below, dummy);
				level.setBlock(below, Blocks.AIR.defaultBlockState(), 3);
				level.setBlock(below, Blocks.DIRT.defaultBlockState(), 3);
			}
		} else {
			placeCropsAndFarmland(pos, false, ent);
		}
	}
}

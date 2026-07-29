package luckytnt.feature;

import java.time.LocalDate;
import java.time.temporal.ChronoField;

import com.mojang.serialization.Codec;

import luckytnt.config.LuckyTNTConfigValues;
import luckytnt.registry.BlockRegistry;
import net.fabricmc.fabric.api.tag.convention.v2.ConventionalBiomeTags;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.state.properties.SlabType;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;

public class Altar extends Feature<NoneFeatureConfiguration>{

	public static final BlockState DEFAULT_GROUND = Blocks.GRASS_BLOCK.defaultBlockState();

	//LocalDate.now() resolves the system clock and the time zone rules on every call; place() runs on the
	//parallel worldgen threads, so the seasonal check is cached and refreshed at most once a minute.
	private static volatile long seasonCheckedAt = 0L;
	private static volatile boolean seasonActive = false;

	private static boolean isSeasonActive() {
		long now = System.currentTimeMillis();
		if(now - seasonCheckedAt >= 60000L) {
			seasonActive = LocalDate.now().get(ChronoField.MONTH_OF_YEAR) == 10;
			seasonCheckedAt = now;
		}
		return seasonActive;
	}

	public BlockState slab = Blocks.STONE_BRICK_SLAB.defaultBlockState().setValue(SlabBlock.TYPE, SlabType.BOTTOM);
	public BlockState mossySlab = Blocks.MOSSY_STONE_BRICK_SLAB.defaultBlockState().setValue(SlabBlock.TYPE, SlabType.BOTTOM);

	public BlockState wall = Blocks.STONE_BRICK_WALL.defaultBlockState();
	public BlockState mossyWall = Blocks.MOSSY_STONE_BRICK_WALL.defaultBlockState();

	public Altar(Codec<NoneFeatureConfiguration> codec) {
		super(codec);
	}

	public BlockState randomBrick(RandomSource random) {
		double value = random.nextDouble();
		if(value < 0.5D) {
			return Blocks.STONE_BRICKS.defaultBlockState();
		} else if(value >= 0.5D && value < 0.8D) {
			return Blocks.MOSSY_STONE_BRICKS.defaultBlockState();
		} else if(value >= 0.8D) {
			return Blocks.CRACKED_STONE_BRICKS.defaultBlockState();
		}

		return Blocks.STONE_BRICKS.defaultBlockState();
	}

	public BlockState randomTNT(RandomSource random) {
		double value = random.nextDouble();
		if(value < 0.4D) {
			return BlockRegistry.PUMPKIN_BOMB.get().defaultBlockState();
		} else if(value >= 0.4D && value < 0.8D) {
			return BlockRegistry.ZOMBIE_APOCALYPSE.get().defaultBlockState();
		} else if(value >= 0.8D) {
			return BlockRegistry.GRAVEYARD_TNT.get().defaultBlockState();
		}

		return BlockRegistry.PUMPKIN_BOMB.get().defaultBlockState();
	}

	public BlockState randomSlab(RandomSource random) {
		return random.nextDouble() > 0.4D ? slab : mossySlab;
	}

	@Override
	public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> ctx) {
		if(!LuckyTNTConfigValues.SEASON_EVENTS_ALWAYS_ACTIVE.get().booleanValue() && !isSeasonActive()) {
			return false;
		}

		WorldGenLevel level = ctx.level();
		BlockPos pos = ctx.origin();
		RandomSource random = level.getRandom();

		//local, not a field: Feature instances are registry singletons shared by all parallel worldgen threads
		BlockState ground = DEFAULT_GROUND;
		if(level.getBiome(pos).is(ConventionalBiomeTags.IS_MUSHROOM)) {
			ground = Blocks.MYCELIUM.defaultBlockState();
		}

		for(int offX = -3; offX <= 3; offX++) {
			for(int offY = -2; offY <= -1; offY++) {
				for(int offZ = -3; offZ <= 3; offZ++) {
					if(level.getBlockState(pos.offset(offX, offY, offZ)).isAir()) {
						if(offY == -2) {
							level.setBlock(pos.offset(offX, offY, offZ), Blocks.DIRT.defaultBlockState(), 3);
						} else {
							level.setBlock(pos.offset(offX, offY, offZ), ground, 3);
						}
					}
				}
			}
		}

		for(int offX = -1; offX <= 1; offX++) {
			for(int offZ = -1; offZ <= 1; offZ++) {
				level.setBlock(pos.offset(offX, 0, offZ), randomBrick(random), 3);
			}
		}

		for(int offX = -1; offX <= 1; offX++) {
			level.setBlock(pos.offset(offX, 0, 2), randomSlab(random), 3);
			level.setBlock(pos.offset(offX, 0, -2), randomSlab(random), 3);
		}

		for(int offZ = -1; offZ <= 1; offZ++) {
			level.setBlock(pos.offset(2, 0, offZ), randomSlab(random), 3);
			level.setBlock(pos.offset(-2, 0, offZ), randomSlab(random), 3);
		}

		level.setBlock(pos.offset(1, 1, 0), randomSlab(random), 3);
		level.setBlock(pos.offset(0, 1, -1), randomSlab(random), 3);
		level.setBlock(pos.offset(1, 1, -1), random.nextDouble() > 0.4D ? wall : mossyWall, 3);
		level.setBlock(pos.offset(1, 2, -1), randomSlab(random), 3);

		for(int offY = 1; offY <= 3; offY++) {
			if(offY <= 2) {
				level.setBlock(pos.offset(1, offY, 1), random.nextDouble() > 0.4D ? wall : mossyWall, 3);
				level.setBlock(pos.offset(-1, offY, 1), random.nextDouble() > 0.4D ? wall : mossyWall, 3);
				level.setBlock(pos.offset(-1, offY, -1), random.nextDouble() > 0.4D ? wall : mossyWall, 3);
			} else {
				level.setBlock(pos.offset(1, offY, 1), random.nextDouble() > 0.4D ? wall : mossyWall, 3);
				level.setBlock(pos.offset(-1, offY, 1), randomSlab(random), 3);
				level.setBlock(pos.offset(-1, offY, -1), random.nextDouble() > 0.4D ? wall : mossyWall, 3);

				level.setBlock(pos.offset(0, offY, 1), randomSlab(random).setValue(SlabBlock.TYPE, SlabType.TOP), 3);
				level.setBlock(pos.offset(-1, offY, 0), randomSlab(random).setValue(SlabBlock.TYPE, SlabType.TOP), 3);
			}
		}

		level.setBlock(pos.offset(0, 4, 0), randomBrick(random), 3);
		level.setBlock(pos.offset(1, 4, 1), randomSlab(random), 3);
		level.setBlock(pos.offset(-1, 4, -1), randomSlab(random), 3);

		level.setBlock(pos.offset(0, 1, 0), randomTNT(random), 3);


		return false;
	}
}

package luckytnt.tnteffects;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

import luckytnt.block.StructureTNTBlock;
import luckytnt.registry.BlockRegistry;
import luckytnt.util.StructureStates;
import luckytntlib.util.IExplosiveEntity;
import luckytntlib.util.tnteffects.PrimedTNTEffect;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.Entity;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.structure.BastionRemnantGenerator;
import net.minecraft.structure.DesertTempleGenerator;
import net.minecraft.structure.OceanMonumentGenerator;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import net.minecraft.structure.StructurePiecesCollector;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;
import net.minecraft.structure.pool.StructurePool;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.YOffset;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.gen.heightprovider.ConstantHeightProvider;
import net.minecraft.world.gen.noise.NoiseConfig;
import net.minecraft.world.level.levelgen.structure.structures.DesertPyramidStructure;
import net.minecraft.world.level.levelgen.structure.structures.JigsawStructure;
import net.minecraft.world.level.levelgen.structure.structures.OceanMonumentStructure;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.gen.structure.StructureKeys;

public class StructureTNTEffect extends PrimedTNTEffect {
	
	public Predicate<Holder<Biome>> PREDICATE = (holder) -> {
		return true;
	};

	@Override
	public void serverExplosion(IExplosiveEntity ent) {
		String value = ent.getPersistentData().getString("structure");
		if(ent.getLevel() instanceof ServerLevel sLevel) {
			DynamicRegistryManager rAccess = sLevel.registryAccess();
			ChunkGenerator chunkGenerator = sLevel.getChunkSource().getChunkGenerator();
			BiomeSource biomeSource = sLevel.getChunkSource().getChunkGenerator().getBiomeSource();
			StructureTemplateManager sManager = sLevel.getStructureManager();
			StructureAccessor sFManager = sLevel.getStructureAccessor();
			BlockBox bb = new BlockBox((int)ent.x() - 150, (int)ent.y() - 150, (int)ent.z() - 150, (int)ent.x() + 150, (int)ent.y() + 150, (int)ent.z() + 150);
			ChunkPos chunkPosition = ((Entity)ent).getChunkPos();
			Random random = sLevel.getRandom();
			NoiseConfig randomState = sLevel.getChunkSource().getNoiseConfig();
			
			Registry<Structure> registry = rAccess.get(Registries.STRUCTURE);
			Registry<StructurePool> pools = rAccess.get(Registries.TEMPLATE_POOL);

			Holder<StructurePool> pool = pools.entryOf(BastionRemnantGenerator.STRUCTURE_POOLS);
			
			Structure pillager_outpost = registry.get(StructureKeys.PILLAGER_OUTPOST);
			Structure mansion = registry.get(StructureKeys.MANSION);
			Structure jungle_pyramid = registry.get(StructureKeys.JUNGLE_PYRAMID);
			Structure desert_pyramid = new DesertPyramid(null);
			Structure stronghold = registry.get(StructureKeys.STRONGHOLD);
			Structure monument = new Monument(null);
			Structure fortress = registry.get(StructureKeys.FORTRESS);
			Structure end_city = registry.get(StructureKeys.END_CITY);
			Structure bastion = new JigsawStructure(null, pool, 6, ConstantHeightProvider.create(YOffset.fixed((int)ent.y())), false);
			Structure village_plains = registry.get(StructureKeys.VILLAGE_PLAINS);
			Structure village_desert = registry.get(StructureKeys.VILLAGE_DESERT);
			Structure village_savanna = registry.get(StructureKeys.VILLAGE_SAVANNA);
			Structure village_snowy = registry.get(StructureKeys.VILLAGE_SNOWY);
			Structure village_taiga = registry.get(StructureKeys.VILLAGE_TAIGA);
			
			if(value.equals("pillager_outpost")) {
				StructureStart start = pillager_outpost.createStructureStart(rAccess, chunkGenerator, biomeSource, randomState, sManager, sLevel.getSeed(), chunkPosition, 20, sLevel, PREDICATE);
				start.place(sLevel, sFManager, chunkGenerator, random, bb, chunkPosition);
			}
			else if(value.equals("mansion")) {
				StructureStart start = mansion.createStructureStart(rAccess, chunkGenerator, biomeSource, randomState, sManager, sLevel.getSeed(), chunkPosition, 20, sLevel, PREDICATE);
				start.place(sLevel, sFManager, chunkGenerator, random, bb, chunkPosition);
			}
			else if(value.equals("jungle_pyramid")) {
				StructureStart start = jungle_pyramid.createStructureStart(rAccess, chunkGenerator, biomeSource, randomState, sManager, sLevel.getSeed(), chunkPosition, 20, sLevel, PREDICATE);
				start.place(sLevel, sFManager, chunkGenerator, random, bb, chunkPosition);
			}
			else if(value.equals("desert_pyramid")) {
				StructureStart start = desert_pyramid.createStructureStart(rAccess, chunkGenerator, biomeSource, randomState, sManager, sLevel.getSeed(), chunkPosition, 20, sLevel, PREDICATE);
				start.place(sLevel, sFManager, chunkGenerator, random, bb, chunkPosition);
			}
			else if(value.equals("stronghold")) {
				StructureStart start = stronghold.createStructureStart(rAccess, chunkGenerator, biomeSource, randomState, sManager, sLevel.getSeed(), chunkPosition, 20, sLevel, PREDICATE);
				start.place(sLevel, sFManager, chunkGenerator, random, bb, chunkPosition);
			}
			else if(value.equals("monument")) {
				StructureStart start = monument.createStructureStart(rAccess, chunkGenerator, biomeSource, randomState, sManager, sLevel.getSeed(), chunkPosition, 20, sLevel, PREDICATE);
				start.place(sLevel, sFManager, chunkGenerator, random, bb, chunkPosition);
			}
			else if(value.equals("fortress")) {
				StructureStart start = fortress.createStructureStart(rAccess, chunkGenerator, biomeSource, randomState, sManager, sLevel.getSeed(), chunkPosition, 20, sLevel, PREDICATE);
				start.place(sLevel, sFManager, chunkGenerator, random, bb, chunkPosition);
			}
			else if(value.equals("end_city")) {
				StructureStart start = end_city.createStructureStart(rAccess, chunkGenerator, biomeSource, randomState, sManager, sLevel.getSeed(), chunkPosition, 20, sLevel, PREDICATE);
				start.place(sLevel, sFManager, chunkGenerator, random, bb, chunkPosition);
			}
			else if(value.equals("bastion")) {
				StructureStart start = bastion.createStructureStart(rAccess, chunkGenerator, biomeSource, randomState, sManager, sLevel.getSeed(), chunkPosition, 20, sLevel, PREDICATE);
				start.place(sLevel, sFManager, chunkGenerator, random, bb, chunkPosition);
			}
			else if(value.equals("village_plains")) {
				JungleTNTEffect.replaceNonSolidBlockOrVegetationWithAir(ent, 100, 10, true);
				StructureStart start = village_plains.createStructureStart(rAccess, chunkGenerator, biomeSource, randomState, sManager, sLevel.getSeed(), chunkPosition, 20, sLevel, PREDICATE);
				start.place(sLevel, sFManager, chunkGenerator, random, bb, chunkPosition);
			}
			else if(value.equals("village_desert")) {
				JungleTNTEffect.replaceNonSolidBlockOrVegetationWithAir(ent, 100, 10, true);
				StructureStart start = village_desert.createStructureStart(rAccess, chunkGenerator, biomeSource, randomState, sManager, sLevel.getSeed(), chunkPosition, 20, sLevel, PREDICATE);
				start.place(sLevel, sFManager, chunkGenerator, random, bb, chunkPosition);
			}
			else if(value.equals("village_savanna")) {
				JungleTNTEffect.replaceNonSolidBlockOrVegetationWithAir(ent, 100, 10, true);
				StructureStart start = village_savanna.createStructureStart(rAccess, chunkGenerator, biomeSource, randomState, sManager, sLevel.getSeed(), chunkPosition, 20, sLevel, PREDICATE);
				start.place(sLevel, sFManager, chunkGenerator, random, bb, chunkPosition);
			}
			else if(value.equals("village_snowy")) {
				JungleTNTEffect.replaceNonSolidBlockOrVegetationWithAir(ent, 100, 10, true);
				StructureStart start = village_snowy.createStructureStart(rAccess, chunkGenerator, biomeSource, randomState, sManager, sLevel.getSeed(), chunkPosition, 20, sLevel, PREDICATE);
				start.place(sLevel, sFManager, chunkGenerator, random, bb, chunkPosition);
			}
			else if(value.equals("village_taiga")) {
				JungleTNTEffect.replaceNonSolidBlockOrVegetationWithAir(ent, 100, 10, true);
				StructureStart start = village_taiga.createStructureStart(rAccess, chunkGenerator, biomeSource, randomState, sManager, sLevel.getSeed(), chunkPosition, 20, sLevel, PREDICATE);
				start.place(sLevel, sFManager, chunkGenerator, random, bb, chunkPosition);
			}
		}
	}
	
	@Override
	public BlockState getBlockState(IExplosiveEntity ent) {
		boolean bool = false;
		List<StructureStates> list = Arrays.asList(StructureStates.values());
		for(StructureStates state : list) {
			if(ent.getPersistentData().getString("structure").equals(state.asString())) {
				bool = true;
			}
		}
		if(bool) {
			return BlockRegistry.STRUCTURE_TNT.get().defaultBlockState().setValue(StructureTNTBlock.STRUCTURE, StructureTNTBlock.STRUCTURE.parse(ent.getPersistentData().getString("structure")).get());
		}
		return BlockRegistry.STRUCTURE_TNT.get().defaultBlockState();
	}
	
	@Override
	public int getDefaultFuse(IExplosiveEntity ent) {
		return 160;
	}
	
	public static class DesertPyramid extends DesertPyramidStructure {

		public DesertPyramid(Structure.Config settings) {
			super(settings);
		}
		
		@Override
		public Optional<Structure.StructurePosition> getStructurePosition(Structure.Context ctx) {
			return getStructurePosition(ctx, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, (p) -> {
				generatePieces(p, ctx);
			});
		}
		
		private void generatePieces(StructurePiecesCollector builder, Structure.Context ctx) {
			ChunkPos chunkpos = ctx.chunkPos();
			Constructor constructor = DesertTempleGenerator::new;
			builder.addPiece(constructor.construct(ctx.random(), chunkpos.getStartX(), chunkpos.getStartZ()));
		}
	}
	
	public static class Monument extends OceanMonumentStructure {

		public Monument(Structure.Config settings) {
			super(settings);
		}
		
		@Override
		public Optional<Structure.StructurePosition> getStructurePosition(Structure.Context ctx) {
			return getStructurePosition(ctx, Heightmap.Type.OCEAN_FLOOR_WG, (p) -> {
				generatePieces(p, ctx);
			});
		}

		private static StructurePiece createTopPiece(ChunkPos pos, ChunkRandom rand) {
			int i = pos.getStartX() - 29;
			int j = pos.getStartZ() - 29;
			Direction direction = Direction.Type.HORIZONTAL.random(rand);
			return new OceanMonumentGenerator.Base(rand, i, j, direction);
		}

		private static void generatePieces(StructurePiecesCollector builder, Structure.Context ctx) {
			builder.addPiece(createTopPiece(ctx.chunkPos(), ctx.random()));
		}
	}
}

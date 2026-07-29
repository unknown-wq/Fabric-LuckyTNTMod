package luckytnt.tnteffects;

import java.util.List;

import luckytnt.entity.OreTNTMinecart;
import luckytnt.entity.PrimedOreTNT;
import luckytnt.registry.BlockRegistry;
import luckytntlib.util.IExplosiveEntity;
import luckytntlib.util.explosions.ExplosionHelper;
import luckytntlib.util.explosions.IForEachBlockExplosionEffect;
import luckytntlib.util.tnteffects.PrimedTNTEffect;
import net.fabricmc.fabric.api.tag.convention.v2.ConventionalBlockTags;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.sounds.SoundSource;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class OreTNTEffect extends PrimedTNTEffect{

	/**
	 * The same 22 way switch used to be written out four times and re-evaluated per placement.
	 * Built lazily so the class does not depend on BlockRegistry being populated at class load.
	 */
	private static Block[] oreBlocks;

	private static Block[] ores() {
		if(oreBlocks == null) {
			oreBlocks = new Block[] {
				Blocks.COAL_ORE,
				Blocks.IRON_ORE,
				Blocks.GOLD_ORE,
				Blocks.COPPER_ORE,
				Blocks.DIAMOND_ORE,
				Blocks.EMERALD_ORE,
				Blocks.NETHER_QUARTZ_ORE,
				Blocks.NETHER_GOLD_ORE,
				Blocks.LAPIS_ORE,
				Blocks.REDSTONE_ORE,
				Blocks.DEEPSLATE_COAL_ORE,
				Blocks.DEEPSLATE_IRON_ORE,
				Blocks.DEEPSLATE_COPPER_ORE,
				Blocks.DEEPSLATE_GOLD_ORE,
				Blocks.DEEPSLATE_EMERALD_ORE,
				Blocks.DEEPSLATE_DIAMOND_ORE,
				Blocks.DEEPSLATE_LAPIS_ORE,
				Blocks.DEEPSLATE_REDSTONE_ORE,
				BlockRegistry.GUNPOWDER_ORE.get(),
				BlockRegistry.DEEPSLATE_GUNPOWDER_ORE.get(),
				BlockRegistry.URANIUM_ORE.get(),
				BlockRegistry.DEEPSLATE_URANIUM_ORE.get()
			};
		}
		return oreBlocks;
	}

	@Override
	public void explosionTick(IExplosiveEntity entity) {
		if(!entity.getLevel().isClientSide()) {
			if(entity instanceof PrimedOreTNT tnt) {
				if(tnt.availablePos.isEmpty()) {
					fillAvailablePos(tnt);
				}
				placeOres(entity.getLevel(), tnt.availablePos, 5, false);
			}
			else if(entity instanceof OreTNTMinecart tnt) {
				if(tnt.availablePos.isEmpty()) {
					fillAvailablePos(tnt);
				}
				placeOres(entity.getLevel(), tnt.availablePos, 5, false);
			}
		}
	}

	@Override
	public void serverExplosion(IExplosiveEntity entity) {
		if(entity instanceof PrimedOreTNT tnt) {
			if(tnt.availablePos.isEmpty()) {
				fillAvailablePos(tnt);
			}
			placeOres(entity.getLevel(), tnt.availablePos, 750, true);
		}
		else if(entity instanceof OreTNTMinecart tnt) {
			if(tnt.availablePos.isEmpty()) {
				fillAvailablePos(tnt);
			}
			placeOres(entity.getLevel(), tnt.availablePos, 750, true);
		}
	}

	/**
	 * Picks {@code count} random positions out of {@code availablePos} and turns them into ore.
	 * <p>
	 * {@code ArrayList#remove(int)} from a random index of a ~7200 element list arraycopies on
	 * average half the list; doing that 750 times was ~2.7M reference moves. The list is only ever
	 * appended to and sampled uniformly at random, so its order carries no meaning and the removed
	 * slot can simply be overwritten with the last element (O(1), still uniform).
	 *
	 * @param rerollEachPlacement  {@code true} rolls a new ore per placement (explosion),
	 *                             {@code false} uses one ore for the whole batch (per tick)
	 */
	private static void placeOres(Level level, List<BlockPos> availablePos, int count, boolean rerollEachPlacement) {
		RandomSource random = level.getRandom();
		Block[] ores = ores();
		Block block = ores[random.nextInt(ores.length)];

		for(int i = 0; i < count; i++) {
			if(rerollEachPlacement) {
				block = ores[random.nextInt(ores.length)];
			}
			int size = availablePos.size();
			if(size == 0) {
				break;
			}
			int rand = random.nextInt(size);
			BlockPos pos = availablePos.get(rand);
			if(pos == null) {
				continue;
			}
			availablePos.set(rand, availablePos.get(size - 1));
			availablePos.remove(size - 1);
			level.setBlockAndUpdate(pos, block.defaultBlockState());
			level.playSound(null, pos, SoundEvents.STONE_PLACE, SoundSource.BLOCKS, 1f, 1f);
		}
	}

	public void fillAvailablePos(PrimedOreTNT tnt) {
		fillAvailablePos(tnt.level(), tnt.getPos(), tnt.availablePos);
	}

	public void fillAvailablePos(OreTNTMinecart tnt) {
		fillAvailablePos(tnt.level(), tnt.getPos(), tnt.availablePos);
	}

	private static void fillAvailablePos(Level level, Vec3 position, List<BlockPos> availablePos) {
		ExplosionHelper.doSphericalExplosion(level, position, 12, new IForEachBlockExplosionEffect() {
			@Override
			public void doBlockExplosion(Level level, BlockPos pos, BlockState state, double distance) {
				if (!state.isAir() && state.getBlock().getExplosionResistance() < 100 && state.isCollisionShapeFullBlock(level, pos) && !state.is(ConventionalBlockTags.ORES)) {
					availablePos.add(pos);
				}
			}
		});
	}

	@Override
	public Block getBlock() {
		return BlockRegistry.ORE_TNT.get();
	}

	@Override
	public int getDefaultFuse(IExplosiveEntity entity) {
		return 150;
	}
}

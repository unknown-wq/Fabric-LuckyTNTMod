package luckytnt.tnteffects;

import luckytnt.registry.BlockRegistry;
import luckytnt.util.BlockSurviveChecks;
import luckytntlib.util.IExplosiveEntity;
import luckytntlib.util.tnteffects.PrimedTNTEffect;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.Level;

public class HeatWaveEffect extends PrimedTNTEffect {

	private static final int RADIUS = 150;
	private static final int RADIUS_SQ = RADIUS * RADIUS;

	@Override
	public void serverExplosion(IExplosiveEntity ent) {
		Level level = ent.getLevel();
		if(!(level instanceof ServerLevel)) {
			return;
		}
		// Loop invariants hoisted: one flint & steel stack and one hit vector for the whole r=150 sphere
		// instead of one of each per air block. The place context itself still has to be per-position
		// because FireBlock's BlockGetter/BlockPos overload of getStateForPlacement is protected.
		final ItemStack flintAndSteel = new ItemStack(Items.FLINT_AND_STEEL);
		final Vec3 hitVec = ent.getPos();
		final int centerX = Mth.floor(ent.x());
		final int centerY = Mth.floor(ent.y());
		final int centerZ = Mth.floor(ent.z());
		final int levelMinY = level.getMinY();
		final int levelMaxY = level.getMaxY();
		BlockPos.MutableBlockPos mutable = new BlockPos.MutableBlockPos();

		// The old full r=150 sphere sweep (4/3*pi*150^3 = 14.1M cells) was far more expensive than the
		// cell count suggests: BlockSurviveChecks.canFirePlaceAt reads the block below and, if that is
		// not sturdy, all six neighbours, so every one of the ~7M *air* cells above the terrain cost
		// about seven getBlockState calls - roughly 50M reads spent proving that empty sky cannot catch
		// fire.
		// Fire needs a non air block either directly below it or in one of its six neighbours, so the
		// highest cell that can ever ignite in a column is bounded by the game's own WORLD_SURFACE
		// heightmap ("first y above the highest non air block") of that column and its four horizontal
		// neighbours - an O(1) array read each. Everything above that bound is a guaranteed reject and
		// is no longer visited: ~14.1M cells / ~56M reads -> ~7M cells / ~7.5M reads, with an identical
		// set of fires (cave and overhang fires below the surface are still placed).
		// The traversal order changes from (x, y, z) to (x, z, y); that is safe because fire is neither
		// face sturdy nor flammable, so a placed fire can never change whether another cell ignites.
		for(int offX = -RADIUS; offX <= RADIUS; offX++) {
			int xSq = offX * offX;
			int posX = centerX + offX;
			int zMax = floorSqrt(RADIUS_SQ - xSq);
			for(int offZ = -zMax; offZ <= zMax; offZ++) {
				int posZ = centerZ + offZ;
				if(!level.hasChunk(posX >> 4, posZ >> 4)) {
					continue;
				}
				int yMax = floorSqrt(RADIUS_SQ - xSq - offZ * offZ);
				int top = level.getHeight(Heightmap.Types.WORLD_SURFACE, posX, posZ);
				top = Math.max(top, neighbourSurface(level, posX - 1, posZ));
				top = Math.max(top, neighbourSurface(level, posX + 1, posZ));
				top = Math.max(top, neighbourSurface(level, posX, posZ - 1));
				top = Math.max(top, neighbourSurface(level, posX, posZ + 1));
				int from = Math.min(Math.min(centerY + yMax, top), levelMaxY);
				int to = Math.max(centerY - yMax, levelMinY);
				for(int posY = from; posY >= to; posY--) {
					mutable.set(posX, posY, posZ);
					BlockState state = level.getBlockState(mutable);
					// isAir() is a cached flag and implies an explosion resistance of 0, so testing it first
					// is equivalent and skips two virtual calls for every non-air block in the sphere.
					if(state.isAir() && BlockSurviveChecks.canFirePlaceAt(state, level, mutable)) {
						BlockPos pos = mutable.immutable();
						BlockPlaceContext ctx = new BlockPlaceContext(level, null, InteractionHand.MAIN_HAND, flintAndSteel, new BlockHitResult(hitVec, Direction.DOWN, pos, true));
						level.setBlock(pos, Blocks.FIRE.getStateForPlacement(ctx), 3);
					}
				}
			}
		}
	}

	/**
	 * The highest y at which a horizontally neighbouring column can still supply a non air block, or
	 * {@link Integer#MIN_VALUE} for a chunk that is not loaded (which is never force generated here).
	 */
	private static int neighbourSurface(Level level, int x, int z) {
		return level.hasChunk(x >> 4, z >> 4) ? level.getHeight(Heightmap.Types.WORLD_SURFACE, x, z) - 1 : Integer.MIN_VALUE;
	}

	/**
	 * @param value  a value greater than or equal to 0
	 * @return the greatest int whose square is less than or equal to the given value
	 */
	private static int floorSqrt(int value) {
		int root = (int)Math.sqrt(value);
		while(root > 0 && root * root > value) {
			root--;
		}
		while((root + 1) * (root + 1) <= value) {
			root++;
		}
		return root;
	}

	@Override
	public void spawnParticles(IExplosiveEntity ent) {
		for(int i = 0; i < 50; i++) {
			ent.getLevel().addParticle(ParticleTypes.FLAME, ent.x() + Math.random() * 10 - Math.random() * 10, ent.y() + Math.random() * 10 - Math.random() * 10, ent.z() + Math.random() * 10 - Math.random() * 10, Math.random() * 0.1 - Math.random() * 0.1, Math.random() * 0.1 - Math.random() * 0.1, Math.random() * 0.1 - Math.random() * 0.1);
		}
	}

	@Override
	public Block getBlock() {
		return BlockRegistry.HEAT_WAVE.get();
	}

	@Override
	public int getDefaultFuse(IExplosiveEntity ent) {
		return 200;
	}
}

package luckytnt.tnteffects;

import java.util.List;

import com.google.common.collect.Lists;
import com.mojang.datafixers.util.Pair;

import luckytnt.entity.PrimedResetTNT;
import luckytnt.registry.BlockRegistry;
import luckytntlib.block.LTNTBlock;
import luckytntlib.util.IExplosiveEntity;
import luckytntlib.util.tnteffects.PrimedTNTEffect;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.level.chunk.PalettedContainer;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.Level;

public class ResetTNTEffect extends PrimedTNTEffect {

	private static final int RADIUS = 100;
	private static final int RADIUS_SQ = RADIUS * RADIUS;

	/**
	 * Snapshot storage. The snapshot used to be one {@code int} per sphere position (~4.19M ints =
	 * 16.8 MB, a single G1 humongous array, live for the whole 2400 tick fuse and per entity).
	 * <p>
	 * It is now a copy of the {@link PalettedContainer} of every chunk section the sphere touches, i.e.
	 * the exact same block data in the game's own compressed form: a section made of a single block
	 * (air, stone, deepslate - the overwhelming majority underground and in the sky) costs a few dozen
	 * bytes instead of 4096 ints, and a mixed section costs its real entropy (~2.5 kB at 5 bits/block).
	 * For a r=100 sphere that is ~1500 sections, in practice ~1-3 MB instead of 16.8 MB, with no
	 * humongous allocation at all.
	 * <p>
	 * The restore is bit for bit the same as before: it still walks exactly the sphere positions, and
	 * "the position held an {@link LTNTBlock} when the snapshot was taken" - which used to be encoded as
	 * a 0 in the int array - is now simply read back off the saved state itself.
	 * <p>
	 * A {@link ResetTNTEffect} instance is created per {@link PrimedResetTNT} (see its constructor),
	 * so keeping the snapshot here is per entity, exactly like {@code ent.blocks} was.
	 */
	private BlockPos snapshotCenter;
	private PalettedContainer<BlockState>[] snapshotSections;
	private int snapshotMinSectionX;
	private int snapshotMinSectionY;
	private int snapshotMinSectionZ;
	private int snapshotSizeY;
	private int snapshotSizeZ;
	private int snapshotMinY;
	private int snapshotMaxY;

	@Override
	public void explosionTick(IExplosiveEntity entity) {
		// explosionTick runs on both logical sides; the snapshot is only ever consumed by
		// serverExplosion, so taking it on the client was pure waste (and a second memory spike).
		if(entity.getLevel() instanceof ServerLevel && entity instanceof PrimedResetTNT ent && ent.getTNTFuse() == 2400) {
			saveBlocks(ent);
			saveEntities(ent);
		}
	}

	@Override
	public void serverExplosion(IExplosiveEntity entity) {
		if(entity instanceof PrimedResetTNT ent) {
			restoreBlocks(ent);

			if(ent.entities != null) {
				for(Pair<Vec3, Entity> pair : ent.entities) {
			    		if(pair.getSecond().isAlive() && !(pair.getSecond() instanceof Player)) {
			    			pair.getSecond().setPos(pair.getFirst());
			    		} else if(pair.getSecond() instanceof Player pla) {
			    			if(pla instanceof ServerPlayer player) {
			    				player.teleportTo(pair.getFirst().x, pair.getFirst().y, pair.getFirst().z);
			    			}
			    		}
			    	}
			}
		}
	}

	@SuppressWarnings("unchecked")
	public void saveBlocks(PrimedResetTNT ent) {
		Level level = ent.getLevel();
		BlockPos center = toBlockPos(ent.getPos());

		// Section grid covering the sphere. 4.19M getBlockState calls (and the 3.9M pure waste
		// iterations of the old 201^3 cube) are replaced by ~1500 palette copies.
		int minY = Math.max(center.getY() - RADIUS, level.getMinY());
		int maxY = Math.min(center.getY() + RADIUS, level.getMaxY());
		int minSectionX = (center.getX() - RADIUS) >> 4;
		int maxSectionX = (center.getX() + RADIUS) >> 4;
		int minSectionY = minY >> 4;
		int maxSectionY = maxY >> 4;
		int minSectionZ = (center.getZ() - RADIUS) >> 4;
		int maxSectionZ = (center.getZ() + RADIUS) >> 4;
		int sizeX = maxSectionX - minSectionX + 1;
		int sizeY = maxSectionY - minSectionY + 1;
		int sizeZ = maxSectionZ - minSectionZ + 1;
		PalettedContainer<BlockState>[] sections = new PalettedContainer[sizeX * sizeY * sizeZ];

		for(int sectionX = minSectionX; sectionX <= maxSectionX; sectionX++) {
			for(int sectionZ = minSectionZ; sectionZ <= maxSectionZ; sectionZ++) {
				// a chunk that is not loaded cannot have been modified during the fuse, and pulling it to
				// ChunkStatus.FULL here would generate up to 169 chunks that nobody has ever seen
				if(!level.hasChunk(sectionX, sectionZ)) {
					continue;
				}
				ChunkAccess chunk = level.getChunk(sectionX, sectionZ);
				LevelChunkSection[] chunkSections = chunk.getSections();
				for(int sectionY = minSectionY; sectionY <= maxSectionY; sectionY++) {
					if(!sectionTouchesSphere(center, sectionX, sectionY, sectionZ)) {
						continue;
					}
					int index = level.getSectionIndexFromSectionY(sectionY);
					if(index < 0 || index >= chunkSections.length) {
						continue;
					}
					sections[((sectionX - minSectionX) * sizeY + (sectionY - minSectionY)) * sizeZ + (sectionZ - minSectionZ)] = chunkSections[index].getStates().copy();
				}
			}
		}

		snapshotCenter = center;
		snapshotSections = sections;
		snapshotMinSectionX = minSectionX;
		snapshotMinSectionY = minSectionY;
		snapshotMinSectionZ = minSectionZ;
		snapshotSizeY = sizeY;
		snapshotSizeZ = sizeZ;
		snapshotMinY = minY;
		snapshotMaxY = maxY;
		// the old per-block pair list is no longer used, keep the field non null for compatibility
		ent.blocks = Lists.newArrayList();
	}

	private void restoreBlocks(PrimedResetTNT ent) {
		if(snapshotCenter == null || snapshotSections == null) {
			return;
		}
		Level level = ent.getLevel();
		PalettedContainer<BlockState>[] sections = snapshotSections;
		int centerX = snapshotCenter.getX();
		int centerY = snapshotCenter.getY();
		int centerZ = snapshotCenter.getZ();
		BlockPos.MutableBlockPos mutable = new BlockPos.MutableBlockPos();

		// The z span is derived in closed form instead of testing (and rejecting) every cell of the
		// 201^3 cube: 8 120 601 iterations -> 4 188 790, the exact same set of positions in the exact
		// same order.
		for(int offX = -RADIUS; offX <= RADIUS; offX++) {
			int xSq = offX * offX;
			int posX = centerX + offX;
			int localX = posX & 15;
			int sectionX = (posX >> 4) - snapshotMinSectionX;
			for(int offY = RADIUS; offY >= -RADIUS; offY--) {
				int xySq = xSq + offY * offY;
				if(xySq > RADIUS_SQ) {
					continue;
				}
				int posY = centerY + offY;
				if(posY < snapshotMinY || posY > snapshotMaxY) {
					continue;
				}
				int localY = posY & 15;
				int sectionBase = (sectionX * snapshotSizeY + ((posY >> 4) - snapshotMinSectionY)) * snapshotSizeZ;
				int zMax = floorSqrt(RADIUS_SQ - xySq);
				for(int offZ = -zMax; offZ <= zMax; offZ++) {
					int posZ = centerZ + offZ;
					PalettedContainer<BlockState> section = sections[sectionBase + ((posZ >> 4) - snapshotMinSectionZ)];
					if(section == null) {
						continue;
					}
					BlockState state = section.get(localX, localY, posZ & 15);
					// LTNT blocks were skipped by the save pass (they were stored as a 0), which is exactly
					// the same test as "the saved state is an LTNTBlock"
					if(state.getBlock() instanceof LTNTBlock) {
						continue;
					}
					mutable.set(posX, posY, posZ);
					if(!level.getBlockState(mutable).equals(state)) {
						// UPDATE_CLIENTS instead of flag 3: the whole sphere is rewritten to a configuration
						// that was stable when it was captured, so the up to 4.19M neighbour cascades of flag 3
						// only ever notify blocks that this same pass overwrites microseconds later.
						level.setBlock(mutable.immutable(), state, Block.UPDATE_CLIENTS);
					}
				}
			}
		}

		snapshotCenter = null;
		snapshotSections = null;
	}

	/**
	 * @return whether any block of the given chunk section is inside the snapshot sphere
	 */
	private static boolean sectionTouchesSphere(BlockPos center, int sectionX, int sectionY, int sectionZ) {
		long dx = axisDistance(center.getX(), sectionX);
		long dy = axisDistance(center.getY(), sectionY);
		long dz = axisDistance(center.getZ(), sectionZ);
		return dx * dx + dy * dy + dz * dz <= RADIUS_SQ;
	}

	/**
	 * @return the distance from the given coordinate to the closest block of the given section on one axis
	 */
	private static int axisDistance(int coordinate, int section) {
		int min = section << 4;
		int max = min + 15;
		if(coordinate < min) {
			return min - coordinate;
		}
		return coordinate > max ? coordinate - max : 0;
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

	public void saveEntities(PrimedResetTNT ent) {
		ent.entities = Lists.newArrayList();
		List<Entity> list = ent.getLevel().getEntities(ent, new AABB(ent.x() - RADIUS, ent.y() - RADIUS, ent.z() - RADIUS, ent.x() + RADIUS, ent.y() + RADIUS, ent.z() + RADIUS));

    	for(int i = 0; i < list.size(); i++) {
    		Entity entity = list.get(i);
    		ent.entities.add(Pair.of(new Vec3(entity.getX(), entity.getY(), entity.getZ()), entity));
    	}
	}

	@Override
	public Block getBlock() {
		return BlockRegistry.RESET_TNT.get();
	}

	@Override
	public int getDefaultFuse(IExplosiveEntity ent) {
		return 2400;
	}
}

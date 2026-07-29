package luckytnt.tnteffects;

import java.util.ArrayList;
import java.util.HashMap;
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
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.Level;

public class ResetTNTEffect extends PrimedTNTEffect {

	private static final int RADIUS = 100;
	private static final int RADIUS_SQ = RADIUS * RADIUS;

	/**
	 * Amount of positions contained in the snapshot sphere. Computed lazily because the
	 * counting loop is pure integer math but still ~8M iterations.
	 */
	private static int sphereSize = -1;

	/**
	 * Snapshot storage. Instead of ~4.19M {@code Pair<BlockPos, BlockState>} objects (~200 MB live
	 * for the whole 2400 tick fuse) the snapshot is stored as
	 * <ul>
	 * <li>the center of the sphere, so every position can be re-derived,</li>
	 * <li>one int per sphere position (in the fixed iteration order of {@link #RADIUS}), holding
	 * {@code paletteIndex + 1}, or 0 for positions that were skipped (LTNT blocks),</li>
	 * <li>a small palette of the distinct {@link BlockState}s that occurred.</li>
	 * </ul>
	 * That is ~16.8 MB instead of ~200 MB, with an identical restore result.
	 * <p>
	 * A {@link ResetTNTEffect} instance is created per {@link PrimedResetTNT} (see its constructor),
	 * so keeping the snapshot here is per entity, exactly like {@code ent.blocks} was.
	 */
	private BlockPos snapshotCenter;
	private int[] snapshotStates;
	private BlockState[] snapshotPalette;

	@Override
	public void explosionTick(IExplosiveEntity entity) {
		// explosionTick runs on both logical sides; the snapshot is only ever consumed by
		// serverExplosion, so taking it on the client was pure waste (and a second ~200 MB spike).
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

	public void saveBlocks(PrimedResetTNT ent) {
		Level level = ent.getLevel();
		BlockPos center = toBlockPos(ent.getPos());
		int[] states = new int[getSphereSize()];
		HashMap<BlockState, Integer> paletteIndices = new HashMap<>();
		List<BlockState> palette = new ArrayList<>();
		BlockPos.MutableBlockPos mutable = new BlockPos.MutableBlockPos();

		int index = 0;
		for(int offX = -RADIUS; offX <= RADIUS; offX++) {
			for(int offY = RADIUS; offY >= -RADIUS; offY--) {
				for(int offZ = -RADIUS; offZ <= RADIUS; offZ++) {
					if(offX * offX + offY * offY + offZ * offZ > RADIUS_SQ) {
						continue;
					}
					mutable.set(center.getX() + offX, center.getY() + offY, center.getZ() + offZ);
					BlockState state = level.getBlockState(mutable);
					if(!(state.getBlock() instanceof LTNTBlock)) {
						Integer id = paletteIndices.get(state);
						if(id == null) {
							id = Integer.valueOf(palette.size() + 1);
							paletteIndices.put(state, id);
							palette.add(state);
						}
						states[index] = id.intValue();
					}
					index++;
				}
			}
		}

		snapshotCenter = center;
		snapshotStates = states;
		snapshotPalette = palette.toArray(new BlockState[0]);
		// the old per-block pair list is no longer used, keep the field non null for compatibility
		ent.blocks = Lists.newArrayList();
	}

	private void restoreBlocks(PrimedResetTNT ent) {
		if(snapshotCenter == null || snapshotStates == null || snapshotPalette == null) {
			return;
		}
		Level level = ent.getLevel();
		BlockPos center = snapshotCenter;
		int[] states = snapshotStates;
		BlockState[] palette = snapshotPalette;
		BlockPos.MutableBlockPos mutable = new BlockPos.MutableBlockPos();

		int index = 0;
		for(int offX = -RADIUS; offX <= RADIUS; offX++) {
			for(int offY = RADIUS; offY >= -RADIUS; offY--) {
				for(int offZ = -RADIUS; offZ <= RADIUS; offZ++) {
					if(offX * offX + offY * offY + offZ * offZ > RADIUS_SQ) {
						continue;
					}
					int id = states[index++];
					if(id == 0) {
						continue;
					}
					mutable.set(center.getX() + offX, center.getY() + offY, center.getZ() + offZ);
					BlockState state = palette[id - 1];
					if(!level.getBlockState(mutable).equals(state)) {
						level.setBlock(mutable.immutable(), state, 3);
					}
				}
			}
		}

		snapshotCenter = null;
		snapshotStates = null;
		snapshotPalette = null;
	}

	private static int getSphereSize() {
		if(sphereSize < 0) {
			int count = 0;
			for(int offX = -RADIUS; offX <= RADIUS; offX++) {
				for(int offY = RADIUS; offY >= -RADIUS; offY--) {
					for(int offZ = -RADIUS; offZ <= RADIUS; offZ++) {
						if(offX * offX + offY * offY + offZ * offZ <= RADIUS_SQ) {
							count++;
						}
					}
				}
			}
			sphereSize = count;
		}
		return sphereSize;
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

package luckytnt.tnteffects;
import net.minecraft.world.item.DyeColor;
import net.minecraft.server.level.ServerLevel;

import luckytnt.block.GotthardTunnelBlock;
import luckytnt.registry.BlockRegistry;
import luckytnt.util.Materials;
import luckytntlib.util.IExplosiveEntity;
import luckytntlib.util.explosions.ImprovedExplosion;
import luckytntlib.util.tnteffects.PrimedTNTEffect;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.tags.BlockTags;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;

/**
 * The four horizontal directions used to be four copy-pasted ~40 line bodies per method. They are now a
 * single loop parameterised by the direction's step vector: {@code a} walks 0..200 along the tunnel and
 * {@code p} walks the perpendicular horizontal axis, which reproduces the original coordinates exactly
 * for every direction. Everything else here is loop-invariant hoisting and de-duplicated world reads.
 */
public class GotthardTunnelEffect extends PrimedTNTEffect {

	private static final int LENGTH = 200;

	@Override
	public void serverExplosion(IExplosiveEntity ent) {
		boolean streets = ent.getPersistentData().getBooleanOr("streets", false);
		Direction dir = Direction.byName(ent.getPersistentData().getStringOr("direction", "")) != null ? Direction.byName(ent.getPersistentData().getStringOr("direction", "")) : Direction.NORTH;
		if(dir.getAxis().isVertical()) {
			return;
		}

		Level level = ent.getLevel();
		ServerLevel sLevel = (ServerLevel)level;
		ImprovedExplosion dummy = ImprovedExplosion.dummyExplosion(level);
		BlockState air = Blocks.AIR.defaultBlockState();

		int stepX = dir.getStepX();
		int stepZ = dir.getStepZ();
		int perpX = Math.abs(stepZ);
		int perpZ = Math.abs(stepX);
		int baseX = Mth.floor(ent.x());
		int baseY = Mth.floor(ent.y());
		int baseZ = Mth.floor(ent.z());

		for(int a = 0; a <= LENGTH; a++) {
			int ax = baseX + a * stepX;
			int az = baseZ + a * stepZ;
			for(int p = -10; p <= 10; p++) {
				int x = ax + p * perpX;
				int z = az + p * perpZ;
				for(int offY = 0; offY <= 15; offY++) {
					BlockPos pos = new BlockPos(x, baseY + offY, z);
					BlockState state = level.getBlockState(pos);
					Block block = state.getBlock();
					if(block.getExplosionResistance() <= 200) {
						block.wasExploded(sLevel, pos, dummy);
						level.setBlock(pos, air, 3);
					}
				}
			}
		}

		placeWalls(ent, dir);
		if(streets) {
			createStreet(ent, dir);
		}
		placeLights(ent, dir);
	}

	@Override
	public BlockState getBlockState(IExplosiveEntity ent) {
		Direction dir = Direction.NORTH;
		if(!ent.getPersistentData().getStringOr("direction", "").equals("")) {
			dir = Direction.byName(ent.getPersistentData().getStringOr("direction", ""));
		}
		return BlockRegistry.GOTTHARD_TUNNEL.get().defaultBlockState().setValue(GotthardTunnelBlock.STREETS, ent.getPersistentData().getBooleanOr("streets", false)).setValue(GotthardTunnelBlock.FACING, dir);
	}

	@Override
	public int getDefaultFuse(IExplosiveEntity ent) {
		return 200;
	}

	public void placeLights(IExplosiveEntity ent, Direction dir) {
		if(dir.getAxis().isVertical()) {
			return;
		}
		Level level = ent.getLevel();
		ServerLevel sLevel = (ServerLevel)level;
		ImprovedExplosion dummy = ImprovedExplosion.dummyExplosion(level);
		BlockState seaLantern = Blocks.SEA_LANTERN.defaultBlockState();

		int stepX = dir.getStepX();
		int stepZ = dir.getStepZ();
		int perpX = Math.abs(stepZ);
		int perpZ = Math.abs(stepX);
		int baseX = Mth.floor(ent.x());
		int baseY = Mth.floor(ent.y());
		int baseZ = Mth.floor(ent.z());

		for(int a = 2; a <= LENGTH; a += 4) {
			int ax = baseX + a * stepX;
			int az = baseZ + a * stepZ;
			placeShaded(level, sLevel, dummy, new BlockPos(ax, baseY - 1, az), seaLantern);
			placeShaded(level, sLevel, dummy, new BlockPos(ax + 10 * perpX, baseY - 1, az + 10 * perpZ), seaLantern);
			placeShaded(level, sLevel, dummy, new BlockPos(ax - 10 * perpX, baseY - 1, az - 10 * perpZ), seaLantern);

			placeShaded(level, sLevel, dummy, new BlockPos(ax + 11 * perpX, baseY + 7, az + 11 * perpZ), seaLantern);
			placeShaded(level, sLevel, dummy, new BlockPos(ax + 11 * perpX, baseY + 8, az + 11 * perpZ), seaLantern);
			placeShaded(level, sLevel, dummy, new BlockPos(ax - 11 * perpX, baseY + 7, az - 11 * perpZ), seaLantern);
			placeShaded(level, sLevel, dummy, new BlockPos(ax - 11 * perpX, baseY + 8, az - 11 * perpZ), seaLantern);

			placeShaded(level, sLevel, dummy, new BlockPos(ax, baseY + 16, az), seaLantern);
		}
	}

	public void createStreet(IExplosiveEntity ent, Direction dir) {
		if(dir.getAxis().isVertical()) {
			return;
		}
		Level level = ent.getLevel();
		ServerLevel sLevel = (ServerLevel)level;
		ImprovedExplosion dummy = ImprovedExplosion.dummyExplosion(level);
		BlockState grayConcrete = Blocks.CONCRETE.pick(DyeColor.GRAY).defaultBlockState();
		BlockState yellowConcrete = Blocks.CONCRETE.pick(DyeColor.YELLOW).defaultBlockState();
		BlockState smoothStone = Blocks.SMOOTH_STONE.defaultBlockState();

		int stepX = dir.getStepX();
		int stepZ = dir.getStepZ();
		int perpX = Math.abs(stepZ);
		int perpZ = Math.abs(stepX);
		int baseX = Mth.floor(ent.x());
		int floorY = Mth.floor(ent.y()) - 1;
		int baseZ = Mth.floor(ent.z());

		for(int a = 0; a <= LENGTH; a++) {
			int ax = baseX + a * stepX;
			int az = baseZ + a * stepZ;
			for(int p = 1; p <= 9; p++) {
				placeShaded(level, sLevel, dummy, new BlockPos(ax + p * perpX, floorY, az + p * perpZ), grayConcrete);
				placeShaded(level, sLevel, dummy, new BlockPos(ax - p * perpX, floorY, az - p * perpZ), grayConcrete);
			}

			placeShaded(level, sLevel, dummy, new BlockPos(ax, floorY, az), smoothStone);
			placeShaded(level, sLevel, dummy, new BlockPos(ax + 10 * perpX, floorY, az + 10 * perpZ), smoothStone);
			placeShaded(level, sLevel, dummy, new BlockPos(ax - 10 * perpX, floorY, az - 10 * perpZ), smoothStone);

			if(a % 5 == 0) {
				for(int back = 2; back <= 4; back++) {
					int mx = baseX + (a - back) * stepX;
					int mz = baseZ + (a - back) * stepZ;
					placeShaded(level, sLevel, dummy, new BlockPos(mx + 5 * perpX, floorY, mz + 5 * perpZ), yellowConcrete);
				}
				for(int back = 2; back <= 4; back++) {
					int mx = baseX + (a - back) * stepX;
					int mz = baseZ + (a - back) * stepZ;
					placeShaded(level, sLevel, dummy, new BlockPos(mx - 5 * perpX, floorY, mz - 5 * perpZ), yellowConcrete);
				}
			}
		}
	}

	public void placeWalls(IExplosiveEntity ent, Direction dir) {
		if(dir.getAxis().isVertical()) {
			return;
		}
		Level level = ent.getLevel();
		ServerLevel sLevel = (ServerLevel)level;
		ImprovedExplosion dummy = ImprovedExplosion.dummyExplosion(level);
		BlockState stone = Blocks.STONE.defaultBlockState();

		int stepX = dir.getStepX();
		int stepZ = dir.getStepZ();
		int perpX = Math.abs(stepZ);
		int perpZ = Math.abs(stepX);
		int baseX = Mth.floor(ent.x());
		int baseY = Mth.floor(ent.y());
		int baseZ = Mth.floor(ent.z());

		for(int a = 0; a <= LENGTH; a++) {
			int ax = baseX + a * stepX;
			int az = baseZ + a * stepZ;
			for(int offY = 15; offY >= 0; offY--) {
				int y = baseY + offY;
				placeWallBlock(level, sLevel, dummy, new BlockPos(ax + 11 * perpX, y, az + 11 * perpZ), 10, stone);
				placeWallBlock(level, sLevel, dummy, new BlockPos(ax - 11 * perpX, y, az - 11 * perpZ), 10, stone);
			}
		}

		for(int a = 0; a <= LENGTH; a++) {
			int ax = baseX + a * stepX;
			int az = baseZ + a * stepZ;
			for(int p = -10; p <= 10; p++) {
				int x = ax + p * perpX;
				int z = az + p * perpZ;
				// The floor uses a sky-light threshold of 15, the ceiling one of 10 (as before).
				placeWallBlock(level, sLevel, dummy, new BlockPos(x, baseY - 1, z), 15, stone);
				placeWallBlock(level, sLevel, dummy, new BlockPos(x, baseY + 16, z), 10, stone);
			}
		}
	}

	/**
	 * Places {@code replacement} if the position is not exposed to the sky and is breakable.
	 * One world read instead of the two identical ones the inlined version did.
	 */
	private static void placeShaded(Level level, ServerLevel sLevel, ImprovedExplosion dummy, BlockPos pos, BlockState replacement) {
		if(level.canSeeSky(pos)) {
			return;
		}
		BlockState state = level.getBlockState(pos);
		Block block = state.getBlock();
		if(block.getExplosionResistance() <= 200) {
			block.wasExploded(sLevel, pos, dummy);
			level.setBlock(pos, replacement, 3);
		}
	}

	/**
	 * Notifies leaves/wood that they were exploded and, if the position is dark enough, replaces it with
	 * stone. Reads the state once for the leaves/wood test and once more only when the light test passes
	 * (the inlined version did up to six reads of the same position).
	 */
	private static void placeWallBlock(Level level, ServerLevel sLevel, ImprovedExplosion dummy, BlockPos pos, int maxSkyLight, BlockState stone) {
		BlockState state = level.getBlockState(pos);
		if(state.is(BlockTags.LEAVES) || Materials.isWood(state)) {
			state.getBlock().wasExploded(sLevel, pos, dummy);
		}
		if(level.getBrightness(LightLayer.SKY, pos) < maxSkyLight) {
			BlockState current = level.getBlockState(pos);
			Block block = current.getBlock();
			if(block.getExplosionResistance() <= 200) {
				block.wasExploded(sLevel, pos, dummy);
				level.setBlock(pos, stone, 3);
			}
		}
	}
}

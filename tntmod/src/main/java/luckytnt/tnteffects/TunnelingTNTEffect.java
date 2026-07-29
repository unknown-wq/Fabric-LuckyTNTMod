package luckytnt.tnteffects;

import net.minecraft.server.level.ServerLevel;
import luckytnt.block.TunnelingTNTBlock;
import luckytnt.registry.BlockRegistry;
import luckytntlib.util.IExplosiveEntity;
import luckytntlib.util.tnteffects.PrimedTNTEffect;
import luckytntlib.util.explosions.ImprovedExplosion;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;

public class TunnelingTNTEffect extends PrimedTNTEffect{

	@Override
	public void serverExplosion(IExplosiveEntity entity) {
		Direction direction = Direction.byName(entity.getPersistentData().getStringOr("direction", "")) != null ? Direction.byName(entity.getPersistentData().getStringOr("direction", "")) : Direction.EAST;
		if(direction.getAxis().isVertical()) {
			return;
		}

		// One loop for all four directions (the bodies were identical up to which axis is the tunnel and
		// which is the perpendicular one), with the r<4 disc test moved ahead of the BlockPos allocation
		// and the world read - only ~40 of the 81 cells per slice are inside the disc.
		Level level = entity.getLevel();
		ServerLevel sLevel = (ServerLevel)level;
		ImprovedExplosion dummy = ImprovedExplosion.dummyExplosion(level);
		BlockState air = Blocks.AIR.defaultBlockState();

		int stepX = direction.getStepX();
		int stepZ = direction.getStepZ();
		int perpX = Math.abs(stepZ);
		int perpZ = Math.abs(stepX);
		int baseX = Mth.floor(entity.x());
		int baseY = Mth.floor(entity.y());
		int baseZ = Mth.floor(entity.z());

		for(int perp = -4; perp <= 4; perp++) {
			int perpSq = perp * perp;
			for(int offY = -4; offY <= 4; offY++) {
				if(perpSq + offY * offY >= 16) {
					continue;
				}
				int x = baseX + perp * perpX;
				int y = baseY + offY;
				int z = baseZ + perp * perpZ;
				for(int a = 0; a <= 90; a++) {
					BlockPos pos = new BlockPos(x + a * stepX, y, z + a * stepZ);
					BlockState state = level.getBlockState(pos);
					Block block = state.getBlock();
					if(block.getExplosionResistance() < 100) {
						block.wasExploded(sLevel, pos, dummy);
						level.setBlock(pos, air, 3);
					}
				}
			}
		}
	}
	
	@Override
	public BlockState getBlockState(IExplosiveEntity entity) {
		return BlockRegistry.TUNNELING_TNT.get().defaultBlockState().setValue(TunnelingTNTBlock.FACING, Direction.byName(entity.getPersistentData().getStringOr("direction", "")) != null ? Direction.byName(entity.getPersistentData().getStringOr("direction", "")) : Direction.EAST);
	}
	
	@Override
	public Block getBlock() {
		return BlockRegistry.TUNNELING_TNT.get();
	}
}

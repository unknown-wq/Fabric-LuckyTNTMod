package luckytnt.tnteffects;
import net.minecraft.server.level.ServerLevel;

import luckytnt.registry.BlockRegistry;
import luckytntlib.util.IExplosiveEntity;
import luckytntlib.util.explosions.ImprovedExplosion;
import luckytntlib.util.tnteffects.PrimedTNTEffect;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;

public class KolaBoreholeTNTEffect extends PrimedTNTEffect {

	@Override
	public void serverExplosion(IExplosiveEntity ent) {
		int y = ((int)Math.ceil(ent.y()) + 64);
		if(y % 6 != 0) {
			y += 6;
			while(y % 6 != 0) {
				if(y % 6 == 0) {
					break;
				} else {
					y -= 1;
				}
			}
		}
		int intv = y / 6;
		int rad = 8;
		int prevRad = 8;
			
		Level level = ent.getLevel();
		ServerLevel sLevel = (ServerLevel)level;
		ImprovedExplosion dummy = ImprovedExplosion.dummyExplosion(level);
		BlockState air = Blocks.AIR.defaultBlockState();
		BlockState stone = Blocks.STONE.defaultBlockState();
		BlockState deepslate = Blocks.DEEPSLATE.defaultBlockState();
		int baseX = Mth.floor(ent.x());
		int baseZ = Mth.floor(ent.z());

		for(int offY = y - 1; offY >= 0; offY--) {
			// Squared radii, computed once per layer. rad can go negative as the borehole narrows, which
			// is why the sign is checked explicitly instead of just comparing squares.
			int innerSq = rad >= 0 ? rad * rad : -1;
			int outer = rad + 1;
			int outerSq = outer >= 0 ? outer * outer : -1;
			int posY = offY - 64;
			for(int offX = -10; offX <= 10; offX++) {
				int dx2 = offX * offX;
				if(outerSq < 0 || dx2 > outerSq) {
					continue;
				}
				for(int offZ = -10; offZ <= 10; offZ++) {
					int d2 = dx2 + offZ * offZ;
					if(d2 > outerSq) {
						continue;
					}
					BlockPos pos = new BlockPos(baseX + offX, posY, baseZ + offZ);
					BlockState state = level.getBlockState(pos);
					Block block = state.getBlock();
					if(block.getExplosionResistance() > 200) {
						continue;
					}
					if(innerSq >= 0 && d2 <= innerSq) {
						block.wasExploded(sLevel, pos, dummy);
						level.setBlock(pos, air, 3);
						continue;
					}
					// Ring shell: rad < distance <= rad + 1
					BlockPos above = pos.above();
					boolean supported;
					if(rad != prevRad) {
						supported = false;
						for(Direction side : Direction.Plane.HORIZONTAL) {
							BlockPos neighbour = above.relative(side);
							BlockState neighbourState = level.getBlockState(neighbour);
							if(Block.isShapeFullBlock(neighbourState.getShape(level, neighbour)) && neighbourState.canOcclude()) {
								supported = true;
								break;
							}
						}
					} else {
						BlockState aboveState = level.getBlockState(above);
						// NB: the original passes `pos` (not `above`) as the shape context here; kept as is.
						supported = Block.isShapeFullBlock(aboveState.getShape(level, pos)) && aboveState.canOcclude();
					}
					if(supported) {
						block.wasExploded(sLevel, pos, dummy);
						level.setBlock(pos, air, 3);
						if(pos.getY() > (Math.random() * 2 - Math.random() * 2)) {
							level.setBlock(pos, stone, 3);
						} else {
							level.setBlock(pos, deepslate, 3);
						}
					}
				}
			}
			prevRad = rad;
			if(offY % intv == 0) {
				rad--;
			}
		}
		for(int i = -59; i >= -65; i--) {
			BlockPos pos = new BlockPos(baseX, i, baseZ);
			level.getBlockState(pos).getBlock().wasExploded(sLevel, pos, dummy);
			level.setBlock(pos, air, 3);
		}
	}
	
	@Override
	public Block getBlock() {
		return BlockRegistry.KOLA_BOREHOLE_TNT.get();
	}
	
	@Override
	public int getDefaultFuse(IExplosiveEntity ent) {
		return 200;
	}
}

package luckytnt.tnteffects;

import luckytnt.registry.BlockRegistry;
import luckytntlib.util.IExplosiveEntity;
import luckytntlib.util.explosions.ImprovedExplosion;
import luckytntlib.util.tnteffects.PrimedTNTEffect;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;

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
			
		for(int offY = y - 1; offY >= 0; offY--) {
			for(int offX = -10; offX <= 10; offX++) {
				for(int offZ = -10; offZ <= 10; offZ++) {
					double distance = Math.sqrt(offX * offX + offZ * offZ);
					BlockPos pos = new BlockPos(MathHelper.floor(ent.x() + offX), offY - 64, MathHelper.floor(ent.z() + offZ));
					if(distance <= rad && ent.getLevel().getBlockState(pos).getBlock().getBlastResistance() <= 200) {
						ent.getLevel().getBlockState(pos).getBlock().onDestroyedByExplosion(ent.getLevel(), pos, ImprovedExplosion.dummyExplosion(ent.getLevel()));
						ent.getLevel().setBlockState(pos, Blocks.AIR.getDefaultState(), 3);
					}
					if(distance > rad && distance <= (rad + 1) && ent.getLevel().getBlockState(pos).getBlock().getBlastResistance() <= 200) {
						if(rad != prevRad) {
							if((Block.isShapeFullCube(ent.getLevel().getBlockState(pos.up().north()).getOutlineShape(ent.getLevel(), pos.up().north())) && ent.getLevel().getBlockState(pos.up().north()).isOpaque())
							|| (Block.isShapeFullCube(ent.getLevel().getBlockState(pos.up().east()).getOutlineShape(ent.getLevel(), pos.up().east())) && ent.getLevel().getBlockState(pos.up().east()).isOpaque())
							|| (Block.isShapeFullCube(ent.getLevel().getBlockState(pos.up().south()).getOutlineShape(ent.getLevel(), pos.up().south())) && ent.getLevel().getBlockState(pos.up().south()).isOpaque())
							|| (Block.isShapeFullCube(ent.getLevel().getBlockState(pos.up().west()).getOutlineShape(ent.getLevel(), pos.up().west())) && ent.getLevel().getBlockState(pos.up().west()).isOpaque()))
							{
								ent.getLevel().getBlockState(pos).getBlock().onDestroyedByExplosion(ent.getLevel(), pos, ImprovedExplosion.dummyExplosion(ent.getLevel()));
								ent.getLevel().setBlockState(pos, Blocks.AIR.getDefaultState(), 3);
								if(pos.getY() > (Math.random() * 2 - Math.random() * 2)) {
									ent.getLevel().setBlockState(pos, Blocks.STONE.getDefaultState(), 3);
								} else {
									ent.getLevel().setBlockState(pos, Blocks.DEEPSLATE.getDefaultState(), 3);
								}
							}
						} else if(prevRad == rad) {
							if(Block.isShapeFullCube(ent.getLevel().getBlockState(pos.up()).getOutlineShape(ent.getLevel(), pos)) && ent.getLevel().getBlockState(pos.up()).isOpaque()) {
								Block block = ent.getLevel().getBlockState(pos).getBlock();
								block.onDestroyedByExplosion(ent.getLevel(), pos, ImprovedExplosion.dummyExplosion(ent.getLevel()));
								ent.getLevel().setBlockState(pos, Blocks.AIR.getDefaultState(), 3);
								if(pos.getY() > (Math.random() * 2 - Math.random() * 2)) {
									ent.getLevel().setBlockState(pos, Blocks.STONE.getDefaultState(), 3);
								} else {
									ent.getLevel().setBlockState(pos, Blocks.DEEPSLATE.getDefaultState(), 3);
								}
							}
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
			BlockPos pos = new BlockPos(MathHelper.floor(ent.x()), i, MathHelper.floor(ent.z()));
			ent.getLevel().getBlockState(pos).getBlock().onDestroyedByExplosion(ent.getLevel(), pos, ImprovedExplosion.dummyExplosion(ent.getLevel()));
			ent.getLevel().setBlockState(pos, Blocks.AIR.getDefaultState(), 3);
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

package luckytnt.tnteffects;

import luckytnt.registry.BlockRegistry;
import luckytnt.util.Materials;
import luckytntlib.util.IExplosiveEntity;
import luckytntlib.util.tnteffects.PrimedTNTEffect;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.entity.Entity;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;

public class InvertedTNTEffect extends PrimedTNTEffect{

	public void serverExplosion(IExplosiveEntity entity) {
		for(float angleY = 0; angleY <= 360; angleY += 11.25f) {
			for(float angleX = 0; angleX <= 360; angleX += 11.25f) {
				double offX = 30 * ((Entity)entity).getRotationVec(1).x + Math.random() * 4f - Math.random() * 4f;
				double offY = 30 * ((Entity)entity).getRotationVec(1).y + Math.random() * 4f - Math.random() * 4f;
				double offZ = 30 * ((Entity)entity).getRotationVec(1).z + Math.random() * 4f - Math.random() * 4f;
				double magnitude = Math.sqrt(offX * offX + offY * offY + offZ * offZ) + 0.1f;
				for(int j = 1; j < magnitude; j++) {
					BlockPos pos = new BlockPos(Mth.floor(entity.x() + offX * j / magnitude), Mth.floor(entity.y() + offY * j / magnitude), Mth.floor(entity.z() + offZ * j / magnitude));
					if(entity.getLevel().getBlockState(pos).isAir() || Materials.isPlant(entity.getLevel().getBlockState(pos))) {
						if(entity.getLevel().getBlockState(new BlockPos(pos.getX(), pos.getY() - 30 * 2, pos.getZ())).getBlock().getBlastResistance() < 100) {
							entity.getLevel().setBlockState(pos, entity.getLevel().getBlockState(new BlockPos(pos.getX(), pos.getY() - 30 * 2, pos.getZ())), 3);
						}
					}
				}
				((Entity)entity).setXRot(angleX);
			}
			((Entity)entity).setYRot(angleY);
		}
	}
	
	@Override
	public Block getBlock() {
		return BlockRegistry.INVERTED_TNT.get();
	}
	
	@Override
	public int getDefaultFuse(IExplosiveEntity entity) {
		return 120;
	}
}

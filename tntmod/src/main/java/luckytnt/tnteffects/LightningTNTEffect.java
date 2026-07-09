package luckytnt.tnteffects;

import luckytnt.registry.BlockRegistry;
import luckytntlib.util.IExplosiveEntity;
import luckytntlib.util.tnteffects.PrimedTNTEffect;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LightningEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;

public class LightningTNTEffect extends PrimedTNTEffect{

	@Override
	public void explosionTick(IExplosiveEntity entity) {
		double x = entity.getPos().x;
		double z = entity.getPos().z;
		if(entity.getTNTFuse() < 120) {
			if(entity.getLevel() instanceof ServerWorld) {
				double offX = Math.random() * 40 - 20;
				double offZ = Math.random() * 40 - 20;
				for(int offY = 320; offY > -64; offY--) {
					if(!entity.getLevel().getBlockState(new BlockPos(MathHelper.floor(x + offX), offY, MathHelper.floor(z + offZ))).isAir()) {
						Entity lighting = new LightningEntity(EntityType.LIGHTNING_BOLT, entity.getLevel());
						lighting.setPosition(x + offX, offY, z + offZ);
						entity.getLevel().spawnEntity(lighting);
						break;
					}
				}
			}
		}
	}
	
	@Override
	public Block getBlock() {
		return BlockRegistry.LIGHTNING_TNT.get();
	}
	
	@Override
	public int getDefaultFuse(IExplosiveEntity entity) {
		return 200;
	}
}

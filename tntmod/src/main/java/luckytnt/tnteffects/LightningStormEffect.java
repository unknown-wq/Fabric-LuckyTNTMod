package luckytnt.tnteffects;

import java.util.List;

import luckytnt.registry.BlockRegistry;
import luckytntlib.util.IExplosiveEntity;
import luckytntlib.util.explosions.ImprovedExplosion;
import luckytntlib.util.tnteffects.PrimedTNTEffect;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LightningEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public class LightningStormEffect extends PrimedTNTEffect {

	@Override
	public void explosionTick(IExplosiveEntity ent) {
		if(ent.getTNTFuse() < 120) {
			for(int count = 0; count < 10; count++) {
				if(ent.getLevel() instanceof ServerWorld) {
					double offX = Math.random() * 150D - 75D;
					double offZ = Math.random() * 150D - 75D;
					for(int offY = ent.getLevel().getTopY(); offY > ent.getLevel().getBottomY(); offY--) {
						if(!ent.getLevel().getBlockState(new BlockPos(MathHelper.floor(ent.x() + offX), offY, MathHelper.floor(ent.z() + offZ))).isAir()) {
							Entity lighting = new LightningEntity(EntityType.LIGHTNING_BOLT, ent.getLevel());
							lighting.setPosition(ent.x() + offX, offY, ent.z() + offZ);
							ent.getLevel().spawnEntity(lighting);
							break;
						}
					}
				}
			}
		}
	}
	
	@Override
	public void serverExplosion(IExplosiveEntity ent) {
		List<LivingEntity> ents = ent.getLevel().getNonSpectatingEntities(LivingEntity.class, new Box(ent.x() - 75, ent.y() - 75, ent.z() - 75, ent.x() + 75, ent.y() + 75, ent.z() + 75));
		for(LivingEntity lent : ents) {
			for(int offY = ent.getLevel().getTopY(); offY > ent.getLevel().getBottomY(); offY--) {
				if(!ent.getLevel().getBlockState(new BlockPos(MathHelper.floor(lent.getX()), offY, MathHelper.floor(lent.getZ()))).isAir()) {
					Entity lighting = new LightningEntity(EntityType.LIGHTNING_BOLT,  ent.getLevel());
					lighting.setPosition(lent.getX(), offY, lent.getZ());
					ent.getLevel().spawnEntity(lighting);
					
					ImprovedExplosion explosion = new ImprovedExplosion(ent.getLevel(), new Vec3d(lent.getX(), offY, lent.getZ()), 3);
					explosion.doEntityExplosion(1f, true);
					explosion.doBlockExplosion(1f, 1.2f, 1f, 1.2f, false, false);
					
					break;
				}
			}
		}
	}
	
	@Override
	public Block getBlock() {
		return BlockRegistry.LIGHTNING_STORM.get();
	}
	
	@Override
	public int getDefaultFuse(IExplosiveEntity ent) {
		return 160;
	}
}

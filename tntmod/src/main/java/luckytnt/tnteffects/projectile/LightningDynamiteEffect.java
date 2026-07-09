	package luckytnt.tnteffects.projectile;

import org.joml.Vector3f;

import luckytnt.registry.ItemRegistry;
import luckytntlib.util.IExplosiveEntity;
import luckytntlib.util.tnteffects.PrimedTNTEffect;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LightningEntity;
import net.minecraft.item.Item;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;

public class LightningDynamiteEffect extends PrimedTNTEffect{

	@Override
	public void explosionTick(IExplosiveEntity entity) {
		double x = entity.getPos().x;
		double z = entity.getPos().z;
		if (entity.getLevel() instanceof ServerWorld) {
			double offX = Math.random() * 20 - 10;
			double offZ = Math.random() * 20 - 10;
			for (double offY = 320; offY > -64; offY--) {
				if (!entity.getLevel().getBlockState(new BlockPos(MathHelper.floor(x + offX), MathHelper.floor(offY), MathHelper.floor(z + offZ))).isAir()) {
					Entity lighting = new LightningEntity(EntityType.LIGHTNING_BOLT, entity.getLevel());
					lighting.setPosition(x + offX, offY, z + offZ);
					entity.getLevel().spawnEntity(lighting);
					break;
				}
			}
		}
	}
	
	@Override
	public void spawnParticles(IExplosiveEntity entity) {
		entity.getLevel().addParticle(new DustParticleEffect(new Vector3f(1f, 1f, 0.5f), 1), entity.x(), entity.y(), entity.z(), 0, 0, 0);
	}
	
	@Override
	public boolean explodesOnImpact() {
		return false;
	}
	
	@Override
	public Item getItem() {
		return ItemRegistry.LIGHTNING_DYNAMITE.get();
	}
	
	@Override
	public int getDefaultFuse(IExplosiveEntity entity) {
		return 40;
	}
}

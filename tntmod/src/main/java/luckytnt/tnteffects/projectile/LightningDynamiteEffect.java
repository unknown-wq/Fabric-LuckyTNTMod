	package luckytnt.tnteffects.projectile;

import org.joml.Vector3f;

import luckytnt.registry.ItemRegistry;
import luckytntlib.util.IExplosiveEntity;
import luckytntlib.util.tnteffects.PrimedTNTEffect;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.item.Item;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.level.levelgen.Heightmap;

public class LightningDynamiteEffect extends PrimedTNTEffect{

	/**
	 * Ticks between two bolts. A LightningBolt is an expensive vanilla entity (its own damage AABB,
	 * fire placement and mob conversions), and one per tick meant 40 of them per dynamite.
	 */
	private static final int STRIKE_INTERVAL = 5;

	@Override
	public void explosionTick(IExplosiveEntity entity) {
		double x = entity.getPos().x;
		double z = entity.getPos().z;
		if (entity.getLevel() instanceof ServerLevel serverLevel && entity.getTNTFuse() % STRIKE_INTERVAL == 0) {
			RandomSource random = serverLevel.getRandom();
			double offX = random.nextDouble() * 20 - 10;
			double offZ = random.nextDouble() * 20 - 10;
			int blockX = Mth.floor(x + offX);
			int blockZ = Mth.floor(z + offZ);
			// The old code walked ~380 block positions downwards from y=320 to find the ground. The
			// heightmap already stores exactly that answer, which is also how vanilla picks a
			// lightning target.
			int groundY = serverLevel.getHeight(Heightmap.Types.MOTION_BLOCKING, blockX, blockZ);
			Entity lighting = new LightningBolt(EntityTypes.LIGHTNING_BOLT, serverLevel);
			lighting.setPos(x + offX, groundY, z + offZ);
			serverLevel.addFreshEntity(lighting);
		}
	}
	
	@Override
	public void spawnParticles(IExplosiveEntity entity) {
		entity.getLevel().addParticle(new DustParticleOptions(((int)(1f*255)<<16)|((int)(1f*255)<<8)|(int)(0.5f*255), 1), entity.x(), entity.y(), entity.z(), 0, 0, 0);
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

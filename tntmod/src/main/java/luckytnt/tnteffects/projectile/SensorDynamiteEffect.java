package luckytnt.tnteffects.projectile;

import java.util.List;

import org.joml.Vector3f;

import luckytnt.registry.ItemRegistry;
import luckytntlib.util.IExplosiveEntity;
import luckytntlib.util.explosions.ImprovedExplosion;
import luckytntlib.util.tnteffects.PrimedTNTEffect;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.level.Level;

public class SensorDynamiteEffect extends PrimedTNTEffect{

	/** Ticks between two proximity polls. Each poll is an AABB entity query. */
	private static final int SENSOR_INTERVAL = 4;

	@Override
	public void explosionTick(IExplosiveEntity entity) {
		Level level = entity.getLevel();
		if(level instanceof ServerLevel && entity.getTNTFuse() % SENSOR_INTERVAL == 0) {
			List<Player> players = level.getEntitiesOfClass(Player.class, new AABB(entity.getPos().add(-5f, -5f, -5f), entity.getPos().add(5f, 5f, 5f)));
			for(Player player : players) {
				// Triggers on any player in range, the thrower included.
				if(player.isSpectator() || player.isRemoved()) {
					continue;
				}
				ImprovedExplosion explosion = new ImprovedExplosion(level, entity.getPos(), 5);
				explosion.doEntityExplosion(1f, true);
				explosion.doBlockExplosion(1f, 1f, 1f, 1.25f, false, false);
				level.playSound((Entity)entity, toBlockPos(entity.getPos()), SoundEvents.GENERIC_EXPLODE.value(), SoundSource.BLOCKS, 4f, (1f + (level.getRandom().nextFloat() - level.getRandom().nextFloat()) * 0.2f) * 0.7f);
				entity.destroy();
				// Without this the loop kept running on an already destroyed entity and detonated once
				// per nearby player.
				break;
			}
		}
	}
	
	@Override
	public void spawnParticles(IExplosiveEntity entity) {
		entity.getLevel().addParticle(new DustParticleOptions(((int)(1f*255)<<16)|((int)(0f*255)<<8)|(int)(0f*255), 1f), entity.x(), entity.y(), entity.z(), 0f, 0f, 0f);
	}
	
	@Override
	public boolean explodesOnImpact() {
		return false;
	}
	
	@Override
	public boolean playsSound() {
		return false;
	}
	
	@Override
	public Item getItem() {
		return ItemRegistry.SENSOR_DYNAMITE.get();
	}
	
	@Override
	public int getDefaultFuse(IExplosiveEntity entity) {
		// 60 s of armed lifetime instead of 250 s. Combined with SENSOR_INTERVAL this is ~17x fewer
		// AABB polls per thrown mine.
		return 1200;
	}
}

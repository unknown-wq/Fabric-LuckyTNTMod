package luckytnt.tnteffects;

import org.joml.Vector3f;

import luckytnt.registry.BlockRegistry;
import luckytntlib.util.IExplosiveEntity;
import luckytntlib.util.explosions.ImprovedExplosion;
import luckytntlib.util.tnteffects.PrimedTNTEffect;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.level.Level;

public class SensorTNTEffect extends PrimedTNTEffect{

	@Override
	public void explosionTick(IExplosiveEntity entity) {
		Level level = entity.getLevel();
		if(level instanceof ServerLevel) {
			// this runs every tick of a 5000 tick fuse; iterating the player list skips the
			// EntitySection walk entirely
			AABB range = new AABB(entity.getPos().add(-10f, -10f, -10f), entity.getPos().add(10f, 10f, 10f));
			for(Player player : level.players()) {
				if(player.isSpectator() || player.isRemoved() || !range.intersects(player.getBoundingBox())) {
					continue;
				}
				// Triggers on any player in range, the placer included.
				ImprovedExplosion explosion = new ImprovedExplosion(level, entity.getPos(), 10);
				explosion.doEntityExplosion(1f, true);
				explosion.doBlockExplosion(1f, 1f, 1f, 1.25f, false, false);
				level.playSound((Entity)entity, toBlockPos(entity.getPos()), SoundEvents.GENERIC_EXPLODE.value(), SoundSource.BLOCKS, 4f, (1f + (level.getRandom().nextFloat() - level.getRandom().nextFloat()) * 0.2f) * 0.7f);
				entity.destroy();
				// Stop after one detonation instead of exploding once per player in range.
				break;
			}
		}
	}
	
	@Override
	public void spawnParticles(IExplosiveEntity entity) {
		entity.getLevel().addParticle(new DustParticleOptions(((int)(1f*255)<<16)|((int)(0f*255)<<8)|(int)(0f*255), 1f), entity.x(), entity.y() + 1f, entity.z(), 0f, 0f, 0f);
	}
	
	@Override
	public boolean playsSound() {
		return false;
	}
	
	@Override
	public Block getBlock() {
		return BlockRegistry.SENSOR_TNT.get();
	}
	
	@Override
	public int getDefaultFuse(IExplosiveEntity entity) {
		return 5000;
	}
}

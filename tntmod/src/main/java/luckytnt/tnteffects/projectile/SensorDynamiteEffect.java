package luckytnt.tnteffects.projectile;

import java.util.List;

import org.joml.Vector3f;

import luckytnt.registry.ItemRegistry;
import luckytntlib.util.IExplosiveEntity;
import luckytntlib.util.explosions.ImprovedExplosion;
import luckytntlib.util.tnteffects.PrimedTNTEffect;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;

public class SensorDynamiteEffect extends PrimedTNTEffect{

	@Override
	public void explosionTick(IExplosiveEntity entity) {
		World level = entity.getLevel();
		if(level instanceof ServerWorld) {
			List<PlayerEntity> players = level.getNonSpectatingEntities(PlayerEntity.class, new Box(entity.getPos().add(-5f, -5f, -5f), entity.getPos().add(5f, 5f, 5f)));
			for(PlayerEntity player : players) {
				if(!player.equals(entity.owner())) {
					ImprovedExplosion explosion = new ImprovedExplosion(level, entity.getPos(), 5);
					explosion.doEntityExplosion(1f, true);
					explosion.doBlockExplosion(1f, 1f, 1f, 1.25f, false, false);
					level.playSound((Entity)entity, toBlockPos(entity.getPos()), SoundEvents.ENTITY_GENERIC_EXPLODE.value(), SoundCategory.BLOCKS, 4f, (1f + (level.random.nextFloat() - level.random.nextFloat()) * 0.2f) * 0.7f);
					entity.destroy();
				}
			}
		}
	}
	
	@Override
	public void spawnParticles(IExplosiveEntity entity) {
		entity.getLevel().addParticle(new DustParticleEffect(new Vector3f(1f, 0f, 0f), 1f), entity.x(), entity.y(), entity.z(), 0f, 0f, 0f);
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
		return 5000;
	}
}

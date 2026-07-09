package luckytnt.tnteffects;

import org.joml.Vector3f;

import luckytnt.registry.BlockRegistry;
import luckytntlib.util.IExplosiveEntity;
import luckytntlib.util.tnteffects.PrimedTNTEffect;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.entity.mob.ZombieHorseEntity;
import net.minecraft.entity.mob.ZombieVillagerEntity;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.server.world.ServerWorld;

public class ZombieApocalypseEffect extends PrimedTNTEffect{
	
	@Override
	public void serverExplosion(IExplosiveEntity entity) {
		for(int count = 0; count <= 30 + Math.random() * 15; count++) {
			ZombieEntity zombie = new ZombieEntity(EntityType.ZOMBIE, entity.getLevel());
			zombie.setPosition(entity.getPos());
			entity.getLevel().spawnEntity(zombie);
		}
		for(int count = 0; count <= 10 + Math.random() * 5; count++) {
			ZombieHorseEntity zombie = new ZombieHorseEntity(EntityType.ZOMBIE_HORSE, entity.getLevel());
			zombie.setPosition(entity.getPos());
			entity.getLevel().spawnEntity(zombie);
		}
		for(int count = 0; count <= 15 + Math.random() * 10; count++) {
			ZombieVillagerEntity zombie = new ZombieVillagerEntity(EntityType.ZOMBIE_VILLAGER, entity.getLevel());
			zombie.setPosition(entity.getPos());
			entity.getLevel().spawnEntity(zombie);
		}
		((ServerWorld)entity.getLevel()).setTimeOfDay(18000);
	}
	
	@Override
	public void spawnParticles(IExplosiveEntity entity) {
		entity.getLevel().addParticle(new DustParticleEffect(new Vector3f(0, 0.7f, 0), 1), entity.x(), entity.y() + 1f, entity.z(), 0, 0, 0);
	}
	
	@Override
	public Block getBlock() {
		return BlockRegistry.ZOMBIE_APOCALYPSE.get();
	}
}

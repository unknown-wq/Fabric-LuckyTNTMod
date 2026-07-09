package luckytnt.tnteffects;

import org.joml.Vector3f;

import luckytnt.registry.BlockRegistry;
import luckytntlib.util.IExplosiveEntity;
import luckytntlib.util.tnteffects.PrimedTNTEffect;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.zombie.Zombie;
import net.minecraft.world.entity.animal.equine.ZombieHorse;
import net.minecraft.world.entity.monster.zombie.ZombieVillager;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityTypes;

public class ZombieApocalypseEffect extends PrimedTNTEffect{
	
	@Override
	public void serverExplosion(IExplosiveEntity entity) {
		for(int count = 0; count <= 30 + Math.random() * 15; count++) {
			Zombie zombie = new Zombie(EntityTypes.ZOMBIE, entity.getLevel());
			zombie.setPos(entity.getPos());
			entity.getLevel().addFreshEntity(zombie);
		}
		for(int count = 0; count <= 10 + Math.random() * 5; count++) {
			ZombieHorse zombie = new ZombieHorse(EntityTypes.ZOMBIE_HORSE, entity.getLevel());
			zombie.setPos(entity.getPos());
			entity.getLevel().addFreshEntity(zombie);
		}
		for(int count = 0; count <= 15 + Math.random() * 10; count++) {
			ZombieVillager zombie = new ZombieVillager(EntityTypes.ZOMBIE_VILLAGER, entity.getLevel());
			zombie.setPos(entity.getPos());
			entity.getLevel().addFreshEntity(zombie);
		}
		((ServerLevel)entity.getLevel()).setTimeOfDay(18000);
	}
	
	@Override
	public void spawnParticles(IExplosiveEntity entity) {
		entity.getLevel().addParticle(new DustParticleOptions(new Vector3f(0, 0.7f, 0), 1), entity.x(), entity.y() + 1f, entity.z(), 0, 0, 0);
	}
	
	@Override
	public Block getBlock() {
		return BlockRegistry.ZOMBIE_APOCALYPSE.get();
	}
}

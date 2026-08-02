package luckytnt.tnteffects;

import java.util.List;

import luckytnt.registry.BlockRegistry;
import luckytntlib.entity.LTNTMinecart;
import luckytntlib.util.IExplosiveEntity;
import luckytntlib.util.tnteffects.PrimedTNTEffect;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.PrimedTnt;
import net.minecraft.world.entity.player.Player;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.phys.AABB;
import net.minecraft.util.Mth;

public class GravityTNTEffect extends PrimedTNTEffect{

	@Override
	public void explosionTick(IExplosiveEntity entity) {
		int x = Mth.floor(entity.getPos().x);
		int y = Mth.floor(entity.getPos().y);
		int z = Mth.floor(entity.getPos().z);
		// explosionTick runs on both logical sides: this 50 wide query used to be walked twice a
		// tick for 200 ticks. Players are hurtMarked so the server still pushes the velocity to them.
		if(entity.getTNTFuse() < 200 && entity.getLevel() instanceof ServerLevel) {
			List<Entity> ents = entity.getLevel().getEntities((Entity)entity, new AABB(x - 25, y - 25, z - 25, x + 25, y + 25, z + 25));
			for(Entity ent : ents) {
				if(!(ent instanceof PrimedTnt) && !(ent instanceof LTNTMinecart)) {
					double lx = ent.getX() - x;
					double ly = ent.getY() - y;
					double lz = ent.getZ() - z;
					double distance = Math.sqrt(lx * lx + ly * ly + lz * lz) + 0.1f;
					if(ent instanceof Player) {
						if(!((Player)ent).isCreative()) {
							if(distance > 2 && distance < 25 && ent.getDeltaMovement().y < 5) {
								ent.setDeltaMovement(-lx / distance, -ly / distance + 0.1f, -lz / distance);
								((Player)ent).hurtMarked = true;
							}
							else if(distance < 2) {
								ent.setDeltaMovement(ent.getDeltaMovement().x, 6, ent.getDeltaMovement().z);
								((Player)ent).hurtMarked = true;
							}
						}
					}
					else {
						if(distance > 2 && distance < 25 && ent.getDeltaMovement().y < 5)
							ent.setDeltaMovement(-lx / distance, -ly / distance + 0.1f, -lz / distance);
						else if(distance < 2)
							ent.setDeltaMovement(ent.getDeltaMovement().x, 6, ent.getDeltaMovement().z);
					}
				}
			}
		}
	}
	
	@Override
	public void spawnParticles(IExplosiveEntity entity) {
		entity.getLevel().addParticle(ParticleTypes.SMOKE, entity.x() + 2f, entity.y() + 0.5f, entity.z(), -0.2f, 0, 0);
		entity.getLevel().addParticle(ParticleTypes.SMOKE, entity.x() - 2f, entity.y() + 0.5f, entity.z(), 0.2f, 0, 0);
		entity.getLevel().addParticle(ParticleTypes.SMOKE, entity.x(), entity.y() + 0.5f, entity.z() + 2f, 0, 0, -0.2f);
		entity.getLevel().addParticle(ParticleTypes.SMOKE, entity.x(), entity.y() + 0.5f, entity.z() - 2f, 0, 0, 0.2f);
		
		entity.getLevel().addParticle(ParticleTypes.SMOKE, entity.x() + 1.5f, entity.y() + 0.5f, entity.z() + 1.5f, -0.1f, 0, -0.1f);
		entity.getLevel().addParticle(ParticleTypes.SMOKE, entity.x() - 1.5f, entity.y() + 0.5f, entity.z() - 1.5f, 0.1f, 0, 0.1f);
		entity.getLevel().addParticle(ParticleTypes.SMOKE, entity.x() + 1.5f, entity.y() + 0.5f, entity.z() - 1.5f, -0.1f, 0, 0.1f);
		entity.getLevel().addParticle(ParticleTypes.SMOKE, entity.x() - 1.5f, entity.y() + 0.5f, entity.z() + 1.5f, 0.1f, 0, -0.1f);
	}
	
	@Override
	public Block getBlock() {
		return BlockRegistry.GRAVITY_TNT.get();
	}
	
	@Override
	public int getDefaultFuse(IExplosiveEntity entity) {
		return 300;
	}
}

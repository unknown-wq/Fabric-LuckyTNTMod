package luckytnt.tnteffects;

import java.util.List;

import luckytnt.registry.BlockRegistry;
import luckytntlib.entity.LTNTMinecart;
import luckytntlib.util.IExplosiveEntity;
import luckytntlib.util.tnteffects.PrimedTNTEffect;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.TntEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;

public class GravityTNTEffect extends PrimedTNTEffect{

	@Override
	public void explosionTick(IExplosiveEntity entity) {
		int x = MathHelper.floor(entity.getPos().x);
		int y = MathHelper.floor(entity.getPos().y);
		int z = MathHelper.floor(entity.getPos().z);
		if(entity.getTNTFuse() < 200) {
			BlockPos min = new BlockPos(x - 25, y - 25, z - 25);
			BlockPos max = new BlockPos(x + 25, y + 25, z + 25);
			List<Entity> ents = entity.getLevel().getOtherEntities((Entity)entity, new Box(min.getX(), min.getY(), min.getZ(), max.getX(), max.getY(), max.getZ()));
			for(Entity ent : ents) {
				if(!(ent instanceof TntEntity) && !(ent instanceof LTNTMinecart)) {
					double lx = ent.getX() - x;
					double ly = ent.getY() - y;
					double lz = ent.getZ() - z;
					double distance = Math.sqrt(lx * lx + ly * ly + lz * lz) + 0.1f;
					if(ent instanceof PlayerEntity) {
						if(!((PlayerEntity)ent).isCreative())
							if(distance > 2 && distance < 25 && ent.getVelocity().y < 5)
								ent.setVelocity(-lx / distance, -ly / distance + 0.1f, -lz / distance);
							else if(distance < 2)
								ent.setVelocity(ent.getVelocity().x, 6, ent.getVelocity().z);
					}
					else {
						if(distance > 2 && distance < 25 && ent.getVelocity().y < 5)
							ent.setVelocity(-lx / distance, -ly / distance + 0.1f, -lz / distance);
						else if(distance < 2)
							ent.setVelocity(ent.getVelocity().x, 6, ent.getVelocity().z);
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

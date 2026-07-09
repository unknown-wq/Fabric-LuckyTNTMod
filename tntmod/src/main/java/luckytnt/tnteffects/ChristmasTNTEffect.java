package luckytnt.tnteffects;

import java.util.Random;

import luckytnt.block.ChristmasTNTBlock;
import luckytnt.registry.BlockRegistry;
import luckytnt.registry.EntityRegistry;
import luckytntlib.entity.LExplosiveProjectile;
import luckytntlib.entity.PrimedLTNT;
import luckytntlib.util.IExplosiveEntity;
import luckytntlib.util.tnteffects.PrimedTNTEffect;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.packet.s2c.play.EntityVelocityUpdateS2CPacket;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;

public class ChristmasTNTEffect extends PrimedTNTEffect{
	
	@Override
	public void serverExplosion(IExplosiveEntity entity) {
		if(entity instanceof PrimedLTNT) {
			((ServerWorld)entity.getLevel()).spawnParticles(ParticleTypes.WAX_OFF, entity.x() + Math.random() - 0.5f, entity.y() + 1 + Math.random() * 0.5f, entity.z() + Math.random() - 0.5f, 500, 0.5f, 0.5f, 0.5f, 0f);
		}
		else {			
			((ServerWorld)entity.getLevel()).spawnParticles(ParticleTypes.WAX_OFF, entity.x() + Math.random() - 0.5f, entity.y() + 1 + Math.random() * 0.5f, entity.z() + Math.random() - 0.5f, 100, 0.5f, 0.5f, 0.5f, 0f);
		}
	}
	
	@Override
	public void explosionTick(IExplosiveEntity entity) {
		if(entity instanceof PrimedLTNT) {
			if(entity.getTNTFuse() == 240) {
				((Entity)entity).setNoGravity(true);
				Vec3d flying = new Vec3d(Math.random() - Math.random(), 0, Math.random() - Math.random()).normalize().multiply(40);
				NbtCompound tag = entity.getPersistentData();
				tag.putDouble("flyingX", flying.x);
				tag.putDouble("flyingY", flying.y);
				tag.putDouble("flyingZ", flying.z);
				entity.setPersistentData(tag);
				Vec3d flyingPos = new Vec3d(entity.x() + flying.negate().normalize().multiply(20).x, entity.y() + 30, entity.z() + flying.negate().normalize().multiply(20).z);
				((Entity)entity).setPosition(flyingPos.x, flyingPos.y, flyingPos.z);
			}
			if(entity.getTNTFuse() <= 220) {
				((Entity)entity).setVelocity(new Vec3d(entity.getPersistentData().getDouble("flyingX"), entity.getPersistentData().getDouble("flyingY"), entity.getPersistentData().getDouble("flyingZ")).normalize().multiply(40D / 220D));
				if(entity.getTNTFuse() % 10 == 0) {
					LExplosiveProjectile present = EntityRegistry.PRESENT.get().create(entity.getLevel());
					present.setPosition(entity.getPos());
					present.setOwner(entity.owner());
					double randomX = Math.random();
					randomX *= new Random().nextBoolean() ? 1 : -1;
					double randomZ = Math.random();
					randomZ *= new Random().nextBoolean() ? 1 : -1;
					present.setVelocity(randomX, -Math.random() * 0.5f, randomZ);
					entity.getLevel().spawnEntity(present);
				}
			}
			if(entity.getLevel() instanceof ServerWorld sLevel) {
				for(ServerPlayerEntity player : sLevel.getPlayers()) {
					double x = player.getX() - entity.x();
					double y = player.getY() - entity.y();
					double z = player.getZ() - entity.z();
					double distance = Math.sqrt(x * x + y * y + z * z);
					if(distance <= 200) {
						player.networkHandler.sendPacket(new EntityVelocityUpdateS2CPacket((Entity)entity));
					}
				}
			}
		}
	}
	
	@Override
	public void spawnParticles(IExplosiveEntity entity) {
		if(entity instanceof PrimedLTNT) {
			if(entity.getTNTFuse() < 230) {
				for(int i = 0; i <= 10; i++) {
					entity.getLevel().addParticle(ParticleTypes.WAX_OFF, true, entity.x() + Math.random() - 0.5f, entity.y() + 1f + Math.random() * 0.5f, entity.z() + Math.random() - 0.5f, 0, 0, 0);
				}
			}
			else {
				super.spawnParticles(entity);
			}
		}
	}
	
	@Override
	public boolean airFuse() {
		return true;
	}
	
	@Override
	public BlockState getBlockState(IExplosiveEntity entity) {
		return entity instanceof PrimedLTNT ? BlockRegistry.CHRISTMAS_TNT.get().getDefaultState() : BlockRegistry.CHRISTMAS_TNT.get().getDefaultState().with(ChristmasTNTBlock.ONLY_PRESENT, true);
	}
	
	@Override
	public int getDefaultFuse(IExplosiveEntity entity) {
		return entity instanceof PrimedLTNT ? 300 : 10000;
	}
}

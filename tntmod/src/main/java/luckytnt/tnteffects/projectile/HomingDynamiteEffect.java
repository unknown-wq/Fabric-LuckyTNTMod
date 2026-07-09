package luckytnt.tnteffects.projectile;

import java.util.List;

import org.jetbrains.annotations.Nullable;

import luckytnt.registry.ItemRegistry;
import luckytntlib.util.IExplosiveEntity;
import luckytntlib.util.explosions.ImprovedExplosion;
import luckytntlib.util.tnteffects.PrimedTNTEffect;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.packet.s2c.play.EntityVelocityUpdateS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class HomingDynamiteEffect extends PrimedTNTEffect{

	@Override
	public void serverExplosion(IExplosiveEntity entity) {
		ImprovedExplosion explosion = new ImprovedExplosion(entity.getLevel(), (Entity)entity, entity.getPos(), 20);
		explosion.doEntityExplosion(1.5f, true);
		explosion.doBlockExplosion(1f, 1f, 1f, 1.25f, false, false);
	}
	
	@Override
	public void explosionTick(IExplosiveEntity entity) {
		if(entity.getTNTFuse() < 390) {
			Entity target = entity.getLevel().getEntityById(entity.getPersistentData().getInt("targetID"));
			if(target == null) {
				target = setTarget(entity);
			}
			else {
				Vec3d movement = target.getLerpedPos(1f).subtract(entity.getPos()).normalize();
				((Entity)entity).setVelocity(movement);
				if(entity.getLevel() instanceof ServerWorld server) {
					for(ServerPlayerEntity splayer : server.getPlayers()) {
						splayer.networkHandler.sendPacket(new EntityVelocityUpdateS2CPacket(((Entity)entity).getId(), ((Entity)entity).getVelocity()));
					}
				}
			}
		}
	}
	
	@Nullable
	public Entity setTarget(IExplosiveEntity entity) {
		World level = entity.getLevel();
		Entity target = null;
		List<PlayerEntity> players = level.getNonSpectatingEntities(PlayerEntity.class, new Box(entity.getPos().add(-100, -100, -100), entity.getPos().add(100, 100, 100)));
		double distance = Math.sqrt(20000);
		for(PlayerEntity player : players) {
			double entityDistance = entity.getPos().distanceTo(player.getLerpedPos(1f));
			if(!player.equals(entity.owner()) && entityDistance <= distance) {
				NbtCompound tag = entity.getPersistentData();
				tag.putInt("targetID", player.getId());
				entity.setPersistentData(tag);
				distance = entityDistance;
				target = player;
			}
		}
		if(target == null) {
			distance = Math.sqrt(20000);
			List<LivingEntity> livingEntities = level.getNonSpectatingEntities(LivingEntity.class, new Box(entity.getPos().add(-100, -100, -100), entity.getPos().add(100, 100, 100)));
			for(LivingEntity ent : livingEntities) {
				double entityDistance = entity.getPos().distanceTo(ent.getLerpedPos(1f));
				if(!ent.equals(entity.owner()) && entityDistance <= distance) {
					NbtCompound tag = entity.getPersistentData();
					tag.putInt("targetID", ent.getId());
					entity.setPersistentData(tag);
					distance = entityDistance;
					target = ent;
				}
			}
		}
		return target;
	}
	
	@Override
	public boolean airFuse() {
		return true;
	}
	
	@Override
	public Item getItem() {
		return ItemRegistry.HOMING_DYNAMITE.get();
	}
	
	@Override
	public int getDefaultFuse(IExplosiveEntity entity) {
		return 400;
	}
}

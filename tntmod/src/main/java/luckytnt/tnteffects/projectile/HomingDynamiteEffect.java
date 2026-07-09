package luckytnt.tnteffects.projectile;

import java.util.List;

import org.jetbrains.annotations.Nullable;

import luckytnt.registry.ItemRegistry;
import luckytntlib.util.IExplosiveEntity;
import luckytntlib.util.explosions.ImprovedExplosion;
import luckytntlib.util.tnteffects.PrimedTNTEffect;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundSetEntityMotionPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.Level;

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
			Entity target = entity.getLevel().getEntity(entity.getPersistentData().getIntOr("targetID", 0));
			if(target == null) {
				target = setTarget(entity);
			}
			else {
				Vec3 movement = target.position().subtract(entity.getPos()).normalize();
				((Entity)entity).setDeltaMovement(movement);
				if(entity.getLevel() instanceof ServerLevel server) {
					for(ServerPlayer splayer : server.players()) {
						splayer.connection.send(new ClientboundSetEntityMotionPacket((Entity)entity));
					}
				}
			}
		}
	}
	
	@Nullable
	public Entity setTarget(IExplosiveEntity entity) {
		Level level = entity.getLevel();
		Entity target = null;
		List<Player> players = level.getEntitiesOfClass(Player.class, new AABB(entity.getPos().add(-100, -100, -100), entity.getPos().add(100, 100, 100)));
		double distance = Math.sqrt(20000);
		for(Player player : players) {
			double entityDistance = entity.getPos().distanceTo(player.position());
			if(!player.equals(entity.owner()) && entityDistance <= distance) {
				CompoundTag tag = entity.getPersistentData();
				tag.putInt("targetID", player.getId());
				entity.setPersistentData(tag);
				distance = entityDistance;
				target = player;
			}
		}
		if(target == null) {
			distance = Math.sqrt(20000);
			List<LivingEntity> livingEntities = level.getEntitiesOfClass(LivingEntity.class, new AABB(entity.getPos().add(-100, -100, -100), entity.getPos().add(100, 100, 100)));
			for(LivingEntity ent : livingEntities) {
				double entityDistance = entity.getPos().distanceTo(ent.position());
				if(!ent.equals(entity.owner()) && entityDistance <= distance) {
					CompoundTag tag = entity.getPersistentData();
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

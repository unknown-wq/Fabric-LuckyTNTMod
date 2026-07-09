package luckytnt.registry;

import luckytnt.entity.AngryMiner;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.mob.MobEntity;

public class AttributeRegistry {

	public static void init() {
		FabricDefaultAttributeRegistry.register(EntityRegistry.ANGRY_MINER.get(), AngryMiner.createAttributes().build());
		FabricDefaultAttributeRegistry.register(EntityRegistry.ATTACKING_TNT.get(), MobEntity.createMobAttributes().add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.4f).add(EntityAttributes.GENERIC_MAX_HEALTH, 1024).add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 5).build());
		FabricDefaultAttributeRegistry.register(EntityRegistry.WALKING_TNT.get(), MobEntity.createMobAttributes().add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.4f).add(EntityAttributes.GENERIC_MAX_HEALTH, 1024).build());
		FabricDefaultAttributeRegistry.register(EntityRegistry.VICIOUS_TNT.get(), MobEntity.createMobAttributes().add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.5f).add(EntityAttributes.GENERIC_MAX_HEALTH, 1024).add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 10).build());
		FabricDefaultAttributeRegistry.register(EntityRegistry.EVIL_TNT.get(), MobEntity.createMobAttributes().add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.6f).add(EntityAttributes.GENERIC_MAX_HEALTH, 1024).add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 20).build());
	}
}

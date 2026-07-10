package luckytnt.registry;

import luckytnt.entity.AngryMiner;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.Mob;

public class AttributeRegistry {

	public static void init() {
		FabricDefaultAttributeRegistry.register(EntityRegistry.ANGRY_MINER.get(), AngryMiner.createAttributes().build());
		FabricDefaultAttributeRegistry.register(EntityRegistry.ATTACKING_TNT.get(), Mob.createMobAttributes().add(Attributes.MOVEMENT_SPEED, 0.4f).add(Attributes.MAX_HEALTH, 1024).add(Attributes.ATTACK_DAMAGE, 5).build());
		FabricDefaultAttributeRegistry.register(EntityRegistry.WALKING_TNT.get(), Mob.createMobAttributes().add(Attributes.MOVEMENT_SPEED, 0.4f).add(Attributes.MAX_HEALTH, 1024).build());
		FabricDefaultAttributeRegistry.register(EntityRegistry.VICIOUS_TNT.get(), Mob.createMobAttributes().add(Attributes.MOVEMENT_SPEED, 0.5f).add(Attributes.MAX_HEALTH, 1024).add(Attributes.ATTACK_DAMAGE, 10).build());
		FabricDefaultAttributeRegistry.register(EntityRegistry.EVIL_TNT.get(), Mob.createMobAttributes().add(Attributes.MOVEMENT_SPEED, 0.6f).add(Attributes.MAX_HEALTH, 1024).add(Attributes.ATTACK_DAMAGE, 20).build());
	}
}

package luckytnt.registry;

import luckytnt.event.EntityLivingEvent;
import luckytnt.event.LevelEvents;
import net.fabricmc.api.EnvType;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.entity.LivingEntity;
import net.minecraft.server.world.ServerWorld;

public class EventRegistry {
	
	public static final Event<LivingEntityTick> LIVING_ENTITY_TICK = EventFactory.createArrayBacked(LivingEntityTick.class, callbacks -> livingEnt -> {
		for (LivingEntityTick callback : callbacks) {
			callback.onLivingTick(livingEnt);
		}
	});

	public static void init() {
		if(FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT) {
			ClientEventRegistry.init();
		}
		
		LIVING_ENTITY_TICK.register(new LivingEntityTick() {
			
			@Override
			public void onLivingTick(LivingEntity ent) {
				EntityLivingEvent.playerLivingTick(ent);
			}
		});
		LIVING_ENTITY_TICK.register(new LivingEntityTick() {
			
			@Override
			public void onLivingTick(LivingEntity ent) {
				EntityLivingEvent.onLivingTick(ent);
			}
		});
		ServerTickEvents.START_WORLD_TICK.register(new ServerTickEvents.StartWorldTick() {
			
			@Override
			public void onStartTick(ServerWorld world) {
				LevelEvents.onLevelUpdate(world);
			}
		});
	}
	
	@FunctionalInterface
	public interface LivingEntityTick {
		public void onLivingTick(LivingEntity ent);
	}
}

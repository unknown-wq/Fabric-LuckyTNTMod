package luckytnt.registry;

import java.util.function.Supplier;

import luckytnt.LuckyTNTMod;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.Registry;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.resources.Identifier;

public class SoundRegistry {
	
	public static Supplier<SoundEvent> SAY_GOODBYE = register("say_goodbye");
	public static Supplier<SoundEvent> DEATH_RAY = register("death_ray");
	public static Supplier<SoundEvent> VACUUM_CLEANER_START = register("vacuum_cleaner_start");
	public static Supplier<SoundEvent> VACUUM_CLEANER = register("vacuum_cleaner");
	
	public static Supplier<SoundEvent> register(String name){
		SoundEvent event = Registry.register(BuiltInRegistries.SOUND_EVENT, Identifier.fromNamespaceAndPath(LuckyTNTMod.MODID, name), SoundEvent.createVariableRangeEvent(Identifier.fromNamespaceAndPath(LuckyTNTMod.MODID, name)));
		return () -> event;
	}
	
	public static void init() {}
}

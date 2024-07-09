package luckytnt.registry;

import java.util.function.Supplier;

import luckytnt.LuckyTNTMod;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;

public class SoundRegistry {
	
	public static Supplier<SoundEvent> SAY_GOODBYE = register("say_goodbye");
	public static Supplier<SoundEvent> DEATH_RAY = register("death_ray");
	public static Supplier<SoundEvent> VACUUM_CLEANER_START = register("vacuum_cleaner_start");
	public static Supplier<SoundEvent> VACUUM_CLEANER = register("vacuum_cleaner");
	
	public static Supplier<SoundEvent> register(String name){
		SoundEvent event = Registry.register(Registries.SOUND_EVENT, Identifier.of(LuckyTNTMod.MODID, name), SoundEvent.of(Identifier.of(LuckyTNTMod.MODID, name)));
		return () -> event;
	}
	
	public static void init() {}
}

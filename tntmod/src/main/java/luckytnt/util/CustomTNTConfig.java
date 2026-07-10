package luckytnt.util;

import luckytntlib.config.common.StringRepresentable;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Component;

public enum CustomTNTConfig implements StringRepresentable {	

	NO_EXPLOSION(Component.translatable("luckytntmod.config.no_tnt"), "no_explosion"),
	NORMAL_EXPLOSION(Component.translatable("luckytntmod.config.normal_tnt"), "normal_explosion"),
	SPHERICAL_EXPLOSION(Component.translatable("luckytntmod.config.spherical_tnt"), "spherical_explosion"),
	CUBICAL_EXPLOSION(Component.translatable("luckytntmod.config.cubical_tnt"), "cubical_explosion"),
	EASTER_EGG(Component.translatable("luckytntmod.config.easter_egg_tnt"), "easter_egg"),
	FIREWORK(Component.translatable("luckytntmod.config.firework_tnt"), "firework");
	
	private final MutableComponent text;
	private final String name;
	
	private CustomTNTConfig(MutableComponent text, String name) {
		this.text = text;
		this.name = name;
	}
	
	public MutableComponent getComponent() {
		return text;
	}

	@Override
	public String getString() {
		return name;
	}
}

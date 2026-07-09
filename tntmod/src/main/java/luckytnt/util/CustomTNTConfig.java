package luckytnt.util;

import luckytntlib.config.common.StringRepresentable;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;

public enum CustomTNTConfig implements StringRepresentable {	

	NO_EXPLOSION(Text.translatable("luckytntmod.config.no_tnt"), "no_explosion"),
	NORMAL_EXPLOSION(Text.translatable("luckytntmod.config.normal_tnt"), "normal_explosion"),
	SPHERICAL_EXPLOSION(Text.translatable("luckytntmod.config.spherical_tnt"), "spherical_explosion"),
	CUBICAL_EXPLOSION(Text.translatable("luckytntmod.config.cubical_tnt"), "cubical_explosion"),
	EASTER_EGG(Text.translatable("luckytntmod.config.easter_egg_tnt"), "easter_egg"),
	FIREWORK(Text.translatable("luckytntmod.config.firework_tnt"), "firework");
	
	private final MutableText text;
	private final String name;
	
	private CustomTNTConfig(MutableText text, String name) {
		this.text = text;
		this.name = name;
	}
	
	public MutableText getComponent() {
		return text;
	}

	@Override
	public String getString() {
		return name;
	}
}

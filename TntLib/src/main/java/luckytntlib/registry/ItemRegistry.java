package luckytntlib.registry;

/**
 * Registers the Config Item, which can be used to add your own config screens
 */
import java.util.function.Supplier;

import luckytntlib.LuckyTNTLib;
import luckytntlib.item.TNTConfigItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ItemRegistry {
	
	public static final Supplier<Item> CONFIG_ITEM = registerItem("tnt_config", new TNTConfigItem());

	public static Supplier<Item> registerItem(String name, Item item) {
		Item ritem = Registry.register(Registries.ITEM, Identifier.of(LuckyTNTLib.MODID, name), item);
		return () -> ritem;
	}
	
	public static void init() {}
}

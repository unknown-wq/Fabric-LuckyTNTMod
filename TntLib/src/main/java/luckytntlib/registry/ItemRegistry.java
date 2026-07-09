package luckytntlib.registry;

/**
 * Registers the Config Item, which can be used to add your own config screens
 */
import java.util.function.Supplier;

import luckytntlib.LuckyTNTLib;
import luckytntlib.item.TNTConfigItem;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;

public class ItemRegistry {

	public static final Supplier<Item> CONFIG_ITEM = registerItem("tnt_config", new TNTConfigItem());

	public static Supplier<Item> registerItem(String name, Item item) {
		Item ritem = Registry.register(BuiltInRegistries.ITEM, Identifier.fromNamespaceAndPath(LuckyTNTLib.MODID, name), item);
		return () -> ritem;
	}

	public static void init() {}
}

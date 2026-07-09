package luckytnt.registry;

import java.util.function.Supplier;

import luckytnt.LuckyTNTMod;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

public class LuckyTNTTabs {

	public static CreativeModeTab NORMAL_TNT;
	public static CreativeModeTab GOD_TNT;
	public static CreativeModeTab DOOMSDAY_TNT;
	public static CreativeModeTab DYNAMITE;
	public static CreativeModeTab MINECART;
	public static CreativeModeTab OTHER;
	
	public static void init() {
		NORMAL_TNT = FabricItemGroup.builder().title(Component.translatable("item_group.luckytntmod.normal_tnt")).icon(() -> new ItemStack(BlockRegistry.METEOR_TNT.get())).displayItems((enabledFlags, populator) -> {
			for(Supplier<? extends Item> item : LuckyTNTMod.RH.creativeTabItemLists.get("n")) {
				populator.accept(item.get());
			}
        }).build();
		
		GOD_TNT = FabricItemGroup.builder().title(Component.translatable("item_group.luckytntmod.god_tnt")).icon(() -> new ItemStack(BlockRegistry.THE_REVOLUTION.get())).displayItems((enabledFlags, populator) -> {
			for(Supplier<? extends Item> item : LuckyTNTMod.RH.creativeTabItemLists.get("g")) {
				populator.accept(item.get());
			}
        }).build();
		
		DOOMSDAY_TNT = FabricItemGroup.builder().title(Component.translatable("item_group.luckytntmod.doomsday_tnt")).icon(() -> new ItemStack(BlockRegistry.CHUNK_TNT.get())).displayItems((enabledFlags, populator) -> {
			for(Supplier<? extends Item> item : LuckyTNTMod.RH.creativeTabItemLists.get("d")) {
				populator.accept(item.get());
			}
        }).build();
		
		DYNAMITE = FabricItemGroup.builder().title(Component.translatable("item_group.luckytntmod.dynamite")).icon(() -> new ItemStack(ItemRegistry.DYNAMITE.get())).displayItems((enabledFlags, populator) -> {
			for(Supplier<? extends Item> item : LuckyTNTMod.RH.creativeTabItemLists.get("dy")) {
				populator.accept(item.get());
			}
        }).build();
		
		MINECART = FabricItemGroup.builder().title(Component.translatable("item_group.luckytntmod.minecarts")).icon(() -> new ItemStack(ItemRegistry.TNT_X5_MINECART.get())).displayItems((enabledFlags, populator) -> {
			for(Supplier<? extends Item> item : LuckyTNTMod.RH.creativeTabItemLists.get("m")) {
				populator.accept(item.get());
			}
        }).build();
		
		OTHER = FabricItemGroup.builder().title(Component.translatable("item_group.luckytntmod.other")).icon(() -> new ItemStack(ItemRegistry.BLUE_CANDY.get())).displayItems((enabledFlags, populator) -> {
			populator.accept(ItemRegistry.NUCLEAR_WASTE.get());
			populator.accept(ItemRegistry.RED_CANDY.get());
			populator.accept(ItemRegistry.GREEN_CANDY.get());
			populator.accept(ItemRegistry.BLUE_CANDY.get());
			populator.accept(ItemRegistry.PURPLE_CANDY.get());
			populator.accept(ItemRegistry.YELLOW_CANDY.get());			
			populator.accept(ItemRegistry.URANIUM_INGOT.get());			
			populator.accept(ItemRegistry.ANTIMATTER.get());			
			populator.accept(ItemRegistry.URANIUM_ORE.get());			
			populator.accept(ItemRegistry.DEEPSLATE_URANIUM_ORE.get());			
			populator.accept(ItemRegistry.GUNPOWDER_ORE.get());			
			populator.accept(ItemRegistry.DEEPSLATE_GUNPOWDER_ORE.get());
			populator.accept(ItemRegistry.CONFIGURATION_WAND.get());
			populator.accept(ItemRegistry.OBSIDIAN_RAIL.get());
			populator.accept(ItemRegistry.OBSIDIAN_POWERED_RAIL.get());
			populator.accept(ItemRegistry.OBSIDIAN_ACTIVATOR_RAIL.get());
			populator.accept(ItemRegistry.OBSIDIAN_DETECTOR_RAIL.get());
			populator.accept(ItemRegistry.DEATH_RAY_RAY.get());
			populator.accept(ItemRegistry.VACUUM_CLEANER.get());
			populator.accept(ItemRegistry.TOXIC_STONE.get());
        }).build();
		
		Registry.register(BuiltInRegistries.CREATIVE_MODE_TAB, Identifier.fromNamespaceAndPath(LuckyTNTMod.MODID, "luckytntmod_a_normal_tnt"), NORMAL_TNT);
		Registry.register(BuiltInRegistries.CREATIVE_MODE_TAB, Identifier.fromNamespaceAndPath(LuckyTNTMod.MODID, "luckytntmod_b_god_tnt"), GOD_TNT);
		Registry.register(BuiltInRegistries.CREATIVE_MODE_TAB, Identifier.fromNamespaceAndPath(LuckyTNTMod.MODID, "luckytntmod_c_doomsday_tnt"), DOOMSDAY_TNT);
		Registry.register(BuiltInRegistries.CREATIVE_MODE_TAB, Identifier.fromNamespaceAndPath(LuckyTNTMod.MODID, "luckytntmod_d_dynamite"), DYNAMITE);
		Registry.register(BuiltInRegistries.CREATIVE_MODE_TAB, Identifier.fromNamespaceAndPath(LuckyTNTMod.MODID, "luckytntmod_e_minecarts"), MINECART);
		Registry.register(BuiltInRegistries.CREATIVE_MODE_TAB, Identifier.fromNamespaceAndPath(LuckyTNTMod.MODID, "luckytntmod_f_other"), OTHER);
	}
}

package luckytnt.registry;

import java.util.function.Supplier;

import luckytnt.LuckyTNTMod;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class LuckyTNTTabs {

	public static ItemGroup NORMAL_TNT;
	public static ItemGroup GOD_TNT;
	public static ItemGroup DOOMSDAY_TNT;
	public static ItemGroup DYNAMITE;
	public static ItemGroup MINECART;
	public static ItemGroup OTHER;
	
	public static void init() {
		NORMAL_TNT = FabricItemGroup.builder().displayName(Text.translatable("item_group.luckytntmod.normal_tnt")).icon(() -> new ItemStack(BlockRegistry.METEOR_TNT.get())).entries((enabledFlags, populator) -> {
			for(Supplier<? extends Item> item : LuckyTNTMod.RH.creativeTabItemLists.get("n")) {
				populator.add(item.get());
			}
        }).build();
		
		GOD_TNT = FabricItemGroup.builder().displayName(Text.translatable("item_group.luckytntmod.god_tnt")).icon(() -> new ItemStack(BlockRegistry.THE_REVOLUTION.get())).entries((enabledFlags, populator) -> {
			for(Supplier<? extends Item> item : LuckyTNTMod.RH.creativeTabItemLists.get("g")) {
				populator.add(item.get());
			}
        }).build();
		
		DOOMSDAY_TNT = FabricItemGroup.builder().displayName(Text.translatable("item_group.luckytntmod.doomsday_tnt")).icon(() -> new ItemStack(BlockRegistry.CHUNK_TNT.get())).entries((enabledFlags, populator) -> {
			for(Supplier<? extends Item> item : LuckyTNTMod.RH.creativeTabItemLists.get("d")) {
				populator.add(item.get());
			}
        }).build();
		
		DYNAMITE = FabricItemGroup.builder().displayName(Text.translatable("item_group.luckytntmod.dynamite")).icon(() -> new ItemStack(ItemRegistry.DYNAMITE.get())).entries((enabledFlags, populator) -> {
			for(Supplier<? extends Item> item : LuckyTNTMod.RH.creativeTabItemLists.get("dy")) {
				populator.add(item.get());
			}
        }).build();
		
		MINECART = FabricItemGroup.builder().displayName(Text.translatable("item_group.luckytntmod.minecarts")).icon(() -> new ItemStack(ItemRegistry.TNT_X5_MINECART.get())).entries((enabledFlags, populator) -> {
			for(Supplier<? extends Item> item : LuckyTNTMod.RH.creativeTabItemLists.get("m")) {
				populator.add(item.get());
			}
        }).build();
		
		OTHER = FabricItemGroup.builder().displayName(Text.translatable("item_group.luckytntmod.other")).icon(() -> new ItemStack(ItemRegistry.BLUE_CANDY.get())).entries((enabledFlags, populator) -> {
			populator.add(ItemRegistry.NUCLEAR_WASTE.get());
			populator.add(ItemRegistry.RED_CANDY.get());
			populator.add(ItemRegistry.GREEN_CANDY.get());
			populator.add(ItemRegistry.BLUE_CANDY.get());
			populator.add(ItemRegistry.PURPLE_CANDY.get());
			populator.add(ItemRegistry.YELLOW_CANDY.get());			
			populator.add(ItemRegistry.URANIUM_INGOT.get());			
			populator.add(ItemRegistry.ANTIMATTER.get());			
			populator.add(ItemRegistry.URANIUM_ORE.get());			
			populator.add(ItemRegistry.DEEPSLATE_URANIUM_ORE.get());			
			populator.add(ItemRegistry.GUNPOWDER_ORE.get());			
			populator.add(ItemRegistry.DEEPSLATE_GUNPOWDER_ORE.get());
			populator.add(ItemRegistry.CONFIGURATION_WAND.get());
			populator.add(ItemRegistry.OBSIDIAN_RAIL.get());
			populator.add(ItemRegistry.OBSIDIAN_POWERED_RAIL.get());
			populator.add(ItemRegistry.OBSIDIAN_ACTIVATOR_RAIL.get());
			populator.add(ItemRegistry.OBSIDIAN_DETECTOR_RAIL.get());
			populator.add(ItemRegistry.DEATH_RAY_RAY.get());
			populator.add(ItemRegistry.VACUUM_CLEANER.get());
			populator.add(ItemRegistry.TOXIC_STONE.get());
        }).build();
		
		Registry.register(Registries.ITEM_GROUP, Identifier.of(LuckyTNTMod.MODID, "luckytntmod_a_normal_tnt"), NORMAL_TNT);
		Registry.register(Registries.ITEM_GROUP, Identifier.of(LuckyTNTMod.MODID, "luckytntmod_b_god_tnt"), GOD_TNT);
		Registry.register(Registries.ITEM_GROUP, Identifier.of(LuckyTNTMod.MODID, "luckytntmod_c_doomsday_tnt"), DOOMSDAY_TNT);
		Registry.register(Registries.ITEM_GROUP, Identifier.of(LuckyTNTMod.MODID, "luckytntmod_d_dynamite"), DYNAMITE);
		Registry.register(Registries.ITEM_GROUP, Identifier.of(LuckyTNTMod.MODID, "luckytntmod_e_minecarts"), MINECART);
		Registry.register(Registries.ITEM_GROUP, Identifier.of(LuckyTNTMod.MODID, "luckytntmod_f_other"), OTHER);
	}
}

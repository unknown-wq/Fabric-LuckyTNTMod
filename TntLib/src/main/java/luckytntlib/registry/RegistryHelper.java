package luckytntlib.registry;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.function.Supplier;

import org.jetbrains.annotations.Nullable;

import com.mojang.datafixers.util.Pair;

import luckytntlib.block.LTNTBlock;
import luckytntlib.block.LivingLTNTBlock;
import luckytntlib.block.LuckyTNTBlock;
import luckytntlib.config.common.ConfigScreenFactory;
import luckytntlib.entity.LExplosiveProjectile;
import luckytntlib.entity.LTNTMinecart;
import luckytntlib.entity.LivingPrimedLTNT;
import luckytntlib.entity.LuckyTNTMinecart;
import luckytntlib.entity.PrimedLTNT;
import luckytntlib.item.LDynamiteItem;
import luckytntlib.item.LTNTMinecartItem;
import luckytntlib.item.LuckyDynamiteItem;
import luckytntlib.util.dispenser.DispenserBehaviorHelper;
import luckytntlib.util.tnteffects.PrimedTNTEffect;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Blocks;
import net.minecraft.block.FireBlock;
import net.minecraft.block.MapColor;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

/**
 * The RegistryHelper offers many methods with varying complexity for each important part of the TNT/Dynamite/TNT Minecart registering 
 * and even allows easy registration of {@link DispenseItemBehavior} where possible.
 * On top of all this it also saves Lists of TNT, dynamite and minecarts in a {@link HashMap} with the corresponding string assigned while registering.
 * These lists can be used to simply add the items into a tab or pass the whole list to the {@link LuckyTNTBlock}, {@link LuckyDynamiteItem} and {@link LuckyTNTMinecart} respectively.
 */
public class RegistryHelper {
	
	private final String blockModid;
	private final String itemModid;
	private final String entityModid;
	
	/**
	 * {@link List} that contains {@link Pair}s that cointain all registered {@link ConfigScreenFactory}s along with
	 * their Names in the form of {@link Text}
	 */
	public static final List<Pair<Text, ConfigScreenFactory>> configScreens = new ArrayList<>();
	/**
	 * {@link HashMap}, with strings as keys, of Lists of all registered TNT blocks.
	 * The key is in this case the variable 'tab' in the individual register method
	 */
	public final HashMap<String, List<Supplier<LTNTBlock>>> TNTLists = new HashMap<>();
	/**
	 * {@link HashMap}, with strings as keys, of Lists of all registered dynamite items.
	 * The key is in this case the variable 'tab' in the individual register method
	 */
	public final HashMap<String, List<Supplier<LDynamiteItem>>> dynamiteLists = new HashMap<>();
	/**
	 * {@link HashMap}, with strings as keys, of Lists of all registered TNT minecart items.
	 * The key is in this case the variable 'tab' in the individual register method
	 */
	public final HashMap<String, List<Supplier<LTNTMinecartItem>>> minecartLists = new HashMap<>();
	
	/**
	 * {@link HashMap}, with strings as keys, of Lists of all registered items, if the strings passed were not 'none'.
	 * The key is in this case the variable 'tab' in the individual register method
	 */
	public final HashMap<String, List<Supplier<? extends Item>>> creativeTabItemLists = new HashMap<>();
	
	/**
	 * Creates a new instance of the RegistryHelper
	 * @param blockModid  the {@link String} under which name all blocks will get registered if not stated otherwise
	 * @param itemModid  the {@link String} under which name all items will get registered if not stated otherwise
	 * @param entityModid  the {@link String} under which name all entities will get registered if not stated otherwise
	 */
	public RegistryHelper(String blockModid, String itemModid, String entityModid) {
		this.blockModid = blockModid;
		this.itemModid = itemModid;
		this.entityModid = entityModid;
	}
	
	/**
	 * Creates a new instance of the RegistryHelper
	 * @param modid the {@link String} under which name all blocks, items and entities will get registered if not stated otherwise
	 */
	public RegistryHelper(String modid) {
		this.blockModid = modid;
		this.itemModid = modid;
		this.entityModid = modid;
	}
	
	/**
	 * Registers a new ConfigScreen
	 * @param name  the {@link Text} that represents the name of the button that will lead to the registered ConfigScreen
	 * @param screenFactory  the {@link ConfigScreenFactory} that will return the ConfigScreen that is registered
	 */
	public void registerConfigScreenFactory(Text name, ConfigScreenFactory screenFactory) {
		configScreens.add(Pair.of(name, screenFactory));
	}
	
	/**
	 * Sends a packet from the server to the client
	 * @param player  the player to whose client the packet is sent
	 * @param packet  the {@link LuckyTNTPacket} that is being sent
	 */
	public void sendS2CPacket(ServerPlayerEntity player, CustomPayload packet) {
		ServerPlayNetworking.send(player, packet);
	}
	
	/**
	 * Sends a packet from the client to the server
	 * @param packet  the {@link LuckyTNTPacket} that is being sent
	 */
	public void sendC2SPacket(CustomPayload packet) {
		ClientPlayNetworking.send(packet);
	}
	
	/**
	 * Registers a new {@link LTNTBlock}
	 * @param registryName  the registry name of this TNT (for block and for item)
	 * @param TNT  the {@link PrimedLTNT} that is passed to this block and spawned when the block is ignited
	 * @param tab  the string which is passed as a key to {@link RegistryHelper#TNTLists} and {@link RegistryHelper#creativeTabItemLists}
	 * @return {@link Supplier} of a {@link LTNTBlock}
	 */
	public Supplier<LTNTBlock> registerTNTBlock(String registryName, Supplier<EntityType<PrimedLTNT>> TNT, String tab){
		return registerTNTBlock(registryName, TNT, tab, MapColor.RED, true);
	}
	
	/**
	 * Registers a new {@link LTNTBlock}
	 * @param registryName  the registry name of this TNT (for block and for item)
	 * @param TNT  the {@link PrimedLTNT} that is passed to this block and spawned when the block is ignited
	 * @param tab  the string which is passed as a key to {@link RegistryHelper#TNTLists} and {@link RegistryHelper#creativeTabItemLists}
	 * @param randomizedFuseUponExploded  whether or not the TNT should have a random fuse based upon the default fuse when removed by another explosion
	 * @return {@link Supplier} of a {@link LTNTBlock}
	 */
	public Supplier<LTNTBlock> registerTNTBlock(String registryName, Supplier<EntityType<PrimedLTNT>> TNT, String tab, boolean randomizedFuseUponExploded){
		return registerTNTBlock(registryName, TNT, tab, MapColor.RED, randomizedFuseUponExploded);
	}
	
	/**
	 * Registers a new {@link LTNTBlock}
	 * @param registryName  the registry name of this TNT (for block and for item)
	 * @param TNT  the {@link PrimedLTNT} that is passed to this block and spawned when the block is ignited
	 * @param tab  the string which is passed as a key to {@link RegistryHelper#TNTLists} and {@link RegistryHelper#creativeTabItemLists}
	 * @param color  the color the block will have on the map
	 * @param randomizedFuseUponExploded  whether or not the TNT should have a random fuse based upon the default fuse when removed by another explosion
	 * @return {@link Supplier} of a {@link LTNTBlock}
	 */
	public Supplier<LTNTBlock> registerTNTBlock(String registryName, Supplier<EntityType<PrimedLTNT>> TNT, String tab, MapColor color, boolean randomizedFuseUponExploded){
		return registerTNTBlock(TNT, new TNTBlockRegistryData.Builder(registryName).tab(tab).color(color).randomizedFuseUponExploded(randomizedFuseUponExploded).build());
	}
	
	/**
	 * Registers a new {@link LTNTBlock}
	 * @param TNT  the {@link PrimedLTNT} that is passed to this block and spawned when the block is ignited
	 * @param blockData  all the information that a TNT block may need, e.g. registry name and color, contained in an object
	 * @return {@link Supplier} of a {@link LTNTBlock}
	 */
	public Supplier<LTNTBlock> registerTNTBlock(Supplier<EntityType<PrimedLTNT>> TNT, TNTBlockRegistryData blockData){
		return registerTNTBlock(blockModid, itemModid, () -> new LTNTBlock(AbstractBlock.Settings.create().mapColor(blockData.getColor()).sounds(BlockSoundGroup.GRASS), TNT, blockData.randomizedFuseUponExploded()), blockData);
	}
	
	/**
	 * Registers a new {@link LTNTBlock}
	 * @param blockRegistry  the registry in which the block is being registered into
	 * @param itemRegistry  the registry in which the block item is being registered into
	 * @param TNTBlock  the TNT block that is being registered
	 * @param blockData  all the information that a TNT block may need, e.g. registry name and color, contained in an object
	 * @return {@link Supplier} of a {@link LTNTBlock}
	 */
	public Supplier<LTNTBlock> registerTNTBlock(String blockRegistry, @Nullable String itemRegistry, Supplier<LTNTBlock> TNTBlock, TNTBlockRegistryData blockData){
		LTNTBlock rblock = Registry.register(Registries.BLOCK, Identifier.of(blockRegistry, blockData.getRegistryName()), TNTBlock.get());
		Supplier<LTNTBlock> block = () -> rblock;
		((FireBlock)Blocks.FIRE).registerFlammableBlock(rblock, 15, 100);
		
		if(itemRegistry != null && blockData.makeItem()) {
			Item ritem = Registry.register(Registries.ITEM, Identifier.of(itemRegistry, blockData.getRegistryName()), new BlockItem(block.get(), new Item.Settings()) {
				
				@Override
				public void appendTooltip(ItemStack stack, Item.TooltipContext level, List<Text> components, TooltipType flag) {
					super.appendTooltip(stack, level, components, flag);
					if(!blockData.getDescription().getString().equals("")) {
						components.add(blockData.getDescription());
					}
				}
			});
			Supplier<Item> item = () -> ritem;
			
			if(blockData.addToTNTLists()) {
				if(TNTLists.get(blockData.getTab()) == null) {
					TNTLists.put(blockData.getTab(), new ArrayList<Supplier<LTNTBlock>>());
				}
				TNTLists.get(blockData.getTab()).add(block);
			}
			if(blockData.addDispenseBehavior()) {
				DispenserBehaviorHelper.registerTNTBlockDispenserBehavior(block);
			}
			if(!blockData.getTab().equals("none")) {
				if(creativeTabItemLists.get(blockData.getTab()) == null) {
					creativeTabItemLists.put(blockData.getTab(), new ArrayList<Supplier<? extends Item>>());
				}
				creativeTabItemLists.get(blockData.getTab()).add(item);				
			}
		}
		return block;	
	}
	
	/**
	 * Registers a new {@link LivingLTNTBlock}
	 * @param registryName  the registry name of this TNT (for block and for item)
	 * @param TNT  the {@link LivingPrimedLTNT} that is passed to this block and spawned when the block is ignited
	 * @param tab  the string which is passed as a key to {@link RegistryHelper#TNTLists} and {@link RegistryHelper#creativeTabItemLists}
	 * @return {@link Supplier} of a {@link LTNTBlock}
	 */
	public Supplier<LTNTBlock> registerLivingTNTBlock(String registryName, Supplier<EntityType<LivingPrimedLTNT>> TNT, String tab){
		return registerLivingTNTBlock(registryName, TNT, tab, MapColor.RED, true);
	}
	
	/**
	 * Registers a new {@link LivingLTNTBlock}
	 * @param registryName  the registry name of this TNT (for block and for item)
	 * @param TNT  the {@link LivingPrimedLTNT} that is passed to this block and spawned when the block is ignited
	 * @param tab  the string which is passed as a key to {@link RegistryHelper#TNTLists} and {@link RegistryHelper#creativeTabItemLists}
	 * @param randomizedFuseUponExploded  whether or not the TNT should have a random fuse based upon the default fuse when removed by another explosion
	 * @return {@link Supplier} of a {@link LTNTBlock}
	 */
	public Supplier<LTNTBlock> registerLivingTNTBlock(String registryName, Supplier<EntityType<LivingPrimedLTNT>> TNT, String tab, boolean randomizedFuseUponExploded){
		return registerLivingTNTBlock(registryName, TNT, tab, MapColor.RED, randomizedFuseUponExploded);
	}
	
	/**
	 * Registers a new {@link LivingLTNTBlock}
	 * @param registryName  the registry name of this TNT (for block and for item)
	 * @param TNT  the {@link LivingPrimedLTNT} that is passed to this block and spawned when the block is ignited
	 * @param tab  the string which is passed as a key to {@link RegistryHelper#TNTLists} and {@link RegistryHelper#creativeTabItemLists}
	 * @param color  the color the block will have on the map
	 * @param randomizedFuseUponExploded  whether or not the TNT should have a random fuse based upon the default fuse when removed by another explosion
	 * @return {@link Supplier} of a {@link LTNTBlock}
	 */
	public Supplier<LTNTBlock> registerLivingTNTBlock(String registryName, Supplier<EntityType<LivingPrimedLTNT>> TNT, String tab, MapColor color, boolean randomizedFuseUponExploded){
		return registerLivingTNTBlock(TNT, new TNTBlockRegistryData.Builder(registryName).tab(tab).color(color).randomizedFuseUponExploded(randomizedFuseUponExploded).build());
	}
	
	/**
	 * Registers a new {@link LivingLTNTBlock}
	 * @param TNT  the {@link LivingPrimedLTNT} that is passed to this block and spawned when the block is ignited
	 * @param blockData  all the information that a TNT block may need, e.g. registry name and color, contained in an object
	 * @return {@link Supplier} of a {@link LTNTBlock}
	 */
	public Supplier<LTNTBlock> registerLivingTNTBlock(Supplier<EntityType<LivingPrimedLTNT>> TNT, TNTBlockRegistryData blockData){
		return registerLivingTNTBlock(blockModid, itemModid, () -> new LivingLTNTBlock(AbstractBlock.Settings.create().mapColor(blockData.getColor()).sounds(BlockSoundGroup.GRASS), TNT, blockData.randomizedFuseUponExploded()), blockData);
	}
	
	/**
	 * Registers a new {@link LivingLTNTBlock}
	 * @param blockRegistry  the registry in which the block is being registered into
	 * @param itemRegistry  the registry in which the block item is being registered into
	 * @param TNTBlock  the living TNT block that is being registered
	 * @param blockData  all the information that a TNT block may need, e.g. registry name and color, contained in an object
	 * @return {@link Supplier} of a {@link LTNTBlock}
	 */
	public Supplier<LTNTBlock> registerLivingTNTBlock(String blockRegistry, @Nullable String itemRegistry, Supplier<LivingLTNTBlock> TNTBlock, TNTBlockRegistryData blockData){
		LTNTBlock rblock = Registry.register(Registries.BLOCK, Identifier.of(blockRegistry, blockData.getRegistryName()), (LTNTBlock)TNTBlock.get());
		Supplier<LTNTBlock> block = () -> rblock;
		((FireBlock)Blocks.FIRE).registerFlammableBlock(rblock, 15, 100);
		
		if(itemRegistry != null && blockData.makeItem()) {
			Item ritem = Registry.register(Registries.ITEM, Identifier.of(itemRegistry, blockData.getRegistryName()), new BlockItem(block.get(), new Item.Settings()) {
				
				@Override
				public void appendTooltip(ItemStack stack, Item.TooltipContext level, List<Text> components, TooltipType flag) {
					super.appendTooltip(stack, level, components, flag);
					if(!blockData.getDescription().getString().equals("")) {
						components.add(blockData.getDescription());
					}
				}
			});
			Supplier<Item> item = () -> ritem;
			
			if(blockData.addToTNTLists()) {
				if(TNTLists.get(blockData.getTab()) == null) {
					TNTLists.put(blockData.getTab(), new ArrayList<Supplier<LTNTBlock>>());
				}
				TNTLists.get(blockData.getTab()).add(block);
			}
			if(blockData.addDispenseBehavior()) {
				DispenserBehaviorHelper.registerTNTBlockDispenserBehavior(block);
			}
			if(!blockData.getTab().equals("none")) {
				if(creativeTabItemLists.get(blockData.getTab()) == null) {
					creativeTabItemLists.put(blockData.getTab(), new ArrayList<Supplier<? extends Item>>());
				}
				creativeTabItemLists.get(blockData.getTab()).add(item);				
			}
		}
		return block;	
	}
	
	/**
	 * Registers a new {@link LDynamiteItem}
	 * @param registryName  the registry name of this dynamite item
	 * @param dynamiteSupplier  the dynamite item which is being registered
	 * @param tab  the string which is passed as a key to {@link RegistryHelper#dynamiteLists} and {@link RegistryHelper#creativeTabItemLists}
	 * @return {@link Supplier} of a {@link LDynamiteItem}
	 */
	public Supplier<LDynamiteItem> registerDynamiteItem(String registryName, RegistryEntry<Supplier<LDynamiteItem>> dynamiteSupplier, String tab){
		return registerDynamiteItem(registryName, dynamiteSupplier.value(), tab, true, true);
	}
	
	/**
	 * Registers a new {@link LDynamiteItem}
	 * @param registryName  the registry name of this dynamite item
	 * @param dynamiteSupplier  the dynamite item which is being registered
	 * @param tab  the string which is passed as a key to {@link RegistryHelper#dynamiteLists} and {@link RegistryHelper#creativeTabItemLists}
	 * @param addToLists  whether or not this dynamite should be added to {@link RegistryHelper#dynamiteLists} or not
	 * @param addDispenseBehavior  whether or not a {@link DispenseItemBehavior} should be registered or not
	 * @return {@link Supplier} of a {@link LDynamiteItem}
	 */
	public Supplier<LDynamiteItem> registerDynamiteItem(String registryName, Supplier<LDynamiteItem> dynamiteSupplier, String tab, boolean addToLists, boolean addDispenseBehavior){
		return registerDynamiteItem(itemModid, registryName, dynamiteSupplier, tab, addToLists, addDispenseBehavior);
	}
	
	/**
	 * Registers a new {@link LDynamiteItem}
	 * @param registryName  the registry name of this dynamite item
	 * @param dynamite  the {@link LExplosiveProjectile} that is passed to this item and thrown upon right clicking the item
	 * @param tab  the string which is passed as a key to {@link RegistryHelper#dynamiteLists} and {@link RegistryHelper#creativeTabItemLists}
	 * @return {@link Supplier} of a {@link LDynamiteItem}
	 */
	public Supplier<LDynamiteItem> registerDynamiteItem(String registryName, Supplier<EntityType<LExplosiveProjectile>> dynamite, String tab){
		return registerDynamiteItem(registryName, RegistryEntry.of(() -> new LDynamiteItem(new Item.Settings(), dynamite)), tab);
	}
	
	/**
	 * Registers a new {@link LDynamiteItem}
	 * @param itemRegistry  the registry in which this dynamite is being registered into
	 * @param registryName  the registry name of this dynamite item
	 * @param dynamiteSupplier  the dynamite item which is being registered
	 * @param tab  the string which is passed as a key to {@link RegistryHelper#dynamiteLists} and {@link RegistryHelper#creativeTabItemLists}
	 * @param addToLists  whether or not this dynamite should be added to {@link RegistryHelper#dynamiteLists} or not
	 * @param addDispenseBehavior  whether or not a {@link DispenseItemBehavior} should be registered or not
	 * @return {@link Supplier} of a {@link LDynamiteItem}
	 */
	public Supplier<LDynamiteItem> registerDynamiteItem(String itemRegistry, String registryName, Supplier<LDynamiteItem> dynamiteSupplier, String tab, boolean addToLists, boolean addDispenseBehavior){
		LDynamiteItem ritem = Registry.register(Registries.ITEM, Identifier.of(itemRegistry, registryName), dynamiteSupplier.get());		
		Supplier<LDynamiteItem> item = () -> ritem;
		if(addToLists) {
			if(dynamiteLists.get(tab) == null) {
				dynamiteLists.put(tab, new ArrayList<Supplier<LDynamiteItem>>());
			}
			dynamiteLists.get(tab).add(item);
		}
		if(addDispenseBehavior) {
			DispenserBehaviorHelper.registerDynamiteDispenserBehavior(item);
		}
		if(!tab.equals("none")) {
			if(creativeTabItemLists.get(tab) == null) {
				creativeTabItemLists.put(tab, new ArrayList<Supplier<? extends Item>>());
			}
			creativeTabItemLists.get(tab).add(item);
		}
		return item;
	}
	
	/**
	 * Registers a new {@link LTNTMinecart}
	 * @param registryName  the registry name of this minecart item
	 * @param TNT  the {@link LTNTMinecart} that is passed to this item and thrown
	 * @param tab  the string which is passed as a key to {@link RegistryHelper#minecartLists} and {@link RegistryHelper#creativeTabItemLists}
	 * @return {@link Supplier} of a {@link LTNTMinecartItem}
	 */
	public Supplier<LTNTMinecartItem> registerTNTMinecartItem(String registryName, Supplier<Supplier<EntityType<LTNTMinecart>>> TNT, String tab){
		return registerTNTMinecartItem(registryName, () -> new LTNTMinecartItem(new Item.Settings().maxCount(1), TNT), tab, true, true);
	}
	
	/**
	 * Registers a new {@link LTNTMinecart}
	 * @param registryName  the registry name of this minecart item
	 * @param TNT  the {@link LTNTMinecart} that is passed to this item and thrown
	 * @param tab  the string which is passed as a key to {@link RegistryHelper#minecartLists} and {@link RegistryHelper#creativeTabItemLists}
	 * @param addToLists  whether or not this minecart should be added to {@link RegistryHelper#dynamiteLists} or not
	 * @param addDispenseBehavior  whether or not a {@link DispenseItemBehavior} should be registered or not
	 * @return {@link Supplier} of a {@link LTNTMinecartItem}
	 */
	public Supplier<LTNTMinecartItem> registerTNTMinecartItem(String registryName, Supplier<LTNTMinecartItem> minecartSupplier, String tab, boolean addToLists, boolean addDispenseBehavior){
		return registerTNTMinecartItem(itemModid, registryName, minecartSupplier, tab, addToLists, addDispenseBehavior);
	}
	
	/**
	 * Registers a new {@link LTNTMinecart}
	 * @param itemRegistry  the registry in which this minecart is being registered into
	 * @param registryName  the registry name of this minecart item
	 * @param TNT  the {@link LTNTMinecart} that is passed to this item and thrown
	 * @param tab  the string which is passed as a key to {@link RegistryHelper#minecartLists} and {@link RegistryHelper#creativeTabItemLists}
	 * @param addToLists  whether or not this minecart should be added to {@link RegistryHelper#dynamiteLists} or not
	 * @param addDispenseBehavior  whether or not a {@link DispenseItemBehavior} should be registered or not
	 * @return {@link Supplier} of a {@link LTNTMinecartItem}
	 */
	public Supplier<LTNTMinecartItem> registerTNTMinecartItem(String itemRegistry, String registryName, Supplier<LTNTMinecartItem> minecartSupplier, String tab, boolean addToLists, boolean addDispenseBehavior){
		LTNTMinecartItem ritem = Registry.register(Registries.ITEM, Identifier.of(itemRegistry, registryName), minecartSupplier.get());
		Supplier<LTNTMinecartItem> item = () -> ritem;
		if(addToLists) {
			if(minecartLists.get(tab) == null) {
				minecartLists.put(tab, new ArrayList<Supplier<LTNTMinecartItem>>());
			}
			minecartLists.get(tab).add(item);
		}
		if(addDispenseBehavior) {
			DispenserBehaviorHelper.registerMinecartDispenserBehavior(item);
		}
		if(!tab.equals("none")) {
			if(creativeTabItemLists.get(tab) == null) {
				creativeTabItemLists.put(tab, new ArrayList<Supplier<? extends Item>>());
			}
			creativeTabItemLists.get(tab).add(item);
		}
		return item;
	}
	
	/**
	 * Registers a new {@link PrimedLTNT}
	 * @param registryName  the registry name of this primed TNT
	 * @param effect  the TNT effect this primed TNT will have
	 * @return {@link Supplier} of an {@link EntityType} of a {@link PrimedLTNT}
	 */
	public Supplier<EntityType<PrimedLTNT>> registerTNTEntity(String registryName, PrimedTNTEffect effect){
		return registerTNTEntity(registryName, effect, 1f, true);
	}
	
	/**
	 * Registers a new {@link PrimedLTNT}
	 * @param registryName  the registry name of this primed TNT
	 * @param effect  the TNT effect this primed TNT will have
	 * @param size  the size of the hitbox of this primed TNT
	 * @param fireImmune whether or not this primed TNT can burn (visual only)
	 * @return {@link Supplier} of an {@link EntityType} of a {@link PrimedLTNT}
	 */
	public Supplier<EntityType<PrimedLTNT>> registerTNTEntity(String registryName, PrimedTNTEffect effect, float size, boolean fireImmune){
		return registerTNTEntity(entityModid, registryName, effect, size, fireImmune);
	}
	
	/**
	 * Registers a new {@link PrimedLTNT}
	 * @param entityRegistry  the registry in which this primed TNT is being registered into
	 * @param registryName  the registry name of this primed TNT
	 * @param effect  the TNT effect this primed TNT will have
	 * @param size  the size of the hitbox of this primed TNT
	 * @param fireImmune whether or not this primed TNT can burn (visual only)
	 * @return {@link Supplier} of an {@link EntityType} of a {@link PrimedLTNT}
	 */
	public Supplier<EntityType<PrimedLTNT>> registerTNTEntity(String entityRegistry, String registryName, PrimedTNTEffect effect, float size, boolean fireImmune){
		if(fireImmune) {
			EntityType<PrimedLTNT> rtype = Registry.register(Registries.ENTITY_TYPE, Identifier.of(entityRegistry, registryName), EntityType.Builder.<PrimedLTNT>create((EntityType<PrimedLTNT> type, World level) -> new PrimedLTNT(type, level, effect), SpawnGroup.MISC)/*.setShouldReceiveVelocityUpdates(true)*/.maxTrackingRange(64).makeFireImmune().dimensions(size, size).build(registryName));
			return () -> rtype;
		}
		else {
			EntityType<PrimedLTNT> rtype = Registry.register(Registries.ENTITY_TYPE, Identifier.of(entityRegistry, registryName), EntityType.Builder.<PrimedLTNT>create((EntityType<PrimedLTNT> type, World level) -> new PrimedLTNT(type, level, effect), SpawnGroup.MISC)/*.setShouldReceiveVelocityUpdates(true)*/.maxTrackingRange(64).dimensions(size, size).build(registryName));
			return () -> rtype;
		}
	}
		
	/**
	 * Registers a new {@link PrimedLTNT}
	 * @param entityRegistry  the registry in which this primed TNT is being registered into
	 * @param registryName  the registry name of this primed TNT
	 * @param TNT  the primed TNT that is being registered
	 * @return {@link Supplier} of an {@link EntityType} of a {@link PrimedLTNT}
	 */
	public Supplier<EntityType<PrimedLTNT>> registerTNTEntity(String entityRegistry, String registryName, Supplier<EntityType<PrimedLTNT>> TNT){
		EntityType<PrimedLTNT> rtype = Registry.register(Registries.ENTITY_TYPE, Identifier.of(entityRegistry, registryName), TNT.get());
		return () -> rtype;
	}
	
	/**
	 * Registers a new {@link LTNTMinecart}
	 * @param registryName  the registry name of this minecart
	 * @param TNT  the {@link PrimedLTNT} that passes the TNT effect over to this minecart
	 * @param pickItem  the minecart item that is gotten when this minecart is middle-clicked
	 * @return {@link Supplier} of an {@link EntityType} of a {@link LTNTMinecart}
	 */
	public Supplier<EntityType<LTNTMinecart>> registerTNTMinecart(String registryName, Supplier<EntityType<PrimedLTNT>> TNT, Supplier<Supplier<LTNTMinecartItem>> pickItem){
		return registerTNTMinecart(registryName, TNT, pickItem, true);
	}
	
	/**
	 * Registers a new {@link LTNTMinecart}
	 * @param registryName  the registry name of this minecart
	 * @param TNT  the {@link PrimedLTNT} that passes the TNT effect over to this minecart
	 * @param pickItem  the minecart item that is gotten when this minecart is middle-clicked
	 * @param explodesInstantly  whether or not this minecart will fuse or explode immediately
	 * @return {@link Supplier} of an {@link EntityType} of a {@link LTNTMinecart}
	 */
	public Supplier<EntityType<LTNTMinecart>> registerTNTMinecart(String registryName, Supplier<EntityType<PrimedLTNT>> TNT, Supplier<Supplier<LTNTMinecartItem>> pickItem, boolean explodesInstantly){
		return registerTNTMinecart(entityModid, registryName, TNT, pickItem, explodesInstantly);
	}
	
	/**
	 * Registers a new {@link LTNTMinecart}
	 * @param entityRegistry  the registry in which this minecart is being registered into
	 * @param registryName  the registry name of this minecart
	 * @param TNT  the {@link PrimedLTNT} that passes the TNT effect over to this minecart
	 * @param pickItem  the minecart item that is gotten when this minecart is middle-clicked
	 * @param explodesInstantly  whether or not this minecart will fuse or explode immediately
	 * @return {@link Supplier} of an {@link EntityType} of a {@link LTNTMinecart}
	 */
	public Supplier<EntityType<LTNTMinecart>> registerTNTMinecart(String entityRegistry, String registryName, Supplier<EntityType<PrimedLTNT>> TNT, Supplier<Supplier<LTNTMinecartItem>> pickItem, boolean explodesInstantly){		
		EntityType<LTNTMinecart> rtype = Registry.register(Registries.ENTITY_TYPE, Identifier.of(entityRegistry, registryName), EntityType.Builder.<LTNTMinecart>create((EntityType<LTNTMinecart> type, World level) -> new LTNTMinecart(type, level, TNT, pickItem, explodesInstantly), SpawnGroup.MISC)/*.setShouldReceiveVelocityUpdates(true)*/.maxTrackingRange(64).dimensions(0.98f, 0.7f).build(registryName));
		return () -> rtype;
	}
	
	/**
	 * Registers a new {@link LTNTMinecart}
	 * @param entityRegistry  the registry in which this minecart is being registered into
	 * @param registryName  the registry name of this minecart
	 * @param minecart  the minecart that is being registered
	 * @return {@link Supplier} of an {@link EntityType} of a {@link LTNTMinecart}
	 */
	public Supplier<EntityType<LTNTMinecart>> registerTNTMinecart(String entityRegistry, String registryName, Supplier<EntityType<LTNTMinecart>> minecart){		
		EntityType<LTNTMinecart> rtype = Registry.register(Registries.ENTITY_TYPE, Identifier.of(entityRegistry, registryName), minecart.get());
		return () -> rtype;
	}
	
	/**
	 * Registers a new {@link LivingPrimedLTNT}
	 * @param registryName  the registry name of this living primed TNT
	 * @param effect  the TNT effect this living primed TNT will have
	 * @param size  the size of the hitbox of this living primed TNT
	 * @param fireImmune  whether or not this living primed TNT can burn (visual only)
	 * @return {@link Supplier} of an {@link EntityType} of a {@link LivingPrimedLTNT}
	 */
	public Supplier<EntityType<LivingPrimedLTNT>> registerLivingTNTEntity(String registryName, Supplier<EntityType<LivingPrimedLTNT>> TNT){
		return registerLivingTNTEntity(entityModid, registryName, TNT);
	}
	
	/**
	 * Registers a new {@link LivingPrimedLTNT}
	 * @param entityRegistry  the registry in which this living primed TNT is being registered into
	 * @param registryName  the registry name of this living primed TNT
	 * @param TNT  the TNT that is being registered
	 * @return {@link Supplier} of an {@link EntityType} of a {@link LivingPrimedLTNT}
	 */
	public Supplier<EntityType<LivingPrimedLTNT>> registerLivingTNTEntity(String entityRegistry, String registryName, Supplier<EntityType<LivingPrimedLTNT>> TNT){
		EntityType<LivingPrimedLTNT> rtype = Registry.register(Registries.ENTITY_TYPE, Identifier.of(entityRegistry, registryName), TNT.get());
		return () -> rtype;
	}
	
	/**
	 * Registers a new {@link LExplosiveProjectile}
	 * @param registryName  the registry name of this explosive projectile
	 * @param effect  the TNT effect this explosive projectile will have
	 * @return {@link Supplier} of an {@link EntityType} of a {@link LExplosiveProjectile}
	 */
	public Supplier<EntityType<LExplosiveProjectile>> registerExplosiveProjectile(String registryName, PrimedTNTEffect effect){
		return registerExplosiveProjectile(registryName, effect, 1f, false);
	}
	
	/**
	 * Registers a new {@link LExplosiveProjectile}
	 * @param registryName  the registry name of this explosive projectile
	 * @param effect  the TNT effect this explosive projectile will have
	 * @param size  the size of the hitbox of this explosive projectile
	 * @param fireImmune  whether or not this explosive projectile can burn (visual only)
	 * @return {@link Supplier} of an {@link EntityType} of a {@link LExplosiveProjectile}
	 */
	public Supplier<EntityType<LExplosiveProjectile>> registerExplosiveProjectile(String registryName, PrimedTNTEffect effect, float size, boolean fireImmune) {
		return registerExplosiveProjectile(entityModid, registryName, effect, size, fireImmune);
	}
	
	/**
	 * Registers a new {@link LExplosiveProjectile}
	 * @param entityRegistry  the registry in which this explosive projectile is being registered into
	 * @param registryName  the registry name of this explosive projectile
	 * @param effect  the TNT effect this explosive projectile will have
	 * @param size  the size of the hitbox of this explosive projectile
	 * @param fireImmune  whether or not this explosive projectile can burn (visual only)
	 * @return {@link Supplier} of an {@link EntityType} of a {@link LExplosiveProjectile}
	 */
	public Supplier<EntityType<LExplosiveProjectile>> registerExplosiveProjectile(String entityRegistry, String registryName, PrimedTNTEffect effect, float size, boolean fireImmune){
		if(fireImmune) {
			EntityType<LExplosiveProjectile> rtype = Registry.register(Registries.ENTITY_TYPE, Identifier.of(entityRegistry, registryName), EntityType.Builder.<LExplosiveProjectile>create((EntityType<LExplosiveProjectile> type, World level) -> new LExplosiveProjectile(type, level, effect), SpawnGroup.MISC)/*.setShouldReceiveVelocityUpdates(true)*/.maxTrackingRange(64).makeFireImmune().dimensions(size, size).build(registryName));
			return () -> rtype;
		}
		else {
			EntityType<LExplosiveProjectile> rtype = Registry.register(Registries.ENTITY_TYPE, Identifier.of(entityRegistry, registryName), EntityType.Builder.<LExplosiveProjectile>create((EntityType<LExplosiveProjectile> type, World level) -> new LExplosiveProjectile(type, level, effect), SpawnGroup.MISC)/*.setShouldReceiveVelocityUpdates(true)*/.maxTrackingRange(64).dimensions(size, size).build(registryName));
			return () -> rtype;
		}
	}
	
	/**
	 * Registers a new {@link LExplosiveProjectile}
	 * @param entityRegistry  the registry in which this explosive projectile is being registered into
	 * @param registryName  the registry name of this explosive projectile
	 * @param projectile  the explosive projectile that is being registered
	 * @return {@link Supplier} of an {@link EntityType} of a {@link LExplosiveProjectile}
	 */
	public Supplier<EntityType<LExplosiveProjectile>> registerExplosiveProjectile(String entityRegistry, String registryName, Supplier<EntityType<LExplosiveProjectile>> projectile){
		EntityType<LExplosiveProjectile> rtype = Registry.register(Registries.ENTITY_TYPE, Identifier.of(entityRegistry, registryName), projectile.get());
		return () -> rtype;
	}
}

package luckytnt.item;

import java.util.function.Consumer;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUseAnimation;
import net.minecraft.world.item.TooltipDisplay;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

public class VacuumCleaner extends Item {

	public int soundCooldown = 0;

	public VacuumCleaner() {
		super(new Item.Properties().stacksTo(1).durability(1000));
	}

	@Override
	public ItemUseAnimation getUseAnimation(ItemStack stack) {
		return ItemUseAnimation.NONE;
	}

	@Override
	public void appendHoverText(ItemStack stack, Item.TooltipContext level, TooltipDisplay display, Consumer<Component> components, TooltipFlag flag) {
		super.appendHoverText(stack, level, display, components, flag);
		components.accept(Component.translatable("item.vacuum_cleaner.info"));
	}

	@Override
	public InteractionResult use(Level level, Player player, InteractionHand hand) {
		// TODO(port-26.2): DISABLED — needs manual port. Original toggled a "using" flag stored in
		// mutable CustomData NBT (removed in 26.2), played sounds, and spawned VACUUM_SHOT projectiles
		// via renamed entity/item APIs. Stubbed to a no-op so the item still exists and is craftable.
		return InteractionResult.PASS;
	}

	@Override
	public void inventoryTick(ItemStack stack, ServerLevel level, Entity entity, EquipmentSlot slot) {
		// TODO(port-26.2): DISABLED — see use(); vacuum tick logic depended on mutable CustomData and
		// removed projectile/velocity/damage accessors.
	}
}

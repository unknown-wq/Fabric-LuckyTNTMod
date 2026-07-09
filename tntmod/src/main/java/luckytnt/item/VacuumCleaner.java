package luckytnt.item;

import java.util.List;

import luckytnt.registry.EntityRegistry;
import luckytnt.registry.SoundRegistry;
import luckytntlib.entity.LExplosiveProjectile;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.arrow.AbstractArrow.AbstractArrow.Pickup;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.sounds.SoundSource;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.ItemUseAnimation;
import net.minecraft.world.level.Level;

public class VacuumCleaner extends Item {

	public int soundCooldown = 0;
	
	public VacuumCleaner() {
		super(new Item.Properties().stacksTo(1).maxDamage(1000));
	}
	
	@Override
	public ItemUseAnimation getUseAction(ItemStack stack) {
		return ItemUseAnimation.NONE;
	}
	
	@Override
	public void appendTooltip(ItemStack stack, Item.TooltipContext level, List<Component> components, TooltipFlag flag) {
		super.appendTooltip(stack, level, components, flag);
		components.add(Component.translatable("item.vacuum_cleaner.info"));
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public InteractionResult<ItemStack> use(Level level, Player player, InteractionHand hand) {
		if(!player.getItemInHand(hand).contains(DataComponents.CUSTOM_DATA)) {
			player.getItemInHand(hand).set(DataComponents.CUSTOM_DATA, CustomData.DEFAULT);
		}
		
		usageTick(level, player, player.getItemInHand(hand) , player.getItemInHand(hand).getCount());
		if(!player.getItemInHand(hand).get(DataComponents.CUSTOM_DATA).getNbt().getBoolean("using")) {
			soundCooldown = 42;
			player.getItemInHand(hand).get(DataComponents.CUSTOM_DATA).getNbt().putBoolean("using", true);
		}
		else if(player.getItemInHand(hand).get(DataComponents.CUSTOM_DATA).getNbt().getBoolean("using")) {
			player.getItemInHand(hand).get(DataComponents.CUSTOM_DATA).getNbt().putBoolean("using", false);
		}
		if(player.getItemInHand(hand).get(DataComponents.CUSTOM_DATA).getNbt().getBoolean("using"))
			level.playSoundFromEntity(null, player, SoundRegistry.VACUUM_CLEANER_START.get(), SoundSource.MASTER, 2, 1);
		return new InteractionResult<ItemStack>(InteractionResult.SUCCESS, player.getItemInHand(hand));
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public void inventoryTick(ItemStack stack, Level level, Entity entity, int count, boolean inHand) {		
		if(!stack.contains(DataComponents.CUSTOM_DATA)) {
			stack.set(DataComponents.CUSTOM_DATA, CustomData.DEFAULT);
		}
		
		if(stack.get(DataComponents.CUSTOM_DATA).getNbt().getBoolean("using") && inHand) {
			if(!level.isClientSide())
				soundCooldown--;
			if(soundCooldown == 0) {
				level.playSoundFromEntity(null, entity, SoundRegistry.VACUUM_CLEANER.get(), SoundSource.MASTER, 2, 1);
				if(!level.isClientSide())
					soundCooldown = 22;
			}
			if(entity instanceof Player player) {
				if(!player.isCreative()) {
					stack.setDamage(stack.getDamage() + 1);
					if(stack.getDamage() > 1960)
						stack.shrink(1);
				}
				LExplosiveProjectile shot = EntityRegistry.VACUUM_SHOT.get().create(level);
				shot.setPos(player.getPosition(1f).add(0, player.getStandingEyeHeight(), 0));
				shot.setDeltaMovement(player.getRotationVec(1).x, player.getRotationVec(1).y, player.getRotationVec(1).z, 4, 0);
				shot.pickup = AbstractArrow.Pickup.DISALLOWED;
				level.addFreshEntity(shot);
			}
		} else {
			stack.get(DataComponents.CUSTOM_DATA).getNbt().putBoolean("using", false);
		}
	}
}

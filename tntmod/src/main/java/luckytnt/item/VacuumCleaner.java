package luckytnt.item;

import java.util.List;

import luckytnt.registry.EntityRegistry;
import luckytnt.registry.SoundRegistry;
import luckytntlib.entity.LExplosiveProjectile;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity.PickupPermission;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.sound.SoundCategory;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.UseAction;
import net.minecraft.world.World;

public class VacuumCleaner extends Item {

	public int soundCooldown = 0;
	
	public VacuumCleaner() {
		super(new Item.Settings().maxCount(1).maxDamage(1000));
	}
	
	@Override
	public UseAction getUseAction(ItemStack stack) {
		return UseAction.NONE;
	}
	
	@Override
	public void appendTooltip(ItemStack stack, Item.TooltipContext level, List<Text> components, TooltipType flag) {
		super.appendTooltip(stack, level, components, flag);
		components.add(Text.translatable("item.vacuum_cleaner.info"));
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public TypedActionResult<ItemStack> use(World level, PlayerEntity player, Hand hand) {
		if(!player.getStackInHand(hand).contains(DataComponentTypes.CUSTOM_DATA)) {
			player.getStackInHand(hand).set(DataComponentTypes.CUSTOM_DATA, NbtComponent.DEFAULT);
		}
		
		usageTick(level, player, player.getStackInHand(hand) , player.getStackInHand(hand).getCount());
		if(!player.getStackInHand(hand).get(DataComponentTypes.CUSTOM_DATA).getNbt().getBoolean("using")) {
			soundCooldown = 42;
			player.getStackInHand(hand).get(DataComponentTypes.CUSTOM_DATA).getNbt().putBoolean("using", true);
		}
		else if(player.getStackInHand(hand).get(DataComponentTypes.CUSTOM_DATA).getNbt().getBoolean("using")) {
			player.getStackInHand(hand).get(DataComponentTypes.CUSTOM_DATA).getNbt().putBoolean("using", false);
		}
		if(player.getStackInHand(hand).get(DataComponentTypes.CUSTOM_DATA).getNbt().getBoolean("using"))
			level.playSoundFromEntity(null, player, SoundRegistry.VACUUM_CLEANER_START.get(), SoundCategory.MASTER, 2, 1);
		return new TypedActionResult<ItemStack>(ActionResult.SUCCESS, player.getStackInHand(hand));
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public void inventoryTick(ItemStack stack, World level, Entity entity, int count, boolean inHand) {		
		if(!stack.contains(DataComponentTypes.CUSTOM_DATA)) {
			stack.set(DataComponentTypes.CUSTOM_DATA, NbtComponent.DEFAULT);
		}
		
		if(stack.get(DataComponentTypes.CUSTOM_DATA).getNbt().getBoolean("using") && inHand) {
			if(!level.isClient)
				soundCooldown--;
			if(soundCooldown == 0) {
				level.playSoundFromEntity(null, entity, SoundRegistry.VACUUM_CLEANER.get(), SoundCategory.MASTER, 2, 1);
				if(!level.isClient)
					soundCooldown = 22;
			}
			if(entity instanceof PlayerEntity player) {
				if(!player.isCreative()) {
					stack.setDamage(stack.getDamage() + 1);
					if(stack.getDamage() > 1960)
						stack.decrement(1);
				}
				LExplosiveProjectile shot = EntityRegistry.VACUUM_SHOT.get().create(level);
				shot.setPosition(player.getLerpedPos(1f).add(0, player.getStandingEyeHeight(), 0));
				shot.setVelocity(player.getRotationVec(1).x, player.getRotationVec(1).y, player.getRotationVec(1).z, 4, 0);
				shot.pickupType = PickupPermission.DISALLOWED;
				level.spawnEntity(shot);
			}
		} else {
			stack.get(DataComponentTypes.CUSTOM_DATA).getNbt().putBoolean("using", false);
		}
	}
}

package luckytnt.effects;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.HoeItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.PickaxeItem;
import net.minecraft.world.item.ShovelItem;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.ToolItem;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.level.Level;

public class MidasTouchEffect extends MobEffect {

	public MidasTouchEffect(MobEffectCategory category, int id) {
		super(category, id);		
	}
	
	@Override
	public Component getName() {
		return Component.translatable("effect.midas_touch");
	}
	
	@Override
	public boolean isBeneficial() {
		return false;
	}
	
	@Override
	public boolean isInstant() {
		return false;
	}
	
	@Override
	public boolean canApplyUpdateEffect(int duration, int amplifier) {
		return true;
	}
	
	@Override
	public boolean applyUpdateEffect(LivingEntity entity, int amplifier) {
		Level level = entity.level();
		if(!level.isClientSide) {

			BlockHitResult result = level.raycast(new RaycastContext(entity.getPosition(1), entity.getPosition(1).add(0, -1, 0), RaycastContext.ShapeType.COLLIDER, RaycastContext.FluidHandling.NONE, entity));
			if(result != null) {
				BlockState state = level.getBlockState(result.getBlockPos());
				if(state.getBlock().getExplosionResistance() < 100 && !state.isAir()) {
					level.setBlock(result.getBlockPos(), Blocks.GOLD_BLOCK.defaultBlockState(), 3);
				}
			}
			
			result = level.raycast(new RaycastContext(entity.getPosition(1).add(0, entity.getStandingEyeHeight(), 0), entity.getPosition(1).add(0, entity.getStandingEyeHeight(), 0).add(entity.getRotationVec(1).multiply(5)), RaycastContext.ShapeType.COLLIDER, RaycastContext.FluidHandling.NONE, entity));
			if(result != null) {
				BlockState state = level.getBlockState(result.getBlockPos());
				if(state.getBlock().getExplosionResistance() < 100 && !state.isAir()) {
					level.setBlock(result.getBlockPos(), Blocks.GOLD_BLOCK.defaultBlockState(), 3);
				}
			}
			if(entity.getMainHandStack() != ItemStack.EMPTY) {
				Item item = entity.getMainHandStack().getItem();
				if(item instanceof SwordItem && item != Items.GOLDEN_SWORD) {
					entity.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(Items.GOLDEN_SWORD));
				}
				else if(item instanceof ShovelItem && item != Items.GOLDEN_SHOVEL) {
					entity.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(Items.GOLDEN_SHOVEL));
				}
				else if(item instanceof PickaxeItem && item != Items.GOLDEN_PICKAXE) {
					entity.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(Items.GOLDEN_PICKAXE));
				}
				else if(item instanceof AxeItem && item != Items.GOLDEN_AXE) {
					entity.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(Items.GOLDEN_AXE));
				}
				else if(item instanceof HoeItem && item != Items.GOLDEN_HOE) {
					entity.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(Items.GOLDEN_HOE));
				}
				else if(item == Items.APPLE) {
					entity.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(Items.GOLDEN_APPLE, entity.getMainHandStack().getCount()));					
				}
				else if(item == Items.CARROT) {
					entity.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(Items.GOLDEN_CARROT, entity.getMainHandStack().getCount()));		
				}
				else if(item == Items.MELON_SLICE) {
					entity.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(Items.GLISTERING_MELON_SLICE, entity.getMainHandStack().getCount()));					
				}
				else if(item instanceof BlockItem && item != Items.GOLD_BLOCK){
					entity.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(Items.GOLD_BLOCK, entity.getMainHandStack().getCount()));										
				}
				else if(!(item instanceof BlockItem) && !(item instanceof ToolItem) && item != Items.GOLDEN_APPLE && item != Items.GOLDEN_CARROT && item != Items.GLISTERING_MELON_SLICE){
					entity.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(Items.GOLD_INGOT, entity.getMainHandStack().getCount()));										
				}
			}
			if(entity.getOffHandStack() != ItemStack.EMPTY) {
				Item item = entity.getOffHandStack().getItem();
				if(item instanceof SwordItem && item != Items.GOLDEN_SWORD) {
					entity.setItemInHand(InteractionHand.OFF_HAND, new ItemStack(Items.GOLDEN_SWORD));
				}
				else if(item instanceof ShovelItem && item != Items.GOLDEN_SHOVEL) {
					entity.setItemInHand(InteractionHand.OFF_HAND, new ItemStack(Items.GOLDEN_SHOVEL));
				}
				else if(item instanceof PickaxeItem && item != Items.GOLDEN_PICKAXE) {
					entity.setItemInHand(InteractionHand.OFF_HAND, new ItemStack(Items.GOLDEN_PICKAXE));
				}
				else if(item instanceof AxeItem && item != Items.GOLDEN_AXE) {
					entity.setItemInHand(InteractionHand.OFF_HAND, new ItemStack(Items.GOLDEN_AXE));
				}
				else if(item instanceof HoeItem && item != Items.GOLDEN_HOE) {
					entity.setItemInHand(InteractionHand.OFF_HAND, new ItemStack(Items.GOLDEN_HOE));
				}
				else if(item == Items.APPLE) {
					entity.setItemInHand(InteractionHand.OFF_HAND, new ItemStack(Items.GOLDEN_APPLE, entity.getOffHandStack().getCount()));					
				}
				else if(item == Items.CARROT) {
					entity.setItemInHand(InteractionHand.OFF_HAND, new ItemStack(Items.GOLDEN_CARROT, entity.getOffHandStack().getCount()));		
				}
				else if(item == Items.MELON_SLICE) {
					entity.setItemInHand(InteractionHand.OFF_HAND, new ItemStack(Items.GLISTERING_MELON_SLICE, entity.getOffHandStack().getCount()));					
				}
				else if(item instanceof BlockItem && item != Items.GOLD_BLOCK){
					entity.setItemInHand(InteractionHand.OFF_HAND, new ItemStack(Items.GOLD_BLOCK, entity.getOffHandStack().getCount()));										
				}
				else if(!(item instanceof BlockItem) && !(item instanceof ToolItem) && item != Items.GOLDEN_APPLE && item != Items.GOLDEN_CARROT && item != Items.GLISTERING_MELON_SLICE){
					entity.setItemInHand(InteractionHand.OFF_HAND, new ItemStack(Items.GOLD_INGOT, entity.getOffHandStack().getCount()));										
				}
			}
			if(entity.getEquippedStack(EquipmentSlot.HEAD) != ItemStack.EMPTY && entity.getEquippedStack(EquipmentSlot.HEAD).getItem() != Items.GOLDEN_HELMET) {
				entity.equipStack(EquipmentSlot.HEAD, new ItemStack(Items.GOLDEN_HELMET));
			}
			if(entity.getEquippedStack(EquipmentSlot.CHEST) != ItemStack.EMPTY && entity.getEquippedStack(EquipmentSlot.HEAD).getItem() != Items.GOLDEN_CHESTPLATE) {
				entity.equipStack(EquipmentSlot.CHEST, new ItemStack(Items.GOLDEN_CHESTPLATE));
			}
			if(entity.getEquippedStack(EquipmentSlot.LEGS) != ItemStack.EMPTY && entity.getEquippedStack(EquipmentSlot.HEAD).getItem() != Items.GOLDEN_LEGGINGS) {
				entity.equipStack(EquipmentSlot.LEGS, new ItemStack(Items.GOLDEN_LEGGINGS));
			}
			if(entity.getEquippedStack(EquipmentSlot.FEET) != ItemStack.EMPTY && entity.getEquippedStack(EquipmentSlot.HEAD).getItem() != Items.GOLDEN_BOOTS) {
				entity.equipStack(EquipmentSlot.FEET, new ItemStack(Items.GOLDEN_BOOTS));
			}
		}
		return true;
	}
}

package luckytnt.effects;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.item.AxeItem;
import net.minecraft.item.BlockItem;
import net.minecraft.item.HoeItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.PickaxeItem;
import net.minecraft.item.ShovelItem;
import net.minecraft.item.SwordItem;
import net.minecraft.item.ToolItem;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;

public class MidasTouchEffect extends StatusEffect {

	public MidasTouchEffect(StatusEffectCategory category, int id) {
		super(category, id);		
	}
	
	@Override
	public Text getName() {
		return Text.translatable("effect.midas_touch");
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
		World level = entity.getWorld();
		if(!level.isClient) {

			BlockHitResult result = level.raycast(new RaycastContext(entity.getLerpedPos(1), entity.getLerpedPos(1).add(0, -1, 0), RaycastContext.ShapeType.COLLIDER, RaycastContext.FluidHandling.NONE, entity));
			if(result != null) {
				BlockState state = level.getBlockState(result.getBlockPos());
				if(state.getBlock().getBlastResistance() < 100 && !state.isAir()) {
					level.setBlockState(result.getBlockPos(), Blocks.GOLD_BLOCK.getDefaultState(), 3);
				}
			}
			
			result = level.raycast(new RaycastContext(entity.getLerpedPos(1).add(0, entity.getStandingEyeHeight(), 0), entity.getLerpedPos(1).add(0, entity.getStandingEyeHeight(), 0).add(entity.getRotationVec(1).multiply(5)), RaycastContext.ShapeType.COLLIDER, RaycastContext.FluidHandling.NONE, entity));
			if(result != null) {
				BlockState state = level.getBlockState(result.getBlockPos());
				if(state.getBlock().getBlastResistance() < 100 && !state.isAir()) {
					level.setBlockState(result.getBlockPos(), Blocks.GOLD_BLOCK.getDefaultState(), 3);
				}
			}
			if(entity.getMainHandStack() != ItemStack.EMPTY) {
				Item item = entity.getMainHandStack().getItem();
				if(item instanceof SwordItem && item != Items.GOLDEN_SWORD) {
					entity.setStackInHand(Hand.MAIN_HAND, new ItemStack(Items.GOLDEN_SWORD));
				}
				else if(item instanceof ShovelItem && item != Items.GOLDEN_SHOVEL) {
					entity.setStackInHand(Hand.MAIN_HAND, new ItemStack(Items.GOLDEN_SHOVEL));
				}
				else if(item instanceof PickaxeItem && item != Items.GOLDEN_PICKAXE) {
					entity.setStackInHand(Hand.MAIN_HAND, new ItemStack(Items.GOLDEN_PICKAXE));
				}
				else if(item instanceof AxeItem && item != Items.GOLDEN_AXE) {
					entity.setStackInHand(Hand.MAIN_HAND, new ItemStack(Items.GOLDEN_AXE));
				}
				else if(item instanceof HoeItem && item != Items.GOLDEN_HOE) {
					entity.setStackInHand(Hand.MAIN_HAND, new ItemStack(Items.GOLDEN_HOE));
				}
				else if(item == Items.APPLE) {
					entity.setStackInHand(Hand.MAIN_HAND, new ItemStack(Items.GOLDEN_APPLE, entity.getMainHandStack().getCount()));					
				}
				else if(item == Items.CARROT) {
					entity.setStackInHand(Hand.MAIN_HAND, new ItemStack(Items.GOLDEN_CARROT, entity.getMainHandStack().getCount()));		
				}
				else if(item == Items.MELON_SLICE) {
					entity.setStackInHand(Hand.MAIN_HAND, new ItemStack(Items.GLISTERING_MELON_SLICE, entity.getMainHandStack().getCount()));					
				}
				else if(item instanceof BlockItem && item != Items.GOLD_BLOCK){
					entity.setStackInHand(Hand.MAIN_HAND, new ItemStack(Items.GOLD_BLOCK, entity.getMainHandStack().getCount()));										
				}
				else if(!(item instanceof BlockItem) && !(item instanceof ToolItem) && item != Items.GOLDEN_APPLE && item != Items.GOLDEN_CARROT && item != Items.GLISTERING_MELON_SLICE){
					entity.setStackInHand(Hand.MAIN_HAND, new ItemStack(Items.GOLD_INGOT, entity.getMainHandStack().getCount()));										
				}
			}
			if(entity.getOffHandStack() != ItemStack.EMPTY) {
				Item item = entity.getOffHandStack().getItem();
				if(item instanceof SwordItem && item != Items.GOLDEN_SWORD) {
					entity.setStackInHand(Hand.OFF_HAND, new ItemStack(Items.GOLDEN_SWORD));
				}
				else if(item instanceof ShovelItem && item != Items.GOLDEN_SHOVEL) {
					entity.setStackInHand(Hand.OFF_HAND, new ItemStack(Items.GOLDEN_SHOVEL));
				}
				else if(item instanceof PickaxeItem && item != Items.GOLDEN_PICKAXE) {
					entity.setStackInHand(Hand.OFF_HAND, new ItemStack(Items.GOLDEN_PICKAXE));
				}
				else if(item instanceof AxeItem && item != Items.GOLDEN_AXE) {
					entity.setStackInHand(Hand.OFF_HAND, new ItemStack(Items.GOLDEN_AXE));
				}
				else if(item instanceof HoeItem && item != Items.GOLDEN_HOE) {
					entity.setStackInHand(Hand.OFF_HAND, new ItemStack(Items.GOLDEN_HOE));
				}
				else if(item == Items.APPLE) {
					entity.setStackInHand(Hand.OFF_HAND, new ItemStack(Items.GOLDEN_APPLE, entity.getOffHandStack().getCount()));					
				}
				else if(item == Items.CARROT) {
					entity.setStackInHand(Hand.OFF_HAND, new ItemStack(Items.GOLDEN_CARROT, entity.getOffHandStack().getCount()));		
				}
				else if(item == Items.MELON_SLICE) {
					entity.setStackInHand(Hand.OFF_HAND, new ItemStack(Items.GLISTERING_MELON_SLICE, entity.getOffHandStack().getCount()));					
				}
				else if(item instanceof BlockItem && item != Items.GOLD_BLOCK){
					entity.setStackInHand(Hand.OFF_HAND, new ItemStack(Items.GOLD_BLOCK, entity.getOffHandStack().getCount()));										
				}
				else if(!(item instanceof BlockItem) && !(item instanceof ToolItem) && item != Items.GOLDEN_APPLE && item != Items.GOLDEN_CARROT && item != Items.GLISTERING_MELON_SLICE){
					entity.setStackInHand(Hand.OFF_HAND, new ItemStack(Items.GOLD_INGOT, entity.getOffHandStack().getCount()));										
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

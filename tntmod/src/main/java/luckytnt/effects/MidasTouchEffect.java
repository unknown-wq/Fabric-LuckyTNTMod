package luckytnt.effects;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;

public class MidasTouchEffect extends MobEffect {

	public MidasTouchEffect(MobEffectCategory category, int id) {
		super(category, id);
	}

	@Override
	public boolean isBeneficial() {
		return false;
	}

	@Override
	public boolean shouldApplyEffectTickThisTick(int tickCount, int amplifier) {
		return true;
	}

	@Override
	public boolean applyEffectTick(ServerLevel level, LivingEntity entity, int amplifier) {
		// TODO(port-26.2): DISABLED — needs manual port. Original logic converted held items/armor/
		// looked-at blocks to gold and relied on PickaxeItem/SwordItem/ToolItem classes that were
		// removed in 26.2, plus RaycastContext (now ClipContext) and several renamed entity/item
		// accessors. Body stubbed so the effect still registers but is inert.
		/*
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
			// ... offhand + armor conversions (see git history) ...
		}
		*/
		return true;
	}
}

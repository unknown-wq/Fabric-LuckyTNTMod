package luckytntlib.util.dispenser;

import java.util.function.Supplier;

import luckytntlib.block.LTNTBlock;
import luckytntlib.entity.LTNTMinecart;
import luckytntlib.item.LDynamiteItem;
import luckytntlib.item.LTNTMinecartItem;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Position;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.dispenser.BlockSource;
import net.minecraft.core.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.core.dispenser.DispenseItemBehavior;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseRailBlock;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.RailShape;
import net.minecraft.world.phys.Vec3;

/**
 * Used to register default {@link DispenseItemBehavior}s for {@link LTNTBlock}s, {@link LDynamiteItem}s and {@link LTNTMinecartItem}s
 */
public class DispenserBehaviorHelper {

	private static final DefaultDispenseItemBehavior DEFAULT_BEHAVIOR = new DefaultDispenseItemBehavior();

	public static void registerTNTBlockDispenserBehavior(Supplier<LTNTBlock> tnt) {
		LTNTBlock block = tnt.get();

		DispenseItemBehavior behaviour = new DispenseItemBehavior() {

			@Override
			public ItemStack dispense(BlockSource source, ItemStack stack) {
				Level level = source.level();
				Position p = DispenserBlock.getDispensePosition(source);
				BlockPos pos = new BlockPos(Mth.floor(p.x()), Mth.floor(p.y()), Mth.floor(p.z()));
				block.explode(level, false, pos.getX(), pos.getY(), pos.getZ(), null);
				stack.shrink(1);
				return stack;
			}
		};
		DispenserBlock.registerBehavior(block, behaviour);
	}

	public static void registerDynamiteDispenserBehavior(Supplier<LDynamiteItem> dynamite) {
			LDynamiteItem item = dynamite.get();

			DispenseItemBehavior behaviour = new DispenseItemBehavior() {

				@Override
				public ItemStack dispense(BlockSource source, ItemStack stack) {
					Level level = source.level();
					Vec3 dispenserPos = new Vec3(source.pos().getX() + 0.5f, source.pos().getY() + 0.5f, source.pos().getZ() + 0.5f);
					Position pos = DispenserBlock.getDispensePosition(source);
					item.shoot(level, pos.x(), pos.y(), pos.z(), new Vec3(pos.x(), pos.y(), pos.z()).add(-dispenserPos.x(), -dispenserPos.y(), -dispenserPos.z()), 2, null);
					stack.shrink(1);
					return stack;
				}
			};
			DispenserBlock.registerBehavior(item, behaviour);
	}

	public static void registerMinecartDispenserBehavior(Supplier<LTNTMinecartItem> minecart) {
		LTNTMinecartItem item = minecart.get();

		DispenseItemBehavior behaviour = new DispenseItemBehavior() {

			@Override
			public ItemStack dispense(BlockSource source, ItemStack stack) {
				Direction direction = source.state().getValue(DispenserBlock.FACING);
				Level level = source.level();
				double x = source.center().x() + (double) direction.getStepX() * 1.125D;
				double y = Math.floor(source.center().y()) + (double) direction.getStepY();
				double z = source.center().z() + (double) direction.getStepZ() * 1.125D;
				BlockPos pos = source.pos().relative(direction);
				BlockState state = level.getBlockState(pos);
				RailShape rail = state.getBlock() instanceof BaseRailBlock ? state.getValue(((BaseRailBlock)state.getBlock()).getShapeProperty()) : RailShape.NORTH_SOUTH;
				double railHeight;
				if (state.is(BlockTags.RAILS)) {
					if (rail.isSlope()) {
						railHeight = 0.6D;
					} else {
						railHeight = 0.1D;
					}
				} else {
					if (!state.isAir() || !level.getBlockState(pos.below()).is(BlockTags.RAILS)) {
						return DEFAULT_BEHAVIOR.dispense(source, stack);
					}

					BlockState stateDown = level.getBlockState(pos.below());
					RailShape railDown = stateDown.getBlock() instanceof BaseRailBlock ? stateDown.getValue(((BaseRailBlock)stateDown.getBlock()).getShapeProperty()) : RailShape.NORTH_SOUTH;
					if (direction != Direction.DOWN && railDown.isSlope()) {
						railHeight = -0.4D;
					} else {
						railHeight = -0.9D;
					}
				}

				LTNTMinecart cart = item.createMinecart(level, x, y + railHeight, z, null);
				if (stack.has(DataComponents.CUSTOM_NAME) && stack.get(DataComponents.CUSTOM_NAME) != null && !stack.get(DataComponents.CUSTOM_NAME).getString().equals("")) {
					cart.setCustomName(stack.getHoverName());
				}
				stack.shrink(1);
				return stack;
			}
		};
		DispenserBlock.registerBehavior(item, behaviour);
	}
}

package luckytntlib.util.dispenser;

import java.util.function.Supplier;

import luckytntlib.block.LTNTBlock;
import luckytntlib.entity.LTNTMinecart;
import luckytntlib.item.LDynamiteItem;
import luckytntlib.item.LTNTMinecartItem;
import net.minecraft.block.AbstractRailBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.DispenserBlock;
import net.minecraft.block.dispenser.DispenserBehavior;
import net.minecraft.block.dispenser.ItemDispenserBehavior;
import net.minecraft.block.enums.RailShape;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.util.math.BlockPointer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Position;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

/**
 * Used to register default {@link DispenserBehavior}s for {@link LTNTBlock}s, {@link LDynamiteItem}s and {@link LTNTMinecartItem}s
 */
public class DispenserBehaviorHelper {
	
	public static void registerTNTBlockDispenserBehavior(Supplier<LTNTBlock> tnt) {
		LTNTBlock block = tnt.get();
		
		DispenserBehavior behaviour = new DispenserBehavior() {
			
			@Override
			public ItemStack dispense(BlockPointer source, ItemStack stack) {
				World level = source.world();
				Position p = DispenserBlock.getOutputLocation(source);
				BlockPos pos = new BlockPos(MathHelper.floor(p.getX()), MathHelper.floor(p.getY()), MathHelper.floor(p.getZ()));
				block.explode(level, false, pos.getX(), pos.getY(), pos.getZ(), null);
				stack.decrement(1);
				return stack;
			}
		};
		DispenserBlock.registerBehavior(block, behaviour);
	}

	public static void registerDynamiteDispenserBehavior(Supplier<LDynamiteItem> dynamite) {
    		LDynamiteItem item = dynamite.get();
    		
    		DispenserBehavior behaviour = new DispenserBehavior() {
				
				@Override
				public ItemStack dispense(BlockPointer source, ItemStack stack) {
					World level = source.world();
					Vec3d dispenserPos = new Vec3d(source.pos().getX() + 0.5f, source.pos().getY() + 0.5f, source.pos().getZ() + 0.5f);
					Position pos = DispenserBlock.getOutputLocation(source);
					item.shoot(level, pos.getX(), pos.getY(), pos.getZ(), new Vec3d(pos.getX(), pos.getY(), pos.getZ()).add(-dispenserPos.getX(), -dispenserPos.getY(), -dispenserPos.getZ()), 2, null);
					stack.decrement(1);
					return stack;
				}
			};
			DispenserBlock.registerBehavior(item, behaviour);
	}
	
	public static void registerMinecartDispenserBehavior(Supplier<LTNTMinecartItem> minecart) {
		LTNTMinecartItem item = minecart.get();
		
		DispenserBehavior behaviour = new DispenserBehavior() {
			
			@Override
			public ItemStack dispense(BlockPointer source, ItemStack stack) {
				Direction direction = source.state().get(DispenserBlock.FACING);
				World level = source.world();
				double x = source.centerPos().getX() + (double) direction.getOffsetX() * 1.125D;
				double y = Math.floor(source.centerPos().getY()) + (double) direction.getOffsetY();
				double z = source.centerPos().getZ() + (double) direction.getOffsetZ() * 1.125D;
				BlockPos pos = source.pos().offset(direction);
				BlockState state = level.getBlockState(pos);
				RailShape rail = state.getBlock() instanceof AbstractRailBlock ? state.get(((AbstractRailBlock)state.getBlock()).getShapeProperty()) : RailShape.NORTH_SOUTH;
				double railHeight;
				if (state.isIn(BlockTags.RAILS)) {
					if (rail.isAscending()) {
						railHeight = 0.6D;
					} else {
						railHeight = 0.1D;
					}
				} else {
					if (!state.isAir() || !level.getBlockState(pos.down()).isIn(BlockTags.RAILS)) {
						return new ItemDispenserBehavior().dispense(source, stack);
					}

					BlockState stateDown = level.getBlockState(pos.down());
					RailShape railDown = stateDown.getBlock() instanceof AbstractRailBlock ? stateDown.get(((AbstractRailBlock)stateDown.getBlock()).getShapeProperty()) : RailShape.NORTH_SOUTH;
					if (direction != Direction.DOWN && railDown.isAscending()) {
						railHeight = -0.4D;
					} else {
						railHeight = -0.9D;
					}
				}

				LTNTMinecart cart = item.createMinecart(level, x, y + railHeight, z, null);
				if (stack.contains(DataComponentTypes.CUSTOM_NAME) && stack.get(DataComponentTypes.CUSTOM_NAME) != null && !stack.get(DataComponentTypes.CUSTOM_NAME).getString().equals("")) {
					cart.setCustomName(stack.getName());
				}
				stack.decrement(1);
				return stack;
			}
		};
		DispenserBlock.registerBehavior(item, behaviour);
	}
}

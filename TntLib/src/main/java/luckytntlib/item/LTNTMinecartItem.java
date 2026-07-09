package luckytntlib.item;

import java.util.function.Supplier;

import org.jetbrains.annotations.Nullable;

import luckytntlib.entity.LTNTMinecart;
import net.minecraft.block.AbstractRailBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.dispenser.DispenserBehavior;
import net.minecraft.block.enums.RailShape;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.vehicle.AbstractMinecartEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.item.MinecartItem;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;

/**
 * The LTNTMinecartItem is an important step in making a custom TNT minecart.
 * It can be used to spawn a {@link LTNTMinecart} onto rails.
 * If a {@link DispenserBehavior} has been registered dispensers can also spawn the minecart.
 */
public class LTNTMinecartItem extends MinecartItem{

	@Nullable Supplier<Supplier<EntityType<LTNTMinecart>>> minecart;
	
	public LTNTMinecartItem(Item.Settings properties, @Nullable Supplier<Supplier<EntityType<LTNTMinecart>>> minecart) {
		super(AbstractMinecartEntity.Type.TNT, properties);
		this.minecart = minecart;
	}
	
	@Override
	public ActionResult useOnBlock(ItemUsageContext context) {
		World level = context.getWorld();
		BlockPos pos = context.getBlockPos();
		BlockState state = level.getBlockState(pos);
		if(!state.isIn(BlockTags.RAILS)) {
			return ActionResult.FAIL;
		}
		ItemStack stack = context.getStack();
		double railHeight = 0;
		if(!level.isClient) {
            RailShape rail = state.getBlock() instanceof AbstractRailBlock ? state.get(((AbstractRailBlock)state.getBlock()).getShapeProperty()) : RailShape.NORTH_SOUTH;
            if (rail.isAscending()) {
               railHeight = 0.5D;
            }
		}
		LTNTMinecart minecart = createMinecart(level, pos.getX() + 0.5f, pos.getY() + 0.0625f + railHeight, pos.getZ() + 0.5f, context.getPlayer());
		minecart.setOwner(context.getPlayer());
        if (stack.contains(DataComponentTypes.CUSTOM_NAME) && stack.get(DataComponentTypes.CUSTOM_NAME) != null && !stack.get(DataComponentTypes.CUSTOM_NAME).getString().equals("")) {
            minecart.setCustomName(stack.getName());
        }
        level.emitGameEvent(context.getPlayer(), GameEvent.ENTITY_PLACE, pos);
		stack.decrement(1);
		return ActionResult.success(level.isClient);
	}
	
	/**
	 * Spawns a new {@link LTNTMinecart} held by this item at the given position.
	 * @param level  the current level
	 * @param x  the x position 
	 * @param y  the y position
	 * @param z  the z position
	 * @param placer  the owner of the spawned minecart (used primarely for the {@link DamageSource})
	 * @return {@link LTNTMinecart} or null
	 * @throws NullPointerException
	 */
	@Nullable
	public LTNTMinecart createMinecart(World level, double x, double y, double z, @Nullable LivingEntity placer) throws NullPointerException{
		if(minecart != null) {
			LTNTMinecart cart = minecart.get().get().create(level);
			cart.setPosition(x, y, z);
			level.spawnEntity(cart);
			return cart;
		}
		throw new NullPointerException("Minecart entity type is null");
	}
}

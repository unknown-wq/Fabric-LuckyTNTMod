package luckytntlib.item;

import java.util.function.Supplier;

import org.jetbrains.annotations.Nullable;

import luckytntlib.entity.LTNTMinecart;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.MinecartItem;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseRailBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.RailShape;
import net.minecraft.world.level.gameevent.GameEvent;

/**
 * The LTNTMinecartItem is an important step in making a custom TNT minecart.
 * It can be used to spawn a {@link LTNTMinecart} onto rails.
 * If a dispense behavior has been registered dispensers can also spawn the minecart.
 */
public class LTNTMinecartItem extends MinecartItem{

	@Nullable Supplier<Supplier<EntityType<LTNTMinecart>>> minecart;

	public LTNTMinecartItem(Item.Properties properties, @Nullable Supplier<Supplier<EntityType<LTNTMinecart>>> minecart) {
		super(EntityTypes.TNT_MINECART, properties);
		this.minecart = minecart;
	}

	@Override
	public InteractionResult useOn(UseOnContext context) {
		Level level = context.getLevel();
		BlockPos pos = context.getClickedPos();
		BlockState state = level.getBlockState(pos);
		if(!state.is(BlockTags.RAILS)) {
			return InteractionResult.FAIL;
		}
		ItemStack stack = context.getItemInHand();
		double railHeight = 0;
		if(!level.isClientSide()) {
			RailShape rail = state.getBlock() instanceof BaseRailBlock ? state.getValue(((BaseRailBlock)state.getBlock()).getShapeProperty()) : RailShape.NORTH_SOUTH;
			if (rail.isSlope()) {
				railHeight = 0.5D;
			}
		}
		LTNTMinecart minecart = createMinecart(level, pos.getX() + 0.5f, pos.getY() + 0.0625f + railHeight, pos.getZ() + 0.5f, context.getPlayer());
		minecart.setOwner(context.getPlayer());
		if (stack.has(DataComponents.CUSTOM_NAME) && stack.get(DataComponents.CUSTOM_NAME) != null && !stack.get(DataComponents.CUSTOM_NAME).getString().isEmpty()) {
			minecart.setCustomName(stack.getHoverName());
		}
		level.gameEvent(context.getPlayer(), GameEvent.ENTITY_PLACE, pos);
		stack.shrink(1);
		return InteractionResult.SUCCESS;
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
	public LTNTMinecart createMinecart(Level level, double x, double y, double z, @Nullable LivingEntity placer) throws NullPointerException{
		if(minecart != null) {
			LTNTMinecart cart = minecart.get().get().create(level, EntitySpawnReason.SPAWN_ITEM_USE);
			cart.setPos(x, y, z);
			level.addFreshEntity(cart);
			return cart;
		}
		throw new NullPointerException("Minecart entity type is null");
	}
}

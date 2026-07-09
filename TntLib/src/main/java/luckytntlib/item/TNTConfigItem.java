package luckytntlib.item;

import luckytntlib.client.ClientAccess;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

public class TNTConfigItem extends Item {

	public TNTConfigItem() {
		super(new Item.Settings().maxCount(1));
	}

	@Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
		if(user.isSneaking()) {
			return TypedActionResult.fail(user.getStackInHand(hand));
		}
		
		if(world.isClient) {
			ClientAccess.openConfigScreenListScreen();
		}
		
		return TypedActionResult.success(user.getStackInHand(hand), true);
	}
}

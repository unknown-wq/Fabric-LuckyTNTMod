package luckytntlib.item;

import luckytntlib.LuckyTNTLib;
import luckytntlib.client.ClientAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;

public class TNTConfigItem extends Item {

	public TNTConfigItem() {
		super(new Item.Properties().stacksTo(1).setId(ResourceKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath(LuckyTNTLib.MODID, "tnt_config"))));
	}

	@Override
	public InteractionResult use(Level world, Player user, InteractionHand hand) {
		if(user.isShiftKeyDown()) {
			return InteractionResult.FAIL;
		}

		if(world.isClientSide()) {
			ClientAccess.openConfigScreenListScreen();
		}

		return InteractionResult.SUCCESS;
	}
}

package luckytnt.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import luckytnt.LuckyTNTMod;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

public class RandomTNTCommand {

	public static int executeGiveItems(ServerCommandSource command, int amount, boolean allowDuplicate, String key) {
		if(command.getEntity() instanceof PlayerEntity player) {
			if(allowDuplicate) {
				int j = 0;
				for(int i = 0; i < amount; i++) {
					if(player.giveItemStack(new ItemStack(LuckyTNTMod.RH.creativeTabItemLists.get(key).get(new Random().nextInt(LuckyTNTMod.RH.creativeTabItemLists.get(key).size())).get()))) {
						j++;
					}
				}
				int l = j;
				command.sendFeedback(() -> Text.translatable("command.randomtnt.success1").append(Text.literal(Integer.toString(l))).append(Text.translatable("command.randomtnt.success2")), false);
			} else {
				int j = 0;
				int tries = 0;
				List<Item> list = new ArrayList<>();
				while(j < amount && tries < 1000) {
					Item item = LuckyTNTMod.RH.creativeTabItemLists.get(key).get(new Random().nextInt(LuckyTNTMod.RH.creativeTabItemLists.get(key).size())).get();
					if(!list.contains(item) && player.giveItemStack(new ItemStack(item))) {
						j++;
						list.add(item);
					}
					tries++;
				}
				int l = j;
				command.sendFeedback(() -> Text.translatable("command.randomtnt.success1").append(Text.literal(Integer.toString(l))).append(Text.translatable("command.randomtnt.success2")), false);
			}
		}
		return 1;
	}
}

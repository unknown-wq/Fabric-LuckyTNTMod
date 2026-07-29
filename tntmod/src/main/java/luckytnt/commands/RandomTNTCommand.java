package luckytnt.commands;

import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.function.Supplier;

import luckytnt.LuckyTNTMod;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;

public class RandomTNTCommand {

	/**
	 * Upper bound for the 'amount' argument. A player inventory holds at most 36 stacks (+ offhand), so anything
	 * beyond this can never be added anyway. The argument is registered as IntegerArgumentType.integer(1) with no
	 * maximum, so without this clamp '/randomtnt <tab> 1000000' would spin the server thread.
	 */
	private static final int MAX_AMOUNT = 2368;

	public static int executeGiveItems(CommandSourceStack command, int amount, boolean allowDuplicate, String key) {
		if(command.getEntity() instanceof Player player) {
			List<Supplier<? extends Item>> items = LuckyTNTMod.RH.creativeTabItemLists.get(key);
			if(items == null || items.isEmpty()) {
				return 1;
			}
			int count = Math.min(amount, MAX_AMOUNT);
			Random random = new Random();
			int j = 0;
			if(allowDuplicate) {
				for(int i = 0; i < count; i++) {
					if(player.addItem(new ItemStack(items.get(random.nextInt(items.size())).get()))) {
						j++;
					}
				}
			} else {
				int tries = 0;
				Set<Item> list = new HashSet<>();
				while(j < count && tries < 1000) {
					Item item = items.get(random.nextInt(items.size())).get();
					if(!list.contains(item) && player.addItem(new ItemStack(item))) {
						j++;
						list.add(item);
					}
					tries++;
				}
			}
			int l = j;
			command.sendSuccess(() -> Component.translatable("command.randomtnt.success1").append(Component.literal(Integer.toString(l))).append(Component.translatable("command.randomtnt.success2")), false);
		}
		return 1;
	}
}

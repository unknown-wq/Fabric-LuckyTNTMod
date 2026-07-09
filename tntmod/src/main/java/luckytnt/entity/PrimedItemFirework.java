package luckytnt.entity;

import luckytnt.tnteffects.ItemFireworkEffect;
import luckytntlib.entity.PrimedLTNT;
import net.minecraft.entity.EntityType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class PrimedItemFirework extends PrimedLTNT{

	public Item item;
	public ItemStack stack;
	
	public PrimedItemFirework(EntityType<PrimedLTNT> type, World level) {
		super(type, level, new ItemFireworkEffect());
	}
}

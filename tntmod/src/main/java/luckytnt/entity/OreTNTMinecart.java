package luckytnt.entity;

import java.util.ArrayList;
import java.util.List;

import luckytnt.registry.EntityRegistry;
import luckytnt.registry.ItemRegistry;
import luckytntlib.entity.LTNTMinecart;
import net.minecraft.entity.EntityType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class OreTNTMinecart extends LTNTMinecart{

	public List<BlockPos> availablePos = new ArrayList<>();
	
	public OreTNTMinecart(EntityType<LTNTMinecart> type, World level) {
		super(type, level, EntityRegistry.ORE_TNT, () -> ItemRegistry.ORE_TNT_MINECART, false);
	}
}

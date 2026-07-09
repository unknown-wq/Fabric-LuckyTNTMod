package luckytnt.entity;

import java.util.ArrayList;
import java.util.List;

import luckytnt.tnteffects.OreTNTEffect;
import luckytntlib.entity.PrimedLTNT;
import net.minecraft.entity.EntityType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class PrimedOreTNT extends PrimedLTNT{

	public List<BlockPos> availablePos = new ArrayList<>();
	
	public PrimedOreTNT(EntityType<PrimedLTNT> type, World level) {
		super(type, level, new OreTNTEffect());
	}
}

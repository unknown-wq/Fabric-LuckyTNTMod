package luckytnt.entity;

import luckytnt.tnteffects.CustomFireworkEffect;
import luckytntlib.entity.PrimedLTNT;
import net.minecraft.block.BlockState;
import net.minecraft.entity.EntityType;
import net.minecraft.world.World;

public class PrimedCustomFirework extends PrimedLTNT {

	public BlockState state = null;
	
	public PrimedCustomFirework(EntityType<PrimedLTNT> type, World level) {
		super(type, level, new CustomFireworkEffect());
	}
}

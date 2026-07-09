package luckytnt.entity;

import luckytnt.tnteffects.CustomFireworkEffect;
import luckytntlib.entity.PrimedLTNT;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;

public class PrimedCustomFirework extends PrimedLTNT {

	public BlockState state = null;
	
	public PrimedCustomFirework(EntityType<PrimedLTNT> type, Level level) {
		super(type, level, new CustomFireworkEffect());
	}
}

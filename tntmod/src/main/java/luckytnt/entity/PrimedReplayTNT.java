package luckytnt.entity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import luckytnt.tnteffects.ReplayTNTEffect;
import luckytntlib.entity.PrimedLTNT;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.EntityType;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

public class PrimedReplayTNT extends PrimedLTNT{

	public HashMap<BlockPos, BlockState> blocks = new HashMap<>();
	public List<HashMap<BlockPos, BlockState>> blockChanges = new ArrayList<>();
	
	public PrimedReplayTNT(EntityType<PrimedLTNT> type, Level level) {
		super(type, level, new ReplayTNTEffect());
		for(int i = 0; i < 201; i++) {
			blockChanges.add(new HashMap<>());
		}
	}
}

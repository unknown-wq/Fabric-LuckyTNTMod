package luckytnt.entity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import luckytnt.tnteffects.ReplayTNTEffect;
import luckytntlib.entity.PrimedLTNT;
import net.minecraft.block.BlockState;
import net.minecraft.entity.EntityType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class PrimedReplayTNT extends PrimedLTNT{

	public HashMap<BlockPos, BlockState> blocks = new HashMap<>();
	public List<HashMap<BlockPos, BlockState>> blockChanges = new ArrayList<>();
	
	public PrimedReplayTNT(EntityType<PrimedLTNT> type, World level) {
		super(type, level, new ReplayTNTEffect());
		for(int i = 0; i < 201; i++) {
			blockChanges.add(new HashMap<>());
		}
	}
}

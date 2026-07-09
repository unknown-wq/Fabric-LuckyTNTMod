package luckytnt.entity;

import java.util.List;

import com.mojang.datafixers.util.Pair;

import luckytnt.tnteffects.ResetTNTEffect;
import luckytntlib.entity.PrimedLTNT;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class PrimedResetTNT extends PrimedLTNT {

	public List<Pair<BlockPos, BlockState>> blocks;
	public List<Pair<Vec3d, Entity>> entities;
	
	public PrimedResetTNT(EntityType<PrimedLTNT> type, World level) {
		super(type, level, new ResetTNTEffect());
	}
}

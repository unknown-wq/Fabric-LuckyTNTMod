package luckytnt.entity;

import luckytnt.util.BlockSurviveChecks;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.thrown.SnowballEntity;
import net.minecraft.fluid.Fluids;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class SnowySnowball extends SnowballEntity {

	public SnowySnowball(EntityType<? extends SnowballEntity> type, World level) {
		super(type, level);
	}
	
	public SnowySnowball(World level, LivingEntity type) {
		super(level, type);
	}
	
	public SnowySnowball(World level, double x, double y, double z) {
		super(level, x, y, z);
	}
	
	@Override
	public void onBlockHit(BlockHitResult result) {
		super.onBlockHit(result);
		BlockPos pos = result.getBlockPos();
		if(BlockSurviveChecks.canSnowPlaceAt(getWorld().getBlockState(pos.up()), getWorld(), pos.up()) && getWorld().getBlockState(pos.up()).getBlock().getBlastResistance() < 100 && getWorld().getFluidState(pos.up()).isOf(Fluids.EMPTY)) {
			getWorld().setBlockState(pos.up(), Blocks.SNOW.getDefaultState(), 3);
		}
	}
}

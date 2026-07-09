package luckytnt.entity;

import luckytnt.util.BlockSurviveChecks;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.throwableitemprojectile.Snowball;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

public class SnowySnowball extends Snowball {

	public SnowySnowball(EntityType<? extends Snowball> type, Level level) {
		super(type, level);
	}
	
	public SnowySnowball(Level level, LivingEntity type) {
		super(level, type);
	}
	
	public SnowySnowball(Level level, double x, double y, double z) {
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

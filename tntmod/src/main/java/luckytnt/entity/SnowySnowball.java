package luckytnt.entity;

import luckytnt.util.BlockSurviveChecks;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.throwableitemprojectile.Snowball;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

public class SnowySnowball extends Snowball {

	public SnowySnowball(EntityType<? extends Snowball> type, Level level) {
		super(type, level);
	}

	public SnowySnowball(Level level, LivingEntity type) {
		super(level, type, new ItemStack(Items.SNOWBALL));
	}

	public SnowySnowball(Level level, double x, double y, double z) {
		super(level, x, y, z, new ItemStack(Items.SNOWBALL));
	}

	@Override
	public void onHitBlock(BlockHitResult result) {
		super.onHitBlock(result);
		BlockPos pos = result.getBlockPos();
		if(BlockSurviveChecks.canSnowPlaceAt(level().getBlockState(pos.above()), level(), pos.above()) && level().getBlockState(pos.above()).getBlock().getExplosionResistance() < 100 && level().getFluidState(pos.above()).is(Fluids.EMPTY)) {
			level().setBlock(pos.above(), Blocks.SNOW.defaultBlockState(), 3);
		}
	}
}

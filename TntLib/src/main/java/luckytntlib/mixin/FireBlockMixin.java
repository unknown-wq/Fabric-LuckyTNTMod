package luckytntlib.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import luckytntlib.block.LTNTBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FireBlock;
import net.minecraft.world.level.block.TntBlock;
import net.minecraft.world.level.block.state.BlockState;

/**
 * This Mixin ensures that TNT is turning into the correct TNT when it's lit by fire
 */
@Mixin(FireBlock.class)
public abstract class FireBlockMixin {

	@Shadow
	protected abstract BlockState getStateWithAge(LevelReader level, BlockPos pos, int age);

	@Shadow
	protected abstract int getBurnOdds(BlockState state);

	@Inject(method = "checkBurnOut", at = @At(value = "HEAD"), cancellable = true)
	private void redirectPrimeTnt(Level level, BlockPos pos, int chance, RandomSource random, int age, CallbackInfo ci) {
		BlockState blockState = level.getBlockState(pos);
		Block block = blockState.getBlock();

		if (!(block instanceof TntBlock)) {
			return;
		}

		int i = getBurnOdds(level.getBlockState(pos));
		if (random.nextInt(chance) < i) {
			if (random.nextInt(age + 10) < 5 && !level.isRainingAt(pos)) {
				int j = Math.min(age + random.nextInt(5) / 4, 15);
				level.setBlock(pos, getStateWithAge(level, pos, j), Block.UPDATE_ALL);
			} else {
				level.removeBlock(pos, false);
			}

			if (block instanceof TntBlock tnt) {
				if(tnt instanceof LTNTBlock ltnt) {
					ltnt.explode(level, false, pos.getX(), pos.getY(), pos.getZ(), null);
				} else {
					TntBlock.prime(level, pos);
				}
				level.setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
			}
		}
		ci.cancel();
	}
}

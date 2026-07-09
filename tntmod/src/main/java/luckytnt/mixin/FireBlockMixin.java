package luckytnt.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import luckytnt.util.mixin.FireBlockExtension;
import net.minecraft.world.level.block.FireBlock;
import net.minecraft.world.level.block.state.BlockState;

@Mixin(FireBlock.class)
public abstract class FireBlockMixin implements FireBlockExtension {

	@Shadow
	protected abstract int getBurnOdds(BlockState state);

	@Unique
	public boolean canBurn(BlockState state) {
		return getBurnOdds(state) > 0;
	}
}

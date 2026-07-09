package luckytnt.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import luckytnt.util.mixin.FireBlockExtension;
import net.minecraft.block.BlockState;
import net.minecraft.block.FireBlock;

@Mixin(FireBlock.class)
public abstract class FireBlockMixin implements FireBlockExtension {

	@Shadow
	protected abstract int getBurnChance(BlockState state);

	@Unique
	public boolean canBurn(BlockState state) {
		return getBurnChance(state) > 0;
	}
}

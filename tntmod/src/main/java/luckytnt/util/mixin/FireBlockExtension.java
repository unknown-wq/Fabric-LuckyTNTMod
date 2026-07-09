package luckytnt.util.mixin;

import net.minecraft.block.BlockState;

public interface FireBlockExtension {
	public boolean canBurn(BlockState state);
}

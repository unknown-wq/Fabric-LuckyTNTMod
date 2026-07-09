package luckytnt.util.mixin;

import net.minecraft.world.level.block.state.BlockState;

public interface FireBlockExtension {
	public boolean canBurn(BlockState state);
}

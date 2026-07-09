package luckytnt.registry;

import net.fabricmc.fabric.impl.client.rendering.ColorProviderRegistryImpl;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.client.color.block.BlockColorProvider;
import net.minecraft.client.color.item.ItemColorProvider;
import net.minecraft.client.renderer.BiomeColors;
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.BlockPos;
import net.minecraft.world.BlockRenderView;
import net.minecraft.world.level.biome.GrassColors;

public class ColorRegistry {
	
	private static final BlockColorProvider BLOCK_PROVIDER = new BlockColorProvider() {
		
		@Override
		public int getColor(BlockState state, BlockRenderView tint, BlockPos pos, int color) {
			return tint != null && pos != null ? BiomeColors.getGrassColor(tint, pos) : GrassColors.getColor(0.5D, 1D);
		}
	};
	private static final ItemColorProvider ITEM_PROVIDER = new ItemColorProvider() {
		
		@Override
		public int getColor(ItemStack stack, int color) {
			return GrassColors.getColor(0.5D, 1D);
		}
	};
	
	public static void init() {
		ColorProviderRegistryImpl.BLOCK.register(BLOCK_PROVIDER, BlockRegistry.CUSTOM_FIREWORK.get());
		ColorProviderRegistryImpl.ITEM.register(ITEM_PROVIDER, BlockRegistry.CUSTOM_FIREWORK.get());
	}
}

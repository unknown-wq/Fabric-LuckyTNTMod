package luckytnt.block;

import java.util.Collections;
import java.util.List;

import luckytntlib.util.explosions.ImprovedExplosion;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.Explosion;

public class UraniumOreBlock extends Block {
	
	public UraniumOreBlock(BlockBehaviour.Properties properties) {
		super(properties);
	}
	
	@Override
	public List<ItemStack> getDroppedStacks(BlockState state, LootContextParameterSet.Builder builder) {
		return Collections.singletonList(new ItemStack(this, 1));
	}
	
    @Override
    public void onDestroyedByExplosion(Level level, BlockPos pos, Explosion explosion) {
    	level.setBlockState(pos, Blocks.AIR.getDefaultState(), 3);
    	ImprovedExplosion explo = new ImprovedExplosion(level, new Vec3(pos.getX() + 0.5f, pos.getY(), pos.getZ() + 0.5f), 10);
    	explo.doEntityExplosion(1.5f, true);
    	explo.doBlockExplosion();
    }
}

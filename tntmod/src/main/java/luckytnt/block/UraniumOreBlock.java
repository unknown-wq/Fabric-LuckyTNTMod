package luckytnt.block;

import java.util.Collections;
import java.util.List;

import luckytntlib.util.explosions.ImprovedExplosion;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.context.LootContextParameterSet;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.explosion.Explosion;

public class UraniumOreBlock extends Block {
	
	public UraniumOreBlock(AbstractBlock.Settings properties) {
		super(properties);
	}
	
	@Override
	public List<ItemStack> getDroppedStacks(BlockState state, LootContextParameterSet.Builder builder) {
		return Collections.singletonList(new ItemStack(this, 1));
	}
	
    @Override
    public void onDestroyedByExplosion(World level, BlockPos pos, Explosion explosion) {
    	level.setBlockState(pos, Blocks.AIR.getDefaultState(), 3);
    	ImprovedExplosion explo = new ImprovedExplosion(level, new Vec3d(pos.getX() + 0.5f, pos.getY(), pos.getZ() + 0.5f), 10);
    	explo.doEntityExplosion(1.5f, true);
    	explo.doBlockExplosion();
    }
}

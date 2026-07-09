package luckytntlib.util.explosions;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * An IForEachEntityExplosionEffect is used to affect individual blocks gotten by an {@link ImprovedExplosion} 
 * or a function of the {@link ExplosionHelper} in different ways.
 * It is usually used as a parameter of a function.
 */
@FunctionalInterface
public interface IForEachBlockExplosionEffect {

	/**
	 * @param level  the current level
	 * @param pos  the position of the block
	 * @param state  the state of the block
	 * @param distance  the distance of the block to the explosion origin
	 */
	public void doBlockExplosion(World level, BlockPos pos, BlockState state, double distance);
	
}

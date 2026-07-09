package luckytntlib.block;

import java.util.List;
import java.util.function.Supplier;

import org.jetbrains.annotations.Nullable;

import luckytntlib.entity.PrimedLTNT;
import luckytntlib.registry.RegistryHelper;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Blocks;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * The LuckyTNTBlock is an extension of the {@link LTNTBlock} and serves the simple purpose of spawning a random
 * {@link PrimedLTNT} of a {@link LTNTBlock} contained in a {@link List}.
 * The list could for instance be set to one of the many lists of {@link LTNTBlock} found in the {@link RegistryHelper}.
 */
public class LuckyTNTBlock extends LTNTBlock{

	public List<Supplier<LTNTBlock>> TNTs;
	
	public LuckyTNTBlock(AbstractBlock.Settings properties, List<Supplier<LTNTBlock>> TNTs) {
		super(properties, null, false);
		this.TNTs = TNTs;
	}
	
	/**
	 * Gets a random {@link LTNTBlock} from the list held by this block and calls its explode method.
	 * Can not explode another {@link LuckyTNTBlock}.
	 * @param level  the current level
	 * @param exploded  whether or not the block was destroyed by another explosion (used for randomized fuse)
	 * @param x  the x position
	 * @param y  the y position
	 * @param z  the z position
	 * @param igniter  the owner for the spawned TNT (used primarely for the {@link DamageSource})
	 * @return {@link PrimedLTNT}
	 */
	@Override
	public PrimedLTNT explode(World level, boolean exploded, double x, double y, double z, @Nullable LivingEntity igniter) {
		int rand = random.nextInt(TNTs.size());
		if(level.getBlockState(new BlockPos((int)x, (int)y, (int)z)).getBlock() == this) {
			level.setBlockState(new BlockPos((int)x, (int)y, (int)z), Blocks.AIR.getDefaultState(), 3);
		}
		return TNTs.get(rand).get().explode(level, exploded, x, y, z, igniter);
	}
}

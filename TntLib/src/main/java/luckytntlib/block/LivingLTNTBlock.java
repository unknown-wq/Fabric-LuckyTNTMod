package luckytntlib.block;

import java.util.Random;
import java.util.function.Supplier;

import org.jetbrains.annotations.Nullable;

import luckytntlib.entity.LivingPrimedLTNT;
import luckytntlib.entity.PrimedLTNT;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.TntEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

/**
 * The LivingLTNTBlock is a simple extension of the {@link LTNTBlock} and only serves the purpose of hosting
 * a {@link LivingPrimedLTNT} instead of a {@link PrimedLTNT}.
 * This class is necessary because Minecraft's {@link LivingEntity} is fundamentally different from {@link TntEntity}.
 */
public class LivingLTNTBlock extends LTNTBlock{

	@Nullable
	protected Supplier<EntityType<LivingPrimedLTNT>> TNT;
	protected Random random = new Random();
	
	public LivingLTNTBlock(AbstractBlock.Settings properties, @Nullable Supplier<EntityType<LivingPrimedLTNT>> TNT, boolean randomizedFuseUponExploded) {
		super(properties, null, randomizedFuseUponExploded);
		this.TNT = TNT;
	}
	
	@Override
	@Nullable
	public PrimedLTNT explode(World level, boolean exploded, double x, double y, double z, @Nullable LivingEntity igniter) {
		explodus(level, exploded, x, y, z, igniter);
		return null;
	}
	
	/**
	 * Spawns a new {@link LivingPrimedLTNT} held by this block
	 * @param level  the current level
	 * @param exploded  whether or not the block was destroyed by another explosion (used for randomized fuse)
	 * @param x  the x position
	 * @param y  the y position
	 * @param z  the z position
	 * @param igniter  the owner for the spawned TNT (used primarely for the {@link DamageSource})
	 * @return {@link LivingPrimedLTNT} or null
	 * @throws NullPointerException
	 */
	@Nullable
	public LivingPrimedLTNT explodus(World level, boolean exploded, double x, double y, double z, @Nullable LivingEntity igniter) throws NullPointerException {
		if(TNT != null) {
			LivingPrimedLTNT tnt = TNT.get().create(level);
			tnt.setTNTFuse(exploded && randomizedFuseUponExploded() ? tnt.getEffect().getDefaultFuse(tnt) / 8 + random.nextInt(MathHelper.clamp(tnt.getEffect().getDefaultFuse(tnt) / 4, 1, Integer.MAX_VALUE)) : tnt.getEffect().getDefaultFuse(tnt));
			tnt.setPosition(x + 0.5f, y, z + 0.5f);
			tnt.setOwner(igniter);
			level.spawnEntity(tnt);
			level.playSound(null, new BlockPos((int)x, (int)y, (int)z), SoundEvents.ENTITY_TNT_PRIMED, SoundCategory.MASTER, 1, 1);
			if(level.getBlockState(new BlockPos((int)x, (int)y, (int)z)).getBlock() == this) {				
				level.setBlockState(new BlockPos((int)x, (int)y, (int)z), Blocks.AIR.getDefaultState(), 3);
			}
			return tnt;
		}
		throw new NullPointerException("Living TNT entity type is null");
	}
}

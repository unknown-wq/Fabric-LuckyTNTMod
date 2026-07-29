package luckytntlib.util.explosions;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.jetbrains.annotations.Nullable;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntIterator;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import luckytntlib.config.LuckyTNTLibConfigValues;
import luckytntlib.util.IExplosiveEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.EntityBasedExplosionDamageCalculator;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.ExplosionDamageCalculator;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerExplosion;
import net.minecraft.world.level.block.BaseFireBlock;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

/**
 * ImprovedExplosion is an implementation of Minecraft's {@link Explosion}.
 * It is needed because the explosion of Minecraft is rather limited in functionality and size,
 * while an ImprovedExplosion has no limit in its size and offers multiple and dynamic ways to interact with and customize the explosion.
 * Similar to Minecraft's explosion it is also raycasted, making it evaluate every block in its path.
 * Due to an improved algorithm, no block drops and no particles it also is more performant, making it best suited for humongous explosions.
 */
public class ImprovedExplosion implements Explosion {

	public final Level level;
	public final double posX, posY, posZ;
	public final int size;
	public final ExplosionDamageCalculator damageCalculator;
	public final DamageSource damageSource;
	@Nullable
	private final Entity source;
	private final Vec3 center;
	private final Map<Player, Vec3> affectedPlayers = new HashMap<>();
	/**
	 * Encoded (see {@link ImprovedExplosion#encodeBlockPos(int, int, int)}) positions of all blocks affected by the
	 * explosions run on this object that were told to save their block positions.
	 */
	final IntArrayList affectedBlocks = new IntArrayList();

	/**
	 * Held weakly so an unloaded {@link Level} is not retained forever by this static field.
	 */
	private static WeakReference<ImprovedExplosion> dummyExplosion;

	/**
	 * Creates a new ImprovedExplosion
	 * @implNote size must not be greater than 511 in most cases. See the respective doBlockExplosion method
	 * @param level  the level
	 * @param position  the center position of the explosion
	 * @param size  the rough size of the explosion, which must not be greater than 511 in most cases
	 */
	public ImprovedExplosion(Level level, Vec3 position, int size) {
		this(level, null, null, position, size);
	}

	/**
	 * Creates a new ImprovedExplosion
	 * @implNote size must not be greater than 511 in most cases. See the respective doBlockExplosion method
	 * @param level  the level
	 * @param source  the DamageSource this explosion uses
	 * @param position  the center position of the explosion
	 * @param size  the rough size of the explosion, which must not be greater than 511 in most cases
	 */
	public ImprovedExplosion(Level level, @Nullable DamageSource source, Vec3 position, int size) {
		this(level, null, source, position, size);
	}

	/**
	 * Creates a new ImprovedExplosion
	 * @implNote size must not be greater than 511 in most cases. See the respective doBlockExplosion method
	 * @param level  the level
	 * @param entity  the entity not affected by this explosion. Should be the entity causing the explosion and also an IExplosiveEntity
	 * @param position  the center position of the explosion
	 * @param size  the rough size of the explosion, which must not be greater than 511 in most cases
	 */
	public ImprovedExplosion(Level level, @Nullable Entity explodingEntity, Vec3 position, int size) {
		this(level, explodingEntity, null, position.x, position.y, position.z, size);
	}

	/**
	 * Creates a new ImprovedExplosion
	 * @implNote size must not be greater than 511 in most cases. See the respective doBlockExplosion method
	 * @param level  the level
	 * @param entity  the entity not affected by this explosion. Should be the entity causing the explosion and also an IExplosiveEntity
	 * @param source  the DamageSource this explosion uses
	 * @param position  the center position of the explosion
	 * @param size  the rough size of the explosion, which must not be greater than 511 in most cases
	 */
	public ImprovedExplosion(Level level, @Nullable Entity explodingEntity, @Nullable DamageSource source, Vec3 position, int size) {
		this(level, explodingEntity, source, position.x, position.y, position.z, size);
	}

	/**
	 * Creates a new ImprovedExplosion
	 * @implNote size must not be greater than 511 in most cases. See the respective doBlockExplosion method
	 * @param level  the level
	 * @param entity  the entity not affected by this explosion. Should be the entity causing the explosion and also an IExplosiveEntity
	 * @param x  the x center position
	 * @param y  the y center position
	 * @param z  the z center position
	 * @param size  the rough size of the explosion, which must not be greater than 511 in most cases
	 */
	public ImprovedExplosion(Level level, @Nullable Entity explodingEntity, double x, double y, double z, int size) {
		this(level, explodingEntity, null, x, y, z, size);
	}

	/**
	 * Creates a new ImprovedExplosion
	 * @implNote size must not be greater than 511 in most cases. See the respective doBlockExplosion method
	 * @param level  the level
	 * @param entity  the entity not affected by this explosion. Should be the entity causing the explosion and also an IExplosiveEntity
	 * @param source  the DamageSource this explosion uses
	 * @param x  the x center position
	 * @param y  the y center position
	 * @param z  the z center position
	 * @param size  the rough size of the explosion, which must not be greater than 511 in most cases
	 */
	public ImprovedExplosion(Level level, @Nullable Entity explodingEntity, @Nullable DamageSource source, double x, double y, double z, int size) {
		this.level = level;
		this.source = explodingEntity;
		this.posX = x;
		this.posY = y;
		this.posZ = z;
		this.size = size;
		this.center = new Vec3(x, y, z);
		this.damageSource = source == null ? level.damageSources().explosion(this) : source;
		damageCalculator = explodingEntity == null ? new ExplosionDamageCalculator() : new EntityBasedExplosionDamageCalculator(explodingEntity);
	}

	/**
	 * Creates a new ImprovedExplosion
	 * @implNote size must not be greater than 511 in most cases. See the respective doBlockExplosion method
	 * @param level  the level
	 * @param entity  the entity not affected by this explosion. Should be the entity causing the explosion and also an IExplosiveEntity
	 * @param source  the DamageSource this explosion uses
	 * @param sound  the Sound this explosion will play
	 * @param x  the x center position
	 * @param y  the y center position
	 * @param z  the z center position
	 * @param size  the rough size of the explosion, which must not be greater than 511 in most cases
	 */
	public ImprovedExplosion(Level level, @Nullable Entity explodingEntity, @Nullable DamageSource source, SoundEvent sound, double x, double y, double z, int size) {
		this(level, explodingEntity, source, x, y, z, size);
	}

	/**
	 * Gets all blocks in an area calculated by shooting vectors to the borders of a cube determined by the {@link ImprovedExplosion#size} and destroys them.
	 * If any of the relative coordinates of the affected block exceed 511 they will be clamped to that value.
	 * Encodes block positions into a singular int, increasing performance.
	 * The shape the vectors orient to can either be a sphere or a cube, depending on the players config.
	 * @param xzStrength  a multiplier to the x and z vector addition, which makes the explosion more powerful. It should not be set higher than 1.2, otherwise blocks might be skipped
	 * @param yStrength  a multiplier to the y vector addition, which makes the explosion more powerful. It should not be set to high, otherwise blocks might be skipped
	 * @param resistanceImpact  the relative impact that explosion resistance of blocks has on the penetration force of explosion
	 * @param randomVecLength  the greater this value, the more distributed the length of the explosion vectors will be. Large explosions should have a value less than 1
	 * @param fire  whether or not the explosion should spawn fire afterwards
	 * @param isStrongExplosion  whether or not fluids should be ignored in the explosion resistance calculation. Very useful for large explosions
	 */
	public void doBlockExplosion(float xzStrength, float yStrength, float resistanceImpact, float randomVecLength, boolean fire, boolean isStrongExplosion) {
		doBlockExplosion(xzStrength, yStrength, resistanceImpact, randomVecLength, fire, isStrongExplosion, true);
	}

	/**
	 * Gets all blocks in an area calculated by shooting vectors to the borders of a cube determined by the {@link ImprovedExplosion#size} and destroys them.
	 * If any of the relative coordinates of the affected block exceed 511 they will be clamped to that value.
	 * Encodes block positions into a singular int, increasing performance.
	 * The shape the vectors orient to can either be a sphere or a cube, depending on the players config.
	 * @param xzStrength  a multiplier to the x and z vector addition, which makes the explosion more powerful. It should not be set higher than 1.2, otherwise blocks might be skipped
	 * @param yStrength  a multiplier to the y vector addition, which makes the explosion more powerful. It should not be set to high, otherwise blocks might be skipped
	 * @param resistanceImpact  the relative impact that explosion resistance of blocks has on the penetration force of explosion
	 * @param randomVecLength  the greater this value, the more distributed the length of the explosion vectors will be. Large explosions should have a value less than 1
	 * @param fire  whether or not the explosion should spawn fire afterwards
	 * @param isStrongExplosion  whether or not fluids should be ignored in the explosion resistance calculation. Very useful for large explosions
	 * @param saveBlockPos  whether or not affected blocks should be saved to be used externally by {@link ImprovedExplosion#getAffectedBlocks()}
	 */
	public void doBlockExplosion(float xzStrength, float yStrength, float resistanceImpact, float randomVecLength, boolean fire, boolean isStrongExplosion, boolean saveBlockPos) {
		final BlockPos posTNT = new BlockPos(floor(posX), floor(posY), floor(posZ));
		final int tntX = posTNT.getX();
		final int tntY = posTNT.getY();
		final int tntZ = posTNT.getZ();
		final IntOpenHashSet blocks = new IntOpenHashSet();
		final RandomSource random = level.getRandom();
		final double factor = LuckyTNTLibConfigValues.EXPLOSION_PERFORMANCE_FACTOR.get();
		final double vecStepSize = factor * 1.5f - 0.225f;
		final BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();

		forEachShellCell((offX, offY, offZ, distance) -> {
			double xStep = offX / distance;
			double yStep = offY / distance;
			double zStep = offZ / distance;
			float vecLength = size * (0.7f + random.nextFloat() * 0.6f * randomVecLength);
			double blockX = posX;
			double blockY = posY;
			double blockZ = posZ;
			final double addX = xStep * factor * xzStrength;
			final double addY = yStep * factor * yStrength;
			final double addZ = zStep * factor * xzStrength;
			int lastX = Integer.MIN_VALUE, lastY = Integer.MIN_VALUE, lastZ = Integer.MIN_VALUE;
			BlockState blockState = null;
			FluidState fluidState = null;
			Optional<Float> explosionResistance = null;
			for(float vecStep = 0; vecStep < vecLength; vecStep += vecStepSize) {
				blockX += addX;
				blockY += addY;
				blockZ += addZ;
				int blockPosX = (int)blockX;
				int blockPosY = (int)blockY;
				int blockPosZ = (int)blockZ;
				if(blockPosX != lastX || blockPosY != lastY || blockPosZ != lastZ) {
					lastX = blockPosX;
					lastY = blockPosY;
					lastZ = blockPosZ;
					pos.set(blockPosX, blockPosY, blockPosZ);
					if(!level.isInWorldBounds(pos)) {
						break;
					}
					blockState = level.getBlockState(pos);
					fluidState = level.getFluidState(pos);
					explosionResistance = null;
				}
				if(!(isStrongExplosion && !fluidState.isEmpty())) {
					if(explosionResistance == null) {
						explosionResistance = damageCalculator.getBlockExplosionResistance(this, level, pos, blockState, fluidState);
					}
					if(explosionResistance.isPresent()) {
						vecLength -= (explosionResistance.get() + 0.3f) * 0.3f * resistanceImpact;
					}
					if(vecLength > 0 && damageCalculator.shouldBlockExplode(this, level, pos, blockState, vecLength) && !blockState.isAir()) {
						blocks.add(encodeBlockPos(blockPosX - tntX, blockPosY - tntY, blockPosZ - tntZ));
					}
				} else {
					blocks.add(encodeBlockPos(blockPosX - tntX, blockPosY - tntY, blockPosZ - tntZ));
				}
			}
		});

		if(saveBlockPos) {
			affectedBlocks.addAll(blocks);
		}
		final ServerLevel serverLevel = level instanceof ServerLevel sLevel ? sLevel : null;
		final BlockState air = Blocks.AIR.defaultBlockState();
		for(IntIterator iterator = blocks.iterator(); iterator.hasNext();) {
			BlockPos blockPos = decodeBlockPos(iterator.nextInt(), tntX, tntY, tntZ);
			if(serverLevel != null) {
				level.getBlockState(blockPos).getBlock().wasExploded(serverLevel, blockPos, this);
			}
			level.setBlock(blockPos, air, 3);
		}
		if(fire) {
			for(IntIterator iterator = blocks.iterator(); iterator.hasNext();) {
				BlockPos blockPos = decodeBlockPos(iterator.nextInt(), tntX, tntY, tntZ);
				if(random.nextDouble() > 0.75f && level.getBlockState(blockPos).isAir() && level.getBlockState(blockPos.below()).isSolidRender()) {
					level.setBlock(blockPos, BaseFireBlock.getState(level, blockPos), 3);
				}
			}
		}
	}

	/**
	 * Gets all blocks in an area calculated by shooting vectors to the borders of a cube determined by the {@link ImprovedExplosion#size}
	 * and does to them whatever specified in the {@link IForEachBlockExplosionEffect}.
	 * If any of the relative coordinates of the affected block exceed 511 they will be clamped to that value.
	 * Encodes block positions into a singular int, increasing performance.
	 * The shape the vectors orient to can either be a sphere or a cube, depending on the players config.
	 * @param xzStrength  a multiplier to the x and z vector addition, which makes the explosion more powerful. It should not be set to high, otherwise blocks might be skipped
	 * @param yStrength  a multiplier to the y vector addition, which makes the explosion more powerful. It should not be set to high, otherwise blocks might be skipped
	 * @param resistanceImpact  the relative impact that explosion resistance of blocks has on the penetration force of explosion
	 * @param randomVecLength  the greater this value, the more distributed the length of the explosion vectors will be. Large explosions should have a value less than 1
	 * @param isStrongExplosion  whether or not fluids should be ignored in the explosion resistance calculation. Very useful for large explosions
	 * @param blockEffect  determines what should happen to the blocks gotten by this explosion
	 */
	public void doBlockExplosion(float xzStrength, float yStrength, float resistanceImpact, float randomVecLength, boolean isStrongExplosion, IForEachBlockExplosionEffect blockEffect) {
		doBlockExplosion(xzStrength, yStrength, resistanceImpact, randomVecLength, isStrongExplosion, blockEffect, false);
	}

	/**
	 * Gets all blocks in an area calculated by shooting vectors to the borders of a cube determined by the {@link ImprovedExplosion#size}
	 * and does to them whatever specified in the {@link IForEachBlockExplosionEffect}.
	 * If any of the relative coordinates of the affected block exceed 511 they will be clamped to that value.
	 * Encodes block positions into a singular int, increasing performance.
	 * The shape the vectors orient to can either be a sphere or a cube, depending on the players config.
	 * @param xzStrength  a multiplier to the x and z vector addition, which makes the explosion more powerful. It should not be set to high, otherwise blocks might be skipped
	 * @param yStrength  a multiplier to the y vector addition, which makes the explosion more powerful. It should not be set to high, otherwise blocks might be skipped
	 * @param resistanceImpact  the relative impact that explosion resistance of blocks has on the penetration force of explosion
	 * @param randomVecLength  the greater this value, the more distributed the length of the explosion vectors will be. Large explosions should have a value less than 1
	 * @param isStrongExplosion  whether or not fluids should be ignored in the explosion resistance calculation. Very useful for large explosions
	 * @param blockEffect  determines what should happen to the blocks gotten by this explosion
	 * @param saveBlockPos  whether or not affected blocks should be saved to be used externally by {@link ImprovedExplosion#getAffectedBlocks()}
	 */
	public void doBlockExplosion(float xzStrength, float yStrength, float resistanceImpact, float randomVecLength, boolean isStrongExplosion, IForEachBlockExplosionEffect blockEffect, boolean saveBlockPos) {
		final BlockPos posTNT = new BlockPos(floor(posX), floor(posY), floor(posZ));
		final int tntX = posTNT.getX();
		final int tntY = posTNT.getY();
		final int tntZ = posTNT.getZ();
		final IntOpenHashSet blocks = new IntOpenHashSet();
		final RandomSource random = level.getRandom();
		final double factor = LuckyTNTLibConfigValues.EXPLOSION_PERFORMANCE_FACTOR.get();
		final double vecStepSize = factor * 1.5f - 0.225f;
		final BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();

		forEachShellCell((offX, offY, offZ, distance) -> {
			double xStep = offX / distance;
			double yStep = offY / distance;
			double zStep = offZ / distance;
			float vecLength = size * (0.7f + random.nextFloat() * 0.6f * randomVecLength);
			double blockX = posX;
			double blockY = posY;
			double blockZ = posZ;
			final double addX = xStep * factor * xzStrength;
			final double addY = yStep * factor * yStrength;
			final double addZ = zStep * factor * xzStrength;
			int lastX = Integer.MIN_VALUE, lastY = Integer.MIN_VALUE, lastZ = Integer.MIN_VALUE;
			BlockState blockState = null;
			FluidState fluidState = null;
			Optional<Float> explosionResistance = null;
			for(float vecStep = 0; vecStep < vecLength; vecStep += vecStepSize) {
				blockX += addX;
				blockY += addY;
				blockZ += addZ;
				int blockPosX = (int)blockX;
				int blockPosY = (int)blockY;
				int blockPosZ = (int)blockZ;
				if(blockPosX != lastX || blockPosY != lastY || blockPosZ != lastZ) {
					lastX = blockPosX;
					lastY = blockPosY;
					lastZ = blockPosZ;
					pos.set(blockPosX, blockPosY, blockPosZ);
					if(!level.isInWorldBounds(pos)) {
						break;
					}
					blockState = level.getBlockState(pos);
					fluidState = level.getFluidState(pos);
					explosionResistance = null;
				}
				if(!(isStrongExplosion && !fluidState.isEmpty())) {
					if(explosionResistance == null) {
						explosionResistance = damageCalculator.getBlockExplosionResistance(this, level, pos, blockState, fluidState);
					}
					if(explosionResistance.isPresent()) {
						vecLength -= (explosionResistance.get() + 0.3f) * 0.3f * resistanceImpact;
					}
					if(vecLength > 0 && damageCalculator.shouldBlockExplode(this, level, pos, blockState, vecLength) && !blockState.isAir()) {
						blocks.add(encodeBlockPos(blockPosX - tntX, blockPosY - tntY, blockPosZ - tntZ));
					}
				} else {
					blocks.add(encodeBlockPos(blockPosX - tntX, blockPosY - tntY, blockPosZ - tntZ));
				}
			}
		});

		if(saveBlockPos) {
			affectedBlocks.addAll(blocks);
		}
		for(IntIterator iterator = blocks.iterator(); iterator.hasNext();) {
			BlockPos blockPos = decodeBlockPos(iterator.nextInt(), tntX, tntY, tntZ);
			double distance = Math.sqrt(blockPos.distToCenterSqr(posX, posY, posZ));
			blockEffect.doBlockExplosion(level, blockPos, level.getBlockState(blockPos), distance);
		}
	}

	/**
	 * Gets blocks in an area calculated by shooting vectors to the borders of a cube determined by the {@link ImprovedExplosion#size} if the {@link IBlockExplosionCondition} is met
	 * and does to them whatever specified in the blockEffect.
	 * If any of the relative coordinates of the affected block exceed 511 they will be clamped to that value.
	 * Encodes block positions into a singular int, increasing performance.
	 * The shape the vectors orient to can either be a sphere or a cube, depending on the players config.
	 * @param xzStrength  a multiplier to the x and z vector addition, which makes the explosion more powerful. It should not be set to high, otherwise blocks might be skipped
	 * @param yStrength  a multiplier to the y vector addition, which makes the explosion more powerful. It should not be set to high, otherwise blocks might be skipped
	 * @param resistanceImpact  the relative impact that explosion resistance of blocks has on the penetration force of explosion
	 * @param randomVecLength  the greater this value, the more distributed the length of the explosion vectors will be. Large explosions should have a value less than 1
	 * @param isStrongExplosion  whether or not fluids should be ignored in the explosion resistance calculation. Very useful for large explosions
	 * @param condition  the condition on which a block is added to the set of blocks
	 * @param blockEffect  determines what should happen to the blocks gotten by this explosion
	 */
	public void doBlockExplosion(float xzStrength, float yStrength, float resistanceImpact, float randomVecLength, boolean isStrongExplosion, IBlockExplosionCondition condition, IForEachBlockExplosionEffect blockEffect) {
		doBlockExplosion(xzStrength, yStrength, resistanceImpact, randomVecLength, isStrongExplosion, condition, blockEffect, false);
	}

	/**
	 * Gets blocks in an area calculated by shooting vectors to the borders of a cube determined by the {@link ImprovedExplosion#size} if the {@link IBlockExplosionCondition} is met
	 * and does to them whatever specified in the blockEffect.
	 * If any of the relative coordinates of the affected block exceed 511 they will be clamped to that value.
	 * Encodes block positions into a singular int, increasing performance.
	 * The shape the vectors orient to can either be a sphere or a cube, depending on the players config.
	 * @param xzStrength  a multiplier to the x and z vector addition, which makes the explosion more powerful. It should not be set to high, otherwise blocks might be skipped
	 * @param yStrength  a multiplier to the y vector addition, which makes the explosion more powerful. It should not be set to high, otherwise blocks might be skipped
	 * @param resistanceImpact  the relative impact that explosion resistance of blocks has on the penetration force of explosion
	 * @param randomVecLength  the greater this value, the more distributed the length of the explosion vectors will be. Large explosions should have a value less than 1
	 * @param isStrongExplosion  whether or not fluids should be ignored in the explosion resistance calculation. Very useful for large explosions
	 * @param condition  the condition on which a block is added to the set of blocks
	 * @param blockEffect  determines what should happen to the blocks gotten by this explosion
	 * @param saveBlockPos  whether or not affected blocks should be saved to be used externally by {@link ImprovedExplosion#getAffectedBlocks()}
	 */
	public void doBlockExplosion(float xzStrength, float yStrength, float resistanceImpact, float randomVecLength, boolean isStrongExplosion, IBlockExplosionCondition condition, IForEachBlockExplosionEffect blockEffect, boolean saveBlockPos) {
		final BlockPos posTNT = new BlockPos(floor(posX), floor(posY), floor(posZ));
		final int tntX = posTNT.getX();
		final int tntY = posTNT.getY();
		final int tntZ = posTNT.getZ();
		final IntOpenHashSet blocks = new IntOpenHashSet();
		final RandomSource random = level.getRandom();
		final double factor = LuckyTNTLibConfigValues.EXPLOSION_PERFORMANCE_FACTOR.get();
		final double vecStepSize = factor * 1.5f - 0.225f;
		final BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();

		forEachShellCell((offX, offY, offZ, distance) -> {
			double xStep = offX / distance;
			double yStep = offY / distance;
			double zStep = offZ / distance;
			float vecLength = size * (0.7f + random.nextFloat() * 0.6f * randomVecLength);
			double blockX = posX;
			double blockY = posY;
			double blockZ = posZ;
			final double addX = xStep * factor * xzStrength;
			final double addY = yStep * factor * yStrength;
			final double addZ = zStep * factor * xzStrength;
			int lastX = Integer.MIN_VALUE, lastY = Integer.MIN_VALUE, lastZ = Integer.MIN_VALUE;
			BlockState blockState = null;
			FluidState fluidState = null;
			Optional<Float> explosionResistance = null;
			for(float vecStep = 0; vecStep < vecLength; vecStep += vecStepSize) {
				blockX += addX;
				blockY += addY;
				blockZ += addZ;
				int blockPosX = (int)blockX;
				int blockPosY = (int)blockY;
				int blockPosZ = (int)blockZ;
				if(blockPosX != lastX || blockPosY != lastY || blockPosZ != lastZ) {
					lastX = blockPosX;
					lastY = blockPosY;
					lastZ = blockPosZ;
					pos.set(blockPosX, blockPosY, blockPosZ);
					if(!level.isInWorldBounds(pos)) {
						break;
					}
					blockState = level.getBlockState(pos);
					fluidState = level.getFluidState(pos);
					explosionResistance = null;
				}
				if(!(isStrongExplosion && !fluidState.isEmpty())) {
					if(explosionResistance == null) {
						explosionResistance = damageCalculator.getBlockExplosionResistance(this, level, pos, blockState, fluidState);
					}
					if(explosionResistance.isPresent()) {
						vecLength -= (explosionResistance.get() + 0.3f) * 0.3f * resistanceImpact;
					}
					if(vecLength > 0 && damageCalculator.shouldBlockExplode(this, level, pos, blockState, vecLength) && !blockState.isAir()) {
						if(condition.conditionMet(level, pos, blockState, distance)) {
							blocks.add(encodeBlockPos(blockPosX - tntX, blockPosY - tntY, blockPosZ - tntZ));
						}
					}
				} else {
					if(condition.conditionMet(level, pos, blockState, distance)) {
						blocks.add(encodeBlockPos(blockPosX - tntX, blockPosY - tntY, blockPosZ - tntZ));
					}
				}
			}
		});

		if(saveBlockPos) {
			affectedBlocks.addAll(blocks);
		}
		for(IntIterator iterator = blocks.iterator(); iterator.hasNext();) {
			BlockPos blockPos = decodeBlockPos(iterator.nextInt(), tntX, tntY, tntZ);
			double distance = Math.sqrt(blockPos.distToCenterSqr(posX, posY, posZ));
			blockEffect.doBlockExplosion(level, blockPos, level.getBlockState(blockPos), distance);
		}
	}

	/**
	 * Executes {@link ImprovedExplosion#doBlockExplosion(float, float, float, float, boolean, blockEffect)} with default values.
	 * @param blockEffect  determines what should happen to the blocks gotten by this explosion
	 *
	 */
	public void doBlockExplosion(IForEachBlockExplosionEffect blockEffect) {
		doBlockExplosion(1f, 1f, 1f, 1f, false, blockEffect);
	}

	/**
	 * Executes {@link ImprovedExplosion#doBlockExplosion(float, float, float, float, boolean, condition, blockEffect)} with default values.
	 * @param blockEffect  determines what should happen to the blocks gotten by this explosion
	 */
	public void doBlockExplosion(IBlockExplosionCondition condition, IForEachBlockExplosionEffect blockEffect) {
		doBlockExplosion(1f, 1f, 1f, 1f, false, condition, blockEffect);
	}

	/**
	 * Executes {@link ImprovedExplosion#doBlockExplosion(float, float, float, float, boolean, boolean)} with default values.
	 */
	public void doBlockExplosion() {
		doBlockExplosion(1f, 1f, 1f, 1f, false, false);
	}

	/**
	 * Gets all blocks in an area calculated by shooting vectors to the borders of a cube determined by the {@link ImprovedExplosion#size} and destroys them.
	 * Values of the relative coordinates can exceed 511, allowing for bigger explosions at the cost of more ram usage and slower explosion time.
	 * @param xzStrength  a multiplier to the x and z vector addition, which makes the explosion more powerful. It should not be set to high, otherwise blocks might be skipped
	 * @param yStrength  a multiplier to the y vector addition, which makes the explosion more powerful. It should not be set to high, otherwise blocks might be skipped
	 * @param resistanceImpact  the relative impact that explosion resistance of blocks has on the penetration force of explosion
	 * @param randomVecLength  the greater this value, the more distributed the length of the explosion vectors will be. Large explosions should have a value less than 1
	 * @param fire  whether or not the explosion should spawn fire afterwards
	 * @param isStrongExplosion  whether or not fluids should be ignored in the explosion resistance calculation. Very useful for large explosions
	 * @param saveBlockPos  whether or not affected blocks should be saved to be used externally
	 */
	public void doOldBlockExplosion(float xzStrength, float yStrength, float resistanceImpact, float randomVecLength, boolean fire, boolean isStrongExplosion, boolean saveBlockPos) {
		final Set<BlockPos> blocks = new HashSet<>();
		final RandomSource random = level.getRandom();
		final double factor = LuckyTNTLibConfigValues.EXPLOSION_PERFORMANCE_FACTOR.get();
		final double vecStepSize = factor * 1.5f - 0.225f;
		final BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();

		forEachShellCell((offX, offY, offZ, distance) -> {
			double xStep = offX / distance;
			double yStep = offY / distance;
			double zStep = offZ / distance;
			float vecLength = size * (0.7f + random.nextFloat() * 0.6f * randomVecLength);
			double blockX = posX;
			double blockY = posY;
			double blockZ = posZ;
			final double addX = xStep * factor * xzStrength;
			final double addY = yStep * factor * yStrength;
			final double addZ = zStep * factor * xzStrength;
			int lastX = Integer.MIN_VALUE, lastY = Integer.MIN_VALUE, lastZ = Integer.MIN_VALUE;
			BlockState blockState = null;
			FluidState fluidState = null;
			Optional<Float> explosionResistance = null;
			for(float vecStep = 0; vecStep < vecLength; vecStep += vecStepSize) {
				blockX += addX;
				blockY += addY;
				blockZ += addZ;
				int blockPosX = (int)blockX;
				int blockPosY = (int)blockY;
				int blockPosZ = (int)blockZ;
				if(blockPosX != lastX || blockPosY != lastY || blockPosZ != lastZ) {
					lastX = blockPosX;
					lastY = blockPosY;
					lastZ = blockPosZ;
					pos.set(blockPosX, blockPosY, blockPosZ);
					if(!level.isInWorldBounds(pos)) {
						break;
					}
					blockState = level.getBlockState(pos);
					fluidState = level.getFluidState(pos);
					explosionResistance = null;
				}
				if(!(isStrongExplosion && !fluidState.isEmpty())) {
					if(explosionResistance == null) {
						explosionResistance = damageCalculator.getBlockExplosionResistance(this, level, pos, blockState, fluidState);
					}
					if(explosionResistance.isPresent()) {
						vecLength -= (explosionResistance.get() + 0.3f) * 0.3f * resistanceImpact;
					}
					if(vecLength > 0 && damageCalculator.shouldBlockExplode(this, level, pos, blockState, vecLength) && !blockState.isAir()) {
						if(!blocks.contains(pos)) {
							blocks.add(pos.immutable());
						}
					}
				} else {
					if(!blocks.contains(pos)) {
						blocks.add(pos.immutable());
					}
				}
			}
		});

		if(saveBlockPos) {
			BlockPos posTNT = new BlockPos(floor(posX), floor(posY), floor(posZ));
			for(BlockPos blockPos : blocks) {
				affectedBlocks.add(encodeBlockPos(blockPos.getX() - posTNT.getX(), blockPos.getY() - posTNT.getY(), blockPos.getZ() - posTNT.getZ()));
			}
		}
		if(level instanceof ServerLevel serverLevel) {
			for(BlockPos blockPos : blocks) {
				level.getBlockState(blockPos).getBlock().wasExploded(serverLevel, blockPos, this);
			}
		}
		if(fire) {
			for(BlockPos blockPos : blocks) {
				if(random.nextDouble() > 0.75f && level.getBlockState(blockPos).isAir() && level.getBlockState(blockPos.below()).isSolidRender()) {
					level.setBlock(blockPos, BaseFireBlock.getState(level, blockPos), 3);
				}
			}
		}
	}

	/**
	 * Iterates over every cell of the shell of this explosion, which is either a spherical shell or the surface of a cube,
	 * depending on the players config. <br>
	 * The set of cells passed to the consumer is exactly the set of cells for which
	 * {@code (int)Math.sqrt(offX * offX + offY * offY + offZ * offZ) == size} (spherical shell) or
	 * {@code offX == -size || offX == size || offY == -size || offY == size || offZ == -size || offZ == size} (cube surface) holds,
	 * but the whole interior of the cube is never visited.
	 * @param cell  the consumer that is called for every cell of the shell
	 */
	private void forEachShellCell(IShellCellConsumer cell) {
		if(LuckyTNTLibConfigValues.PERFORMANT_EXPLOSION.get()) {
			//(int)sqrt(dSqr) == size is exactly size * size <= dSqr < (size + 1) * (size + 1),
			//so the range of valid offZ can be calculated in closed form for every column
			final long innerSqr = (long)size * size;
			final long outerSqr = (long)(size + 1) * (size + 1);
			for(int offX = -size; offX <= size; offX++) {
				final long xSqr = (long)offX * offX;
				for(int offY = -size; offY <= size; offY++) {
					final long xySqr = xSqr + (long)offY * offY;
					final long maxSqr = outerSqr - xySqr;
					if(maxSqr <= 0) {
						continue;
					}
					final int zMax = floorSqrt(maxSqr - 1);
					final long minSqr = innerSqr - xySqr;
					final int zMin = minSqr <= 0 ? 0 : ceilSqrt(minSqr);
					if(zMin > zMax) {
						continue;
					}
					if(zMin == 0) {
						for(int offZ = -zMax; offZ <= zMax; offZ++) {
							cell.accept(offX, offY, offZ, Math.sqrt(xySqr + (double)offZ * offZ));
						}
					} else {
						for(int offZ = -zMax; offZ <= -zMin; offZ++) {
							cell.accept(offX, offY, offZ, Math.sqrt(xySqr + (double)offZ * offZ));
						}
						for(int offZ = zMin; offZ <= zMax; offZ++) {
							cell.accept(offX, offY, offZ, Math.sqrt(xySqr + (double)offZ * offZ));
						}
					}
				}
			}
		} else {
			//only the 6 faces of the cube are visited, every cell exactly once
			for(int offY = -size; offY <= size; offY++) {
				for(int offZ = -size; offZ <= size; offZ++) {
					cell.accept(-size, offY, offZ, cellDistance(-size, offY, offZ));
					if(size != 0) {
						cell.accept(size, offY, offZ, cellDistance(size, offY, offZ));
					}
				}
			}
			for(int offX = -size + 1; offX <= size - 1; offX++) {
				for(int offZ = -size; offZ <= size; offZ++) {
					cell.accept(offX, -size, offZ, cellDistance(offX, -size, offZ));
					cell.accept(offX, size, offZ, cellDistance(offX, size, offZ));
				}
			}
			for(int offX = -size + 1; offX <= size - 1; offX++) {
				for(int offY = -size + 1; offY <= size - 1; offY++) {
					cell.accept(offX, offY, -size, cellDistance(offX, offY, -size));
					cell.accept(offX, offY, size, cellDistance(offX, offY, size));
				}
			}
		}
	}

	private static double cellDistance(int offX, int offY, int offZ) {
		return Math.sqrt((double)offX * offX + (double)offY * offY + (double)offZ * offZ);
	}

	/**
	 * @param value  a value greater than or equal to 0
	 * @return the greatest int whose square is less than or equal to the given value
	 */
	private static int floorSqrt(long value) {
		int root = (int)Math.sqrt((double)value);
		while(root > 0 && (long)root * root > value) {
			root--;
		}
		while((long)(root + 1) * (root + 1) <= value) {
			root++;
		}
		return root;
	}

	/**
	 * @param value  a value greater than or equal to 0
	 * @return the smallest int whose square is greater than or equal to the given value
	 */
	private static int ceilSqrt(long value) {
		int root = (int)Math.sqrt((double)value);
		while((long)root * root < value) {
			root++;
		}
		while(root > 0 && (long)(root - 1) * (root - 1) >= value) {
			root--;
		}
		return root;
	}

	@FunctionalInterface
	private interface IShellCellConsumer {

		void accept(int offX, int offY, int offZ, double distance);
	}

	/**
	 * Encodes 3 coordinates into a singular int value.
	 * Coordinates greater than the absolute value of 511 will be clamped to 511.
	 * @implNote coordinates given must be realtive coordinates to the center of the explosion
	 * @param x  the x position of the block
	 * @param y  the y position of the block
	 * @param z  the z position of the block
	 * @return encoded int containing information about x, y and z positions, all of which can have values between -511 and 511
	 */
	protected int encodeBlockPos(int x, int y, int z) {
		int x0 = Integer.signum(x);
		x = Math.abs(x) > 511 ? 511 : Math.abs(x);
		x0 = x0 == -1 ? 0b1000000000 : 0;
		x += x0;

		x = x << 20;

		int y0 = Integer.signum(y);
		y = Math.abs(y) > 511 ? 511 : Math.abs(y);
		y0 = y0 == -1 ? 0b1000000000 : 0;
		y += y0;

		y = y << 10;

		int z0 = Integer.signum(z);
		z = Math.abs(z) > 511 ? 511 : Math.abs(z);
		z0 = z0 == -1 ? 0b1000000000 : 0;
		z += z0;

		return (x + y + z);
	}

	/**
	 * Decodes an encoded value generated by {@link ImprovedExplosion#encodeBlockPos(int, int, int)} into a {@link BlockPos}.
	 * @param encodedVal  the position encoded by {@link ImprovedExplosion#encodeBlockPos(int, int, int)}
	 * @return BlockPos with the relative x, y and z coordinates decoded again with an absolute max value of 511
	 */
	protected BlockPos decodeBlockPos(int encodedVal) {
		return decodeBlockPos(encodedVal, 0, 0, 0);
	}

	/**
	 * Decodes an encoded value generated by {@link ImprovedExplosion#encodeBlockPos(int, int, int)} into a {@link BlockPos}
	 * and offsets it by the given values, all in one allocation.
	 * @param encodedVal  the position encoded by {@link ImprovedExplosion#encodeBlockPos(int, int, int)}
	 * @param offX  the value the decoded x coordinate is offset by
	 * @param offY  the value the decoded y coordinate is offset by
	 * @param offZ  the value the decoded z coordinate is offset by
	 * @return BlockPos with the offset x, y and z coordinates
	 */
	protected BlockPos decodeBlockPos(int encodedVal, int offX, int offY, int offZ) {
		int zRaw = (encodedVal & 0b00000000000000000000000111111111);
		int zNeg = (encodedVal & 0b00000000000000000000001000000000) >> 9;
		int yRaw = (encodedVal & 0b00000000000001111111110000000000) >> 10;
		int yNeg = (encodedVal & 0b00000000000010000000000000000000) >> 19;
		int xRaw = (encodedVal & 0b00011111111100000000000000000000) >> 20;
		int xNeg = (encodedVal & 0b00100000000000000000000000000000) >> 29;
		int xVal = xNeg == 1 ? -xRaw : xRaw;
		int yVal = yNeg == 1 ? -yRaw : yRaw;
		int zVal = zNeg == 1 ? -zRaw : zRaw;
		return new BlockPos(xVal + offX, yVal + offY, zVal + offZ);
	}

	/**
	 * Damages and throws back all entities affected by this explosion determined by the {@link ImprovedExplosion#size}.
	 * @param knockbackStrength  multiplier to the strength of the knockback
	 * @param damageEntities  whether or not entities should be damaged by this explosion
	 */
	public void doEntityExplosion(float knockbackStrength, boolean damageEntities) {
		List<Entity> entities = level.getEntities(source, entityBoundingBox());
		for(Entity entity : entities) {
			if(!entity.ignoreExplosion(this)) {
				double distance = Math.sqrt(entity.distanceToSqr(center)) / (size * 2);
				if(distance <= 1f) {
					double offX = (entity.getX() - posX);
					double offY = (entity.getEyePosition().y - posY);
					double offZ = (entity.getZ() - posZ);
					double distance2 = Math.sqrt(offX * offX + offY * offY + offZ * offZ);
					offX /= distance2;
					offY /= distance2;
					offZ /= distance2;
					//the visibility raycast can be skipped whenever the falloff is 0, because the damage is 0 either way
					float falloff = 1f - (float)distance;
					float damage = falloff <= 0f ? 0f : falloff * (float)ServerExplosion.getSeenPercent(center, entity);
					if(damageEntities && level instanceof ServerLevel serverLevel) {
						entity.hurtServer(serverLevel, damageSource, (damage * damage + damage) / 2f * 7 * size + 1f);
					}
					double knockback = damage;
					if(entity instanceof LivingEntity lEnt) {
						knockback = transformExplosionKnockback(lEnt, damage);
					}
					entity.setDeltaMovement(entity.getDeltaMovement().add(offX * knockback * knockbackStrength, offY * knockback * knockbackStrength, offZ * knockback * knockbackStrength));
					if(entity instanceof Player player) {
						player.hurtMarked = true;
						if(!player.isSpectator() && (!player.isCreative() || !player.getAbilities().flying)) {
							affectedPlayers.put(player, new Vec3(offX * damage, offY * damage, offZ * damage));
						}
					}
				}
			}
		}
	}

	/**
	 * Does whatever specified in the {@link IForEachBlockExplosionEffect} to all entities gotten by this explosion,
	 * which is determined by the {@link ImprovedExplosion#size}.
	 * @param entityEffect  determines what should be done to the entities gotten by this explosion
	 */
	public void doEntityExplosion(IForEachEntityExplosionEffect entityEffect) {
		List<Entity> entities = level.getEntities(source, entityBoundingBox());
		for(Entity entity : entities) {
			if(!entity.ignoreExplosion(this)) {
				double distance = Math.sqrt(entity.distanceToSqr(center)) / (size * 2);
				if(distance < 1f && distance != 0) {
					entityEffect.doEntityExplosion(entity, distance);
				}
			}
		}
	}

	/**
	 * @return the {@link AABB} entities are searched in, clamped to the build height of the level
	 */
	private AABB entityBoundingBox() {
		double reach = size * 2;
		double minY = Math.max(posY - reach, level.getMinY());
		double maxY = Math.min(posY + reach, level.getMaxY() + 1);
		return new AABB(posX - reach, minY, posZ - reach, posX + reach, maxY, posZ + reach);
	}

	/**
	 * Gets the players affected by the last {@link ImprovedExplosion#doEntityExplosion(float, boolean)} together with their knockback vector.
	 * @return a map of affected players and the velocity applied to them
	 */
	public Map<Player, Vec3> getAffectedPlayers() {
		return affectedPlayers;
	}

	public static double transformExplosionKnockback(LivingEntity entity, double velocity) {
		double resistance = entity.getAttributeValue(Attributes.EXPLOSION_KNOCKBACK_RESISTANCE);
		return velocity * (1.0 - resistance);
	}

	/**
	 * @implNote This method has been copied from the Forge version to ensure the same behaviour
	 * @param d The double that's supposed to be floored
	 * @return int that has been floored
	 */
	private static int floor(double d) {
		int i = (int) d;
		return d < (double) i ? i - 1 : i;
	}

	/**
	 * @implNote Must not be used to create an actual explosion!
	 * @return ImprovedExplosion with no strength and position at (0, 0, 0)
	 */
	public static ImprovedExplosion dummyExplosion(Level level) {
		ImprovedExplosion dummy = dummyExplosion == null ? null : dummyExplosion.get();
		if(dummy == null || dummy.level != level) {
			dummy = new ImprovedExplosion(level, new Vec3(0, 0, 0), 0);
			dummyExplosion = new WeakReference<>(dummy);
		}
		return dummy;
	}

	@Nullable
	public List<BlockPos> getAffectedBlocks() {
		List<BlockPos> blocks = new ArrayList<>(affectedBlocks.size());
		int offX = floor(posX);
		int offY = floor(posY);
		int offZ = floor(posZ);
		for(int index = 0; index < affectedBlocks.size(); index++) {
			blocks.add(decodeBlockPos(affectedBlocks.getInt(index), offX, offY, offZ));
		}
		return blocks;
	}

	@Override
	public ServerLevel level() {
		return (ServerLevel) level;
	}

	@Override
	public Explosion.BlockInteraction getBlockInteraction() {
		return Explosion.BlockInteraction.KEEP;
	}

	@Override
	@Nullable
	public LivingEntity getIndirectSourceEntity() {
		if(source instanceof IExplosiveEntity ent) {
			return ent.owner();
		}
		return Explosion.getIndirectSourceEntity(source);
	}

	@Override
	@Nullable
	public Entity getDirectSourceEntity() {
		return source;
	}

	@Override
	public float radius() {
		return size;
	}

	@Override
	public Vec3 center() {
		return center;
	}

	@Override
	public boolean canTriggerBlocks() {
		return false;
	}

	@Override
	public boolean shouldAffectBlocklikeEntities() {
		return false;
	}
}

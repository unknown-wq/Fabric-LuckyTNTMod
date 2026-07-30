package luckytntlib.util.tnteffects;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

import org.jetbrains.annotations.Nullable;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntIterator;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import luckytntlib.block.LTNTBlock;
import luckytntlib.config.LuckyTNTLibConfigValues;
import luckytntlib.entity.PrimedLTNT;
import luckytntlib.item.LDynamiteItem;
import luckytntlib.util.IExplosiveEntity;
import luckytntlib.util.explosions.IForEachBlockExplosionEffect;
import luckytntlib.util.explosions.ImprovedExplosion;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseFireBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.Vec3;

/**
 * TNTXStrengthEffect is an extension of the {@link PrimedTNTEffect} and is an easy way to use an {@link ImprovedExplosion} as a TNT effect.
 * <p>
 * It offers all the customization needed to create small and large explosions for projectiles, TNT and {@link StackedPrimedTNTEffect}.
 */
public class TNTXStrengthEffect extends PrimedTNTEffect{
	
	@Nullable private final Supplier<Supplier<LTNTBlock>> TNT;
	@Nullable private final Supplier<Supplier<LDynamiteItem>> dynamite;
	private final int fuse;
	private final int strength;
	private final float xzStrength, yStrength;
	private final float resistanceImpact;
	private final float randomVecLength;
	private final boolean fire;
	private final float knockbackStrength;
	private final boolean isStrongExplosion;
	private final float size;
	private final boolean airFuse;
	private final boolean explodesOnImpact;
	
	private TNTXStrengthEffect(@Nullable Supplier<Supplier<LTNTBlock>> TNT, @Nullable Supplier<Supplier<LDynamiteItem>> dynamite, int fuse, int strength, float xzStrength, float yStrength, float resistanceImpact, float randomVecLength, boolean fire, float knockbackStrength, boolean isStrongExplosion, float size, boolean airFuse, boolean explodesOnImpact) {
		this.TNT = TNT;
		this.dynamite = dynamite;
		this.fuse = fuse;
		this.strength = strength;
		this.xzStrength = xzStrength;
		this.yStrength = yStrength;
		this.resistanceImpact = resistanceImpact;
		this.randomVecLength = randomVecLength;
		this.fire = fire;
		this.knockbackStrength = knockbackStrength;
		this.isStrongExplosion = isStrongExplosion;
		this.size = size;
		this.airFuse = airFuse;
		this.explodesOnImpact = explodesOnImpact;
	}

	@Override
	public void serverExplosion(IExplosiveEntity entity) {
		//getPos() built a fresh Vec3 three times. The block pass runs on a SectionSkippingExplosion, whose
		//ray walk is identical to ImprovedExplosion's but jumps over whole all-air chunk sections, and the
		//affected positions are not copied into the explosion because this object is discarded right away.
		//At strength 300 (tnt_x10000) the walk is 1.08 G loop iterations of which the sky half is ~5.4 G/10.
		final Vec3 pos = entity.getPos();
		SectionSkippingExplosion explosion = new SectionSkippingExplosion(entity.getLevel(), (Entity) entity, pos.x, pos.y + 0.5f, pos.z, strength);
		explosion.doEntityExplosion(knockbackStrength, true);
		explosion.doBlockExplosion(xzStrength, yStrength, resistanceImpact, randomVecLength, fire, isStrongExplosion, false);
	}
	
	@Override
	public Block getBlock() {
		return TNT.get().get() == null ? Blocks.TNT : TNT.get().get();
	}
	
	@Override
	public Item getItem() {
		return dynamite.get().get() == null ? Items.AIR : dynamite.get().get();
	}
	
	@Override
	public int getDefaultFuse(IExplosiveEntity entity) {
		return fuse;
	}
	
	@Override
	public float getSize(IExplosiveEntity entity) {
		return size;
	}
		
	@Override
	public boolean airFuse() {
		return airFuse;
	}
	
	@Override
	public boolean explodesOnImpact() {
		return explodesOnImpact;
	}
	
	public static class Builder {
		
		private int fuse = 80;
		private int strength = 4;
		private float xzStrength = 1f, yStrength = 1f;
		private float resistanceImpact = 1f;
		private float randomVecLength = 1f;
		private boolean fire = false;
		private float knockbackStrength = 1f;
		private boolean isStrongExplosion = false;
		private float size = 1f;
		private boolean airFuse = false;
		private boolean explodesOnImpact = true;
		
		public Builder() {			
		}
		
		private Builder(int fuse, int strength, float xzStrength, float yStrength, float resistanceImpact, float randomVecLength, boolean fire, float knockbackStrength, boolean isStrongExplosion, float size, boolean airFuse,  boolean explodesOnImpact) {
			this.fuse = fuse;
			this.strength = strength;
			this.xzStrength = xzStrength;
			this.yStrength = yStrength;
			this.resistanceImpact = resistanceImpact;
			this.randomVecLength = randomVecLength;
			this.fire = fire;
			this.knockbackStrength = knockbackStrength;
			this.isStrongExplosion = isStrongExplosion;
			this.size = size;
			this.airFuse = airFuse;
			this.explodesOnImpact = explodesOnImpact;
		}

		/**
		 * Determines the time in ticks the Explosive will wait before exploding
		 * @implNote defaults to 80 Ticks (4 Seconds)
		 * @param fuse in ticks
		 */
		public Builder fuse(int fuse) {
			return new Builder(fuse, strength, xzStrength, yStrength, resistanceImpact, randomVecLength, fire, knockbackStrength, isStrongExplosion, size, airFuse, explodesOnImpact);
		}
		
		/**
		 * Determines the base strength/radius of the explosion
		 * @implNote defaults to 4 (just like TNT)
		 * @param strength
		 */
		public Builder strength(int strength) {
			return new Builder(fuse, strength, xzStrength, yStrength, resistanceImpact, randomVecLength, fire, knockbackStrength, isStrongExplosion, size, airFuse, explodesOnImpact);
		}
		
		/**
		 * This is a specific value that scales the explosion in the x and z direction
		 * @implNote defaults to 1f
		 * @param xzStrength
		 */
		public Builder xzStrength(float xzStrength) {
			return new Builder(fuse, strength, xzStrength, yStrength, resistanceImpact, randomVecLength, fire, knockbackStrength, isStrongExplosion, size, airFuse, explodesOnImpact);
		}
		
		/**
		 * This is a specific value that scales the explosion in the y direction
		 * @implNote defaults to 1f
		 * @param yStrength
		 */
		public Builder yStrength(float yStrength) {
			return new Builder(fuse, strength, xzStrength, yStrength, resistanceImpact, randomVecLength, fire, knockbackStrength, isStrongExplosion, size, airFuse, explodesOnImpact);
		}
		
		/**
		 * This value determines the impact that explosion resistance of blocks has on the explosion. Higher values equal weaker explosions
		 * @implNote defaults to 1f
		 * @param resistanceImpact
		 */
		public Builder resistanceImpact(float resistanceImpact) {
			return new Builder(fuse, strength, xzStrength, yStrength, resistanceImpact, randomVecLength, fire, knockbackStrength, isStrongExplosion, size, airFuse, explodesOnImpact);
		}
		
		/**
		 * This value is a multiplier to the random vector length of explosion vectors shot by the {@link ImprovedExplosion}, 
		 * which is also affected by the base strength/radius. Higher values give more noisy explosion edges
		 * @implNote defaults to 1f
		 * @param randomVecLength
		 */
		public Builder randomVecLength(float randomVecLength) {
			return new Builder(fuse, strength, xzStrength, yStrength, resistanceImpact, randomVecLength, fire, knockbackStrength, isStrongExplosion, size, airFuse, explodesOnImpact);
		}

		/**
		 * This boolean determines whether or not this explosion should spawn fire, similar to a Ghast fireball
		 * @implNote defaults to false
		 * @param fire
		 */
		public Builder fire(boolean fire) {
			return new Builder(fuse, strength, xzStrength, yStrength, resistanceImpact, randomVecLength, fire, knockbackStrength, isStrongExplosion, size, airFuse, explodesOnImpact);
		}

		/**
		 * This value is a multiplier to the knockback inflicted on entities by the explosion
		 * @implNote defaults to 1f
		 * @param knockbackStrength
		 */
		public Builder knockbackStrength(float knockbackStrength) {
			return new Builder(fuse, strength, xzStrength, yStrength, resistanceImpact, randomVecLength, fire, knockbackStrength, isStrongExplosion, size, airFuse, explodesOnImpact);
		}
		
		/**
		 * This boolean decides whether or not the explosion resistance of fluids should be taken into account.
		 * If turned on, water or lava will have huge effects on the looks of the explosion and will make even the strongest of explosions very tiny
		 * @implNote defaults to false
		 * @param isStrongExplosion
		 */
		public Builder isStrongExplosion(boolean isStrongExplosion) {
			return new Builder(fuse, strength, xzStrength, yStrength, resistanceImpact, randomVecLength, fire, knockbackStrength, isStrongExplosion, size, airFuse, explodesOnImpact);
		}

		/**
		 * This value defines the size of the rendered Block/Item
		 * @implNote defaults to 1f
		 * @param size
		 */
		public Builder size(float size) {
			return new Builder(fuse, strength, xzStrength, yStrength, resistanceImpact, randomVecLength, fire, knockbackStrength, isStrongExplosion, size, airFuse, explodesOnImpact);
		}
		
		/**
		 * This boolean decides whether or not an explosive projectile should be allowed to tick down their fuse in the air.
		 * If enabled, the projectile can explode in the air. Otherwise it will only tick down as long as it is grounded
		 * @implNote only works for explosive projectiles
		 * @implNote defaults to false
		 * @param airFuse
		 */
		public Builder airFuse(boolean airFuse) {
			return new Builder(fuse, strength, xzStrength, yStrength, resistanceImpact, randomVecLength, fire, knockbackStrength, isStrongExplosion, size, airFuse, explodesOnImpact);
		}
		
		/**
		 * This boolean determines whether or not an explosive projectile explodes immediately upon impact.
		 * If enabled, the projectile will ignore any fuse the moment it hits an entity or a block
		 * @implNote only works for explosive projectiles
		 * @implNote defaults to true
		 * @param explodesOnImpact
		 */
		public Builder explodesOnImpact(boolean explodesOnImpact) {
			return new Builder(fuse, strength, xzStrength, yStrength, resistanceImpact, randomVecLength, fire, knockbackStrength, isStrongExplosion, size, airFuse, explodesOnImpact);
		}
		
		/**
		 * Builds a new {@link TNTXStrengthEffect} without a TNT Block or Dynamite Item.
		 * @implNote Should only be used in secondary effects for {@link StackedPrimedTNTEffect}
		 * @return new TNTXStrengthEffect
		 */
		public TNTXStrengthEffect build() {
			return new TNTXStrengthEffect(null, null, fuse, strength, xzStrength, yStrength, resistanceImpact, randomVecLength, fire, knockbackStrength, isStrongExplosion, size, airFuse, explodesOnImpact);
		}
		
		/**
		 * Builds a new {@link TNTXStrengthEffect} with a TNT Block.
		 * @param TNT  a {@link Supplier} of a {@link Supplier} of a {@link PrimedLTNT}
		 * @return new TNTXStrengthEffect
		 */
		public TNTXStrengthEffect buildTNT(Supplier<Supplier<LTNTBlock>> TNT) {
			return new TNTXStrengthEffect(TNT, null, fuse, strength, xzStrength, yStrength, resistanceImpact, randomVecLength, fire, knockbackStrength, isStrongExplosion, size, airFuse, explodesOnImpact);
		}
		
		/**
		 * Builds a new {@link TNTXStrengthEffect} with a Dynamite Item.
		 * @param dynamite  a {@link Supplier} of a {@link Supplier} of a {@link LDynamiteItem}
		 * @return new TNTXStrengthEffect
		 */
		public TNTXStrengthEffect buildDynamite(Supplier<Supplier<LDynamiteItem>> dynamite) {
			return new TNTXStrengthEffect(null, dynamite, fuse, strength, xzStrength, yStrength, resistanceImpact, randomVecLength, fire, knockbackStrength, isStrongExplosion, size, airFuse, explodesOnImpact);
		}
	}

	/**
	 * An {@link ImprovedExplosion} that walks its rays exactly like its parent, but
	 * <ul>
	 * <li>jumps over a whole 16³ chunk section in one step whenever that section contains nothing but air, and</li>
	 * <li>reads the block and the fluid state out of the cached {@link LevelChunkSection} instead of going through
	 * {@link Level#getBlockState(BlockPos)} and {@link Level#getFluidState(BlockPos)}, which resolve the chunk twice
	 * for every cell.</li>
	 * </ul>
	 * <b>Why the jump does not change the explosion.</b> {@link LevelChunk#getBlockState(BlockPos)} and
	 * {@link LevelChunk#getFluidState(int, int, int)} both short circuit a section with
	 * {@link LevelChunkSection#hasOnlyAir()} to {@code Blocks.AIR.defaultBlockState()} and
	 * {@code Fluids.EMPTY.defaultFluidState()}. For such a cell
	 * {@link net.minecraft.world.level.ExplosionDamageCalculator#getBlockExplosionResistance} returns
	 * {@link Optional#empty()} (and {@code EntityBasedExplosionDamageCalculator} maps over the empty optional), so the
	 * remaining vector length is not reduced, and the {@code !blockState.isAir()} guard keeps the cell out of the
	 * affected set. Iterating such a cell is a pure no-op, so a run of them can be replaced by advancing the position
	 * and the accumulated step in one go. {@code shouldBlockExplode} is a constant {@code true} on both vanilla
	 * calculators and on {@link Entity}, and neither call has a side effect.
	 * <p>
	 * <b>Cost.</b> The parent walk is {@code 4 * PI * size²} rays of {@code size * (0.7 + 0.3 * randomVecLength) / 0.225}
	 * steps each, that is {@code ~40 * size³} iterations. At {@code size = 300} (tnt_x10000) that measured 24.1 s on a
	 * dedicated server. Every ray that leaves the terrain spends the rest of its length walking the sky one 0.3 block
	 * step at a time; here it crosses ~1.5 sections per 16 blocks instead, which is ~24 cell reads replaced by one
	 * chunk section probe.
	 * <p>
	 * This class belongs next to {@link ImprovedExplosion} in {@code luckytntlib.util.explosions}; it lives here
	 * because that package was out of scope for the change that introduced it.
	 */
	public static class SectionSkippingExplosion extends ImprovedExplosion {

		/**
		 * Replacement for the package private {@code ImprovedExplosion#affectedBlocks}, which a subclass cannot fill.
		 */
		private final IntArrayList savedBlocks = new IntArrayList();

		//Single entry caches. Consecutive cells of a ray, and consecutive rays, almost always land in the same
		//chunk and in the same chunk section, so one entry is enough to remove nearly every lookup.
		@Nullable private LevelChunk cachedChunk;
		private int cachedChunkX = Integer.MIN_VALUE;
		private int cachedChunkZ = Integer.MIN_VALUE;
		@Nullable private LevelChunkSection cachedSection;
		private int cachedSectionX = Integer.MIN_VALUE;
		private int cachedSectionY = Integer.MIN_VALUE;
		private int cachedSectionZ = Integer.MIN_VALUE;
		private boolean cachedSectionAir;
		/**
		 * Highest non-air y over all 256 columns of {@link SectionSkippingExplosion#cachedChunk},
		 * {@link Integer#MIN_VALUE} while it has not been asked for yet.
		 */
		private int cachedChunkSurfaceTop = Integer.MIN_VALUE;

		//Invariants of the walk that is currently running. The object is single use, so fields are cheaper than
		//threading a dozen arguments through the ray method, which is called once per cell of the explosion shell.
		private float walkXzStrength, walkYStrength, walkResistanceImpact, walkRandomVecLength;
		private boolean walkIsStrongExplosion;
		private boolean walkCanSkip;
		private boolean walkUseSurface;
		private IntOpenHashSet walkBlocks;
		private RandomSource walkRandom;
		private double walkFactor, walkVecStepSize;
		private BlockPos.MutableBlockPos walkPos;
		private int walkTntX, walkTntY, walkTntZ;
		/**
		 * {@code walkStepTable[n]} is the value {@code vecStep} of the parent loop holds in its n-th iteration,
		 * that is the float sum of n copies of {@code vecStepSize}. Every ray starts that sum at 0 and adds the
		 * same constant, so the whole explosion shares one table. Skipping ahead is then an index addition and
		 * stays bit identical to the parent, which a single {@code vecStep += n * vecStepSize} would not be.
		 */
		private float[] walkStepTable;

		public SectionSkippingExplosion(Level level, Vec3 position, int size) {
			super(level, position, size);
		}

		public SectionSkippingExplosion(Level level, @Nullable Entity explodingEntity, Vec3 position, int size) {
			super(level, explodingEntity, position, size);
		}

		public SectionSkippingExplosion(Level level, @Nullable Entity explodingEntity, @Nullable DamageSource source, Vec3 position, int size) {
			super(level, explodingEntity, source, position, size);
		}

		public SectionSkippingExplosion(Level level, @Nullable Entity explodingEntity, double x, double y, double z, int size) {
			super(level, explodingEntity, x, y, z, size);
		}

		@Override
		public void doBlockExplosion(float xzStrength, float yStrength, float resistanceImpact, float randomVecLength, boolean fire, boolean isStrongExplosion, boolean saveBlockPos) {
			if(!prepareWalk(xzStrength, yStrength, resistanceImpact, randomVecLength, isStrongExplosion)) {
				super.doBlockExplosion(xzStrength, yStrength, resistanceImpact, randomVecLength, fire, isStrongExplosion, saveBlockPos);
				return;
			}
			final BlockPos posTNT = new BlockPos(floorDouble(posX), floorDouble(posY), floorDouble(posZ));
			final int tntX = posTNT.getX();
			final int tntY = posTNT.getY();
			final int tntZ = posTNT.getZ();
			final IntOpenHashSet blocks = collectBlocks(tntX, tntY, tntZ);

			if(saveBlockPos) {
				savedBlocks.addAll(blocks);
			}
			final ServerLevel serverLevel = level instanceof ServerLevel sLevel ? sLevel : null;
			final BlockState air = Blocks.AIR.defaultBlockState();
			//see ImprovedExplosion#doBlockExplosion for the reasoning behind the two sets of block flags
			for(IntIterator iterator = blocks.iterator(); iterator.hasNext();) {
				final int encodedPos = iterator.nextInt();
				BlockPos blockPos = decodeBlockPos(encodedPos, tntX, tntY, tntZ);
				if(serverLevel != null) {
					level.getBlockState(blockPos).getBlock().wasExploded(serverLevel, blockPos, this);
				}
				level.setBlock(blockPos, air, isEnclosed(blocks, encodedPos)
						? Block.UPDATE_CLIENTS | Block.UPDATE_KNOWN_SHAPE
						: Block.UPDATE_NEIGHBORS | Block.UPDATE_CLIENTS);
			}
			if(fire) {
				final RandomSource random = level.getRandom();
				for(IntIterator iterator = blocks.iterator(); iterator.hasNext();) {
					BlockPos blockPos = decodeBlockPos(iterator.nextInt(), tntX, tntY, tntZ);
					if(random.nextDouble() > 0.75f && level.getBlockState(blockPos).isAir() && level.getBlockState(blockPos.below()).isSolidRender()) {
						level.setBlock(blockPos, BaseFireBlock.getState(level, blockPos), 3);
					}
				}
			}
		}

		@Override
		public void doBlockExplosion(float xzStrength, float yStrength, float resistanceImpact, float randomVecLength, boolean isStrongExplosion, IForEachBlockExplosionEffect blockEffect, boolean saveBlockPos) {
			if(!prepareWalk(xzStrength, yStrength, resistanceImpact, randomVecLength, isStrongExplosion)) {
				super.doBlockExplosion(xzStrength, yStrength, resistanceImpact, randomVecLength, isStrongExplosion, blockEffect, saveBlockPos);
				return;
			}
			final BlockPos posTNT = new BlockPos(floorDouble(posX), floorDouble(posY), floorDouble(posZ));
			final int tntX = posTNT.getX();
			final int tntY = posTNT.getY();
			final int tntZ = posTNT.getZ();
			final IntOpenHashSet blocks = collectBlocks(tntX, tntY, tntZ);

			if(saveBlockPos) {
				savedBlocks.addAll(blocks);
			}
			for(IntIterator iterator = blocks.iterator(); iterator.hasNext();) {
				BlockPos blockPos = decodeBlockPos(iterator.nextInt(), tntX, tntY, tntZ);
				double distance = Math.sqrt(blockPos.distToCenterSqr(posX, posY, posZ));
				blockEffect.doBlockExplosion(level, blockPos, level.getBlockState(blockPos), distance);
			}
		}

		@Override
		public List<BlockPos> getAffectedBlocks() {
			List<BlockPos> blocks = new ArrayList<>(savedBlocks.size());
			int offX = floorDouble(posX);
			int offY = floorDouble(posY);
			int offZ = floorDouble(posZ);
			for(int index = 0; index < savedBlocks.size(); index++) {
				blocks.add(decodeBlockPos(savedBlocks.getInt(index), offX, offY, offZ));
			}
			return blocks;
		}

		/**
		 * Runs the ray walk over the shell of this explosion and returns the encoded positions of every affected block.
		 * The set is exactly the one {@link ImprovedExplosion#doBlockExplosion} would have built.
		 */
		private boolean prepareWalk(float xzStrength, float yStrength, float resistanceImpact, float randomVecLength, boolean isStrongExplosion) {
			walkXzStrength = xzStrength;
			walkYStrength = yStrength;
			walkResistanceImpact = resistanceImpact;
			walkRandomVecLength = randomVecLength;
			walkIsStrongExplosion = isStrongExplosion;
			walkFactor = LuckyTNTLibConfigValues.EXPLOSION_PERFORMANCE_FACTOR.get();
			//exactly the expression of ImprovedExplosion, float literals included, so that the step is bit identical
			walkVecStepSize = walkFactor * 1.5f - 0.225f;
			if(!(walkVecStepSize > 0d) || size < 0) {
				return false;
			}
			//nextFloat() is below 1, so this is an upper bound for every vecLength this explosion can draw.
			//Two extra entries make sure the table always reaches past the longest ray.
			final double maxVecLength = (double)size * (0.7f + 0.6f * Math.max(randomVecLength, 0f));
			final double entries = maxVecLength / walkVecStepSize + 3d;
			if(!(entries >= 1d) || entries > 1 << 20) {
				return false;
			}
			final float[] table = new float[(int)entries];
			float value = 0f;
			for(int index = 0; index < table.length; index++) {
				table[index] = value;
				value += walkVecStepSize;
			}
			walkStepTable = table;
			//A single step must not be able to move a coordinate by a whole block, otherwise the closed form
			//section exit below could land more than one cell past the section. Every explosion of this mod
			//stays far below that (factor <= 0.6, strength <= 1.3), but the parent walk skips blocks in that
			//case anyway, so only the jump is turned off instead of guessing.
			walkCanSkip = walkFactor * Math.max(Math.abs(xzStrength), Math.abs(yStrength)) < 1d;
			//The surface jump below reads the WORLD_SURFACE heightmap of all 256 columns of a chunk once. That
			//pays for itself as soon as a ray spends more than ~256 steps inside the chunks it touches, which
			//needs a radius of a couple of chunks; below that the whole walk is a few hundred thousand steps
			//anyway. The heightmap is only guaranteed to be live on the server, and doBlockExplosion is a server
			//side operation everywhere in this mod, so the client keeps the plain walk.
			walkUseSurface = walkCanSkip && size >= 24 && !level.isClientSide();
			return true;
		}

		private IntOpenHashSet collectBlocks(int tntX, int tntY, int tntZ) {
			walkBlocks = new IntOpenHashSet();
			walkRandom = level.getRandom();
			walkPos = new BlockPos.MutableBlockPos();
			walkTntX = tntX;
			walkTntY = tntY;
			walkTntZ = tntZ;

			if(LuckyTNTLibConfigValues.PERFORMANT_EXPLOSION.get()) {
				//identical closed form spherical shell as ImprovedExplosion#forEachShellCell
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
								castRay(offX, offY, offZ, Math.sqrt(xySqr + (double)offZ * offZ));
							}
						} else {
							for(int offZ = -zMax; offZ <= -zMin; offZ++) {
								castRay(offX, offY, offZ, Math.sqrt(xySqr + (double)offZ * offZ));
							}
							for(int offZ = zMin; offZ <= zMax; offZ++) {
								castRay(offX, offY, offZ, Math.sqrt(xySqr + (double)offZ * offZ));
							}
						}
					}
				}
			} else {
				for(int offY = -size; offY <= size; offY++) {
					for(int offZ = -size; offZ <= size; offZ++) {
						castRay(-size, offY, offZ, cellDistance(-size, offY, offZ));
						if(size != 0) {
							castRay(size, offY, offZ, cellDistance(size, offY, offZ));
						}
					}
				}
				for(int offX = -size + 1; offX <= size - 1; offX++) {
					for(int offZ = -size; offZ <= size; offZ++) {
						castRay(offX, -size, offZ, cellDistance(offX, -size, offZ));
						castRay(offX, size, offZ, cellDistance(offX, size, offZ));
					}
				}
				for(int offX = -size + 1; offX <= size - 1; offX++) {
					for(int offY = -size + 1; offY <= size - 1; offY++) {
						castRay(offX, offY, -size, cellDistance(offX, offY, -size));
						castRay(offX, offY, size, cellDistance(offX, offY, size));
					}
				}
			}
			final IntOpenHashSet blocks = walkBlocks;
			walkBlocks = null;
			walkRandom = null;
			walkPos = null;
			walkStepTable = null;
			return blocks;
		}

		/**
		 * The body of {@link ImprovedExplosion#doBlockExplosion}'s ray, with the all-air section jump added.
		 */
		private void castRay(int offX, int offY, int offZ, double distance) {
			final double factor = walkFactor;
			final float[] stepTable = walkStepTable;
			final float resistanceImpact = walkResistanceImpact;
			final boolean isStrongExplosion = walkIsStrongExplosion;
			final IntOpenHashSet blocks = walkBlocks;
			final BlockPos.MutableBlockPos pos = walkPos;
			final int tntX = walkTntX, tntY = walkTntY, tntZ = walkTntZ;
			double xStep = offX / distance;
			double yStep = offY / distance;
			double zStep = offZ / distance;
			float vecLength = size * (0.7f + walkRandom.nextFloat() * 0.6f * walkRandomVecLength);
			double blockX = posX;
			double blockY = posY;
			double blockZ = posZ;
			final double addX = xStep * factor * walkXzStrength;
			final double addY = yStep * factor * walkYStrength;
			final double addZ = zStep * factor * walkXzStrength;
			int lastX = Integer.MIN_VALUE, lastY = Integer.MIN_VALUE, lastZ = Integer.MIN_VALUE;
			BlockState blockState = null;
			FluidState fluidState = null;
			Optional<Float> explosionResistance = null;
			//identical to "for(float vecStep = 0; vecStep < vecLength; vecStep += vecStepSize)": stepTable[step]
			//is exactly the vecStep of the step-th iteration, and the table always reaches past the longest ray
			for(int step = 0; step < stepTable.length && stepTable[step] < vecLength; step++) {
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
					LevelChunkSection section = sectionAt(blockPosX, blockPosY, blockPosZ);
					if(!cachedSectionAir && walkUseSurface) {
						//A section that is not empty still sits under up to 15 blocks of open sky, and a nearly
						//horizontal ray skims exactly that band for its whole length. WORLD_SURFACE is the
						//highest non-air block of a column, so above the maximum of the whole chunk every cell
						//is air for the same reason as in an empty section, and the same closed form jump
						//applies, now to the box of the chunk floored by that maximum.
						final int surfaceTop = chunkSurfaceTop();
						if(blockPosY > surfaceTop) {
							long jumps = stepsToLeaveBox(blockX, blockY, blockZ, addX, addY, addZ,
									blockPosX & ~15, (blockPosX & ~15) + 15,
									surfaceTop + 1, 1 << 29,
									blockPosZ & ~15, (blockPosZ & ~15) + 15) - 1L;
							if(jumps > 0L) {
								blockX += jumps * addX;
								blockY += jumps * addY;
								blockZ += jumps * addZ;
								step += (int)jumps;
							}
							lastX = lastY = lastZ = Integer.MIN_VALUE;
							continue;
						}
					}
					if(cachedSectionAir) {
						//nothing in this section can affect the explosion, so the walk is moved to the first
						//step whose cell is outside of it. jumps is one less than that step count because the
						//loop update and the next iteration together already advance one step.
						long jumps = walkCanSkip ? stepsToLeaveBox(blockX, blockY, blockZ, addX, addY, addZ,
								blockPosX & ~15, (blockPosX & ~15) + 15,
								blockPosY & ~15, (blockPosY & ~15) + 15,
								blockPosZ & ~15, (blockPosZ & ~15) + 15) - 1L : 0L;
						if(jumps > 0L) {
							blockX += jumps * addX;
							blockY += jumps * addY;
							blockZ += jumps * addZ;
							step += (int)jumps;
						}
						//the next step can land in this very cell again, and blockState/fluidState still hold
						//whatever the last non-air cell had, so the carried state is invalidated unconditionally
						lastX = lastY = lastZ = Integer.MIN_VALUE;
						continue;
					}
					blockState = section.getBlockState(blockPosX & 15, blockPosY & 15, blockPosZ & 15);
					fluidState = section.getFluidState(blockPosX & 15, blockPosY & 15, blockPosZ & 15);
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
		}

		/**
		 * Resolves the {@link LevelChunkSection} a block position belongs to and remembers whether it holds
		 * nothing but air in {@link SectionSkippingExplosion#cachedSectionAir}.
		 * The chunk is fetched through {@link Level#getChunk(int, int)}, exactly like
		 * {@link Level#getBlockState(BlockPos)} does, so an ungenerated chunk is generated here just as before.
		 * @return the section, or null if the position has no section, in which case it counts as air
		 */
		@Nullable
		private LevelChunkSection sectionAt(int x, int y, int z) {
			final int secX = x >> 4;
			final int secY = y >> 4;
			final int secZ = z >> 4;
			if(secX != cachedSectionX || secY != cachedSectionY || secZ != cachedSectionZ) {
				if(cachedChunk == null || secX != cachedChunkX || secZ != cachedChunkZ) {
					cachedChunk = level.getChunk(secX, secZ);
					cachedChunkX = secX;
					cachedChunkZ = secZ;
					cachedChunkSurfaceTop = Integer.MIN_VALUE;
				}
				final int index = cachedChunk.getSectionIndex(y);
				final LevelChunkSection section = index >= 0 && index < cachedChunk.getSectionsCount() ? cachedChunk.getSection(index) : null;
				cachedSection = section;
				cachedSectionX = secX;
				cachedSectionY = secY;
				cachedSectionZ = secZ;
				cachedSectionAir = section == null || section.hasOnlyAir();
			}
			return cachedSection;
		}

		/**
		 * @return the number of steps, at least 1, after which the walk is guaranteed to still be inside the given
		 * box of cells or exactly on the first cell outside of it, but never further than that
		 */
		private static long stepsToLeaveBox(double x, double y, double z, double addX, double addY, double addZ,
				int loX, int hiX, int loY, int hiY, int loZ, int hiZ) {
			long steps = stepsToLeaveRange(x, addX, loX, hiX);
			steps = Math.min(steps, stepsToLeaveRange(y, addY, loY, hiY));
			steps = Math.min(steps, stepsToLeaveRange(z, addZ, loZ, hiZ));
			return steps;
		}

		/**
		 * @return the highest non-air y of any of the 256 columns of the currently cached chunk
		 */
		private int chunkSurfaceTop() {
			if(cachedChunkSurfaceTop == Integer.MIN_VALUE) {
				int top = level.getMinY() - 1;
				final int baseX = cachedChunkX << 4;
				final int baseZ = cachedChunkZ << 4;
				for(int x = 0; x < 16; x++) {
					for(int z = 0; z < 16; z++) {
						final int height = cachedChunk.getHeight(Heightmap.Types.WORLD_SURFACE, baseX + x, baseZ + z);
						if(height > top) {
							top = height;
						}
					}
				}
				cachedChunkSurfaceTop = top;
			}
			return cachedChunkSurfaceTop;
		}

		/**
		 * Solves {@code (int)(value + steps * add)} leaving {@code [lo, hi]} for steps, rounding the answer down so
		 * that the walk can never be moved past the first cell outside the range.
		 * <p>
		 * The cast truncates towards zero, so the cell of a coordinate is not its floor for negative values:
		 * {@code (int)v == k} holds on {@code [k, k + 1)} for {@code k >= 0} and on {@code (k - 1, k]} for
		 * {@code k < 0}. Both cases are covered by picking the bound accordingly.
		 */
		private static long stepsToLeaveRange(double value, double add, int lo, int hi) {
			final double steps;
			if(add > 0d) {
				//leaving upwards means (int)v > hi, which starts at v >= hi + 1 for hi >= 0 and at v > hi for hi < 0
				final double bound = hi >= 0 ? (double)hi + 1d : (double)hi;
				steps = Math.floor((bound - value) / add - 1.0E-9d);
			} else if(add < 0d) {
				//leaving downwards means (int)v < lo, which starts at v < lo for lo > 0 and at v <= lo - 1 for lo <= 0
				final double bound = lo > 0 ? (double)lo : (double)lo - 1d;
				steps = Math.floor((value - bound) / -add - 1.0E-9d);
			} else {
				return Long.MAX_VALUE;
			}
			if(!(steps > 1d)) {
				return 1L;
			}
			//a 16 wide section cannot need more steps than this even for a nearly axis parallel ray
			return steps > 65536d ? 65536L : (long)steps;
		}

		/**
		 * Reimplementation of the private {@code ImprovedExplosion#isEnclosedByAffectedBlocks}.
		 */
		private boolean isEnclosed(IntOpenHashSet blocks, int encodedVal) {
			int xRaw = (encodedVal & 0b00011111111100000000000000000000) >> 20;
			int yRaw = (encodedVal & 0b00000000000001111111110000000000) >> 10;
			int zRaw = (encodedVal & 0b00000000000000000000000111111111);
			if(xRaw >= 510 || yRaw >= 510 || zRaw >= 510) {
				return false;
			}
			int x = (encodedVal & 0b00100000000000000000000000000000) != 0 ? -xRaw : xRaw;
			int y = (encodedVal & 0b00000000000010000000000000000000) != 0 ? -yRaw : yRaw;
			int z = (encodedVal & 0b00000000000000000000001000000000) != 0 ? -zRaw : zRaw;
			return blocks.contains(encodeBlockPos(x - 1, y, z))
					&& blocks.contains(encodeBlockPos(x + 1, y, z))
					&& blocks.contains(encodeBlockPos(x, y - 1, z))
					&& blocks.contains(encodeBlockPos(x, y + 1, z))
					&& blocks.contains(encodeBlockPos(x, y, z - 1))
					&& blocks.contains(encodeBlockPos(x, y, z + 1));
		}

		private static double cellDistance(int offX, int offY, int offZ) {
			return Math.sqrt((double)offX * offX + (double)offY * offY + (double)offZ * offZ);
		}

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

		private static int floorDouble(double d) {
			int i = (int)d;
			return d < (double)i ? i - 1 : i;
		}
	}
}

package luckytnt.tnteffects;


import luckytnt.registry.BlockRegistry;
import luckytnt.registry.EntityRegistry;
import luckytntlib.util.IExplosiveEntity;
import luckytntlib.util.tnteffects.PrimedTNTEffect;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.server.level.ServerLevel;

public class BlackHoleTNTEffect extends PrimedTNTEffect {

	/**
	 * Upper bound on how many FallingBlockEntities may orbit the black hole at once.
	 * Without it up to 800 were spawned every 20 ticks for 350 ticks (~13600 entities).
	 * 2000 was still 2000 physics entities: each one resolves collisions in move() and is tracked and
	 * synced to every nearby player once per tick for up to 350 ticks. 400 keeps the orbit visually
	 * dense (the blocks are packed into a 150x150 footprint) at a fifth of that sustained cost.
	 */
	private static final int MAX_LIVE_FALLING_BLOCKS = 400;

	/**
	 * Half extents of the per tick pull query. The old box was 200x200x200 = ~13^3 = 2197 entity
	 * sections walked every tick for 350 consecutive ticks (~769000 section visits per detonation).
	 * The falling blocks are only ever spawned within +-75 horizontally and the pull is negligible
	 * past ~50 blocks, while the 100 block y extent usually spanned most of the world height for
	 * nothing. 160x96x160 is ~10*6*10 = 600 sections, a 3.7x cut (~210000 section visits).
	 */
	private static final double PULL_RANGE_XZ = 80D;
	private static final double PULL_RANGE_Y = 48D;

	/** DustParticleOptions is immutable; this used to be allocated 150 times a tick, 52500 per detonation. */
	private static final DustParticleOptions BLACK_DUST = new DustParticleOptions(0x000000, 0.75f);

	@Override
	public void explosionTick(IExplosiveEntity ent) {
		if(ent.getTNTFuse() < 400 && ent.getTNTFuse() >= 300) {
			((Entity)ent).setNoGravity(true);
			((Entity)ent).setDeltaMovement(0, 0.05, 0);
		}
		if(ent.getTNTFuse() < 300) {
			((Entity)ent).setDeltaMovement(0, 0, 0);
		}
		// The 200 wide AABB query and the falling block spawning are both server only work
		// (the client discards spawned entities and its delta movements get overwritten by the
		// server sync anyway), so the whole block is guarded once.
		if(ent.getTNTFuse() < 350 && ent.getTNTFuse() > 0 && ent.getLevel() instanceof ServerLevel sLevel) {
			AABB range = new AABB(ent.x() - PULL_RANGE_XZ, ent.y() - PULL_RANGE_Y, ent.z() - PULL_RANGE_XZ, ent.x() + PULL_RANGE_XZ, ent.y() + PULL_RANGE_Y, ent.z() + PULL_RANGE_XZ);

			// One pass over the ~600 entity sections instead of two identical ones over ~2197.
			int liveBlocks = 0;
			for(Entity target : sLevel.getEntities((Entity)ent, range)) {
				if(target instanceof FallingBlockEntity block) {
					Vec3 vec = new Vec3(ent.x() - block.getX(), ent.y() - block.getY(), ent.z() - block.getZ());
					if(vec.length() <= 2) {
						block.discard();
						continue;
					}
					liveBlocks++;
					block.setDeltaMovement(vec.normalize().scale(0.4D).add(0, 0.1D, 0));
				} else if(target instanceof LivingEntity living) {
					Vec3 vec = new Vec3(ent.x() - living.getX(), ent.y() - living.getEyeY(), ent.z() - living.getZ());
					double distance = vec.length();
					if(distance <= 2) {
						if(living instanceof Player) {
							if(ent.getTNTFuse() % 80 == 0) {
								living.hurtServer(sLevel, sLevel.damageSources().inWall(), 6f);
							}
						} else {
							living.discard();
							continue;
						}
					}
					living.setDeltaMovement(vec.normalize().scale(Math.min((1D / (0.25D * distance + 0.0001D)) + 0.5D, 2.5D)));
					// the pull loop no longer runs client side, so the server has to actually push
					// the velocity down to the affected player
					if(living instanceof Player player) {
						player.hurtMarked = true;
					}
				}
			}

			if(ent.getTNTFuse() % 20 == 0) {
				RandomSource random = sLevel.getRandom();
				int amount = Math.min(400 + (int)Math.round(1600D / ((double)ent.getTNTFuse() * 0.5D)), 800);
				// keep the amount of concurrently orbiting falling blocks bounded
				amount = Math.min(amount, MAX_LIVE_FALLING_BLOCKS - liveBlocks);
				int entY = Mth.floor(ent.y());
				BlockPos.MutableBlockPos mutable = new BlockPos.MutableBlockPos();
				for(int i = 0; i < amount; i++) {
					int posX = Mth.floor(ent.x()) + random.nextInt(151) - 75;
					int posZ = Mth.floor(ent.z()) + random.nextInt(151) - 75;
					mutable.set(posX, entY, posZ);
					if(!sLevel.isLoaded(mutable)) {
						continue;
					}
					// heightmap lookup instead of LevelEvents.getTopBlock, which scanned the
					// whole Y column (2 getBlockState + 2 collision shape checks per step)
					int posY = sLevel.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, posX, posZ) - 1;
					mutable.set(posX, posY, posZ);
					BlockState state = sLevel.getBlockState(mutable);
					if(!state.isAir() && !state.hasBlockEntity() && state.getBlock().getExplosionResistance() < 100) {
						FallingBlockEntity.fall(sLevel, mutable.immutable(), state);
					}
				}
			}
		}
	}

	@Override
	public void serverExplosion(IExplosiveEntity ent) {
		EntityRegistry.TNT_X500_EFFECT.build().serverExplosion(ent);

		AABB range = new AABB(ent.x() - 100, ent.y() - 100, ent.z() - 100, ent.x() + 100, ent.y() + 100, ent.z() + 100);

		// one section walk instead of two over the same 200 wide box
		for(Entity target : ent.getLevel().getEntities((Entity)ent, range)) {
			if(target instanceof FallingBlockEntity block) {
				block.discard();
			} else if(target instanceof LivingEntity living) {
				double x = living.getX() - ent.x();
				double y = living.getEyeY() - ent.y();
				double z = living.getZ() - ent.z();
				Vec3 vec = new Vec3(x, y, z).normalize().scale(4);
				living.setDeltaMovement(vec);
			}
		}
	}

	@Override
	public void spawnParticles(IExplosiveEntity ent) {
		if(ent.getTNTFuse() < 350) {
			final Level level = ent.getLevel();
			final double cx = ent.x();
			final double cy = ent.y() + 0.5D;
			final double cz = ent.z();
			int amount = 150;
			double phi = Math.PI * (3D - Math.sqrt(5D));
			for(int i = 0; i < amount; i++) {
				double y = 1D - ((double)i / ((double)amount - 1D)) * 2D;
				double radius = Math.sqrt(1D - y * y);

				double theta = phi * i;

				double x = Math.cos(theta) * radius;
				double z = Math.sin(theta) * radius;

				level.addParticle(BLACK_DUST, cx + x * 2, cy + y * 2, cz + z * 2, 0, 0, 0);
			}
		}
	}

	@Override
	public Block getBlock() {
		return BlockRegistry.BLACK_HOLE_TNT.get();
	}

	@Override
	public int getDefaultFuse(IExplosiveEntity ent) {
		return 500;
	}
}

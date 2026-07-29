package luckytnt.tnteffects;


import java.util.List;

import luckytnt.event.LevelEvents;
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
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.server.level.ServerLevel;

public class BlackHoleTNTEffect extends PrimedTNTEffect {

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
			AABB range = new AABB(ent.x() - 100, ent.y() - 100, ent.z() - 100, ent.x() + 100, ent.y() + 100, ent.z() + 100);

			// One pass over the ~2197 entity sections instead of two identical ones.
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
		List<LivingEntity> list = ent.getLevel().getEntitiesOfClass(LivingEntity.class, range);
		List<FallingBlockEntity> blocks = ent.getLevel().getEntitiesOfClass(FallingBlockEntity.class, range);

		for(FallingBlockEntity block : blocks) {
			block.discard();
		}

		for(LivingEntity living : list) {
			double x = living.getX() - ent.x();
			double y = living.getEyeY() - ent.y();
			double z = living.getZ() - ent.z();
			Vec3 vec = new Vec3(x, y, z).normalize().scale(4);
			living.setDeltaMovement(vec);
		}
	}

	@Override
	public void spawnParticles(IExplosiveEntity ent) {
		if(ent.getTNTFuse() < 350) {
			int amount = 150;
			double phi = Math.PI * (3D - Math.sqrt(5D));
			for(int i = 0; i < amount; i++) {
				double y = 1D - ((double)i / ((double)amount - 1D)) * 2D;
				double radius = Math.sqrt(1D - y * y);

				double theta = phi * i;

				double x = Math.cos(theta) * radius;
				double z = Math.sin(theta) * radius;

				ent.getLevel().addParticle(new DustParticleOptions(0x000000, 0.75f), ent.x() + x * 2, ent.y() + 0.5D + y * 2, ent.z() + z * 2, 0, 0, 0);
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

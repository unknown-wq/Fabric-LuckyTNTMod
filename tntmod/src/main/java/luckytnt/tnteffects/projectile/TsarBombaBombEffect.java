package luckytnt.tnteffects.projectile;

import java.util.List;
import java.util.function.Predicate;

import org.joml.Vector3f;

import luckytnt.LuckyTNTMod;
import luckytnt.network.HydrogenBombS2CPacket;
import luckytnt.registry.BlockRegistry;
import luckytnt.registry.EffectRegistry;
import luckytnt.util.NuclearBombLike;
import luckytntlib.util.IExplosiveEntity;
import luckytntlib.util.explosions.ImprovedExplosion;
import luckytntlib.util.tnteffects.PrimedTNTEffect;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.tags.BlockTags;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.AABB;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.level.levelgen.Heightmap;

public class TsarBombaBombEffect extends PrimedTNTEffect implements NuclearBombLike {

	@Override
	public void serverExplosion(IExplosiveEntity entity) {
		if(entity.getLevel() instanceof ServerLevel sworld) {
			for(ServerLevel sw : sworld.getServer().getAllLevels()) {
				for(ServerPlayer player : sw.players()) {
					if(player.level().dimension() == sworld.dimension() && player.distanceTo((Entity)entity) <= 150) {
						LuckyTNTMod.RH.sendS2CPacket(player, new HydrogenBombS2CPacket(((Entity)entity).getId()));
					}
				}
			}
		}
		
		ImprovedExplosion explosion = new ImprovedExplosion(entity.getLevel(), (Entity)entity, entity.getPos(), 160);
		explosion.doEntityExplosion(15f, true);
		explosion.doBlockExplosion(1f, 1f, 0.167f, 0.05f, false, true);
		
		List<LivingEntity> list = entity.getLevel().getEntitiesOfClass(LivingEntity.class, new AABB(entity.x() - 90, entity.y() - 65, entity.z() - 90, entity.x() + 90, entity.y() + 65, entity.z() + 90));
		for(LivingEntity living : list) {
			living.addEffect(new MobEffectInstance(BuiltInRegistries.MOB_EFFECT.getOrThrow(EffectRegistry.CONTAMINATED), 3600, 0, true, true, true));
		}
		
		// The single r=300 ball that used to do both jobs at once has been split, see the two methods below.
		if(entity.getLevel() instanceof ServerLevel level) {
			int baseX = Mth.floor(entity.x());
			int baseY = Mth.floor(entity.y());
			int baseZ = Mth.floor(entity.z());
			spreadNuclearWaste(level, baseX, baseY, baseZ);
			clearLeaves(level, baseX, baseY, baseZ);
		}
	}

	/**
	 * Covers the surface within r=150 of the blast with nuclear waste.
	 * <p>The waste branch of the old r=300 ball was gated on {@code d2 <= 22500} and on the block below
	 * having a sturdy upwards face, so it only ever wrote to the block sitting on top of a solid surface.
	 * Walking the r=150 disc instead of the r=150 ball turns 4/3*pi*150^3 = 14.1M cells into
	 * <b>70 686 columns</b>, of which the unchanged 20% roll keeps ~14 100 - a ~200x cut - and places the
	 * waste on exactly the same "block above a sturdy face" positions.
	 * <p>Waste is no longer placed on buried ledges (cave floors, overhangs); only the topmost placement of
	 * each column, which is the only one that was ever visible, survives.
	 */
	private static void spreadNuclearWaste(ServerLevel level, int baseX, int baseY, int baseZ) {
		final RandomSource random = level.getRandom();
		final BlockState nuclearWaste = BlockRegistry.NUCLEAR_WASTE.get().defaultBlockState();
		final BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
		final BlockPos.MutableBlockPos below = new BlockPos.MutableBlockPos();
		for(int offX = -150; offX <= 150; offX++) {
			final int xSqr = offX * offX;
			for(int offZ = -150; offZ <= 150; offZ++) {
				final int xzSqr = xSqr + offZ * offZ;
				if(xzSqr > 22500) {
					continue;
				}
				//the 20% roll is the cheapest of all the tests, so it runs before any chunk is touched
				if(random.nextDouble() >= 0.2D) {
					continue;
				}
				final int x = baseX + offX;
				final int z = baseZ + offZ;
				pos.set(x, baseY, z);
				if(!level.isLoaded(pos)) {
					continue;
				}
				//OCEAN_FLOOR is the topmost block that blocks motion and is not a fluid, so the block right
				//above it is the position the old traversal was looking for
				final int y = level.getHeight(Heightmap.Types.OCEAN_FLOOR, x, z);
				final int offY = y - baseY;
				//the old loop clipped the ball to |offY| <= 100 on top of the d2 <= 22500 radius test
				if(offY > 100 || offY < -100 || xzSqr + offY * offY > 22500) {
					continue;
				}
				pos.set(x, y, z);
				final BlockState state = level.getBlockState(pos);
				if(state.getBlock().getExplosionResistance() > 200 || !(state.isAir() || state.getDestroySpeed(level, pos) <= 0.2f)) {
					continue;
				}
				below.set(x, y - 1, z);
				if(level.getBlockState(below).isFaceSturdy(level, below, Direction.UP)) {
					//the support below has just been verified and nothing sits on the replaced block, so the
					//neighbour cascade of flag 3 has nothing to tell anybody
					level.setBlock(pos.immutable(), nuclearWaste, Block.UPDATE_CLIENTS);
				}
			}
		}
	}

	/**
	 * Burns away every leaf block in the r=300, |y| &lt;= 100 blast region.
	 * <p>This is the only reason the old traversal reached out to r=300 at all, and it made it read every
	 * one of the 54.7M cells of that region through {@code Level.getBlockState} - which resolves its chunk
	 * with load-or-generate, so the 600x600 footprint pulled ~1521 chunks to ChunkStatus.FULL synchronously.
	 * <p>Leaves are extremely clustered, so the search runs per chunk section instead: chunks that are not
	 * already loaded are skipped (nothing is ever generated), and a section that contains no leaves at all is
	 * rejected by a single {@code LevelChunkSection.maybeHas} palette scan instead of 4096 block reads. Of the
	 * 1521 x 24 = ~36 500 sections of the region only the handful that actually hold leaves - in a forest
	 * typically one or two per chunk near the surface - are walked, and those are walked through the section's
	 * own storage, without a chunk lookup per block. <b>54.7M level block reads -&gt; ~36 500 palette probes
	 * plus a few 100k section local reads.</b>
	 */
	private static void clearLeaves(ServerLevel level, int baseX, int baseY, int baseZ) {
		final Predicate<BlockState> isLeaves = state -> state.is(BlockTags.LEAVES);
		final BlockState air = Blocks.AIR.defaultBlockState();
		final BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
		final int minY = Math.max(level.getMinY(), baseY - 100);
		final int maxY = Math.min(level.getMaxY(), baseY + 100);
		final int minSectionY = level.getMinSectionY();
		final int minChunkX = (baseX - 300) >> 4;
		final int maxChunkX = (baseX + 300) >> 4;
		final int minChunkZ = (baseZ - 300) >> 4;
		final int maxChunkZ = (baseZ + 300) >> 4;
		for(int chunkX = minChunkX; chunkX <= maxChunkX; chunkX++) {
			final int chunkMinX = chunkX << 4;
			//distance from the blast to the nearest x of this chunk, so corner chunks are rejected wholesale
			final int nearX = Math.max(0, Math.max(chunkMinX - baseX, baseX - (chunkMinX + 15)));
			final int remainingX = 90000 - nearX * nearX;
			if(remainingX < 0) {
				continue;
			}
			for(int chunkZ = minChunkZ; chunkZ <= maxChunkZ; chunkZ++) {
				final int chunkMinZ = chunkZ << 4;
				final int nearZ = Math.max(0, Math.max(chunkMinZ - baseZ, baseZ - (chunkMinZ + 15)));
				if(nearZ * nearZ > remainingX) {
					continue;
				}
				pos.set(chunkMinX, baseY, chunkMinZ);
				if(!level.isLoaded(pos)) {
					continue;
				}
				final LevelChunk chunk = level.getChunk(chunkX, chunkZ);
				final LevelChunkSection[] sections = chunk.getSections();
				for(int index = 0; index < sections.length; index++) {
					final int sectionMinY = (minSectionY + index) << 4;
					if(sectionMinY > maxY || sectionMinY + 15 < minY) {
						continue;
					}
					final LevelChunkSection section = sections[index];
					if(section.hasOnlyAir() || !section.maybeHas(isLeaves)) {
						continue;
					}
					for(int localY = 0; localY < 16; localY++) {
						final int y = sectionMinY + localY;
						if(y < minY || y > maxY) {
							continue;
						}
						final int offY = y - baseY;
						final int remainingY = 90000 - offY * offY;
						for(int localX = 0; localX < 16; localX++) {
							final int x = chunkMinX + localX;
							final int offX = x - baseX;
							final int remaining = remainingY - offX * offX;
							if(remaining < 0) {
								continue;
							}
							for(int localZ = 0; localZ < 16; localZ++) {
								final int offZ = chunkMinZ + localZ - baseZ;
								if(offZ * offZ > remaining) {
									continue;
								}
								final BlockState state = section.getBlockState(localX, localY, localZ);
								if(state.is(BlockTags.LEAVES) && state.getBlock().getExplosionResistance() <= 200) {
									pos.set(x, y, chunkMinZ + localZ);
									//a bulk leaf clear: the shape updates that flag 2 still performs are what
									//pops vines and similar attachments, the neighbour cascade is not needed
									level.setBlock(pos.immutable(), air, Block.UPDATE_CLIENTS);
								}
							}
						}
					}
				}
			}
		}
	}

	@Override
	public void displayMushroomCloud(IExplosiveEntity ent) {
		for(int count = 0; count < 1500; count++) {
			ent.getLevel().addParticle(new DustParticleOptions(((int)(1f*255)<<16)|((int)(2f*255)<<8)|(int)(0f*255), 10f), ent.x() + Math.random() * 60 - Math.random() * 60, ent.y() + Math.random() * 3 - Math.random() * 3, ent.z() + Math.random() * 60 - Math.random() * 60, 0, 0, 0);
		}
		for(int count = 0; count < 1000; count++) {
			ent.getLevel().addParticle(new DustParticleOptions(((int)(1f*255)<<16)|((int)(2f*255)<<8)|(int)(0f*255), 10f), ent.x() + Math.random() * 20 - Math.random() * 20, ent.y() + 3 + Math.random() * 3 - Math.random() * 3, ent.z() + Math.random() * 20 - Math.random() * 20, 0, 0, 0);
		}
		for(int count = 0; count < 800; count++) {
			ent.getLevel().addParticle(new DustParticleOptions(((int)(1f*255)<<16)|((int)(2f*255)<<8)|(int)(0f*255), 10f), ent.x() + Math.random() * 10 - Math.random() * 10, ent.y() + Math.random() * 3 - Math.random() * 3, ent.z() + Math.random() * 10 - Math.random() * 10, 0, 0, 0);
		}
		for(int count = 0; count < 600; count++) {
			ent.getLevel().addParticle(new DustParticleOptions(((int)(1f*255)<<16)|((int)(2f*255)<<8)|(int)(0f*255), 10f), ent.x() + Math.random() * 6 - Math.random() * 6, ent.y() + 4 + Math.random() * 3 - Math.random() * 3, ent.z() + Math.random() * 6 - Math.random() * 6, 0, 0, 0);
		}
		for(int count = 0; count < 600; count++) {
			ent.getLevel().addParticle(new DustParticleOptions(((int)(1f*255)<<16)|((int)(2f*255)<<8)|(int)(0f*255), 10f), ent.x() + Math.random() * 2 - Math.random() * 2, ent.y() + 15 + Math.random() * 12 - Math.random() * 12, ent.z() + Math.random() * 2 - Math.random() * 2, 0, 0, 0);
		}
		for(int count = 0; count < 600; count++) {
			ent.getLevel().addParticle(new DustParticleOptions(((int)(1f*255)<<16)|((int)(2f*255)<<8)|(int)(0f*255), 10f), ent.x() + Math.random() * 6 - Math.random() * 6, ent.y() + 22 + Math.random() * 3 - Math.random() * 3, ent.z() + Math.random() * 6 - Math.random() * 6, 0, 0, 0);
		}
		for(int count = 0; count < 600; count++) {
			ent.getLevel().addParticle(new DustParticleOptions(((int)(1f*255)<<16)|((int)(2f*255)<<8)|(int)(0f*255), 10f), ent.x() + Math.random() * 6 - Math.random() * 6, ent.y() + 29 + Math.random() * 3 - Math.random() * 3, ent.z() + Math.random() * 6 - Math.random() * 6, 0, 0, 0);
		}
		for(int count = 0; count < 2000; count++) {
			ent.getLevel().addParticle(new DustParticleOptions(((int)(1f*255)<<16)|((int)(2f*255)<<8)|(int)(0f*255), 10f), ent.x() + Math.random() * 12 - Math.random() * 12, ent.y() + 24 + Math.random() * 6 - Math.random() * 6, ent.z() + Math.random() * 12 - Math.random() * 12, 0, 0, 0);
		}
		for(int count = 0; count < 2000; count++) {
			ent.getLevel().addParticle(ParticleTypes.LARGE_SMOKE, ent.x() + Math.random() * 2 - Math.random() * 2, ent.y() + 22 + Math.random() * 2 - Math.random() * 2, ent.z() + Math.random() * 2 - Math.random() * 2, Math.random() * 2 - Math.random() * 2, Math.random() * 2 - Math.random() * 2, Math.random() * 2 - Math.random() * 2);
		}
	}
}

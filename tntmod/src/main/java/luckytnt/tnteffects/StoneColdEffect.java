package luckytnt.tnteffects;

import java.util.List;
import java.util.function.Predicate;

import org.joml.Vector3f;

import luckytnt.registry.BlockRegistry;
import luckytntlib.util.IExplosiveEntity;
import luckytntlib.util.explosions.ExplosionHelper;
import luckytntlib.util.explosions.IForEachBlockExplosionEffect;
import luckytntlib.util.explosions.ImprovedExplosion;
import luckytntlib.util.tnteffects.PrimedTNTEffect;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.AABB;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.LevelChunkSection;

public class StoneColdEffect extends PrimedTNTEffect {

	private static final Predicate<BlockState> IS_WATER = state -> state.getBlock() == Blocks.WATER;

	@Override
	public void explosionTick(IExplosiveEntity ent) {
		// TODO(port-26.2): DISABLED — advance-time-of-day. 26.2 rewrote the time system
		// (ServerClockManager/WorldClock); ServerLevel.setTimeOfDay/getTimeOfDay are gone with no
		// clean drop-in. The rest of the freezing effect is preserved.
		/*
		if(ent.getLevel() instanceof ServerLevel s_Level) {
			s_Level.setTimeOfDay(s_Level.getTimeOfDay() + 200);
		}
		*/
		if(ent.getLevel() instanceof ServerLevel sLevel) {
			for(int count = 0; count < 7; count++) {
				double offX = Math.random() * 15 - Math.random() * 15;
				double offY = Math.random() * 15 - Math.random() * 15;
				double offZ = Math.random() * 15 - Math.random() * 15;
				BlockPos pos = new BlockPos(Mth.floor(ent.x() + offX), Mth.floor(ent.y() + offY), Mth.floor(ent.z() + offZ));
				BlockState state = sLevel.getBlockState(pos);
				if(state.getBlock().getExplosionResistance() < 100 && state.isCollisionShapeFullBlock(sLevel, pos) && !state.isAir()) {
					state.getBlock().wasExploded(sLevel, pos, ImprovedExplosion.dummyExplosion(sLevel));
					sLevel.setBlock(pos, Blocks.BLUE_ICE.defaultBlockState(), 3);
				}
			}
		}
		ent.getLevel().playSound(null, ent.x(), ent.y(), ent.z(), SoundEvents.STONE_PLACE, SoundSource.BLOCKS, 0.5f, 1);
	}
	
	@Override
	public void serverExplosion(IExplosiveEntity ent) {
		Level level = ent.getLevel();
		if(!(level instanceof ServerLevel sLevel)) {
			return;
		}
		// The old r=90 "turn solid blocks into blue ice" sweep is fully contained in the r=130
		// "turn water into ice" sweep, so both used to run in a single r=130 traversal - but that meant
		// paying 4/3*pi*130^3 = 9.2M getBlockState when only the inner r=90 ball (3.05M) can ever be
		// blue-iced and the 90 < d <= 130 shell (6.1M) exists purely to find water.
		// Both cell operations only look at the position's own state, so they are order independent and
		// the sweep can be split: the r=90 ball keeps both branches, and the shell is walked chunk
		// section by chunk section and skipped whole whenever the section's palette proves it holds no
		// water. 9.2M reads -> 3.05M plus the handful of sections that actually contain water.
		final ImprovedExplosion dummy = ImprovedExplosion.dummyExplosion(level);
		final BlockState blueIce = Blocks.BLUE_ICE.defaultBlockState();
		final BlockState ice = Blocks.ICE.defaultBlockState();
		final int centerX = Mth.floor(ent.x());
		final int centerY = Mth.floor(ent.y());
		final int centerZ = Mth.floor(ent.z());
		BlockPos.MutableBlockPos mutable = new BlockPos.MutableBlockPos();
		int cachedChunkX = Integer.MIN_VALUE;
		int cachedChunkZ = Integer.MIN_VALUE;
		boolean chunkLoaded = false;

		for(int offX = -90; offX <= 90; offX++) {
			int xSq = offX * offX;
			int posX = centerX + offX;
			for(int offY = 90; offY >= -90; offY--) {
				int xySq = xSq + offY * offY;
				if(xySq > 8100) {
					continue;
				}
				int posY = centerY + offY;
				int zMax = floorSqrt(8100 - xySq);
				for(int offZ = -zMax; offZ <= zMax; offZ++) {
					int posZ = centerZ + offZ;
					//the loaded check only has to be redone when the column crosses into another chunk
					int chunkX = posX >> 4;
					int chunkZ = posZ >> 4;
					if(chunkX != cachedChunkX || chunkZ != cachedChunkZ) {
						cachedChunkX = chunkX;
						cachedChunkZ = chunkZ;
						chunkLoaded = level.hasChunk(chunkX, chunkZ);
					}
					if(!chunkLoaded) {
						continue;
					}
					mutable.set(posX, posY, posZ);
					BlockState state = level.getBlockState(mutable);
					Block block = state.getBlock();
					if(block.getExplosionResistance() < 200 && state != blueIce && Block.isFaceFull(state.getCollisionShape(level, mutable), Direction.UP)) {
						BlockPos pos = mutable.immutable();
						block.wasExploded(sLevel, pos, dummy);
						level.setBlock(pos, blueIce, 3);
						continue;
					}
					if(block == Blocks.WATER && state != ice) {
						BlockPos pos = mutable.immutable();
						block.wasExploded(sLevel, pos, dummy);
						level.setBlock(pos, ice, 3);
					}
				}
			}
		}

		freezeWaterShell(level, sLevel, centerX, centerY, centerZ, dummy, ice);

		final BlockState snow = Blocks.SNOW.defaultBlockState();
		ExplosionHelper.doTopBlockExplosionForAll(level, ent.getPos(), 130, new IForEachBlockExplosionEffect() {

			@Override
			public void doBlockExplosion(Level level, BlockPos pos, BlockState state, double distance) {
				Block block = state.getBlock();
				if(block.getExplosionResistance() < 100) {
					block.wasExploded(sLevel, pos, dummy);
					level.setBlock(pos, snow, 3);
				}
			}
		});

		List<LivingEntity> entities = level.getEntitiesOfClass(LivingEntity.class, new AABB(ent.x() - 90, ent.y() - 90, ent.z() - 90, ent.x() + 90, ent.y() + 90, ent.z() + 90));
		for(LivingEntity lEnt : entities) {
			lEnt.addEffect(new MobEffectInstance(MobEffects.SLOWNESS, 800, 2));
		}
	}

	/**
	 * Turns the water in the 90 &lt; d &lt;= 130 shell into ice. Only water can be affected out there, so
	 * the shell is walked chunk section by chunk section and a section whose palette contains no water is
	 * rejected with a single palette scan instead of up to 4096 {@code getBlockState} calls.
	 * That takes the shell from 6.1M reads down to the sections that actually hold water (usually none
	 * at all on land, a few dozen in an ocean).
	 */
	private static void freezeWaterShell(Level level, ServerLevel sLevel, int centerX, int centerY, int centerZ, ImprovedExplosion dummy, BlockState ice) {
		final int outerSq = 130 * 130;
		final int innerSq = 90 * 90;
		final int minY = Math.max(centerY - 130, level.getMinY());
		final int maxY = Math.min(centerY + 130, level.getMaxY());
		BlockPos.MutableBlockPos mutable = new BlockPos.MutableBlockPos();

		for(int chunkX = (centerX - 130) >> 4; chunkX <= (centerX + 130) >> 4; chunkX++) {
			for(int chunkZ = (centerZ - 130) >> 4; chunkZ <= (centerZ + 130) >> 4; chunkZ++) {
				if(!level.hasChunk(chunkX, chunkZ)) {
					continue;
				}
				ChunkAccess chunk = level.getChunk(chunkX, chunkZ);
				LevelChunkSection[] sections = chunk.getSections();
				int fromX = Math.max(chunkX << 4, centerX - 130);
				int toX = Math.min((chunkX << 4) + 15, centerX + 130);
				int fromZ = Math.max(chunkZ << 4, centerZ - 130);
				int toZ = Math.min((chunkZ << 4) + 15, centerZ + 130);
				for(int index = 0; index < sections.length; index++) {
					LevelChunkSection section = sections[index];
					//hasFluid() is a counter, maybeHas() a palette scan; both reject a whole 4096 block section
					if(!section.hasFluid() || !section.maybeHas(IS_WATER)) {
						continue;
					}
					int sectionBottom = level.getSectionYFromSectionIndex(index) << 4;
					int fromY = Math.max(sectionBottom, minY);
					int toY = Math.min(sectionBottom + 15, maxY);
					for(int posX = fromX; posX <= toX; posX++) {
						int dx = posX - centerX;
						int xSq = dx * dx;
						for(int posZ = fromZ; posZ <= toZ; posZ++) {
							int dz = posZ - centerZ;
							int xzSq = xSq + dz * dz;
							if(xzSq > outerSq) {
								continue;
							}
							for(int posY = fromY; posY <= toY; posY++) {
								int dy = posY - centerY;
								int distanceSq = xzSq + dy * dy;
								if(distanceSq > outerSq || distanceSq <= innerSq) {
									continue;
								}
								BlockState state = section.getBlockState(posX & 15, posY & 15, posZ & 15);
								if(state.getBlock() == Blocks.WATER && state != ice) {
									BlockPos pos = mutable.set(posX, posY, posZ).immutable();
									state.getBlock().wasExploded(sLevel, pos, dummy);
									level.setBlock(pos, ice, 3);
								}
							}
						}
					}
				}
			}
		}
	}

	/**
	 * @param value  a value greater than or equal to 0
	 * @return the greatest int whose square is less than or equal to the given value
	 */
	private static int floorSqrt(int value) {
		int root = (int)Math.sqrt(value);
		while(root > 0 && root * root > value) {
			root--;
		}
		while((root + 1) * (root + 1) <= value) {
			root++;
		}
		return root;
	}

	@Override
	public void spawnParticles(IExplosiveEntity ent) {
		ent.getLevel().addParticle(new DustParticleOptions(((int)(0.2f*255)<<16)|((int)(0.9f*255)<<8)|(int)(1f*255), 1f), ent.x(), ent.y() + 1f, ent.z(), 0, 0, 0);
	}
	
	@Override
	public Block getBlock() {
		return BlockRegistry.STONE_COLD.get();
	}
	
	@Override
	public int getDefaultFuse(IExplosiveEntity ent) {
		return 140;
	}
}

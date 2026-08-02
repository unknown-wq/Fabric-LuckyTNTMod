package luckytnt.tnteffects;
import net.minecraft.server.level.ServerLevel;

import org.joml.Vector3f;

import luckytnt.config.LuckyTNTConfigValues;
import luckytnt.registry.BlockRegistry;
import luckytnt.registry.EntityRegistry;
import luckytnt.util.CustomTNTConfig;
import luckytntlib.entity.PrimedLTNT;
import luckytntlib.util.IExplosiveEntity;
import luckytntlib.util.explosions.ExplosionHelper;
import luckytntlib.util.explosions.IForEachBlockExplosionEffect;
import luckytntlib.util.explosions.ImprovedExplosion;
import luckytntlib.util.tnteffects.PrimedTNTEffect;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.entity.Entity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.entity.EntitySpawnReason;

public class CustomTNTEffect extends PrimedTNTEffect {

	/**
	 * Hard cap on the radius handed to the volumetric ExplosionHelper sweeps. The configured intensity
	 * bound (20) is untouched - what changes is that "5 * intensity" is no longer passed to a triple
	 * loop unclamped. At radius 100 doCubicalExplosion visits 201^3 = 8 120 601 cells and
	 * doSphericalExplosion 4/3*pi*100^3 = 4 188 790, and a Custom TNT chains up to 1 + 3 + 9 = 13
	 * detonations, i.e. ~105 M block reads with no culling. Clamped to 50 that is 101^3 = 1 030 301 /
	 * 523 599 per detonation (a 7.9x cut, ~13.4 M chained) - the same radius CubicTNTEffect already
	 * uses for Chunk TNT, so it stays inside what the mod treats as an acceptable single tick sweep.
	 * Intensities up to 10 are unaffected; the default is 1.
	 */
	private static final int MAX_VOLUMETRIC_RADIUS = 50;

	private static int sweepRadius(int intensity) {
		return Math.min(5 * intensity, MAX_VOLUMETRIC_RADIUS);
	}

	/**
	 * The annihilation callback was written out six times, each copy re-resolving
	 * ImprovedExplosion.dummyExplosion(level) and Blocks.AIR.defaultBlockState() per visited block.
	 * Both are loop invariant, and the write is a bulk sweep whose neighbour updates are spent almost
	 * entirely on blocks the same pass removes, so it uses UPDATE_CLIENTS like EndTNTEffect:65 and
	 * GlobalDisasterEffect:47 already do.
	 */
	private static IForEachBlockExplosionEffect annihilate(Level level) {
		final BlockState air = Blocks.AIR.defaultBlockState();
		final ImprovedExplosion dummy = ImprovedExplosion.dummyExplosion(level);
		return (lvl, pos, state, distance) -> {
			if(!state.isAir() && state.getBlock().getExplosionResistance() <= 200) {
				state.getBlock().wasExploded((ServerLevel) lvl, pos, dummy);
				lvl.setBlock(pos, air, Block.UPDATE_CLIENTS);
			}
		};
	}

	@Override
	public void explosionTick(IExplosiveEntity ent) {
		if(!ent.getLevel().isClientSide()) {
			CustomTNTConfig config = LuckyTNTConfigValues.CUSTOM_TNT_FIRST_EXPLOSION.get();
			if(ent.getPersistentData().getIntOr("level", 0) == 0) {
				if(config == CustomTNTConfig.FIREWORK) {
					((Entity)ent).setDeltaMovement(((Entity)ent).getDeltaMovement().x, 0.8f, ((Entity)ent).getDeltaMovement().z);
					if(ent.getTNTFuse() > 40) {
						CompoundTag tag = ent.getPersistentData();
						tag.putInt("fuse", 40);
						ent.setPersistentData(tag);
					}
				}
			}
			if(ent.getPersistentData().getIntOr("level", 0) == 1) {
				config = LuckyTNTConfigValues.CUSTOM_TNT_SECOND_EXPLOSION.get();
				if(config == CustomTNTConfig.FIREWORK) {
					((Entity)ent).setDeltaMovement(((Entity)ent).getDeltaMovement().x, 0.8f, ((Entity)ent).getDeltaMovement().z);
					if(ent.getTNTFuse() > 40) {
						CompoundTag tag = ent.getPersistentData();
						tag.putInt("fuse", 40);
						ent.setPersistentData(tag);
					}
				}
			}
			if(ent.getPersistentData().getIntOr("level", 0) == 2) {
				config = LuckyTNTConfigValues.CUSTOM_TNT_THIRD_EXPLOSION.get();
				if(config == CustomTNTConfig.FIREWORK) {
					((Entity)ent).setDeltaMovement(((Entity)ent).getDeltaMovement().x, 0.8f, ((Entity)ent).getDeltaMovement().z);
					if(ent.getTNTFuse() > 40) {
						CompoundTag tag = ent.getPersistentData();
						tag.putInt("fuse", 40);
						ent.setPersistentData(tag);
					}
				}
			}
		}
	}
	
	@Override
	public void serverExplosion(IExplosiveEntity ent) {
		CustomTNTConfig config = LuckyTNTConfigValues.CUSTOM_TNT_FIRST_EXPLOSION.get();
		if(ent.getPersistentData().getIntOr("level", 0) == 0) {
			if(config == CustomTNTConfig.NORMAL_EXPLOSION) {
				ImprovedExplosion explosion = new ImprovedExplosion(ent.getLevel(), (Entity)ent, ent.getPos(), LuckyTNTConfigValues.CUSTOM_TNT_FIRST_EXPLOSION_INTENSITY.get().intValue());
				explosion.doEntityExplosion(3f, true);
				explosion.doBlockExplosion(1f, 1.3f, 1f, 1.2f, LuckyTNTConfigValues.CUSTOM_TNT_FIRST_EXPLOSION_INTENSITY.get().intValue() > 10 ? true : false, false);
				
				if(LuckyTNTConfigValues.CUSTOM_TNT_SECOND_EXPLOSION.get() != CustomTNTConfig.NO_EXPLOSION) {
					for(int count = 0; count < 3; count++) {
						PrimedLTNT custom = EntityRegistry.CUSTOM_TNT.get().create(ent.getLevel(), EntitySpawnReason.MOB_SUMMONED);
						custom.setPos(ent.getPos());
						custom.setOwner(ent.owner());
						custom.setDeltaMovement(Math.random() * 2f - 1f, Math.random() * 2f, Math.random() * 2f - 1f);
						CompoundTag tag = custom.getPersistentData();
						tag.putInt("level", ent.getPersistentData().getIntOr("level", 0) + 1);
						custom.setPersistentData(tag);
						ent.getLevel().addFreshEntity(custom);
					}
				}
			}
			if(config == CustomTNTConfig.SPHERICAL_EXPLOSION) {
				ExplosionHelper.doSphericalExplosion(ent.getLevel(), ent.getPos(), sweepRadius(LuckyTNTConfigValues.CUSTOM_TNT_FIRST_EXPLOSION_INTENSITY.get().intValue()), annihilate(ent.getLevel()));

				if(LuckyTNTConfigValues.CUSTOM_TNT_SECOND_EXPLOSION.get() != CustomTNTConfig.NO_EXPLOSION) {
					for(int count = 0; count < 3; count++) {
						PrimedLTNT custom = EntityRegistry.CUSTOM_TNT.get().create(ent.getLevel(), EntitySpawnReason.MOB_SUMMONED);
						custom.setPos(ent.getPos());
						custom.setOwner(ent.owner());
						custom.setDeltaMovement(Math.random() * 2f - 1f, Math.random() * 2f, Math.random() * 2f - 1f);
						CompoundTag tag = custom.getPersistentData();
						tag.putInt("level", ent.getPersistentData().getIntOr("level", 0) + 1);
						custom.setPersistentData(tag);
						ent.getLevel().addFreshEntity(custom);
					}
				}
			}
			if(config == CustomTNTConfig.CUBICAL_EXPLOSION) {
				ExplosionHelper.doCubicalExplosion(ent.getLevel(), ent.getPos(), sweepRadius(LuckyTNTConfigValues.CUSTOM_TNT_FIRST_EXPLOSION_INTENSITY.get().intValue()), annihilate(ent.getLevel()));

				if(LuckyTNTConfigValues.CUSTOM_TNT_SECOND_EXPLOSION.get() != CustomTNTConfig.NO_EXPLOSION) {
					for(int count = 0; count < 3; count++) {
						PrimedLTNT custom = EntityRegistry.CUSTOM_TNT.get().create(ent.getLevel(), EntitySpawnReason.MOB_SUMMONED);
						custom.setPos(ent.getPos());
						custom.setOwner(ent.owner());
						custom.setDeltaMovement(Math.random() * 2f - 1f, Math.random() * 2f, Math.random() * 2f - 1f);
						CompoundTag tag = custom.getPersistentData();
						tag.putInt("level", ent.getPersistentData().getIntOr("level", 0) + 1);
						custom.setPersistentData(tag);
						ent.getLevel().addFreshEntity(custom);
					}
				}
			}
			if(config == CustomTNTConfig.EASTER_EGG) {
				ImprovedExplosion explosion = new ImprovedExplosion(ent.getLevel(), (Entity)ent, ent.getPos(), 3 * LuckyTNTConfigValues.CUSTOM_TNT_FIRST_EXPLOSION_INTENSITY.get().intValue());
				explosion.doBlockExplosion(1f, 1f, 1f, 3 * LuckyTNTConfigValues.CUSTOM_TNT_FIRST_EXPLOSION_INTENSITY.get().floatValue() > 30f ? 1.75f : 1.5f, false, false);
				explosion.doBlockExplosion(new IForEachBlockExplosionEffect() {		
					@Override
					public void doBlockExplosion(Level level, BlockPos pos, BlockState state, double distance) {
						if(Math.random() < 0.66f && !state.isAir()) {
							state.getBlock().wasExploded((ServerLevel) level, pos, explosion);
							level.setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
							if(Math.random() < 0.5f) {
								ent.getLevel().setBlockAndUpdate(pos, Blocks.MELON.defaultBlockState());
							}
							else {
								ent.getLevel().setBlockAndUpdate(pos, Blocks.PUMPKIN.defaultBlockState());
							}
						}
					}
				});
				
				if(LuckyTNTConfigValues.CUSTOM_TNT_SECOND_EXPLOSION.get() != CustomTNTConfig.NO_EXPLOSION) {
					for(int count = 0; count < 1 * LuckyTNTConfigValues.CUSTOM_TNT_FIRST_EXPLOSION_INTENSITY.get().intValue(); count++) {
						PrimedLTNT custom = EntityRegistry.CUSTOM_TNT.get().create(ent.getLevel(), EntitySpawnReason.MOB_SUMMONED);
						custom.setPos(ent.getPos());
						custom.setOwner(ent.owner());
						custom.setDeltaMovement(Math.random() * 2f - 1f, Math.random() * 2f, Math.random() * 2f - 1f);
						CompoundTag tag = custom.getPersistentData();
						tag.putInt("level", ent.getPersistentData().getIntOr("level", 0) + 1);
						custom.setPersistentData(tag);
						ent.getLevel().addFreshEntity(custom);
					}
				}
			}
			if(config == CustomTNTConfig.FIREWORK) {
				for(int count = 0; count < 15 * LuckyTNTConfigValues.CUSTOM_TNT_FIRST_EXPLOSION_INTENSITY.get().intValue(); count++) {
					PrimedLTNT custom = EntityRegistry.CUSTOM_TNT.get().create(ent.getLevel(), EntitySpawnReason.MOB_SUMMONED);
					custom.setPos(ent.getPos());
					custom.setOwner(ent.owner());
					custom.setDeltaMovement(Math.random() * 3f - 1.5f, Math.random() * 3f - 1.5f, Math.random() * 3f - 1.5f);
					CompoundTag tag = custom.getPersistentData();
					tag.putInt("level", ent.getPersistentData().getIntOr("level", 0) + 1);
					custom.setPersistentData(tag);
					ent.getLevel().addFreshEntity(custom);
				}
			}
		}
		if(ent.getPersistentData().getIntOr("level", 0) == 1) {
			config = LuckyTNTConfigValues.CUSTOM_TNT_SECOND_EXPLOSION.get();
			if(config == CustomTNTConfig.NORMAL_EXPLOSION) {
				ImprovedExplosion explosion = new ImprovedExplosion(ent.getLevel(), (Entity)ent, ent.getPos(), LuckyTNTConfigValues.CUSTOM_TNT_SECOND_EXPLOSION_INTENSITY.get().intValue());
				explosion.doEntityExplosion(3f, true);
				explosion.doBlockExplosion(1f, 1.3f, 1f, 1.2f, LuckyTNTConfigValues.CUSTOM_TNT_SECOND_EXPLOSION_INTENSITY.get().intValue() > 10 ? true : false, false);
				
				if(LuckyTNTConfigValues.CUSTOM_TNT_THIRD_EXPLOSION.get() != CustomTNTConfig.NO_EXPLOSION) {
					for(int count = 0; count < 3; count++) {
						PrimedLTNT custom = EntityRegistry.CUSTOM_TNT.get().create(ent.getLevel(), EntitySpawnReason.MOB_SUMMONED);
						custom.setPos(ent.getPos());
						custom.setOwner(ent.owner());
						custom.setDeltaMovement(Math.random() * 2f - 1f, Math.random() * 2f, Math.random() * 2f - 1f);
						CompoundTag tag = custom.getPersistentData();
						tag.putInt("level", ent.getPersistentData().getIntOr("level", 0) + 1);
						custom.setPersistentData(tag);
						ent.getLevel().addFreshEntity(custom);
					}
				}
			}
			if(config == CustomTNTConfig.SPHERICAL_EXPLOSION) {
				ExplosionHelper.doSphericalExplosion(ent.getLevel(), ent.getPos(), sweepRadius(LuckyTNTConfigValues.CUSTOM_TNT_SECOND_EXPLOSION_INTENSITY.get().intValue()), annihilate(ent.getLevel()));

				if(LuckyTNTConfigValues.CUSTOM_TNT_THIRD_EXPLOSION.get() != CustomTNTConfig.NO_EXPLOSION) {
					for(int count = 0; count < 3; count++) {
						PrimedLTNT custom = EntityRegistry.CUSTOM_TNT.get().create(ent.getLevel(), EntitySpawnReason.MOB_SUMMONED);
						custom.setPos(ent.getPos());
						custom.setOwner(ent.owner());
						custom.setDeltaMovement(Math.random() * 2f - 1f, Math.random() * 2f, Math.random() * 2f - 1f);
						CompoundTag tag = custom.getPersistentData();
						tag.putInt("level", ent.getPersistentData().getIntOr("level", 0) + 1);
						custom.setPersistentData(tag);
						ent.getLevel().addFreshEntity(custom);
					}
				}
			}
			if(config == CustomTNTConfig.CUBICAL_EXPLOSION) {
				ExplosionHelper.doCubicalExplosion(ent.getLevel(), ent.getPos(), sweepRadius(LuckyTNTConfigValues.CUSTOM_TNT_SECOND_EXPLOSION_INTENSITY.get().intValue()), annihilate(ent.getLevel()));

				if(LuckyTNTConfigValues.CUSTOM_TNT_THIRD_EXPLOSION.get() != CustomTNTConfig.NO_EXPLOSION) {
					for(int count = 0; count < 3; count++) {
						PrimedLTNT custom = EntityRegistry.CUSTOM_TNT.get().create(ent.getLevel(), EntitySpawnReason.MOB_SUMMONED);
						custom.setPos(ent.getPos());
						custom.setOwner(ent.owner());
						custom.setDeltaMovement(Math.random() * 2f - 1f, Math.random() * 2f, Math.random() * 2f - 1f);
						CompoundTag tag = custom.getPersistentData();
						tag.putInt("level", ent.getPersistentData().getIntOr("level", 0) + 1);
						custom.setPersistentData(tag);
						ent.getLevel().addFreshEntity(custom);
					}
				}
			}
			if(config == CustomTNTConfig.EASTER_EGG) {
				ImprovedExplosion explosion = new ImprovedExplosion(ent.getLevel(), (Entity)ent, ent.getPos(), 3 * LuckyTNTConfigValues.CUSTOM_TNT_SECOND_EXPLOSION_INTENSITY.get().intValue());
				explosion.doBlockExplosion(1f, 1f, 1f, 3 * LuckyTNTConfigValues.CUSTOM_TNT_SECOND_EXPLOSION_INTENSITY.get().floatValue() > 30f ? 1.75f : 1.5f, false, false);
				explosion.doBlockExplosion(new IForEachBlockExplosionEffect() {		
					@Override
					public void doBlockExplosion(Level level, BlockPos pos, BlockState state, double distance) {
						if(Math.random() < 0.66f && !state.isAir()) {
							state.getBlock().wasExploded((ServerLevel) level, pos, explosion);
							level.setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
							if(Math.random() < 0.5f) {
								ent.getLevel().setBlockAndUpdate(pos, Blocks.MELON.defaultBlockState());
							}
							else {
								ent.getLevel().setBlockAndUpdate(pos, Blocks.PUMPKIN.defaultBlockState());
							}
						}
					}
				});
				
				if(LuckyTNTConfigValues.CUSTOM_TNT_THIRD_EXPLOSION.get() != CustomTNTConfig.NO_EXPLOSION) {
					for(int count = 0; count < 1 * LuckyTNTConfigValues.CUSTOM_TNT_SECOND_EXPLOSION_INTENSITY.get().intValue(); count++) {
						PrimedLTNT custom = EntityRegistry.CUSTOM_TNT.get().create(ent.getLevel(), EntitySpawnReason.MOB_SUMMONED);
						custom.setPos(ent.getPos());
						custom.setOwner(ent.owner());
						custom.setDeltaMovement(Math.random() * 2f - 1f, Math.random() * 2f, Math.random() * 2f - 1f);
						CompoundTag tag = custom.getPersistentData();
						tag.putInt("level", ent.getPersistentData().getIntOr("level", 0) + 1);
						custom.setPersistentData(tag);
						ent.getLevel().addFreshEntity(custom);
					}
				}
			}
			if(config == CustomTNTConfig.FIREWORK) {
				for(int count = 0; count < 15 * LuckyTNTConfigValues.CUSTOM_TNT_SECOND_EXPLOSION_INTENSITY.get().intValue(); count++) {
					PrimedLTNT custom = EntityRegistry.CUSTOM_TNT.get().create(ent.getLevel(), EntitySpawnReason.MOB_SUMMONED);
					custom.setPos(ent.getPos());
					custom.setOwner(ent.owner());
					custom.setDeltaMovement(Math.random() * 3f - 1.5f, Math.random() * 3f - 1.5f, Math.random() * 3f - 1.5f);
					CompoundTag tag = custom.getPersistentData();
					tag.putInt("level", ent.getPersistentData().getIntOr("level", 0) + 1);
					custom.setPersistentData(tag);
					ent.getLevel().addFreshEntity(custom);
				}
			}
		}
		if(ent.getPersistentData().getIntOr("level", 0) == 2) {
			config = LuckyTNTConfigValues.CUSTOM_TNT_THIRD_EXPLOSION.get();
			if(config == CustomTNTConfig.NORMAL_EXPLOSION) {
				ImprovedExplosion explosion = new ImprovedExplosion(ent.getLevel(), (Entity)ent, ent.getPos(), LuckyTNTConfigValues.CUSTOM_TNT_THIRD_EXPLOSION_INTENSITY.get().intValue());
				explosion.doEntityExplosion(3f, true);
				explosion.doBlockExplosion(1f, 1.3f, 1f, 1.2f, LuckyTNTConfigValues.CUSTOM_TNT_THIRD_EXPLOSION_INTENSITY.get().intValue() > 10 ? true : false, false);
			}
			if(config == CustomTNTConfig.SPHERICAL_EXPLOSION) {
				ExplosionHelper.doSphericalExplosion(ent.getLevel(), ent.getPos(), sweepRadius(LuckyTNTConfigValues.CUSTOM_TNT_THIRD_EXPLOSION_INTENSITY.get().intValue()), annihilate(ent.getLevel()));
			}
			if(config == CustomTNTConfig.CUBICAL_EXPLOSION) {
				ExplosionHelper.doCubicalExplosion(ent.getLevel(), ent.getPos(), sweepRadius(LuckyTNTConfigValues.CUSTOM_TNT_THIRD_EXPLOSION_INTENSITY.get().intValue()), annihilate(ent.getLevel()));
			}
			if(config == CustomTNTConfig.EASTER_EGG) {
				ImprovedExplosion explosion = new ImprovedExplosion(ent.getLevel(), (Entity)ent, ent.getPos(), 3 * LuckyTNTConfigValues.CUSTOM_TNT_THIRD_EXPLOSION_INTENSITY.get().intValue());
				explosion.doBlockExplosion(1f, 1f, 1f, 3 * LuckyTNTConfigValues.CUSTOM_TNT_THIRD_EXPLOSION_INTENSITY.get().floatValue() > 30f ? 1.75f : 1.5f, false, false);
				explosion.doBlockExplosion(new IForEachBlockExplosionEffect() {		
					@Override
					public void doBlockExplosion(Level level, BlockPos pos, BlockState state, double distance) {
						if(Math.random() < 0.66f && !state.isAir()) {
							state.getBlock().wasExploded((ServerLevel) level, pos, explosion);
							level.setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
							if(Math.random() < 0.5f) {
								ent.getLevel().setBlockAndUpdate(pos, Blocks.MELON.defaultBlockState());
							}
							else {
								ent.getLevel().setBlockAndUpdate(pos, Blocks.PUMPKIN.defaultBlockState());
							}
						}
					}
				});
			}
			if(config == CustomTNTConfig.FIREWORK) {
				for(int count = 0; count < 15 * LuckyTNTConfigValues.CUSTOM_TNT_THIRD_EXPLOSION_INTENSITY.get().intValue(); count++) {
					PrimedLTNT custom = EntityRegistry.TNT.get().create(ent.getLevel(), EntitySpawnReason.MOB_SUMMONED);
					custom.setPos(ent.getPos());
					custom.setOwner(ent.owner());
					custom.setDeltaMovement(Math.random() * 3f - 1.5f, Math.random() * 3f - 1.5f, Math.random() * 3f - 1.5f);
					ent.getLevel().addFreshEntity(custom);
				}
			}
		}
	}
	
	@Override
	public void spawnParticles(IExplosiveEntity ent) {
		ent.getLevel().addParticle(new DustParticleOptions(((int)(1f*255)<<16)|((int)(0f*255)<<8)|(int)(0f*255), 1f), ent.x(), ent.y() + 1f, ent.z(), 0, 0, 0);
		ent.getLevel().addParticle(new DustParticleOptions(((int)(0f*255)<<16)|((int)(1f*255)<<8)|(int)(0f*255), 1f), ent.x(), ent.y() + 1f, ent.z(), 0, 0, 0);
		ent.getLevel().addParticle(new DustParticleOptions(((int)(0f*255)<<16)|((int)(0f*255)<<8)|(int)(1f*255), 1f), ent.x(), ent.y() + 1f, ent.z(), 0, 0, 0);
		if(ent.getLevel().isClientSide()) {
			CustomTNTConfig config = LuckyTNTConfigValues.CUSTOM_TNT_FIRST_EXPLOSION.get();
			if(ent.getPersistentData().getIntOr("level", 0) == 0) {
				if(config == CustomTNTConfig.FIREWORK) {
					ent.getLevel().addParticle(ParticleTypes.FLAME, ent.x(), ent.y() + 0.5f, ent.z(), 0, 0, 0);
				}
			}
			if(ent.getPersistentData().getIntOr("level", 0) == 1) {
				config = LuckyTNTConfigValues.CUSTOM_TNT_SECOND_EXPLOSION.get();
				if(config == CustomTNTConfig.FIREWORK) {
					ent.getLevel().addParticle(ParticleTypes.FLAME, ent.x(), ent.y() + 0.5f, ent.z(), 0, 0, 0);
				}
			}
			if(ent.getPersistentData().getIntOr("level", 0) == 2) {
				config = LuckyTNTConfigValues.CUSTOM_TNT_THIRD_EXPLOSION.get();
				if(config == CustomTNTConfig.FIREWORK) {
					ent.getLevel().addParticle(ParticleTypes.FLAME, ent.x(), ent.y() + 0.5f, ent.z(), 0, 0, 0);
				}
			}
		}
	}
	
	@Override
	public Block getBlock() {
		return BlockRegistry.CUSTOM_TNT.get();
	}
}

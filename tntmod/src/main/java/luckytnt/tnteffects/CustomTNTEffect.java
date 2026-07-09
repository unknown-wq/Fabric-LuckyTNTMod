package luckytnt.tnteffects;

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
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class CustomTNTEffect extends PrimedTNTEffect {

	@Override
	public void explosionTick(IExplosiveEntity ent) {
		if(!ent.getLevel().isClient()) {
			CustomTNTConfig config = LuckyTNTConfigValues.CUSTOM_TNT_FIRST_EXPLOSION.get();
			if(ent.getPersistentData().getInt("level") == 0) {
				if(config == CustomTNTConfig.FIREWORK) {
					((Entity)ent).setVelocity(((Entity)ent).getVelocity().x, 0.8f, ((Entity)ent).getVelocity().z);
					if(ent.getTNTFuse() > 40) {
						NbtCompound tag = ent.getPersistentData();
						tag.putInt("fuse", 40);
						ent.setPersistentData(tag);
					}
				}
			}
			if(ent.getPersistentData().getInt("level") == 1) {
				config = LuckyTNTConfigValues.CUSTOM_TNT_SECOND_EXPLOSION.get();
				if(config == CustomTNTConfig.FIREWORK) {
					((Entity)ent).setVelocity(((Entity)ent).getVelocity().x, 0.8f, ((Entity)ent).getVelocity().z);
					if(ent.getTNTFuse() > 40) {
						NbtCompound tag = ent.getPersistentData();
						tag.putInt("fuse", 40);
						ent.setPersistentData(tag);
					}
				}
			}
			if(ent.getPersistentData().getInt("level") == 2) {
				config = LuckyTNTConfigValues.CUSTOM_TNT_THIRD_EXPLOSION.get();
				if(config == CustomTNTConfig.FIREWORK) {
					((Entity)ent).setVelocity(((Entity)ent).getVelocity().x, 0.8f, ((Entity)ent).getVelocity().z);
					if(ent.getTNTFuse() > 40) {
						NbtCompound tag = ent.getPersistentData();
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
		if(ent.getPersistentData().getInt("level") == 0) {
			if(config == CustomTNTConfig.NORMAL_EXPLOSION) {
				ImprovedExplosion explosion = new ImprovedExplosion(ent.getLevel(), (Entity)ent, ent.getPos(), LuckyTNTConfigValues.CUSTOM_TNT_FIRST_EXPLOSION_INTENSITY.get().intValue());
				explosion.doEntityExplosion(3f, true);
				explosion.doBlockExplosion(1f, 1.3f, 1f, 1.2f, LuckyTNTConfigValues.CUSTOM_TNT_FIRST_EXPLOSION_INTENSITY.get().intValue() > 10 ? true : false, false);
				
				if(LuckyTNTConfigValues.CUSTOM_TNT_SECOND_EXPLOSION.get() != CustomTNTConfig.NO_EXPLOSION) {
					for(int count = 0; count < 3; count++) {
						PrimedLTNT custom = EntityRegistry.CUSTOM_TNT.get().create(ent.getLevel());
						custom.setPosition(ent.getPos());
						custom.setOwner(ent.owner());
						custom.setVelocity(Math.random() * 2f - 1f, Math.random() * 2f, Math.random() * 2f - 1f);
						NbtCompound tag = custom.getPersistentData();
						tag.putInt("level", ent.getPersistentData().getInt("level") + 1);
						custom.setPersistentData(tag);
						ent.getLevel().spawnEntity(custom);
					}
				}
			}
			if(config == CustomTNTConfig.SPHERICAL_EXPLOSION) {
				ExplosionHelper.doSphericalExplosion(ent.getLevel(), ent.getPos(), 5 * LuckyTNTConfigValues.CUSTOM_TNT_FIRST_EXPLOSION_INTENSITY.get().intValue(), new IForEachBlockExplosionEffect() {
					
					@Override
					public void doBlockExplosion(World level, BlockPos pos, BlockState state, double distance) {
						if(!state.isAir() && state.getBlock().getBlastResistance() <= 200) {
							state.getBlock().onDestroyedByExplosion(level, pos, ImprovedExplosion.dummyExplosion(ent.getLevel()));
							level.setBlockState(pos, Blocks.AIR.getDefaultState(), 3);
						}
					}
				});
				
				if(LuckyTNTConfigValues.CUSTOM_TNT_SECOND_EXPLOSION.get() != CustomTNTConfig.NO_EXPLOSION) {
					for(int count = 0; count < 3; count++) {
						PrimedLTNT custom = EntityRegistry.CUSTOM_TNT.get().create(ent.getLevel());
						custom.setPosition(ent.getPos());
						custom.setOwner(ent.owner());
						custom.setVelocity(Math.random() * 2f - 1f, Math.random() * 2f, Math.random() * 2f - 1f);
						NbtCompound tag = custom.getPersistentData();
						tag.putInt("level", ent.getPersistentData().getInt("level") + 1);
						custom.setPersistentData(tag);
						ent.getLevel().spawnEntity(custom);
					}
				}
			}
			if(config == CustomTNTConfig.CUBICAL_EXPLOSION) {
				ExplosionHelper.doCubicalExplosion(ent.getLevel(), ent.getPos(), 5 * LuckyTNTConfigValues.CUSTOM_TNT_FIRST_EXPLOSION_INTENSITY.get().intValue(), new IForEachBlockExplosionEffect() {
					
					@Override
					public void doBlockExplosion(World level, BlockPos pos, BlockState state, double distance) {
						if(!state.isAir() && state.getBlock().getBlastResistance() <= 200) {
							state.getBlock().onDestroyedByExplosion(level, pos, ImprovedExplosion.dummyExplosion(ent.getLevel()));
							level.setBlockState(pos, Blocks.AIR.getDefaultState(), 3);
						}
					}
				});
				
				if(LuckyTNTConfigValues.CUSTOM_TNT_SECOND_EXPLOSION.get() != CustomTNTConfig.NO_EXPLOSION) {
					for(int count = 0; count < 3; count++) {
						PrimedLTNT custom = EntityRegistry.CUSTOM_TNT.get().create(ent.getLevel());
						custom.setPosition(ent.getPos());
						custom.setOwner(ent.owner());
						custom.setVelocity(Math.random() * 2f - 1f, Math.random() * 2f, Math.random() * 2f - 1f);
						NbtCompound tag = custom.getPersistentData();
						tag.putInt("level", ent.getPersistentData().getInt("level") + 1);
						custom.setPersistentData(tag);
						ent.getLevel().spawnEntity(custom);
					}
				}
			}
			if(config == CustomTNTConfig.EASTER_EGG) {
				ImprovedExplosion explosion = new ImprovedExplosion(ent.getLevel(), (Entity)ent, ent.getPos(), 3 * LuckyTNTConfigValues.CUSTOM_TNT_FIRST_EXPLOSION_INTENSITY.get().intValue());
				explosion.doBlockExplosion(1f, 1f, 1f, 3 * LuckyTNTConfigValues.CUSTOM_TNT_FIRST_EXPLOSION_INTENSITY.get().floatValue() > 30f ? 1.75f : 1.5f, false, false);
				explosion.doBlockExplosion(new IForEachBlockExplosionEffect() {		
					@Override
					public void doBlockExplosion(World level, BlockPos pos, BlockState state, double distance) {
						if(Math.random() < 0.66f && !state.isAir()) {
							state.getBlock().onDestroyedByExplosion(level, pos, explosion);
							level.setBlockState(pos, Blocks.AIR.getDefaultState(), 3);
							if(Math.random() < 0.5f) {
								ent.getLevel().setBlockState(pos, Blocks.MELON.getDefaultState());
							}
							else {
								ent.getLevel().setBlockState(pos, Blocks.PUMPKIN.getDefaultState());
							}
						}
					}
				});
				
				if(LuckyTNTConfigValues.CUSTOM_TNT_SECOND_EXPLOSION.get() != CustomTNTConfig.NO_EXPLOSION) {
					for(int count = 0; count < 1 * LuckyTNTConfigValues.CUSTOM_TNT_FIRST_EXPLOSION_INTENSITY.get().intValue(); count++) {
						PrimedLTNT custom = EntityRegistry.CUSTOM_TNT.get().create(ent.getLevel());
						custom.setPosition(ent.getPos());
						custom.setOwner(ent.owner());
						custom.setVelocity(Math.random() * 2f - 1f, Math.random() * 2f, Math.random() * 2f - 1f);
						NbtCompound tag = custom.getPersistentData();
						tag.putInt("level", ent.getPersistentData().getInt("level") + 1);
						custom.setPersistentData(tag);
						ent.getLevel().spawnEntity(custom);
					}
				}
			}
			if(config == CustomTNTConfig.FIREWORK) {
				for(int count = 0; count < 15 * LuckyTNTConfigValues.CUSTOM_TNT_FIRST_EXPLOSION_INTENSITY.get().intValue(); count++) {
					PrimedLTNT custom = EntityRegistry.CUSTOM_TNT.get().create(ent.getLevel());
					custom.setPosition(ent.getPos());
					custom.setOwner(ent.owner());
					custom.setVelocity(Math.random() * 3f - 1.5f, Math.random() * 3f - 1.5f, Math.random() * 3f - 1.5f);
					NbtCompound tag = custom.getPersistentData();
					tag.putInt("level", ent.getPersistentData().getInt("level") + 1);
					custom.setPersistentData(tag);
					ent.getLevel().spawnEntity(custom);
				}
			}
		}
		if(ent.getPersistentData().getInt("level") == 1) {
			config = LuckyTNTConfigValues.CUSTOM_TNT_SECOND_EXPLOSION.get();
			if(config == CustomTNTConfig.NORMAL_EXPLOSION) {
				ImprovedExplosion explosion = new ImprovedExplosion(ent.getLevel(), (Entity)ent, ent.getPos(), LuckyTNTConfigValues.CUSTOM_TNT_SECOND_EXPLOSION_INTENSITY.get().intValue());
				explosion.doEntityExplosion(3f, true);
				explosion.doBlockExplosion(1f, 1.3f, 1f, 1.2f, LuckyTNTConfigValues.CUSTOM_TNT_SECOND_EXPLOSION_INTENSITY.get().intValue() > 10 ? true : false, false);
				
				if(LuckyTNTConfigValues.CUSTOM_TNT_THIRD_EXPLOSION.get() != CustomTNTConfig.NO_EXPLOSION) {
					for(int count = 0; count < 3; count++) {
						PrimedLTNT custom = EntityRegistry.CUSTOM_TNT.get().create(ent.getLevel());
						custom.setPosition(ent.getPos());
						custom.setOwner(ent.owner());
						custom.setVelocity(Math.random() * 2f - 1f, Math.random() * 2f, Math.random() * 2f - 1f);
						NbtCompound tag = custom.getPersistentData();
						tag.putInt("level", ent.getPersistentData().getInt("level") + 1);
						custom.setPersistentData(tag);
						ent.getLevel().spawnEntity(custom);
					}
				}
			}
			if(config == CustomTNTConfig.SPHERICAL_EXPLOSION) {
				ExplosionHelper.doSphericalExplosion(ent.getLevel(), ent.getPos(), 5 * LuckyTNTConfigValues.CUSTOM_TNT_SECOND_EXPLOSION_INTENSITY.get().intValue(), new IForEachBlockExplosionEffect() {
					
					@Override
					public void doBlockExplosion(World level, BlockPos pos, BlockState state, double distance) {
						if(!state.isAir() && state.getBlock().getBlastResistance() <= 200) {
							state.getBlock().onDestroyedByExplosion(level, pos, ImprovedExplosion.dummyExplosion(ent.getLevel()));
							level.setBlockState(pos, Blocks.AIR.getDefaultState(), 3);
						}
					}
				});
				
				if(LuckyTNTConfigValues.CUSTOM_TNT_THIRD_EXPLOSION.get() != CustomTNTConfig.NO_EXPLOSION) {
					for(int count = 0; count < 3; count++) {
						PrimedLTNT custom = EntityRegistry.CUSTOM_TNT.get().create(ent.getLevel());
						custom.setPosition(ent.getPos());
						custom.setOwner(ent.owner());
						custom.setVelocity(Math.random() * 2f - 1f, Math.random() * 2f, Math.random() * 2f - 1f);
						NbtCompound tag = custom.getPersistentData();
						tag.putInt("level", ent.getPersistentData().getInt("level") + 1);
						custom.setPersistentData(tag);
						ent.getLevel().spawnEntity(custom);
					}
				}
			}
			if(config == CustomTNTConfig.CUBICAL_EXPLOSION) {
				ExplosionHelper.doCubicalExplosion(ent.getLevel(), ent.getPos(), 5 * LuckyTNTConfigValues.CUSTOM_TNT_SECOND_EXPLOSION_INTENSITY.get().intValue(), new IForEachBlockExplosionEffect() {
					
					@Override
					public void doBlockExplosion(World level, BlockPos pos, BlockState state, double distance) {
						if(!state.isAir() && state.getBlock().getBlastResistance() <= 200) {
							state.getBlock().onDestroyedByExplosion(level, pos, ImprovedExplosion.dummyExplosion(ent.getLevel()));
							level.setBlockState(pos, Blocks.AIR.getDefaultState(), 3);
						}
					}
				});
				
				if(LuckyTNTConfigValues.CUSTOM_TNT_THIRD_EXPLOSION.get() != CustomTNTConfig.NO_EXPLOSION) {
					for(int count = 0; count < 3; count++) {
						PrimedLTNT custom = EntityRegistry.CUSTOM_TNT.get().create(ent.getLevel());
						custom.setPosition(ent.getPos());
						custom.setOwner(ent.owner());
						custom.setVelocity(Math.random() * 2f - 1f, Math.random() * 2f, Math.random() * 2f - 1f);
						NbtCompound tag = custom.getPersistentData();
						tag.putInt("level", ent.getPersistentData().getInt("level") + 1);
						custom.setPersistentData(tag);
						ent.getLevel().spawnEntity(custom);
					}
				}
			}
			if(config == CustomTNTConfig.EASTER_EGG) {
				ImprovedExplosion explosion = new ImprovedExplosion(ent.getLevel(), (Entity)ent, ent.getPos(), 3 * LuckyTNTConfigValues.CUSTOM_TNT_SECOND_EXPLOSION_INTENSITY.get().intValue());
				explosion.doBlockExplosion(1f, 1f, 1f, 3 * LuckyTNTConfigValues.CUSTOM_TNT_SECOND_EXPLOSION_INTENSITY.get().floatValue() > 30f ? 1.75f : 1.5f, false, false);
				explosion.doBlockExplosion(new IForEachBlockExplosionEffect() {		
					@Override
					public void doBlockExplosion(World level, BlockPos pos, BlockState state, double distance) {
						if(Math.random() < 0.66f && !state.isAir()) {
							state.getBlock().onDestroyedByExplosion(level, pos, explosion);
							level.setBlockState(pos, Blocks.AIR.getDefaultState(), 3);
							if(Math.random() < 0.5f) {
								ent.getLevel().setBlockState(pos, Blocks.MELON.getDefaultState());
							}
							else {
								ent.getLevel().setBlockState(pos, Blocks.PUMPKIN.getDefaultState());
							}
						}
					}
				});
				
				if(LuckyTNTConfigValues.CUSTOM_TNT_THIRD_EXPLOSION.get() != CustomTNTConfig.NO_EXPLOSION) {
					for(int count = 0; count < 1 * LuckyTNTConfigValues.CUSTOM_TNT_SECOND_EXPLOSION_INTENSITY.get().intValue(); count++) {
						PrimedLTNT custom = EntityRegistry.CUSTOM_TNT.get().create(ent.getLevel());
						custom.setPosition(ent.getPos());
						custom.setOwner(ent.owner());
						custom.setVelocity(Math.random() * 2f - 1f, Math.random() * 2f, Math.random() * 2f - 1f);
						NbtCompound tag = custom.getPersistentData();
						tag.putInt("level", ent.getPersistentData().getInt("level") + 1);
						custom.setPersistentData(tag);
						ent.getLevel().spawnEntity(custom);
					}
				}
			}
			if(config == CustomTNTConfig.FIREWORK) {
				for(int count = 0; count < 15 * LuckyTNTConfigValues.CUSTOM_TNT_SECOND_EXPLOSION_INTENSITY.get().intValue(); count++) {
					PrimedLTNT custom = EntityRegistry.CUSTOM_TNT.get().create(ent.getLevel());
					custom.setPosition(ent.getPos());
					custom.setOwner(ent.owner());
					custom.setVelocity(Math.random() * 3f - 1.5f, Math.random() * 3f - 1.5f, Math.random() * 3f - 1.5f);
					NbtCompound tag = custom.getPersistentData();
					tag.putInt("level", ent.getPersistentData().getInt("level") + 1);
					custom.setPersistentData(tag);
					ent.getLevel().spawnEntity(custom);
				}
			}
		}
		if(ent.getPersistentData().getInt("level") == 2) {
			config = LuckyTNTConfigValues.CUSTOM_TNT_THIRD_EXPLOSION.get();
			if(config == CustomTNTConfig.NORMAL_EXPLOSION) {
				ImprovedExplosion explosion = new ImprovedExplosion(ent.getLevel(), (Entity)ent, ent.getPos(), LuckyTNTConfigValues.CUSTOM_TNT_THIRD_EXPLOSION_INTENSITY.get().intValue());
				explosion.doEntityExplosion(3f, true);
				explosion.doBlockExplosion(1f, 1.3f, 1f, 1.2f, LuckyTNTConfigValues.CUSTOM_TNT_THIRD_EXPLOSION_INTENSITY.get().intValue() > 10 ? true : false, false);
			}
			if(config == CustomTNTConfig.SPHERICAL_EXPLOSION) {
				ExplosionHelper.doSphericalExplosion(ent.getLevel(), ent.getPos(), 5 * LuckyTNTConfigValues.CUSTOM_TNT_THIRD_EXPLOSION_INTENSITY.get().intValue(), new IForEachBlockExplosionEffect() {
					
					@Override
					public void doBlockExplosion(World level, BlockPos pos, BlockState state, double distance) {
						if(!state.isAir() && state.getBlock().getBlastResistance() <= 200) {
							state.getBlock().onDestroyedByExplosion(level, pos, ImprovedExplosion.dummyExplosion(ent.getLevel()));
							level.setBlockState(pos, Blocks.AIR.getDefaultState(), 3);
						}
					}
				});
			}
			if(config == CustomTNTConfig.CUBICAL_EXPLOSION) {
				ExplosionHelper.doCubicalExplosion(ent.getLevel(), ent.getPos(), 5 * LuckyTNTConfigValues.CUSTOM_TNT_THIRD_EXPLOSION_INTENSITY.get().intValue(), new IForEachBlockExplosionEffect() {
					
					@Override
					public void doBlockExplosion(World level, BlockPos pos, BlockState state, double distance) {
						if(!state.isAir() && state.getBlock().getBlastResistance() <= 200) {
							state.getBlock().onDestroyedByExplosion(level, pos, ImprovedExplosion.dummyExplosion(ent.getLevel()));
							level.setBlockState(pos, Blocks.AIR.getDefaultState(), 3);
						}
					}
				});
			}
			if(config == CustomTNTConfig.EASTER_EGG) {
				ImprovedExplosion explosion = new ImprovedExplosion(ent.getLevel(), (Entity)ent, ent.getPos(), 3 * LuckyTNTConfigValues.CUSTOM_TNT_THIRD_EXPLOSION_INTENSITY.get().intValue());
				explosion.doBlockExplosion(1f, 1f, 1f, 3 * LuckyTNTConfigValues.CUSTOM_TNT_THIRD_EXPLOSION_INTENSITY.get().floatValue() > 30f ? 1.75f : 1.5f, false, false);
				explosion.doBlockExplosion(new IForEachBlockExplosionEffect() {		
					@Override
					public void doBlockExplosion(World level, BlockPos pos, BlockState state, double distance) {
						if(Math.random() < 0.66f && !state.isAir()) {
							state.getBlock().onDestroyedByExplosion(level, pos, explosion);
							level.setBlockState(pos, Blocks.AIR.getDefaultState(), 3);
							if(Math.random() < 0.5f) {
								ent.getLevel().setBlockState(pos, Blocks.MELON.getDefaultState());
							}
							else {
								ent.getLevel().setBlockState(pos, Blocks.PUMPKIN.getDefaultState());
							}
						}
					}
				});
			}
			if(config == CustomTNTConfig.FIREWORK) {
				for(int count = 0; count < 15 * LuckyTNTConfigValues.CUSTOM_TNT_THIRD_EXPLOSION_INTENSITY.get().intValue(); count++) {
					PrimedLTNT custom = EntityRegistry.TNT.get().create(ent.getLevel());
					custom.setPosition(ent.getPos());
					custom.setOwner(ent.owner());
					custom.setVelocity(Math.random() * 3f - 1.5f, Math.random() * 3f - 1.5f, Math.random() * 3f - 1.5f);
					ent.getLevel().spawnEntity(custom);
				}
			}
		}
	}
	
	@Override
	public void spawnParticles(IExplosiveEntity ent) {
		ent.getLevel().addParticle(new DustParticleEffect(new Vector3f(1f, 0f, 0f), 1f), ent.x(), ent.y() + 1f, ent.z(), 0, 0, 0);
		ent.getLevel().addParticle(new DustParticleEffect(new Vector3f(0f, 1f, 0f), 1f), ent.x(), ent.y() + 1f, ent.z(), 0, 0, 0);
		ent.getLevel().addParticle(new DustParticleEffect(new Vector3f(0f, 0f, 1f), 1f), ent.x(), ent.y() + 1f, ent.z(), 0, 0, 0);
		if(ent.getLevel().isClient()) {
			CustomTNTConfig config = LuckyTNTConfigValues.CUSTOM_TNT_FIRST_EXPLOSION.get();
			if(ent.getPersistentData().getInt("level") == 0) {
				if(config == CustomTNTConfig.FIREWORK) {
					ent.getLevel().addParticle(ParticleTypes.FLAME, ent.x(), ent.y() + 0.5f, ent.z(), 0, 0, 0);
				}
			}
			if(ent.getPersistentData().getInt("level") == 1) {
				config = LuckyTNTConfigValues.CUSTOM_TNT_SECOND_EXPLOSION.get();
				if(config == CustomTNTConfig.FIREWORK) {
					ent.getLevel().addParticle(ParticleTypes.FLAME, ent.x(), ent.y() + 0.5f, ent.z(), 0, 0, 0);
				}
			}
			if(ent.getPersistentData().getInt("level") == 2) {
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

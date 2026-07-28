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
		if(ent.getTNTFuse() < 350 && ent.getTNTFuse() > 0) {
			Level level = ent.getLevel();

			if(ent.getTNTFuse() % 20 == 0 && level instanceof ServerLevel) {
				RandomSource random = level.getRandom();
				int amount = Math.min(400 + (int)Math.round(1600D / ((double)ent.getTNTFuse() * 0.5D)), 800);
				for(int i = 0; i < amount; i++) {
					int posX = Mth.floor(ent.x()) + random.nextInt(151) - 75;
					int posZ = Mth.floor(ent.z()) + random.nextInt(151) - 75;
					if(!level.isLoaded(new BlockPos(posX, Mth.floor(ent.y()), posZ))) {
						continue;
					}
					int posY = LevelEvents.getTopBlock(level, posX, posZ, false);
					BlockPos pos = new BlockPos(posX, posY, posZ);
					BlockState state = level.getBlockState(pos);
					if(!state.isAir() && !state.hasBlockEntity() && state.getBlock().getExplosionResistance() < 100) {
						FallingBlockEntity.fall(level, pos, state);
					}
				}
			}

			AABB range = new AABB(ent.x() - 100, ent.y() - 100, ent.z() - 100, ent.x() + 100, ent.y() + 100, ent.z() + 100);
			List<LivingEntity> list = level.getEntitiesOfClass(LivingEntity.class, range);
			List<FallingBlockEntity> blocks = level.getEntitiesOfClass(FallingBlockEntity.class, range);

			for(FallingBlockEntity block : blocks) {
				Vec3 vec = new Vec3(ent.x() - block.getX(), ent.y() - block.getY(), ent.z() - block.getZ());
				if(vec.length() <= 2) {
					if(level instanceof ServerLevel) {
						block.discard();
					}
					continue;
				}
				block.setDeltaMovement(vec.normalize().scale(0.4D).add(0, 0.1D, 0));
			}

			for(LivingEntity living : list) {
				Vec3 vec = new Vec3(ent.x() - living.getX(), ent.y() - living.getEyeY(), ent.z() - living.getZ());
				double distance = vec.length();
				if(distance <= 2 && level instanceof ServerLevel sLevel) {
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

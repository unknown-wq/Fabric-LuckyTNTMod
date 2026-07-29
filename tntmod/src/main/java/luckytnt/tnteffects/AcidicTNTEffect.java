package luckytnt.tnteffects;

import java.util.List;

import luckytnt.registry.BlockRegistry;
import luckytnt.registry.EntityRegistry;
import luckytntlib.entity.LExplosiveProjectile;
import luckytntlib.entity.PrimedLTNT;
import luckytntlib.util.IExplosiveEntity;
import luckytntlib.util.explosions.ExplosionHelper;
import luckytntlib.util.explosions.IForEachBlockExplosionEffect;
import luckytntlib.util.tnteffects.PrimedTNTEffect;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.damagesource.DamageSources;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class AcidicTNTEffect extends PrimedTNTEffect {

	private static final BlockState AIR = Blocks.AIR.defaultBlockState();

	private static final DustParticleOptions BLACK_SMALL = new DustParticleOptions(0, 0.5f);
	private static final DustParticleOptions YELLOW_SMALL = new DustParticleOptions((255 << 16) | (255 << 8), 0.5f);
	private static final DustParticleOptions BLACK_LARGE = new DustParticleOptions(0, 1f);
	private static final DustParticleOptions YELLOW_LARGE = new DustParticleOptions((255 << 16) | (255 << 8), 1f);
	private static final Vec3 VEC31 = new Vec3(0.5D, Math.sqrt(1D - (0.5D * 0.5D)), 0);
	private static final Vec3 VEC32 = new Vec3(-0.5D, Math.sqrt(1D - (0.5D * 0.5D)), 0);

	/**
	 * Stateless, so it is allocated once instead of once per projectile per tick.
	 * The cheap isAir/distance checks run before the random roll, which previously was rolled
	 * for every single position including air. 5D + random * 2 is always < 7D, so the
	 * distance < 7D gate is exact.
	 */
	private static final IForEachBlockExplosionEffect DISSOLVE = new IForEachBlockExplosionEffect() {

		@Override
		public void doBlockExplosion(Level level, BlockPos pos, BlockState state, double distance) {
			if(distance >= 7D || state.isAir() || state.getBlock().getExplosionResistance() > 200) {
				return;
			}
			//everything closer than 5 blocks dissolves no matter what the roll is, so two thirds of the
			//sphere never has to touch the RNG at all. level.getRandom() is a plain xoroshiro source,
			//while Math.random() went through a shared, CAS guarded java.util.Random.
			if(distance < 5D || distance <= 5D + level.getRandom().nextDouble() * 2) {
				//UPDATE_CLIENTS: the acid eats a contiguous volume, the neighbour updates were almost
				//all spent notifying blocks that dissolve in the same pass
				level.setBlock(pos, AIR, Block.UPDATE_CLIENTS);
			}
		}
	};

	@Override
	public void baseTick(IExplosiveEntity entity) {
		if(entity instanceof LExplosiveProjectile) {
			if(!entity.getLevel().isClientSide()) {
				explosionTick(entity);
			} else {
				spawnParticles(entity);
			}
			entity.setTNTFuse(entity.getTNTFuse() - 1);
			if(entity.getTNTFuse() <= 0) {
				entity.destroy();
			}
		} else {
			super.baseTick(entity);
		}
	}

	@Override
	public void explosionTick(IExplosiveEntity ent) {
		if(ent instanceof PrimedLTNT) {
			if(ent.getTNTFuse() <= 10) {
				Entity tnt = (Entity)ent;
				//no side guard here on purpose: this runs on both sides and moves the TNT itself
				tnt.setDeltaMovement(tnt.getDeltaMovement().x, 0.8f, tnt.getDeltaMovement().z);
			}
			return;
		}
		Level level = ent.getLevel();
		int fuse = ent.getTNTFuse();
		if(fuse == 0) {
			level.playSound(null, toBlockPos(ent.getPos()), SoundEvents.FIRE_EXTINGUISH, SoundSource.MASTER, 1f, 1f);
		}
		if(!(level instanceof ServerLevel sLevel)) {
			return;
		}
		//All 70 projectiles are spawned on the same tick and therefore share a fuse, so a plain "fuse % 4 == 0"
		//made all 70 sweeps land on the very same tick. Keying the phase off the entity id keeps the per
		//projectile rate identical while spreading the work evenly over the 4 ticks.
		int id = ((Entity)ent).getId();
		if(fuse % 4 == (id & 3)) {
			//a spherical sweep of radius 7 visits exactly the positions with distance <= 7, which is the
			//same set the DISSOLVE predicate accepts. The cubical sweep visited 3375 positions to reach
			//those 1437, so 57% of the getBlockState calls were thrown away.
			ExplosionHelper.doSphericalExplosion(level, ent.getPos(), 7, DISSOLVE);
		}
		if(fuse % 20 == Math.floorMod(id, 20)) {
			int minX = Mth.floor(ent.x());
			int minY = Mth.floor(ent.y());
			int minZ = Mth.floor(ent.z());
			List<LivingEntity> list = level.getEntitiesOfClass(LivingEntity.class, new AABB(minX - 3, minY - 3, minZ - 3, minX + 3, minY + 3, minZ + 3));
			if(!list.isEmpty()) {
				DamageSources sources = level.damageSources();
				for(LivingEntity lent : list) {
					lent.hurtServer(sLevel, sources.magic(), 3f);
				}
			}
		}
	}

	@Override
	public void serverExplosion(IExplosiveEntity ent) {
		if(ent instanceof PrimedLTNT) {
			Level level = ent.getLevel();
			LivingEntity owner = ent.owner();
			Vec3 pos = ent.getPos();
			for(int count = 0; count < 70; count++) {
				LExplosiveProjectile projectile = EntityRegistry.ACIDIC_PROJECTILE.get().create(level, EntitySpawnReason.MOB_SUMMONED);
				projectile.setPos(pos);
				projectile.setOwner(owner);
				projectile.setDeltaMovement(Math.random() * 2.5f - Math.random() * 2.5f, Math.random() - Math.random(), Math.random() * 2.5f - Math.random() * 2.5f);
				level.addFreshEntity(projectile);
			}
		}
	}

	@Override
	public void spawnParticles(IExplosiveEntity ent) {
		Level level = ent.getLevel();
		double x = ent.x();
		double y = ent.y();
		double z = ent.z();
		if(ent instanceof PrimedLTNT) {
			for(int count = 0; count <= 10; count++) {
				level.addParticle(BLACK_SMALL, x - 0.5D + 0.1D * count, y + 1.25D, z, 0, 0, 0);
			}
			for(double i = 0; i < 1; i += 0.1D) {
				level.addParticle(BLACK_SMALL, x + 0.5D + i * VEC32.x, y + 1.25D + i * VEC32.y, z, 0, 0, 0);
				level.addParticle(BLACK_SMALL, x - 0.5D + i * VEC31.x, y + 1.25D + i * VEC31.y, z, 0, 0, 0);
			}
			for(double i = 0; i < 0.7; i += 0.1D) {
				level.addParticle(YELLOW_SMALL, x - 0.1D, y + 1.35D + i * VEC31.y, z, 0, 0, 0);
				level.addParticle(YELLOW_SMALL, x + 0.1D, y + 1.35D + i * VEC31.y, z, 0, 0, 0);
			}
			for(double i = 0; i < 0.4; i += 0.1D) {
				level.addParticle(YELLOW_SMALL, x - 0.2D, y + 1.35D + i * VEC31.y, z, 0, 0, 0);
				level.addParticle(YELLOW_SMALL, x + 0.2D, y + 1.35D + i * VEC31.y, z, 0, 0, 0);
			}
			for(double i = 0; i < 0.2; i += 0.1D) {
				level.addParticle(YELLOW_SMALL, x - 0.3D, y + 1.35D + i * VEC31.y, z, 0, 0, 0);
				level.addParticle(YELLOW_SMALL, x + 0.3D, y + 1.35D + i * VEC31.y, z, 0, 0, 0);
			}
			level.addParticle(YELLOW_SMALL, x - 0.4D, y + 1.35D, z, 0, 0, 0);
			level.addParticle(YELLOW_SMALL, x + 0.4D, y + 1.35D, z, 0, 0, 0);
			level.addParticle(YELLOW_SMALL, x, y + 1.35D, z, 0, 0, 0);
			level.addParticle(BLACK_SMALL, x, y + 1.45D, z, 0, 0, 0);
			level.addParticle(YELLOW_SMALL, x, y + 1.55D, z, 0, 0, 0);
			for(double i = 0; i < 0.3; i += 0.1D) {
				level.addParticle(BLACK_SMALL, x, y + 1.65D + i * VEC31.y, z, 0, 0, 0);
			}
			level.addParticle(YELLOW_SMALL, x, y + 1.95D, z, 0, 0, 0);
			level.addParticle(YELLOW_SMALL, x, y + 2.05D, z, 0, 0, 0);
		} else {
			level.addParticle(BLACK_LARGE, x + 0.2f, y + 1f, z, 0, 0, 0);
			level.addParticle(BLACK_LARGE, x - 0.2f, y + 1f, z, 0, 0, 0);
			level.addParticle(YELLOW_LARGE, x, y + 1f, z + 0.2f, 0, 0, 0);
			level.addParticle(YELLOW_LARGE, x, y + 1f, z - 0.2f, 0, 0, 0);
		}
	}

	@Override
	public Block getBlock() {
		return BlockRegistry.ACIDIC_TNT.get();
	}

	@Override
	public int getDefaultFuse(IExplosiveEntity ent) {
		return ent instanceof PrimedLTNT ? 160 : 120;
	}
}

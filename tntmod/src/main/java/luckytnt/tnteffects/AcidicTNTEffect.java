package luckytnt.tnteffects;

import java.util.List;

import org.joml.Vector3f;

import luckytnt.registry.BlockRegistry;
import luckytnt.registry.EntityRegistry;
import luckytntlib.entity.LExplosiveProjectile;
import luckytntlib.entity.PrimedLTNT;
import luckytntlib.util.IExplosiveEntity;
import luckytntlib.util.explosions.ExplosionHelper;
import luckytntlib.util.explosions.IForEachBlockExplosionEffect;
import luckytntlib.util.tnteffects.PrimedTNTEffect;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSources;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class AcidicTNTEffect extends PrimedTNTEffect {

	@Override
	public void baseTick(IExplosiveEntity entity) {
		if(entity instanceof LExplosiveProjectile) {
			if(!entity.getLevel().isClient()) {
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
				((Entity)ent).setVelocity(((Entity)ent).getVelocity().x, 0.8f, ((Entity)ent).getVelocity().z);
			}
		} else {
			if(ent.getTNTFuse() == 0) {
				ent.getLevel().playSound(null, toBlockPos(ent.getPos()), SoundEvents.BLOCK_FIRE_EXTINGUISH, SoundCategory.MASTER, 1f, 1f);
			}
			if(!ent.getLevel().isClient()) {
				ExplosionHelper.doCubicalExplosion(ent.getLevel(), ent.getPos(), 7, new IForEachBlockExplosionEffect() {
					
					@Override
					public void doBlockExplosion(World level, BlockPos pos, BlockState state, double distance) {
						if(distance <= 5D + Math.random() * 2 && !state.isAir() && state.getBlock().getBlastResistance() <= 200) {
							level.setBlockState(pos, Blocks.AIR.getDefaultState(), 3);
						}
					}
				});
			}
			if(ent.getTNTFuse() % 20 == 0) {
				BlockPos min = toBlockPos(ent.getPos()).add(-3, -3, -3);
				BlockPos max = toBlockPos(ent.getPos()).add(3, 3, 3);
				List<LivingEntity> list = ent.getLevel().getNonSpectatingEntities(LivingEntity.class, new Box(min.getX(), min.getY(), min.getZ(), max.getX(), max.getY(), max.getZ()));			
				DamageSources sources = ent.getLevel().getDamageSources();
				for(LivingEntity lent : list) {
					lent.damage(sources.magic(), 3f);
				}
			}
		}
	}
	
	@Override
	public void serverExplosion(IExplosiveEntity ent) {
		if(ent instanceof PrimedLTNT) {
			for(int count = 0; count < 70; count++) {
				LExplosiveProjectile projectile = EntityRegistry.ACIDIC_PROJECTILE.get().create(ent.getLevel());
				projectile.setPosition(ent.getPos());
				projectile.setOwner(ent.owner());
				projectile.setVelocity(Math.random() * 2.5f - Math.random() * 2.5f, Math.random() - Math.random(), Math.random() * 2.5f - Math.random() * 2.5f);
				ent.getLevel().spawnEntity(projectile);
			}
		}
	}
	
	@Override
	public void spawnParticles(IExplosiveEntity ent) {
		if(ent instanceof PrimedLTNT) {
			Vec3d vec31 = new Vec3d(0.5D, Math.sqrt(1D - (0.5D * 0.5D)), 0);
			Vec3d vec32 = new Vec3d(-0.5D, Math.sqrt(1D - (0.5D * 0.5D)), 0);
			for(int count = 0; count <= 10; count++) {
				ent.getLevel().addParticle(new DustParticleEffect(new Vector3f(0, 0, 0), 0.5f), ent.x() - 0.5D + 0.1D * count, ent.y() + 1.25D, ent.z(), 0, 0, 0);
			}
			for(double i = 0; i < 1; i += 0.1D) {
				ent.getLevel().addParticle(new DustParticleEffect(new Vector3f(0, 0, 0), 0.5f), ent.x() + 0.5D + i * vec32.x, ent.y() + 1.25D + i * vec32.y, ent.z(), 0, 0, 0);
				ent.getLevel().addParticle(new DustParticleEffect(new Vector3f(0, 0, 0), 0.5f), ent.x() - 0.5D + i * vec31.x, ent.y() + 1.25D + i * vec31.y, ent.z(), 0, 0, 0);
			}
			for(double i = 0; i < 0.7; i += 0.1D) {
				ent.getLevel().addParticle(new DustParticleEffect(new Vector3f(1f, 1f, 0), 0.5f), ent.x() - 0.1D, ent.y() + 1.35D + i * vec31.y, ent.z(), 0, 0, 0);
				ent.getLevel().addParticle(new DustParticleEffect(new Vector3f(1f, 1f, 0), 0.5f), ent.x() + 0.1D, ent.y() + 1.35D + i * vec31.y, ent.z(), 0, 0, 0);
			}
			for(double i = 0; i < 0.4; i += 0.1D) {
				ent.getLevel().addParticle(new DustParticleEffect(new Vector3f(1f, 1f, 0), 0.5f), ent.x() - 0.2D, ent.y() + 1.35D + i * vec31.y, ent.z(), 0, 0, 0);
				ent.getLevel().addParticle(new DustParticleEffect(new Vector3f(1f, 1f, 0), 0.5f), ent.x() + 0.2D, ent.y() + 1.35D + i * vec31.y, ent.z(), 0, 0, 0);
			}
			for(double i = 0; i < 0.2; i += 0.1D) {
				ent.getLevel().addParticle(new DustParticleEffect(new Vector3f(1f, 1f, 0), 0.5f), ent.x() - 0.3D, ent.y() + 1.35D + i * vec31.y, ent.z(), 0, 0, 0);
				ent.getLevel().addParticle(new DustParticleEffect(new Vector3f(1f, 1f, 0), 0.5f), ent.x() + 0.3D, ent.y() + 1.35D + i * vec31.y, ent.z(), 0, 0, 0);
			}
			ent.getLevel().addParticle(new DustParticleEffect(new Vector3f(1f, 1f, 0), 0.5f), ent.x() - 0.4D, ent.y() + 1.35D, ent.z(), 0, 0, 0);
			ent.getLevel().addParticle(new DustParticleEffect(new Vector3f(1f, 1f, 0), 0.5f), ent.x() + 0.4D, ent.y() + 1.35D, ent.z(), 0, 0, 0);
			ent.getLevel().addParticle(new DustParticleEffect(new Vector3f(1f, 1f, 0), 0.5f), ent.x(), ent.y() + 1.35D, ent.z(), 0, 0, 0);
			ent.getLevel().addParticle(new DustParticleEffect(new Vector3f(0, 0, 0), 0.5f), ent.x(), ent.y() + 1.45D, ent.z(), 0, 0, 0);
			ent.getLevel().addParticle(new DustParticleEffect(new Vector3f(1f, 1f, 0), 0.5f), ent.x(), ent.y() + 1.55D, ent.z(), 0, 0, 0);
			for(double i = 0; i < 0.3; i += 0.1D) {
				ent.getLevel().addParticle(new DustParticleEffect(new Vector3f(0, 0, 0), 0.5f), ent.x(), ent.y() + 1.65D + i * vec31.y, ent.z(), 0, 0, 0);
			}
			ent.getLevel().addParticle(new DustParticleEffect(new Vector3f(1f, 1f, 0), 0.5f), ent.x(), ent.y() + 1.95D, ent.z(), 0, 0, 0);
			ent.getLevel().addParticle(new DustParticleEffect(new Vector3f(1f, 1f, 0), 0.5f), ent.x(), ent.y() + 2.05D, ent.z(), 0, 0, 0);
		} else {
			ent.getLevel().addParticle(new DustParticleEffect(new Vector3f(0, 0, 0), 1), ent.x() + 0.2f, ent.y() + 1f, ent.z(), 0, 0, 0);
			ent.getLevel().addParticle(new DustParticleEffect(new Vector3f(0, 0, 0), 1), ent.x() - 0.2f, ent.y() + 1f, ent.z(), 0, 0, 0);
			ent.getLevel().addParticle(new DustParticleEffect(new Vector3f(1f, 1f, 0), 1), ent.x(), ent.y() + 1f, ent.z() + 0.2f, 0, 0, 0);
			ent.getLevel().addParticle(new DustParticleEffect(new Vector3f(1f, 1f, 0), 1), ent.x(), ent.y() + 1f, ent.z() - 0.2f, 0, 0, 0);
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

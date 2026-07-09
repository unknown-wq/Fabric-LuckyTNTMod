package luckytnt.tnteffects.projectile;

import java.util.List;

import org.joml.Vector3f;

import luckytnt.registry.BlockRegistry;
import luckytntlib.util.IExplosiveEntity;
import luckytntlib.util.explosions.ExplosionHelper;
import luckytntlib.util.explosions.IForEachBlockExplosionEffect;
import luckytntlib.util.explosions.ImprovedExplosion;
import luckytntlib.util.tnteffects.PrimedTNTEffect;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSources;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;

public class DisintegratingProjectileEffect extends PrimedTNTEffect {

	@Override
	public void baseTick(IExplosiveEntity entity) {
		if(!entity.getLevel().isClient()) {
			explosionTick(entity);
		} else {
			spawnParticles(entity);
		}
		entity.setTNTFuse(entity.getTNTFuse() - 1);
		if(entity.getTNTFuse() <= 0) {
			entity.destroy();
		}
	}
	
	@Override
	public void explosionTick(IExplosiveEntity ent) {
		if(ent.getTNTFuse() == 0) {
			ent.getLevel().playSound(null, toBlockPos(ent.getPos()), SoundEvents.BLOCK_FIRE_EXTINGUISH, SoundCategory.MASTER, 1f, 1f);
		}
		if(!ent.getLevel().isClient()) {
			ExplosionHelper.doCubicalExplosion(ent.getLevel(), ent.getPos(), 12, new IForEachBlockExplosionEffect() {
					
				@Override
				public void doBlockExplosion(World level, BlockPos pos, BlockState state, double distance) {
					if(distance < (10D + (Math.random() * 2))) {
						if(state.getBlock().getBlastResistance() < 200) {
							state.getBlock().onDestroyedByExplosion(level, pos, ImprovedExplosion.dummyExplosion(ent.getLevel()));
							level.setBlockState(pos, Blocks.AIR.getDefaultState(), 3);
						}
					} else if(distance > 11 && distance <= 13) {
						if(state.getBlock() == Blocks.STONE && Math.random() < 0.01D) {
							level.setBlockState(pos, BlockRegistry.TOXIC_STONE.get().getDefaultState(), 3);
						}
					}
				}
			});
		}
		if(ent.getTNTFuse() % 20 == 0) {
			BlockPos min = toBlockPos(ent.getPos()).add(-6, -6, -6);
			BlockPos max = toBlockPos(ent.getPos()).add(6, 6, 6);
			List<LivingEntity> list = ent.getLevel().getNonSpectatingEntities(LivingEntity.class, new Box(min.getX(), min.getY(), min.getZ(), max.getX(), max.getY(), max.getZ()));
			DamageSources sources = ent.getLevel().getDamageSources();
			for(LivingEntity lent : list) {
				lent.damage(sources.magic(), 5f);
			}
		}
	}
	
	@Override
	public void spawnParticles(IExplosiveEntity ent) {
		ent.getLevel().addParticle(new DustParticleEffect(new Vector3f(1f, 1f, 1f), 1f), ent.x() + 0.2f, ent.y() + 1f, ent.z(), 0, 0, 0);
		ent.getLevel().addParticle(new DustParticleEffect(new Vector3f(1f, 1f, 1f), 1f), ent.x() - 0.2f, ent.y() + 1f, ent.z(), 0, 0, 0);
		ent.getLevel().addParticle(new DustParticleEffect(new Vector3f(1f, 0, 0), 1f), ent.x(), ent.y() + 1f, ent.z() + 0.2f, 0, 0, 0);
		ent.getLevel().addParticle(new DustParticleEffect(new Vector3f(1f, 0, 0), 1f), ent.x(), ent.y() + 1f, ent.z() - 0.2f, 0, 0, 0);
	}
	
	@Override
	public Block getBlock() {
		return BlockRegistry.DISINTEGRATING_TNT.get();
	}
	
	@Override
	public int getDefaultFuse(IExplosiveEntity ent) {
		return 120;
	}
}

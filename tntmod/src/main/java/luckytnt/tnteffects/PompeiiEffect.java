package luckytnt.tnteffects;

import luckytnt.registry.BlockRegistry;
import luckytnt.registry.EntityRegistry;
import luckytntlib.entity.LExplosiveProjectile;
import luckytntlib.entity.PrimedLTNT;
import luckytntlib.util.IExplosiveEntity;
import luckytntlib.util.tnteffects.PrimedTNTEffect;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;

public class PompeiiEffect extends PrimedTNTEffect{

	@SuppressWarnings("resource")
	@Override
	public void explosionTick(IExplosiveEntity entity) {
		if(entity instanceof PrimedLTNT) {
			if(entity.getTNTFuse() < 150) {
				if(entity.getTNTFuse() % 15 == 0) {
					for(int i = 0; i < 30; i++) {
						LExplosiveProjectile pompeii = EntityRegistry.POMPEII_PROJECTILE.get().create(entity.getLevel());
						pompeii.setPosition(entity.getPos());
						pompeii.setOwner(entity.owner());
						pompeii.setVelocity((Math.random() * 3D - 1.5D) * 0.1f, 0.6f + Math.random() * 0.4f, (Math.random() * 3D - 1.5D) * 0.1f, 3f + entity.getLevel().random.nextFloat() * 2f, 0f);	
						pompeii.setOnFireFor(1000);
						entity.getLevel().spawnEntity(pompeii);
						entity.getLevel().playSound(null, toBlockPos(entity.getPos()), SoundEvents.ENTITY_GENERIC_EXPLODE.value(), SoundCategory.MASTER, 3, 1);
					}
				}
			}
		}
	}
	
	@Override
	public void serverExplosion(IExplosiveEntity ent) {
		if(ent instanceof LExplosiveProjectile) {
			if(ent.getLevel().getBlockState(toBlockPos(ent.getPos()).up()).getBlock().getBlastResistance() <= 200) {
				ent.getLevel().setBlockState(toBlockPos(ent.getPos()).up(), Blocks.LAVA.getDefaultState(), 3);
			}
		}
	}
	
	@Override
	public void spawnParticles(IExplosiveEntity ent) {
		if(ent instanceof PrimedLTNT) {
			ent.getLevel().addParticle(ParticleTypes.FLAME, ent.x() + 0.5f, ent.y() + 1f, ent.z() + 0.5f, 0.05f, 0.2f, 0.05f);
			ent.getLevel().addParticle(ParticleTypes.FLAME, ent.x() - 0.5f, ent.y() + 1f, ent.z() - 0.5f, -0.05f, 0.2f, -0.05f);
			ent.getLevel().addParticle(ParticleTypes.FLAME, ent.x() + 0.5f, ent.y() + 1f, ent.z() - 0.5f, 0.05f, 0.2f, -0.05f);
			ent.getLevel().addParticle(ParticleTypes.FLAME, ent.x() - 0.5f, ent.y() + 1f, ent.z() + 0.5f, -0.05f, 0.2f, 0.05f);
			ent.getLevel().addParticle(ParticleTypes.LAVA, ent.x(), ent.y() + 1f, ent.z(), 0, 0, 0);
		}
		else {
			ent.getLevel().addParticle(ParticleTypes.LARGE_SMOKE, true, ent.x(), ent.y() + 0.5f, ent.z(), 0, 0.1f, 0);
		}
	}
	
	@Override
	public BlockState getBlockState(IExplosiveEntity ent) {
		return ent instanceof PrimedLTNT ? BlockRegistry.POMPEII.get().getDefaultState() : Blocks.MAGMA_BLOCK.getDefaultState();
	}
	
	@Override
	public boolean airFuse() {
		return true;
	}
	
	@Override
	public int getDefaultFuse(IExplosiveEntity entity) {
		return entity instanceof PrimedLTNT ? 220 : 100000;
	}
}

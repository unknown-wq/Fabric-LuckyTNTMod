package luckytnt.tnteffects;


import luckytnt.registry.BlockRegistry;
import luckytntlib.util.IExplosiveEntity;
import luckytntlib.util.explosions.IForEachBlockExplosionEffect;
import luckytntlib.util.explosions.ImprovedExplosion;
import luckytntlib.util.tnteffects.PrimedTNTEffect;
import net.minecraft.block.AbstractFireBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.mob.GhastEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

public class HellfireTNTEffect extends PrimedTNTEffect{
	
	private final int strength;
	private final int ghastCount;
	
	public HellfireTNTEffect(int strength, int ghastCount) {
		this.strength = strength;
		this.ghastCount = ghastCount;
	}
	
	@Override
	public void serverExplosion(IExplosiveEntity entity) {
		ImprovedExplosion explosion = new ImprovedExplosion(entity.getLevel(), (Entity) entity, entity.getPos().x, entity.getPos().y + 0.5f, entity.getPos().z, strength);
		explosion.doEntityExplosion(2f, true);
		explosion.doBlockExplosion(1f, 1f, 1f, 1.5f, false, false);
		ImprovedExplosion netherExplosion = new ImprovedExplosion(entity.getLevel(), (Entity)entity, entity.getPos().add(0, 0.5f, 0), MathHelper.floor(strength * 1.5f));
		netherExplosion.doBlockExplosion(1f, 1f, 1f, 1.5f, false, new IForEachBlockExplosionEffect() {
			
			@Override
			public void doBlockExplosion(World level, BlockPos pos, BlockState state, double distance) {
				if(distance <= 25) {
					if(Math.random() < 0.9f) {
						state.getBlock().onDestroyedByExplosion(level, pos, netherExplosion);
						level.setBlockState(pos, Blocks.NETHERRACK.getDefaultState());
						if(Math.random() < 0.1f) {
							if(level.getBlockState(pos.up()).isAir()) {
								level.setBlockState(pos.up(), AbstractFireBlock.getState(level, pos.up()));
							}
						}
					}
					else if(Math.random() < 0.3f) {
						state.getBlock().onDestroyedByExplosion(level, pos, netherExplosion);
						level.setBlockState(pos, Blocks.LAVA.getDefaultState());
					}
				}
			}
		});
		for(int i = 0; i < ghastCount; i++) {
			GhastEntity ghast = new GhastEntity(EntityType.GHAST, entity.getLevel());
			ghast.setPosition(entity.getPos().add(0, 20 + Math.random() * 20, 0));
			entity.getLevel().playSound(ghast, ghast.getBlockPos(), SoundEvents.ENTITY_GHAST_HURT, SoundCategory.HOSTILE, 3f, 1f);
			entity.getLevel().spawnEntity(ghast);
		}
	}
	
	@Override
	public void spawnParticles(IExplosiveEntity entity) {
		World level = entity.getLevel();
		level.addParticle(ParticleTypes.FLAME, entity.x(), entity.y() + 0.5f, entity.z(), 0, 0.1f, 0);
		level.addParticle(ParticleTypes.FLAME, entity.x(), entity.y() + 0.5f, entity.z(), 0.05f, 0.1f, 0);
		level.addParticle(ParticleTypes.FLAME, entity.x(), entity.y() + 0.5f, entity.z(), -0.05f, 0.1f, 0);
		level.addParticle(ParticleTypes.FLAME, entity.x(), entity.y() + 0.5f, entity.z(), 0, 0.1f, 0.05f);
		level.addParticle(ParticleTypes.FLAME, entity.x(), entity.y() + 0.5f, entity.z(), 0, 0.1f, -0.05f);
		
		level.addParticle(ParticleTypes.FLAME, entity.x(), entity.y() + 0.5f, entity.z(), 0.2f, 0, 0);
		level.addParticle(ParticleTypes.FLAME, entity.x(), entity.y() + 0.5f, entity.z(), -0.2f, 0, 0);
		level.addParticle(ParticleTypes.FLAME, entity.x(), entity.y() + 0.5f, entity.z(), 0, 0, 0.2f);
		level.addParticle(ParticleTypes.FLAME, entity.x(), entity.y() + 0.5f, entity.z(), 0, 0, -0.2f);
		
		level.addParticle(ParticleTypes.FLAME, entity.x(), entity.y() + 0.5f, entity.z(), 0.1f, 0, 0.1f);
		level.addParticle(ParticleTypes.FLAME, entity.x(), entity.y() + 0.5f, entity.z(), -0.1f, 0, -0.1f);
		level.addParticle(ParticleTypes.FLAME, entity.x(), entity.y() + 0.5f, entity.z(), 0.1f, 0, -0.1f);
		level.addParticle(ParticleTypes.FLAME, entity.x(), entity.y() + 0.5f, entity.z(), -0.1f, 0, 0.1f);
	}

	@Override
	public Block getBlock() {
		return BlockRegistry.HELLFIRE_TNT.get();
	}
	
	@Override
	public int getDefaultFuse(IExplosiveEntity entity) {
		return 160;
	}
}

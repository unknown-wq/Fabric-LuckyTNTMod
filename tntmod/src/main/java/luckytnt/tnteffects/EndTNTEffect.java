package luckytnt.tnteffects;


import luckytnt.registry.BlockRegistry;
import luckytntlib.util.IExplosiveEntity;
import luckytntlib.util.explosions.IForEachBlockExplosionEffect;
import luckytntlib.util.explosions.ImprovedExplosion;
import luckytntlib.util.tnteffects.PrimedTNTEffect;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.entity.mob.EndermanEntity;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.Level;

public class EndTNTEffect extends PrimedTNTEffect {

	private final int strength;
	
	public EndTNTEffect(int strength) {
		this.strength = strength;
	}
	
	@Override
	public void serverExplosion(IExplosiveEntity entity) {
		ImprovedExplosion explosion = new ImprovedExplosion(entity.getLevel(), (Entity)entity, entity.getPos().x, entity.getPos().y + 0.5f, entity.getPos().z, strength);
		explosion.doEntityExplosion(2f, true);
		explosion.doBlockExplosion(1f, 1f, 1f, 1.5f, false, false);
		ImprovedExplosion endExplosion = new ImprovedExplosion(entity.getLevel(), (Entity)entity, entity.getPos().add(0, 0.5f, 0), Mth.floor(strength * 1.5f));
		endExplosion.doBlockExplosion(1f, 1f, 1f, 1.5f, false, new IForEachBlockExplosionEffect() {
			
			@Override
			public void doBlockExplosion(Level level, BlockPos pos, BlockState state, double distance) {
				if(distance <= 25) {
					if(Math.random() < 0.9f) {
						state.getBlock().onDestroyedByExplosion(level, pos, endExplosion);
						level.setBlockState(pos, Blocks.END_STONE.getDefaultState());
						if(Math.random() < 0.1f) {
							if(level.getBlockState(pos.up()).isAir()) {
								level.setBlockState(pos.up(), Blocks.CHORUS_FLOWER.getDefaultState());
							}
						}
						if(Math.random() < 0.025f) {
							EndermanEntity enderman = EntityTypes.ENDERMAN.create(level);
							enderman.setPosition(new Vec3(pos.getX(), pos.getY() + 1f, pos.getZ()));
							level.addFreshEntity(enderman);
						}
					}
				}
			}
		});
	}
	
	@Override
	public void spawnParticles(IExplosiveEntity entity) {
		entity.getLevel().addParticle(ParticleTypes.END_ROD, entity.x() + 0.5D, entity.y() + 1D, entity.z() + 0.5D, 0, 0, 0);
		entity.getLevel().addParticle(ParticleTypes.END_ROD, entity.x() + 0.5D, entity.y() + 1D, entity.z() - 0.5D, 0, 0, 0);
		entity.getLevel().addParticle(ParticleTypes.END_ROD, entity.x() - 0.5D, entity.y() + 1D, entity.z() + 0.5D, 0, 0, 0);
		entity.getLevel().addParticle(ParticleTypes.END_ROD, entity.x() - 0.5D, entity.y() + 1D, entity.z() - 0.5D, 0, 0, 0);
	}
	
	@Override
	public Block getBlock() {
		return BlockRegistry.END_TNT.get();
	}
	
	@Override
	public int getDefaultFuse(IExplosiveEntity entity) {
		return 160;
	}
}

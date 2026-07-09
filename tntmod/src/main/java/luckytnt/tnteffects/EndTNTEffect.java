package luckytnt.tnteffects;


import luckytnt.registry.BlockRegistry;
import luckytntlib.util.IExplosiveEntity;
import luckytntlib.util.explosions.IForEachBlockExplosionEffect;
import luckytntlib.util.explosions.ImprovedExplosion;
import luckytntlib.util.tnteffects.PrimedTNTEffect;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.mob.EndermanEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

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
		ImprovedExplosion endExplosion = new ImprovedExplosion(entity.getLevel(), (Entity)entity, entity.getPos().add(0, 0.5f, 0), MathHelper.floor(strength * 1.5f));
		endExplosion.doBlockExplosion(1f, 1f, 1f, 1.5f, false, new IForEachBlockExplosionEffect() {
			
			@Override
			public void doBlockExplosion(World level, BlockPos pos, BlockState state, double distance) {
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
							EndermanEntity enderman = EntityType.ENDERMAN.create(level);
							enderman.setPosition(new Vec3d(pos.getX(), pos.getY() + 1f, pos.getZ()));
							level.spawnEntity(enderman);
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

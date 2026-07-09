package luckytnt.tnteffects.projectile;


import org.joml.Vector3f;

import luckytnt.block.UraniumOreBlock;
import luckytnt.registry.ItemRegistry;
import luckytntlib.util.IExplosiveEntity;
import luckytntlib.util.explosions.ExplosionHelper;
import luckytntlib.util.explosions.IForEachBlockExplosionEffect;
import luckytntlib.util.explosions.IForEachEntityExplosionEffect;
import luckytntlib.util.explosions.ImprovedExplosion;
import luckytntlib.util.tnteffects.PrimedTNTEffect;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.damagesource.DamageSources;
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.particles.DustParticleEffect;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

public class DeathRayRayEffect extends PrimedTNTEffect{

	@Override
	public void serverExplosion(IExplosiveEntity entity) {
		ExplosionHelper.doSphericalExplosion(entity.getLevel(), entity.getPos(), 5, new IForEachBlockExplosionEffect() {
			
			@Override
			public void doBlockExplosion(Level level, BlockPos pos, BlockState state, double distance) {
				if(state.getBlock() instanceof UraniumOreBlock) {
					if(Math.random() < 0.4f) {
						ItemEntity antimatter = new ItemEntity(level, pos.getX(), pos.getY(), pos.getZ(), new ItemStack(ItemRegistry.ANTIMATTER.get()));
						level.addFreshEntity(antimatter);
					}
					level.setBlockState(pos, Blocks.AIR.getDefaultState());
				}
				else {
					state.getBlock().onDestroyedByExplosion(level, pos, ImprovedExplosion.dummyExplosion(entity.getLevel()));
					level.setBlockState(pos, Blocks.AIR.getDefaultState(), 3);
				}
			}
		}); 
	}
	
	@Override
	public void explosionTick(IExplosiveEntity entity) {
		ImprovedExplosion explosion = new ImprovedExplosion(entity.getLevel(), (Entity)entity, entity.getPos(), 4);
		explosion.doEntityExplosion(new IForEachEntityExplosionEffect() {
			
			@Override
			public void doEntityExplosion(Entity ent, double distance) {
				if(!ent.equals(entity.owner())) {
					DamageSources sources = ent.level().getDamageSources();
					if(ent instanceof ItemEntity itemEntity) {
						if(!itemEntity.getStack().getItem().equals(ItemRegistry.ANTIMATTER.get())) {
							ent.damage(sources.explosion(explosion), 1);
						}
					}
					else {
						ent.damage(sources.explosion(explosion), 200);
					}
				}
			}
		});
	}
	
	@Override
	public void spawnParticles(IExplosiveEntity entity) {
		entity.getLevel().addParticle(new DustParticleEffect(new Vector3f(0.5f, 0.25f, 0f), 1), entity.x(), entity.y(), entity.z(), 0, 0, 0);
	}
	
	@Override
	public boolean airFuse() {
		return true;
	}
	
	@Override
	public int getDefaultFuse(IExplosiveEntity entity) {
		return 10000;
	}
}

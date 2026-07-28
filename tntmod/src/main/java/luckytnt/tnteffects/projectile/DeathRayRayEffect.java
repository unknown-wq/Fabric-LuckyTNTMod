package luckytnt.tnteffects.projectile;

import net.minecraft.server.level.ServerLevel;


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
import net.minecraft.core.particles.DustParticleOptions;
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
					level.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState());
				}
				else {
					state.getBlock().wasExploded((ServerLevel) level, pos, ImprovedExplosion.dummyExplosion(entity.getLevel()));
					level.setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
				}
			}
		}); 
	}
	
	@Override
	public void explosionTick(IExplosiveEntity entity) {
		if(!(entity.getLevel() instanceof ServerLevel)) {
			return;
		}
		ImprovedExplosion explosion = new ImprovedExplosion(entity.getLevel(), (Entity)entity, entity.getPos(), 4);
		explosion.doEntityExplosion(new IForEachEntityExplosionEffect() {
			
			@Override
			public void doEntityExplosion(Entity ent, double distance) {
				if(!ent.equals(entity.owner())) {
					DamageSources sources = ent.level().damageSources();
					if(ent instanceof ItemEntity itemEntity) {
						if(!itemEntity.getItem().getItem().equals(ItemRegistry.ANTIMATTER.get())) {
							ent.hurtServer((ServerLevel) ent.level(), sources.explosion(explosion), 1);
						}
					}
					else {
						ent.hurtServer((ServerLevel) ent.level(), sources.explosion(explosion), 200);
					}
				}
			}
		});
	}
	
	@Override
	public void spawnParticles(IExplosiveEntity entity) {
		entity.getLevel().addParticle(new DustParticleOptions(((int)(0.5f*255)<<16)|((int)(0.25f*255)<<8)|(int)(0f*255), 1), entity.x(), entity.y(), entity.z(), 0, 0, 0);
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

package luckytnt.tnteffects;


import org.joml.Vector3f;

import luckytnt.registry.BlockRegistry;
import luckytnt.registry.EntityRegistry;
import luckytntlib.entity.PrimedLTNT;
import luckytntlib.util.IExplosiveEntity;
import luckytntlib.util.explosions.IForEachBlockExplosionEffect;
import luckytntlib.util.explosions.ImprovedExplosion;
import luckytntlib.util.tnteffects.PrimedTNTEffect;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class EasterEggEffect extends PrimedTNTEffect{
	
	@Override
	public void baseTick(IExplosiveEntity entity) {
		super.baseTick(entity);
		if(((Entity)entity).isOnGround() && entity.getPersistentData().getInt("level") > 0) {
			serverExplosion(entity);
			World level = entity.getLevel();
			entity.getLevel().playSound((Entity)entity, toBlockPos(entity.getPos()), SoundEvents.ENTITY_GENERIC_EXPLODE.value(), SoundCategory.BLOCKS, 4f, (1f + (level.random.nextFloat() - level.random.nextFloat()) * 0.2f) * 0.7f);
			entity.destroy();
		}
	}
	
	@Override
	public void serverExplosion(IExplosiveEntity entity) {
		int level = entity.getPersistentData().getInt("level");
		ImprovedExplosion explosion = new ImprovedExplosion(entity.getLevel(), (Entity)entity, entity.getPos(), 15);
		explosion.doBlockExplosion(1f, 1f, 1f, 1.25f, false, false);
		explosion.doBlockExplosion(new IForEachBlockExplosionEffect() {		
			@Override
			public void doBlockExplosion(World level, BlockPos pos, BlockState state, double distance) {
				if(Math.random() < 0.66f && !state.isAir()) {
					state.getBlock().onDestroyedByExplosion(level, pos, explosion);
					if(Math.random() < 0.5f) {
						entity.getLevel().setBlockState(pos, Blocks.MELON.getDefaultState());
					}
					else {
						entity.getLevel().setBlockState(pos, Blocks.PUMPKIN.getDefaultState());
					}
				}
			}
		});
		if(level + 1 == 4) {
			entity.destroy();
		}
		else {
			for(int count = 0; count < 4; count++) {
				PrimedLTNT tnt = EntityRegistry.EASTER_EGG.get().create(entity.getLevel());
				tnt.setPosition(entity.getPos());
				tnt.setOwner(entity.owner());
				tnt.setVelocity(Math.random() * 2 - 1, 1 + Math.random(), Math.random() * 2 - 1);
				NbtCompound tag = tnt.getPersistentData();
				tag.putInt("level", level + 1);
				tnt.setPersistentData(tag);
				entity.getLevel().spawnEntity(tnt);
			}
		}
	}
	
	@Override
	public void spawnParticles(IExplosiveEntity entity) {
		entity.getLevel().addParticle(new DustParticleEffect(new Vector3f(0f, 0.5f, 0f), 1), entity.x(), entity.y() + 1f, entity.z(), 0, 0, 0);
		entity.getLevel().addParticle(new DustParticleEffect(new Vector3f(1f, 0.5f, 0f), 1), entity.x() + 0.2f, entity.y() + 1f, entity.z() + 0.2f, 0, 0, 0);
		entity.getLevel().addParticle(new DustParticleEffect(new Vector3f(1f, 0.5f, 0f), 1), entity.x() - 0.2f, entity.y() + 1f, entity.z() - 0.2f, 0, 0, 0);
		entity.getLevel().addParticle(new DustParticleEffect(new Vector3f(1f, 0.5f, 0f), 1), entity.x() + 0.2f, entity.y() + 1f, entity.z() - 0.2f, 0, 0, 0);
		entity.getLevel().addParticle(new DustParticleEffect(new Vector3f(1f, 0.5f, 0f), 1), entity.x() - 0.2f, entity.y() + 1f, entity.z() + 0.2f, 0, 0, 0);
	}
	
	@Override
	public Block getBlock() {
		return BlockRegistry.EASTER_EGG.get();
	}
	
	@Override
	public int getDefaultFuse(IExplosiveEntity entity) {
		return 120;
	}
}

package luckytnt.tnteffects;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import luckytnt.registry.BlockRegistry;
import luckytntlib.util.IExplosiveEntity;
import luckytntlib.util.explosions.IForEachBlockExplosionEffect;
import luckytntlib.util.explosions.ImprovedExplosion;
import luckytntlib.util.tnteffects.PrimedTNTEffect;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.FallingBlockEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class PhysicsTNTEffect extends PrimedTNTEffect{

	private final int strength;
	
	public PhysicsTNTEffect(int strength) {
		this.strength = strength;
	}
	
	@Override
	public void serverExplosion(IExplosiveEntity entity) {
		ImprovedExplosion explosion = new ImprovedExplosion(entity.getLevel(), (Entity)entity, entity.getPos(), strength);
		explosion.doBlockExplosion(new IForEachBlockExplosionEffect() {
			
			@Override
			public void doBlockExplosion(World level, BlockPos pos, BlockState state, double distance) {
				if(state.getBlock().getBlastResistance() < 100) {
					level.setBlockState(pos, Blocks.AIR.getDefaultState());
					@SuppressWarnings("rawtypes")
					Class[] parameters = new Class[]{World.class, double.class, double.class, double.class, BlockState.class};
					Constructor<FallingBlockEntity> sandConstructor;
					try {
						sandConstructor = FallingBlockEntity.class.getDeclaredConstructor(parameters);
						sandConstructor.setAccessible(true);
						try {
							FallingBlockEntity sand = sandConstructor.newInstance(level, pos.getX() + 0.5f, pos.getY(), pos.getZ() + 0.5f, state);
							sand.setVelocity(Math.random() * 2f - 1f, 0.5f + Math.random() * 2, Math.random() * 2f - 1f);
							level.spawnEntity(sand);
						} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
							e.printStackTrace();
						}
					} catch (NoSuchMethodException | SecurityException e) {
						e.printStackTrace();
					}
				}
			}
		});
	}
	
	@Override
	public Block getBlock() {
		return BlockRegistry.PHYSICS_TNT.get();
	}
}

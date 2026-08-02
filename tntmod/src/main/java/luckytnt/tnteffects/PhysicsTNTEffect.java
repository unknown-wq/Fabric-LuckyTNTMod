package luckytnt.tnteffects;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import luckytnt.registry.BlockRegistry;
import luckytntlib.util.IExplosiveEntity;
import luckytntlib.util.explosions.IForEachBlockExplosionEffect;
import luckytntlib.util.explosions.ImprovedExplosion;
import luckytntlib.util.tnteffects.PrimedTNTEffect;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

public class PhysicsTNTEffect extends PrimedTNTEffect{

	/**
	 * This was resolved once per destroyed block. Class#getDeclaredConstructor is not cached by the
	 * JDK: it copies the declared constructor array, runs a setAccessible check and allocates the
	 * Class[] argument every single call.
	 * <p>
	 * The reflective private constructor is kept on purpose: FallingBlockEntity#fall would also
	 * overwrite the source block and start the entity at time = 1, which changes the behavior here
	 * (the block is already cleared by hand and the entity is expected to start at time = 0).
	 */
	private static final Constructor<FallingBlockEntity> FALLING_BLOCK_CONSTRUCTOR = resolveFallingBlockConstructor();

	private static Constructor<FallingBlockEntity> resolveFallingBlockConstructor() {
		try {
			Constructor<FallingBlockEntity> constructor = FallingBlockEntity.class.getDeclaredConstructor(Level.class, double.class, double.class, double.class, BlockState.class);
			constructor.setAccessible(true);
			return constructor;
		} catch (NoSuchMethodException | SecurityException e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Stateless, so it is allocated once instead of once per explosion.
	 */
	private static final IForEachBlockExplosionEffect TO_FALLING_BLOCK = new IForEachBlockExplosionEffect() {

		@Override
		public void doBlockExplosion(Level level, BlockPos pos, BlockState state, double distance) {
			if(state.getBlock().getExplosionResistance() < 100) {
				level.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState());
				if(FALLING_BLOCK_CONSTRUCTOR == null) {
					return;
				}
				try {
					FallingBlockEntity sand = FALLING_BLOCK_CONSTRUCTOR.newInstance(level, pos.getX() + 0.5f, pos.getY(), pos.getZ() + 0.5f, state);
					sand.setDeltaMovement(Math.random() * 2f - 1f, 0.5f + Math.random() * 2, Math.random() * 2f - 1f);
					level.addFreshEntity(sand);
				} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
					e.printStackTrace();
				}
			}
		}
	};

	private final int strength;

	public PhysicsTNTEffect(int strength) {
		this.strength = strength;
	}

	@Override
	public void serverExplosion(IExplosiveEntity entity) {
		ImprovedExplosion explosion = new ImprovedExplosion(entity.getLevel(), (Entity)entity, entity.getPos(), strength);
		explosion.doBlockExplosion(TO_FALLING_BLOCK);
	}

	@Override
	public Block getBlock() {
		return BlockRegistry.PHYSICS_TNT.get();
	}
}

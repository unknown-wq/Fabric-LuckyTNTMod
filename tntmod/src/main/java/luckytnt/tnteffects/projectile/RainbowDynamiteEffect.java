package luckytnt.tnteffects.projectile;

import java.lang.reflect.Constructor;

import luckytnt.registry.ItemRegistry;
import luckytntlib.util.IExplosiveEntity;
import luckytntlib.util.tnteffects.PrimedTNTEffect;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.item.Item;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;

public class RainbowDynamiteEffect extends PrimedTNTEffect{

	/**
	 * {@link FallingBlockEntity}'s (Level, double, double, double, BlockState) constructor is private and the public
	 * {@link FallingBlockEntity#fall(Level, net.minecraft.core.BlockPos, BlockState)} is not a substitute: it also
	 * clears the block at the target position and snaps the entity to the block grid. So reflection stays, but the
	 * lookup is resolved exactly once instead of five times per tick - {@code getDeclaredConstructor} is not cached by
	 * the JDK and copies the whole constructor array on every call.
	 */
	private static final Constructor<FallingBlockEntity> FALLING_BLOCK_CONSTRUCTOR;

	private static final BlockState[] CONCRETE_COLORS;

	static {
		Constructor<FallingBlockEntity> constructor = null;
		try {
			constructor = FallingBlockEntity.class.getDeclaredConstructor(Level.class, double.class, double.class, double.class, BlockState.class);
			constructor.setAccessible(true);
		} catch (NoSuchMethodException | SecurityException e) {
			e.printStackTrace();
		}
		FALLING_BLOCK_CONSTRUCTOR = constructor;

		CONCRETE_COLORS = new BlockState[] {
			Blocks.CONCRETE.red().defaultBlockState(),
			Blocks.CONCRETE.green().defaultBlockState(),
			Blocks.CONCRETE.blue().defaultBlockState(),
			Blocks.CONCRETE.yellow().defaultBlockState(),
			Blocks.CONCRETE.brown().defaultBlockState(),
			Blocks.CONCRETE.cyan().defaultBlockState(),
			Blocks.CONCRETE.lime().defaultBlockState(),
			Blocks.CONCRETE.purple().defaultBlockState(),
			Blocks.CONCRETE.pink().defaultBlockState(),
			Blocks.CONCRETE.magenta().defaultBlockState(),
			Blocks.CONCRETE.orange().defaultBlockState(),
			Blocks.CONCRETE.lightBlue().defaultBlockState()
		};
	}

	@Override
	public void serverExplosion(IExplosiveEntity entity) {
		RandomSource random = entity.getLevel().getRandom();
		for(int count = 0; count < 101; count++) {
			spawnConcrete(entity, random);
		}
	}

	@Override
	public void explosionTick(IExplosiveEntity entity) {
		((Entity)entity).setDeltaMovement(((Entity)entity).getDeltaMovement().add(0f, 0.08f, 0f));

		if(!(entity.getLevel() instanceof ServerLevel)) {
			return;
		}

		RandomSource random = entity.getLevel().getRandom();
		for(int count = 0; count < 5; count++) {
			spawnConcrete(entity, random);
		}
	}

	private void spawnConcrete(IExplosiveEntity entity, RandomSource random) {
		if(FALLING_BLOCK_CONSTRUCTOR == null) {
			return;
		}
		try {
			BlockState state = CONCRETE_COLORS[random.nextInt(CONCRETE_COLORS.length)];
			FallingBlockEntity sand = FALLING_BLOCK_CONSTRUCTOR.newInstance(entity.getLevel(), entity.getPos().x, entity.getPos().y, entity.getPos().z, state);
			sand.setDeltaMovement((random.nextDouble() - random.nextDouble()) * 1.5f, (random.nextDouble() - random.nextDouble()) * 1.5f, (random.nextDouble() - random.nextDouble()) * 1.5f);
			entity.getLevel().addFreshEntity(sand);
		} catch (ReflectiveOperationException | IllegalArgumentException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void spawnParticles(IExplosiveEntity entity) {
		entity.getLevel().addParticle(new DustParticleOptions(((int)(10f*255)<<16)|((int)(10f*255)<<8)|(int)(10f*255), 1f), entity.x(), entity.y(), entity.z(), 0, 0, 0);
	}

	@Override
	public boolean airFuse() {
		return true;
	}

	@Override
	public Item getItem() {
		return ItemRegistry.RAINBOW_DYNAMITE.get();
	}

	@Override
	public int getDefaultFuse(IExplosiveEntity entity) {
		return 40;
	}
}

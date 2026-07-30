package luckytnt.tnteffects;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import org.joml.Vector3f;

import luckytnt.registry.BlockRegistry;
import luckytntlib.util.IExplosiveEntity;
import luckytntlib.util.tnteffects.PrimedTNTEffect;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;

public class RainbowFireworkEffect extends PrimedTNTEffect{

	@Override
	public void baseTick(IExplosiveEntity entity) {
		super.baseTick(entity);
		((Entity)entity).setDeltaMovement(((Entity)entity).getDeltaMovement().x, 0.8f, ((Entity)entity).getDeltaMovement().z);
	}
	
	@Override
	public void serverExplosion(IExplosiveEntity entity) {
		// Class#getDeclaredConstructor is not cached by the JDK: it walks the declared constructor array
		// and hands back a defensive copy, plus a Class[] allocation and a setAccessible check. It used to
		// run once per iteration, i.e. 301 times in a single tick (GrandeFinaleEffect:33 hoists the same
		// lookup). `new Random()` was allocated per iteration too; one RandomSource covers the whole burst.
		final RandomSource random = entity.getLevel().getRandom();
		Constructor<FallingBlockEntity> sandConstructor = null;
		try {
			sandConstructor = FallingBlockEntity.class.getDeclaredConstructor(Level.class, double.class, double.class, double.class, BlockState.class);
			sandConstructor.setAccessible(true);
		} catch (NoSuchMethodException | SecurityException e) {
			e.printStackTrace();
			return;
		}
		for(int count = 0; count <= 300; count++) {
			try {
				BlockState state = Blocks.CONCRETE.white().defaultBlockState();
				int rand = random.nextInt(12);
				switch (rand) {
					case 0: state = Blocks.CONCRETE.red().defaultBlockState(); break;
					case 1: state = Blocks.CONCRETE.green().defaultBlockState(); break;
					case 2: state = Blocks.CONCRETE.blue().defaultBlockState(); break;
					case 3: state = Blocks.CONCRETE.yellow().defaultBlockState(); break;
					case 4: state = Blocks.CONCRETE.brown().defaultBlockState(); break;
					case 5: state = Blocks.CONCRETE.cyan().defaultBlockState(); break;
					case 6: state = Blocks.CONCRETE.lime().defaultBlockState(); break;
					case 7: state = Blocks.CONCRETE.purple().defaultBlockState(); break;
					case 8: state = Blocks.CONCRETE.pink().defaultBlockState(); break;
					case 9: state = Blocks.CONCRETE.magenta().defaultBlockState(); break;
					case 10: state = Blocks.CONCRETE.orange().defaultBlockState(); break;
					case 11: state = Blocks.CONCRETE.lightBlue().defaultBlockState(); break;
				}
				FallingBlockEntity sand = sandConstructor.newInstance(entity.getLevel(), entity.getPos().x, entity.getPos().y, entity.getPos().z, state);
				sand.setDeltaMovement((Math.random() - Math.random()) * 1.5f, (Math.random() - Math.random()) * 1.5f, (Math.random() - Math.random()) * 1.5f);
				entity.getLevel().addFreshEntity(sand);
			} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
				e.printStackTrace();
			}
		}
	}
	
	@Override
	public void spawnParticles(IExplosiveEntity entity) {
		entity.getLevel().addParticle(new DustParticleOptions(((int)(10f*255)<<16)|((int)(10f*255)<<8)|(int)(10f*255), 1f), entity.x(), entity.y() + 1f, entity.z(), 0, 0, 0);
	}
	
	@Override
	public Block getBlock() {
		return BlockRegistry.RAINBOW_FIREWORK.get();
	}
	
	@Override
	public int getDefaultFuse(IExplosiveEntity entity) {
		return 40;
	}
}

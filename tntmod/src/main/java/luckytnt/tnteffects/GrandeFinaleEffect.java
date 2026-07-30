package luckytnt.tnteffects;

import net.minecraft.world.entity.EntitySpawnReason;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import luckytnt.registry.BlockRegistry;
import luckytnt.registry.EntityRegistry;
import luckytntlib.entity.PrimedLTNT;
import luckytntlib.util.IExplosiveEntity;
import luckytntlib.util.tnteffects.PrimedTNTEffect;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;

public class GrandeFinaleEffect extends PrimedTNTEffect {

	/**
	 * Class#getDeclaredConstructor is not cached by the JDK: every call walks the declared
	 * constructor array and hands back a defensive copy, plus a setAccessible check and a Class[]
	 * allocation. This used to happen 1000 times in a single tick.
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

	@Override
	public void serverExplosion(IExplosiveEntity ent) {

	}

	@Override
	public void explosionTick(IExplosiveEntity ent) {
		Level level = ent.getLevel();
		RandomSource rng = level.getRandom();
		// explosionTick runs on both logical sides and addFreshEntity is a no-op on the client, so every
		// entity below (1000 of them through a reflective newInstance that cannot be inlined) used to be
		// built and thrown away a second time in single player. The delta movement and the particle still
		// have to run client side, so the guard is per block rather than at the top of the method.
		final boolean server = level instanceof ServerLevel;
		if(server && ent.getTNTFuse() % (int)(1 + Math.random() * 50) == 0) {
			PrimedLTNT entity = EntityRegistry.SAND_FIREWORK.get().create(level, EntitySpawnReason.MOB_SUMMONED);
			int random = rng.nextInt(4);
			switch(random){
				case 0: entity = EntityRegistry.SAND_FIREWORK.get().create(level, EntitySpawnReason.MOB_SUMMONED); break;
				case 1: entity = EntityRegistry.GRAVEL_FIREWORK.get().create(level, EntitySpawnReason.MOB_SUMMONED); break;
				case 2: entity = EntityRegistry.RAINBOW_FIREWORK.get().create(level, EntitySpawnReason.MOB_SUMMONED);; break;
				case 3: entity = EntityRegistry.NEW_YEARS_FIREWORK.get().create(level, EntitySpawnReason.MOB_SUMMONED);
						CompoundTag tag = entity.getPersistentData();
						tag.putInt("type", 1);
						entity.setPersistentData(tag); break;
			}
			level.playSound(null, toBlockPos(ent.getPos()), SoundEvents.FIREWORK_ROCKET_LAUNCH, SoundSource.MASTER, 3, 1);
			entity.setPos(ent.getPos());
			entity.setOwner(ent.owner());
			entity.setDeltaMovement(Math.random() * 5 - Math.random() * 5, 0, Math.random() * 5 - Math.random() * 5);
			entity.setTNTFuse(40 + rng.nextInt(41));
			level.addFreshEntity(entity);
		}
		if(server) {
			// These two positions are cleared once (when the TNT is primed) and are air for the remaining
			// 439 ticks, but the writes ran unconditionally: 880 flag-3 setBlock calls - each a six way
			// neighbour cascade plus a client packet - per detonation. Two getBlockState reads per tick
			// replace them; the actual writes still use flag 3 so anything resting on the cleared block
			// keeps updating.
			BlockPos self = toBlockPos(ent.getPos());
			if(!level.getBlockState(self).isAir()) {
				level.setBlock(self, Blocks.AIR.defaultBlockState(), 3);
			}
			BlockPos above = self.above();
			if(!level.getBlockState(above).isAir()) {
				level.setBlock(above, Blocks.AIR.defaultBlockState(), 3);
			}
		}
		if(ent.getTNTFuse() <= 40) {
			((Entity)ent).setDeltaMovement(((Entity)ent).getDeltaMovement().x, 1.6f, ((Entity)ent).getDeltaMovement().z);
			level.addParticle(ParticleTypes.LARGE_SMOKE, ent.x(), ent.y(), ent.z(), 0, -0.5f, 0);
			if(server && ent.getTNTFuse() == 0) {
				BlockState[] colors = concreteStates();
				for(int count = 0; count < 1000; count++) {
					BlockState template = colors[rng.nextInt(colors.length)];
					FallingBlockEntity block = null;
					if(FALLING_BLOCK_CONSTRUCTOR != null) {
						try {
							block = FALLING_BLOCK_CONSTRUCTOR.newInstance(level, ent.x(), ent.y(), ent.z(), template);
						} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
							e.printStackTrace();
						}
					}
					if(block != null) {
						block.dropItem = false;
						block.setDeltaMovement(Math.random() * 5f - Math.random() * 5f, Math.random() * 5f - Math.random() * 5f, Math.random() * 5f - Math.random() * 5f);
						level.addFreshEntity(block);
					}
				}
				for(int count = 0; count < 500; count++) {
					PrimedLTNT tnt = EntityRegistry.TNT.get().create(level, EntitySpawnReason.MOB_SUMMONED);
					tnt.setOwner(ent.owner());
					tnt.setPos(ent.getPos());
					tnt.setTNTFuse(80 + (int)(Math.random() * 100));
					tnt.setDeltaMovement(Math.random() * 5f - Math.random() * 5f, Math.random() * 5f - Math.random() * 5f, Math.random() * 5f - Math.random() * 5f);
					level.addFreshEntity(tnt);
				}
			}
		}
	}
	
	/**
	 * Blocks.CONCRETE.pick(DyeColor) was called once per iteration (1000 times) even though the
	 * 12 possible results never change. Resolved lazily once, not in a static initializer, so the
	 * class does not depend on Blocks being bootstrapped at class load time.
	 */
	private static BlockState[] concreteColors;

	private static BlockState[] concreteStates() {
		if(concreteColors == null) {
			concreteColors = new BlockState[] {
				Blocks.CONCRETE.pick(DyeColor.RED).defaultBlockState(),
				Blocks.CONCRETE.pick(DyeColor.GREEN).defaultBlockState(),
				Blocks.CONCRETE.pick(DyeColor.BLUE).defaultBlockState(),
				Blocks.CONCRETE.pick(DyeColor.YELLOW).defaultBlockState(),
				Blocks.CONCRETE.pick(DyeColor.BROWN).defaultBlockState(),
				Blocks.CONCRETE.pick(DyeColor.CYAN).defaultBlockState(),
				Blocks.CONCRETE.pick(DyeColor.LIME).defaultBlockState(),
				Blocks.CONCRETE.pick(DyeColor.PURPLE).defaultBlockState(),
				Blocks.CONCRETE.pick(DyeColor.PINK).defaultBlockState(),
				Blocks.CONCRETE.pick(DyeColor.MAGENTA).defaultBlockState(),
				Blocks.CONCRETE.pick(DyeColor.ORANGE).defaultBlockState(),
				Blocks.CONCRETE.pick(DyeColor.LIGHT_BLUE).defaultBlockState()
			};
		}
		return concreteColors;
	}

	@Override
	public Block getBlock() {
		return BlockRegistry.GRANDE_FINALE.get();
	}
	
	@Override
	public int getDefaultFuse(IExplosiveEntity ent) {
		return 440;
	}
}

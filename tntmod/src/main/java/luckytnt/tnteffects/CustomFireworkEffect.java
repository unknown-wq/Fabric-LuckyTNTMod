package luckytnt.tnteffects;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

import luckytnt.entity.PrimedCustomFirework;
import luckytnt.registry.BlockRegistry;
import luckytntlib.block.LTNTBlock;
import luckytntlib.util.IExplosiveEntity;
import luckytntlib.util.tnteffects.PrimedTNTEffect;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.TntBlock;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.entity.item.PrimedTnt;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.Level;

public class CustomFireworkEffect extends PrimedTNTEffect {

	/**
	 * getDeclaredConstructor is not cached by the JDK (defensive copy of the declared constructor
	 * array plus a Class[] allocation per call), and this ran 200 times per explosion.
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
	public void explosionTick(IExplosiveEntity ent) {
		if(ent.getTNTFuse() == 40 && ent instanceof PrimedCustomFirework tnt) {
			BlockPos pos = toBlockPos(new Vec3(ent.x(), ent.y() - 1f, ent.z()));
			CompoundTag tag = ent.getPersistentData();
			tag.putInt("x", pos.getX());
			tag.putInt("y", pos.getY());
			tag.putInt("z", pos.getZ());
			ent.setPersistentData(tag);
			tnt.state = ent.getLevel().getBlockState(pos);
		}
		((Entity)ent).setDeltaMovement(((Entity)ent).getDeltaMovement().x, 0.8f, ((Entity)ent).getDeltaMovement().z);
	}
	
	@Override
	public void serverExplosion(IExplosiveEntity ent) {
		if(ent instanceof PrimedCustomFirework fire) {
			BlockState state = ent.getLevel().getBlockState(new BlockPos(ent.getPersistentData().getIntOr("x", 0), ent.getPersistentData().getIntOr("y", 0), ent.getPersistentData().getIntOr("z", 0)));
			if(fire.state != null) {
				state = fire.state;
			}
			
			for(int count = 0; count < 200; count++) {
				if(state.getBlock() instanceof TntBlock tnt) {
					if(tnt instanceof LTNTBlock ltnt) {
						ltnt.explode(ent.getLevel(), false, ent.getPos().x, ent.getPos().y, ent.getPos().z, ent.owner());
					} else {
						TntBlock.prime(ent.getLevel(), toBlockPos(ent.getPos()));
					}
				} else if(FALLING_BLOCK_CONSTRUCTOR != null) {
					try {
						FallingBlockEntity sand = FALLING_BLOCK_CONSTRUCTOR.newInstance(ent.getLevel(), ent.getPos().x, ent.getPos().y, ent.getPos().z, state);
						sand.setDeltaMovement(Math.random() * 1.5f - Math.random() * 1.5f, Math.random() * 1.5f - Math.random() * 1.5f, Math.random() * 1.5f - Math.random() * 1.5f);
						ent.getLevel().addFreshEntity(sand);
					} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
						e.printStackTrace();
					}
				}
			}
			BlockPos min = toBlockPos(ent.getPos()).offset(2, 2, 2);
			BlockPos max = toBlockPos(ent.getPos()).offset(-2, -2, -2);
			List<PrimedTnt> tnts = ent.getLevel().getEntitiesOfClass(PrimedTnt.class, new AABB(min.getX(), min.getY(), min.getZ(), max.getX(), max.getY(), max.getZ()));
			for(PrimedTnt tnt : tnts) {
				tnt.setDeltaMovement(Math.random() * 1.5f - Math.random() * 1.5f, Math.random() * 1.5f - Math.random() * 1.5f, Math.random() * 1.5f - Math.random() * 1.5f);
			}
		}
	}
	
	@Override
	public void spawnParticles(IExplosiveEntity ent) {
		ent.getLevel().addParticle(ParticleTypes.FLAME, ent.x(), ent.y(), ent.z(), 0, 0, 0);
	}
	
	@Override
	public Block getBlock() {
		return BlockRegistry.CUSTOM_FIREWORK.get();
	}
	
	@Override
	public int getDefaultFuse(IExplosiveEntity ent) {
		return 40;
	}
}

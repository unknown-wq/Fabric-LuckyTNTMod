package luckytnt.tnteffects;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

import luckytnt.entity.PrimedCustomFirework;
import luckytnt.registry.BlockRegistry;
import luckytntlib.block.LTNTBlock;
import luckytntlib.util.IExplosiveEntity;
import luckytntlib.util.tnteffects.PrimedTNTEffect;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.TntBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.FallingBlockEntity;
import net.minecraft.entity.TntEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class CustomFireworkEffect extends PrimedTNTEffect {

	@Override
	public void explosionTick(IExplosiveEntity ent) {
		if(ent.getTNTFuse() == 40 && ent instanceof PrimedCustomFirework tnt) {
			BlockPos pos = toBlockPos(new Vec3d(ent.x(), ent.y() - 1f, ent.z()));
			NbtCompound tag = ent.getPersistentData();
			tag.putInt("x", pos.getX());
			tag.putInt("y", pos.getY());
			tag.putInt("z", pos.getZ());
			ent.setPersistentData(tag);
			tnt.state = ent.getLevel().getBlockState(pos);
		}
		((Entity)ent).setVelocity(((Entity)ent).getVelocity().x, 0.8f, ((Entity)ent).getVelocity().z);
	}
	
	@Override
	public void serverExplosion(IExplosiveEntity ent) {
		if(ent instanceof PrimedCustomFirework fire) {
			BlockState state = ent.getLevel().getBlockState(new BlockPos(ent.getPersistentData().getInt("x"), ent.getPersistentData().getInt("y"), ent.getPersistentData().getInt("z")));
			if(fire.state != null) {
				state = fire.state;
			}
			
			for(int count = 0; count < 200; count++) {
				if(state.getBlock() instanceof TntBlock tnt) {
					if(tnt instanceof LTNTBlock ltnt) {
						ltnt.explode(ent.getLevel(), false, ent.getPos().x, ent.getPos().y, ent.getPos().z, ent.owner());
					} else {
						TntBlock.primeTnt(ent.getLevel(), toBlockPos(ent.getPos()));
					}
				} else {
					try {
						@SuppressWarnings("rawtypes")
						Class[] parameters = new Class[]{World.class, double.class, double.class, double.class, BlockState.class};
						Constructor<FallingBlockEntity> sandConstructor = FallingBlockEntity.class.getDeclaredConstructor(parameters);
						sandConstructor.setAccessible(true);
						FallingBlockEntity sand = sandConstructor.newInstance(ent.getLevel(), ent.getPos().x, ent.getPos().y, ent.getPos().z, state);
						sand.setVelocity(Math.random() * 1.5f - Math.random() * 1.5f, Math.random() * 1.5f - Math.random() * 1.5f, Math.random() * 1.5f - Math.random() * 1.5f);
						ent.getLevel().spawnEntity(sand);
					} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException e) {
						e.printStackTrace();
					}
				}
			}
			BlockPos min = toBlockPos(ent.getPos()).add(2, 2, 2);
			BlockPos max = toBlockPos(ent.getPos()).add(-2, -2, -2);
			List<TntEntity> tnts = ent.getLevel().getNonSpectatingEntities(TntEntity.class, new Box(min.getX(), min.getY(), min.getZ(), max.getX(), max.getY(), max.getZ()));
			for(TntEntity tnt : tnts) {
				tnt.setVelocity(Math.random() * 1.5f - Math.random() * 1.5f, Math.random() * 1.5f - Math.random() * 1.5f, Math.random() * 1.5f - Math.random() * 1.5f);
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

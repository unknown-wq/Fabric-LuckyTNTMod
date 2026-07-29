package luckytnt.tnteffects;

import java.util.List;

import org.joml.Vector3f;

import luckytnt.registry.BlockRegistry;
import luckytnt.registry.EffectRegistry;
import luckytntlib.util.IExplosiveEntity;
import luckytntlib.util.tnteffects.PrimedTNTEffect;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.level.Level;

public class MidasTNTEffect extends PrimedTNTEffect {

	@Override
	public void explosionTick(IExplosiveEntity ent) {
		if(ent.getTNTFuse() < 80 && ent.getTNTFuse() % 2 == 0 && !ent.getLevel().isClientSide()) {
			Level level = ent.getLevel();
			BlockState gold = Blocks.GOLD_BLOCK.defaultBlockState();
			int radius = ent.getPersistentData().getIntOr("size", 0);

			// The radius grows 1 block per firing and every previous firing already converted
			// everything within radius - 1, so only the newly reached shell has to be read/written.
			// Same end result, ~20x fewer getBlockState calls over the 40 firings.
			int radiusSq = radius * radius;
			int innerSq = radius == 0 ? -1 : (radius - 1) * (radius - 1);
			int centerX = Mth.floor(ent.x());
			int centerY = Mth.floor(ent.y());
			int centerZ = Mth.floor(ent.z());
			BlockPos.MutableBlockPos mutable = new BlockPos.MutableBlockPos();

			for(int offX = -radius; offX <= radius; offX++) {
				for(int offY = radius; offY >= -radius; offY--) {
					int xy = offX * offX + offY * offY;
					if(xy > radiusSq) {
						continue;
					}
					for(int offZ = -radius; offZ <= radius; offZ++) {
						int distanceSq = xy + offZ * offZ;
						if(distanceSq > radiusSq || distanceSq <= innerSq) {
							continue;
						}
						mutable.set(centerX + offX, centerY + offY, centerZ + offZ);
						BlockState state = level.getBlockState(mutable);
						if(state.getBlock().getExplosionResistance() < 100 && !state.isAir() && state.getBlock() != Blocks.GOLD_BLOCK) {
							level.setBlock(mutable.immutable(), gold, 3);
						}
					}
				}
			}

			CompoundTag tag = ent.getPersistentData();
			tag.putInt("size", radius + 1);
			ent.setPersistentData(tag);

			int i = radius + 1;
			List<LivingEntity> list = level.getEntitiesOfClass(LivingEntity.class, new AABB(centerX - i, centerY - i, centerZ - i, centerX + i, centerY + i, centerZ + i));
			for(LivingEntity lent : list) {
				lent.addEffect(new MobEffectInstance(BuiltInRegistries.MOB_EFFECT.getOrThrow(EffectRegistry.MIDAS_TOUCH), 2000, 0));
			}
		}
	}
	
	@Override
	public void spawnParticles(IExplosiveEntity ent) {
		ent.getLevel().addParticle(new DustParticleOptions(((int)(1f*255)<<16)|((int)(1f*255)<<8)|(int)(0.4f*255), 1f), ent.x(), ent.y() + 1f, ent.z(), 0, 0, 0);
	}
	
	@Override
	public Block getBlock() {
		return BlockRegistry.MIDAS_TNT.get();
	}
	
	@Override
	public int getDefaultFuse(IExplosiveEntity ent) {
		return 160;
	}
}

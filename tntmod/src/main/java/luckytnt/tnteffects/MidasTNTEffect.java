package luckytnt.tnteffects;

import java.util.List;

import org.joml.Vector3f;

import luckytnt.registry.BlockRegistry;
import luckytnt.registry.EffectRegistry;
import luckytntlib.util.IExplosiveEntity;
import luckytntlib.util.explosions.ExplosionHelper;
import luckytntlib.util.explosions.IForEachBlockExplosionEffect;
import luckytntlib.util.tnteffects.PrimedTNTEffect;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.core.particles.DustParticleEffect;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.level.Level;

public class MidasTNTEffect extends PrimedTNTEffect {

	@Override
	public void explosionTick(IExplosiveEntity ent) {
		if(ent.getTNTFuse() < 80 && ent.getTNTFuse() % 2 == 0 && !ent.getLevel().isClient()) {
			ExplosionHelper.doSphericalExplosion(ent.getLevel(), ent.getPos(), ent.getPersistentData().getInt("size"), new IForEachBlockExplosionEffect() {
				
				@Override
				public void doBlockExplosion(Level level, BlockPos pos, BlockState state, double distance) {
					if(state.getBlock().getBlastResistance() < 100 && !state.isAir() && state.getBlock() != Blocks.GOLD_BLOCK) {
						level.setBlockState(pos, Blocks.GOLD_BLOCK.getDefaultState(), 3);
					}
				}
			});
			
			CompoundTag tag = ent.getPersistentData();
			tag.putInt("size", ent.getPersistentData().getInt("size") + 1);
			ent.setPersistentData(tag);
			
			int i = ent.getPersistentData().getInt("size");
			BlockPos min = toBlockPos(ent.getPos()).add(-i, -i, -i);
			BlockPos max = toBlockPos(ent.getPos()).add(i, i, i);
			List<LivingEntity> list = ent.getLevel().getNonSpectatingEntities(LivingEntity.class, new Box(min.getX(), min.getY(), min.getZ(), max.getX(), max.getY(), max.getZ()));
			for(LivingEntity lent : list) {
				lent.addStatusEffect(new MobEffectInstance(BuiltInRegistries.STATUS_EFFECT.entryOf(EffectRegistry.MIDAS_TOUCH), 2000, 0));
			}
		}
	}
	
	@Override
	public void spawnParticles(IExplosiveEntity ent) {
		ent.getLevel().addParticle(new DustParticleEffect(new Vector3f(1f, 1f, 0.4f), 1f), ent.x(), ent.y() + 1f, ent.z(), 0, 0, 0);
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

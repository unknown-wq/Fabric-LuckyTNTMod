package luckytnt.tnteffects;

import java.util.List;

import org.joml.Vector3f;

import luckytnt.registry.BlockRegistry;
import luckytnt.registry.EffectRegistry;
import luckytntlib.util.IExplosiveEntity;
import luckytntlib.util.explosions.ExplosionHelper;
import luckytntlib.util.explosions.IForEachBlockExplosionEffect;
import luckytntlib.util.tnteffects.PrimedTNTEffect;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.registry.Registries;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;

public class MidasTNTEffect extends PrimedTNTEffect {

	@Override
	public void explosionTick(IExplosiveEntity ent) {
		if(ent.getTNTFuse() < 80 && ent.getTNTFuse() % 2 == 0 && !ent.getLevel().isClient()) {
			ExplosionHelper.doSphericalExplosion(ent.getLevel(), ent.getPos(), ent.getPersistentData().getInt("size"), new IForEachBlockExplosionEffect() {
				
				@Override
				public void doBlockExplosion(World level, BlockPos pos, BlockState state, double distance) {
					if(state.getBlock().getBlastResistance() < 100 && !state.isAir() && state.getBlock() != Blocks.GOLD_BLOCK) {
						level.setBlockState(pos, Blocks.GOLD_BLOCK.getDefaultState(), 3);
					}
				}
			});
			
			NbtCompound tag = ent.getPersistentData();
			tag.putInt("size", ent.getPersistentData().getInt("size") + 1);
			ent.setPersistentData(tag);
			
			int i = ent.getPersistentData().getInt("size");
			BlockPos min = toBlockPos(ent.getPos()).add(-i, -i, -i);
			BlockPos max = toBlockPos(ent.getPos()).add(i, i, i);
			List<LivingEntity> list = ent.getLevel().getNonSpectatingEntities(LivingEntity.class, new Box(min.getX(), min.getY(), min.getZ(), max.getX(), max.getY(), max.getZ()));
			for(LivingEntity lent : list) {
				lent.addStatusEffect(new StatusEffectInstance(Registries.STATUS_EFFECT.entryOf(EffectRegistry.MIDAS_TOUCH), 2000, 0));
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

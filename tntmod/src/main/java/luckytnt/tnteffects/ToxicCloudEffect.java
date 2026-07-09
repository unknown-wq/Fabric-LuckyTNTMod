package luckytnt.tnteffects;

import java.util.List;

import org.joml.Math;
import org.joml.Vector3f;

import luckytntlib.util.IExplosiveEntity;
import luckytntlib.util.explosions.ImprovedExplosion;
import luckytntlib.util.tnteffects.PrimedTNTEffect;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.core.particles.DustParticleOptions;

public class ToxicCloudEffect extends PrimedTNTEffect {

	@Override
	public void explosionTick(IExplosiveEntity ent) {
		if(ent.getTNTFuse() == 1200 && !ent.getLevel().isClientSide()) {
			CompoundTag tag = ent.getPersistentData();
			tag.putDouble("size", 1D + Math.random() * 3D);
			ent.setPersistentData(tag);
		}
		((Entity)ent).setDeltaMovement(0, 0, 0);
		((Entity)ent).setPos(((Entity)ent).xo, ((Entity)ent).yo, ((Entity)ent).zo);
		List<LivingEntity> list = ent.getLevel().getEntitiesOfClass(LivingEntity.class, ((Entity)ent).getBoundingBox());
		for(LivingEntity lent : list) {
			lent.addStatusEffect(new MobEffectInstance(MobEffects.POISON, 80, 4));
			lent.addStatusEffect(new MobEffectInstance(MobEffects.NAUSEA, 400, 0));
			lent.addStatusEffect(new MobEffectInstance(MobEffects.SLOWNESS, 80, 2));
		}
	}
	
	@Override
	public void serverExplosion(IExplosiveEntity ent) {
		ImprovedExplosion explosion = new ImprovedExplosion(ent.getLevel(), (Entity)ent, ent.getPos(), (int)Math.round(ent.getPersistentData().getDoubleOr("size", 0d) * 5D));
		explosion.doEntityExplosion(2f, true);
		explosion.doBlockExplosion(1f, 1f, 1f, 1.1f, true, false);
	}
	
	@Override
	public void spawnParticles(IExplosiveEntity ent) {
		for(int count = 0; count < ent.getPersistentData().getDoubleOr("size", 0d) * 5; count++) {
			ent.getLevel().addParticle(new DustParticleOptions(((int)(0.7f*255)<<16)|((int)(1f*255)<<8)|(int)(0.5f*255), 10f), true, ent.x() + ent.getPersistentData().getDoubleOr("size", 0d) * 1.5f * Math.random() - ent.getPersistentData().getDoubleOr("size", 0d) * 1.5f * Math.random(), ent.y() + ent.getPersistentData().getDoubleOr("size", 0d) * 1.5f * Math.random() - ent.getPersistentData().getDoubleOr("size", 0d) * 1.5f * Math.random(), ent.z() + ent.getPersistentData().getDoubleOr("size", 0d) * 1.5f * Math.random() - ent.getPersistentData().getDoubleOr("size", 0d) * 1.5f * Math.random(), 0, 0, 0);
		}
	}
	
	@Override
	public Block getBlock() {
		return Blocks.AIR;
	}
	
	@Override
	public int getDefaultFuse(IExplosiveEntity ent) {
		return 1200;
	}
}

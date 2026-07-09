package luckytnt.tnteffects;

import java.util.List;

import org.joml.Math;
import org.joml.Vector3f;

import luckytntlib.util.IExplosiveEntity;
import luckytntlib.util.explosions.ImprovedExplosion;
import luckytntlib.util.tnteffects.PrimedTNTEffect;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.DustParticleEffect;

public class ToxicCloudEffect extends PrimedTNTEffect {

	@Override
	public void explosionTick(IExplosiveEntity ent) {
		if(ent.getTNTFuse() == 1200 && !ent.getLevel().isClient()) {
			NbtCompound tag = ent.getPersistentData();
			tag.putDouble("size", 1D + Math.random() * 3D);
			ent.setPersistentData(tag);
		}
		((Entity)ent).setVelocity(0, 0, 0);
		((Entity)ent).setPosition(((Entity)ent).prevX, ((Entity)ent).prevY, ((Entity)ent).prevZ);
		List<LivingEntity> list = ent.getLevel().getNonSpectatingEntities(LivingEntity.class, ((Entity)ent).getBoundingBox());
		for(LivingEntity lent : list) {
			lent.addStatusEffect(new StatusEffectInstance(StatusEffects.POISON, 80, 4));
			lent.addStatusEffect(new StatusEffectInstance(StatusEffects.NAUSEA, 400, 0));
			lent.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, 80, 2));
		}
	}
	
	@Override
	public void serverExplosion(IExplosiveEntity ent) {
		ImprovedExplosion explosion = new ImprovedExplosion(ent.getLevel(), (Entity)ent, ent.getPos(), (int)Math.round(ent.getPersistentData().getDouble("size") * 5D));
		explosion.doEntityExplosion(2f, true);
		explosion.doBlockExplosion(1f, 1f, 1f, 1.1f, true, false);
	}
	
	@Override
	public void spawnParticles(IExplosiveEntity ent) {
		for(int count = 0; count < ent.getPersistentData().getDouble("size") * 5; count++) {
			ent.getLevel().addParticle(new DustParticleEffect(new Vector3f(0.7f, 1f, 0.5f), 10f), true, ent.x() + ent.getPersistentData().getDouble("size") * 1.5f * Math.random() - ent.getPersistentData().getDouble("size") * 1.5f * Math.random(), ent.y() + ent.getPersistentData().getDouble("size") * 1.5f * Math.random() - ent.getPersistentData().getDouble("size") * 1.5f * Math.random(), ent.z() + ent.getPersistentData().getDouble("size") * 1.5f * Math.random() - ent.getPersistentData().getDouble("size") * 1.5f * Math.random(), 0, 0, 0);
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

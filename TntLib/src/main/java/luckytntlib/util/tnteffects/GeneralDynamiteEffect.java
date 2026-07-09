package luckytntlib.util.tnteffects;

import java.util.function.Supplier;

import luckytntlib.item.LDynamiteItem;
import luckytntlib.util.IExplosiveEntity;
import net.minecraft.item.Item;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;

/**
 * GeneralDynamiteEffect is an extension of the {@link PrimedTNTEffect} and is an easy way of generalizing Dynamites without having to 
 * make existing PrimedTNTEffects messy by being able to transfer already existing PrimedTNTEffect together with some Particles and a Dynamite Item to render.
 */
public class GeneralDynamiteEffect extends PrimedTNTEffect{

	private final Supplier<Supplier<LDynamiteItem>> dynamite;
	private final PrimedTNTEffect effect;
	private ParticleEffect particles = ParticleTypes.SMOKE;
	
	/**
	 * @param dynamite  {@link LDynamiteItem} to render
	 * @param particles  Particles to display
	 * @param effect  TNT effect to execute
	 */
	public GeneralDynamiteEffect(Supplier<Supplier<LDynamiteItem>> dynamite, ParticleEffect particles, PrimedTNTEffect effect) {
		this.dynamite = dynamite;
		this.particles = particles;
		this.effect = effect;
	}
	
	/**
	 * Displays standard smoke particles
	 * 
	 * @param dynamite  {@link LDynamiteItem} to render
	 * @param effect  TNT effect to execute
	 */
	public GeneralDynamiteEffect(Supplier<Supplier<LDynamiteItem>> dynamite, PrimedTNTEffect effect) {
		this.dynamite = dynamite;
		this.effect = effect;
	}
	
	@Override
	public void serverExplosion(IExplosiveEntity entity) {
		effect.serverExplosion(entity);
	}
	
	@Override
	public void explosionTick(IExplosiveEntity entity) {
		effect.explosionTick(entity);
	}
	
	@Override
	public void spawnParticles(IExplosiveEntity entity) {
		entity.getLevel().addParticle(particles, entity.x(), entity.y(), entity.z(), 0, 0, 0);
	}
	
	@Override
	public Item getItem() {
		return dynamite.get().get();
	}
}

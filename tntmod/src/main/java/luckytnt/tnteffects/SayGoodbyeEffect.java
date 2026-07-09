package luckytnt.tnteffects;

import luckytnt.LuckyTNTMod;
import luckytnt.registry.BlockRegistry;
import luckytnt.registry.SoundRegistry;
import luckytntlib.util.IExplosiveEntity;
import luckytntlib.util.explosions.ImprovedExplosion;
import luckytntlib.util.tnteffects.PrimedTNTEffect;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.registry.RegistryKey;
import net.minecraft.core.registries.Registries;
import net.minecraft.core.Holder.Reference;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.resources.Identifier;
import net.minecraft.core.Holder;

public class SayGoodbyeEffect extends PrimedTNTEffect{

	@Override
	public void explosionTick(IExplosiveEntity entity) {
		if(entity.getTNTFuse() == 30) {
			entity.getLevel().playSound(null, entity.x(), entity.y(), entity.z(), SoundRegistry.SAY_GOODBYE.get(), SoundSource.HOSTILE, 20, 1);
		}
	}
	
	@Override
	public void serverExplosion(IExplosiveEntity entity) {		
		Player ent = entity.getLevel().getClosestPlayer((Entity)entity, 60);
		if(ent != null) {
			Reference<DamageType> type = entity.getLevel().registryAccess().get(Registries.DAMAGE_TYPE).entryOf(RegistryKey.of(Registries.DAMAGE_TYPE, Identifier.fromNamespaceAndPath(LuckyTNTMod.MODID, "say_goodbye")));
			DamageSource source = new DamageSource(type, (Entity)entity, entity.owner());
			
			ImprovedExplosion explosion = new ImprovedExplosion(ent.level(), (Entity) entity, source, ent.getX(), ent.getY(), ent.getZ(), 20);
			explosion.doEntityExplosion(2f, true);
			explosion.doBlockExplosion(1f, 1f, 1f, 1.5f, false, false);
			if(entity.getLevel() instanceof ServerLevel sLevel) {
				sLevel.sendParticles(ParticleTypes.EXPLOSION, ent.getX(), ent.getY(), ent.getZ(), 60, 2, 2, 2, 0);
			}
		}
	}
	
	@Override
	public Block getBlock() {
		return BlockRegistry.SAY_GOODBYE.get();
	}
}

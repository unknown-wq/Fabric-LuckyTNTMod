package luckytnt.tnteffects;

import java.util.List;

import luckytnt.registry.BlockRegistry;
import luckytntlib.util.IExplosiveEntity;
import luckytntlib.util.tnteffects.PrimedTNTEffect;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.Registries;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Box;

public class EntityFireworkEffect extends PrimedTNTEffect {

	@Override
	public void explosionTick(IExplosiveEntity ent) {
		((Entity)ent).setVelocity(((Entity)ent).getVelocity().x, 0.8f, ((Entity)ent).getVelocity().z);
		if(ent.getTNTFuse() == 40) {
			List<LivingEntity> ents = ent.getLevel().getNonSpectatingEntities(LivingEntity.class, new Box(ent.x() - 20, ent.y() - 20, ent.z() - 20, ent.x() + 20, ent.y() + 20, ent.z() + 20));
	      	double distance = 2000;
	      	for(LivingEntity lent : ents) {
	      		double xD = lent.getX() - ent.x();
	      		double yD = lent.getY() - ent.y();
	      		double zD = lent.getZ() - ent.z();
	      		double d = Math.sqrt(xD * xD + yD * yD + zD * zD);
	      		if(d < distance && !(lent instanceof PlayerEntity)) {
	      			distance = d;
	      			NbtCompound tag = ent.getPersistentData();
	      			tag.putString("type", EntityType.getId(lent.getType()).toString());
	      			ent.setPersistentData(tag);
	      		}
	      	}
		}
	}
	
	@Override
	public void serverExplosion(IExplosiveEntity ent) {
		EntityType<?> type = Registries.ENTITY_TYPE.get(Identifier.of(ent.getPersistentData().getString("type")));
		if(type == null) {
			type = EntityType.PIG;
		}
		for(int count = 0; count < 300; count++) {
			Entity lent = type.create(ent.getLevel());	
			lent.setPosition(ent.x(), ent.y(), ent.z());
			lent.setVelocity(Math.random() * 3f - 1.5f, Math.random() * 3f - 1.5f, Math.random() * 3f - 1.5f);
			if(lent instanceof MobEntity mob && ent.getLevel() instanceof ServerWorld sLevel) {
				mob.initialize(sLevel, sLevel.getLocalDifficulty(toBlockPos(ent.getPos())), SpawnReason.MOB_SUMMONED, null);
			}
			ent.getLevel().spawnEntity(lent);
		}
	}
	
	@Override
	public void spawnParticles(IExplosiveEntity ent) {
		ent.getLevel().addParticle(ParticleTypes.FLAME, ent.x(), ent.y(), ent.z(), 0, 0, 0);
	}
	
	@Override
	public Block getBlock() {
		return BlockRegistry.ENTITY_FIREWORK.get();
	}
	
	@Override
	public int getDefaultFuse(IExplosiveEntity ent) {
		return 40;
	}
}

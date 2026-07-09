package luckytnt.tnteffects.projectile;


import luckytnt.registry.EntityRegistry;
import luckytnt.registry.ItemRegistry;
import luckytntlib.entity.LExplosiveProjectile;
import luckytntlib.util.IExplosiveEntity;
import luckytntlib.util.tnteffects.PrimedTNTEffect;
import net.minecraft.item.Item;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.world.World;

public class EruptingDynamiteEffect extends PrimedTNTEffect{

	@Override
	public void baseTick(IExplosiveEntity entity) {
		World level = entity.getLevel();
		if(entity instanceof LExplosiveProjectile ent) {
			if(ent.inGround()) {
				NbtCompound tag = ent.getPersistentData();
				tag.putBoolean("hitBefore", true);
				ent.setPersistentData(tag);
			}
			if(ent.getTNTFuse() == 0) {
				ent.destroy();
			}
			if(ent.inGround() || ent.getPersistentData().getBoolean("hitBefore")) {
				explosionTick(ent);
				ent.setTNTFuse(ent.getTNTFuse() - 1);
			}
			if(level.isClient) {
				spawnParticles(entity);
			}
		}
	}
	
	@Override
	public void explosionTick(IExplosiveEntity entity) {
		World level = entity.getLevel();
		if(entity.getTNTFuse() < 15 && entity.getTNTFuse() % 3 == 0) {
			LExplosiveProjectile erupting_tnt = EntityRegistry.ERUPTING_PROJECTILE.get().create(level);
			erupting_tnt.setPosition(entity.getPos());
			erupting_tnt.setOwner(entity.owner());
			erupting_tnt.setVelocity((Math.random() * 2D - 1D) * 0.1f, 0.6f + Math.random() * 0.4f, (Math.random() * 2D - 1D) * 0.1f, 2f + level.random.nextFloat(), 0f);	
			erupting_tnt.setOnFireFor(1000);
			level.spawnEntity(erupting_tnt);
			level.playSound(null, toBlockPos(entity.getPos()), SoundEvents.ENTITY_GENERIC_EXPLODE.value(), SoundCategory.MASTER, 3, 1);
		}
	}
	
	@Override
	public void spawnParticles(IExplosiveEntity entity) {
		entity.getLevel().addParticle(ParticleTypes.FLAME, entity.x(), entity.y(), entity.z(), 0, 0, 0);
	}
	
	@Override
	public Item getItem() {
		return ItemRegistry.ERUPTING_DYNAMITE.get();
	}
	
	@Override
	public int getDefaultFuse(IExplosiveEntity entity) {
		return 40;
	}
}

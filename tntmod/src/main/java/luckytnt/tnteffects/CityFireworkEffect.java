package luckytnt.tnteffects;

import java.util.function.Supplier;

import luckytnt.registry.BlockRegistry;
import luckytnt.registry.EntityRegistry;
import luckytntlib.entity.PrimedLTNT;
import luckytntlib.util.IExplosiveEntity;
import luckytntlib.util.tnteffects.PrimedTNTEffect;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.core.particles.ParticleTypes;

public class CityFireworkEffect extends PrimedTNTEffect {

	@Override
	public void explosionTick(IExplosiveEntity ent) {
		((Entity)ent).setDeltaMovement(((Entity)ent).getDeltaMovement().x, 0.8f, ((Entity)ent).getDeltaMovement().z);
	}
	
	@Override
	public void serverExplosion(IExplosiveEntity ent) {
		for(int count = 0; count < 50; count++) {
			Supplier<EntityType<PrimedLTNT>> type = null;
			int rand = (int)Math.round(Math.random() * 3);
			switch(rand) {
				case 0: type = EntityRegistry.COBBLESTONE_HOUSE_TNT; break;
				case 1: type = EntityRegistry.WOOD_HOUSE_TNT; break;
				case 2: type = EntityRegistry.BRICK_HOUSE_TNT; break;
				case 3: type = EntityRegistry.MANKINDS_MARK; break;
			}
			
			if(type != null) {
				PrimedLTNT tnt = type.get().create(ent.getLevel());
				tnt.setPosition(ent.getPos());
				tnt.setOwner(ent.owner() instanceof LivingEntity ? (LivingEntity)ent.owner() : null);
				tnt.setDeltaMovement(Math.random() * 1.5f - Math.random() * 1.5f, Math.random() * 1.5f - Math.random() * 1.5f, Math.random() * 1.5f  - Math.random() * 1.5f);
				ent.getLevel().addFreshEntity(tnt);
			}
		}
	}
	
	@Override
	public void spawnParticles(IExplosiveEntity ent) {
		ent.getLevel().addParticle(ParticleTypes.FLAME, ent.x(), ent.y(), ent.z(), 0, 0, 0);
	}
	
	@Override
	public Block getBlock() {
		return BlockRegistry.CITY_FIREWORK.get();
	}
	
	@Override
	public int getDefaultFuse(IExplosiveEntity ent) {
		return 40;
	}
}

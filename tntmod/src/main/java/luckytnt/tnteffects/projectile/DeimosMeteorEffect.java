package luckytnt.tnteffects.projectile;

import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import luckytnt.registry.EntityRegistry;
import luckytntlib.entity.LExplosiveProjectile;
import luckytntlib.util.IExplosiveEntity;

public class DeimosMeteorEffect extends IceMeteorEffect {

	public DeimosMeteorEffect() {
		super(80, 4f);
	}

	@Override
	public void serverExplosion(IExplosiveEntity ent) {
		super.serverExplosion(ent);

		final Level level = ent.getLevel();
		final Vec3 pos = ent.getPos();
		final LivingEntity owner = ent.owner();
		final EntityType<LExplosiveProjectile> miniIceMeteor = EntityRegistry.MINI_ICE_METEOR.get();
		for(int count = 0; count < 300; count++) {
			LExplosiveProjectile mini = miniIceMeteor.create(level, EntitySpawnReason.MOB_SUMMONED);
			mini.setPos(pos);
			mini.setOwner(owner);
			mini.setDeltaMovement(Math.random() * 8D - 4D, 3 + Math.random() * 2, Math.random() * 8D - 4D);
			mini.setTNTFuse(100000);
			level.addFreshEntity(mini);
		}
	}
}

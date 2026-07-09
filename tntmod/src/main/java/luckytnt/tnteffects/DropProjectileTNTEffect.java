package luckytnt.tnteffects;

import java.util.function.Supplier;

import luckytnt.config.LuckyTNTConfigValues;
import luckytntlib.entity.LExplosiveProjectile;
import luckytntlib.util.IExplosiveEntity;
import luckytntlib.util.tnteffects.PrimedTNTEffect;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityType;

public class DropProjectileTNTEffect extends PrimedTNTEffect {
	
	public final Supplier<Supplier<EntityType<LExplosiveProjectile>>> projectile;
	
	public DropProjectileTNTEffect(Supplier<Supplier<EntityType<LExplosiveProjectile>>> projectile) {
		this.projectile = projectile;
	}

	@Override
	public void serverExplosion(IExplosiveEntity entity) {
		LExplosiveProjectile meteor = projectile.get().get().create(entity.getLevel());
		meteor.setPosition(entity.x(), entity.y() + LuckyTNTConfigValues.DROP_HEIGHT.get(), entity.z());
		meteor.setOwner(entity.owner());
		entity.getLevel().spawnEntity(meteor);
	}
	
	@Override
	public boolean playsSound() {
		return false;
	}
	
	@Override
	public Block getBlock() {
		return Blocks.AIR;
	}

	@Override
	public int getDefaultFuse(IExplosiveEntity entity) {
		return 0;
	}
}

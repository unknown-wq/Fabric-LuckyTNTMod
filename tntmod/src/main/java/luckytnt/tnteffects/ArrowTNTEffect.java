package luckytnt.tnteffects;

import luckytnt.registry.BlockRegistry;
import luckytntlib.entity.PrimedLTNT;
import luckytntlib.util.IExplosiveEntity;
import luckytntlib.util.tnteffects.PrimedTNTEffect;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity.PickupPermission;

public class ArrowTNTEffect extends PrimedTNTEffect{

	private final int arrowCount;
	
	public ArrowTNTEffect(int arrowCount) {
		this.arrowCount = arrowCount;
	}
	
	@Override
	public void serverExplosion(IExplosiveEntity entity) {
		for(int count = 0; count < arrowCount; count++) {
			ArrowEntity arrow = new ArrowEntity(EntityType.ARROW, entity.getLevel());
			arrow.setPosition(entity.x(), entity.y() + (entity instanceof PrimedLTNT ? 0.5f : 0f), entity.z());
			arrow.setVelocity(Math.random() * 3 - Math.random() * 3, Math.random() * 2 - Math.random(), Math.random() * 3 - Math.random() * 3);
			arrow.setOwner(entity.owner());
			arrow.setDamage(10);
			arrow.pickupType = PickupPermission.CREATIVE_ONLY;
			entity.getLevel().spawnEntity(arrow);
		}
	}
	
	@Override
	public Block getBlock() {
		return BlockRegistry.ARROW_TNT.get();
	}
}

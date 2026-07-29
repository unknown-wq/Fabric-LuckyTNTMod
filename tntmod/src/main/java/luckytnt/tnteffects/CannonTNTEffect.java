package luckytnt.tnteffects;

import luckytnt.registry.BlockRegistry;
import luckytnt.registry.ItemRegistry;
import luckytntlib.util.IExplosiveEntity;
import luckytntlib.util.tnteffects.PrimedTNTEffect;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.Level;

public class CannonTNTEffect extends PrimedTNTEffect {

	@Override
	public void explosionTick(IExplosiveEntity entity) {
		Level level = entity.getLevel();
		if(!level.isClientSide() && entity.getTNTFuse() <= 400) {
			// getEntitiesOfClass walks every EntitySection the box intersects (~2197 for a 200 edge
			// box) just to find players. level.players() is already the exact candidate set.
			AABB range = new AABB(entity.getPos().add(-100, -100, -100), entity.getPos().add(100, 100, 100));
			for(Player player : level.players()) {
				if(player.isSpectator() || player.isRemoved() || !range.intersects(player.getBoundingBox())) {
					continue;
				}
				if(!player.equals(entity.owner())) {
					double xVel = player.getX() - entity.x();
					double yVel = player.getY() - 0.6f;
					double zVel = player.getZ() - entity.z();
					Vec3 dir = new Vec3(xVel + (Math.random() * 0.4f - 0.2f), yVel - entity.y() - 0.5f + Math.sqrt(xVel * xVel + zVel * zVel) * 0.2f + (Math.random() * 0.4f - 0.2f), zVel + (Math.random() * 0.4f - 0.2f));
					ItemRegistry.DYNAMITE_X5.get().shoot(level, entity.x(), entity.y() + 0.5f, entity.z(), dir, 3, entity.owner());
				}
			}
		}
	}
	
	@Override
	public Block getBlock() {
		return BlockRegistry.CANNON_TNT.get();
	}
	
	@Override
	public int getDefaultFuse(IExplosiveEntity entity) {
		return 600;
	}
}

package luckytnt.tnteffects;

import java.util.List;

import luckytnt.registry.BlockRegistry;
import luckytnt.registry.ItemRegistry;
import luckytntlib.util.IExplosiveEntity;
import luckytntlib.util.tnteffects.PrimedTNTEffect;
import net.minecraft.block.Block;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class TurretTNTEffect extends PrimedTNTEffect {

	@Override
	public void explosionTick(IExplosiveEntity entity) {
		World level = entity.getLevel();
		if(!level.isClient && entity.getTNTFuse() <= 300) {
			List<PlayerEntity> players = level.getNonSpectatingEntities(PlayerEntity.class, new Box(entity.getPos().add(-100, -100, -100), entity.getPos().add(100, 100, 100)));
			for(PlayerEntity player : players) {
				if(!player.equals(entity.owner())) {
					double xVel = player.getX() - entity.x();
					double yVel = player.getY() - 0.6f;
					double zVel = player.getZ() - entity.z();
					Vec3d dir = new Vec3d(xVel + (Math.random() * 0.4f - 0.2f), yVel - entity.y() - 0.5f + Math.sqrt(xVel * xVel + zVel * zVel) * 0.2f + (Math.random() * 0.4f - 0.2f), zVel + (Math.random() * 0.4f - 0.2f));
					ItemRegistry.DYNAMITE.get().shoot(level, entity.x(), entity.y() + 0.5f, entity.z(), dir, 3, entity.owner());
				}
			}
		}
	}
	
	@Override
	public Block getBlock() {
		return BlockRegistry.TURRET_TNT.get();
	}
	
	@Override
	public int getDefaultFuse(IExplosiveEntity entity) {
		return 400;
	}
}

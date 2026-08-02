package luckytnt.tnteffects;

import java.util.List;

import luckytnt.registry.BlockRegistry;
import luckytntlib.util.IExplosiveEntity;
import luckytntlib.util.explosions.ImprovedExplosion;
import luckytntlib.util.tnteffects.PrimedTNTEffect;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.phys.AABB;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.entity.EntityTypes;

public class LightningStormEffect extends PrimedTNTEffect {

	@Override
	public void explosionTick(IExplosiveEntity ent) {
		// 10 bolts a tick for 120 ticks were 1200 full vanilla LightningBolt entities, each preceded
		// by a whole Y column scan. Firing every other tick halves that; a bolt lives ~10 ticks so
		// the storm stays just as dense on screen.
		if(ent.getTNTFuse() < 120 && ent.getTNTFuse() % 2 == 0 && ent.getLevel() instanceof ServerLevel sLevel) {
			for(int count = 0; count < 10; count++) {
				double offX = Math.random() * 150D - 75D;
				double offZ = Math.random() * 150D - 75D;
				int x = Mth.floor(ent.x() + offX);
				int z = Mth.floor(ent.z() + offZ);
				int y = sLevel.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, x, z) - 1;
				Entity lighting = new LightningBolt(EntityTypes.LIGHTNING_BOLT, sLevel);
				lighting.setPos(ent.x() + offX, y, ent.z() + offZ);
				sLevel.addFreshEntity(lighting);
			}
		}
	}
	
	@Override
	public void serverExplosion(IExplosiveEntity ent) {
		Level level = ent.getLevel();
		List<LivingEntity> ents = level.getEntitiesOfClass(LivingEntity.class, new AABB(ent.x() - 75, ent.y() - 75, ent.z() - 75, ent.x() + 75, ent.y() + 75, ent.z() + 75));
		for(LivingEntity lent : ents) {
			// heightmap instead of a full Y column scan per target
			int offY = level.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Mth.floor(lent.getX()), Mth.floor(lent.getZ())) - 1;
			Entity lighting = new LightningBolt(EntityTypes.LIGHTNING_BOLT, level);
			lighting.setPos(lent.getX(), offY, lent.getZ());
			level.addFreshEntity(lighting);

			ImprovedExplosion explosion = new ImprovedExplosion(level, new Vec3(lent.getX(), offY, lent.getZ()), 3);
			explosion.doEntityExplosion(1f, true);
			explosion.doBlockExplosion(1f, 1.2f, 1f, 1.2f, false, false);
		}
	}
	
	@Override
	public Block getBlock() {
		return BlockRegistry.LIGHTNING_STORM.get();
	}
	
	@Override
	public int getDefaultFuse(IExplosiveEntity ent) {
		return 160;
	}
}

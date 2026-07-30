package luckytnt.tnteffects;

import luckytnt.registry.BlockRegistry;
import luckytnt.registry.EntityRegistry;
import luckytntlib.entity.PrimedLTNT;
import luckytntlib.util.IExplosiveEntity;
import luckytntlib.util.explosions.ImprovedExplosion;
import luckytntlib.util.tnteffects.PrimedTNTEffect;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundSource;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.Level;
import net.minecraft.world.entity.EntitySpawnReason;

public class DividingTNTEffect extends PrimedTNTEffect{
	
	@Override
	public void baseTick(IExplosiveEntity entity) {
		super.baseTick(entity);
		if(entity.getTNTFuse() == 60 && entity.getPersistentData().getIntOr("level", 0) != 0) {
			double x = entity.getPersistentData().getDoubleOr("x", 0) - entity.x();
			double z = entity.getPersistentData().getDoubleOr("z", 0) - entity.z();
			double magnitude = Math.sqrt(x * x + z * z) + 0.01f;
			((Entity)entity).setDeltaMovement(x / magnitude, 1, z / magnitude);
		}
	}
	
	@Override
	public void serverExplosion(IExplosiveEntity entity) {
		if(entity.getPersistentData().getIntOr("level", 0) == 0) {		
			for(int offX = -50; offX < 50; offX += 10) {
				for(int offZ = -50; offZ < 50; offZ += 10) {
					findBlock: for(int offY = 320; offY > -64; offY--) {
						BlockPos pos = toBlockPos(new Vec3(entity.x() + offX, entity.y() + offY, entity.z() + offZ));
						if(entity.getLevel().getBlockState(pos).isCollisionShapeFullBlock(entity.getLevel(), pos) && !entity.getLevel().getBlockState(pos.above()).isCollisionShapeFullBlock(entity.getLevel(), pos.above())) {
							PrimedLTNT projectile = EntityRegistry.DIVIDING_TNT.get().create(entity.getLevel(), EntitySpawnReason.MOB_SUMMONED);
							projectile.setPos(entity.getPos().add(offX, offY, offZ));
							projectile.setOwner(entity.owner() instanceof LivingEntity ? (LivingEntity)entity.owner() : null);
							CompoundTag tag = projectile.getPersistentData();
							tag.putInt("maxLevel", entity.getLevel().getRandom().nextInt(5));
							tag.putInt("level", entity.getPersistentData().getIntOr("level", 0) + 1);
							tag.putDouble("x", entity.x());
							tag.putDouble("z", entity.z());
							projectile.setPersistentData(tag);
							entity.getLevel().addFreshEntity(projectile);
							break findBlock;
						}
					}
				}
			}
			entity.destroy();
		}
		else {
			if(entity.getPersistentData().getIntOr("level", 0) >= entity.getPersistentData().getIntOr("maxLevel", 0)) {
				ImprovedExplosion explosion = new ImprovedExplosion(entity.getLevel(), (Entity)entity, entity.getPos(), 10);
				explosion.doEntityExplosion(1f, true);
				explosion.doBlockExplosion();
				if(entity.getPersistentData().getIntOr("level", 0) >= entity.getPersistentData().getIntOr("maxLevel", 0)) {
					Level level = entity.getLevel();
					entity.getLevel().playSound((Entity)entity, toBlockPos(entity.getPos()), SoundEvents.GENERIC_EXPLODE.value(), SoundSource.BLOCKS, 4f, (1f + (level.getRandom().nextFloat() - level.getRandom().nextFloat()) * 0.2f) * 0.7f);
				}
				entity.destroy();
			}
			else {
				ImprovedExplosion explosion = new ImprovedExplosion(entity.getLevel(), (Entity)entity, entity.getPos(), 10);
				explosion.doEntityExplosion(1.5f, true);
				explosion.doBlockExplosion();
				PrimedLTNT projectile = EntityRegistry.DIVIDING_TNT.get().create(entity.getLevel(), EntitySpawnReason.MOB_SUMMONED);
				projectile.setOwner(entity.owner() instanceof LivingEntity ? (LivingEntity)entity.owner() : null);
				projectile.setPos(entity.getPos());
				projectile.setDeltaMovement(Math.random() - Math.random(), 1 + Math.random() * 0.75f, Math.random() - Math.random());
				CompoundTag tag = projectile.getPersistentData();
				tag.putInt("maxLevel", entity.getPersistentData().getIntOr("maxLevel", 0));
				tag.putInt("level", entity.getPersistentData().getIntOr("level", 0) + 1);
				tag.putDouble("x", entity.getPersistentData().getDoubleOr("x", 0));
				tag.putDouble("z", entity.getPersistentData().getDoubleOr("z", 0));
				projectile.setPersistentData(tag);
				projectile.setDeltaMovement(0, 0, 0);
				entity.getLevel().addFreshEntity(projectile);
			}
		}
	}
	
	@Override
	public Block getBlock() {
		return BlockRegistry.DIVIDING_TNT.get();
	}
	
	@Override
	public int getDefaultFuse(IExplosiveEntity entity) {
		return 80;
	}
}

package luckytnt.tnteffects;

import java.util.Random;

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

public class DividingTNTEffect extends PrimedTNTEffect{
	
	@Override
	public void baseTick(IExplosiveEntity entity) {
		super.baseTick(entity);
		if(entity.getTNTFuse() == 60 && entity.getPersistentData().getInt("level") != 0) {
			double x = entity.getPersistentData().getDouble("x") - entity.x();
			double z = entity.getPersistentData().getDouble("z") - entity.z();
			double magnitude = Math.sqrt(x * x + z * z) + 0.01f;
			((Entity)entity).setDeltaMovement(x / magnitude, 1, z / magnitude);
		}
	}
	
	@Override
	public void serverExplosion(IExplosiveEntity entity) {
		if(entity.getPersistentData().getInt("level") == 0) {		
			for(int offX = -50; offX < 50; offX += 10) {
				for(int offZ = -50; offZ < 50; offZ += 10) {
					findBlock: for(int offY = 320; offY > -64; offY--) {
						BlockPos pos = toBlockPos(new Vec3(entity.x() + offX, entity.y() + offY, entity.z() + offZ));
						if(entity.getLevel().getBlockState(pos).isFullCube(entity.getLevel(), pos) && !entity.getLevel().getBlockState(pos.up()).isFullCube(entity.getLevel(), pos.up())) {
							PrimedLTNT projectile = EntityRegistry.DIVIDING_TNT.get().create(entity.getLevel());
							projectile.setPosition(entity.getPos().add(offX, offY, offZ));
							projectile.setOwner(entity.owner() instanceof LivingEntity ? (LivingEntity)entity.owner() : null);
							CompoundTag tag = projectile.getPersistentData();
							tag.putInt("maxLevel", new Random().nextInt(5));
							tag.putInt("level", entity.getPersistentData().getInt("level") + 1);
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
			if(entity.getPersistentData().getInt("level") >= entity.getPersistentData().getInt("maxLevel")) {
				ImprovedExplosion explosion = new ImprovedExplosion(entity.getLevel(), (Entity)entity, entity.getPos(), 10);
				explosion.doEntityExplosion(1f, true);
				explosion.doBlockExplosion();
				if(entity.getPersistentData().getInt("level") >= entity.getPersistentData().getInt("maxLevel")) {
					Level level = entity.getLevel();
					entity.getLevel().playSound((Entity)entity, toBlockPos(entity.getPos()), SoundEvents.ENTITY_GENERIC_EXPLODE.value(), SoundSource.BLOCKS, 4f, (1f + (level.random.nextFloat() - level.random.nextFloat()) * 0.2f) * 0.7f);
				}
				entity.destroy();
			}
			else {
				ImprovedExplosion explosion = new ImprovedExplosion(entity.getLevel(), (Entity)entity, entity.getPos(), 10);
				explosion.doEntityExplosion(1.5f, true);
				explosion.doBlockExplosion();
				PrimedLTNT projectile = EntityRegistry.DIVIDING_TNT.get().create(entity.getLevel());
				projectile.setOwner(entity.owner() instanceof LivingEntity ? (LivingEntity)entity.owner() : null);
				projectile.setPosition(entity.getPos());
				projectile.setDeltaMovement(Math.random() - Math.random(), 1 + Math.random() * 0.75f, Math.random() - Math.random());
				CompoundTag tag = projectile.getPersistentData();
				tag.putInt("maxLevel", entity.getPersistentData().getInt("maxLevel"));
				tag.putInt("level", entity.getPersistentData().getInt("level") + 1);
				tag.putDouble("x", entity.getPersistentData().getDouble("x"));
				tag.putDouble("z", entity.getPersistentData().getDouble("z"));
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

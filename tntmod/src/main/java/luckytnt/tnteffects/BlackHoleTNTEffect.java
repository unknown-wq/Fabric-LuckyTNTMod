package luckytnt.tnteffects;


import java.util.List;
import java.util.Random;

import org.joml.Vector3f;

import luckytnt.event.LevelEvents;
import luckytnt.registry.BlockRegistry;
import luckytnt.registry.EntityRegistry;
import luckytntlib.util.IExplosiveEntity;
import luckytntlib.util.tnteffects.PrimedTNTEffect;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.FallingBlockEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSources;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;

public class BlackHoleTNTEffect extends PrimedTNTEffect {

	@Override
	public void explosionTick(IExplosiveEntity ent) {
		if(ent.getTNTFuse() < 400 && ent.getTNTFuse() >= 300) {
			((Entity)ent).setNoGravity(true);
			((Entity)ent).setVelocity(0, 0.05, 0);
		}
		if(ent.getTNTFuse() < 300) {
			((Entity)ent).setVelocity(0, 0, 0);
		}
		if(ent.getTNTFuse() < 350) {
			if(ent.getTNTFuse() % 20 == 0 && !ent.getLevel().isClient()) {
				for(int i = 0; i <= 400 + (int)Math.round((1D / ((double)ent.getTNTFuse() * 0.5D))) * 1600D; i++) {
					int offX = new Random().nextInt(75) - new Random().nextInt(75);
					int offZ = new Random().nextInt(75) - new Random().nextInt(75);
					int offY = LevelEvents.getTopBlock(ent.getLevel(), (int)Math.round(ent.x()) + offX, (int)Math.round(ent.z()) + offZ, false);
					BlockPos pos = toBlockPos(new Vec3d(ent.x() + offX, offY, ent.z() + offZ));
					FallingBlockEntity.spawnFromBlock(ent.getLevel(), pos, ent.getLevel().getBlockState(pos));
					ent.getLevel().setBlockState(pos, Blocks.AIR.getDefaultState(), 3);
				}
			}
			
			List<LivingEntity> list = ent.getLevel().getNonSpectatingEntities(LivingEntity.class, new Box(ent.x() + 100, ent.y() + 100, ent.z() + 100, ent.x() - 100, ent.y() - 100, ent.z() - 100));
			List<FallingBlockEntity> blocks = ent.getLevel().getNonSpectatingEntities(FallingBlockEntity.class, new Box(ent.x() + 100, ent.y() + 100, ent.z() + 100, ent.x() - 100, ent.y() - 100, ent.z() - 100));
			
			for(FallingBlockEntity block : blocks) {
				double x = ent.x() - block.getX();
				double y = ent.y() - block.getY();
				double z = ent.z() - block.getZ();
				Vec3d vec = new Vec3d(x, y, z);
				if(vec.length() <= 2) {
					block.discard();
				}
				Vec3d vec3 = vec.normalize().multiply(0.4D);
				block.setVelocity(vec3.add(0, 0.1D, 0));
			}
			
			for(LivingEntity living : list) {
				double x = ent.x() - living.getX();
				double y = ent.y() - living.getEyeY();
				double z = ent.z() - living.getZ();
				Vec3d vec = new Vec3d(x, y, z);
				DamageSources sources = ent.getLevel().getDamageSources();
				if(vec.length() <= 2 && ent.getTNTFuse() % 80 == 0 && living instanceof PlayerEntity) {
					living.damage(sources.inWall(), 6f);
				}
				if(vec.length() <= 2 && !(living instanceof PlayerEntity)) {
					living.discard();
				}
				Vec3d vec3 = vec.normalize().multiply((1D / (0.25D * vec.length() + 0.0001D)) + 0.5D);
				living.setVelocity(vec3);
			}
		}
	}
	
	@Override
	public void serverExplosion(IExplosiveEntity ent) {
		EntityRegistry.TNT_X500_EFFECT.build().serverExplosion(ent);
		
		List<LivingEntity> list = ent.getLevel().getNonSpectatingEntities(LivingEntity.class, new Box(ent.x() + 100, ent.y() + 100, ent.z() + 100, ent.x() - 100, ent.y() - 100, ent.z() - 100));
		List<FallingBlockEntity> blocks = ent.getLevel().getNonSpectatingEntities(FallingBlockEntity.class, new Box(ent.x() + 100, ent.y() + 100, ent.z() + 100, ent.x() - 100, ent.y() - 100, ent.z() - 100));
	
		for(FallingBlockEntity block : blocks) {
			block.discard();
		}
		
		for(LivingEntity living : list) {
			double x = living.getX() - ent.x();
			double y = living.getEyeY() - ent.y();
			double z = living.getZ() - ent.z();
			Vec3d vec = new Vec3d(x, y, z).normalize().multiply(4);
			living.setVelocity(vec);
		}
	}
	
	@Override
	public void spawnParticles(IExplosiveEntity ent) {
		if(ent.getTNTFuse() < 350) {
			double phi = Math.PI * (3D - Math.sqrt(5D));
			for(int i = 0; i < 1000; i++) {
				double y = 1D - ((double)i / (1000D - 1D)) * 2D;
				double radius = Math.sqrt(1D - y * y);
				
				double theta = phi * i;
				
				double x = Math.cos(theta) * radius;
				double z = Math.sin(theta) * radius;
				
				ent.getLevel().addParticle(new DustParticleEffect(new Vector3f(0f, 0f, 0f), 0.75f), ent.x() + x * 2, ent.y() + 0.5D  + y * 2, ent.z() + 2 * z, 0, 0, 0);
			}
		}
	}
	
	@Override
	public Block getBlock() {
		return BlockRegistry.BLACK_HOLE_TNT.get();
	}
	
	@Override
	public int getDefaultFuse(IExplosiveEntity ent) {
		return 500;
	}
}

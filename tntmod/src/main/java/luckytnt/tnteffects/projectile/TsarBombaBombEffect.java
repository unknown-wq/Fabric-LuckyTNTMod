package luckytnt.tnteffects.projectile;

import java.util.List;

import org.joml.Vector3f;

import luckytnt.LuckyTNTMod;
import luckytnt.network.HydrogenBombS2CPacket;
import luckytnt.registry.BlockRegistry;
import luckytnt.registry.EffectRegistry;
import luckytnt.util.NuclearBombLike;
import luckytntlib.util.IExplosiveEntity;
import luckytntlib.util.explosions.ImprovedExplosion;
import luckytntlib.util.tnteffects.PrimedTNTEffect;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.tags.BlockTags;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.AABB;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class TsarBombaBombEffect extends PrimedTNTEffect implements NuclearBombLike {

	@Override
	public void serverExplosion(IExplosiveEntity entity) {
		if(entity.getLevel() instanceof ServerLevel sworld) {
			for(ServerLevel sw : sworld.getServer().getAllLevels()) {
				for(ServerPlayer player : sw.players()) {
					if(player.level().dimension() == sworld.dimension() && player.distanceTo((Entity)entity) <= 150) {
						LuckyTNTMod.RH.sendS2CPacket(player, new HydrogenBombS2CPacket(((Entity)entity).getId()));
					}
				}
			}
		}
		
		ImprovedExplosion explosion = new ImprovedExplosion(entity.getLevel(), (Entity)entity, entity.getPos(), 160);
		explosion.doEntityExplosion(15f, true);
		explosion.doBlockExplosion(1f, 1f, 0.167f, 0.05f, false, true);
		
		List<LivingEntity> list = entity.getLevel().getEntitiesOfClass(LivingEntity.class, new AABB(entity.x() - 90, entity.y() - 65, entity.z() - 90, entity.x() + 90, entity.y() + 65, entity.z() + 90));
		for(LivingEntity living : list) {
			living.addEffect(new MobEffectInstance(BuiltInRegistries.MOB_EFFECT.getOrThrow(EffectRegistry.CONTAMINATED), 3600, 0, true, true, true));
		}
		
		// Only the cells actually inside the r=300 sphere are visited: the z span is derived from the
		// remaining squared radius instead of walking the full 601x201x601 cuboid and testing afterwards.
		Level level = entity.getLevel();
		int baseX = Mth.floor(entity.x());
		int baseY = Mth.floor(entity.y());
		int baseZ = Mth.floor(entity.z());
		BlockState nuclearWaste = BlockRegistry.NUCLEAR_WASTE.get().defaultBlockState();
		BlockState air = Blocks.AIR.defaultBlockState();
		for(int offX = -300; offX <= 300; offX++) {
			int dx2 = offX * offX;
			for(int offY = -300 / 3; offY <= 300 / 3; offY++) {
				int remaining = 90000 - dx2 - offY * offY;
				if(remaining < 0) {
					continue;
				}
				int zMax = (int)Math.sqrt(remaining);
				while((zMax + 1) * (zMax + 1) <= remaining) {
					zMax++;
				}
				while(zMax > 0 && zMax * zMax > remaining) {
					zMax--;
				}
				if(zMax > 300) {
					zMax = 300;
				}
				for(int offZ = -zMax; offZ <= zMax; offZ++) {
					BlockPos pos = new BlockPos(baseX + offX, baseY + offY, baseZ + offZ);
					BlockState state = level.getBlockState(pos);
					if(state.getBlock().getExplosionResistance() <= 200) {
						int d2 = dx2 + offY * offY + offZ * offZ;
						// The cheap, pure "is this block soft enough" test moved ahead of the world read of
						// the block below and of the RNG roll; the set of positions passing all three tests
						// (and the 20% chance applied to each) is unchanged.
						if(d2 <= 22500 && (state.isAir() || state.getDestroySpeed(level, pos) <= 0.2f)) {
							BlockPos below = pos.below();
							if(level.getBlockState(below).isFaceSturdy(level, below, Direction.UP) && Math.random() < 0.2D) {
								level.setBlock(pos, nuclearWaste, 3);
							}
						}
						if(state.is(BlockTags.LEAVES)) {
							level.setBlock(pos, air, 3);
						}
					}
				}
			}
		}
	}
	
	@Override
	public void displayMushroomCloud(IExplosiveEntity ent) {
		for(int count = 0; count < 1500; count++) {
			ent.getLevel().addParticle(new DustParticleOptions(((int)(1f*255)<<16)|((int)(2f*255)<<8)|(int)(0f*255), 10f), ent.x() + Math.random() * 60 - Math.random() * 60, ent.y() + Math.random() * 3 - Math.random() * 3, ent.z() + Math.random() * 60 - Math.random() * 60, 0, 0, 0);
		}
		for(int count = 0; count < 1000; count++) {
			ent.getLevel().addParticle(new DustParticleOptions(((int)(1f*255)<<16)|((int)(2f*255)<<8)|(int)(0f*255), 10f), ent.x() + Math.random() * 20 - Math.random() * 20, ent.y() + 3 + Math.random() * 3 - Math.random() * 3, ent.z() + Math.random() * 20 - Math.random() * 20, 0, 0, 0);
		}
		for(int count = 0; count < 800; count++) {
			ent.getLevel().addParticle(new DustParticleOptions(((int)(1f*255)<<16)|((int)(2f*255)<<8)|(int)(0f*255), 10f), ent.x() + Math.random() * 10 - Math.random() * 10, ent.y() + Math.random() * 3 - Math.random() * 3, ent.z() + Math.random() * 10 - Math.random() * 10, 0, 0, 0);
		}
		for(int count = 0; count < 600; count++) {
			ent.getLevel().addParticle(new DustParticleOptions(((int)(1f*255)<<16)|((int)(2f*255)<<8)|(int)(0f*255), 10f), ent.x() + Math.random() * 6 - Math.random() * 6, ent.y() + 4 + Math.random() * 3 - Math.random() * 3, ent.z() + Math.random() * 6 - Math.random() * 6, 0, 0, 0);
		}
		for(int count = 0; count < 600; count++) {
			ent.getLevel().addParticle(new DustParticleOptions(((int)(1f*255)<<16)|((int)(2f*255)<<8)|(int)(0f*255), 10f), ent.x() + Math.random() * 2 - Math.random() * 2, ent.y() + 15 + Math.random() * 12 - Math.random() * 12, ent.z() + Math.random() * 2 - Math.random() * 2, 0, 0, 0);
		}
		for(int count = 0; count < 600; count++) {
			ent.getLevel().addParticle(new DustParticleOptions(((int)(1f*255)<<16)|((int)(2f*255)<<8)|(int)(0f*255), 10f), ent.x() + Math.random() * 6 - Math.random() * 6, ent.y() + 22 + Math.random() * 3 - Math.random() * 3, ent.z() + Math.random() * 6 - Math.random() * 6, 0, 0, 0);
		}
		for(int count = 0; count < 600; count++) {
			ent.getLevel().addParticle(new DustParticleOptions(((int)(1f*255)<<16)|((int)(2f*255)<<8)|(int)(0f*255), 10f), ent.x() + Math.random() * 6 - Math.random() * 6, ent.y() + 29 + Math.random() * 3 - Math.random() * 3, ent.z() + Math.random() * 6 - Math.random() * 6, 0, 0, 0);
		}
		for(int count = 0; count < 2000; count++) {
			ent.getLevel().addParticle(new DustParticleOptions(((int)(1f*255)<<16)|((int)(2f*255)<<8)|(int)(0f*255), 10f), ent.x() + Math.random() * 12 - Math.random() * 12, ent.y() + 24 + Math.random() * 6 - Math.random() * 6, ent.z() + Math.random() * 12 - Math.random() * 12, 0, 0, 0);
		}
		for(int count = 0; count < 2000; count++) {
			ent.getLevel().addParticle(ParticleTypes.LARGE_SMOKE, ent.x() + Math.random() * 2 - Math.random() * 2, ent.y() + 22 + Math.random() * 2 - Math.random() * 2, ent.z() + Math.random() * 2 - Math.random() * 2, Math.random() * 2 - Math.random() * 2, Math.random() * 2 - Math.random() * 2, Math.random() * 2 - Math.random() * 2);
		}
	}
}

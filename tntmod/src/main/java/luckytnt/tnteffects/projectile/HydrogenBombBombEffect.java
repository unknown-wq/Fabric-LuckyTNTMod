package luckytnt.tnteffects.projectile;

import java.util.List;

import org.joml.Vector3f;

import luckytnt.LuckyTNTMod;
import luckytnt.network.HydrogenBombS2CPacket;
import luckytnt.registry.BlockRegistry;
import luckytnt.registry.EffectRegistry;
import luckytnt.util.NuclearBombLike;
import luckytntlib.util.IExplosiveEntity;
import luckytntlib.util.explosions.ExplosionHelper;
import luckytntlib.util.explosions.IForEachBlockExplosionEffect;
import luckytntlib.util.explosions.ImprovedExplosion;
import luckytntlib.util.tnteffects.PrimedTNTEffect;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.Registries;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class HydrogenBombBombEffect extends PrimedTNTEffect implements NuclearBombLike {

	@Override
	public void serverExplosion(IExplosiveEntity ent) {
		if(ent.getLevel() instanceof ServerWorld sworld) {
			for(ServerWorld sw : sworld.getServer().getWorlds()) {
				for(ServerPlayerEntity player : sw.getPlayers()) {
					if(player.getWorld().getDimension() == sworld.getDimension() && player.distanceTo((Entity)ent) <= 150) {
						LuckyTNTMod.RH.sendS2CPacket(player, new HydrogenBombS2CPacket(((Entity)ent).getId()));
					}
				}
			}
		}
		
		ImprovedExplosion explosion = new ImprovedExplosion(ent.getLevel(), (Entity)ent, ent.getPos(), 230);
		explosion.doEntityExplosion(25f, true);
		explosion.doBlockExplosion(1f, 1f, 0.167f, 0.05f, false, true);
		
		ExplosionHelper.doModifiedSphericalExplosion(ent.getLevel(), ent.getPos(), 250, new Vec3d(1f, (2f/3f), 1f), new IForEachBlockExplosionEffect() {
			
			@Override
			public void doBlockExplosion(World level, BlockPos pos, BlockState state, double distance) {
				BlockPos posTop = pos.add(0, 1, 0);
				BlockState stateTop = level.getBlockState(posTop);
				if(distance <= 250) {
					if(Math.random() < 0.25f) {
						if(Block.isFaceFullSquare(state.getCollisionShape(level, pos), Direction.UP) && !state.isAir() && !Block.isFaceFullSquare(stateTop.getCollisionShape(level, posTop), Direction.UP) && stateTop.getBlock().getBlastResistance() < 200) {
							level.setBlockState(posTop, BlockRegistry.NUCLEAR_WASTE.get().getDefaultState(), 3);
						}
					}
				}
			}
		});
		
		List<LivingEntity> list = ent.getLevel().getNonSpectatingEntities(LivingEntity.class, new Box(ent.x() - 90, ent.y() - 65, ent.z() - 90, ent.x() + 90, ent.y() + 65, ent.z() + 90));
		for(LivingEntity living : list) {
			living.addStatusEffect(new StatusEffectInstance(Registries.STATUS_EFFECT.entryOf(EffectRegistry.CONTAMINATED), 4800, 0, true, true, true));
		}
	}
	
	@Override
	public void displayMushroomCloud(IExplosiveEntity ent) {
		for(int count = 0; count < 3000; count++) {
			ent.getLevel().addParticle(new DustParticleEffect(new Vector3f(1f, 2f, 0f), 10f), true, ent.x() + Math.random() * 120 - Math.random() * 120, ent.y() + Math.random() * 6 - Math.random() * 6, ent.z() + Math.random() * 120 - Math.random() * 120, 0, 0, 0);
		}
		for(int count = 0; count < 2000; count++) {
			ent.getLevel().addParticle(new DustParticleEffect(new Vector3f(1f, 2f, 0f), 10f), true, ent.x() + Math.random() * 40 - Math.random() * 40, ent.y() + 6 + Math.random() * 6 - Math.random() * 6, ent.z() + Math.random() * 40 - Math.random() * 40, 0, 0, 0);
		}
		for(int count = 0; count < 1600; count++) {
			ent.getLevel().addParticle(new DustParticleEffect(new Vector3f(1f, 2f, 0f), 10f), true, ent.x() + Math.random() * 20 - Math.random() * 20, ent.y() + Math.random() * 6 - Math.random() * 6, ent.z() + Math.random() * 20 - Math.random() * 20, 0, 0, 0);
		}
		for(int count = 0; count < 1200; count++) {
			ent.getLevel().addParticle(new DustParticleEffect(new Vector3f(1f, 2f, 0f), 10f), true, ent.x() + Math.random() * 12 - Math.random() * 12, ent.y() + 8 + Math.random() * 6 - Math.random() * 6, ent.z() + Math.random() * 12 - Math.random() * 12, 0, 0, 0);
		}
		for(int count = 0; count < 1200; count++) {
			ent.getLevel().addParticle(new DustParticleEffect(new Vector3f(1f, 2f, 0f), 10f), true, ent.x() + Math.random() * 4 - Math.random() * 4, ent.y() + 30 + Math.random() * 24 - Math.random() * 24, ent.z() + Math.random() * 4 - Math.random() * 4, 0, 0, 0);
		}
		for(int count = 0; count < 1200; count++) {
			ent.getLevel().addParticle(new DustParticleEffect(new Vector3f(1f, 2f, 0f), 10f), true, ent.x() + Math.random() * 12 - Math.random() * 12, ent.y() + 44 + Math.random() * 6 - Math.random() * 6, ent.z() + Math.random() * 12 - Math.random() * 12, 0, 0, 0);
		}
		for(int count = 0; count < 1200; count++) {
			ent.getLevel().addParticle(new DustParticleEffect(new Vector3f(1f, 2f, 0f), 10f), true, ent.x() + Math.random() * 12 - Math.random() * 12, ent.y() + 58 + Math.random() * 6 - Math.random() * 6, ent.z() + Math.random() * 12 - Math.random() * 12, 0, 0, 0);
		}
		for(int count = 0; count < 4000; count++) {
			ent.getLevel().addParticle(new DustParticleEffect(new Vector3f(1f, 2f, 0f), 10f), true, ent.x() + Math.random() * 24 - Math.random() * 24, ent.y() + 48 + Math.random() * 12 - Math.random() * 12, ent.z() + Math.random() * 24 - Math.random() * 24, 0, 0, 0);
		}
		for(int count = 0; count < 4000; count++) {
			ent.getLevel().addParticle(ParticleTypes.LARGE_SMOKE, true, ent.x() + Math.random() * 4 - Math.random() * 4, ent.y() + 44 + Math.random() * 4 - Math.random() * 4, ent.z() + Math.random() * 4 - Math.random() * 4, Math.random() * 4 - Math.random() * 4, Math.random() * 4 - Math.random() * 4, Math.random() * 4 - Math.random() * 4);
		}
	}
	
	@Override
	public void spawnParticles(IExplosiveEntity ent) {
		
	}
	
	@Override
	public float getSize(IExplosiveEntity ent) {
		return 1.5f;
	}
}

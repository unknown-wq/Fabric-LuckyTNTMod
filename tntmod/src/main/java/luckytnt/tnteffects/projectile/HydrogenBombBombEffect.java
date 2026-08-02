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
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.AABB;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.Heightmap;

public class HydrogenBombBombEffect extends PrimedTNTEffect implements NuclearBombLike {

	@Override
	public void serverExplosion(IExplosiveEntity ent) {
		if(ent.getLevel() instanceof ServerLevel sworld) {
			for(ServerLevel sw : sworld.getServer().getAllLevels()) {
				for(ServerPlayer player : sw.players()) {
					if(player.level().dimension() == sworld.dimension() && player.distanceTo((Entity)ent) <= 150) {
						LuckyTNTMod.RH.sendS2CPacket(player, new HydrogenBombS2CPacket(((Entity)ent).getId()));
					}
				}
			}
		}
		
		ImprovedExplosion explosion = new ImprovedExplosion(ent.getLevel(), (Entity)ent, ent.getPos(), 230);
		explosion.doEntityExplosion(25f, true);
		explosion.doBlockExplosion(1f, 1f, 0.167f, 0.05f, false, true);
		
		spreadNuclearWaste(ent);

		List<LivingEntity> list = ent.getLevel().getEntitiesOfClass(LivingEntity.class, new AABB(ent.x() - 90, ent.y() - 65, ent.z() - 90, ent.x() + 90, ent.y() + 65, ent.z() + 90));
		for(LivingEntity living : list) {
			living.addEffect(new MobEffectInstance(BuiltInRegistries.MOB_EFFECT.getOrThrow(EffectRegistry.CONTAMINATED), 4800, 0, true, true, true));
		}
	}

	/**
	 * Covers the surface inside the r=250, (1, 2/3, 1) scaled ellipsoid of the blast with nuclear waste.
	 * <p>This used to be a {@code doModifiedSphericalExplosion} over the whole ellipsoid, but the callback
	 * only ever wrote to {@code pos.above()} of a block with a full upwards facing collision face - a surface
	 * operation wearing a volume operation's clothes. The volume contains 4/3*pi*250*204*250 = 53.4M cells,
	 * each of which cost a {@code new BlockPos} plus a {@code Level.getBlockState}, and because
	 * {@code Level.getBlockState} resolves its chunk with load-or-generate the 500x500 footprint pulled
	 * ~1024 chunks to ChunkStatus.FULL synchronously.
	 * <p>Walking the r=250 disc instead visits 196 321 columns, the unchanged 25% roll keeps ~49 000 of them
	 * and each of those does one heightmap lookup plus the same two block reads as before:
	 * <b>53.4M block reads -&gt; ~98k</b> (a ~270x cut), no allocation per cell and, thanks to the
	 * {@code isLoaded} guard, not a single chunk is generated that was not already loaded.
	 * <p>The only behavioural difference is that waste is no longer placed on ledges buried under the
	 * surface (cave floors, overhangs) - only on the topmost block of every column, which is the only
	 * placement that was ever visible.
	 */
	private static void spreadNuclearWaste(IExplosiveEntity ent) {
		if(!(ent.getLevel() instanceof ServerLevel level)) {
			return;
		}
		final RandomSource random = level.getRandom();
		final BlockState waste = BlockRegistry.NUCLEAR_WASTE.get().defaultBlockState();
		final int cx = Mth.floor(ent.x());
		final int cy = Mth.floor(ent.y());
		final int cz = Mth.floor(ent.z());
		final BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
		final BlockPos.MutableBlockPos posTop = new BlockPos.MutableBlockPos();
		for(int offX = -250; offX <= 250; offX++) {
			final int xSqr = offX * offX;
			for(int offZ = -250; offZ <= 250; offZ++) {
				final int xzSqr = xSqr + offZ * offZ;
				if(xzSqr > 62500) {
					continue;
				}
				//the 25% roll is by far the cheapest of all the tests, so it runs before any chunk is touched
				if(random.nextFloat() >= 0.25f) {
					continue;
				}
				final int x = cx + offX;
				final int z = cz + offZ;
				pos.set(x, cy, z);
				if(!level.isLoaded(pos)) {
					continue;
				}
				//OCEAN_FLOOR is the topmost block that blocks motion and is not a fluid, i.e. exactly the
				//kind of block the old callback was looking for, and it is a live heightmap read
				final int topY = level.getHeight(Heightmap.Types.OCEAN_FLOOR, x, z) - 1;
				final int offY = topY - cy;
				//the ellipsoid: offX^2 + offY^2 / (2/3) + offZ^2 <= 250^2, clipped to the |offY| <= 166 the
				//old traversal actually iterated over
				if(offY > 166 || offY < -166 || xzSqr + (double)offY * offY / (2f / 3f) > 62500d) {
					continue;
				}
				pos.set(x, topY, z);
				final BlockState state = level.getBlockState(pos);
				if(state.isAir() || !Block.isFaceFull(state.getCollisionShape(level, pos), Direction.UP)) {
					continue;
				}
				posTop.set(x, topY + 1, z);
				final BlockState stateTop = level.getBlockState(posTop);
				if(!Block.isFaceFull(stateTop.getCollisionShape(level, posTop), Direction.UP) && stateTop.getBlock().getExplosionResistance() < 200) {
					//the block below has just been verified to be a full solid face and the replaced block is
					//the last one of the column, so nothing depends on it and no neighbour cascade is needed
					level.setBlock(posTop.immutable(), waste, Block.UPDATE_CLIENTS);
				}
			}
		}
	}

	@Override
	public void displayMushroomCloud(IExplosiveEntity ent) {
		for(int count = 0; count < 3000; count++) {
			ent.getLevel().addParticle(new DustParticleOptions(((int)(1f*255)<<16)|((int)(2f*255)<<8)|(int)(0f*255), 10f), ent.x() + Math.random() * 120 - Math.random() * 120, ent.y() + Math.random() * 6 - Math.random() * 6, ent.z() + Math.random() * 120 - Math.random() * 120, 0, 0, 0);
		}
		for(int count = 0; count < 2000; count++) {
			ent.getLevel().addParticle(new DustParticleOptions(((int)(1f*255)<<16)|((int)(2f*255)<<8)|(int)(0f*255), 10f), ent.x() + Math.random() * 40 - Math.random() * 40, ent.y() + 6 + Math.random() * 6 - Math.random() * 6, ent.z() + Math.random() * 40 - Math.random() * 40, 0, 0, 0);
		}
		for(int count = 0; count < 1600; count++) {
			ent.getLevel().addParticle(new DustParticleOptions(((int)(1f*255)<<16)|((int)(2f*255)<<8)|(int)(0f*255), 10f), ent.x() + Math.random() * 20 - Math.random() * 20, ent.y() + Math.random() * 6 - Math.random() * 6, ent.z() + Math.random() * 20 - Math.random() * 20, 0, 0, 0);
		}
		for(int count = 0; count < 1200; count++) {
			ent.getLevel().addParticle(new DustParticleOptions(((int)(1f*255)<<16)|((int)(2f*255)<<8)|(int)(0f*255), 10f), ent.x() + Math.random() * 12 - Math.random() * 12, ent.y() + 8 + Math.random() * 6 - Math.random() * 6, ent.z() + Math.random() * 12 - Math.random() * 12, 0, 0, 0);
		}
		for(int count = 0; count < 1200; count++) {
			ent.getLevel().addParticle(new DustParticleOptions(((int)(1f*255)<<16)|((int)(2f*255)<<8)|(int)(0f*255), 10f), ent.x() + Math.random() * 4 - Math.random() * 4, ent.y() + 30 + Math.random() * 24 - Math.random() * 24, ent.z() + Math.random() * 4 - Math.random() * 4, 0, 0, 0);
		}
		for(int count = 0; count < 1200; count++) {
			ent.getLevel().addParticle(new DustParticleOptions(((int)(1f*255)<<16)|((int)(2f*255)<<8)|(int)(0f*255), 10f), ent.x() + Math.random() * 12 - Math.random() * 12, ent.y() + 44 + Math.random() * 6 - Math.random() * 6, ent.z() + Math.random() * 12 - Math.random() * 12, 0, 0, 0);
		}
		for(int count = 0; count < 1200; count++) {
			ent.getLevel().addParticle(new DustParticleOptions(((int)(1f*255)<<16)|((int)(2f*255)<<8)|(int)(0f*255), 10f), ent.x() + Math.random() * 12 - Math.random() * 12, ent.y() + 58 + Math.random() * 6 - Math.random() * 6, ent.z() + Math.random() * 12 - Math.random() * 12, 0, 0, 0);
		}
		for(int count = 0; count < 4000; count++) {
			ent.getLevel().addParticle(new DustParticleOptions(((int)(1f*255)<<16)|((int)(2f*255)<<8)|(int)(0f*255), 10f), ent.x() + Math.random() * 24 - Math.random() * 24, ent.y() + 48 + Math.random() * 12 - Math.random() * 12, ent.z() + Math.random() * 24 - Math.random() * 24, 0, 0, 0);
		}
		for(int count = 0; count < 4000; count++) {
			ent.getLevel().addParticle(ParticleTypes.LARGE_SMOKE, ent.x() + Math.random() * 4 - Math.random() * 4, ent.y() + 44 + Math.random() * 4 - Math.random() * 4, ent.z() + Math.random() * 4 - Math.random() * 4, Math.random() * 4 - Math.random() * 4, Math.random() * 4 - Math.random() * 4, Math.random() * 4 - Math.random() * 4);
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

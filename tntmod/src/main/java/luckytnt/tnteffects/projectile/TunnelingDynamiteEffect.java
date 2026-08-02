package luckytnt.tnteffects.projectile;

import net.minecraft.server.level.ServerLevel;


import luckytnt.registry.ItemRegistry;
import luckytntlib.util.IExplosiveEntity;
import luckytntlib.util.explosions.ExplosionHelper;
import luckytntlib.util.explosions.IForEachBlockExplosionEffect;
import luckytntlib.util.explosions.ImprovedExplosion;
import luckytntlib.util.tnteffects.PrimedTNTEffect;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.Item;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.Level;

public class TunnelingDynamiteEffect extends PrimedTNTEffect{

	private static final int TUNNEL_LENGTH = 40;
	private static final int TUNNEL_RADIUS = 4;
	/**
	 * Distance between two spheres along the tunnel axis. Spheres of radius 4 spaced 2 apart still
	 * overlap with a waist radius of sqrt(4^2 - 1^2) = 3.87 (the old spacing of 1 gave 3.97), so the
	 * tunnel stays gapless and visually identical while halving the sphere count from 41 to 21.
	 */
	private static final float SPHERE_SPACING = 2f;

	@Override
	public void serverExplosion(IExplosiveEntity entity) {
		Vec3 direction = entity.getPos().subtract(((Entity)entity).xo, ((Entity)entity).yo, ((Entity)entity).zo).normalize();
		for(float length = 0; length <= TUNNEL_LENGTH; length += SPHERE_SPACING) {
			BlockPos pos = toBlockPos(entity.getPos().add(direction.scale(length)));
			ExplosionHelper.doSphericalExplosion(entity.getLevel(), new Vec3(pos.getX(), pos.getY(), pos.getZ()), TUNNEL_RADIUS, new IForEachBlockExplosionEffect() {

				@Override
				public void doBlockExplosion(Level level, BlockPos pos, BlockState state, double distance) {
					// Air passes the resistance test below, so overlapping spheres used to re-clear
					// everything an earlier sphere had already turned into air. Skipping air here also
					// makes an explicit visited-set unnecessary: a position cleared by a previous sphere
					// is air by the time the next sphere reaches it.
					// (The "distance < 4" test that used to guard this body was dead code - ExplosionHelper
					// only reports positions with distance <= radius.)
					if(state.isAir()) {
						return;
					}
					if(state.getBlock().getExplosionResistance() < 100) {
						state.getBlock().wasExploded((ServerLevel) level, pos, ImprovedExplosion.dummyExplosion(entity.getLevel()));
						level.setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
					}
				}
			});

		}
	}

	@Override
	public Item getItem() {
		return ItemRegistry.TUNNELING_DYNAMITE.get();
	}
}

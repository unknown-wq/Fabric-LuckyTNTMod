package luckytnt.tnteffects;
import net.minecraft.server.level.ServerLevel;


import luckytnt.registry.BlockRegistry;
import luckytntlib.util.IExplosiveEntity;
import luckytntlib.util.explosions.IForEachBlockExplosionEffect;
import luckytntlib.util.explosions.ImprovedExplosion;
import luckytntlib.util.tnteffects.PrimedTNTEffect;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.EnderMan;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.Level;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.EntitySpawnReason;

public class EndTNTEffect extends PrimedTNTEffect {

	private final int strength;
	
	public EndTNTEffect(int strength) {
		this.strength = strength;
	}
	
	/**
	 * Endermen spawned per detonation. The roll below fires for 2.5% of every block the
	 * end explosion touches, which at strength 20 (radius 30) is several hundred mobs
	 * created in a single tick - each with goal selectors, pathfinding and teleport AI.
	 * That was the whole cost of this TNT.
	 */
	private static final int MAX_ENDERMEN = 24;

	@Override
	public void serverExplosion(IExplosiveEntity entity) {
		final Level level = entity.getLevel();
		final Vec3 pos = entity.getPos();
		ImprovedExplosion explosion = new ImprovedExplosion(level, (Entity)entity, pos.x, pos.y + 0.5f, pos.z, strength);
		explosion.doEntityExplosion(2f, true);
		explosion.doBlockExplosion(1f, 1f, 1f, 1.5f, false, false);
		ImprovedExplosion endExplosion = new ImprovedExplosion(level, (Entity)entity, pos.add(0, 0.5f, 0), Mth.floor(strength * 1.5f));
		final RandomSource random = level.getRandom();
		final BlockState endStone = Blocks.END_STONE.defaultBlockState();
		final BlockState chorus = Blocks.CHORUS_FLOWER.defaultBlockState();
		final int[] endermen = new int[1];
		endExplosion.doBlockExplosion(1f, 1f, 1f, 1.5f, false, new IForEachBlockExplosionEffect() {

			@Override
			public void doBlockExplosion(Level level, BlockPos pos, BlockState state, double distance) {
				if(distance > 25 || random.nextFloat() >= 0.9f) {
					return;
				}
				if(level instanceof ServerLevel serverLevel) {
					state.getBlock().wasExploded(serverLevel, pos, endExplosion);
				}
				// Bulk terrain conversion: the neighbour updates flag 3 would fire are spent
				// almost entirely on blocks this same pass overwrites.
				level.setBlock(pos, endStone, Block.UPDATE_CLIENTS);
				if(random.nextFloat() < 0.1f) {
					BlockPos above = pos.above();
					if(level.getBlockState(above).isAir()) {
						level.setBlock(above, chorus, Block.UPDATE_CLIENTS);
					}
				}
				if(endermen[0] < MAX_ENDERMEN && random.nextFloat() < 0.025f) {
					EnderMan enderman = EntityTypes.ENDERMAN.create(level, EntitySpawnReason.MOB_SUMMONED);
					if(enderman != null) {
						enderman.setPos(pos.getX(), pos.getY() + 1f, pos.getZ());
						level.addFreshEntity(enderman);
						endermen[0]++;
					}
				}
			}
		});
	}
	
	@Override
	public void spawnParticles(IExplosiveEntity entity) {
		entity.getLevel().addParticle(ParticleTypes.END_ROD, entity.x() + 0.5D, entity.y() + 1D, entity.z() + 0.5D, 0, 0, 0);
		entity.getLevel().addParticle(ParticleTypes.END_ROD, entity.x() + 0.5D, entity.y() + 1D, entity.z() - 0.5D, 0, 0, 0);
		entity.getLevel().addParticle(ParticleTypes.END_ROD, entity.x() - 0.5D, entity.y() + 1D, entity.z() + 0.5D, 0, 0, 0);
		entity.getLevel().addParticle(ParticleTypes.END_ROD, entity.x() - 0.5D, entity.y() + 1D, entity.z() - 0.5D, 0, 0, 0);
	}
	
	@Override
	public Block getBlock() {
		return BlockRegistry.END_TNT.get();
	}
	
	@Override
	public int getDefaultFuse(IExplosiveEntity entity) {
		return 160;
	}
}

package luckytnt.tnteffects;
import net.minecraft.server.level.ServerLevel;

import luckytnt.registry.BlockRegistry;
import luckytnt.util.Noise3D;
import luckytntlib.util.IExplosiveEntity;
import luckytntlib.util.explosions.ExplosionHelper;
import luckytntlib.util.explosions.IForEachBlockExplosionEffect;
import luckytntlib.util.explosions.ImprovedExplosion;
import luckytntlib.util.tnteffects.PrimedTNTEffect;
import net.minecraft.world.level.block.BeehiveBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.bee.Bee;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.Level;
import net.minecraft.world.entity.EntityTypes;

public class HoneyTNTEffect extends PrimedTNTEffect{

	private final int radius;
	
	public HoneyTNTEffect(int radius) {
		this.radius = radius;
	}
	
	@Override
	public void serverExplosion(IExplosiveEntity entity) {
		//only a sub range of the old radius*4 cube was ever indexed below: x/z reach radius*2, y reaches radius*3.
		//+2 so the highest index used is still an interpolated cell and not the (always zero) border plane.
		final int noiseMaxXZ = radius * 2;
		final int noiseMaxY = radius * 3;
		Noise3D noise = new Noise3D(noiseMaxXZ + 2, noiseMaxY + 2, noiseMaxXZ + 2, 5);
		//loop invariants: the level, the shared dummy explosion, the level RandomSource (replaces the
		//per-block `new Random()`), the constant block states and the explosion centre
		final Level entLevel = entity.getLevel();
		final ImprovedExplosion dummy = ImprovedExplosion.dummyExplosion(entLevel);
		final RandomSource random = entLevel.getRandom();
		final BlockState air = Blocks.AIR.defaultBlockState();
		final BlockState beeNest = Blocks.BEE_NEST.defaultBlockState();
		final BlockState honey = Blocks.HONEY_BLOCK.defaultBlockState();
		final BlockState honeycomb = Blocks.HONEYCOMB_BLOCK.defaultBlockState();
		final float centerX = (float)entity.x();
		final float centerY = (float)entity.y();
		final float centerZ = (float)entity.z();
		final float noiseOffsetY = radius * 1.5f;
		ExplosionHelper.doModifiedSphericalExplosion(entLevel, entity.getPos(), radius, new Vec3(1f, 1.5f, 1f), new IForEachBlockExplosionEffect() {
			@Override
			public void doBlockExplosion(Level level, BlockPos pos, BlockState state, double distance) {
				if(state.getBlock().getExplosionResistance() <= 200) {
					distance += Math.random();
					if(distance <= radius - 2) {
						state.getBlock().wasExploded((ServerLevel)level, pos, dummy);
						//the original wrote AIR and then immediately overwrote it with the bee nest;
						//only the state the block actually ends up in is written now
						if(distance >= radius - 3 && Math.random() < 0.05f) {
							level.setBlockAndUpdate(pos, beeNest.setValue(BeehiveBlock.FACING, getRandomDirectionHorizontal(random)).setValue(BeehiveBlock.HONEY_LEVEL, random.nextInt(6)));
						}
						//writing AIR over AIR is a no-op inside Level#setBlock, so skip the call entirely
						else if(state != air) {
							level.setBlock(pos, air, 3);
						}
						if(Math.random() < 0.025f) {
							Bee bee = new Bee(EntityTypes.BEE, level);
							bee.setPos(pos.getX(), pos.getY(), pos.getZ());
							level.addFreshEntity(bee);
						}
					}
					else if(distance <= radius){
						int offX = Math.round(pos.getX() - centerX);
						int offY = Math.round(pos.getY() - centerY);
						int offZ = Math.round(pos.getZ() - centerZ);
						state.getBlock().wasExploded((ServerLevel)level, pos, dummy);
						//same here: the AIR write was immediately replaced by honey/honeycomb, so it is dropped
						if(noise.getValue(Mth.clamp(offX + radius, 0, noiseMaxXZ), Mth.clamp((int)(offY + noiseOffsetY), 0, noiseMaxY), Mth.clamp(offZ + radius, 0, noiseMaxXZ)) > 0.7f) {
							level.setBlockAndUpdate(pos, honey);
						}
						else {
							level.setBlockAndUpdate(pos, honeycomb);
						}
					}
				}
			}
		});
	}
	
	@Override
	public void spawnParticles(IExplosiveEntity entity){
		entity.getLevel().addParticle(ParticleTypes.DRIPPING_HONEY, entity.x(), entity.y(), entity.z(), 0f, 0f, 0f);
	}
	
	@Override
	public Block getBlock() {
		return BlockRegistry.HONEY_TNT.get();
	}
	
	@Override
	public int getDefaultFuse(IExplosiveEntity entity) {
		return 120;
	}
	
	/**
	 * @deprecated allocates a fresh RandomSource per call, use {@link #getRandomDirectionHorizontal(RandomSource)}
	 */
	@Deprecated
	public Direction getRandomDirectionHorizontal() {
		return getRandomDirectionHorizontal(RandomSource.create());
	}

	public Direction getRandomDirectionHorizontal(RandomSource source) {
		int random = source.nextInt(4);
		switch(random) {
			case 0: return Direction.NORTH;
			case 1: return Direction.EAST;
			case 2: return Direction.SOUTH;
			case 3: return Direction.WEST;
		}
		return Direction.NORTH;
	}
}

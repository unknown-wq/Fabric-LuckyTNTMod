package luckytnt.tnteffects;
import net.minecraft.server.level.ServerLevel;

import java.util.Random;

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
		ExplosionHelper.doModifiedSphericalExplosion(entity.getLevel(), entity.getPos(), radius, new Vec3(1f, 1.5f, 1f), new IForEachBlockExplosionEffect() {		
			@Override
			public void doBlockExplosion(Level level, BlockPos pos, BlockState state, double distance) {
				if(state.getBlock().getExplosionResistance() <= 200) {
					distance += Math.random();
					if(distance <= radius - 2) {
						state.getBlock().wasExploded((ServerLevel)level, pos, ImprovedExplosion.dummyExplosion(entity.getLevel()));
						level.setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
						if(distance >= radius - 3 && Math.random() < 0.05f) {
							level.setBlockAndUpdate(pos, Blocks.BEE_NEST.defaultBlockState().setValue(BeehiveBlock.FACING, getRandomDirectionHorizontal()).setValue(BeehiveBlock.HONEY_LEVEL, new Random().nextInt(6)));
						}
						if(Math.random() < 0.025f) {
							Bee bee = new Bee(EntityTypes.BEE, level);
							bee.setPos(pos.getX(), pos.getY(), pos.getZ());
							level.addFreshEntity(bee);
						}				
					}
					else if(distance <= radius){
						int offX = Math.round(pos.getX() - (float)entity.x());
						int offY = Math.round(pos.getY() - (float)entity.y());
						int offZ = Math.round(pos.getZ() - (float)entity.z());
						state.getBlock().wasExploded((ServerLevel)level, pos, ImprovedExplosion.dummyExplosion(entity.getLevel()));
						level.setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
						if(noise.getValue(Mth.clamp(offX + radius, 0, noiseMaxXZ), Mth.clamp((int)(offY + radius * 1.5f), 0, noiseMaxY), Mth.clamp(offZ + radius, 0, noiseMaxXZ)) > 0.7f) {
							level.setBlockAndUpdate(pos, Blocks.HONEY_BLOCK.defaultBlockState());
						}
						else {
							level.setBlockAndUpdate(pos, Blocks.HONEYCOMB_BLOCK.defaultBlockState());
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
	
	public Direction getRandomDirectionHorizontal() {
		int random = new Random().nextInt(4);
		switch(random) {
			case 0: return Direction.NORTH;
			case 1: return Direction.EAST;
			case 2: return Direction.SOUTH;
			case 3: return Direction.WEST;
		}
		return Direction.NORTH;
	}
}

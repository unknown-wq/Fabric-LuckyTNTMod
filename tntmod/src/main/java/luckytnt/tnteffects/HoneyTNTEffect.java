package luckytnt.tnteffects;

import java.util.Random;

import luckytnt.registry.BlockRegistry;
import luckytnt.util.Noise3D;
import luckytntlib.util.IExplosiveEntity;
import luckytntlib.util.explosions.ExplosionHelper;
import luckytntlib.util.explosions.IForEachBlockExplosionEffect;
import luckytntlib.util.explosions.ImprovedExplosion;
import luckytntlib.util.tnteffects.PrimedTNTEffect;
import net.minecraft.block.BeehiveBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.BeeEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class HoneyTNTEffect extends PrimedTNTEffect{

	private final int radius;
	
	public HoneyTNTEffect(int radius) {
		this.radius = radius;
	}
	
	@Override
	public void serverExplosion(IExplosiveEntity entity) {
		Noise3D noise = new Noise3D(radius * 4, radius * 4, radius * 4, 5);
		ExplosionHelper.doModifiedSphericalExplosion(entity.getLevel(), entity.getPos(), radius, new Vec3d(1f, 1.5f, 1f), new IForEachBlockExplosionEffect() {		
			@Override
			public void doBlockExplosion(World level, BlockPos pos, BlockState state, double distance) {
				if(state.getBlock().getBlastResistance() <= 200) {
					distance += Math.random();
					if(distance <= radius - 2) {
						state.getBlock().onDestroyedByExplosion(level, pos, ImprovedExplosion.dummyExplosion(entity.getLevel()));
						level.setBlockState(pos, Blocks.AIR.getDefaultState(), 3);
						if(distance >= radius - 3 && Math.random() < 0.05f) {
							level.setBlockState(pos, Blocks.BEE_NEST.getDefaultState().with(BeehiveBlock.FACING, getRandomDirectionHorizontal()).with(BeehiveBlock.HONEY_LEVEL, new Random().nextInt(6)));
						}
						if(Math.random() < 0.025f) {
							BeeEntity bee = new BeeEntity(EntityType.BEE, level);
							bee.setPosition(pos.getX(), pos.getY(), pos.getZ());
							level.spawnEntity(bee);
						}				
					}
					else if(distance <= radius){
						int offX = Math.round(pos.getX() - (float)entity.x());
						int offY = Math.round(pos.getY() - (float)entity.y());
						int offZ = Math.round(pos.getZ() - (float)entity.z());
						state.getBlock().onDestroyedByExplosion(level, pos, ImprovedExplosion.dummyExplosion(entity.getLevel()));
						level.setBlockState(pos, Blocks.AIR.getDefaultState(), 3);
						if(noise.getValue(MathHelper.clamp(offX + radius, 0, radius * 4), MathHelper.clamp((int)(offY + radius * 1.5f), 0, radius * 4), MathHelper.clamp(offZ + radius, 0, radius * 4)) > 0.7f) {
							level.setBlockState(pos, Blocks.HONEY_BLOCK.getDefaultState());
						}
						else {
							level.setBlockState(pos, Blocks.HONEYCOMB_BLOCK.getDefaultState());
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

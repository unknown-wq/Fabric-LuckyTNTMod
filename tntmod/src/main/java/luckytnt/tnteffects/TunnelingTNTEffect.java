package luckytnt.tnteffects;

import luckytnt.block.TunnelingTNTBlock;
import luckytnt.registry.BlockRegistry;
import luckytntlib.util.IExplosiveEntity;
import luckytntlib.util.tnteffects.PrimedTNTEffect;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.explosion.Explosion;

public class TunnelingTNTEffect extends PrimedTNTEffect{

	@Override
	public void serverExplosion(IExplosiveEntity entity) {
		Direction direction = Direction.byName(entity.getPersistentData().getString("direction")) != null ? Direction.byName(entity.getPersistentData().getString("direction")) : Direction.EAST;
		switch(direction) {
			case NORTH: for(double offX = -4; offX <= 4; offX++) {
							for(double offY = -4; offY <= 4; offY++) {
								for(double offZ = -90; offZ <= 0; offZ++) {
									double distance = Math.sqrt(offX * offX + offY * offY);
									BlockPos pos = new BlockPos(MathHelper.floor(entity.x() + offX), MathHelper.floor(entity.y() + offY), MathHelper.floor(entity.z() + offZ));
									BlockState state = entity.getLevel().getBlockState(pos);
									if(distance < 4 && state.getBlock().getBlastResistance() < 100) {
										Block block = state.getBlock();
										block.onDestroyedByExplosion(entity.getLevel(), pos, new Explosion(entity.getLevel(), (Entity) entity, entity.x(), entity.y(), entity.z(), 0, false, Explosion.DestructionType.DESTROY_WITH_DECAY));
										entity.getLevel().setBlockState(pos, Blocks.AIR.getDefaultState(), 3);
									}
								}
							}
						}
						break;
			case EAST: for(double offX = 0; offX <= 90; offX++) {
							for(double offY = -4; offY <= 4; offY++) {
								for(double offZ = -4; offZ <= 4; offZ++) {
									double distance = Math.sqrt(offZ * offZ + offY * offY);
									BlockPos pos = new BlockPos(MathHelper.floor(entity.x() + offX), MathHelper.floor(entity.y() + offY), MathHelper.floor(entity.z() + offZ));
									BlockState state = entity.getLevel().getBlockState(pos);
									if(distance < 4 && state.getBlock().getBlastResistance() < 100) {
										Block block = state.getBlock();
										block.onDestroyedByExplosion(entity.getLevel(), pos, new Explosion(entity.getLevel(), (Entity) entity, entity.x(), entity.y(), entity.z(), 0, false, Explosion.DestructionType.DESTROY_WITH_DECAY));
										entity.getLevel().setBlockState(pos, Blocks.AIR.getDefaultState(), 3);
									}
								}
							}
						}
						break;
			case SOUTH: for(double offX = -4; offX <= 4; offX++) {
							for(double offY = -4; offY <= 4; offY++) {
								for(double offZ = 0; offZ <= 90; offZ++) {
									double distance = Math.sqrt(offX * offX + offY * offY);
									BlockPos pos = new BlockPos(MathHelper.floor(entity.x() + offX), MathHelper.floor(entity.y() + offY), MathHelper.floor(entity.z() + offZ));
									BlockState state = entity.getLevel().getBlockState(pos);
									if(distance < 4 && state.getBlock().getBlastResistance() < 100) {
										Block block = state.getBlock();
										block.onDestroyedByExplosion(entity.getLevel(), pos, new Explosion(entity.getLevel(), (Entity) entity, entity.x(), entity.y(), entity.z(), 0, false, Explosion.DestructionType.DESTROY_WITH_DECAY));
										entity.getLevel().setBlockState(pos, Blocks.AIR.getDefaultState(), 3);
									}
								}
							}
						}
						break;
			case WEST: for(double offX = -90; offX <= 0; offX++) {
							for(double offY = -4; offY <= 4; offY++) {
								for(double offZ = -4; offZ <= 4; offZ++) {
									double distance = Math.sqrt(offZ * offZ + offY * offY);
									BlockPos pos = new BlockPos(MathHelper.floor(entity.x() + offX), MathHelper.floor(entity.y() + offY), MathHelper.floor(entity.z() + offZ));
									BlockState state = entity.getLevel().getBlockState(pos);
									if(distance < 4 && state.getBlock().getBlastResistance() < 100) {
										Block block = state.getBlock();
										block.onDestroyedByExplosion(entity.getLevel(), pos, new Explosion(entity.getLevel(), (Entity) entity, entity.x(), entity.y(), entity.z(), 0, false, Explosion.DestructionType.DESTROY_WITH_DECAY));
										entity.getLevel().setBlockState(pos, Blocks.AIR.getDefaultState(), 3);
									}
								}
							}
						}
						break;
			default: break;
		}
	}
	
	@Override
	public BlockState getBlockState(IExplosiveEntity entity) {
		return BlockRegistry.TUNNELING_TNT.get().getDefaultState().with(TunnelingTNTBlock.FACING, Direction.byName(entity.getPersistentData().getString("direction")) != null ? Direction.byName(entity.getPersistentData().getString("direction")) : Direction.EAST);
	}
	
	@Override
	public Block getBlock() {
		return BlockRegistry.TUNNELING_TNT.get();
	}
}

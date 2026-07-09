package luckytnt.tnteffects;

import net.minecraft.server.level.ServerLevel;
import luckytnt.block.TunnelingTNTBlock;
import luckytnt.registry.BlockRegistry;
import luckytntlib.util.IExplosiveEntity;
import luckytntlib.util.tnteffects.PrimedTNTEffect;
import luckytntlib.util.explosions.ImprovedExplosion;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;

public class TunnelingTNTEffect extends PrimedTNTEffect{

	@Override
	public void serverExplosion(IExplosiveEntity entity) {
		Direction direction = Direction.byName(entity.getPersistentData().getStringOr("direction", "")) != null ? Direction.byName(entity.getPersistentData().getStringOr("direction", "")) : Direction.EAST;
		switch(direction) {
			case NORTH: for(double offX = -4; offX <= 4; offX++) {
							for(double offY = -4; offY <= 4; offY++) {
								for(double offZ = -90; offZ <= 0; offZ++) {
									double distance = Math.sqrt(offX * offX + offY * offY);
									BlockPos pos = new BlockPos(Mth.floor(entity.x() + offX), Mth.floor(entity.y() + offY), Mth.floor(entity.z() + offZ));
									BlockState state = entity.getLevel().getBlockState(pos);
									if(distance < 4 && state.getBlock().getExplosionResistance() < 100) {
										Block block = state.getBlock();
										block.wasExploded((ServerLevel)entity.getLevel(), pos, ImprovedExplosion.dummyExplosion(entity.getLevel()));
										entity.getLevel().setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
									}
								}
							}
						}
						break;
			case EAST: for(double offX = 0; offX <= 90; offX++) {
							for(double offY = -4; offY <= 4; offY++) {
								for(double offZ = -4; offZ <= 4; offZ++) {
									double distance = Math.sqrt(offZ * offZ + offY * offY);
									BlockPos pos = new BlockPos(Mth.floor(entity.x() + offX), Mth.floor(entity.y() + offY), Mth.floor(entity.z() + offZ));
									BlockState state = entity.getLevel().getBlockState(pos);
									if(distance < 4 && state.getBlock().getExplosionResistance() < 100) {
										Block block = state.getBlock();
										block.wasExploded((ServerLevel)entity.getLevel(), pos, ImprovedExplosion.dummyExplosion(entity.getLevel()));
										entity.getLevel().setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
									}
								}
							}
						}
						break;
			case SOUTH: for(double offX = -4; offX <= 4; offX++) {
							for(double offY = -4; offY <= 4; offY++) {
								for(double offZ = 0; offZ <= 90; offZ++) {
									double distance = Math.sqrt(offX * offX + offY * offY);
									BlockPos pos = new BlockPos(Mth.floor(entity.x() + offX), Mth.floor(entity.y() + offY), Mth.floor(entity.z() + offZ));
									BlockState state = entity.getLevel().getBlockState(pos);
									if(distance < 4 && state.getBlock().getExplosionResistance() < 100) {
										Block block = state.getBlock();
										block.wasExploded((ServerLevel)entity.getLevel(), pos, ImprovedExplosion.dummyExplosion(entity.getLevel()));
										entity.getLevel().setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
									}
								}
							}
						}
						break;
			case WEST: for(double offX = -90; offX <= 0; offX++) {
							for(double offY = -4; offY <= 4; offY++) {
								for(double offZ = -4; offZ <= 4; offZ++) {
									double distance = Math.sqrt(offZ * offZ + offY * offY);
									BlockPos pos = new BlockPos(Mth.floor(entity.x() + offX), Mth.floor(entity.y() + offY), Mth.floor(entity.z() + offZ));
									BlockState state = entity.getLevel().getBlockState(pos);
									if(distance < 4 && state.getBlock().getExplosionResistance() < 100) {
										Block block = state.getBlock();
										block.wasExploded((ServerLevel)entity.getLevel(), pos, ImprovedExplosion.dummyExplosion(entity.getLevel()));
										entity.getLevel().setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
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
		return BlockRegistry.TUNNELING_TNT.get().defaultBlockState().setValue(TunnelingTNTBlock.FACING, Direction.byName(entity.getPersistentData().getStringOr("direction", "")) != null ? Direction.byName(entity.getPersistentData().getStringOr("direction", "")) : Direction.EAST);
	}
	
	@Override
	public Block getBlock() {
		return BlockRegistry.TUNNELING_TNT.get();
	}
}

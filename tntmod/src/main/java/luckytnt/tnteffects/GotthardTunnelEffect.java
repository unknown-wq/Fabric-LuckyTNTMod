package luckytnt.tnteffects;
import net.minecraft.server.level.ServerLevel;

import java.util.List;

import luckytnt.block.GotthardTunnelBlock;
import luckytnt.registry.BlockRegistry;
import luckytnt.util.Materials;
import luckytntlib.util.IExplosiveEntity;
import luckytntlib.util.explosions.ImprovedExplosion;
import luckytntlib.util.tnteffects.PrimedTNTEffect;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.tags.BlockTags;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.LightLayer;

public class GotthardTunnelEffect extends PrimedTNTEffect {
	
	@Override
	public void serverExplosion(IExplosiveEntity ent) {
		boolean streets = ent.getPersistentData().getBooleanOr("streets", false);
		Direction dir = Direction.byName(ent.getPersistentData().getStringOr("direction", "")) != null ? Direction.byName(ent.getPersistentData().getStringOr("direction", "")) : Direction.NORTH;
		switch(dir) {
			case NORTH: for(int offZ = 0; offZ >= dir.getStepZ() * 200; offZ--) {
							for(int offX = -10; offX <= 10; offX++) {
								for(int offY = 0; offY <= 15; offY++) {
									BlockPos pos = toBlockPos(new Vec3(ent.x() + offX, ent.y() + offY, ent.z() + offZ));
									BlockState state = ent.getLevel().getBlockState(pos);
									if(state.getBlock().getExplosionResistance() <= 200) {
										state.getBlock().wasExploded((ServerLevel)ent.getLevel(), pos, ImprovedExplosion.dummyExplosion(ent.getLevel()));
										ent.getLevel().setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
									}
								}
							}
						}
						placeWalls(ent, dir);
						if(streets) {
							createStreet(ent, dir);
						}
						placeLights(ent, dir);
						break;
						
			case EAST: for(int offX = 0; offX <= dir.getStepX() * 200; offX++) {
							for(int offZ = -10; offZ <= 10; offZ++) {
								for(int offY = 0; offY <= 15; offY++) {
									BlockPos pos = toBlockPos(new Vec3(ent.x() + offX, ent.y() + offY, ent.z() + offZ));
									BlockState state = ent.getLevel().getBlockState(pos);
									if(state.getBlock().getExplosionResistance() <= 200) {
										state.getBlock().wasExploded((ServerLevel)ent.getLevel(), pos, ImprovedExplosion.dummyExplosion(ent.getLevel()));
										ent.getLevel().setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
									}
								}
							}
						}
						placeWalls(ent, dir);
						if(streets) {
							createStreet(ent, dir);
						}
						placeLights(ent, dir);
						break;
			
			case SOUTH: for(int offZ = 0; offZ <= dir.getStepZ() * 200; offZ++) {
							for(int offX = -10; offX <= 10; offX++) {
								for(int offY = 0; offY <= 15; offY++) {
									BlockPos pos = toBlockPos(new Vec3(ent.x() + offX, ent.y() + offY, ent.z() + offZ));
									BlockState state = ent.getLevel().getBlockState(pos);
									if(state.getBlock().getExplosionResistance() <= 200) {
										state.getBlock().wasExploded((ServerLevel)ent.getLevel(), pos, ImprovedExplosion.dummyExplosion(ent.getLevel()));
										ent.getLevel().setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
									}
								}
							}
						}
						placeWalls(ent, dir);
						if(streets) {
							createStreet(ent, dir);
						}
						placeLights(ent, dir);
						break;
						
			case WEST: for(int offX = 0; offX >= dir.getStepX() * 200; offX--) {
							for(int offZ = -10; offZ <= 10; offZ++) {
								for(int offY = 0; offY <= 15; offY++) {
									BlockPos pos = toBlockPos(new Vec3(ent.x() + offX, ent.y() + offY, ent.z() + offZ));
									BlockState state = ent.getLevel().getBlockState(pos);
									if(state.getBlock().getExplosionResistance() <= 200) {
										state.getBlock().wasExploded((ServerLevel)ent.getLevel(), pos, ImprovedExplosion.dummyExplosion(ent.getLevel()));
										ent.getLevel().setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
									}
								}
							}
						}
						placeWalls(ent, dir);
						if(streets) {
							createStreet(ent, dir);
						}
						placeLights(ent, dir);
						break;
			
			default: break;
		}
	}
	
	@Override
	public BlockState getBlockState(IExplosiveEntity ent) {
		Direction dir = Direction.NORTH;
		if(!ent.getPersistentData().getStringOr("direction", "").equals("")) {
			dir = Direction.byName(ent.getPersistentData().getStringOr("direction", ""));
		} 
		return BlockRegistry.GOTTHARD_TUNNEL.get().defaultBlockState().setValue(GotthardTunnelBlock.STREETS, ent.getPersistentData().getBooleanOr("streets", false)).setValue(GotthardTunnelBlock.FACING, dir);
	}
	
	@Override
	public int getDefaultFuse(IExplosiveEntity ent) {
		return 200;
	}
	
	public void placeLights(IExplosiveEntity ent, Direction dir) {
		switch(dir) {
			case NORTH: for(int offZ = -2; offZ >= dir.getStepZ() * 200; offZ -= 4) { 
							BlockPos pos1 = toBlockPos(new Vec3(ent.x(), ent.y() - 1, ent.z() + offZ));
							BlockPos pos2 = toBlockPos(new Vec3(ent.x() + 10, ent.y() - 1, ent.z() + offZ));
							BlockPos pos3 = toBlockPos(new Vec3(ent.x() - 10, ent.y() - 1, ent.z() + offZ));
							
							BlockPos pos4 = toBlockPos(new Vec3(ent.x() + 11, ent.y() + 7, ent.z() + offZ));
							BlockPos pos5 = toBlockPos(new Vec3(ent.x() + 11, ent.y() + 8, ent.z() + offZ));
							BlockPos pos6 = toBlockPos(new Vec3(ent.x() - 11, ent.y() + 7, ent.z() + offZ));
							BlockPos pos7 = toBlockPos(new Vec3(ent.x() - 11, ent.y() + 8, ent.z() + offZ));
							
							BlockPos pos8 = toBlockPos(new Vec3(ent.x(), ent.y() + 16, ent.z() + offZ));
							
							List<BlockPos> list = List.of(pos1, pos2, pos3, pos4, pos5, pos6, pos7, pos8);
							
							for(BlockPos pos : list) {
								if(!ent.getLevel().canSeeSky(pos) && ent.getLevel().getBlockState(pos).getBlock().getExplosionResistance() <= 200) {
									ent.getLevel().getBlockState(pos).getBlock().wasExploded((ServerLevel)ent.getLevel(), pos, ImprovedExplosion.dummyExplosion(ent.getLevel()));
									ent.getLevel().setBlock(pos, Blocks.SEA_LANTERN.defaultBlockState(), 3);
								}
							}
						}
						break;
						
			case EAST: 	for(int offX = 2; offX <= dir.getStepX() * 200; offX += 4) {
							BlockPos pos1 = toBlockPos(new Vec3(ent.x() + offX, ent.y() - 1, ent.z()));
							BlockPos pos2 = toBlockPos(new Vec3(ent.x() + offX, ent.y() - 1, ent.z() + 10));
							BlockPos pos3 = toBlockPos(new Vec3(ent.x() + offX, ent.y() - 1, ent.z() - 10));
							
							BlockPos pos4 = toBlockPos(new Vec3(ent.x() + offX, ent.y() + 7, ent.z() + 11));
							BlockPos pos5 = toBlockPos(new Vec3(ent.x() + offX, ent.y() + 8, ent.z() + 11));
							BlockPos pos6 = toBlockPos(new Vec3(ent.x() + offX, ent.y() + 7, ent.z() - 11));
							BlockPos pos7 = toBlockPos(new Vec3(ent.x() + offX, ent.y() + 8, ent.z() - 11));
							
							BlockPos pos8 = toBlockPos(new Vec3(ent.x() + offX, ent.y() + 16, ent.z()));
							
							List<BlockPos> list = List.of(pos1, pos2, pos3, pos4, pos5, pos6, pos7, pos8);
							
							for(BlockPos pos : list) {
								if(!ent.getLevel().canSeeSky(pos) && ent.getLevel().getBlockState(pos).getBlock().getExplosionResistance() <= 200) {
									ent.getLevel().getBlockState(pos).getBlock().wasExploded((ServerLevel)ent.getLevel(), pos, ImprovedExplosion.dummyExplosion(ent.getLevel()));
									ent.getLevel().setBlock(pos, Blocks.SEA_LANTERN.defaultBlockState(), 3);
								}
							}
						}
						break;
						
			case SOUTH: for(int offZ = 2; offZ <= dir.getStepZ() * 200; offZ += 4) {
							BlockPos pos1 = toBlockPos(new Vec3(ent.x(), ent.y() - 1, ent.z() + offZ));
							BlockPos pos2 = toBlockPos(new Vec3(ent.x() + 10, ent.y() - 1, ent.z() + offZ));
							BlockPos pos3 = toBlockPos(new Vec3(ent.x() - 10, ent.y() - 1, ent.z() + offZ));
							
							BlockPos pos4 = toBlockPos(new Vec3(ent.x() + 11, ent.y() + 7, ent.z() + offZ));
							BlockPos pos5 = toBlockPos(new Vec3(ent.x() + 11, ent.y() + 8, ent.z() + offZ));
							BlockPos pos6 = toBlockPos(new Vec3(ent.x() - 11, ent.y() + 7, ent.z() + offZ));
							BlockPos pos7 = toBlockPos(new Vec3(ent.x() - 11, ent.y() + 8, ent.z() + offZ));
							
							BlockPos pos8 = toBlockPos(new Vec3(ent.x(), ent.y() + 16, ent.z() + offZ));
							
							List<BlockPos> list = List.of(pos1, pos2, pos3, pos4, pos5, pos6, pos7, pos8);
							
							for(BlockPos pos : list) {
								if(!ent.getLevel().canSeeSky(pos) && ent.getLevel().getBlockState(pos).getBlock().getExplosionResistance() <= 200) {
									ent.getLevel().getBlockState(pos).getBlock().wasExploded((ServerLevel)ent.getLevel(), pos, ImprovedExplosion.dummyExplosion(ent.getLevel()));
									ent.getLevel().setBlock(pos, Blocks.SEA_LANTERN.defaultBlockState(), 3);
								}
							}
						}
						break;
						
			case WEST: 	for(int offX = -2; offX >= dir.getStepX() * 200; offX -= 4) {
							BlockPos pos1 = toBlockPos(new Vec3(ent.x() + offX, ent.y() - 1, ent.z()));
							BlockPos pos2 = toBlockPos(new Vec3(ent.x() + offX, ent.y() - 1, ent.z() + 10));
							BlockPos pos3 = toBlockPos(new Vec3(ent.x() + offX, ent.y() - 1, ent.z() - 10));
							
							BlockPos pos4 = toBlockPos(new Vec3(ent.x() + offX, ent.y() + 7, ent.z() + 11));
							BlockPos pos5 = toBlockPos(new Vec3(ent.x() + offX, ent.y() + 8, ent.z() + 11));
							BlockPos pos6 = toBlockPos(new Vec3(ent.x() + offX, ent.y() + 7, ent.z() - 11));
							BlockPos pos7 = toBlockPos(new Vec3(ent.x() + offX, ent.y() + 8, ent.z() - 11));
							
							BlockPos pos8 = toBlockPos(new Vec3(ent.x() + offX, ent.y() + 16, ent.z()));
							
							List<BlockPos> list = List.of(pos1, pos2, pos3, pos4, pos5, pos6, pos7, pos8);
							
							for(BlockPos pos : list) {
								if(!ent.getLevel().canSeeSky(pos) && ent.getLevel().getBlockState(pos).getBlock().getExplosionResistance() <= 200) {
									ent.getLevel().getBlockState(pos).getBlock().wasExploded((ServerLevel)ent.getLevel(), pos, ImprovedExplosion.dummyExplosion(ent.getLevel()));
									ent.getLevel().setBlock(pos, Blocks.SEA_LANTERN.defaultBlockState(), 3);
								}
							}
						}
						break;
						
			default: break;
		}
	}
	
	public void createStreet(IExplosiveEntity ent, Direction dir) {
		switch(dir) {
			case NORTH: for(int offZ = 0; offZ >= dir.getStepZ() * 200; offZ--) { 
							for(int offX = 1; offX <= 9; offX++) {
								BlockPos pos1 = toBlockPos(new Vec3(ent.x() + offX, ent.y() - 1, ent.z() + offZ));
								BlockPos pos2 = toBlockPos(new Vec3(ent.x() + (offX * -1), ent.y() - 1, ent.z() + offZ));
								if(!ent.getLevel().canSeeSky(pos1) && ent.getLevel().getBlockState(pos1).getBlock().getExplosionResistance() <= 200) {
									ent.getLevel().getBlockState(pos1).getBlock().wasExploded((ServerLevel)ent.getLevel(), pos1, ImprovedExplosion.dummyExplosion(ent.getLevel()));
									ent.getLevel().setBlock(pos1, Blocks.GRAY_CONCRETE.defaultBlockState(), 3);
								}
								if(!ent.getLevel().canSeeSky(pos2) && ent.getLevel().getBlockState(pos2).getBlock().getExplosionResistance() <= 200) {
									ent.getLevel().getBlockState(pos2).getBlock().wasExploded((ServerLevel)ent.getLevel(), pos2, ImprovedExplosion.dummyExplosion(ent.getLevel()));
									ent.getLevel().setBlock(pos2, Blocks.GRAY_CONCRETE.defaultBlockState(), 3);
								}
							}
							
							BlockPos pos1 = toBlockPos(new Vec3(ent.x(), ent.y() - 1, ent.z() + offZ));
							BlockPos pos2 = toBlockPos(new Vec3(ent.x() + 10, ent.y() - 1, ent.z() + offZ));
							BlockPos pos3 = toBlockPos(new Vec3(ent.x() - 10, ent.y() - 1, ent.z() + offZ));
							List<BlockPos> list = List.of(pos1, pos2, pos3);
							
							for(BlockPos pos : list) {
								if(!ent.getLevel().canSeeSky(pos) && ent.getLevel().getBlockState(pos).getBlock().getExplosionResistance() <= 200) {
									ent.getLevel().getBlockState(pos).getBlock().wasExploded((ServerLevel)ent.getLevel(), pos, ImprovedExplosion.dummyExplosion(ent.getLevel()));
									ent.getLevel().setBlock(pos, Blocks.SMOOTH_STONE.defaultBlockState(), 3);
								}
							}
							
							if(offZ % 5 == 0) {
								BlockPos pos4 = toBlockPos(new Vec3(ent.x() + 5, ent.y() - 1, ent.z() + offZ + 2));
								BlockPos pos5 = toBlockPos(new Vec3(ent.x() + 5, ent.y() - 1, ent.z() + offZ + 3));
								BlockPos pos6 = toBlockPos(new Vec3(ent.x() + 5, ent.y() - 1, ent.z() + offZ + 4));
								
								BlockPos pos7 = toBlockPos(new Vec3(ent.x() - 5, ent.y() - 1, ent.z() + offZ + 2));
								BlockPos pos8 = toBlockPos(new Vec3(ent.x() - 5, ent.y() - 1, ent.z() + offZ + 3));
								BlockPos pos9 = toBlockPos(new Vec3(ent.x() - 5, ent.y() - 1, ent.z() + offZ + 4));
								List<BlockPos> list2 = List.of(pos4, pos5, pos6, pos7, pos8, pos9);
								
								for(BlockPos pos : list2) {
									if(!ent.getLevel().canSeeSky(pos) && ent.getLevel().getBlockState(pos).getBlock().getExplosionResistance() <= 200) {
										ent.getLevel().getBlockState(pos).getBlock().wasExploded((ServerLevel)ent.getLevel(), pos, ImprovedExplosion.dummyExplosion(ent.getLevel()));
										ent.getLevel().setBlock(pos, Blocks.YELLOW_CONCRETE.defaultBlockState(), 3);
									}
								}
							}
						}
						break;
						
			case EAST:	for(int offX = 0; offX <= dir.getStepX() * 200; offX++) {
							for(int offZ = 1; offZ <= 9; offZ++) {
								BlockPos pos1 = toBlockPos(new Vec3(ent.x() + offX, ent.y() - 1, ent.z() + offZ));
								BlockPos pos2 = toBlockPos(new Vec3(ent.x() + offX, ent.y() - 1, ent.z() + (offZ * -1)));
								if(!ent.getLevel().canSeeSky(pos1) && ent.getLevel().getBlockState(pos1).getBlock().getExplosionResistance() <= 200) {
									ent.getLevel().getBlockState(pos1).getBlock().wasExploded((ServerLevel)ent.getLevel(), pos1, ImprovedExplosion.dummyExplosion(ent.getLevel()));
									ent.getLevel().setBlock(pos1, Blocks.GRAY_CONCRETE.defaultBlockState(), 3);
								}
								if(!ent.getLevel().canSeeSky(pos2) && ent.getLevel().getBlockState(pos2).getBlock().getExplosionResistance() <= 200) {
									ent.getLevel().getBlockState(pos2).getBlock().wasExploded((ServerLevel)ent.getLevel(), pos2, ImprovedExplosion.dummyExplosion(ent.getLevel()));
									ent.getLevel().setBlock(pos2, Blocks.GRAY_CONCRETE.defaultBlockState(), 3);
								}
							}
							
							BlockPos pos1 = toBlockPos(new Vec3(ent.x() + offX, ent.y() - 1, ent.z()));
							BlockPos pos2 = toBlockPos(new Vec3(ent.x() + offX, ent.y() - 1, ent.z() + 10));
							BlockPos pos3 = toBlockPos(new Vec3(ent.x() + offX, ent.y() - 1, ent.z() - 10));
							List<BlockPos> list = List.of(pos1, pos2, pos3);
							
							for(BlockPos pos : list) {
								if(!ent.getLevel().canSeeSky(pos) && ent.getLevel().getBlockState(pos).getBlock().getExplosionResistance() <= 200) {
									ent.getLevel().getBlockState(pos).getBlock().wasExploded((ServerLevel)ent.getLevel(), pos, ImprovedExplosion.dummyExplosion(ent.getLevel()));
									ent.getLevel().setBlock(pos, Blocks.SMOOTH_STONE.defaultBlockState(), 3);
								}
							}
							
							if(offX % 5 == 0) {
								BlockPos pos4 = toBlockPos(new Vec3(ent.x() + offX - 2, ent.y() - 1, ent.z() + 5));
								BlockPos pos5 = toBlockPos(new Vec3(ent.x() + offX - 3, ent.y() - 1, ent.z() + 5));
								BlockPos pos6 = toBlockPos(new Vec3(ent.x() + offX - 4, ent.y() - 1, ent.z() + 5));
								
								BlockPos pos7 = toBlockPos(new Vec3(ent.x() + offX - 2, ent.y() - 1, ent.z() - 5));
								BlockPos pos8 = toBlockPos(new Vec3(ent.x() + offX - 3, ent.y() - 1, ent.z() - 5));
								BlockPos pos9 = toBlockPos(new Vec3(ent.x() + offX - 4, ent.y() - 1, ent.z() - 5));
								List<BlockPos> list2 = List.of(pos4, pos5, pos6, pos7, pos8, pos9);
								
								for(BlockPos pos : list2) {
									if(!ent.getLevel().canSeeSky(pos) && ent.getLevel().getBlockState(pos).getBlock().getExplosionResistance() <= 200) {
										ent.getLevel().getBlockState(pos).getBlock().wasExploded((ServerLevel)ent.getLevel(), pos, ImprovedExplosion.dummyExplosion(ent.getLevel()));
										ent.getLevel().setBlock(pos, Blocks.YELLOW_CONCRETE.defaultBlockState(), 3);
									}
								}
							}
						}
						break;
			
			case SOUTH:	for(int offZ = 0; offZ <= dir.getStepZ() * 200; offZ++) {
							for(int offX = 1; offX <= 9; offX++) {
								BlockPos pos1 = toBlockPos(new Vec3(ent.x() + offX, ent.y() - 1, ent.z() + offZ));
								BlockPos pos2 = toBlockPos(new Vec3(ent.x() + (offX * -1), ent.y() - 1, ent.z() + offZ));
								if(!ent.getLevel().canSeeSky(pos1) && ent.getLevel().getBlockState(pos1).getBlock().getExplosionResistance() <= 200) {
									ent.getLevel().getBlockState(pos1).getBlock().wasExploded((ServerLevel)ent.getLevel(), pos1, ImprovedExplosion.dummyExplosion(ent.getLevel()));
									ent.getLevel().setBlock(pos1, Blocks.GRAY_CONCRETE.defaultBlockState(), 3);
								}
								if(!ent.getLevel().canSeeSky(pos2) && ent.getLevel().getBlockState(pos2).getBlock().getExplosionResistance() <= 200) {
									ent.getLevel().getBlockState(pos2).getBlock().wasExploded((ServerLevel)ent.getLevel(), pos2, ImprovedExplosion.dummyExplosion(ent.getLevel()));
									ent.getLevel().setBlock(pos2, Blocks.GRAY_CONCRETE.defaultBlockState(), 3);
								}
							}
							
							BlockPos pos1 = toBlockPos(new Vec3(ent.x(), ent.y() - 1, ent.z() + offZ));
							BlockPos pos2 = toBlockPos(new Vec3(ent.x() + 10, ent.y() - 1, ent.z() + offZ));
							BlockPos pos3 = toBlockPos(new Vec3(ent.x() - 10, ent.y() - 1, ent.z() + offZ));
							List<BlockPos> list = List.of(pos1, pos2, pos3);
							
							for(BlockPos pos : list) {
								if(!ent.getLevel().canSeeSky(pos) && ent.getLevel().getBlockState(pos).getBlock().getExplosionResistance() <= 200) {
									ent.getLevel().getBlockState(pos).getBlock().wasExploded((ServerLevel)ent.getLevel(), pos, ImprovedExplosion.dummyExplosion(ent.getLevel()));
									ent.getLevel().setBlock(pos, Blocks.SMOOTH_STONE.defaultBlockState(), 3);
								}
							}
							
							if(offZ % 5 == 0) {
								BlockPos pos4 = toBlockPos(new Vec3(ent.x() + 5, ent.y() - 1, ent.z() + offZ - 2));
								BlockPos pos5 = toBlockPos(new Vec3(ent.x() + 5, ent.y() - 1, ent.z() + offZ - 3));
								BlockPos pos6 = toBlockPos(new Vec3(ent.x() + 5, ent.y() - 1, ent.z() + offZ - 4));
							
								BlockPos pos7 = toBlockPos(new Vec3(ent.x() - 5, ent.y() - 1, ent.z() + offZ - 2));
								BlockPos pos8 = toBlockPos(new Vec3(ent.x() - 5, ent.y() - 1, ent.z() + offZ - 3));
								BlockPos pos9 = toBlockPos(new Vec3(ent.x() - 5, ent.y() - 1, ent.z() + offZ - 4));
								List<BlockPos> list2 = List.of(pos4, pos5, pos6, pos7, pos8, pos9);
								
								for(BlockPos pos : list2) {
									if(!ent.getLevel().canSeeSky(pos) && ent.getLevel().getBlockState(pos).getBlock().getExplosionResistance() <= 200) {
										ent.getLevel().getBlockState(pos).getBlock().wasExploded((ServerLevel)ent.getLevel(), pos, ImprovedExplosion.dummyExplosion(ent.getLevel()));
										ent.getLevel().setBlock(pos, Blocks.YELLOW_CONCRETE.defaultBlockState(), 3);
									}
								}
							}
						}
						break;
			
			case WEST:	for(int offX = 0; offX >= dir.getStepX() * 200; offX--) {
							for(int offZ = 1; offZ <= 9; offZ++) {
								BlockPos pos1 = toBlockPos(new Vec3(ent.x() + offX, ent.y() - 1, ent.z() + offZ));
								BlockPos pos2 = toBlockPos(new Vec3(ent.x() + offX, ent.y() - 1, ent.z() + (offZ * -1)));
								if(!ent.getLevel().canSeeSky(pos1) && ent.getLevel().getBlockState(pos1).getBlock().getExplosionResistance() <= 200) {
									ent.getLevel().getBlockState(pos1).getBlock().wasExploded((ServerLevel)ent.getLevel(), pos1, ImprovedExplosion.dummyExplosion(ent.getLevel()));
									ent.getLevel().setBlock(pos1, Blocks.GRAY_CONCRETE.defaultBlockState(), 3);
								}
								if(!ent.getLevel().canSeeSky(pos2) && ent.getLevel().getBlockState(pos2).getBlock().getExplosionResistance() <= 200) {
									ent.getLevel().getBlockState(pos2).getBlock().wasExploded((ServerLevel)ent.getLevel(), pos2, ImprovedExplosion.dummyExplosion(ent.getLevel()));
									ent.getLevel().setBlock(pos2, Blocks.GRAY_CONCRETE.defaultBlockState(), 3);
								}
							}
							
							BlockPos pos1 = toBlockPos(new Vec3(ent.x() + offX, ent.y() - 1, ent.z()));
							BlockPos pos2 = toBlockPos(new Vec3(ent.x() + offX, ent.y() - 1, ent.z() + 10));
							BlockPos pos3 = toBlockPos(new Vec3(ent.x() + offX, ent.y() - 1, ent.z() - 10));
							List<BlockPos> list = List.of(pos1, pos2, pos3);
							
							for(BlockPos pos : list) {
								if(!ent.getLevel().canSeeSky(pos) && ent.getLevel().getBlockState(pos).getBlock().getExplosionResistance() <= 200) {
									ent.getLevel().getBlockState(pos).getBlock().wasExploded((ServerLevel)ent.getLevel(), pos, ImprovedExplosion.dummyExplosion(ent.getLevel()));
									ent.getLevel().setBlock(pos, Blocks.SMOOTH_STONE.defaultBlockState(), 3);
								}
							}
							
							if(offX % 5 == 0) {
								BlockPos pos4 = toBlockPos(new Vec3(ent.x() + offX + 2, ent.y() - 1, ent.z() + 5));
								BlockPos pos5 = toBlockPos(new Vec3(ent.x() + offX + 3, ent.y() - 1, ent.z() + 5));
								BlockPos pos6 = toBlockPos(new Vec3(ent.x() + offX + 4, ent.y() - 1, ent.z() + 5));
								
								BlockPos pos7 = toBlockPos(new Vec3(ent.x() + offX + 2, ent.y() - 1, ent.z() - 5));
								BlockPos pos8 = toBlockPos(new Vec3(ent.x() + offX + 3, ent.y() - 1, ent.z() - 5));
								BlockPos pos9 = toBlockPos(new Vec3(ent.x() + offX + 4, ent.y() - 1, ent.z() - 5));
								List<BlockPos> list2 = List.of(pos4, pos5, pos6, pos7, pos8, pos9);
								
								for(BlockPos pos : list2) {
									if(!ent.getLevel().canSeeSky(pos) && ent.getLevel().getBlockState(pos).getBlock().getExplosionResistance() <= 200) {
										ent.getLevel().getBlockState(pos).getBlock().wasExploded((ServerLevel)ent.getLevel(), pos, ImprovedExplosion.dummyExplosion(ent.getLevel()));
										ent.getLevel().setBlock(pos, Blocks.YELLOW_CONCRETE.defaultBlockState(), 3);
									}
								}
							}
						}
						break;
			
			default: break;
		}
	}
	
	public void placeWalls(IExplosiveEntity ent, Direction dir) {
		switch(dir) {
			case NORTH: for(int offZ = 0; offZ >= dir.getStepZ() * 200; offZ--) {
							for(int offY = 15; offY >= 0; offY--) {
								BlockPos pos1 = toBlockPos(new Vec3(ent.x() + 11, ent.y() + offY, ent.z() + offZ));
								BlockPos pos2 = toBlockPos(new Vec3(ent.x() - 11, ent.y() + offY, ent.z() + offZ));
								if(ent.getLevel().getBlockState(pos1).is(BlockTags.LEAVES) || Materials.isWood(ent.getLevel().getBlockState(pos1))) {
									ent.getLevel().getBlockState(pos1).getBlock().wasExploded((ServerLevel)ent.getLevel(), pos1, ImprovedExplosion.dummyExplosion(ent.getLevel()));
								}
								if(ent.getLevel().getBlockState(pos2).is(BlockTags.LEAVES) || Materials.isWood(ent.getLevel().getBlockState(pos2))) {
									ent.getLevel().getBlockState(pos2).getBlock().wasExploded((ServerLevel)ent.getLevel(), pos2, ImprovedExplosion.dummyExplosion(ent.getLevel()));
								}
								if(ent.getLevel().getLightLevel(LightLayer.SKY, pos1) < 10 && ent.getLevel().getBlockState(pos1).getBlock().getExplosionResistance() <= 200) {
									ent.getLevel().getBlockState(pos1).getBlock().wasExploded((ServerLevel)ent.getLevel(), pos1, ImprovedExplosion.dummyExplosion(ent.getLevel()));
									ent.getLevel().setBlock(pos1, Blocks.STONE.defaultBlockState(), 3);
								}
								if(ent.getLevel().getLightLevel(LightLayer.SKY, pos2) < 10 && ent.getLevel().getBlockState(pos2).getBlock().getExplosionResistance() <= 200) {
									ent.getLevel().getBlockState(pos2).getBlock().wasExploded((ServerLevel)ent.getLevel(), pos2, ImprovedExplosion.dummyExplosion(ent.getLevel()));
									ent.getLevel().setBlock(pos2, Blocks.STONE.defaultBlockState(), 3);
								}
							}
						}
						for(int offZ = 0; offZ >= dir.getStepZ() * 200; offZ--) {
							for(int offX = -10; offX <= 10; offX++) {
								BlockPos pos1 = toBlockPos(new Vec3(ent.x() + offX, ent.y() - 1, ent.z() + offZ));
								BlockPos pos2 = toBlockPos(new Vec3(ent.x() + offX, ent.y() + 16, ent.z() + offZ));
								if(ent.getLevel().getBlockState(pos1).is(BlockTags.LEAVES) || Materials.isWood(ent.getLevel().getBlockState(pos1))) {
									ent.getLevel().getBlockState(pos1).getBlock().wasExploded((ServerLevel)ent.getLevel(), pos1, ImprovedExplosion.dummyExplosion(ent.getLevel()));
								}
								if(ent.getLevel().getBlockState(pos2).is(BlockTags.LEAVES) || Materials.isWood(ent.getLevel().getBlockState(pos2))) {
									ent.getLevel().getBlockState(pos2).getBlock().wasExploded((ServerLevel)ent.getLevel(), pos2, ImprovedExplosion.dummyExplosion(ent.getLevel()));
								}
								if(ent.getLevel().getLightLevel(LightLayer.SKY, pos1) < 15 && ent.getLevel().getBlockState(pos1).getBlock().getExplosionResistance() <= 200) {
									ent.getLevel().getBlockState(pos1).getBlock().wasExploded((ServerLevel)ent.getLevel(), pos1, ImprovedExplosion.dummyExplosion(ent.getLevel()));
									ent.getLevel().setBlock(pos1, Blocks.STONE.defaultBlockState(), 3);
								}
								if(ent.getLevel().getLightLevel(LightLayer.SKY, pos2) < 10 && ent.getLevel().getBlockState(pos2).getBlock().getExplosionResistance() <= 200) {
									ent.getLevel().getBlockState(pos2).getBlock().wasExploded((ServerLevel)ent.getLevel(), pos2, ImprovedExplosion.dummyExplosion(ent.getLevel()));
									ent.getLevel().setBlock(pos2, Blocks.STONE.defaultBlockState(), 3);
								}
							}
						}
						break;
						
			case EAST: for(int offX = 0; offX <= dir.getStepX() * 200; offX++) {
							for(int offY = 15; offY >= 0; offY--) {
								BlockPos pos1 = toBlockPos(new Vec3(ent.x() + offX, ent.y() + offY, ent.z() + 11));
								BlockPos pos2 = toBlockPos(new Vec3(ent.x() + offX, ent.y() + offY, ent.z() - 11));
								if(ent.getLevel().getBlockState(pos1).is(BlockTags.LEAVES) || Materials.isWood(ent.getLevel().getBlockState(pos1))) {
									ent.getLevel().getBlockState(pos1).getBlock().wasExploded((ServerLevel)ent.getLevel(), pos1, ImprovedExplosion.dummyExplosion(ent.getLevel()));
								}
								if(ent.getLevel().getBlockState(pos2).is(BlockTags.LEAVES) || Materials.isWood(ent.getLevel().getBlockState(pos2))) {
									ent.getLevel().getBlockState(pos2).getBlock().wasExploded((ServerLevel)ent.getLevel(), pos2, ImprovedExplosion.dummyExplosion(ent.getLevel()));
								}
								if(ent.getLevel().getLightLevel(LightLayer.SKY, pos1) < 10 && ent.getLevel().getBlockState(pos1).getBlock().getExplosionResistance() <= 200) {
									ent.getLevel().getBlockState(pos1).getBlock().wasExploded((ServerLevel)ent.getLevel(), pos1, ImprovedExplosion.dummyExplosion(ent.getLevel()));
									ent.getLevel().setBlock(pos1, Blocks.STONE.defaultBlockState(), 3);
								}
								if(ent.getLevel().getLightLevel(LightLayer.SKY, pos2) < 10 && ent.getLevel().getBlockState(pos2).getBlock().getExplosionResistance() <= 200) {
									ent.getLevel().getBlockState(pos2).getBlock().wasExploded((ServerLevel)ent.getLevel(), pos2, ImprovedExplosion.dummyExplosion(ent.getLevel()));
									ent.getLevel().setBlock(pos2, Blocks.STONE.defaultBlockState(), 3);
								}
							}
						}
						for(int offX = 0; offX <= dir.getStepX() * 200; offX++) {
							for(int offZ = -10; offZ <= 10; offZ++) {
								BlockPos pos1 = toBlockPos(new Vec3(ent.x() + offX, ent.y() - 1, ent.z() + offZ));
								BlockPos pos2 = toBlockPos(new Vec3(ent.x() + offX, ent.y() + 16, ent.z() + offZ));
								if(ent.getLevel().getBlockState(pos1).is(BlockTags.LEAVES) || Materials.isWood(ent.getLevel().getBlockState(pos1))) {
									ent.getLevel().getBlockState(pos1).getBlock().wasExploded((ServerLevel)ent.getLevel(), pos1, ImprovedExplosion.dummyExplosion(ent.getLevel()));
								}
								if(ent.getLevel().getBlockState(pos2).is(BlockTags.LEAVES) || Materials.isWood(ent.getLevel().getBlockState(pos2))) {
									ent.getLevel().getBlockState(pos2).getBlock().wasExploded((ServerLevel)ent.getLevel(), pos2, ImprovedExplosion.dummyExplosion(ent.getLevel()));
								}
								if(ent.getLevel().getLightLevel(LightLayer.SKY, pos1) < 15 && ent.getLevel().getBlockState(pos1).getBlock().getExplosionResistance() <= 200) {
									ent.getLevel().getBlockState(pos1).getBlock().wasExploded((ServerLevel)ent.getLevel(), pos1, ImprovedExplosion.dummyExplosion(ent.getLevel()));
									ent.getLevel().setBlock(pos1, Blocks.STONE.defaultBlockState(), 3);
								}
								if(ent.getLevel().getLightLevel(LightLayer.SKY, pos2) < 10 && ent.getLevel().getBlockState(pos2).getBlock().getExplosionResistance() <= 200) {
									ent.getLevel().getBlockState(pos2).getBlock().wasExploded((ServerLevel)ent.getLevel(), pos2, ImprovedExplosion.dummyExplosion(ent.getLevel()));
									ent.getLevel().setBlock(pos2, Blocks.STONE.defaultBlockState(), 3);
								}
							}
						}
						break;
						
			case SOUTH: for(int offZ = 0; offZ <= dir.getStepZ() * 200; offZ++) {
							for(int offY = 15; offY >= 0; offY--) {
								BlockPos pos1 = toBlockPos(new Vec3(ent.x() + 11, ent.y() + offY, ent.z() + offZ));
								BlockPos pos2 = toBlockPos(new Vec3(ent.x() - 11, ent.y() + offY, ent.z() + offZ));
								if(ent.getLevel().getBlockState(pos1).is(BlockTags.LEAVES) || Materials.isWood(ent.getLevel().getBlockState(pos1))) {
									ent.getLevel().getBlockState(pos1).getBlock().wasExploded((ServerLevel)ent.getLevel(), pos1, ImprovedExplosion.dummyExplosion(ent.getLevel()));
								}
								if(ent.getLevel().getBlockState(pos2).is(BlockTags.LEAVES) || Materials.isWood(ent.getLevel().getBlockState(pos2))) {
									ent.getLevel().getBlockState(pos2).getBlock().wasExploded((ServerLevel)ent.getLevel(), pos2, ImprovedExplosion.dummyExplosion(ent.getLevel()));
								}
								if(ent.getLevel().getLightLevel(LightLayer.SKY, pos1) < 10 && ent.getLevel().getBlockState(pos1).getBlock().getExplosionResistance() <= 200) {
									ent.getLevel().getBlockState(pos1).getBlock().wasExploded((ServerLevel)ent.getLevel(), pos1, ImprovedExplosion.dummyExplosion(ent.getLevel()));
									ent.getLevel().setBlock(pos1, Blocks.STONE.defaultBlockState(), 3);
								}
								if(ent.getLevel().getLightLevel(LightLayer.SKY, pos2) < 10 && ent.getLevel().getBlockState(pos2).getBlock().getExplosionResistance() <= 200) {
									ent.getLevel().getBlockState(pos2).getBlock().wasExploded((ServerLevel)ent.getLevel(), pos2, ImprovedExplosion.dummyExplosion(ent.getLevel()));
									ent.getLevel().setBlock(pos2, Blocks.STONE.defaultBlockState(), 3);
								}
							}
						}
						for(int offZ = 0; offZ <= dir.getStepZ() * 200; offZ++) {
							for(int offX = -10; offX <= 10; offX++) {
								BlockPos pos1 = toBlockPos(new Vec3(ent.x() + offX, ent.y() - 1, ent.z() + offZ));
								BlockPos pos2 = toBlockPos(new Vec3(ent.x() + offX, ent.y() + 16, ent.z() + offZ));
								if(ent.getLevel().getBlockState(pos1).is(BlockTags.LEAVES) || Materials.isWood(ent.getLevel().getBlockState(pos1))) {
									ent.getLevel().getBlockState(pos1).getBlock().wasExploded((ServerLevel)ent.getLevel(), pos1, ImprovedExplosion.dummyExplosion(ent.getLevel()));
								}
								if(ent.getLevel().getBlockState(pos2).is(BlockTags.LEAVES) || Materials.isWood(ent.getLevel().getBlockState(pos2))) {
									ent.getLevel().getBlockState(pos2).getBlock().wasExploded((ServerLevel)ent.getLevel(), pos2, ImprovedExplosion.dummyExplosion(ent.getLevel()));
								}
								if(ent.getLevel().getLightLevel(LightLayer.SKY, pos1) < 15 && ent.getLevel().getBlockState(pos1).getBlock().getExplosionResistance() <= 200) {
									ent.getLevel().getBlockState(pos1).getBlock().wasExploded((ServerLevel)ent.getLevel(), pos1, ImprovedExplosion.dummyExplosion(ent.getLevel()));
									ent.getLevel().setBlock(pos1, Blocks.STONE.defaultBlockState(), 3);
								}
								if(ent.getLevel().getLightLevel(LightLayer.SKY, pos2) < 10 && ent.getLevel().getBlockState(pos2).getBlock().getExplosionResistance() <= 200) {
									ent.getLevel().getBlockState(pos2).getBlock().wasExploded((ServerLevel)ent.getLevel(), pos2, ImprovedExplosion.dummyExplosion(ent.getLevel()));
									ent.getLevel().setBlock(pos2, Blocks.STONE.defaultBlockState(), 3);
								}
							}
						}
						break;
						
			case WEST: for(int offX = 0; offX >= dir.getStepX() * 200; offX--) {
							for(int offY = 15; offY >= 0; offY--) {
								BlockPos pos1 = toBlockPos(new Vec3(ent.x() + offX, ent.y() + offY, ent.z() + 11));
								BlockPos pos2 = toBlockPos(new Vec3(ent.x() + offX, ent.y() + offY, ent.z() - 11));
								if(ent.getLevel().getBlockState(pos1).is(BlockTags.LEAVES) || Materials.isWood(ent.getLevel().getBlockState(pos1))) {
									ent.getLevel().getBlockState(pos1).getBlock().wasExploded((ServerLevel)ent.getLevel(), pos1, ImprovedExplosion.dummyExplosion(ent.getLevel()));
								}
								if(ent.getLevel().getBlockState(pos2).is(BlockTags.LEAVES) || Materials.isWood(ent.getLevel().getBlockState(pos2))) {
									ent.getLevel().getBlockState(pos2).getBlock().wasExploded((ServerLevel)ent.getLevel(), pos2, ImprovedExplosion.dummyExplosion(ent.getLevel()));
								}
								if(ent.getLevel().getLightLevel(LightLayer.SKY, pos1) < 10 && ent.getLevel().getBlockState(pos1).getBlock().getExplosionResistance() <= 200) {
									ent.getLevel().getBlockState(pos1).getBlock().wasExploded((ServerLevel)ent.getLevel(), pos1, ImprovedExplosion.dummyExplosion(ent.getLevel()));
									ent.getLevel().setBlock(pos1, Blocks.STONE.defaultBlockState(), 3);
								}
								if(ent.getLevel().getLightLevel(LightLayer.SKY, pos2) < 10 && ent.getLevel().getBlockState(pos2).getBlock().getExplosionResistance() <= 200) {
									ent.getLevel().getBlockState(pos2).getBlock().wasExploded((ServerLevel)ent.getLevel(), pos2, ImprovedExplosion.dummyExplosion(ent.getLevel()));
									ent.getLevel().setBlock(pos2, Blocks.STONE.defaultBlockState(), 3);
								}
							}
						}
						for(int offX = 0; offX >= dir.getStepX() * 200; offX--) {
							for(int offZ = -10; offZ <= 10; offZ++) {
								BlockPos pos1 = toBlockPos(new Vec3(ent.x() + offX, ent.y() - 1, ent.z() + offZ));
								BlockPos pos2 = toBlockPos(new Vec3(ent.x() + offX, ent.y() + 16, ent.z() + offZ));
								if(ent.getLevel().getBlockState(pos1).is(BlockTags.LEAVES) || Materials.isWood(ent.getLevel().getBlockState(pos1))) {
									ent.getLevel().getBlockState(pos1).getBlock().wasExploded((ServerLevel)ent.getLevel(), pos1, ImprovedExplosion.dummyExplosion(ent.getLevel()));
								}
								if(ent.getLevel().getBlockState(pos2).is(BlockTags.LEAVES) || Materials.isWood(ent.getLevel().getBlockState(pos2))) {
									ent.getLevel().getBlockState(pos2).getBlock().wasExploded((ServerLevel)ent.getLevel(), pos2, ImprovedExplosion.dummyExplosion(ent.getLevel()));
								}
								if(ent.getLevel().getLightLevel(LightLayer.SKY, pos1) < 15 && ent.getLevel().getBlockState(pos1).getBlock().getExplosionResistance() <= 200) {
									ent.getLevel().getBlockState(pos1).getBlock().wasExploded((ServerLevel)ent.getLevel(), pos1, ImprovedExplosion.dummyExplosion(ent.getLevel()));
									ent.getLevel().setBlock(pos1, Blocks.STONE.defaultBlockState(), 3);
								}
								if(ent.getLevel().getLightLevel(LightLayer.SKY, pos2) < 10 && ent.getLevel().getBlockState(pos2).getBlock().getExplosionResistance() <= 200) {
									ent.getLevel().getBlockState(pos2).getBlock().wasExploded((ServerLevel)ent.getLevel(), pos2, ImprovedExplosion.dummyExplosion(ent.getLevel()));
									ent.getLevel().setBlock(pos2, Blocks.STONE.defaultBlockState(), 3);
								}
							}
						}
						break;
						
			default: break;
		}
	}
}


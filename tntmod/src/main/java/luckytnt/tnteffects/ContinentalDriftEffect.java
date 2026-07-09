package luckytnt.tnteffects;

import java.util.List;
import java.util.Random;

import luckytnt.registry.BlockRegistry;
import luckytntlib.util.IExplosiveEntity;
import luckytntlib.util.LuckyTNTEntityExtension;
import luckytntlib.util.tnteffects.PrimedTNTEffect;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.entity.player.Player;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.levelgen.Heightmap;

public class ContinentalDriftEffect extends PrimedTNTEffect {

	@Override
	public void explosionTick(IExplosiveEntity ent) {
		if(ent.getTNTFuse() == 400) {
			double vecx = Math.random() * 2D - 1D;
			double vecz = Math.random() * 2D - 1D;
			Vec3 vec = new Vec3(vecx, 0, vecz).normalize();
			CompoundTag tag = ent.getPersistentData();
			tag.putDouble("vecx", vec.x);
			tag.putDouble("vecz", vec.z);
			
			double vecx2 = Math.random() * 2D - 1D;
			double vecz2 = Math.random() * 2D - 1D;
			Vec3 vec2 = new Vec3(vecx2, 0, vecz2).normalize();
			tag.putDouble("vecx2", vec2.x);
			tag.putDouble("vecz2", vec2.z);
			
			tag.putDouble("x", ent.x());
			tag.putDouble("y", ent.y());
			tag.putDouble("z", ent.z());
	      	
			tag.putInt("second", 30 + new Random().nextInt(101));
			
			ent.setPersistentData(tag);
	      	
	      	List<Player> list = ent.getLevel().getEntitiesOfClass(Player.class, new AABB(ent.x() - 200, ent.y() - 200, ent.z() - 200, ent.x() + 200, ent.y() + 200, ent.z() + 200));
	      	for(Player player : list) {
	      		if(player instanceof LuckyTNTEntityExtension eplayer) {
	      			CompoundTag etag = eplayer.getAdditionalPersistentData();
		      		etag.putInt("shakeTime", 400);
		      		eplayer.setAdditionalPersistentData(etag);
	      		}
	      	}
		}
		
		if(ent.getTNTFuse() <= 400 && (ent.getTNTFuse() % 60 == 0 || ent.getTNTFuse() == 400) && !ent.getLevel().isClientSide()) {
			BlockPos origin = toBlockPos(new Vec3(ent.getPersistentData().getDoubleOr("x", 0), ent.getPersistentData().getDoubleOr("y", 0), ent.getPersistentData().getDoubleOr("z", 0)));
			BlockPos start = origin.add(toBlockPos(new Vec3(ent.getPersistentData().getDoubleOr("vecx", 0) * -80, 0, ent.getPersistentData().getDoubleOr("vecz", 0) * -80)));
			Vec3 vec = new Vec3(ent.getPersistentData().getDoubleOr("vecx", 0), 0, ent.getPersistentData().getDoubleOr("vecz", 0));
			Vec3 vec2 = new Vec3(ent.getPersistentData().getDoubleOr("vecx2", 0), 0, ent.getPersistentData().getDoubleOr("vecz2", 0));
			BlockPos start2 = start.add(toBlockPos(new Vec3(vec.x * ent.getPersistentData().getIntOr("second", 0), 0, vec.z * ent.getPersistentData().getIntOr("second", 0)))).add(toBlockPos(new Vec3(vec2.x * 8, 0, vec2.z * 8)));
			
			for(double i = 0; i < 160D; i += 1D) {
				for(int offX = -10; offX <= 10; offX++) {
					for(int offZ = -10; offZ <= 10; offZ++) {
						double distance = Math.sqrt(offX * offX + offZ * offZ);
						BlockPos pos = start.add(toBlockPos(new Vec3(i * vec.x + offX, 0, i * vec.z + offZ)));
						if(distance <= 7) {
							if(Math.random() > 0.1D) {
								BlockPos pos1 = new BlockPos(pos.getX(), ent.getLevel().getTopY(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, pos.getX(), pos.getZ()) - 1, pos.getZ());
								if(ent.getLevel().getBlockState(pos1).getBlock().getExplosionResistance() <= 100) {
									ent.getLevel().setBlock(pos1, Blocks.AIR.defaultBlockState(), 3);
								}
							}
						}
						if(distance <= 9 && distance > 7) {
							if(Math.random() > 0.5D) {
								BlockPos pos1 = new BlockPos(pos.getX(), ent.getLevel().getTopY(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, pos.getX(), pos.getZ()) - 1, pos.getZ());
								if(ent.getLevel().getBlockState(pos1).getBlock().getExplosionResistance() <= 100) {
									ent.getLevel().setBlock(pos1, Blocks.AIR.defaultBlockState(), 3);
								}
							}
						}
						if(distance <= 10 && distance > 9) {
							if(Math.random() > 0.9D) {
								BlockPos pos1 = new BlockPos(pos.getX(), ent.getLevel().getTopY(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, pos.getX(), pos.getZ()) - 1, pos.getZ());
								if(ent.getLevel().getBlockState(pos1).getBlock().getExplosionResistance() <= 100) {
									ent.getLevel().setBlock(pos1, Blocks.AIR.defaultBlockState(), 3);
								}
							}
						}
					}
				}
			}
			
			for(double i = 0; i < 60D; i += 1D) {
				for(int offX = -10; offX <= 10; offX++) {
					for(int offZ = -10; offZ <= 10; offZ++) {
						double distance = Math.sqrt(offX * offX + offZ * offZ);
						BlockPos pos = start2.add(toBlockPos(new Vec3(i * vec2.x + offX, 0, i * vec2.z + offZ)));
						if(distance <= 7) {
							if(Math.random() > 0.1D) {
								BlockPos pos1 = new BlockPos(pos.getX(), ent.getLevel().getTopY(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, pos.getX(), pos.getZ()) - 1, pos.getZ());
								if(ent.getLevel().getBlockState(pos1).getBlock().getExplosionResistance() <= 100) {
									ent.getLevel().setBlock(pos1, Blocks.AIR.defaultBlockState(), 3);
								}
							}
						}
						if(distance <= 9 && distance > 7) {
							if(Math.random() > 0.5D) {
								BlockPos pos1 = new BlockPos(pos.getX(), ent.getLevel().getTopY(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, pos.getX(), pos.getZ()) - 1, pos.getZ());
								if(ent.getLevel().getBlockState(pos1).getBlock().getExplosionResistance() <= 100) {
									ent.getLevel().setBlock(pos1, Blocks.AIR.defaultBlockState(), 3);
								}
							}
						}
						if(distance <= 10 && distance > 9) {
							if(Math.random() > 0.9D) {
								BlockPos pos1 = new BlockPos(pos.getX(), ent.getLevel().getTopY(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, pos.getX(), pos.getZ()) - 1, pos.getZ());
								if(ent.getLevel().getBlockState(pos1).getBlock().getExplosionResistance() <= 100) {
									ent.getLevel().setBlock(pos1, Blocks.AIR.defaultBlockState(), 3);
								}
							}
						}
					}
				}
			}
		}
	}
	
	@Override
	public Block getBlock() {
		return BlockRegistry.CONTINENTAL_DRIFT.get();
	}
	
	@Override
	public int getDefaultFuse(IExplosiveEntity ent) {
		return 480;
	}
}

package luckytnt.tnteffects;

import java.util.List;
import java.util.Random;

import luckytnt.registry.BlockRegistry;
import luckytntlib.util.IExplosiveEntity;
import luckytntlib.util.LuckyTNTEntityExtension;
import luckytntlib.util.tnteffects.PrimedTNTEffect;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.Heightmap;

public class ContinentalDriftEffect extends PrimedTNTEffect {

	@Override
	public void explosionTick(IExplosiveEntity ent) {
		if(ent.getTNTFuse() == 400) {
			double vecx = Math.random() * 2D - 1D;
			double vecz = Math.random() * 2D - 1D;
			Vec3d vec = new Vec3d(vecx, 0, vecz).normalize();
			NbtCompound tag = ent.getPersistentData();
			tag.putDouble("vecx", vec.x);
			tag.putDouble("vecz", vec.z);
			
			double vecx2 = Math.random() * 2D - 1D;
			double vecz2 = Math.random() * 2D - 1D;
			Vec3d vec2 = new Vec3d(vecx2, 0, vecz2).normalize();
			tag.putDouble("vecx2", vec2.x);
			tag.putDouble("vecz2", vec2.z);
			
			tag.putDouble("x", ent.x());
			tag.putDouble("y", ent.y());
			tag.putDouble("z", ent.z());
	      	
			tag.putInt("second", 30 + new Random().nextInt(101));
			
			ent.setPersistentData(tag);
	      	
	      	List<PlayerEntity> list = ent.getLevel().getNonSpectatingEntities(PlayerEntity.class, new Box(ent.x() - 200, ent.y() - 200, ent.z() - 200, ent.x() + 200, ent.y() + 200, ent.z() + 200));
	      	for(PlayerEntity player : list) {
	      		if(player instanceof LuckyTNTEntityExtension eplayer) {
	      			NbtCompound etag = eplayer.getAdditionalPersistentData();
		      		etag.putInt("shakeTime", 400);
		      		eplayer.setAdditionalPersistentData(etag);
	      		}
	      	}
		}
		
		if(ent.getTNTFuse() <= 400 && (ent.getTNTFuse() % 60 == 0 || ent.getTNTFuse() == 400) && !ent.getLevel().isClient()) {
			BlockPos origin = toBlockPos(new Vec3d(ent.getPersistentData().getDouble("x"), ent.getPersistentData().getDouble("y"), ent.getPersistentData().getDouble("z")));
			BlockPos start = origin.add(toBlockPos(new Vec3d(ent.getPersistentData().getDouble("vecx") * -80, 0, ent.getPersistentData().getDouble("vecz") * -80)));
			Vec3d vec = new Vec3d(ent.getPersistentData().getDouble("vecx"), 0, ent.getPersistentData().getDouble("vecz"));
			Vec3d vec2 = new Vec3d(ent.getPersistentData().getDouble("vecx2"), 0, ent.getPersistentData().getDouble("vecz2"));
			BlockPos start2 = start.add(toBlockPos(new Vec3d(vec.x * ent.getPersistentData().getInt("second"), 0, vec.z * ent.getPersistentData().getInt("second")))).add(toBlockPos(new Vec3d(vec2.x * 8, 0, vec2.z * 8)));
			
			for(double i = 0; i < 160D; i += 1D) {
				for(int offX = -10; offX <= 10; offX++) {
					for(int offZ = -10; offZ <= 10; offZ++) {
						double distance = Math.sqrt(offX * offX + offZ * offZ);
						BlockPos pos = start.add(toBlockPos(new Vec3d(i * vec.x + offX, 0, i * vec.z + offZ)));
						if(distance <= 7) {
							if(Math.random() > 0.1D) {
								BlockPos pos1 = new BlockPos(pos.getX(), ent.getLevel().getTopY(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, pos.getX(), pos.getZ()) - 1, pos.getZ());
								if(ent.getLevel().getBlockState(pos1).getBlock().getBlastResistance() <= 100) {
									ent.getLevel().setBlockState(pos1, Blocks.AIR.getDefaultState(), 3);
								}
							}
						}
						if(distance <= 9 && distance > 7) {
							if(Math.random() > 0.5D) {
								BlockPos pos1 = new BlockPos(pos.getX(), ent.getLevel().getTopY(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, pos.getX(), pos.getZ()) - 1, pos.getZ());
								if(ent.getLevel().getBlockState(pos1).getBlock().getBlastResistance() <= 100) {
									ent.getLevel().setBlockState(pos1, Blocks.AIR.getDefaultState(), 3);
								}
							}
						}
						if(distance <= 10 && distance > 9) {
							if(Math.random() > 0.9D) {
								BlockPos pos1 = new BlockPos(pos.getX(), ent.getLevel().getTopY(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, pos.getX(), pos.getZ()) - 1, pos.getZ());
								if(ent.getLevel().getBlockState(pos1).getBlock().getBlastResistance() <= 100) {
									ent.getLevel().setBlockState(pos1, Blocks.AIR.getDefaultState(), 3);
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
						BlockPos pos = start2.add(toBlockPos(new Vec3d(i * vec2.x + offX, 0, i * vec2.z + offZ)));
						if(distance <= 7) {
							if(Math.random() > 0.1D) {
								BlockPos pos1 = new BlockPos(pos.getX(), ent.getLevel().getTopY(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, pos.getX(), pos.getZ()) - 1, pos.getZ());
								if(ent.getLevel().getBlockState(pos1).getBlock().getBlastResistance() <= 100) {
									ent.getLevel().setBlockState(pos1, Blocks.AIR.getDefaultState(), 3);
								}
							}
						}
						if(distance <= 9 && distance > 7) {
							if(Math.random() > 0.5D) {
								BlockPos pos1 = new BlockPos(pos.getX(), ent.getLevel().getTopY(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, pos.getX(), pos.getZ()) - 1, pos.getZ());
								if(ent.getLevel().getBlockState(pos1).getBlock().getBlastResistance() <= 100) {
									ent.getLevel().setBlockState(pos1, Blocks.AIR.getDefaultState(), 3);
								}
							}
						}
						if(distance <= 10 && distance > 9) {
							if(Math.random() > 0.9D) {
								BlockPos pos1 = new BlockPos(pos.getX(), ent.getLevel().getTopY(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, pos.getX(), pos.getZ()) - 1, pos.getZ());
								if(ent.getLevel().getBlockState(pos1).getBlock().getBlastResistance() <= 100) {
									ent.getLevel().setBlockState(pos1, Blocks.AIR.getDefaultState(), 3);
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

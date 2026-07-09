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

public class EarthquakeTNTEffect extends PrimedTNTEffect{

	@Override
	public void explosionTick(IExplosiveEntity entity) {	
		if(entity.getTNTFuse() == 200) {
			double vecx = Math.random() * new Random().nextInt(11);
			double vecz = Math.random() * new Random().nextInt(11);
			vecx *= new Random().nextBoolean() ? 1D : -1D;
			vecz *= new Random().nextBoolean() ? 1D : -1D;
			Vec3d vec = new Vec3d(vecx, 0, vecz).normalize();
			NbtCompound tag = entity.getPersistentData();
			tag.putDouble("vecx", vec.x);
			tag.putDouble("vecz", vec.z);
			
			tag.putDouble("x", entity.x());
	      	tag.putDouble("y", entity.y());
	      	tag.putDouble("z", entity.z());
	      	entity.setPersistentData(tag);
	      	
	      	List<PlayerEntity> list = entity.getLevel().getNonSpectatingEntities(PlayerEntity.class, new Box(entity.x() - 100, entity.y() - 100, entity.z() - 100, entity.x() + 100, entity.y() + 100, entity.z() + 100));
	      	for(PlayerEntity player : list) {
	      		if(player instanceof LuckyTNTEntityExtension lplayer) {
		      		NbtCompound etag = lplayer.getAdditionalPersistentData();
		      		etag.putInt("shakeTime", 200);
		      		lplayer.setAdditionalPersistentData(etag);
	      		}
	      	}
		}
		
		if(entity.getTNTFuse() <= 200 && entity.getTNTFuse() % 20 == 0 && !entity.getLevel().isClient()) {
			BlockPos origin = toBlockPos(new Vec3d(entity.getPersistentData().getDouble("x"), entity.getPersistentData().getDouble("y"), entity.getPersistentData().getDouble("z")));
			BlockPos start = origin.add(toBlockPos(new Vec3d(entity.getPersistentData().getDouble("vecx") * -40, 0, entity.getPersistentData().getDouble("vecz") * -40)));
			Vec3d vec = new Vec3d(entity.getPersistentData().getDouble("vecx"), 0, entity.getPersistentData().getDouble("vecz"));
			
			for(double i = 0; i < 80D; i += 1D) {
				for(int offX = -6; offX <= 6; offX++) {
					for(int offZ = -6; offZ <= 6; offZ++) {
						double distance = Math.sqrt(offX * offX + offZ * offZ);
						BlockPos pos = start.add(toBlockPos(new Vec3d(i * vec.x + offX, 0, i * vec.z + offZ)));
						if(distance <= 3) {
							if(Math.random() > 0.1D) {
								BlockPos pos1 = new BlockPos(pos.getX(), entity.getLevel().getTopY(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, pos.getX(), pos.getZ()) - 1, pos.getZ());
								if(entity.getLevel().getBlockState(pos1).getBlock().getBlastResistance() <= 100) {
									entity.getLevel().setBlockState(pos1, Blocks.AIR.getDefaultState(), 3);
								}
							}
						}
						if(distance <= 5 && distance > 3) {
							if(Math.random() > 0.5D) {
								BlockPos pos1 = new BlockPos(pos.getX(), entity.getLevel().getTopY(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, pos.getX(), pos.getZ()) - 1, pos.getZ());
								if(entity.getLevel().getBlockState(pos1).getBlock().getBlastResistance() <= 100) {
									entity.getLevel().setBlockState(pos1, Blocks.AIR.getDefaultState(), 3);
								}
							}
						}
						if(distance <= 6 && distance > 5) {
							if(Math.random() > 0.9D) {
								BlockPos pos1 = new BlockPos(pos.getX(), entity.getLevel().getTopY(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, pos.getX(), pos.getZ()) - 1, pos.getZ());
								if(entity.getLevel().getBlockState(pos1).getBlock().getBlastResistance() <= 100) {
									entity.getLevel().setBlockState(pos1, Blocks.AIR.getDefaultState(), 3);
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
		return BlockRegistry.EARTHQUAKE_TNT.get();
	}
	
	@Override
	public int getDefaultFuse(IExplosiveEntity entity) {
		return 280;
	}
}

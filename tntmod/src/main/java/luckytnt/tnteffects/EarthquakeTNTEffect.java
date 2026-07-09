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

public class EarthquakeTNTEffect extends PrimedTNTEffect{

	@Override
	public void explosionTick(IExplosiveEntity entity) {	
		if(entity.getTNTFuse() == 200) {
			double vecx = Math.random() * new Random().nextInt(11);
			double vecz = Math.random() * new Random().nextInt(11);
			vecx *= new Random().nextBoolean() ? 1D : -1D;
			vecz *= new Random().nextBoolean() ? 1D : -1D;
			Vec3 vec = new Vec3(vecx, 0, vecz).normalize();
			CompoundTag tag = entity.getPersistentData();
			tag.putDouble("vecx", vec.x);
			tag.putDouble("vecz", vec.z);
			
			tag.putDouble("x", entity.x());
	      	tag.putDouble("y", entity.y());
	      	tag.putDouble("z", entity.z());
	      	entity.setPersistentData(tag);
	      	
	      	List<Player> list = entity.getLevel().getEntitiesOfClass(Player.class, new AABB(entity.x() - 100, entity.y() - 100, entity.z() - 100, entity.x() + 100, entity.y() + 100, entity.z() + 100));
	      	for(Player player : list) {
	      		if(player instanceof LuckyTNTEntityExtension lplayer) {
		      		CompoundTag etag = lplayer.getAdditionalPersistentData();
		      		etag.putInt("shakeTime", 200);
		      		lplayer.setAdditionalPersistentData(etag);
	      		}
	      	}
		}
		
		if(entity.getTNTFuse() <= 200 && entity.getTNTFuse() % 20 == 0 && !entity.getLevel().isClientSide()) {
			BlockPos origin = toBlockPos(new Vec3(entity.getPersistentData().getDoubleOr("x", 0), entity.getPersistentData().getDoubleOr("y", 0), entity.getPersistentData().getDoubleOr("z", 0)));
			BlockPos start = origin.offset(toBlockPos(new Vec3(entity.getPersistentData().getDoubleOr("vecx", 0) * -40, 0, entity.getPersistentData().getDoubleOr("vecz", 0) * -40)));
			Vec3 vec = new Vec3(entity.getPersistentData().getDoubleOr("vecx", 0), 0, entity.getPersistentData().getDoubleOr("vecz", 0));
			
			for(double i = 0; i < 80D; i += 1D) {
				for(int offX = -6; offX <= 6; offX++) {
					for(int offZ = -6; offZ <= 6; offZ++) {
						double distance = Math.sqrt(offX * offX + offZ * offZ);
						BlockPos pos = start.offset(toBlockPos(new Vec3(i * vec.x + offX, 0, i * vec.z + offZ)));
						if(distance <= 3) {
							if(Math.random() > 0.1D) {
								BlockPos pos1 = new BlockPos(pos.getX(), entity.getLevel().getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, pos.getX(), pos.getZ()) - 1, pos.getZ());
								if(entity.getLevel().getBlockState(pos1).getBlock().getExplosionResistance() <= 100) {
									entity.getLevel().setBlock(pos1, Blocks.AIR.defaultBlockState(), 3);
								}
							}
						}
						if(distance <= 5 && distance > 3) {
							if(Math.random() > 0.5D) {
								BlockPos pos1 = new BlockPos(pos.getX(), entity.getLevel().getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, pos.getX(), pos.getZ()) - 1, pos.getZ());
								if(entity.getLevel().getBlockState(pos1).getBlock().getExplosionResistance() <= 100) {
									entity.getLevel().setBlock(pos1, Blocks.AIR.defaultBlockState(), 3);
								}
							}
						}
						if(distance <= 6 && distance > 5) {
							if(Math.random() > 0.9D) {
								BlockPos pos1 = new BlockPos(pos.getX(), entity.getLevel().getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, pos.getX(), pos.getZ()) - 1, pos.getZ());
								if(entity.getLevel().getBlockState(pos1).getBlock().getExplosionResistance() <= 100) {
									entity.getLevel().setBlock(pos1, Blocks.AIR.defaultBlockState(), 3);
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

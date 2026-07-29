package luckytnt.tnteffects;

import java.util.List;
import java.util.Random;

import luckytnt.registry.BlockRegistry;
import luckytntlib.util.IExplosiveEntity;
import luckytntlib.util.LuckyTNTEntityExtension;
import luckytntlib.util.tnteffects.PrimedTNTEffect;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.player.Player;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
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
			BlockPos start = origin.offset(toBlockPos(new Vec3(ent.getPersistentData().getDoubleOr("vecx", 0) * -80, 0, ent.getPersistentData().getDoubleOr("vecz", 0) * -80)));
			Vec3 vec = new Vec3(ent.getPersistentData().getDoubleOr("vecx", 0), 0, ent.getPersistentData().getDoubleOr("vecz", 0));
			Vec3 vec2 = new Vec3(ent.getPersistentData().getDoubleOr("vecx2", 0), 0, ent.getPersistentData().getDoubleOr("vecz2", 0));
			BlockPos start2 = start.offset(toBlockPos(new Vec3(vec.x * ent.getPersistentData().getIntOr("second", 0), 0, vec.z * ent.getPersistentData().getIntOr("second", 0)))).offset(toBlockPos(new Vec3(vec2.x * 8, 0, vec2.z * 8)));
			
			carveRift(ent, start, vec, 160);
			carveRift(ent, start2, vec2, 60);
		}
	}

	/**
	 * The 21x21 offset ring is loop invariant, so the per-offset chance is looked up from a table that is
	 * built once instead of taking a square root and three range tests per cell. Cells outside r=10 are
	 * skipped before anything is allocated, and the two identical copies of this body were merged.
	 * <p>The three original ranges are mutually exclusive, so at most one Math.random() call happens per
	 * in-range cell - exactly as before.
	 */
	private static void carveRift(IExplosiveEntity ent, BlockPos start, Vec3 vec, int length) {
		Level level = ent.getLevel();
		BlockState air = Blocks.AIR.defaultBlockState();
		int startX = start.getX();
		int startZ = start.getZ();

		// chance[offX + 10][offZ + 10]: the Math.random() threshold, or Double.NaN outside the rift.
		double[] chance = new double[21 * 21];
		for(int offX = -10; offX <= 10; offX++) {
			for(int offZ = -10; offZ <= 10; offZ++) {
				int d2 = offX * offX + offZ * offZ;
				double threshold;
				if(d2 <= 49) {
					threshold = 0.1D;
				} else if(d2 <= 81) {
					threshold = 0.5D;
				} else if(d2 <= 100) {
					threshold = 0.9D;
				} else {
					threshold = Double.NaN;
				}
				chance[(offX + 10) * 21 + (offZ + 10)] = threshold;
			}
		}

		for(int i = 0; i < length; i++) {
			// floor(i * vec.x + offX) == floor(i * vec.x) + offX for integral offX, so the walk along the
			// rift can be hoisted out of the 21x21 loop and no Vec3/BlockPos is built per cell.
			int lineX = startX + Mth.floor(i * vec.x);
			int lineZ = startZ + Mth.floor(i * vec.z);
			for(int offX = -10; offX <= 10; offX++) {
				int row = (offX + 10) * 21 + 10;
				int x = lineX + offX;
				for(int offZ = -10; offZ <= 10; offZ++) {
					double threshold = chance[row + offZ];
					if(Double.isNaN(threshold) || Math.random() <= threshold) {
						continue;
					}
					int z = lineZ + offZ;
					BlockPos pos1 = new BlockPos(x, level.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, x, z) - 1, z);
					if(level.getBlockState(pos1).getBlock().getExplosionResistance() <= 100) {
						level.setBlock(pos1, air, 3);
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

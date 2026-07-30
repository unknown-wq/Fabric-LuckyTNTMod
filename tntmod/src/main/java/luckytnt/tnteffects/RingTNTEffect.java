package luckytnt.tnteffects;

import net.minecraft.world.entity.EntitySpawnReason;
import luckytnt.registry.BlockRegistry;
import luckytnt.registry.EntityRegistry;
import luckytntlib.entity.PrimedLTNT;
import luckytntlib.util.IExplosiveEntity;
import luckytntlib.util.tnteffects.PrimedTNTEffect;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;

public class RingTNTEffect extends PrimedTNTEffect {

	@Override
	public void serverExplosion(IExplosiveEntity ent) {
		for(double angle = 0; angle < 360; angle += 30D) {
			PrimedLTNT tnt = EntityRegistry.TNT.get().create(ent.getLevel(), EntitySpawnReason.MOB_SUMMONED);
			tnt.setTNTFuse(80);
			tnt.setOwner(ent.owner());
			double x = ent.x() + 10 * Math.cos(angle * Math.PI / 180);
			double z = ent.z() + 10 * Math.sin(angle * Math.PI / 180);
			double y = getFirstMotionBlockingBlock(ent.getLevel(), x, z);
			tnt.setPos(x, y + 1D, z);
			ent.getLevel().addFreshEntity(tnt);
		}
	}
	
	@Override
	public Block getBlock() {
		return BlockRegistry.RING_TNT.get();
	}
	
	/**
	 * Returns the highest Y whose block has a collision shape while the block above it does not.
	 * <p>
	 * The previous version used a "blockFound" flag that only suppressed further assignments, so the
	 * loop always ran all 384 steps, each allocating 2 BlockPos and doing 2 getBlockState plus 2
	 * collision shape resolutions - and every position was read twice, once as "pos" in step n and
	 * again as "posUp" in step n+1. Returning at the hit, reusing one MutableBlockPos and carrying the
	 * shape emptiness of the block above over from the previous step turns 384 x (2 reads + 2 shapes) into
	 * ~250 x (1 read + 1 shape) and 0 allocations. EyeOfTheSaharaEffect:23 calls this 60 times in one
	 * tick, so ~46000 collision shape resolutions become ~15000.
	 */
	public static int getFirstMotionBlockingBlock(Level level, double x, double z) {
		if(level.isClientSide()) {
			return 0;
		}
		final int blockX = Mth.floor(x);
		final int blockZ = Mth.floor(z);
		final int maxY = level.getMaxY();
		final int minY = level.getMinY();
		final BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos(blockX, maxY + 1, blockZ);
		boolean upIsEmpty = level.getBlockState(pos).getCollisionShape(level, pos, CollisionContext.empty()).isEmpty();
		for(int offY = maxY; offY >= minY; offY--) {
			pos.set(blockX, offY, blockZ);
			boolean isEmpty = level.getBlockState(pos).getCollisionShape(level, pos, CollisionContext.empty()).isEmpty();
			if(!isEmpty && upIsEmpty) {
				return offY;
			}
			//the block just tested is the "block above" of the next step
			upIsEmpty = isEmpty;
		}
		return 0;
	}
}

package luckytnt.tnteffects;

import luckytnt.registry.BlockRegistry;
import luckytnt.registry.EntityRegistry;
import luckytntlib.entity.PrimedLTNT;
import luckytntlib.util.IExplosiveEntity;
import luckytntlib.util.tnteffects.PrimedTNTEffect;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

public class RingTNTEffect extends PrimedTNTEffect {

	@Override
	public void serverExplosion(IExplosiveEntity ent) {
		for(double angle = 0; angle < 360; angle += 30D) {
			PrimedLTNT tnt = EntityRegistry.TNT.get().create(ent.getLevel());
			tnt.setTNTFuse(80);
			tnt.setOwner(ent.owner());
			double x = ent.x() + 10 * Math.cos(angle * Math.PI / 180);
			double z = ent.z() + 10 * Math.sin(angle * Math.PI / 180);
			double y = getFirstMotionBlockingBlock(ent.getLevel(), x, z);
			tnt.setPosition(x, y + 1D, z);
			ent.getLevel().spawnEntity(tnt);
		}
	}
	
	@Override
	public Block getBlock() {
		return BlockRegistry.RING_TNT.get();
	}
	
	public static int getFirstMotionBlockingBlock(World level, double x, double z) {
		if(!level.isClient) {
			boolean blockFound = false;
			int y = 0;
			for(int offY = level.getTopY(); offY >= level.getBottomY(); offY--) {	
				BlockPos pos = new BlockPos(MathHelper.floor(x), offY, MathHelper.floor(z));
				BlockPos posUp = new BlockPos(MathHelper.floor(x), offY + 1, MathHelper.floor(z));
				BlockState state = level.getBlockState(pos);
				BlockState stateUp = level.getBlockState(posUp);				
				if(!blockFound) {
					if(!state.getCollisionShape(level, pos, ShapeContext.absent()).isEmpty() && stateUp.getCollisionShape(level, posUp, ShapeContext.absent()).isEmpty()) {
						blockFound = true;
						y = offY;
					}	
				}
			}
			return y;
		} else {
			return 0;
		}
	}
}

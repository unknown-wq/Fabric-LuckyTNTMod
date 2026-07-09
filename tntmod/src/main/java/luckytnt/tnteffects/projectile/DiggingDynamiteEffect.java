package luckytnt.tnteffects.projectile;

import luckytnt.registry.ItemRegistry;
import luckytntlib.util.IExplosiveEntity;
import luckytntlib.util.explosions.ImprovedExplosion;
import luckytntlib.util.tnteffects.PrimedTNTEffect;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public class DiggingDynamiteEffect extends PrimedTNTEffect{

	@Override
	public void serverExplosion(IExplosiveEntity entity) {
		Vec3d direction = entity.getPos().subtract(((Entity)entity).prevX, ((Entity)entity).prevY, ((Entity)entity).prevZ).normalize();
		explosion: for(float length = 0; length <= 40; length += 0.25f) {
			BlockPos pos = toBlockPos(entity.getPos().add(direction.multiply(length))); 
			BlockState state = entity.getLevel().getBlockState(pos);
			if(state.getBlock().getBlastResistance() < 100) {
				state.getBlock().onDestroyedByExplosion(entity.getLevel(), pos, ImprovedExplosion.dummyExplosion(entity.getLevel()));
				entity.getLevel().setBlockState(pos, Blocks.AIR.getDefaultState(), 3);
			}
			else {
				break explosion;
			}
		}
	}
	
	@Override
	public Item getItem() {
		return ItemRegistry.DIGGING_DYNAMITE.get();
	}
}

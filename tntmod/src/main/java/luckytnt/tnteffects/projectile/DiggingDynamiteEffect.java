package luckytnt.tnteffects.projectile;

import net.minecraft.server.level.ServerLevel;

import luckytnt.registry.ItemRegistry;
import luckytntlib.util.IExplosiveEntity;
import luckytntlib.util.explosions.ImprovedExplosion;
import luckytntlib.util.tnteffects.PrimedTNTEffect;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.Item;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;

public class DiggingDynamiteEffect extends PrimedTNTEffect{

	@Override
	public void serverExplosion(IExplosiveEntity entity) {
		Vec3 direction = entity.getPos().subtract(((Entity)entity).xo, ((Entity)entity).yo, ((Entity)entity).zo).normalize();
		// The 0.25 step is kept because a coarser step can skip block positions on a diagonal ray and
		// leave a disconnected tunnel. Instead the repeated samples are collapsed, so the expensive part
		// (getBlockState / wasExploded / setBlock) runs at most once per distinct position - at most 41
		// times instead of 161 - while the visited set stays exactly the same as before.
		BlockPos lastPos = null;
		explosion: for(float length = 0; length <= 40; length += 0.25f) {
			BlockPos pos = toBlockPos(entity.getPos().add(direction.scale(length)));
			if(pos.equals(lastPos)) {
				continue explosion;
			}
			lastPos = pos;
			BlockState state = entity.getLevel().getBlockState(pos);
			// Air already passes the resistance test below, so without this it would be "cleared" again.
			if(state.isAir()) {
				continue explosion;
			}
			if(state.getBlock().getExplosionResistance() < 100) {
				state.getBlock().wasExploded((ServerLevel) entity.getLevel(), pos, ImprovedExplosion.dummyExplosion(entity.getLevel()));
				entity.getLevel().setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
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

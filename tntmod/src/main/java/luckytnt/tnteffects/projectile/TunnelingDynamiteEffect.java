package luckytnt.tnteffects.projectile;


import luckytnt.registry.ItemRegistry;
import luckytntlib.util.IExplosiveEntity;
import luckytntlib.util.explosions.ExplosionHelper;
import luckytntlib.util.explosions.IForEachBlockExplosionEffect;
import luckytntlib.util.explosions.ImprovedExplosion;
import luckytntlib.util.tnteffects.PrimedTNTEffect;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.Item;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.Level;

public class TunnelingDynamiteEffect extends PrimedTNTEffect{

	@Override
	public void serverExplosion(IExplosiveEntity entity) {
		Vec3 direction = entity.getPos().subtract(((Entity)entity).prevX, ((Entity)entity).prevY, ((Entity)entity).prevZ).normalize();
		for(float length = 0; length <= 40; length += 1f) {
			BlockPos pos = toBlockPos(entity.getPos().add(direction.multiply(length)));
			ExplosionHelper.doSphericalExplosion(entity.getLevel(), new Vec3(pos.getX(), pos.getY(), pos.getZ()), 4, new IForEachBlockExplosionEffect() {
				
				@Override
				public void doBlockExplosion(Level level, BlockPos pos, BlockState state, double distance) {
					if(distance < 4) {
						if(state.getBlock().getBlastResistance() < 100) {
							state.getBlock().onDestroyedByExplosion(level, pos, ImprovedExplosion.dummyExplosion(entity.getLevel()));
							level.setBlockState(pos, Blocks.AIR.getDefaultState(), 3);
						}
					}
				}
			});

		}
	}
	
	@Override
	public Item getItem() {
		return ItemRegistry.TUNNELING_DYNAMITE.get();
	}
}

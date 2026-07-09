package luckytnt.tnteffects.projectile;


import luckytnt.registry.ItemRegistry;
import luckytntlib.util.IExplosiveEntity;
import luckytntlib.util.explosions.ExplosionHelper;
import luckytntlib.util.explosions.IForEachBlockExplosionEffect;
import luckytntlib.util.explosions.ImprovedExplosion;
import luckytntlib.util.tnteffects.PrimedTNTEffect;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class TunnelingDynamiteEffect extends PrimedTNTEffect{

	@Override
	public void serverExplosion(IExplosiveEntity entity) {
		Vec3d direction = entity.getPos().subtract(((Entity)entity).prevX, ((Entity)entity).prevY, ((Entity)entity).prevZ).normalize();
		for(float length = 0; length <= 40; length += 1f) {
			BlockPos pos = toBlockPos(entity.getPos().add(direction.multiply(length)));
			ExplosionHelper.doSphericalExplosion(entity.getLevel(), new Vec3d(pos.getX(), pos.getY(), pos.getZ()), 4, new IForEachBlockExplosionEffect() {
				
				@Override
				public void doBlockExplosion(World level, BlockPos pos, BlockState state, double distance) {
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

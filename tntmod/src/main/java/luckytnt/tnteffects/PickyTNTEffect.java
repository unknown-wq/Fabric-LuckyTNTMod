package luckytnt.tnteffects;

import java.util.List;

import luckytnt.registry.BlockRegistry;
import luckytntlib.entity.LTNTMinecart;
import luckytntlib.entity.PrimedLTNT;
import luckytntlib.util.IExplosiveEntity;
import luckytntlib.util.explosions.ExplosionHelper;
import luckytntlib.util.explosions.IForEachBlockExplosionEffect;
import luckytntlib.util.explosions.ImprovedExplosion;
import luckytntlib.util.tnteffects.PrimedTNTEffect;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;

public class PickyTNTEffect extends PrimedTNTEffect{

	private final int radius;
	
	public PickyTNTEffect(int radius) {
		this.radius = radius;
	}
	
	@Override
	public void serverExplosion(IExplosiveEntity entity) {
		Block template;
		if(entity instanceof PrimedLTNT || entity instanceof LTNTMinecart) {
			template = entity.getLevel().getBlockState(toBlockPos(entity.getPos()).down()).getBlock();
		}
		else {
			BlockHitResult result = entity.getLevel().raycast(new RaycastContext(entity.getPos(), entity.getPos().add(((Entity)entity).getVelocity().normalize().multiply(0.5f)), RaycastContext.ShapeType.COLLIDER, RaycastContext.FluidHandling.NONE, (Entity)entity));
			if(result != null) {
				template = entity.getLevel().getBlockState(result.getBlockPos()).getBlock();
			}
			else {
				template = Blocks.AIR;
			}
		}
		ExplosionHelper.doSphericalExplosion(entity.getLevel(), entity.getPos(), radius, new IForEachBlockExplosionEffect() {		
			@Override
			public void doBlockExplosion(World level, BlockPos pos, BlockState state, double distance) {
				if(state.getBlock().getBlastResistance() < 100 && !state.isAir() && state.getBlock() == template) {
					List<ItemStack> drops = Block.getDroppedStacks(state, (ServerWorld)level, pos, level.getBlockEntity(pos));
					for(ItemStack stack : drops) {
						ItemEntity item = new ItemEntity(level, pos.getX(), pos.getY(), pos.getZ(), stack);
						level.spawnEntity(item);
					}
					state.getBlock().onDestroyedByExplosion(level, pos, ImprovedExplosion.dummyExplosion(entity.getLevel()));
					level.setBlockState(pos, Blocks.AIR.getDefaultState(), 3);
				}
			}
		});
	}
	
	@Override
	public Block getBlock() {
		return BlockRegistry.PICKY_TNT.get();
	}
}

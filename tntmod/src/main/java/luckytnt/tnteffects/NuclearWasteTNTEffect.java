package luckytnt.tnteffects;

import org.joml.Vector3f;

import luckytnt.registry.BlockRegistry;
import luckytntlib.util.IExplosiveEntity;
import luckytntlib.util.explosions.ExplosionHelper;
import luckytntlib.util.explosions.IForEachBlockExplosionEffect;
import luckytntlib.util.explosions.ImprovedExplosion;
import luckytntlib.util.tnteffects.PrimedTNTEffect;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.core.particles.DustParticleEffect;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

public class NuclearWasteTNTEffect extends PrimedTNTEffect{

	private final int radius;
	
	public NuclearWasteTNTEffect(int radius) {
		this.radius = radius;
	}
	
	@Override
	public void serverExplosion(IExplosiveEntity entity) {
		ExplosionHelper.doTopBlockExplosion(entity.getLevel(), entity.getPos(), radius, new IForEachBlockExplosionEffect() {
			@Override
			public void doBlockExplosion(Level level, BlockPos pos, BlockState state, double distance) {
				if(!level.getBlockState(pos.up()).isFullCube(level, pos.up()) && level.getBlockState(pos.up()).getBlock().getBlastResistance() < 100) {
					level.getBlockState(pos.up()).getBlock().onDestroyedByExplosion(level, pos.up(), ImprovedExplosion.dummyExplosion(entity.getLevel()));
					level.setBlockState(pos, BlockRegistry.NUCLEAR_WASTE.get().getDefaultState());
				}
			}
		});
	}
	
	@Override
	public void spawnParticles(IExplosiveEntity entity) {
		entity.getLevel().addParticle(new DustParticleEffect(new Vector3f(0.9f, 1f, 0f), 1), entity.x(), entity.y() + 1f, entity.z(), 0, 0, 0);
	}
	
	@Override
	public Block getBlock() {
		return BlockRegistry.NUCLEAR_WASTE_TNT.get();
	}
}

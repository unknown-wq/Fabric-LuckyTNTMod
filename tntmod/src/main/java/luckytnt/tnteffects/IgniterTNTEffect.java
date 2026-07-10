package luckytnt.tnteffects;
import net.minecraft.server.level.ServerLevel;

import luckytnt.registry.BlockRegistry;
import luckytntlib.util.IExplosiveEntity;
import luckytntlib.util.explosions.ExplosionHelper;
import luckytntlib.util.explosions.IForEachBlockExplosionEffect;
import luckytntlib.util.tnteffects.PrimedTNTEffect;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.TntBlock;
import net.minecraft.world.entity.Entity;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.Explosion;

public class IgniterTNTEffect extends PrimedTNTEffect{

	private final int strength;
	
	public IgniterTNTEffect(int strength) {
		this.strength = strength;
	}
	
	@Override
	public void serverExplosion(IExplosiveEntity entity) {
		ExplosionHelper.doSphericalExplosion(entity.getLevel(), entity.getPos(), strength, new IForEachBlockExplosionEffect() {
			
			@Override
			public void doBlockExplosion(Level level, BlockPos pos, BlockState state, double distance) {
				if(state.getBlock() instanceof TntBlock block) {
					block.wasExploded((ServerLevel)level, pos, luckytntlib.util.explosions.ImprovedExplosion.dummyExplosion((ServerLevel)level));
					level.setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
				}
			}
		});
	}
	
	@Override
	public Block getBlock() {
		return BlockRegistry.IGNITER_TNT.get();
	}
}

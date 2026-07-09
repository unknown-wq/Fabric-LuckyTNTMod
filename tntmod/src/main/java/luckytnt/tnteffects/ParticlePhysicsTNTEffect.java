package luckytnt.tnteffects;

import luckytnt.registry.BlockRegistry;
import luckytntlib.util.IExplosiveEntity;
import luckytntlib.util.explosions.IForEachBlockExplosionEffect;
import luckytntlib.util.explosions.ImprovedExplosion;
import luckytntlib.util.tnteffects.PrimedTNTEffect;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.FallingBlockEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ParticlePhysicsTNTEffect extends PrimedTNTEffect {

	@Override
	public void serverExplosion(IExplosiveEntity ent) {
		ImprovedExplosion explosion = new ImprovedExplosion(ent.getLevel(), (Entity)ent, ent.getPos(), 25);
		explosion.doBlockExplosion(1f, 1f, 1f, 1.5f, false, new IForEachBlockExplosionEffect() {
			
			@Override
			public void doBlockExplosion(World level, BlockPos pos, BlockState state, double distance) {
				FallingBlockEntity block = FallingBlockEntity.spawnFromBlock(level, pos, state);
				level.setBlockState(pos, Blocks.AIR.getDefaultState(), 3);
				block.setVelocity(Math.random() * 2 - Math.random() * 2, 1f + Math.random() * 3, Math.random() * 2 - Math.random() * 2);
				level.spawnEntity(block);
			}
		});
	}
	
	@Override
	public Block getBlock() {
		return BlockRegistry.PARTICLE_PHYSICS_TNT.get();
	}
	
	@Override
	public int getDefaultFuse(IExplosiveEntity ent) {
		return 160;
	}
}

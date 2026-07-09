package luckytnt.tnteffects;

import java.util.function.Supplier;

import luckytntlib.block.LTNTBlock;
import luckytntlib.util.IExplosiveEntity;
import luckytntlib.util.explosions.IForEachBlockExplosionEffect;
import luckytntlib.util.explosions.ImprovedExplosion;
import luckytntlib.util.tnteffects.PrimedTNTEffect;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class CompactTNTEffect extends PrimedTNTEffect{
	private final double chance;
	private final int size;
	private final Supplier<Supplier<LTNTBlock>> place;

	public CompactTNTEffect(double chance, int size, Supplier<Supplier<LTNTBlock>> place) {
		this.chance = chance;
		this.size = size;
		this.place = place;
	}

	public void serverExplosion(IExplosiveEntity entity) {
		ImprovedExplosion explosion = new ImprovedExplosion(entity.getLevel(), (Entity)entity, entity.getPos(), size);
		explosion.doBlockExplosion(new IForEachBlockExplosionEffect() {
			
			@Override
			public void doBlockExplosion(World level, BlockPos pos, BlockState state, double distance) {
				if(Math.random() < chance && !state.isAir() && state.getBlock().getBlastResistance() < 100) {
					state.getBlock().onDestroyedByExplosion(level, pos, explosion);
					level.setBlockState(pos, place.get().get().getDefaultState());
				}
			}
		});
	}
}

package luckytnt.tnteffects;

import java.util.function.Supplier;

import luckytntlib.block.LTNTBlock;
import luckytntlib.util.IExplosiveEntity;
import luckytntlib.util.explosions.IForEachBlockExplosionEffect;
import luckytntlib.util.explosions.ImprovedExplosion;
import luckytntlib.util.tnteffects.PrimedTNTEffect;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.Entity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

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
			public void doBlockExplosion(Level level, BlockPos pos, BlockState state, double distance) {
				if(Math.random() < chance && !state.isAir() && state.getBlock().getExplosionResistance() < 100) {
					state.getBlock().wasExploded(level, pos, explosion);
					level.setBlock(pos, place.get().get().defaultBlockState());
				}
			}
		});
	}
}

package luckytnt.tnteffects;

import luckytnt.registry.BlockRegistry;
import luckytnt.util.BlockSurviveChecks;
import luckytntlib.util.IExplosiveEntity;
import luckytntlib.util.explosions.ExplosionHelper;
import luckytntlib.util.explosions.IForEachBlockExplosionEffect;
import luckytntlib.util.tnteffects.PrimedTNTEffect;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class HeatWaveEffect extends PrimedTNTEffect {

	@Override
	public void serverExplosion(IExplosiveEntity ent) {
		ExplosionHelper.doSphericalExplosion(ent.getLevel(), ent.getPos(), 150, new IForEachBlockExplosionEffect() {
			
			@Override
			public void doBlockExplosion(World level, BlockPos pos, BlockState state, double distance) {
				if(state.getBlock().getBlastResistance() <= 200) {
					if(state.isAir() && BlockSurviveChecks.canFirePlaceAt(state, level, pos)) {
						ItemPlacementContext ctx = new ItemPlacementContext(level, null, Hand.MAIN_HAND, new ItemStack(Items.FLINT_AND_STEEL), new BlockHitResult(ent.getPos(), Direction.DOWN, pos, true));
						BlockState stateForPlacement = Blocks.FIRE.getPlacementState(ctx);
						level.setBlockState(pos, stateForPlacement, 3);
					}
				}
			}
		});
	}
	
	@Override
	public void spawnParticles(IExplosiveEntity ent) {
		for(int i = 0; i < 50; i++) {
			ent.getLevel().addParticle(ParticleTypes.FLAME, ent.x() + Math.random() * 10 - Math.random() * 10, ent.y() + Math.random() * 10 - Math.random() * 10, ent.z() + Math.random() * 10 - Math.random() * 10, Math.random() * 0.1 - Math.random() * 0.1, Math.random() * 0.1 - Math.random() * 0.1, Math.random() * 0.1 - Math.random() * 0.1);
		}	
	}
	
	@Override
	public Block getBlock() {
		return BlockRegistry.HEAT_WAVE.get();
	}
	
	@Override
	public int getDefaultFuse(IExplosiveEntity ent) {
		return 200;
	}
}

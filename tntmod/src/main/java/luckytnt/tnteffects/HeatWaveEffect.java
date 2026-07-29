package luckytnt.tnteffects;

import luckytnt.registry.BlockRegistry;
import luckytnt.util.BlockSurviveChecks;
import luckytntlib.util.IExplosiveEntity;
import luckytntlib.util.explosions.ExplosionHelper;
import luckytntlib.util.explosions.IForEachBlockExplosionEffect;
import luckytntlib.util.tnteffects.PrimedTNTEffect;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.Level;

public class HeatWaveEffect extends PrimedTNTEffect {

	@Override
	public void serverExplosion(IExplosiveEntity ent) {
		// Loop invariants hoisted: one flint & steel stack and one hit vector for the whole r=150 sphere
		// instead of one of each per air block. The place context itself still has to be per-position
		// because FireBlock's BlockGetter/BlockPos overload of getStateForPlacement is protected.
		final ItemStack flintAndSteel = new ItemStack(Items.FLINT_AND_STEEL);
		final Vec3 hitVec = ent.getPos();
		ExplosionHelper.doSphericalExplosion(ent.getLevel(), ent.getPos(), 150, new IForEachBlockExplosionEffect() {

			@Override
			public void doBlockExplosion(Level level, BlockPos pos, BlockState state, double distance) {
				// isAir() is a cached flag and implies an explosion resistance of 0, so testing it first
				// is equivalent and skips two virtual calls for every non-air block in the sphere.
				if(state.isAir() && BlockSurviveChecks.canFirePlaceAt(state, level, pos)) {
					BlockPlaceContext ctx = new BlockPlaceContext(level, null, InteractionHand.MAIN_HAND, flintAndSteel, new BlockHitResult(hitVec, Direction.DOWN, pos, true));
					level.setBlock(pos, Blocks.FIRE.getStateForPlacement(ctx), 3);
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

package luckytnt.tnteffects;
import net.minecraft.server.level.ServerLevel;

import luckytnt.registry.BlockRegistry;
import luckytnt.util.Materials;
import luckytntlib.util.IExplosiveEntity;
import luckytntlib.util.explosions.ExplosionHelper;
import luckytntlib.util.explosions.IForEachBlockExplosionEffect;
import luckytntlib.util.explosions.ImprovedExplosion;
import luckytntlib.util.tnteffects.PrimedTNTEffect;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.GrassBlock;
import net.minecraft.world.level.block.MyceliumBlock;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.Level;

public class FirestormTNTEffect extends PrimedTNTEffect {

	@Override
	public void serverExplosion(IExplosiveEntity ent) {
		ExplosionHelper.doCylindricalExplosion(ent.getLevel(), ent.getPos(), 50, 50, new IForEachBlockExplosionEffect() {
			
			@Override
			public void doBlockExplosion(Level level, BlockPos pos, BlockState state, double distance) {
				Block block = state.getBlock();
				if(distance <= 50 && block.getExplosionResistance() <= 200) {
					if((!state.isCollisionShapeFullBlock(level, pos) || state.is(Blocks.FIRE) || state.is(Blocks.SOUL_FIRE)
					|| state.is(BlockTags.LEAVES) || Materials.isPlant(state) || state.is(BlockTags.SNOW)
					|| Materials.isWood(state)) && !(block instanceof GrassBlock) && !(block instanceof MyceliumBlock))
					{
						block.wasExploded((ServerLevel) level, pos, ImprovedExplosion.dummyExplosion(level));
						level.setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
					}
				}
			}
		});
		
		ExplosionHelper.doSphericalExplosion(ent.getLevel(), ent.getPos(), 50, new IForEachBlockExplosionEffect() {

			@Override
			public void doBlockExplosion(Level level, BlockPos pos, BlockState state, double distance) {
				Block block = state.getBlock();
				if(!state.isAir() && block.getExplosionResistance() <= 200) {
					block.wasExploded((ServerLevel) level, pos, ImprovedExplosion.dummyExplosion(level));
					level.setBlock(pos, Blocks.NETHERRACK.defaultBlockState(), 3);
				}
			}
		});

		// Loop invariants (the flint & steel stack and the hit vector) hoisted out of the callback;
		// FireBlock only reads the level and the clicked position out of the place context, but the
		// BlockGetter/BlockPos overload of getStateForPlacement is protected, so the context stays.
		final ItemStack flintAndSteel = new ItemStack(Items.FLINT_AND_STEEL);
		final Vec3 hitVec = new Vec3(ent.x(), ent.y(), ent.z());
		ExplosionHelper.doTopBlockExplosionForAll(ent.getLevel(), ent.getPos(), 50, new IForEachBlockExplosionEffect() {

			@Override
			public void doBlockExplosion(Level level, BlockPos pos, BlockState state, double distance) {
				if(Math.random() < 0.75f) {
					BlockPlaceContext ctx = new BlockPlaceContext(level, null, InteractionHand.MAIN_HAND, flintAndSteel, new BlockHitResult(hitVec, Direction.DOWN, pos, true));
					level.setBlock(pos, Blocks.FIRE.getStateForPlacement(ctx), 3);
				}
			}
		});
	}
	
	@Override
	public void spawnParticles(IExplosiveEntity ent) {
		ent.getLevel().addParticle(ParticleTypes.FLAME, ent.x() + 0.5D, ent.y() + 1D, ent.z(), 0.25D, 0.25D, 0);
		ent.getLevel().addParticle(ParticleTypes.FLAME, ent.x() - 0.5D, ent.y() + 1D, ent.z(), -0.25D, 0.25D, 0);
		ent.getLevel().addParticle(ParticleTypes.FLAME, ent.x(), ent.y() + 1D, ent.z() + 0.5D, 0, 0.25D, 0.25D);
		ent.getLevel().addParticle(ParticleTypes.FLAME, ent.x(), ent.y() + 1D, ent.z() - 0.5D, 0, 0.25D, -0.25D);
		
		ent.getLevel().addParticle(ParticleTypes.FLAME, ent.x() + 0.5D, ent.y() + 1D, ent.z() + 0.5D, 0.25D, 0.25D, 0.25D);
		ent.getLevel().addParticle(ParticleTypes.FLAME, ent.x() - 0.5D, ent.y() + 1D, ent.z() + 0.5D, -0.25D, 0.25D, 0.25D);
		ent.getLevel().addParticle(ParticleTypes.FLAME, ent.x() + 0.5D, ent.y() + 1D, ent.z() - 0.5D, 0.25D, 0.25D, -0.25D);
		ent.getLevel().addParticle(ParticleTypes.FLAME, ent.x() - 0.5D, ent.y() + 1D, ent.z() - 0.5D, -0.25D, 0.25D, -0.25D);
		
		ent.getLevel().addParticle(ParticleTypes.FLAME, ent.x() + 0.5D, ent.y() + 0.5D, ent.z(), 0.25D, 0, 0);
		ent.getLevel().addParticle(ParticleTypes.FLAME, ent.x() - 0.5D, ent.y() + 0.5D, ent.z(), -0.25D, 0, 0);
		ent.getLevel().addParticle(ParticleTypes.FLAME, ent.x(), ent.y() + 0.5D, ent.z() + 0.5D, 0, 0, 0.25D);
		ent.getLevel().addParticle(ParticleTypes.FLAME, ent.x(), ent.y() + 0.5D, ent.z() - 0.5D, 0, 0, -0.25D);
		
		ent.getLevel().addParticle(ParticleTypes.FLAME, ent.x() + 0.5D, ent.y() + 0.5D, ent.z() + 0.5D, 0.25D, 0, 0.25D);
		ent.getLevel().addParticle(ParticleTypes.FLAME, ent.x() - 0.5D, ent.y() + 0.5D, ent.z() + 0.5D, -0.25D, 0, 0.25D);
		ent.getLevel().addParticle(ParticleTypes.FLAME, ent.x() + 0.5D, ent.y() + 0.5D, ent.z() - 0.5D, 0.25D, 0, -0.25D);
		ent.getLevel().addParticle(ParticleTypes.FLAME, ent.x() - 0.5D, ent.y() + 0.5D, ent.z() - 0.5D, -0.25D, 0, -0.25D);

		ent.getLevel().addParticle(ParticleTypes.FLAME, ent.x(), ent.y() + 1D, ent.z(), 0, 0.25D, 0);
	}

	@Override
	public Block getBlock() {
		return BlockRegistry.FIRESTORM_TNT.get();
	}

	@Override
	public int getDefaultFuse(IExplosiveEntity ent) {
		return 160;
	}
}

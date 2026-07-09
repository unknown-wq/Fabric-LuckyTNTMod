package luckytnt.tnteffects;

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
import net.minecraft.world.item.ItemPlacementContext;
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
				if(distance <= 50 && state.getBlock().getBlastResistance() <= 200) {
					if((!state.isFullCube(level, pos) || state.isOf(Blocks.FIRE) || state.isOf(Blocks.SOUL_FIRE) 
					|| state.isIn(BlockTags.LEAVES) || Materials.isPlant(state) || state.isIn(BlockTags.SNOW)
					|| Materials.isWood(state)) && !(state.getBlock() instanceof GrassBlock) && !(state.getBlock() instanceof MyceliumBlock)) 
					{
						state.getBlock().onDestroyedByExplosion(level, pos, ImprovedExplosion.dummyExplosion(ent.getLevel()));
						level.setBlockState(pos, Blocks.AIR.getDefaultState(), 3);
					}
				}
			}
		});
		
		ExplosionHelper.doSphericalExplosion(ent.getLevel(), ent.getPos(), 50, new IForEachBlockExplosionEffect() {
			
			@Override
			public void doBlockExplosion(Level level, BlockPos pos, BlockState state, double distance) {
				if(state.getBlock().getBlastResistance() <= 200 && !state.isAir()) {
					state.getBlock().onDestroyedByExplosion(level, pos, ImprovedExplosion.dummyExplosion(ent.getLevel()));
					level.setBlockState(pos, Blocks.NETHERRACK.getDefaultState(), 3);
				}
			}
		});
		
		ExplosionHelper.doTopBlockExplosionForAll(ent.getLevel(), ent.getPos(), 50, new IForEachBlockExplosionEffect() {
			
			@Override
			public void doBlockExplosion(Level level, BlockPos pos, BlockState state, double distance) {
				if(Math.random() < 0.75f) {
					ItemPlacementContext ctx = new ItemPlacementContext(level, null, InteractionHand.MAIN_HAND, new ItemStack(Items.FLINT_AND_STEEL), new BlockHitResult(new Vec3(ent.x(), ent.y(), ent.z()), Direction.DOWN, pos, true));
					level.setBlockState(pos, Blocks.FIRE.getPlacementState(ctx), 3);
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

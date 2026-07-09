package luckytnt.tnteffects;

import luckytnt.registry.BlockRegistry;
import luckytnt.util.Materials;
import luckytntlib.util.IExplosiveEntity;
import luckytntlib.util.explosions.ExplosionHelper;
import luckytntlib.util.explosions.IForEachBlockExplosionEffect;
import luckytntlib.util.explosions.ImprovedExplosion;
import luckytntlib.util.tnteffects.PrimedTNTEffect;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.GrassBlock;
import net.minecraft.block.MyceliumBlock;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class FirestormTNTEffect extends PrimedTNTEffect {

	@Override
	public void serverExplosion(IExplosiveEntity ent) {
		ExplosionHelper.doCylindricalExplosion(ent.getLevel(), ent.getPos(), 50, 50, new IForEachBlockExplosionEffect() {
			
			@Override
			public void doBlockExplosion(World level, BlockPos pos, BlockState state, double distance) {
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
			public void doBlockExplosion(World level, BlockPos pos, BlockState state, double distance) {
				if(state.getBlock().getBlastResistance() <= 200 && !state.isAir()) {
					state.getBlock().onDestroyedByExplosion(level, pos, ImprovedExplosion.dummyExplosion(ent.getLevel()));
					level.setBlockState(pos, Blocks.NETHERRACK.getDefaultState(), 3);
				}
			}
		});
		
		ExplosionHelper.doTopBlockExplosionForAll(ent.getLevel(), ent.getPos(), 50, new IForEachBlockExplosionEffect() {
			
			@Override
			public void doBlockExplosion(World level, BlockPos pos, BlockState state, double distance) {
				if(Math.random() < 0.75f) {
					ItemPlacementContext ctx = new ItemPlacementContext(level, null, Hand.MAIN_HAND, new ItemStack(Items.FLINT_AND_STEEL), new BlockHitResult(new Vec3d(ent.x(), ent.y(), ent.z()), Direction.DOWN, pos, true));
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

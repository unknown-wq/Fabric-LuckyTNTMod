package luckytnt.tnteffects;


import luckytnt.config.LuckyTNTConfigValues;
import luckytnt.registry.BlockRegistry;
import luckytnt.util.Materials;
import luckytntlib.util.IExplosiveEntity;
import luckytntlib.util.explosions.ExplosionHelper;
import luckytntlib.util.explosions.IForEachBlockExplosionEffect;
import luckytntlib.util.tnteffects.PrimedTNTEffect;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FluidBlock;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.mob.EndermanEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class EndGateEffect extends PrimedTNTEffect{

	@Override
	public void serverExplosion(IExplosiveEntity entity) {
		ExplosionHelper.doSphericalExplosion(entity.getLevel(), entity.getPos(), 30, new IForEachBlockExplosionEffect() {
		
			@Override
			public void doBlockExplosion(World level, BlockPos pos, BlockState state, double distance) {
				BlockPos posTop = pos.add(0, LuckyTNTConfigValues.ISLAND_HEIGHT.get(), 0);
				BlockState stateTop = level.getBlockState(posTop);
				
				if(state.getBlock().getBlastResistance() < 200 && stateTop.isAir() && !state.isAir() && Math.abs(entity.y() - pos.getY()) <= 20) {
					level.setBlockState(pos, Blocks.AIR.getDefaultState(), 3);
					if(Materials.isWood(state)) {
						level.setBlockState(posTop, Blocks.OBSIDIAN.getDefaultState(), 3);
					} else if(state.isIn(BlockTags.LEAVES)) {
						level.setBlockState(posTop, Blocks.END_STONE.getDefaultState(), 3);
					} else if(state.getBlock() instanceof FluidBlock) {
						level.setBlockState(posTop, Blocks.AIR.getDefaultState(), 3);
					} else {
						level.setBlockState(posTop, Blocks.END_STONE.getDefaultState(), 3);
					}
				}
			}
		});
		
		ExplosionHelper.doSphericalExplosion(entity.getLevel(), entity.getPos(), 30, new IForEachBlockExplosionEffect() {
			
			@Override
			public void doBlockExplosion(World level, BlockPos pos, BlockState state, double distance) {
				BlockPos posTop = pos.add(0, LuckyTNTConfigValues.ISLAND_HEIGHT.get(), 0);
				BlockState stateTop = level.getBlockState(posTop);
				BlockPos posAbove = pos.add(0, LuckyTNTConfigValues.ISLAND_HEIGHT.get() + 1, 0);
				BlockState stateAbove = level.getBlockState(posAbove);
				
				if(stateAbove.isAir() && Math.random() <= 0.05D && stateTop.getBlock() == Blocks.END_STONE) {
					level.setBlockState(posAbove, Blocks.CHORUS_FLOWER.getDefaultState(), 3);
				}
			}
		});
		
		for(int i = 0; i < 80; i++) {
			int offX = (int)Math.round(Math.random() * 30D - 15D);
			int offZ = (int)Math.round(Math.random() * 30D - 15D);
			EndermanEntity man = new EndermanEntity(EntityType.ENDERMAN, entity.getLevel());
			for(int offY = 320; offY >= -64; offY--) {
				BlockPos pos = toBlockPos(new Vec3d(entity.x() + offX, offY, entity.z() + offZ));
				BlockPos posDown = toBlockPos(new Vec3d(entity.x() + offX, offY - 1, entity.z() + offZ));
				BlockState state = entity.getLevel().getBlockState(pos);
				BlockState stateDown = entity.getLevel().getBlockState(posDown);
				
				if(Block.isFaceFullSquare(stateDown.getCollisionShape(entity.getLevel(), posDown), Direction.UP) && !Block.isFaceFullSquare(state.getCollisionShape(entity.getLevel(), pos), Direction.UP)) {
					man.setPosition(entity.x() + offX, offY, entity.z() + offZ);
					break;
				}
			}
			entity.getLevel().spawnEntity(man);
		}
		
		entity.getLevel().playSound(null, toBlockPos(entity.getPos()), SoundEvents.BLOCK_END_PORTAL_SPAWN, SoundCategory.BLOCKS, 0.5f, 1);
		if(entity.getLevel() instanceof ServerWorld sLevel) {
			sLevel.setTimeOfDay(18000);
		}
	}
	
	@Override
	public void spawnParticles(IExplosiveEntity ent) {
		ent.getLevel().addParticle(ParticleTypes.END_ROD, ent.x(), ent.y() + 1f, ent.z(), 0, 0, 0);
	}

	@Override
	public Block getBlock() {
		return BlockRegistry.END_GATE.get();
	}
	
	@Override
	public int getDefaultFuse(IExplosiveEntity entity) {
		return 140;
	}
}

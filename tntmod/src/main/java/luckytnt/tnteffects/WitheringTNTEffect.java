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
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.mob.WitherSkeletonEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

public class WitheringTNTEffect extends PrimedTNTEffect {

	private final int strength;
	
	public WitheringTNTEffect(int strength) {
		this.strength = strength;
	}
	
	@Override
	public void serverExplosion(IExplosiveEntity entity) {
		ImprovedExplosion explosion = new ImprovedExplosion(entity.getLevel(), (Entity)entity, entity.getPos(), strength);
		explosion.doEntityExplosion(2f, true);
		explosion.doBlockExplosion(1f, 1f, 1f, 1.5f, false, false);
		explosion.doBlockExplosion(new IForEachBlockExplosionEffect() {
			
			@Override
			public void doBlockExplosion(World level, BlockPos pos, BlockState state, double distance) {
				if(state.isFullCube(level, pos)) {
					state.getBlock().onDestroyedByExplosion(level, pos, explosion);
					level.setBlockState(pos, Math.random() < 0.5f ? Blocks.SOUL_SAND.getDefaultState() : Blocks.SOUL_SOIL.getDefaultState());
				}
			}
		});
		for(int i = 0; i < strength * 2f; i++) {
			int offX = (int)Math.round(Math.random() * strength * 2f - strength);
			int offZ = (int)Math.round(Math.random() * strength * 2f - strength);
			WitherSkeletonEntity skeleton = new WitherSkeletonEntity(EntityType.WITHER_SKELETON, entity.getLevel());
			if(entity.getLevel() instanceof ServerWorld sl) {
				skeleton.initialize(sl, entity.getLevel().getLocalDifficulty(toBlockPos(entity.getPos())), SpawnReason.MOB_SUMMONED, null);
			}
			for(int y = entity.getLevel().getTopY(); y >= entity.getLevel().getBottomY(); y--) {
				BlockPos pos = new BlockPos(MathHelper.floor(entity.x() + offX), y, MathHelper.floor(entity.z() + offZ));
				BlockState state = entity.getLevel().getBlockState(pos);
				if(!Block.isFaceFullSquare(state.getCollisionShape(entity.getLevel(), pos), Direction.UP) && Block.isFaceFullSquare(entity.getLevel().getBlockState(pos.down()).getCollisionShape(entity.getLevel(), pos.down()), Direction.UP)) {
					skeleton.setPosition(pos.getX() + 0.5f, y, pos.getZ() + 0.5f);
					break;
				}
			}
			entity.getLevel().spawnEntity(skeleton);
		}
	}
	
	@Override
	public int getDefaultFuse(IExplosiveEntity entity) {
		return 160;
	}
	
	@Override
	public Block getBlock() {
		return BlockRegistry.WITHERING_TNT.get();
	}
}

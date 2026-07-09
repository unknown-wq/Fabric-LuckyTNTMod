package luckytnt.tnteffects;

import java.util.Random;

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
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class MineralTNTEffect extends PrimedTNTEffect {

	@Override
	public void serverExplosion(IExplosiveEntity ent) {
		ExplosionHelper.doCylindricalExplosion(ent.getLevel(), ent.getPos(), 30, 30, new IForEachBlockExplosionEffect() {
			
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
		
		ExplosionHelper.doCubicalExplosion(ent.getLevel(), ent.getPos(), 30, new IForEachBlockExplosionEffect() {
			
			@Override
			public void doBlockExplosion(World level, BlockPos pos, BlockState state, double distance) {
				double distanceRe = Math.sqrt(Math.pow(ent.x() - pos.getX(), 2D) + Math.pow(ent.y() - pos.getY(), 2D) * 25 + Math.pow(ent.z() - pos.getZ(), 2D));
				if(distanceRe <= 30 + Math.random() * 2 - Math.random() * 2 && state.getBlock().getBlastResistance() <= 200) {
					level.getBlockState(pos).getBlock().onDestroyedByExplosion(level, pos, ImprovedExplosion.dummyExplosion(ent.getLevel()));
					level.setBlockState(pos, Blocks.AIR.getDefaultState(), 3);
				}
			}
		});
		
		ExplosionHelper.doCubicalExplosion(ent.getLevel(), ent.getPos(), 40, new IForEachBlockExplosionEffect() {
			
			@Override
			public void doBlockExplosion(World level, BlockPos pos, BlockState state, double distance) {
				double distanceRe = Math.sqrt(Math.pow(ent.x() - pos.getX(), 2D) + Math.pow(ent.y() - pos.getY(), 2D) * 25 + Math.pow(ent.z() - pos.getZ(), 2D));
				if(distanceRe <= 37 && state.getBlock().getBlastResistance() <= 200 && state.isFullCube(level, pos) && !state.isIn(BlockTags.LEAVES) && !Materials.isWood(state)) {
					if(touchesAir(ent, pos)) {
						level.getBlockState(pos).getBlock().onDestroyedByExplosion(level, pos, ImprovedExplosion.dummyExplosion(ent.getLevel()));
						level.setBlockState(pos, Blocks.AIR.getDefaultState(), 3);
						double randomNumber = Math.random();
						if(randomNumber < 0.9D) {
							Block block = null;
							int random = new Random().nextInt(7);
							switch(random) {
								case 0: block = Blocks.COAL_BLOCK; break;
								case 1: block = Blocks.IRON_BLOCK; break;
								case 2: block = Blocks.GOLD_BLOCK; break;
								case 3: block = Blocks.COPPER_BLOCK; break;
								case 4: block = Blocks.REDSTONE_BLOCK; break;
								case 5: block = Blocks.EMERALD_BLOCK; break;
								case 6: block = Blocks.LAPIS_BLOCK; break;
								default: block = Blocks.COAL_BLOCK; break;
							}
							level.setBlockState(pos, block.getDefaultState(), 3);
						} else if(randomNumber >= 0.9D && randomNumber < 0.96D) {
							level.setBlockState(pos, Blocks.DIAMOND_BLOCK.getDefaultState(), 3);
						} else if(randomNumber >= 0.96D) {
							level.setBlockState(pos, Blocks.NETHERITE_BLOCK.getDefaultState(), 3);
						}
					}
				}
			}
		});
	}
	
	@Override
	public Block getBlock() {
		return BlockRegistry.MINERAL_TNT.get();
	}
	
	@Override
	public int getDefaultFuse(IExplosiveEntity ent) {
		return 150;
	}
	
	public boolean touchesAir(IExplosiveEntity ent, BlockPos pos) {
		for(Direction dir : Direction.values()) {
			BlockPos pos1 = pos.add(dir.getVector());
			if(ent.getLevel().getBlockState(pos1).isAir()) {
				return true;
			}
		}
		return false;
	}
}

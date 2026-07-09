package luckytnt.tnteffects;

import java.util.Random;

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
import net.minecraft.tags.BlockTags;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;

public class MineralTNTEffect extends PrimedTNTEffect {

	@Override
	public void serverExplosion(IExplosiveEntity ent) {
		ExplosionHelper.doCylindricalExplosion(ent.getLevel(), ent.getPos(), 30, 30, new IForEachBlockExplosionEffect() {
			
			@Override
			public void doBlockExplosion(Level level, BlockPos pos, BlockState state, double distance) {
				if(distance <= 50 && state.getBlock().getExplosionResistance() <= 200) {
					if((!state.isCollisionShapeFullBlock(level, pos) || state.is(Blocks.FIRE) || state.is(Blocks.SOUL_FIRE) 
					|| state.is(BlockTags.LEAVES) || Materials.isPlant(state) || state.is(BlockTags.SNOW)
					|| Materials.isWood(state)) && !(state.getBlock() instanceof GrassBlock) && !(state.getBlock() instanceof MyceliumBlock)) 
					{
						state.getBlock().wasExploded(level, pos, ImprovedExplosion.dummyExplosion(ent.getLevel()));
						level.setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
					}
				}
			}
		});
		
		ExplosionHelper.doCubicalExplosion(ent.getLevel(), ent.getPos(), 30, new IForEachBlockExplosionEffect() {
			
			@Override
			public void doBlockExplosion(Level level, BlockPos pos, BlockState state, double distance) {
				double distanceRe = Math.sqrt(Math.pow(ent.x() - pos.getX(), 2D) + Math.pow(ent.y() - pos.getY(), 2D) * 25 + Math.pow(ent.z() - pos.getZ(), 2D));
				if(distanceRe <= 30 + Math.random() * 2 - Math.random() * 2 && state.getBlock().getExplosionResistance() <= 200) {
					level.getBlockState(pos).getBlock().wasExploded(level, pos, ImprovedExplosion.dummyExplosion(ent.getLevel()));
					level.setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
				}
			}
		});
		
		ExplosionHelper.doCubicalExplosion(ent.getLevel(), ent.getPos(), 40, new IForEachBlockExplosionEffect() {
			
			@Override
			public void doBlockExplosion(Level level, BlockPos pos, BlockState state, double distance) {
				double distanceRe = Math.sqrt(Math.pow(ent.x() - pos.getX(), 2D) + Math.pow(ent.y() - pos.getY(), 2D) * 25 + Math.pow(ent.z() - pos.getZ(), 2D));
				if(distanceRe <= 37 && state.getBlock().getExplosionResistance() <= 200 && state.isCollisionShapeFullBlock(level, pos) && !state.is(BlockTags.LEAVES) && !Materials.isWood(state)) {
					if(touchesAir(ent, pos)) {
						level.getBlockState(pos).getBlock().wasExploded(level, pos, ImprovedExplosion.dummyExplosion(ent.getLevel()));
						level.setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
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
							level.setBlock(pos, block.defaultBlockState(), 3);
						} else if(randomNumber >= 0.9D && randomNumber < 0.96D) {
							level.setBlock(pos, Blocks.DIAMOND_BLOCK.defaultBlockState(), 3);
						} else if(randomNumber >= 0.96D) {
							level.setBlock(pos, Blocks.NETHERITE_BLOCK.defaultBlockState(), 3);
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

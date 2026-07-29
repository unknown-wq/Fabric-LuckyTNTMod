package luckytnt.tnteffects;
import net.minecraft.server.level.ServerLevel;

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
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;

public class MineralTNTEffect extends PrimedTNTEffect {

	@Override
	public void serverExplosion(IExplosiveEntity ent) {
		ExplosionHelper.doCylindricalExplosion(ent.getLevel(), ent.getPos(), 30, 30, new IForEachBlockExplosionEffect() {
			
			@Override
			public void doBlockExplosion(Level level, BlockPos pos, BlockState state, double distance) {
				Block block = state.getBlock();
				if(distance <= 50 && block.getExplosionResistance() <= 200) {
					if((!state.isCollisionShapeFullBlock(level, pos) || state.is(Blocks.FIRE) || state.is(Blocks.SOUL_FIRE)
					|| state.is(BlockTags.LEAVES) || Materials.isPlant(state) || state.is(BlockTags.SNOW)
					|| Materials.isWood(state)) && !(block instanceof GrassBlock) && !(block instanceof MyceliumBlock))
					{
						block.wasExploded((ServerLevel)level, pos, ImprovedExplosion.dummyExplosion(level));
						level.setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
					}
				}
			}
		});

		// The next two passes used ExplosionHelper.doCubicalExplosion, which reads *every* cell of the
		// cuboid before handing it to the callback - but the callback only ever touches a squashed
		// ellipsoid (y is weighted x25), i.e. ~8% of the 61^3 / 81^3 cells. They are now explicit loops
		// that reject out-of-ellipsoid cells before allocating a BlockPos or reading the world.
		// Math.pow(v, 2D) is replaced by v * v (fdlibm returns exactly x*x for an exponent of 2).
		Level level = ent.getLevel();
		ServerLevel sLevel = (ServerLevel)level;
		ImprovedExplosion dummy = ImprovedExplosion.dummyExplosion(level);
		BlockState air = Blocks.AIR.defaultBlockState();
		int baseX = Mth.floor(ent.x());
		int baseY = Mth.floor(ent.y());
		int baseZ = Mth.floor(ent.z());
		double entX = ent.x();
		double entY = ent.y();
		double entZ = ent.z();

		// 30 + Math.random() * 2 - Math.random() * 2 is always < 32, so anything beyond 32 is a certain
		// reject. Those cells no longer draw from Math.random(); the two draws still happen for every
		// cell that could possibly be affected, with the same 30 +/- 2 threshold distribution.
		for(int offX = -30; offX <= 30; offX++) {
			double dx = entX - (baseX + offX);
			double dx2 = dx * dx;
			for(int offY = -30; offY <= 30; offY++) {
				double dy = entY - (baseY + offY);
				double dxy2 = dx2 + dy * dy * 25;
				if(dxy2 > 1024D) {
					continue;
				}
				for(int offZ = -30; offZ <= 30; offZ++) {
					double dz = entZ - (baseZ + offZ);
					double d2 = dxy2 + dz * dz;
					if(d2 > 1024D) {
						continue;
					}
					BlockPos pos = new BlockPos(baseX + offX, baseY + offY, baseZ + offZ);
					Block block = level.getBlockState(pos).getBlock();
					if(Math.sqrt(d2) <= 30 + Math.random() * 2 - Math.random() * 2 && block.getExplosionResistance() <= 200) {
						block.wasExploded(sLevel, pos, dummy);
						level.setBlock(pos, air, 3);
					}
				}
			}
		}

		for(int offX = -40; offX <= 40; offX++) {
			double dx = entX - (baseX + offX);
			double dx2 = dx * dx;
			for(int offY = -40; offY <= 40; offY++) {
				double dy = entY - (baseY + offY);
				double dxy2 = dx2 + dy * dy * 25;
				if(dxy2 > 1369D) {
					continue;
				}
				for(int offZ = -40; offZ <= 40; offZ++) {
					double dz = entZ - (baseZ + offZ);
					if(dxy2 + dz * dz > 1369D) {
						continue;
					}
					BlockPos pos = new BlockPos(baseX + offX, baseY + offY, baseZ + offZ);
					BlockState state = level.getBlockState(pos);
					Block block = state.getBlock();
					if(block.getExplosionResistance() <= 200 && state.isCollisionShapeFullBlock(level, pos) && !state.is(BlockTags.LEAVES) && !Materials.isWood(state) && touchesAir(level, pos)) {
						block.wasExploded(sLevel, pos, dummy);
						level.setBlock(pos, air, 3);
						double randomNumber = Math.random();
						if(randomNumber < 0.9D) {
							Block replacement;
							int random = new Random().nextInt(7);
							switch(random) {
								case 0: replacement = Blocks.COAL_BLOCK; break;
								case 1: replacement = Blocks.IRON_BLOCK; break;
								case 2: replacement = Blocks.GOLD_BLOCK; break;
								case 3: replacement = Blocks.COPPER_BLOCK.weathering().unaffected(); break;
								case 4: replacement = Blocks.REDSTONE_BLOCK; break;
								case 5: replacement = Blocks.EMERALD_BLOCK; break;
								case 6: replacement = Blocks.LAPIS_BLOCK; break;
								default: replacement = Blocks.COAL_BLOCK; break;
							}
							level.setBlock(pos, replacement.defaultBlockState(), 3);
						} else if(randomNumber < 0.96D) {
							level.setBlock(pos, Blocks.DIAMOND_BLOCK.defaultBlockState(), 3);
						} else {
							level.setBlock(pos, Blocks.NETHERITE_BLOCK.defaultBlockState(), 3);
						}
					}
				}
			}
		}
	}
	
	@Override
	public Block getBlock() {
		return BlockRegistry.MINERAL_TNT.get();
	}
	
	@Override
	public int getDefaultFuse(IExplosiveEntity ent) {
		return 150;
	}
	
	public boolean touchesAir(Level level, BlockPos pos) {
		for(Direction dir : Direction.values()) {
			BlockPos pos1 = pos.relative(dir);
			if(level.getBlockState(pos1).isAir()) {
				return true;
			}
		}
		return false;
	}
}

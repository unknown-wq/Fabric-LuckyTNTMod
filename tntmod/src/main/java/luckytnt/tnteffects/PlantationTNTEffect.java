package luckytnt.tnteffects; 

import java.util.Random;

import org.joml.Vector3f;

import luckytnt.event.LevelEvents;
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
import net.minecraft.block.FarmlandBlock;
import net.minecraft.block.FluidBlock;
import net.minecraft.block.GrassBlock;
import net.minecraft.block.MyceliumBlock;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

public class PlantationTNTEffect extends PrimedTNTEffect {

	@Override
	public void serverExplosion(IExplosiveEntity ent) {
		ExplosionHelper.doSphericalExplosion(ent.getLevel(), ent.getPos(), 41, new IForEachBlockExplosionEffect() {
			
			@Override
			public void doBlockExplosion(World level, BlockPos pos, BlockState state, double distance) {
				if(state.getBlock().getBlastResistance() <= 200) {
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
		
		for(double offX = -42; offX <= 42; offX++) {
			for(double offZ = -42; offZ <= 42; offZ++) {
				double distance = Math.sqrt(offX * offX + offZ * offZ);
				if(distance <= 42) {
					int y = LevelEvents.getTopBlock(ent.getLevel(), ent.x() + offX, ent.z() + offZ, true);
					BlockPos pos = new BlockPos(MathHelper.floor(ent.x() + offX), y, MathHelper.floor(ent.z() + offZ));
					ent.getLevel().getBlockState(pos).getBlock().onDestroyedByExplosion(ent.getLevel(), pos, ImprovedExplosion.dummyExplosion(ent.getLevel()));
					ent.getLevel().setBlockState(pos, Blocks.GRASS_BLOCK.getDefaultState(), 3);
				}
			}
		}
		
		BlockPos posBelow = toBlockPos(ent.getPos()).down();
		placeWater(posBelow, ent);
		
		for(double offX = -41; offX <= 41; offX++) {
			for(double offZ = -41; offZ <= 41; offZ++) {
				double distance = Math.sqrt(offX * offX + offZ * offZ);
				boolean blockFound = false;
				for(double offY = 320; offY > -64; offY--) {	
					BlockPos pos = new BlockPos(MathHelper.floor(ent.x() + offX), MathHelper.floor(ent.y() + offY), MathHelper.floor(ent.z() + offZ));
					BlockPos posUp = new BlockPos(MathHelper.floor(ent.x() + offX), MathHelper.floor(ent.y() + offY + 1), MathHelper.floor(ent.z() + offZ));
					BlockState state = ent.getLevel().getBlockState(pos);
					BlockState stateUp = ent.getLevel().getBlockState(posUp);
					
					if(state.getBlock().getBlastResistance() < 200 && stateUp.getBlock().getBlastResistance() < 200 && !blockFound) {
						if(state.isFullCube(ent.getLevel(), pos) && !stateUp.isFullCube(ent.getLevel(), posUp) && !state.isIn(BlockTags.LEAVES) && !stateUp.isOf(Blocks.WATER) && !stateUp.isOf(Blocks.LAVA)) {
							blockFound = true;
							if(distance > 40 && distance <= 41) {
								placeCropsAndFarmland(pos, true, ent);
							} else if(distance > 39 && distance <= 40) {
								placeWater(pos, ent);
							} else if(distance > 32 && distance <= 39) {
								placeCropsAndFarmland(pos, false, ent);
							} else if(distance > 31 && distance <= 32) {
								placeWater(pos, ent);
							} else if(distance > 24 && distance <= 31) {
								placeCropsAndFarmland(pos, false, ent);
							} else if(distance > 23 && distance <= 24) {
								placeWater(pos, ent);
							} else if(distance > 16 && distance <= 23) {
								placeCropsAndFarmland(pos, false, ent);
							} else if(distance > 15 && distance <= 16) {
								placeWater(pos, ent);
							} else if(distance > 8 && distance <= 15) {
								placeCropsAndFarmland(pos, false, ent);
							} else if(distance > 7 && distance <= 8) {
								placeWater(pos, ent);
							} else if(distance <= 7) {
								placeCropsAndFarmland(pos, false, ent);
							} 
						}
					}
				}
			}
		}
	}

	@Override
	public void spawnParticles(IExplosiveEntity ent) {
		ent.getLevel().addParticle(new DustParticleEffect(new Vector3f(1f, 0.5f, 0.1f), 1f), ent.x(), ent.y() + 1f, ent.z(), 0, 0, 0);
	}
	
	@Override
	public Block getBlock() {
		return BlockRegistry.PLANTATION_TNT.get();
	}
	
	@Override
	public int getDefaultFuse(IExplosiveEntity ent) {
		return 160;
	}
	
	public void placeCropsAndFarmland(BlockPos pos, boolean melonOrPumpkin, IExplosiveEntity ent) {
		if(!melonOrPumpkin) { 
			ent.getLevel().getBlockState(pos).getBlock().onDestroyedByExplosion(ent.getLevel(), pos, ImprovedExplosion.dummyExplosion(ent.getLevel()));
			ent.getLevel().setBlockState(pos, Blocks.AIR.getDefaultState(), 3);
			int rand = new Random().nextInt(4);
			BlockState crop = Blocks.POTATOES.getDefaultState();
			switch(rand) {
				case 0: crop = Blocks.CARROTS.getDefaultState().with(Properties.AGE_7, new Random().nextInt(8)); break;
				case 1: crop = Blocks.POTATOES.getDefaultState().with(Properties.AGE_7, new Random().nextInt(8)); break;
				case 2: crop = Blocks.WHEAT.getDefaultState().with(Properties.AGE_7, new Random().nextInt(8)); break;
				case 3: crop = Blocks.BEETROOTS.getDefaultState().with(Properties.AGE_3, new Random().nextInt(4)); break;
			}
			ent.getLevel().setBlockState(pos, Blocks.FARMLAND.getDefaultState().with(FarmlandBlock.MOISTURE, 7), 3);
			if(!ent.getLevel().getBlockState(pos.up()).isFullCube(ent.getLevel(), pos.up()) && !(ent.getLevel().getBlockState(pos.up()).getBlock() instanceof FarmlandBlock)) 
				ent.getLevel().setBlockState(pos.up(), crop, 3);
		} else if(melonOrPumpkin) {
			ent.getLevel().getBlockState(pos).getBlock().onDestroyedByExplosion(ent.getLevel(), pos, ImprovedExplosion.dummyExplosion(ent.getLevel()));
			ent.getLevel().setBlockState(pos, Blocks.AIR.getDefaultState(), 3);
			int rand = Math.random() > 0.5 ? 0 : 1;
			BlockState crop = Blocks.POTATOES.getDefaultState();
			switch(rand) {
				case 0: crop = Blocks.PUMPKIN_STEM.getDefaultState().with(Properties.AGE_7, new Random().nextInt(8)); break;
				case 1: crop = Blocks.MELON_STEM.getDefaultState().with(Properties.AGE_7, new Random().nextInt(8)); break;
			}
			ent.getLevel().setBlockState(pos, Blocks.FARMLAND.getDefaultState().with(FarmlandBlock.MOISTURE, 7), 3);
			if(!ent.getLevel().getBlockState(pos.up()).isFullCube(ent.getLevel(), pos.up()) && !(ent.getLevel().getBlockState(pos.up()).getBlock() instanceof FarmlandBlock)) 
				ent.getLevel().setBlockState(pos.up(), crop, 3);
		}
	}
	
	public void placeWater(BlockPos pos, IExplosiveEntity ent) {
		boolean placed = false; 
		if(!ent.getLevel().getBlockState(pos.north()).isFullCube(ent.getLevel(), pos.north()) && !(ent.getLevel().getBlockState(pos.north()).getBlock() instanceof FarmlandBlock) && !(ent.getLevel().getBlockState(pos.north()).getBlock() instanceof FluidBlock)) {
			placeCropsAndFarmland(pos.north(), false, ent);
		} 
		if(!ent.getLevel().getBlockState(pos.south()).isFullCube(ent.getLevel(), pos.south()) && !(ent.getLevel().getBlockState(pos.south()).getBlock() instanceof FarmlandBlock) && !(ent.getLevel().getBlockState(pos.south()).getBlock() instanceof FluidBlock)) {
			placeCropsAndFarmland(pos.south(), false, ent);
		} 
		if(!ent.getLevel().getBlockState(pos.east()).isFullCube(ent.getLevel(), pos.east()) && !(ent.getLevel().getBlockState(pos.east()).getBlock() instanceof FarmlandBlock) && !(ent.getLevel().getBlockState(pos.east()).getBlock() instanceof FluidBlock)) {
			placeCropsAndFarmland(pos.east(), false, ent);
		} 
		if(!ent.getLevel().getBlockState(pos.west()).isFullCube(ent.getLevel(), pos.west()) && !(ent.getLevel().getBlockState(pos.west()).getBlock() instanceof FarmlandBlock) && !(ent.getLevel().getBlockState(pos.west()).getBlock() instanceof FluidBlock)) {
			placeCropsAndFarmland(pos.west(), false, ent);
		}
		if((ent.getLevel().getBlockState(pos.north()).isFullCube(ent.getLevel(), pos.north()) || ent.getLevel().getBlockState(pos.north()).getBlock() instanceof FarmlandBlock || ent.getLevel().getBlockState(pos.north()).getBlock() instanceof FluidBlock) 
			&& (ent.getLevel().getBlockState(pos.south()).isFullCube(ent.getLevel(), pos.south()) || ent.getLevel().getBlockState(pos.south()).getBlock() instanceof FarmlandBlock || ent.getLevel().getBlockState(pos.south()).getBlock() instanceof FluidBlock) 
			&& (ent.getLevel().getBlockState(pos.east()).isFullCube(ent.getLevel(), pos.east()) || ent.getLevel().getBlockState(pos.east()).getBlock() instanceof FarmlandBlock || ent.getLevel().getBlockState(pos.east()).getBlock() instanceof FluidBlock) 
			&& (ent.getLevel().getBlockState(pos.west()).isFullCube(ent.getLevel(), pos.west()) || ent.getLevel().getBlockState(pos.west()).getBlock() instanceof FarmlandBlock || ent.getLevel().getBlockState(pos.west()).getBlock() instanceof FluidBlock)) 
		{
			ent.getLevel().getBlockState(pos).getBlock().onDestroyedByExplosion(ent.getLevel(), pos, ImprovedExplosion.dummyExplosion(ent.getLevel()));
			ent.getLevel().setBlockState(pos, Blocks.AIR.getDefaultState(), 3);
			ent.getLevel().setBlockState(pos, Blocks.WATER.getDefaultState(), 3);
			if(!ent.getLevel().getBlockState(pos.down()).isFullCube(ent.getLevel(), pos.down())) {
				ent.getLevel().getBlockState(pos.down()).getBlock().onDestroyedByExplosion(ent.getLevel(), pos.down(), ImprovedExplosion.dummyExplosion(ent.getLevel()));
				ent.getLevel().setBlockState(pos.down(), Blocks.AIR.getDefaultState(), 3);
				ent.getLevel().setBlockState(pos.down(), Blocks.DIRT.getDefaultState(), 3);
			}
			placed = true;
		}
		if(!placed) {
			placeCropsAndFarmland(pos, false, ent);
		}
	}
}

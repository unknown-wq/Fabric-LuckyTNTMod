package luckytnt.tnteffects;

import luckytnt.registry.BlockRegistry;
import luckytntlib.util.IExplosiveEntity;
import luckytntlib.util.explosions.ExplosionHelper;
import luckytntlib.util.explosions.IForEachBlockExplosionEffect;
import luckytntlib.util.tnteffects.PrimedTNTEffect;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.block.ShapeContext;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

public class WoolTNTEffect extends PrimedTNTEffect {

	private final int strength;
	
	public WoolTNTEffect(int strength) {
		this.strength = strength;
	}
	
	@Override
	public void serverExplosion(IExplosiveEntity entity) {
		ExplosionHelper.doSphericalExplosion(entity.getLevel(), entity.getPos(), strength, new IForEachBlockExplosionEffect() {
			
			@Override
			public void doBlockExplosion(Level level, BlockPos pos, BlockState state, double distance) {
				MapColor color = state.getMapColor(level, pos);
				if(color != MapColor.CLEAR & !state.getCollisionShape(level, pos, ShapeContext.absent()).isEmpty() && state.getBlock().getBlastResistance() <= 100) {
					if(WorldOfWoolsEffect.WHITE.contains(color)) {
						level.setBlockState(pos, Blocks.WHITE_WOOL.getDefaultState(), 3);
					} else if(WorldOfWoolsEffect.LIGHT_GRAY.contains(color)) {
						level.setBlockState(pos, Blocks.LIGHT_GRAY_WOOL.getDefaultState(), 3);
					} else if(WorldOfWoolsEffect.GRAY.contains(color)) {
						level.setBlockState(pos, Blocks.GRAY_WOOL.getDefaultState(), 3);
					} else if(WorldOfWoolsEffect.BLACK.contains(color)) {
						level.setBlockState(pos, Blocks.BLACK_WOOL.getDefaultState(), 3);
					} else if(WorldOfWoolsEffect.BROWN.contains(color)) {
						level.setBlockState(pos, Blocks.BROWN_WOOL.getDefaultState(), 3);
					} else if(WorldOfWoolsEffect.RED.contains(color)) {
						level.setBlockState(pos, Blocks.RED_WOOL.getDefaultState(), 3);
					} else if(WorldOfWoolsEffect.ORANGE.contains(color)) {
						level.setBlockState(pos, Blocks.ORANGE_WOOL.getDefaultState(), 3);
					} else if(WorldOfWoolsEffect.YELLOW.contains(color)) {
						level.setBlockState(pos, Blocks.YELLOW_WOOL.getDefaultState(), 3);
					} else if(WorldOfWoolsEffect.LIME.contains(color)) {
						level.setBlockState(pos, Blocks.LIME_WOOL.getDefaultState(), 3);
					} else if(WorldOfWoolsEffect.GREEN.contains(color)) {
						level.setBlockState(pos, Blocks.GREEN_WOOL.getDefaultState(), 3);
					} else if(WorldOfWoolsEffect.CYAN.contains(color)) {
						level.setBlockState(pos, Blocks.CYAN_WOOL.getDefaultState(), 3);
					} else if(WorldOfWoolsEffect.LIGHT_BLUE.contains(color)) {
						level.setBlockState(pos, Blocks.LIGHT_BLUE_WOOL.getDefaultState(), 3);
					} else if(WorldOfWoolsEffect.BLUE.contains(color)) {
						level.setBlockState(pos, Blocks.BLUE_WOOL.getDefaultState(), 3);
					} else if(WorldOfWoolsEffect.PURPLE.contains(color)) {
						level.setBlockState(pos, Blocks.PURPLE_WOOL.getDefaultState(), 3);
					} else if(WorldOfWoolsEffect.MAGENTA.contains(color)) {
						level.setBlockState(pos, Blocks.MAGENTA_WOOL.getDefaultState(), 3);
					} else if(WorldOfWoolsEffect.PINK.contains(color)) {
						level.setBlockState(pos, Blocks.PINK_WOOL.getDefaultState(), 3);
					}
				}
			}
		});
	}
	
	@Override
	public Block getBlock() {
		return BlockRegistry.WOOL_TNT.get();
	}
}

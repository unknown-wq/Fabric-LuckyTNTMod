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
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

public class WoolTNTEffect extends PrimedTNTEffect {

	private final int strength;
	
	public WoolTNTEffect(int strength) {
		this.strength = strength;
	}
	
	@Override
	public void serverExplosion(IExplosiveEntity entity) {
		// TODO(port-26.2): DISABLED — this effect classified blocks by MapColor shade using
		// WorldOfWoolsEffect's WHITE/LIGHT_GRAY/... MapColor lists, which are themselves §9-disabled
		// (the ~60 yarn MapColor constant names have no verified 1:1 Mojang mapping). Restore once
		// the MapColor-shade classification is remapped.
		/*
		ExplosionHelper.doSphericalExplosion(entity.getLevel(), entity.getPos(), strength, new IForEachBlockExplosionEffect() {

			@Override
			public void doBlockExplosion(Level level, BlockPos pos, BlockState state, double distance) {
				MapColor color = state.getMapColor(level, pos);
				if(color != MapColor.NONE & !state.getCollisionShape(level, pos, CollisionContext.empty()).isEmpty() && state.getBlock().getExplosionResistance() <= 100) {
					if(WorldOfWoolsEffect.WHITE.contains(color)) {
						level.setBlock(pos, Blocks.WOOL.white().defaultBlockState(), 3);
					} else if(WorldOfWoolsEffect.LIGHT_GRAY.contains(color)) {
						level.setBlock(pos, Blocks.WOOL.lightGray().defaultBlockState(), 3);
					} else if(WorldOfWoolsEffect.GRAY.contains(color)) {
						level.setBlock(pos, Blocks.WOOL.gray().defaultBlockState(), 3);
					} else if(WorldOfWoolsEffect.BLACK.contains(color)) {
						level.setBlock(pos, Blocks.WOOL.black().defaultBlockState(), 3);
					} else if(WorldOfWoolsEffect.BROWN.contains(color)) {
						level.setBlock(pos, Blocks.WOOL.brown().defaultBlockState(), 3);
					} else if(WorldOfWoolsEffect.RED.contains(color)) {
						level.setBlock(pos, Blocks.WOOL.red().defaultBlockState(), 3);
					} else if(WorldOfWoolsEffect.ORANGE.contains(color)) {
						level.setBlock(pos, Blocks.WOOL.orange().defaultBlockState(), 3);
					} else if(WorldOfWoolsEffect.YELLOW.contains(color)) {
						level.setBlock(pos, Blocks.WOOL.yellow().defaultBlockState(), 3);
					} else if(WorldOfWoolsEffect.LIME.contains(color)) {
						level.setBlock(pos, Blocks.WOOL.lime().defaultBlockState(), 3);
					} else if(WorldOfWoolsEffect.GREEN.contains(color)) {
						level.setBlock(pos, Blocks.WOOL.green().defaultBlockState(), 3);
					} else if(WorldOfWoolsEffect.CYAN.contains(color)) {
						level.setBlock(pos, Blocks.WOOL.cyan().defaultBlockState(), 3);
					} else if(WorldOfWoolsEffect.LIGHT_BLUE.contains(color)) {
						level.setBlock(pos, Blocks.WOOL.lightBlue().defaultBlockState(), 3);
					} else if(WorldOfWoolsEffect.BLUE.contains(color)) {
						level.setBlock(pos, Blocks.WOOL.blue().defaultBlockState(), 3);
					} else if(WorldOfWoolsEffect.PURPLE.contains(color)) {
						level.setBlock(pos, Blocks.WOOL.purple().defaultBlockState(), 3);
					} else if(WorldOfWoolsEffect.MAGENTA.contains(color)) {
						level.setBlock(pos, Blocks.WOOL.magenta().defaultBlockState(), 3);
					} else if(WorldOfWoolsEffect.PINK.contains(color)) {
						level.setBlock(pos, Blocks.WOOL.pink().defaultBlockState(), 3);
					}
				}
			}
		});
		*/
	}
	
	@Override
	public Block getBlock() {
		return BlockRegistry.WOOL_TNT.get();
	}
}

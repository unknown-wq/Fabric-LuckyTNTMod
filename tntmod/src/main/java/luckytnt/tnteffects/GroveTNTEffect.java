package luckytnt.tnteffects;

import luckytnt.LuckyTNTMod;
import luckytnt.registry.BlockRegistry;
import luckytntlib.util.IExplosiveEntity;
import luckytntlib.util.explosions.IForEachBlockExplosionEffect;
import luckytntlib.util.explosions.ImprovedExplosion;
import luckytntlib.util.tnteffects.PrimedTNTEffect;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.entity.Entity;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.structure.StructurePlacementData;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.resources.Identifier;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;

public class GroveTNTEffect extends PrimedTNTEffect {

	private final int strength;
	
	public GroveTNTEffect(int strength) {
		this.strength = strength;
	}
	
	@Override
	public void serverExplosion(IExplosiveEntity entity) {
		ImprovedExplosion explosion = new ImprovedExplosion(entity.getLevel(), (Entity)entity, entity.getPos(), strength);
		explosion.doBlockExplosion(new IForEachBlockExplosionEffect() {
			
			@SuppressWarnings("resource")
			@Override
			public void doBlockExplosion(Level level, BlockPos pos, BlockState state, double distance) {
				if(state.isSideSolidFullSquare(level, pos, Direction.UP) && state.getBlock().getBlastResistance() < 100 && !state.isAir() && (level.getBlockState(pos.up()).isAir() || level.getBlockState(pos.up()).getBlock().getHardness() <= 0.2f)) {
					level.setBlockState(pos, Blocks.GRASS_BLOCK.getDefaultState());
					if(Math.random() < 0.2f) {
						int random = level.random.nextInt(6);
						String string = "";
						switch (random) {
							case 0: string = "acaciatree"; break;
							case 1: string = "sprucetree"; break;
							case 2: string = "oaktree"; break;
							case 3: string = "darkoaktree"; break;
							case 4: string = "birchtree"; break;
							case 5: string = "jungletree"; break;
						}
						StructureTemplate template = ((ServerLevel)entity.getLevel()).getStructureTemplateManager().getTemplateOrBlank(Identifier.fromNamespaceAndPath(LuckyTNTMod.MODID, string));
						if(template != null) {
							template.place((ServerLevel)entity.getLevel(), pos.add(-1, 0, -1), pos.add(-1, 0, -1), new StructurePlacementData(), entity.getLevel().random, 3);
						}
					}
				}
			}
		});
	}
	
	@Override
	public Block getBlock() {
		return BlockRegistry.GROVE_TNT.get();
	}
}

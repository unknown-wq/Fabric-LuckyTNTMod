package luckytnt.tnteffects;

import luckytnt.LuckyTNTMod;
import luckytnt.registry.BlockRegistry;
import luckytntlib.util.IExplosiveEntity;
import luckytntlib.util.explosions.IForEachBlockExplosionEffect;
import luckytntlib.util.explosions.ImprovedExplosion;
import luckytntlib.util.tnteffects.PrimedTNTEffect;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.structure.StructurePlacementData;
import net.minecraft.structure.StructureTemplate;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

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
			public void doBlockExplosion(World level, BlockPos pos, BlockState state, double distance) {
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
						StructureTemplate template = ((ServerWorld)entity.getLevel()).getStructureTemplateManager().getTemplateOrBlank(Identifier.of(LuckyTNTMod.MODID, string));
						if(template != null) {
							template.place((ServerWorld)entity.getLevel(), pos.add(-1, 0, -1), pos.add(-1, 0, -1), new StructurePlacementData(), entity.getLevel().random, 3);
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

package luckytnt.tnteffects;

import java.util.function.Supplier;

import luckytnt.LuckyTNTMod;
import luckytntlib.block.LTNTBlock;
import luckytntlib.util.IExplosiveEntity;
import luckytntlib.util.tnteffects.PrimedTNTEffect;
import net.minecraft.world.level.block.Block;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.structure.StructurePlacementData;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.resources.Identifier;

public class HouseTNTEffect extends PrimedTNTEffect{

	private final Supplier<Supplier<LTNTBlock>> TNT;
	private final String house;
	private final int offX;
	private final int offZ;
	
	public HouseTNTEffect(Supplier<Supplier<LTNTBlock>> TNT, String house, int offX, int offZ) {
		this.TNT = TNT;
		this.house = house;
		this.offX = offX;
		this.offZ = offZ;
	}
	
	@Override
	public Block getBlock() {
		return TNT.get().get();
	}

	@SuppressWarnings("resource")
	@Override
	public void serverExplosion(IExplosiveEntity entity) {
		StructureTemplate template = ((ServerLevel)entity.getLevel()).getStructureManager().getTemplateOrBlank(Identifier.fromNamespaceAndPath(LuckyTNTMod.MODID, house));
		if(template != null) {
			template.place((ServerLevel)entity.getLevel(), toBlockPos(entity.getPos()).add(offX, 0, offZ), toBlockPos(entity.getPos()).add(offX, 0, offZ), new StructurePlacementData(), entity.getLevel().random, 3);
		}
	}
}

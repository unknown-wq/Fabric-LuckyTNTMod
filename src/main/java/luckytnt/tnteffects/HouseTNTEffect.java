package luckytnt.tnteffects;

import java.util.function.Supplier;

import luckytnt.LuckyTNTMod;
import luckytntlib.block.LTNTBlock;
import luckytntlib.util.IExplosiveEntity;
import luckytntlib.util.tnteffects.PrimedTNTEffect;
import net.minecraft.block.Block;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.structure.StructurePlacementData;
import net.minecraft.structure.StructureTemplate;
import net.minecraft.util.Identifier;

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
		StructureTemplate template = ((ServerWorld)entity.getLevel()).getStructureTemplateManager().getTemplateOrBlank(Identifier.of(LuckyTNTMod.MODID, house));
		if(template != null) {
			template.place((ServerWorld)entity.getLevel(), toBlockPos(entity.getPos()).add(offX, 0, offZ), toBlockPos(entity.getPos()).add(offX, 0, offZ), new StructurePlacementData(), entity.getLevel().random, 3);
		}
	}
}

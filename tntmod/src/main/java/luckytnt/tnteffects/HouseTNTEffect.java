package luckytnt.tnteffects;

import java.util.function.Supplier;

import luckytnt.LuckyTNTMod;
import luckytntlib.block.LTNTBlock;
import luckytntlib.util.IExplosiveEntity;
import luckytntlib.util.tnteffects.PrimedTNTEffect;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.resources.Identifier;

public class HouseTNTEffect extends PrimedTNTEffect{

	private final Supplier<Supplier<LTNTBlock>> TNT;
	private final String house;
	//the identifier is constant per effect, it does not need to be parsed and validated on every explosion
	private final Identifier houseId;
	private final int offX;
	private final int offZ;

	public HouseTNTEffect(Supplier<Supplier<LTNTBlock>> TNT, String house, int offX, int offZ) {
		this.TNT = TNT;
		this.house = house;
		this.houseId = Identifier.fromNamespaceAndPath(LuckyTNTMod.MODID, house);
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
		ServerLevel level = (ServerLevel)entity.getLevel();
		StructureTemplate template = level.getStructureManager().getOrCreate(houseId);
		if(template != null) {
			//the origin was built twice (two Vec3 floors and two BlockPos allocations) for the same value
			BlockPos origin = toBlockPos(entity.getPos()).offset(offX, 0, offZ);
			template.placeInWorld(level, origin, origin, new StructurePlaceSettings(), level.getRandom(), 3);
		}
	}
}

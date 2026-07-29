package luckytnt.tnteffects;

import net.minecraft.server.level.ServerLevel;
import luckytnt.registry.BlockRegistry;
import luckytntlib.util.IExplosiveEntity;
import luckytntlib.util.explosions.ImprovedExplosion;
import luckytntlib.util.tnteffects.PrimedTNTEffect;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.core.BlockPos;

public class PrismTNTEffect extends PrimedTNTEffect {

	public final int size;

	public PrismTNTEffect(int size) {
		this.size = size;
	}

	@Override
	public void serverExplosion(IExplosiveEntity ent) {
		//everything that does not change while the prism is carved is read once
		final Level level = ent.getLevel();
		final ServerLevel serverLevel = level instanceof ServerLevel sLevel ? sLevel : null;
		final BlockState air = Blocks.AIR.defaultBlockState();
		final ImprovedExplosion dummy = ImprovedExplosion.dummyExplosion(level);
		final BlockPos pos = toBlockPos(ent.getPos()).offset(-1 * (size / 2) + 1, 0, -1 * (size / 2) + 1);
		final int baseX = pos.getX();
		final int baseY = pos.getY();
		final int baseZ = pos.getZ();
		//only positions that are actually cleared need their own immutable BlockPos
		final BlockPos.MutableBlockPos cursor = new BlockPos.MutableBlockPos();

		for(int offY = (size / 2); offY > (-1 * (size / 2) - 1); offY--) {
			int tri = size;
			for(int offX = 0; offX < size; offX++) {
				for(int offZ = 0; offZ < tri; offZ++) {
					cursor.set(baseX + offX, baseY + offY, baseZ + offZ);
					BlockState state = level.getBlockState(cursor);
					//air (and the void air outside the build height) is already what this explosion would place:
					//Block#wasExploded does nothing for it and Level#setBlock would be a no-op, so it is skipped
					if(state.isAir()) {
						continue;
					}
					Block block = state.getBlock();
					if(block.getExplosionResistance() <= 100) {
						BlockPos pos1 = cursor.immutable();
						if(serverLevel != null) {
							block.wasExploded(serverLevel, pos1, dummy);
						}
						level.setBlock(pos1, air, Block.UPDATE_CLIENTS);
					}
				}
				tri--;
			}
		}
	}

	@Override
	public Block getBlock() {
		return BlockRegistry.PRISM_TNT.get();
	}
}

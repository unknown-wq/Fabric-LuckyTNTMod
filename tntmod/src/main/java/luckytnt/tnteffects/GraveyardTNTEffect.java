package luckytnt.tnteffects;
import net.minecraft.server.level.ServerLevel;

import org.joml.Vector3f;

import luckytnt.registry.BlockRegistry;
import luckytntlib.util.IExplosiveEntity;
import luckytntlib.util.explosions.ImprovedExplosion;
import luckytntlib.util.tnteffects.PrimedTNTEffect;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;

public class GraveyardTNTEffect extends PrimedTNTEffect {

	@Override
	public void serverExplosion(IExplosiveEntity entity) {
		//loop invariants: the level, the shared dummy explosion, the placed state and the floored centre.
		//toBlockPos(new Vec3(x + offX, ...)) is just floor(x) + offX for integer offsets, so the Vec3 and
		//the repeated Mth.floor calls are gone as well.
		final Level level = entity.getLevel();
		final ServerLevel sLevel = (ServerLevel)level;
		final ImprovedExplosion dummy = ImprovedExplosion.dummyExplosion(level);
		final BlockState grass = Blocks.GRASS_BLOCK.defaultBlockState();
		final int cx = Mth.floor(entity.x());
		final int cy = Mth.floor(entity.y()) - 10;
		final int cz = Mth.floor(entity.z());
		for(int offX = -20; offX <= 20; offX++) {
			final int xSqr = offX * offX;
			for(int offY = 0; offY <= 10; offY++) {
				final int xySqr = xSqr + offY * offY;
				//squared comparison instead of Math.sqrt, and the whole row is culled before any
				//BlockPos is built or any block state is read
				if(xySqr > 400) {
					continue;
				}
				for(int offZ = -20; offZ <= 20; offZ++) {
					if(xySqr + offZ * offZ > 400) {
						continue;
					}
					BlockPos pos = new BlockPos(cx + offX, cy + offY, cz + offZ);
					//one world read per position instead of three
					BlockState state = level.getBlockState(pos);
					if(state.getBlock().getExplosionResistance() <= 100 && !state.isCollisionShapeFullBlock(level, pos)) {
						state.getBlock().wasExploded(sLevel, pos, dummy);
						level.setBlockAndUpdate(pos, grass);
					}
				}
			}
		}
	}
	
	@Override
	public void spawnParticles(IExplosiveEntity entity) {
		for(int count = 0; count <= 20; count++) {
			entity.getLevel().addParticle(new DustParticleOptions(((int)(0.5f*255)<<16)|((int)(0.2f*255)<<8)|(int)(0f*255), 0.75f), entity.x(), entity.y() + 1D + 0.05D * count, entity.z(), 0, 0, 0);
			entity.getLevel().addParticle(new DustParticleOptions(((int)(0.5f*255)<<16)|((int)(0.2f*255)<<8)|(int)(0f*255), 0.75f), entity.x() - 0.5D + count * 0.05D, entity.y() + 1D + (2D / 3D), entity.z(), 0, 0, 0);
		}
	}
	
	@Override
	public Block getBlock() {
		return BlockRegistry.GRAVEYARD_TNT.get();
	}
}

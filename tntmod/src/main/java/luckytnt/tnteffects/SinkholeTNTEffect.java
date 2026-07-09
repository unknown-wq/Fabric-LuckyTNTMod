package luckytnt.tnteffects;

import luckytnt.registry.BlockRegistry;
import luckytntlib.util.IExplosiveEntity;
import luckytntlib.util.explosions.ImprovedExplosion;
import luckytntlib.util.tnteffects.PrimedTNTEffect;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public class SinkholeTNTEffect extends PrimedTNTEffect {

	@Override
	public void explosionTick(IExplosiveEntity ent) {
		if(ent.getTNTFuse() == 250) {
			NbtCompound tag = ent.getPersistentData();
			tag.putInt("depth", 20);
			ent.setPersistentData(tag);
		}
		if(ent.getTNTFuse() <= 150) {
			((Entity)ent).setVelocity(Vec3d.ZERO);
			((Entity)ent).setNoGravity(true);
		}
		if(ent.getTNTFuse() <= 150 && !ent.getLevel().isClient() && ent.getTNTFuse() % 2 == 0) {
			for(int offX = -33; offX <= 33; offX++) {
				for(int offY = -33; offY <= 33; offY++) {
					for(int offZ = -33; offZ <= 33; offZ++) {
						double distance = Math.sqrt(offX * offX + offY * offY + offZ * offZ) + Math.random() * 4D - 2D;
						BlockPos pos = new BlockPos(MathHelper.floor(ent.x() + offX), MathHelper.floor(ent.y() + offY + ent.getPersistentData().getInt("depth")), MathHelper.floor(ent.z() + offZ));
						if(distance <= 30 && ent.getLevel().getBlockState(pos).getBlock().getBlastResistance() < 200) {
							ent.getLevel().getBlockState(pos).getBlock().onDestroyedByExplosion(ent.getLevel(), pos, ImprovedExplosion.dummyExplosion(ent.getLevel()));
							ent.getLevel().setBlockState(pos, Blocks.AIR.getDefaultState(), 3);
						}
					}
				}
			}
			
			NbtCompound tag = ent.getPersistentData();
			tag.putInt("depth", ent.getPersistentData().getInt("depth") - 1);
			ent.setPersistentData(tag);
		}
	}
	
	@Override
	public Block getBlock() {
		return BlockRegistry.SINKHOLE_TNT.get();
	}
	
	@Override
	public int getDefaultFuse(IExplosiveEntity ent) {
		return 250;
	}
}

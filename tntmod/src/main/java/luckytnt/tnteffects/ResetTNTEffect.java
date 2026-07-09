package luckytnt.tnteffects;

import java.util.List;

import com.google.common.collect.Lists;
import com.mojang.datafixers.util.Pair;

import luckytnt.entity.PrimedResetTNT;
import luckytnt.registry.BlockRegistry;
import luckytntlib.block.LTNTBlock;
import luckytntlib.util.IExplosiveEntity;
import luckytntlib.util.explosions.ExplosionHelper;
import luckytntlib.util.explosions.IForEachBlockExplosionEffect;
import luckytntlib.util.tnteffects.PrimedTNTEffect;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class ResetTNTEffect extends PrimedTNTEffect {

	@Override
	public void explosionTick(IExplosiveEntity entity) {
		if(entity instanceof PrimedResetTNT ent && ent.getTNTFuse() == 2400) {
			saveBlocks(ent);
			saveEntities(ent);
		}
	}
	
	@Override
	public void serverExplosion(IExplosiveEntity entity) {
		if(entity instanceof PrimedResetTNT ent) {
			for(Pair<BlockPos, BlockState> pair : ent.blocks) {
				if(!ent.getLevel().getBlockState(pair.getFirst()).equals(pair.getSecond())) {
					ent.getLevel().setBlockState(pair.getFirst(), pair.getSecond(), 3);
				}
			}
			
			for(Pair<Vec3d, Entity> pair : ent.entities) {
	    		if(pair.getSecond().isAlive() && !(pair.getSecond() instanceof PlayerEntity)) {
	    			pair.getSecond().setPosition(pair.getFirst());
	    		} else if(pair.getSecond() instanceof PlayerEntity pla) {
	    			if(pla instanceof ServerPlayerEntity player) {
	    				player.requestTeleport(pair.getFirst().x, pair.getFirst().y, pair.getFirst().z);
	    			}
	    		}
	    	}
		}
	}
	
	public void saveBlocks(PrimedResetTNT ent) {
		ent.blocks = Lists.newArrayList();
		ExplosionHelper.doSphericalExplosion(ent.getLevel(), ent.getPos(), 100, new IForEachBlockExplosionEffect() {
			
			@Override
			public void doBlockExplosion(World level, BlockPos pos, BlockState state, double distance) {
				if(!(state.getBlock() instanceof LTNTBlock)) {
					ent.blocks.add(Pair.of(pos, state));
				}
			}
		});
	}
	
	public void saveEntities(PrimedResetTNT ent) {
		ent.entities = Lists.newArrayList();
		List<Entity> list = ent.getLevel().getOtherEntities(ent, new Box(ent.x() - 100, ent.y() - 100, ent.z() - 100, ent.x() + 100, ent.y() + 100, ent.z() + 100));

    	for(int i = 0; i < list.size(); i++) {
    		Entity entity = list.get(i);
    		ent.entities.add(Pair.of(new Vec3d(entity.getX(), entity.getY(), entity.getZ()), entity));
    	}
	}
	
	@Override
	public Block getBlock() {
		return BlockRegistry.RESET_TNT.get();
	}
	
	@Override
	public int getDefaultFuse(IExplosiveEntity ent) {
		return 2400;
	}
}

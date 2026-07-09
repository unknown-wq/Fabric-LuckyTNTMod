package luckytnt.block;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSources;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import net.minecraft.world.tick.TickPriority;

public class ToxicStoneBlock extends Block {
	private int timer = 100;
	
	public ToxicStoneBlock(Settings properties) {
		super(properties);
	}
	
	@Override
	public void onBlockAdded(BlockState state, World level, BlockPos pos, BlockState oldstate, boolean moving) {
		super.onBlockAdded(state, level, pos, oldstate, moving);
		level.scheduleBlockTick(pos, this, 1, TickPriority.EXTREMELY_HIGH);
		if(level instanceof ServerWorld slevel) {
			scheduledTick(state, slevel, pos, slevel.getRandom());
		}
	}
	
	@Override
	public void scheduledTick(BlockState state, ServerWorld level, BlockPos pos, Random rand) {
		super.scheduledTick(state, level, pos, rand);
		level.scheduleBlockTick(pos, this, 1, TickPriority.EXTREMELY_HIGH);
		if(timer >= 0) {
			timer--;
		}
		if(timer == 0) {
			BlockPos min = pos.add(-5, -5, -5);
			BlockPos max = pos.add(5, 5, 5);
			List<LivingEntity> list = level.getNonSpectatingEntities(LivingEntity.class, new Box(min.getX(), min.getY(), min.getZ(), max.getX(), max.getY(), max.getZ()));
			for(LivingEntity living : list) {
				DamageSources sources = level.getDamageSources();
				if(living instanceof PlayerEntity player) {
					if(!player.isCreative() && !player.isSpectator()) {
						player.damage(sources.magic(), 8f);
					}
				} else {
					living.damage(sources.magic(), 8f);
				}
			}
			timer = 100;
		}
	}
}

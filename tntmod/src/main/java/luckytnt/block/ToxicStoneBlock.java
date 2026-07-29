package luckytnt.block;

import java.util.List;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.damagesource.DamageSources;
import net.minecraft.world.entity.player.Player;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.AABB;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;

public class ToxicStoneBlock extends Block {

	/** ticks between two damage pulses of a single toxic stone block */
	private static final int DAMAGE_INTERVAL = 100;

	public ToxicStoneBlock(BlockBehaviour.Properties properties) {
		super(properties);
	}

	@Override
	public void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldstate, boolean moving) {
		super.onPlace(state, level, pos, oldstate, moving);
		//one scheduled tick per position; the level dedupes repeated schedules for the same (pos, block)
		level.scheduleTick(pos, this, DAMAGE_INTERVAL);
	}

	@Override
	public void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource rand) {
		super.tick(state, level, pos, rand);
		level.scheduleTick(pos, this, DAMAGE_INTERVAL);
		BlockPos min = pos.offset(-5, -5, -5);
		BlockPos max = pos.offset(5, 5, 5);
		List<LivingEntity> list = level.getEntitiesOfClass(LivingEntity.class, new AABB(min.getX(), min.getY(), min.getZ(), max.getX(), max.getY(), max.getZ()));
		for(LivingEntity living : list) {
			DamageSources sources = level.damageSources();
			if(living instanceof Player player) {
				if(!player.isCreative() && !player.isSpectator()) {
					player.hurtServer(level, sources.magic(), 8f);
				}
			} else {
				living.hurtServer(level, sources.magic(), 8f);
			}
		}
	}
}

package luckytnt.tnteffects;

import java.util.function.Supplier;

import luckytntlib.block.LTNTBlock;
import luckytntlib.util.IExplosiveEntity;
import luckytntlib.util.explosions.ExplosionHelper;
import luckytntlib.util.explosions.IForEachBlockExplosionEffect;
import luckytntlib.util.explosions.ImprovedExplosion;
import luckytntlib.util.tnteffects.PrimedTNTEffect;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.squid.Squid;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;

public class OceanTNTEffect extends PrimedTNTEffect {
	private final int radius;
	private final int radiusY;
	private final int squidCound;
	private Supplier<Supplier<LTNTBlock>> block;

	public OceanTNTEffect(Supplier<Supplier<LTNTBlock>> block, int radius, int radiusY, int squidCount) {
		this.radius = radius;
		this.radiusY = radiusY;
		this.squidCound = squidCount;
		this.block = block;
	}
	
	public OceanTNTEffect(int radius, int radiusY, int squidCount) {
		this.radius = radius;
		this.radiusY = radiusY;
		this.squidCound = squidCount;
	}
	
	@Override
	public void serverExplosion(IExplosiveEntity entity) {
		ImprovedExplosion dummyExplosion = ImprovedExplosion.dummyExplosion(entity.getLevel());
		ExplosionHelper.doCylindricalExplosion(entity.getLevel(), entity.getPos(), radius, radiusY, new IForEachBlockExplosionEffect() {
			
			@Override
			public void doBlockExplosion(Level level, BlockPos pos, BlockState state, double distance) {
				if(pos.getY() <= entity.getPos().y) {
					if((!state.isSideSolidFullSquare(level, pos, Direction.UP) && state.getBlock().getBlastResistance() < 100) || state.getBlock().getBlastResistance() < 4) {
						state.getBlock().onDestroyedByExplosion(level, pos, dummyExplosion);
						level.setBlockState(pos, Blocks.WATER.getDefaultState());
					}
				}
			}
		});
		
		for(int i = 0; i < squidCound; i++) {
			Squid squid = new Squid(EntityTypes.SQUID, entity.getLevel());
			squid.setPosition(entity.x() + (Math.random() * radius * 2 - radius), entity.y(), entity.z() + (Math.random() * radius * 2 - radius));
			entity.getLevel().addFreshEntity(squid);
		}
	}
	
	@Override
	public void spawnParticles(IExplosiveEntity ent) {
		ent.getLevel().addParticle(ParticleTypes.SPLASH, ent.x(), ent.y() + 0.7f, ent.z(), 0, 0, 0);
	}

	@Override
	public Block getBlock() {
		return block.get().get();
	}
	
	@Override
	public int getDefaultFuse(IExplosiveEntity entity) {
		return 160;
	}
}

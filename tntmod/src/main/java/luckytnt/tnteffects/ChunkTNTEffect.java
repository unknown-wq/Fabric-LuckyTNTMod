package luckytnt.tnteffects;

import luckytnt.registry.BlockRegistry;
import luckytntlib.util.IExplosiveEntity;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityTypes;

public class ChunkTNTEffect extends CubicTNTEffect {

	public ChunkTNTEffect() {
		super(50);
	}
	
	@Override
	public void explosionTick(IExplosiveEntity ent) {
		// explosionTick runs on both logical sides and addFreshEntity is a no-op on the client
		if(ent.getTNTFuse() == 160 && ent.getLevel() instanceof ServerLevel level) {
			Entity lighting = new LightningBolt(EntityTypes.LIGHTNING_BOLT, level);
			lighting.setPos(ent.getPos());
			level.addFreshEntity(lighting);
		}
	}

	@Override
	public void spawnParticles(IExplosiveEntity ent) {
		for(float i = 0; i < 3.25f; i += 0.25f) {
			ent.getLevel().addParticle(ParticleTypes.FLAME, ent.x() + 1.5f, ent.y() - 1 + i, ent.z() + 1.5f, 0, 0, 0);
			ent.getLevel().addParticle(ParticleTypes.FLAME, ent.x() - 1.5f, ent.y() - 1 + i, ent.z() - 1.5f, 0, 0, 0);
			ent.getLevel().addParticle(ParticleTypes.FLAME, ent.x() + 1.5f, ent.y() - 1 + i, ent.z() - 1.5f, 0, 0, 0);
			ent.getLevel().addParticle(ParticleTypes.FLAME, ent.x() - 1.5f, ent.y() - 1 + i, ent.z() + 1.5f, 0, 0, 0);
			ent.getLevel().addParticle(ParticleTypes.FLAME, ent.x() - 1.5f + i, ent.y() - 1, ent.z() + 1.5f, 0, 0, 0);
			ent.getLevel().addParticle(ParticleTypes.FLAME, ent.x() - 1.5f, ent.y() - 1, ent.z() - 1.5f + i, 0, 0, 0);
			ent.getLevel().addParticle(ParticleTypes.FLAME, ent.x() - 1.5f + i, ent.y() + 2, ent.z() + 1.5f, 0, 0, 0);
			ent.getLevel().addParticle(ParticleTypes.FLAME, ent.x() - 1.5f, ent.y() + 2, ent.z() - 1.5f + i, 0, 0, 0);
			ent.getLevel().addParticle(ParticleTypes.FLAME, ent.x() + 1.5f - i, ent.y() - 1, ent.z() - 1.5f, 0, 0, 0);
			ent.getLevel().addParticle(ParticleTypes.FLAME, ent.x() + 1.5f, ent.y() - 1, ent.z() + 1.5f - i, 0, 0, 0);
			ent.getLevel().addParticle(ParticleTypes.FLAME, ent.x() + 1.5f - i, ent.y() + 2, ent.z() - 1.5f, 0, 0, 0);
			ent.getLevel().addParticle(ParticleTypes.FLAME, ent.x() + 1.5f, ent.y() + 2, ent.z() + 1.5f - i, 0, 0, 0);
		}
	}
	
	@Override
	public Block getBlock() {
		return BlockRegistry.CHUNK_TNT.get();
	}
	
	@Override
	public int getDefaultFuse(IExplosiveEntity ent) {
		return 160;
	}
}

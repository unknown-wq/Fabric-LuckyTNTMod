package luckytnt.tnteffects;

import luckytnt.registry.BlockRegistry;
import luckytntlib.util.IExplosiveEntity;
import luckytntlib.util.explosions.ImprovedExplosion;
import luckytntlib.util.tnteffects.PrimedTNTEffect;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;

public class MimicTNTEffect extends PrimedTNTEffect {

	@Override
	public void explosionTick(IExplosiveEntity ent) {
		((Entity)ent).setVelocity(0, 0, 0);
		((Entity)ent).setPosition(((Entity) ent).prevX, ((Entity) ent).prevY, ((Entity) ent).prevZ);
		if (ent.getLevel().getClosestPlayer((Entity) ent, 5) != null && ent.getLevel().getClosestPlayer((Entity) ent, 5) != ent.owner()) {
			ent.getLevel().playSound((Entity)ent, toBlockPos(ent.getPos()), SoundEvents.ENTITY_GENERIC_EXPLODE.value(), SoundCategory.BLOCKS, 4f, (1f + (ent.getLevel().getRandom().nextFloat() - ent.getLevel().getRandom().nextFloat()) * 0.2f) * 0.7f);
			if(!ent.getLevel().isClient()) {
				serverExplosion(ent);
				ent.destroy();
			}
		}
	}

	@Override
	public void serverExplosion(IExplosiveEntity ent) {
		ImprovedExplosion explosion = new ImprovedExplosion(ent.getLevel(), (Entity)ent, ent.getPos(), 20);
		explosion.doEntityExplosion(2f, true);
		explosion.doBlockExplosion(1f, 1f, 1f, 1.5f, false, false);
	}

	@Override
	public void spawnParticles(IExplosiveEntity ent) {

	}

	@Override
	public BlockState getBlockState(IExplosiveEntity ent) {
		return ent.getLevel().getBlockState(toBlockPos(ent.getPos()).down()).isAir() ? BlockRegistry.MIMIC_TNT.get().getDefaultState() : ent.getLevel().getBlockState(toBlockPos(ent.getPos()).down());
	}

	@Override
	public int getDefaultFuse(IExplosiveEntity ent) {
		return 20000;
	}
}

package luckytnt.entity;

import luckytnt.LuckyTNTMod;
import luckytntlib.entity.LExplosiveProjectile;
import luckytntlib.util.tnteffects.PrimedTNTEffect;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.core.particles.BlockStateParticleEffect;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceKey;
import net.minecraft.core.registries.Registries;
import net.minecraft.core.Holder.Reference;
import net.minecraft.sounds.SoundSource;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.resources.Identifier;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.core.Holder;

public class HailstoneProjectile extends LExplosiveProjectile {

	public HailstoneProjectile(EntityType<LExplosiveProjectile> type, Level level, PrimedTNTEffect effect) {
		super(type, level, effect);
	}

	@Override
	public void onBlockHit(BlockHitResult result) {
		super.onBlockHit(result);
		getWorld().playSound(null, new BlockPos(Mth.floor(x()), Mth.floor(y()), Mth.floor(z())), SoundEvents.BLOCK_GLASS_BREAK, SoundSource.BLOCKS, 0.5f, 1f);
		for(int count = 0; count < 10; count++)
			getWorld().addParticle(new BlockStateParticleEffect(ParticleTypes.BLOCK, Blocks.SNOW.defaultBlockState()), x(), y(), z(), 0, 0, 0);
		destroy();
	}
	
	@Override
	public void onEntityHit(EntityHitResult result) {
		super.onEntityHit(result);
		if(result.getEntity() instanceof LivingEntity lent) {
			Reference<DamageType> type = getLevel().registryAccess().get(Registries.DAMAGE_TYPE).entryOf(ResourceKey.of(Registries.DAMAGE_TYPE, Identifier.fromNamespaceAndPath(LuckyTNTMod.MODID, "hailstone")));
			DamageSource source = new DamageSource(type, this, owner());
			
			lent.damage(source, 4f);
		}
	} 
}

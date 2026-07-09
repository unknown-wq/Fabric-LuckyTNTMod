package luckytnt.block;

import java.util.Collections;
import java.util.List;

import org.joml.Vector3f;

import com.mojang.serialization.MapCodec;

import luckytnt.registry.EffectRegistry;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FallingBlock;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.entity.InsideBlockEffectApplier;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.Explosion;

public class NuclearWasteBlock extends FallingBlock {
	public static final MapCodec<NuclearWasteBlock> CODEC = simpleCodec(NuclearWasteBlock::new);
	
	public NuclearWasteBlock(BlockBehaviour.Properties properties) {
		super(properties);
	}
	
	@Override
	public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
		return Shapes.box(0, 0, 0, 1, 2d / 16d, 1);
	}

	@Override
	public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
		BlockPos posDown = new BlockPos(pos.getX(), pos.getY() - 1, pos.getZ());
		if(Block.isFaceFull(level.getBlockState(posDown).getCollisionShape(level, posDown), Direction.UP) || level.getBlockState(posDown).isAir()){
			return true;
		}
		return super.canSurvive(state, level, pos);
	}
	
	@Override
	public void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
		super.randomTick(state, level, pos, random);
		if(Math.random() < 0.2f) {
			if(level.getBlockState(new BlockPos(pos.getX(), pos.getY() - 1, pos.getZ())).getBlock().getExplosionResistance() < 100) {
				level.setBlock(new BlockPos(pos.getX(), pos.getY() - 1, pos.getZ()), Blocks.AIR.defaultBlockState(), 3);
				level.playSound(null, pos, SoundEvents.FIRE_EXTINGUISH, SoundSource.BLOCKS, 1, 1);
				if(Math.random() < 0.05f) {
					level.setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
					level.sendParticles(new DustParticleOptions(((int)(1f*255)<<16)|((int)(1f*255)<<8)|(int)(0.1f*255), 1), pos.getX(), pos.getY(), pos.getZ(), 40, 0.6f, 0.6f, 0.6f, 0);
				}
				level.sendParticles(new DustParticleOptions(((int)(1f*255)<<16)|((int)(1f*255)<<8)|(int)(0.1f*255), 1), pos.getX(), pos.getY() - 1, pos.getZ(), 40, 0.6f, 0.6f, 0.6f, 0);
			}
		}
	}
	
	@Override
	public List<ItemStack> getDrops(BlockState state, LootParams.Builder builder) {
		return Collections.singletonList(ItemStack.EMPTY);
	}

	@Override
	public void entityInside(BlockState state, Level level, BlockPos pos, Entity entity, InsideBlockEffectApplier effectApplier, boolean isPrecise) {
		super.entityInside(state, level, pos, entity, effectApplier, isPrecise);
		if(entity instanceof LivingEntity l_Entity) {
			l_Entity.addEffect(new MobEffectInstance(MobEffects.POISON, 120, 4, false, true));
			l_Entity.addEffect(new MobEffectInstance(BuiltInRegistries.MOB_EFFECT.getOrThrow(EffectRegistry.CONTAMINATED), 120, 0, false, true));
			l_Entity.addEffect(new MobEffectInstance(MobEffects.NAUSEA, 120, 0, false, true));
		}
		else if(entity instanceof ItemEntity i_Entity && level instanceof ServerLevel serverLevel) {
			i_Entity.hurtServer(serverLevel, serverLevel.damageSources().explosion(null, null), 100);
		}
	}

	@Override
	protected MapCodec<? extends FallingBlock> getCodec() {
		return CODEC;
	}
}

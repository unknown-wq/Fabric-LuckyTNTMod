package luckytnt.block;

import java.util.Collections;
import java.util.List;

import org.joml.Vector3f;

import com.mojang.serialization.MapCodec;

import luckytnt.registry.EffectRegistry;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FallingBlock;
import net.minecraft.block.ShapeContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.context.LootContextParameterSet;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.registry.Registries;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;
import net.minecraft.world.explosion.Explosion;

public class NuclearWasteBlock extends FallingBlock {
	public static final MapCodec<NuclearWasteBlock> CODEC = createCodec(NuclearWasteBlock::new);
	
	public NuclearWasteBlock(AbstractBlock.Settings properties) {
		super(properties);
	}
	
	@Override
	public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
		return VoxelShapes.cuboid(0, 0, 0, 1, 2d / 16d, 1);
	}

	@Override
	public boolean canPlaceAt(BlockState state, WorldView level, BlockPos pos) {
		BlockPos posDown = new BlockPos(pos.getX(), pos.getY() - 1, pos.getZ());
		if(Block.isFaceFullSquare(level.getBlockState(posDown).getCollisionShape(level, posDown), Direction.UP) || level.getBlockState(posDown).isAir()){
			return true;
		}
		return super.canPlaceAt(state, level, pos);
	}
	
	@Override
	public void randomTick(BlockState state, ServerWorld level, BlockPos pos, Random random) {
		super.randomTick(state, level, pos, random);
		if(Math.random() < 0.2f) {
			if(level.getBlockState(new BlockPos(pos.getX(), pos.getY() - 1, pos.getZ())).getBlock().getBlastResistance() < 100) {
				level.setBlockState(new BlockPos(pos.getX(), pos.getY() - 1, pos.getZ()), Blocks.AIR.getDefaultState(), 3);
				level.playSound(null, pos, SoundEvents.BLOCK_FIRE_EXTINGUISH, SoundCategory.BLOCKS, 1, 1);
				if(Math.random() < 0.05f) {
					level.setBlockState(pos, Blocks.AIR.getDefaultState(), 3);
					level.spawnParticles(new DustParticleEffect(new Vector3f(1f, 1f, 0.1f), 1), pos.getX(), pos.getY(), pos.getZ(), 40, 0.6f, 0.6f, 0.6f, 0);
				}
				level.spawnParticles(new DustParticleEffect(new Vector3f(1f, 1f, 0.1f), 1), pos.getX(), pos.getY() - 1, pos.getZ(), 40, 0.6f, 0.6f, 0.6f, 0);
			}
		}
	}
	
	@Override
	public List<ItemStack> getDroppedStacks(BlockState state, LootContextParameterSet.Builder builder) {
		return Collections.singletonList(ItemStack.EMPTY);
	}
		
	@Override
	public void onEntityCollision(BlockState state, World level, BlockPos pos, Entity entity) {
		super.onEntityCollision(state, level, pos, entity);
		if(entity instanceof LivingEntity l_Entity) {
			l_Entity.addStatusEffect(new StatusEffectInstance(StatusEffects.POISON, 120, 4, false, true));
			l_Entity.addStatusEffect(new StatusEffectInstance(Registries.STATUS_EFFECT.entryOf(EffectRegistry.CONTAMINATED), 120, 0, false, true));
			l_Entity.addStatusEffect(new StatusEffectInstance(StatusEffects.NAUSEA, 120, 0, false, true));
		}
		else if(entity instanceof ItemEntity i_Entity) {
			i_Entity.damage(Explosion.createDamageSource(level, entity), 100);
		}
	}

	@Override
	protected MapCodec<? extends FallingBlock> getCodec() {
		return CODEC;
	}
}

package luckytnt.block;

import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

import luckytnt.block.entity.SmokeTNTBlockEntity;
import luckytnt.registry.EntityRegistry;
import luckytntlib.block.LTNTBlock;
import luckytntlib.entity.PrimedLTNT;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.DyeItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Hand;
import net.minecraft.util.ItemActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

public class SmokeTNTBlock extends LTNTBlock implements BlockEntityProvider {

	public SmokeTNTBlock(AbstractBlock.Settings properties) {
		super(properties, EntityRegistry.SMOKE_TNT, false);
	}

	@Override
	public PrimedLTNT explode(World level, boolean exploded, double x, double y, double z, @Nullable LivingEntity igniter) throws NullPointerException {
		if(TNT != null) {
			BlockEntity blockEntity = level.getBlockEntity(new BlockPos(MathHelper.floor(x), MathHelper.floor(y), MathHelper.floor(z)));
			PrimedLTNT tnt = TNT.get().create(level);
			tnt.setFuse(exploded && randomizedFuseUponExploded() ? tnt.getEffect().getDefaultFuse(tnt) / 8 + random.nextInt(MathHelper.clamp(tnt.getEffect().getDefaultFuse(tnt) / 4, 1, Integer.MAX_VALUE)) : tnt.getEffect().getDefaultFuse(tnt));
			tnt.setPosition(x + 0.5f, y, z + 0.5f);
			tnt.setOwner(igniter);
			if(blockEntity != null && blockEntity instanceof SmokeTNTBlockEntity smoke) {
				NbtCompound tag = tnt.getPersistentData();
				tag.putFloat("r", smoke.getPersistentData().getFloat("r"));
				tag.putFloat("g", smoke.getPersistentData().getFloat("g"));
				tag.putFloat("b", smoke.getPersistentData().getFloat("b"));
				tnt.setPersistentData(tag);
			}
			level.spawnEntity(tnt);
			level.playSound(null, new BlockPos(MathHelper.floor(x), MathHelper.floor(y), MathHelper.floor(z)), SoundEvents.ENTITY_TNT_PRIMED, SoundCategory.MASTER, 1, 1);
			if(level.getBlockState(new BlockPos(MathHelper.floor(x), MathHelper.floor(y), MathHelper.floor(z))).getBlock() == this) {
				level.setBlockState(new BlockPos(MathHelper.floor(x), MathHelper.floor(y), MathHelper.floor(z)), Blocks.AIR.getDefaultState(), 3);
			}
			return tnt;
		}
		throw new NullPointerException("No TNT entity present. Make sure it is registered before the block is registered");
	}
	
	@Override
	public ItemActionResult onUseWithItem(ItemStack stack, BlockState state, World level, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult result) {
        BlockEntity block = level.getBlockEntity(pos);	
		if(player.getStackInHand(hand).getItem() instanceof DyeItem dye && block != null && block instanceof SmokeTNTBlockEntity blockEntity) {
			if(dye == Items.BLACK_DYE) {
				if(blockEntity.getPersistentData().getFloat("r") > 0 || blockEntity.getPersistentData().getFloat("g") > 0 || blockEntity.getPersistentData().getFloat("b") > 0) {
					if(!player.isCreative()) {
						player.getStackInHand(hand).decrement(1);
					}
				}
				blockEntity.getPersistentData().putFloat("r", MathHelper.clamp(blockEntity.getPersistentData().getFloat("r") - 0.1f, 0f, 1f));
				blockEntity.getPersistentData().putFloat("g", MathHelper.clamp(blockEntity.getPersistentData().getFloat("g") - 0.1f, 0f, 1f));
				blockEntity.getPersistentData().putFloat("b", MathHelper.clamp(blockEntity.getPersistentData().getFloat("b") - 0.1f, 0f, 1f));
				if(level instanceof ServerWorld sLevel) {
					sLevel.spawnParticles(new DustParticleEffect(new Vector3f(blockEntity.getPersistentData().getFloat("r"), blockEntity.getPersistentData().getFloat("g"), blockEntity.getPersistentData().getFloat("b")), 1f), pos.getX() + 0.5f, pos.getY() + 1f, pos.getZ() + 0.5f, 1, 0, 0, 0, 0);
				}
				return ItemActionResult.SUCCESS;
			}
			if(dye == Items.WHITE_DYE) {
				if(blockEntity.getPersistentData().getFloat("r") < 1 || blockEntity.getPersistentData().getFloat("g") < 1 || blockEntity.getPersistentData().getFloat("b") < 1) {
					if(!player.isCreative()) {
						player.getStackInHand(hand).decrement(1);
					}
				}
				blockEntity.getPersistentData().putFloat("r", MathHelper.clamp(blockEntity.getPersistentData().getFloat("r") + 0.1f, 0f, 1f));
				blockEntity.getPersistentData().putFloat("g", MathHelper.clamp(blockEntity.getPersistentData().getFloat("g") + 0.1f, 0f, 1f));
				blockEntity.getPersistentData().putFloat("b", MathHelper.clamp(blockEntity.getPersistentData().getFloat("b") + 0.1f, 0f, 1f));
				if(level instanceof ServerWorld sLevel) {
					sLevel.spawnParticles(new DustParticleEffect(new Vector3f(blockEntity.getPersistentData().getFloat("r"), blockEntity.getPersistentData().getFloat("g"), blockEntity.getPersistentData().getFloat("b")), 1f), pos.getX() + 0.5f, pos.getY() + 1f, pos.getZ() + 0.5f, 1, 0, 0, 0, 0);
				}
				return ItemActionResult.SUCCESS;
			}
			if(dye == Items.RED_DYE) {
				if(blockEntity.getPersistentData().getFloat("r") < 1) {
					if(!player.isCreative()) {
						player.getStackInHand(hand).decrement(1);
					}
				}
				blockEntity.getPersistentData().putFloat("r", MathHelper.clamp(blockEntity.getPersistentData().getFloat("r") + 0.1f, 0f, 1f));
				if(level instanceof ServerWorld sLevel) {
					sLevel.spawnParticles(new DustParticleEffect(new Vector3f(blockEntity.getPersistentData().getFloat("r"), blockEntity.getPersistentData().getFloat("g"), blockEntity.getPersistentData().getFloat("b")), 1f), pos.getX() + 0.5f, pos.getY() + 1f, pos.getZ() + 0.5f, 1, 0, 0, 0, 0);
				}
				return ItemActionResult.SUCCESS;
			}
			if(dye == Items.GREEN_DYE) {
				if(blockEntity.getPersistentData().getFloat("g") < 1) {
					if(!player.isCreative()) {
						player.getStackInHand(hand).decrement(1);
					}
				}
				blockEntity.getPersistentData().putFloat("g", MathHelper.clamp(blockEntity.getPersistentData().getFloat("g") + 0.1f, 0f, 1f));
				if(level instanceof ServerWorld sLevel) {
					sLevel.spawnParticles(new DustParticleEffect(new Vector3f(blockEntity.getPersistentData().getFloat("r"), blockEntity.getPersistentData().getFloat("g"), blockEntity.getPersistentData().getFloat("b")), 1f), pos.getX() + 0.5f, pos.getY() + 1f, pos.getZ() + 0.5f, 1, 0, 0, 0, 0);
				}
				return ItemActionResult.SUCCESS;
			}
			if(dye == Items.BLUE_DYE) {
				if(blockEntity.getPersistentData().getFloat("b") < 1) {
					if(!player.isCreative()) {
						player.getStackInHand(hand).decrement(1);
					}
				}
				blockEntity.getPersistentData().putFloat("b", MathHelper.clamp(blockEntity.getPersistentData().getFloat("b") + 0.1f, 0f, 1f));
				if(level instanceof ServerWorld sLevel) {
					sLevel.spawnParticles(new DustParticleEffect(new Vector3f(blockEntity.getPersistentData().getFloat("r"), blockEntity.getPersistentData().getFloat("g"), blockEntity.getPersistentData().getFloat("b")), 1f), pos.getX() + 0.5f, pos.getY() + 1f, pos.getZ() + 0.5f, 1, 0, 0, 0, 0);
				}
				return ItemActionResult.SUCCESS;
			}
		}
		return super.onUseWithItem(stack, state, level, pos, player, hand, result);
	}
	
	@Override
	public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
		return EntityRegistry.SMOKE_TNT_BLOCK_ENTITY.get().instantiate(pos, state);
	}	
}

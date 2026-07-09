package luckytnt.block;

import org.jetbrains.annotations.Nullable;

import luckytnt.registry.EntityRegistry;
import luckytnt.registry.ItemRegistry;
import luckytnt.util.StructureStates;
import luckytntlib.block.LTNTBlock;
import luckytntlib.entity.PrimedLTNT;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.util.Hand;
import net.minecraft.util.ItemActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

public class StructureTNTBlock extends LTNTBlock {

	public static final EnumProperty<StructureStates> STRUCTURE = EnumProperty.of("structure", StructureStates.class);
	
    public StructureTNTBlock(AbstractBlock.Settings properties) {
        super(properties, EntityRegistry.STRUCTURE_TNT, true);
    }

    @Override
    public void appendProperties(StateManager.Builder<Block, BlockState> definition) {
    	super.appendProperties(definition);
    	definition.add(STRUCTURE);
    }
    
    @Override
    public ItemActionResult onUseWithItem(ItemStack stack, BlockState state, World level, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult result) {
    	if(stack.getItem() == Items.FLINT_AND_STEEL) {
    		explode(level, false, pos.getX(), pos.getY(), pos.getZ(), player);
    		level.setBlockState(pos, Blocks.AIR.getDefaultState(), 3);
    		if(!player.isCreative()) {
    			stack.damage(1, player, LivingEntity.getSlotForHand(hand));
    		}
        	player.incrementStat(Stats.USED.getOrCreateStat(stack.getItem()));
        	return ItemActionResult.success(level.isClient);
    	}
    	else if(stack.getItem() == ItemRegistry.CONFIGURATION_WAND.get()) {
    		cycleThroughStructures(level, state, pos);
    		return ItemActionResult.success(level.isClient);
    	}
    	return ItemActionResult.FAIL;
    }
    
    public void cycleThroughStructures(World level, BlockState state, BlockPos pos) {
    	StructureStates structure = state.get(STRUCTURE);
    	if(structure == StructureStates.PILLAGER_OUTPOST) {
    		level.setBlockState(pos, state.with(STRUCTURE, StructureStates.MANSION), 3);
    	}
    	else if(structure == StructureStates.MANSION) {
    		level.setBlockState(pos, state.with(STRUCTURE, StructureStates.JUNGLE_PYRAMID), 3);
    	}
    	else if(structure == StructureStates.JUNGLE_PYRAMID) {
    		level.setBlockState(pos, state.with(STRUCTURE, StructureStates.DESERT_PYRAMID), 3);
    	}
    	else if(structure == StructureStates.DESERT_PYRAMID) {
    		level.setBlockState(pos, state.with(STRUCTURE, StructureStates.STRONGHOLD), 3);
    	}
    	else if(structure == StructureStates.STRONGHOLD) {
    		level.setBlockState(pos, state.with(STRUCTURE, StructureStates.MONUMENT), 3);
    	}
    	else if(structure == StructureStates.MONUMENT) {
    		level.setBlockState(pos, state.with(STRUCTURE, StructureStates.FORTRESS), 3);
    	}
    	else if(structure == StructureStates.FORTRESS) {
    		level.setBlockState(pos, state.with(STRUCTURE, StructureStates.END_CITY), 3);
    	}
    	else if(structure == StructureStates.END_CITY) {
    		level.setBlockState(pos, state.with(STRUCTURE, StructureStates.BASTION), 3);
    	}
    	else if(structure == StructureStates.BASTION) {
    		level.setBlockState(pos, state.with(STRUCTURE, StructureStates.VILLAGE_PLAINS), 3);
    	}
    	else if(structure == StructureStates.VILLAGE_PLAINS) {
    		level.setBlockState(pos, state.with(STRUCTURE, StructureStates.VILLAGE_DESERT), 3);
    	}
    	else if(structure == StructureStates.VILLAGE_DESERT) {
    		level.setBlockState(pos, state.with(STRUCTURE, StructureStates.VILLAGE_SAVANNA), 3);
    	}
    	else if(structure == StructureStates.VILLAGE_SAVANNA) {
    		level.setBlockState(pos, state.with(STRUCTURE, StructureStates.VILLAGE_SNOWY), 3);
    	}
    	else if(structure == StructureStates.VILLAGE_SNOWY) {
    		level.setBlockState(pos, state.with(STRUCTURE, StructureStates.VILLAGE_TAIGA), 3);
    	}
    	else if(structure == StructureStates.VILLAGE_TAIGA) {
    		level.setBlockState(pos, state.with(STRUCTURE, StructureStates.PILLAGER_OUTPOST), 3);
    	}
    }
    
    @Nullable
    @Override
	public PrimedLTNT explode(World level, boolean exploded, double x, double y, double z, @Nullable LivingEntity igniter) throws NullPointerException {
		if(TNT != null) {
			PrimedLTNT tnt = TNT.get().create(level);
			tnt.setFuse(exploded && randomizedFuseUponExploded() ? tnt.getEffect().getDefaultFuse(tnt) / 8 + random.nextInt(MathHelper.clamp(tnt.getEffect().getDefaultFuse(tnt) / 4, 1, Integer.MAX_VALUE)) : tnt.getEffect().getDefaultFuse(tnt));
			tnt.setPosition(x + 0.5f, y, z + 0.5f);
			tnt.setOwner(igniter);
			if(level.getBlockState(new BlockPos(MathHelper.floor(x), MathHelper.floor(y), MathHelper.floor(z))).contains(STRUCTURE)) {
				NbtCompound tag = tnt.getPersistentData();
				tag.putString("structure", level.getBlockState(new BlockPos(MathHelper.floor(x), MathHelper.floor(y), MathHelper.floor(z))).get(STRUCTURE).asString());
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
}

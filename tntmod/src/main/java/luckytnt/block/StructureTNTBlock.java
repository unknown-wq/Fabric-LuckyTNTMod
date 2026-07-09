package luckytnt.block;

import org.jetbrains.annotations.Nullable;

import luckytnt.registry.EntityRegistry;
import luckytnt.registry.ItemRegistry;
import luckytnt.util.StructureStates;
import luckytntlib.block.LTNTBlock;
import luckytntlib.entity.PrimedLTNT;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundSource;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.stats.Stats;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.InteractionHand;
import net.minecraft.util.ItemActionResult;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;

public class StructureTNTBlock extends LTNTBlock {

	public static final EnumProperty<StructureStates> STRUCTURE = EnumProperty.of("structure", StructureStates.class);
	
    public StructureTNTBlock(BlockBehaviour.Properties properties) {
        super(properties, EntityRegistry.STRUCTURE_TNT, true);
    }

    @Override
    public void appendProperties(StateDefinition.Builder<Block, BlockState> definition) {
    	super.appendProperties(definition);
    	definition.add(STRUCTURE);
    }
    
    @Override
    public ItemActionResult onUseWithItem(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult result) {
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
    
    public void cycleThroughStructures(Level level, BlockState state, BlockPos pos) {
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
	public PrimedLTNT explode(Level level, boolean exploded, double x, double y, double z, @Nullable LivingEntity igniter) throws NullPointerException {
		if(TNT != null) {
			PrimedLTNT tnt = TNT.get().create(level);
			tnt.setFuse(exploded && randomizedFuseUponExploded() ? tnt.getEffect().getDefaultFuse(tnt) / 8 + random.nextInt(Mth.clamp(tnt.getEffect().getDefaultFuse(tnt) / 4, 1, Integer.MAX_VALUE)) : tnt.getEffect().getDefaultFuse(tnt));
			tnt.setPosition(x + 0.5f, y, z + 0.5f);
			tnt.setOwner(igniter);
			if(level.getBlockState(new BlockPos(Mth.floor(x), Mth.floor(y), Mth.floor(z))).contains(STRUCTURE)) {
				CompoundTag tag = tnt.getPersistentData();
				tag.putString("structure", level.getBlockState(new BlockPos(Mth.floor(x), Mth.floor(y), Mth.floor(z))).get(STRUCTURE).asString());
				tnt.setPersistentData(tag);
			}
			level.addFreshEntity(tnt);
			level.playSound(null, new BlockPos(Mth.floor(x), Mth.floor(y), Mth.floor(z)), SoundEvents.ENTITY_TNT_PRIMED, SoundSource.MASTER, 1, 1);
			if(level.getBlockState(new BlockPos(Mth.floor(x), Mth.floor(y), Mth.floor(z))).getBlock() == this) {
				level.setBlockState(new BlockPos(Mth.floor(x), Mth.floor(y), Mth.floor(z)), Blocks.AIR.getDefaultState(), 3);
			}
			return tnt;
		}
		throw new NullPointerException("No TNT entity present. Make sure it is registered before the block is registered");
	}
}

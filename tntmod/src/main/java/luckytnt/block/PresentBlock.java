package luckytnt.block;

import java.util.Collections;
import java.util.List;
import java.util.Random;

import luckytnt.registry.BlockRegistry;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.HorizontalFacingBlock;
import net.minecraft.entity.ExperienceOrbEntity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.loot.context.LootContextParameterSet;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.IntProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class PresentBlock extends Block {
	
	public static final DirectionProperty FACING = HorizontalFacingBlock.FACING;
	public static final IntProperty TYPE = IntProperty.of("type", 0, 4);
	
	public PresentBlock(AbstractBlock.Settings properties) {
		super(properties);
	}	

	@Override
    public void appendProperties(StateManager.Builder<Block, BlockState> definition) {
    	super.appendProperties(definition);
    	definition.add(FACING);
    	definition.add(TYPE);
    }
	
	@Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
    	return getDefaultState();
    }
	
	@Override
	public List<ItemStack> getDroppedStacks(BlockState state, LootContextParameterSet.Builder builder) {
		return Collections.singletonList(ItemStack.EMPTY);
	}
	
	@Override
	public BlockState onBreak(World level, BlockPos pos, BlockState state, PlayerEntity player) {
		if(!player.isCreative()) {
			Random random = new Random();
			Item item = Items.COAL;
			int itemCount = random.nextInt(6, 24);
			int xpCount = 0;
			int rand = new Random().nextInt(128);
			if(rand > 70 && rand <= 100) {
				item = Items.SNOWBALL;
				itemCount = random.nextInt(8,16);
				xpCount = random.nextInt(itemCount / 4, itemCount / 2 + 1);
			}
			else if(rand > 100 && rand <= 110) {
				item = BlockRegistry.SNOW_TNT.get().asItem();
				itemCount = 1;
				xpCount = random.nextInt(8, 12);
			}
			else if(rand > 110 && rand <= 119) {
				item = Items.DIAMOND;
				itemCount = random.nextInt(1, 4);
				xpCount = random.nextInt(8 * itemCount, 12 * itemCount);
			}
			else if(rand > 119 && rand <= 123) {
				item = BlockRegistry.CHRISTMAS_TNT.get().asItem();
				itemCount = 1;
				xpCount = random.nextInt(32, 48);
			}
			else if(rand > 123 && rand <= 125) {
				item = BlockRegistry.SNOWSTORM_TNT.get().asItem();
				itemCount = 1;
				xpCount = random.nextInt(48, 64);
			}
			else if(rand > 125 && rand <= 127) {
				item = Items.TOTEM_OF_UNDYING;
				itemCount = 1;
				xpCount = random.nextInt(64, 96);
			}
			ItemEntity itemEntity = new ItemEntity(level, pos.getX() + 0.5f, pos.getY(), pos.getZ() + 0.5f, new ItemStack(item, itemCount));
			level.spawnEntity(itemEntity);
			rand = random.nextInt(1, 6);
			for(int i = 0; i < rand; i++) {
				ExperienceOrbEntity xp = new ExperienceOrbEntity(level, pos.getX() + 0.5f, pos.getY(), pos.getZ() + 0.5f, xpCount / rand);
				level.spawnEntity(xp);
			}
			for(int i = 0; i < 15; i++) {
				level.addParticle(ParticleTypes.CLOUD, pos.getX() + 0.5f + Math.random() * 2 - 1f, pos.getY() + 0.5f + Math.random() * 2 - 1f, pos.getZ() + 0.5f + Math.random() * 2 - 1f, 0, 0, 0);
			}
		}
		return super.onBreak(level, pos, state, player);
	}
}

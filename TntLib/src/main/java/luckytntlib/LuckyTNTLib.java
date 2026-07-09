package luckytntlib;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import luckytntlib.block.LTNTBlock;
import luckytntlib.client.ClientAccess;
import luckytntlib.config.LuckyTNTLibConfigValues;
import luckytntlib.registry.EventRegistry;
import luckytntlib.registry.ItemGroupModification;
import luckytntlib.registry.ItemRegistry;
import luckytntlib.registry.NetworkRegistry;
import luckytntlib.registry.RegistryHelper;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.block.AbstractFireBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.CampfireBlock;
import net.minecraft.block.CandleBlock;
import net.minecraft.block.CandleCakeBlock;
import net.minecraft.block.DispenserBlock;
import net.minecraft.block.TntBlock;
import net.minecraft.block.dispenser.FallibleItemDispenserBehavior;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.property.Properties;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPointer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.event.GameEvent;

public class LuckyTNTLib implements ModInitializer {
    
	public static final String MODID = "luckytntlib";
	public static final Logger LOGGER = LoggerFactory.getLogger(MODID);
	public static final RegistryHelper RH = new RegistryHelper(MODID);
    
    @Override
	public void onInitialize() {
    	ItemRegistry.init();
    	ItemGroupModification.init();
    	NetworkRegistry.init();
    	
    	if(FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT) {
        	EventRegistry.init();
        	
    		RH.registerConfigScreenFactory(Text.literal("Lucky TNT Lib"), ClientAccess.getFactory());
    	}
    	
    	LuckyTNTLibConfigValues.registerConfig();
    	
    	changeFlintAndSteelDispenserBehavior();
    }
    
	private void changeFlintAndSteelDispenserBehavior() {
		DispenserBlock.registerBehavior(Items.FLINT_AND_STEEL, new FallibleItemDispenserBehavior() {

			@Override
			protected ItemStack dispenseSilently(BlockPointer pointer, ItemStack stack) {
				ServerWorld world = pointer.world();
				setSuccess(true);
				Direction direction = pointer.state().get(DispenserBlock.FACING);
				BlockPos blockPos = pointer.pos().offset(direction);
				BlockState blockState = world.getBlockState(blockPos);
				if (AbstractFireBlock.canPlaceAt(world, blockPos, direction)) {
					world.setBlockState(blockPos, AbstractFireBlock.getState(world, blockPos));
					world.emitGameEvent(null, GameEvent.BLOCK_PLACE, blockPos);
				} else if (CampfireBlock.canBeLit(blockState) || CandleBlock.canBeLit(blockState) || CandleCakeBlock.canBeLit(blockState)) {
					world.setBlockState(blockPos, (BlockState) blockState.with(Properties.LIT, true));
					world.emitGameEvent(null, GameEvent.BLOCK_CHANGE, blockPos);
				} else if (blockState.getBlock() instanceof TntBlock tnt) {
					if(tnt instanceof LTNTBlock ltnt) {
	    				ltnt.explode(world, false, blockPos.getX(), blockPos.getY(), blockPos.getZ(), null);
	    			} else {
	                	TntBlock.primeTnt(world, blockPos);
	    			}
					world.setBlockState(blockPos, Blocks.AIR.getDefaultState(), 3);
				} else {
					setSuccess(false);
				}
				if (this.isSuccess()) {
					stack.damage(1, world, null, (i) -> stack.setCount(0));
				}
				return stack;
			}
		});
	}
}

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
import luckytntlib.util.LTNTDataSerializers;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.dispenser.BlockSource;
import net.minecraft.core.dispenser.OptionalDispenseItemBehavior;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.BaseFireBlock;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CampfireBlock;
import net.minecraft.world.level.block.CandleBlock;
import net.minecraft.world.level.block.CandleCakeBlock;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.level.block.TntBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.gameevent.GameEvent;

public class LuckyTNTLib implements ModInitializer {

	public static final String MODID = "luckytntlib";
	public static final Logger LOGGER = LoggerFactory.getLogger(MODID);
	public static final RegistryHelper RH = new RegistryHelper(MODID);

	@Override
	public void onInitialize() {
		LTNTDataSerializers.register();

		ItemRegistry.init();
		ItemGroupModification.init();
		NetworkRegistry.init();

		if(FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT) {
			EventRegistry.init();

			RH.registerConfigScreenFactory(Component.literal("Lucky TNT Lib"), ClientAccess.getFactory());
		}

		LuckyTNTLibConfigValues.registerConfig();

		changeFlintAndSteelDispenserBehavior();
	}

	private void changeFlintAndSteelDispenserBehavior() {
		DispenserBlock.registerBehavior(Items.FLINT_AND_STEEL, new OptionalDispenseItemBehavior() {

			@Override
			protected ItemStack execute(BlockSource pointer, ItemStack stack) {
				ServerLevel world = pointer.level();
				setSuccess(true);
				Direction direction = pointer.state().getValue(DispenserBlock.FACING);
				BlockPos blockPos = pointer.pos().relative(direction);
				BlockState blockState = world.getBlockState(blockPos);
				if (BaseFireBlock.canBePlacedAt(world, blockPos, direction)) {
					world.setBlockAndUpdate(blockPos, BaseFireBlock.getState(world, blockPos));
					world.gameEvent((Entity)null, GameEvent.BLOCK_PLACE, blockPos);
				} else if (CampfireBlock.canLight(blockState) || CandleBlock.canLight(blockState) || CandleCakeBlock.canLight(blockState)) {
					world.setBlockAndUpdate(blockPos, (BlockState) blockState.setValue(BlockStateProperties.LIT, true));
					world.gameEvent((Entity)null, GameEvent.BLOCK_CHANGE, blockPos);
				} else if (blockState.getBlock() instanceof TntBlock tnt) {
					if(tnt instanceof LTNTBlock ltnt) {
						ltnt.explode(world, false, blockPos.getX(), blockPos.getY(), blockPos.getZ(), null);
					} else {
						TntBlock.prime(world, blockPos);
					}
					world.setBlock(blockPos, Blocks.AIR.defaultBlockState(), 3);
				} else {
					setSuccess(false);
				}
				if (this.isSuccess()) {
					stack.hurtAndBreak(1, world, null, (i) -> stack.setCount(0));
				}
				return stack;
			}
		});
	}
}

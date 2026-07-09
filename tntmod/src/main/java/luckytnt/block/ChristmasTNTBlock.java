package luckytnt.block;

import luckytnt.registry.EntityRegistry;
import luckytntlib.block.LTNTBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;

public class ChristmasTNTBlock extends LTNTBlock{

	public static final BooleanProperty ONLY_PRESENT = BooleanProperty.of("only_present");
	
	public ChristmasTNTBlock() {
		super(BlockBehaviour.Properties.create().mapColor(MapColor.RED).sounds(SoundType.GRASS), EntityRegistry.CHRISTMAS_TNT, false);
        setDefaultState(getDefaultState().setValue(ONLY_PRESENT, false));
	}
    
    @Override
    public void appendProperties(StateDefinition.Builder<Block, BlockState> definition) {
    	super.appendProperties(definition);
    	definition.add(ONLY_PRESENT);
    }
}

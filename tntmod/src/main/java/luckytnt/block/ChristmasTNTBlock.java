package luckytnt.block;

import luckytnt.registry.EntityRegistry;
import luckytntlib.block.LTNTBlock;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.MapColor;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;

public class ChristmasTNTBlock extends LTNTBlock{

	public static final BooleanProperty ONLY_PRESENT = BooleanProperty.of("only_present");
	
	public ChristmasTNTBlock() {
		super(AbstractBlock.Settings.create().mapColor(MapColor.RED).sounds(BlockSoundGroup.GRASS), EntityRegistry.CHRISTMAS_TNT, false);
        setDefaultState(getDefaultState().with(ONLY_PRESENT, false));
	}
    
    @Override
    public void appendProperties(StateManager.Builder<Block, BlockState> definition) {
    	super.appendProperties(definition);
    	definition.add(ONLY_PRESENT);
    }
}

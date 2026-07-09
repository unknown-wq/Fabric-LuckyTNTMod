package luckytntlib.client.gui;

import com.mojang.datafixers.util.Pair;

import luckytntlib.config.common.ConfigScreenFactory;
import luckytntlib.registry.RegistryHelper;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.GridWidget;
import net.minecraft.client.gui.widget.GridWidget.Adder;
import net.minecraft.client.gui.widget.ThreePartsLayoutWidget;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;

public class ConfigScreenListScreen extends Screen {
	
	ThreePartsLayoutWidget layout = new ThreePartsLayoutWidget(this, 20, 40);

	public ConfigScreenListScreen() {
		super(Text.empty());
	}

	@Override
	public void init() {
		GridWidget grid = new GridWidget();
		
		grid.getMainPositioner().marginX(4).marginBottom(4).alignHorizontalCenter();
		Adder rows = grid.createAdder(3);
		
		for(Pair<Text, ConfigScreenFactory> pair : RegistryHelper.configScreens) {
			Text name = pair.getFirst();
			ConfigScreenFactory factory = pair.getSecond();
			
			rows.add(new ButtonWidget.Builder(name, button -> openScreen(factory.apply())).width(100).build());
		}
		
		layout.addBody(grid);
		layout.addFooter(new ButtonWidget.Builder(ScreenTexts.DONE, button -> close()).width(100).build());
		layout.forEachChild(this::addDrawableChild);
		initTabNavigation();
	}
	
    @Override
    protected void initTabNavigation() {
        layout.refreshPositions();
    }
    
    protected void openScreen(Screen screen) {
    	client.setScreen(screen);
    }
	
	@Override
	public void close() {
		super.close();
	}
}

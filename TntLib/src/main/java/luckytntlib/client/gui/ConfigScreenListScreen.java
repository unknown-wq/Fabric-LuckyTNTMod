package luckytntlib.client.gui;

import com.mojang.datafixers.util.Pair;

import luckytntlib.config.common.ConfigScreenFactory;
import luckytntlib.registry.RegistryHelper;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.layouts.GridLayout;
import net.minecraft.client.gui.layouts.HeaderAndFooterLayout;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;

public class ConfigScreenListScreen extends Screen {

	HeaderAndFooterLayout layout = new HeaderAndFooterLayout(this, 20, 40);

	public ConfigScreenListScreen() {
		super(Component.empty());
	}

	@Override
	protected void init() {
		GridLayout grid = new GridLayout();

		grid.defaultCellSetting().paddingHorizontal(4).paddingBottom(4).alignHorizontallyCenter();
		GridLayout.RowHelper rows = grid.createRowHelper(3);

		for(Pair<Component, ConfigScreenFactory> pair : RegistryHelper.configScreens) {
			Component name = pair.getFirst();
			ConfigScreenFactory factory = pair.getSecond();

			rows.addChild(Button.builder(name, button -> openScreen(factory.apply())).width(100).build());
		}

		layout.addToContents(grid);
		layout.addToFooter(Button.builder(CommonComponents.GUI_DONE, button -> onClose()).width(100).build());
		layout.visitWidgets(this::addRenderableWidget);
		repositionElements();
	}

	@Override
	protected void repositionElements() {
		layout.arrangeElements();
	}

	protected void openScreen(Screen screen) {
		minecraft.gui.setScreen(screen);
	}

	@Override
	public void onClose() {
		super.onClose();
	}
}

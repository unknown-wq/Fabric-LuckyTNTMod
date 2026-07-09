package luckytntlib.client;

import luckytntlib.client.gui.ConfigScreen;
import luckytntlib.client.gui.ConfigScreenListScreen;
import luckytntlib.config.common.ConfigScreenFactory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;

public class ClientAccess {

	private static final ConfigScreenFactory screenFactory = new ConfigScreenFactory() {

		@Override
		public Screen apply() {
			return new ConfigScreen();
		}
	};

	public static void openConfigScreenListScreen() {
		Minecraft minecraft = Minecraft.getInstance();
		minecraft.gui.setScreen(new ConfigScreenListScreen());
	}

	public static ConfigScreenFactory getFactory() {
		return screenFactory;
	}
}

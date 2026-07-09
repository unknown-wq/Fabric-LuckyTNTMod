package luckytntlib.client.gui.widget;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.network.chat.Component;

/**
 * Renders a vertically center-aligned String in a Layout.
 * Is used in the config Screen.
 * <p>
 * In 26.2 {@link StringWidget} vertically centers its text within its own
 * height, so this widget simply forces a taller bounding box (matching the
 * height of the surrounding buttons) which reproduces the vertically centered
 * look the old manual pose translation provided.
 */
public class CenteredStringWidget extends StringWidget {

	public CenteredStringWidget(Component component, Font font) {
		super(font.width(component.getVisualOrderText()), 20, component, font);
	}
}

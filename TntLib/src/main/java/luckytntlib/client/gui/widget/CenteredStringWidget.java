package luckytntlib.client.gui.widget;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.TextWidget;
import net.minecraft.text.Text;

/**
 * Renders a vertically center-aligned String in a Layout.
 * Is used in the config Screen
 */
public class CenteredStringWidget extends TextWidget {

	public CenteredStringWidget(Text component, TextRenderer font) {
		super(component, font);
	}

	@Override
	public void renderWidget(DrawContext graphics, int i1, int i2, float f) {
		graphics.getMatrices().push();
		graphics.getMatrices().translate(0f, 6f, 0f);
		super.renderWidget(graphics, i1, i2, f);
		graphics.getMatrices().pop();
	}
}
package luckytntlib.client.gui.widget;

import java.text.DecimalFormat;

import net.minecraft.client.gui.widget.SliderWidget;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;

/**
 * Slider widget implementation which allows inputting values in a certain range
 * with optional step size.
 */
public class AdvancedSlider extends SliderWidget {

	protected Text prefix;
	protected Text suffix;

	protected double minValue;
	protected double maxValue;
	protected double originalCurrentValue;

	/** Allows input of discontinuous values with a certain step */
	protected double stepSize;

	protected boolean drawString;

	private final DecimalFormat format;

	/**
	 * @param x            x position of upper left corner
	 * @param y            y position of upper left corner
	 * @param width        Width of the widget
	 * @param height       Height of the widget
	 * @param prefix       {@link Text} displayed before the value string
	 * @param suffix       {@link Text} displayed after the value string
	 * @param minValue     Minimum (left) value of slider
	 * @param maxValue     Maximum (right) value of slider
	 * @param currentValue Starting value when widget is first displayed
	 * @param stepSize     Size of step used. Precision will automatically be
	 *                     calculated based on this value if this value is not 0.
	 * @param precision    Only used when {@code stepSize} is 0. Limited to a
	 *                     maximum of 4 (inclusive).
	 * @param drawString   Should text be displayed on the widget
	 */
	public AdvancedSlider(int x, int y, int width, int height, Text prefix, Text suffix, double minValue, double maxValue, double currentValue, double stepSize, int precision, boolean drawString) {
		super(x, y, width, height, Text.empty(), 0D);
		this.prefix = prefix;
		this.suffix = suffix;
		this.minValue = minValue;
		this.maxValue = maxValue;
		this.stepSize = Math.abs(stepSize);
		this.originalCurrentValue = currentValue;
		value = snapToNearest((currentValue - minValue) / (maxValue - minValue));
		this.drawString = drawString;

		if (stepSize == 0D) {
			precision = Math.min(precision, 4);

			StringBuilder builder = new StringBuilder("0");

			if (precision > 0) {
				builder.append('.');
			}

			while (precision-- > 0) {
				builder.append('0');
			}

			format = new DecimalFormat(builder.toString());
		} else if (MathHelper.approximatelyEquals(this.stepSize, Math.floor(this.stepSize))) {
			format = new DecimalFormat("0");
		} else {
			format = new DecimalFormat(Double.toString(this.stepSize).replaceAll("\\d", "0"));
		}

		updateMessage();
	}

	public AdvancedSlider(int x, int y, int width, int height, Text prefix, Text suffix, double minValue, double maxValue, double currentValue, boolean drawString) {
		this(x, y, width, height, prefix, suffix, minValue, maxValue, currentValue, 1D, 0, drawString);
	}

	public double getValue() {
		return snapToNearest(value) * (maxValue - minValue) + minValue;
	}

	public long getValueLong() {
		return Math.round(getValue());
	}

	public int getValueInt() {
		return (int) getValueLong();
	}

	public void setSliderValue(double val) {
		double d = value;
        value = snapToNearest((val - minValue) / (maxValue - minValue));
        if (d != value) {
            applyValue();
        }
        updateMessage();
	}

	public String getValueString() {
		return format.format(getValue());
	}

	public double getInitialValue() {
		return originalCurrentValue;
	}

	private double snapToNearest(double value) {
		if (stepSize <= 0D) {
			return MathHelper.clamp(value, 0D, 1D);
		}

		value = MathHelper.lerp(MathHelper.clamp(value, 0D, 1D), minValue, maxValue);

		value = (stepSize * Math.round(value / stepSize));

		if (minValue > maxValue) {
			value = MathHelper.clamp(value, maxValue, minValue);
		} else {
			value = MathHelper.clamp(value, minValue, maxValue);
		}

		return MathHelper.map(value, minValue, maxValue, 0D, 1D);
	}

	@Override
	protected void updateMessage() {
		if (drawString) {
			setMessage(Text.literal("").append(prefix).append(getValueString()).append(suffix));
		} else {
			setMessage(Text.empty());
		}
	}

	@Override
	protected void applyValue() {
	}
}

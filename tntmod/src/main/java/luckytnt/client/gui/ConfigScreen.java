package luckytnt.client.gui;

import java.util.ArrayList;
import java.util.List;

import luckytnt.LuckyTNTMod;
import luckytnt.config.LuckyTNTConfigValues;
import luckytnt.network.LuckyTNTUpdateConfigValuesPacket;
import luckytntlib.client.gui.widget.AdvancedSlider;
import luckytntlib.client.gui.widget.CenteredStringWidget;
import luckytntlib.config.common.Config;
import luckytntlib.config.common.Config.ConfigValue;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.widget.DirectionalLayoutWidget;
import net.minecraft.client.gui.widget.GridWidget;
import net.minecraft.client.gui.widget.GridWidget.Adder;
import net.minecraft.client.gui.widget.Positioner;
import net.minecraft.client.gui.widget.TextWidget;
import net.minecraft.client.gui.widget.ThreePartsLayoutWidget;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;

public class ConfigScreen extends Screen {
	
	int island_height_initial_value = 0;
	int drop_height_initial_value = 0;
	int maximum_disaster_time_initial_value = 0;
	double average_disaster_length_initial_value = 0d;
	boolean season_events_always_active_initial_value = false;
	boolean present_drop_destroy_blocks_initial_value = false;

	AdvancedSlider island_slider = null;
	AdvancedSlider dropped_slider = null;
	AdvancedSlider average_disaster_time_silder = null;
	AdvancedSlider average_disaster_strength_slider = null;
	
	Button season_events_always_active = null;
	Button render_contaminated_overlay = null;	
	Button present_drop_destroy = null;
	
	ThreePartsLayoutWidget layout = new ThreePartsLayoutWidget(this, 20, 40);
	
	public ConfigScreen() {
		super(Component.translatable("luckytntmod.config.title"));
	}
	
	@Override
	public void init() {
		DirectionalLayoutWidget linear = layout.addHeader(DirectionalLayoutWidget.vertical());
		linear.add(new TextWidget(title, textRenderer), Positioner::alignHorizontalCenter);
		
		GridWidget grid = new GridWidget();
		grid.getMainPositioner().marginX(4).marginBottom(4).alignHorizontalCenter();
		
		Adder rows = grid.createAdder(3);
		
		rows.add(island_slider = new AdvancedSlider(0, 0, 100, 20, Component.empty(), Component.empty(), 20, 160, LuckyTNTConfigValues.ISLAND_HEIGHT.get(), true));
		rows.add(new CenteredStringWidget(Component.translatable("luckytntmod.config.island_offset"), textRenderer));
		rows.add(new Button.Builder(Component.translatable("luckytntmod.config.reset"), Button -> resetIntValue(LuckyTNTConfigValues.ISLAND_HEIGHT, 50, island_slider)).width(100).build());
		island_height_initial_value = LuckyTNTConfigValues.ISLAND_HEIGHT.get();
		
		rows.add(dropped_slider = new AdvancedSlider(0, 0, 100, 20, Component.empty(), Component.empty(), 60, 400, LuckyTNTConfigValues.DROP_HEIGHT.get(), true));
		rows.add(new CenteredStringWidget(Component.translatable("luckytntmod.config.drop_offset"), textRenderer));
		rows.add(new Button.Builder(Component.translatable("luckytntmod.config.reset"), Button -> resetIntValue(LuckyTNTConfigValues.DROP_HEIGHT, 200, dropped_slider)).width(100).build());
		drop_height_initial_value = LuckyTNTConfigValues.DROP_HEIGHT.get();
		
		rows.add(average_disaster_time_silder = new AdvancedSlider(0, 0, 100, 20, Component.empty(), Component.empty(), 2, 24, LuckyTNTConfigValues.MAXIMUM_DISASTER_TIME.get(), true));
		rows.add(new CenteredStringWidget(Component.translatable("luckytntmod.config.maximum_time"), textRenderer));
		rows.add(new Button.Builder(Component.translatable("luckytntmod.config.reset"), Button -> resetIntValue(LuckyTNTConfigValues.MAXIMUM_DISASTER_TIME, 12, average_disaster_time_silder)).width(100).build());
		maximum_disaster_time_initial_value = LuckyTNTConfigValues.MAXIMUM_DISASTER_TIME.get();
		
		rows.add(average_disaster_strength_slider = new AdvancedSlider(0, 0, 100, 20, Component.empty(), Component.empty(), 1d, 10d, LuckyTNTConfigValues.AVERAGE_DIASTER_INTENSITY.get().doubleValue(), true));
		rows.add(new CenteredStringWidget(Component.translatable("luckytntmod.config.average_intensity"), textRenderer));
		rows.add(new Button.Builder(Component.translatable("luckytntmod.config.reset"), Button -> resetDoubleValue(LuckyTNTConfigValues.AVERAGE_DIASTER_INTENSITY, 1d, average_disaster_strength_slider)).width(100).build());
		average_disaster_length_initial_value = LuckyTNTConfigValues.AVERAGE_DIASTER_INTENSITY.get();
		
		rows.add(season_events_always_active = new Button.Builder(LuckyTNTConfigValues.SEASON_EVENTS_ALWAYS_ACTIVE.get().booleanValue() ? CommonComponents.ON : CommonComponents.OFF, Button -> nextBooleanValue(LuckyTNTConfigValues.SEASON_EVENTS_ALWAYS_ACTIVE, season_events_always_active)).width(100).build());
		rows.add(new CenteredStringWidget(Component.translatable("luckytntmod.config.event_always_active"), textRenderer));
		rows.add(new Button.Builder(Component.translatable("luckytntmod.config.reset"), Button -> resetBooleanValue(LuckyTNTConfigValues.SEASON_EVENTS_ALWAYS_ACTIVE, false, season_events_always_active)).width(100).build());
		season_events_always_active_initial_value = LuckyTNTConfigValues.SEASON_EVENTS_ALWAYS_ACTIVE.get();
		
		rows.add(render_contaminated_overlay = new Button.Builder(LuckyTNTConfigValues.RENDER_CONTAMINATED_OVERLAY.get().booleanValue() ? CommonComponents.ON : CommonComponents.OFF, Button -> nextBooleanValue(LuckyTNTConfigValues.RENDER_CONTAMINATED_OVERLAY, render_contaminated_overlay)).width(100).build());
		rows.add(new CenteredStringWidget(Component.translatable("luckytntmod.config.render_overlay"), textRenderer));
		rows.add(new Button.Builder(Component.translatable("luckytntmod.config.reset"), Button -> resetBooleanValue(LuckyTNTConfigValues.RENDER_CONTAMINATED_OVERLAY, true, render_contaminated_overlay)).width(100).build());
		
		rows.add(present_drop_destroy = new Button.Builder(LuckyTNTConfigValues.PRESENT_DROP_DESTROY_BLOCKS.get().booleanValue() ? CommonComponents.ON : CommonComponents.OFF, Button -> nextBooleanValue(LuckyTNTConfigValues.PRESENT_DROP_DESTROY_BLOCKS, present_drop_destroy)).width(100).build());
		rows.add(new CenteredStringWidget(Component.translatable("luckytntmod.config.present_drop"), textRenderer));
		rows.add(new Button.Builder(Component.translatable("luckytntmod.config.reset"), Button -> resetBooleanValue(LuckyTNTConfigValues.PRESENT_DROP_DESTROY_BLOCKS, true, present_drop_destroy)).width(100).build());
		present_drop_destroy_blocks_initial_value = LuckyTNTConfigValues.PRESENT_DROP_DESTROY_BLOCKS.get();
		
		Button deactivated = new Button.Builder(Component.translatable("luckytntmod.config.back"), Button -> deactivatedButtonAction()).width(100).build();
		Button done = new Button.Builder(CommonComponents.DONE, Button -> close()).width(100).build();
		Button next = new Button.Builder(Component.translatable("luckytntmod.config.next"), Button -> nextPage()).width(100).build();
		
		deactivated.active = false;
		
		GridWidget grid2 = new GridWidget();
		grid2.getMainPositioner().marginX(20).marginBottom(4).alignHorizontalCenter();
		
		Adder rows2 = grid2.createAdder(3);
		
		rows2.add(deactivated);
		rows2.add(done);
		rows2.add(next);
		
		layout.addBody(grid);
		layout.addFooter(grid2);
		layout.forEachChild(this::addDrawableChild);
		initTabNavigation();
	}
	
	@Override
    public void initTabNavigation() {
        layout.refreshPositions();
    }
	
	@Override
	public void close() {
		if(island_slider != null) {
			LuckyTNTConfigValues.ISLAND_HEIGHT.set(island_slider.getValueInt());
		}
		if(dropped_slider != null) {
			LuckyTNTConfigValues.DROP_HEIGHT.set(dropped_slider.getValueInt());
		}
		if(average_disaster_time_silder != null) {
			LuckyTNTConfigValues.MAXIMUM_DISASTER_TIME.set(average_disaster_time_silder.getValueInt());
		}
		if(average_disaster_strength_slider != null) {
			LuckyTNTConfigValues.AVERAGE_DIASTER_INTENSITY.set(average_disaster_strength_slider.getValue());
		}
		
		List<ConfigValue<?>> values = new ArrayList<>();
		if(LuckyTNTConfigValues.ISLAND_HEIGHT.get() != island_height_initial_value) {
			values.add(LuckyTNTConfigValues.ISLAND_HEIGHT);
		}
		if(LuckyTNTConfigValues.DROP_HEIGHT.get() != drop_height_initial_value) {
			values.add(LuckyTNTConfigValues.DROP_HEIGHT);
		}
		if(LuckyTNTConfigValues.MAXIMUM_DISASTER_TIME.get() != maximum_disaster_time_initial_value) {
			values.add(LuckyTNTConfigValues.MAXIMUM_DISASTER_TIME);
		}
		if(LuckyTNTConfigValues.AVERAGE_DIASTER_INTENSITY.get() != average_disaster_length_initial_value) {
			values.add(LuckyTNTConfigValues.AVERAGE_DIASTER_INTENSITY);
		}
		if(LuckyTNTConfigValues.SEASON_EVENTS_ALWAYS_ACTIVE.get() != season_events_always_active_initial_value) {
			values.add(LuckyTNTConfigValues.SEASON_EVENTS_ALWAYS_ACTIVE);
		}
		if(LuckyTNTConfigValues.PRESENT_DROP_DESTROY_BLOCKS.get() != present_drop_destroy_blocks_initial_value) {
			values.add(LuckyTNTConfigValues.PRESENT_DROP_DESTROY_BLOCKS);
		}
		if(!values.isEmpty()) {
			LuckyTNTMod.RH.sendC2SPacket(new LuckyTNTUpdateConfigValuesPacket(values));
		}
		
		LuckyTNTConfigValues.CLIENT_CONFIG.save(client.world);
		
		super.close();
	}
	
	public static void deactivatedButtonAction() {
	}
	
	public void nextPage() {
		close();
		Minecraft.getInstance().setScreen(new ConfigScreen2());
	}
	
	public void resetIntValue(Config.IntValue config, int newValue, AdvancedSlider slider) {
		config.set(newValue);
		slider.setSliderValue(newValue);
	}
	
	public void resetDoubleValue(Config.DoubleValue config, double newValue, AdvancedSlider slider) {
		config.set(newValue);
		slider.setSliderValue(newValue);
	}
	
	public void nextBooleanValue(Config.BooleanValue config, Button Button) {
		boolean value = config.get().booleanValue();
		if(value) {
			value = false;
		} else {
			value = true;
		}
		config.set(value);
		Button.setMessage(value ? CommonComponents.ON : CommonComponents.OFF);
	}
	
	public void resetBooleanValue(Config.BooleanValue config, boolean defaultValue, Button Button) {
		config.set(defaultValue);
		Button.setMessage(defaultValue ? CommonComponents.ON : CommonComponents.OFF);
	}
}

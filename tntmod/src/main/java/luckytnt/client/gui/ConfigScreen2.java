package luckytnt.client.gui;

import static luckytnt.client.gui.ConfigScreen.deactivatedButtonAction;

import java.util.ArrayList;
import java.util.List;

import luckytnt.LuckyTNTMod;
import luckytnt.config.LuckyTNTConfigValues;
import luckytnt.network.LuckyTNTUpdateConfigValuesPacket;
import luckytnt.util.CustomTNTConfig;
import luckytntlib.client.gui.widget.AdvancedSlider;
import luckytntlib.client.gui.widget.CenteredStringWidget;
import luckytntlib.config.common.Config;
import luckytntlib.config.common.Config.ConfigValue;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.DirectionalLayoutWidget;
import net.minecraft.client.gui.widget.GridWidget;
import net.minecraft.client.gui.widget.GridWidget.Adder;
import net.minecraft.client.gui.widget.Positioner;
import net.minecraft.client.gui.widget.TextWidget;
import net.minecraft.client.gui.widget.ThreePartsLayoutWidget;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;

public class ConfigScreen2 extends Screen {
	
	CustomTNTConfig custom_tnt_first_explosion_initial_value = CustomTNTConfig.NO_EXPLOSION;
	int custom_tnt_first_explosion_intensity_initial_value = 0;
	CustomTNTConfig custom_tnt_second_explosion_initial_value = CustomTNTConfig.NO_EXPLOSION;
	int custom_tnt_second_explosion_intensity_initial_value = 0;
	CustomTNTConfig custom_tnt_third_explosion_initial_value = CustomTNTConfig.NO_EXPLOSION;
	int custom_tnt_third_explosion_intensity_initial_value = 0;
	
	ButtonWidget custom_tnt_first_explosion = null;
	AdvancedSlider custom_tnt_first_explosion_intensity = null;
	ButtonWidget custom_tnt_second_explosion = null;
	AdvancedSlider custom_tnt_second_explosion_intensity = null;
	ButtonWidget custom_tnt_third_explosion = null;
	AdvancedSlider custom_tnt_third_explosion_intensity = null;
	
	ThreePartsLayoutWidget layout = new ThreePartsLayoutWidget(this, 20, 40);

	public ConfigScreen2() {
		super(Text.translatable("luckytntmod.config.title"));
	}
	
	@Override
	public void init() {
		DirectionalLayoutWidget linear = layout.addHeader(DirectionalLayoutWidget.vertical());
		linear.add(new TextWidget(title, textRenderer), Positioner::alignHorizontalCenter);
		
		ButtonWidget empty = new ButtonWidget.Builder(Text.empty(), button -> deactivatedButtonAction()).size(100, 15).build();
		
		empty.active = false;
		empty.visible = false;
		empty.setAlpha(0f);
		
		GridWidget grid = new GridWidget();
		grid.getMainPositioner().marginX(4).marginBottom(1).alignHorizontalCenter();
		
		Adder rows = grid.createAdder(3);
		
		rows.add(new CenteredStringWidget(Text.translatable("luckytntmod.config.custom_tnt"), textRenderer));
		rows.add(empty);
		rows.add(new CenteredStringWidget(Text.literal(""), textRenderer));
		
		rows.add(new CenteredStringWidget(Text.translatable("luckytntmod.config.first_tnt"), textRenderer));
		rows.add(empty);
		rows.add(new CenteredStringWidget(Text.literal(""), textRenderer));
		
		rows.add(custom_tnt_first_explosion = new ButtonWidget.Builder(LuckyTNTConfigValues.CUSTOM_TNT_FIRST_EXPLOSION.get().getComponent(), button -> nextExplosionValue(LuckyTNTConfigValues.CUSTOM_TNT_FIRST_EXPLOSION, custom_tnt_first_explosion)).width(100).build());
		rows.add(new CenteredStringWidget(Text.translatable("luckytntmod.config.first_type"), textRenderer));
		rows.add(new ButtonWidget.Builder(Text.translatable("luckytntmod.config.reset"), button -> resetExplosion(LuckyTNTConfigValues.CUSTOM_TNT_FIRST_EXPLOSION, custom_tnt_first_explosion)).width(100).build());
		custom_tnt_first_explosion_initial_value = LuckyTNTConfigValues.CUSTOM_TNT_FIRST_EXPLOSION.get();
		
		rows.add(custom_tnt_first_explosion_intensity = new AdvancedSlider(0, 0, 100, 20, Text.empty(), Text.empty(), 1, 20, LuckyTNTConfigValues.CUSTOM_TNT_FIRST_EXPLOSION_INTENSITY.get().intValue(), true));
		rows.add(new CenteredStringWidget(Text.translatable("luckytntmod.config.first_intensity"), textRenderer));
		rows.add(new ButtonWidget.Builder(Text.translatable("luckytntmod.config.reset"), button -> resetIntValue(LuckyTNTConfigValues.CUSTOM_TNT_FIRST_EXPLOSION_INTENSITY, 1, custom_tnt_first_explosion_intensity)).width(100).build());
		custom_tnt_first_explosion_intensity_initial_value = LuckyTNTConfigValues.CUSTOM_TNT_FIRST_EXPLOSION_INTENSITY.get().intValue();
		
		rows.add(new CenteredStringWidget(Text.translatable("luckytntmod.config.second_tnt"), textRenderer));
		rows.add(empty);
		rows.add(new CenteredStringWidget(Text.literal(""), textRenderer));
		
		rows.add(custom_tnt_second_explosion = new ButtonWidget.Builder(LuckyTNTConfigValues.CUSTOM_TNT_SECOND_EXPLOSION.get().getComponent(), button -> nextExplosionValue(LuckyTNTConfigValues.CUSTOM_TNT_SECOND_EXPLOSION, custom_tnt_second_explosion)).width(100).build());
		rows.add(new CenteredStringWidget(Text.translatable("luckytntmod.config.second_type"), textRenderer));
		rows.add(new ButtonWidget.Builder(Text.translatable("luckytntmod.config.reset"), button -> resetExplosion(LuckyTNTConfigValues.CUSTOM_TNT_SECOND_EXPLOSION, custom_tnt_second_explosion)).width(100).build());
		custom_tnt_second_explosion_initial_value = LuckyTNTConfigValues.CUSTOM_TNT_SECOND_EXPLOSION.get();
		
		rows.add(custom_tnt_second_explosion_intensity = new AdvancedSlider(0, 0, 100, 20, Text.empty(), Text.empty(), 1, 20, LuckyTNTConfigValues.CUSTOM_TNT_SECOND_EXPLOSION_INTENSITY.get().intValue(), true));
		rows.add(new CenteredStringWidget(Text.translatable("luckytntmod.config.second_intensity"), textRenderer));
		rows.add(new ButtonWidget.Builder(Text.translatable("luckytntmod.config.reset"), button -> resetIntValue(LuckyTNTConfigValues.CUSTOM_TNT_SECOND_EXPLOSION_INTENSITY, 1, custom_tnt_second_explosion_intensity)).width(100).build());
		custom_tnt_second_explosion_intensity_initial_value = LuckyTNTConfigValues.CUSTOM_TNT_SECOND_EXPLOSION_INTENSITY.get().intValue();
		
		rows.add(new CenteredStringWidget(Text.translatable("luckytntmod.config.third_tnt"), textRenderer));
		rows.add(empty);
		rows.add(new CenteredStringWidget(Text.literal(""), textRenderer));
		
		rows.add(custom_tnt_third_explosion = new ButtonWidget.Builder(LuckyTNTConfigValues.CUSTOM_TNT_THIRD_EXPLOSION.get().getComponent(), button -> nextExplosionValue(LuckyTNTConfigValues.CUSTOM_TNT_THIRD_EXPLOSION, custom_tnt_third_explosion)).width(100).build());
		rows.add(new CenteredStringWidget(Text.translatable("luckytntmod.config.third_type"), textRenderer));
		rows.add(new ButtonWidget.Builder(Text.translatable("luckytntmod.config.reset"), button -> resetExplosion(LuckyTNTConfigValues.CUSTOM_TNT_THIRD_EXPLOSION, custom_tnt_third_explosion)).width(100).build());
		custom_tnt_third_explosion_initial_value = LuckyTNTConfigValues.CUSTOM_TNT_THIRD_EXPLOSION.get();
		
		rows.add(custom_tnt_third_explosion_intensity = new AdvancedSlider(0, 0, 100, 20, Text.empty(), Text.empty(), 1, 20, LuckyTNTConfigValues.CUSTOM_TNT_THIRD_EXPLOSION_INTENSITY.get().intValue(), true));
		rows.add(new CenteredStringWidget(Text.translatable("luckytntmod.config.third_intensity"), textRenderer));
		rows.add(new ButtonWidget.Builder(Text.translatable("luckytntmod.config.reset"), button -> resetIntValue(LuckyTNTConfigValues.CUSTOM_TNT_THIRD_EXPLOSION_INTENSITY, 1, custom_tnt_third_explosion_intensity)).width(100).build());
		custom_tnt_third_explosion_intensity_initial_value = LuckyTNTConfigValues.CUSTOM_TNT_THIRD_EXPLOSION_INTENSITY.get().intValue();
		
		ButtonWidget back = new ButtonWidget.Builder(Text.translatable("luckytntmod.config.back"), button -> lastPage()).width(100).build();
		ButtonWidget done = new ButtonWidget.Builder(ScreenTexts.DONE, button -> close()).width(100).build();
		ButtonWidget deactivated = new ButtonWidget.Builder(Text.translatable("luckytntmod.config.next"), button -> deactivatedButtonAction()).width(100).build();
		
		deactivated.active = false;
		
		GridWidget grid2 = new GridWidget();
		grid2.getMainPositioner().marginX(20).marginBottom(4).alignHorizontalCenter();
		
		Adder rows2 = grid2.createAdder(3);
		
		rows2.add(back);
		rows2.add(done);
		rows2.add(deactivated);
		
		layout.addBody(grid);
		layout.addFooter(grid2);
		layout.forEachChild(this::addDrawableChild);
		initTabNavigation();
	}
	
	@Override
    public void initTabNavigation() {
        layout.refreshPositions();;
	}
	
	@Override
	public void close() {
		if(custom_tnt_first_explosion_intensity != null) {
			LuckyTNTConfigValues.CUSTOM_TNT_FIRST_EXPLOSION_INTENSITY.set(custom_tnt_first_explosion_intensity.getValueInt());
		}
		if(custom_tnt_second_explosion_intensity != null) {
			LuckyTNTConfigValues.CUSTOM_TNT_SECOND_EXPLOSION_INTENSITY.set(custom_tnt_second_explosion_intensity.getValueInt());
		}
		if(custom_tnt_third_explosion_intensity != null) {
			LuckyTNTConfigValues.CUSTOM_TNT_THIRD_EXPLOSION_INTENSITY.set(custom_tnt_third_explosion_intensity.getValueInt());
		}
		
		List<ConfigValue<?>> values = new ArrayList<>();
		if(LuckyTNTConfigValues.CUSTOM_TNT_FIRST_EXPLOSION.get() != custom_tnt_first_explosion_initial_value) {
			values.add(LuckyTNTConfigValues.CUSTOM_TNT_FIRST_EXPLOSION);
		}
		if(LuckyTNTConfigValues.CUSTOM_TNT_SECOND_EXPLOSION.get() != custom_tnt_second_explosion_initial_value) {
			values.add(LuckyTNTConfigValues.CUSTOM_TNT_SECOND_EXPLOSION);
		}
		if(LuckyTNTConfigValues.CUSTOM_TNT_THIRD_EXPLOSION.get() != custom_tnt_third_explosion_initial_value) {
			values.add(LuckyTNTConfigValues.CUSTOM_TNT_THIRD_EXPLOSION);
		}
		if(LuckyTNTConfigValues.CUSTOM_TNT_FIRST_EXPLOSION_INTENSITY.get().intValue() != custom_tnt_first_explosion_intensity_initial_value) {
			values.add(LuckyTNTConfigValues.CUSTOM_TNT_FIRST_EXPLOSION_INTENSITY);
		}
		if(LuckyTNTConfigValues.CUSTOM_TNT_SECOND_EXPLOSION_INTENSITY.get().intValue() != custom_tnt_second_explosion_intensity_initial_value) {
			values.add(LuckyTNTConfigValues.CUSTOM_TNT_SECOND_EXPLOSION_INTENSITY);
		}
		if(LuckyTNTConfigValues.CUSTOM_TNT_THIRD_EXPLOSION_INTENSITY.get().intValue() != custom_tnt_third_explosion_intensity_initial_value) {
			values.add(LuckyTNTConfigValues.CUSTOM_TNT_THIRD_EXPLOSION_INTENSITY);
		}
		if(!values.isEmpty()) {
			LuckyTNTMod.RH.sendC2SPacket(new LuckyTNTUpdateConfigValuesPacket(values));
		}
		
		super.close();
	}
	
	public void lastPage() {
		close();
		MinecraftClient.getInstance().setScreen(new ConfigScreen());
	}
	
	public void resetIntValue(Config.IntValue config, int newValue, AdvancedSlider slider) {
		config.set(newValue);
		slider.setSliderValue(newValue);
	}
	
	public void nextExplosionValue(Config.EnumValue<CustomTNTConfig> config, ButtonWidget button) {
		CustomTNTConfig value = config.get();
		if(value == CustomTNTConfig.NO_EXPLOSION) {
			value = CustomTNTConfig.NORMAL_EXPLOSION;
		}
		else if(value == CustomTNTConfig.NORMAL_EXPLOSION) {
			value = CustomTNTConfig.SPHERICAL_EXPLOSION;
		}
		else if(value == CustomTNTConfig.SPHERICAL_EXPLOSION) {
			value = CustomTNTConfig.CUBICAL_EXPLOSION;
		}
		else if(value == CustomTNTConfig.CUBICAL_EXPLOSION) {
			value = CustomTNTConfig.EASTER_EGG;
		}
		else if(value == CustomTNTConfig.EASTER_EGG) {
			value = CustomTNTConfig.FIREWORK;
		}
		else if(value == CustomTNTConfig.FIREWORK) {
			value = CustomTNTConfig.NO_EXPLOSION;
		}
		config.set(value);
		button.setMessage(config.get().getComponent());
	}
	
	public void resetExplosion(Config.EnumValue<CustomTNTConfig> config, ButtonWidget button) {
		config.set(CustomTNTConfig.NO_EXPLOSION);
		button.setMessage(config.get().getComponent());
	}
}

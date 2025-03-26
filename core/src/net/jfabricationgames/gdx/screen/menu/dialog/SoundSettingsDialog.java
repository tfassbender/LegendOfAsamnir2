package net.jfabricationgames.gdx.screen.menu.dialog;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.utils.Align;

import net.jfabricationgames.gdx.music.BackgroundMusicManager;
import net.jfabricationgames.gdx.screen.menu.components.FocusButton;
import net.jfabricationgames.gdx.screen.menu.components.FocusButton.FocusButtonBuilder;
import net.jfabricationgames.gdx.screen.menu.components.FocusCheckbox;
import net.jfabricationgames.gdx.screen.menu.components.MenuBox;
import net.jfabricationgames.gdx.sound.SoundManager;

public class SoundSettingsDialog extends InGameMenuDialog {
	
	private FocusCheckbox checkboxSoundEnabled;
	private FocusButton buttonSoundMusicVolumeUp;
	private FocusButton buttonSoundMusicVolumeDown;
	private FocusButton buttonSoundEffectVolumeUp;
	private FocusButton buttonSoundEffectVolumeDown;
	
	private BackgroundMusicManager backgroundMusicManager;
	private SoundManager soundManager;
	
	public SoundSettingsDialog(OrthographicCamera camera) {
		super(camera);
		
		backgroundMusicManager = BackgroundMusicManager.getInstance();
		soundManager = SoundManager.getInstance();
		
		createControls();
	}
	
	private void createControls() {
		background = new MenuBox(10, 6, MenuBox.TextureType.YELLOW_PAPER);
		banner = new MenuBox(7, 2, MenuBox.TextureType.BIG_BANNER);
		buttonBackToMenu = new FocusButtonBuilder() //
				.setNinePatchConfig(FocusButton.BUTTON_YELLOW_NINEPATCH_CONFIG) //
				.setNinePatchConfigFocused(FocusButton.BUTTON_YELLOW_NINEPATCH_CONFIG_FOCUSED) //
				.setPosition(785, 480) //
				.setSize(110, 40) //
				.build();
		buttonBackToMenu.scaleBy(FocusButton.DEFAULT_BUTTON_SCALE);
		buttonBackToMenu.setFocused(true);
		
		checkboxSoundEnabled = new FocusCheckbox.FocusCheckboxBuilder() //
				.setNinePatchConfig(FocusCheckbox.BUTTON_YELLOW_NINEPATCH_CONFIG) //
				.setNinePatchConfigFocused(FocusCheckbox.BUTTON_YELLOW_NINEPATCH_CONFIG_FOCUSED) //
				.setPosition(690, 366) //
				.setSize(28) //
				.setSelectionSupplier(() -> backgroundMusicManager.isSoundEnabled()) // 
				.setOnSelectionChangeConsumer(b -> {}) // will be done in the pause menu
				.build();
		checkboxSoundEnabled.scaleBy(FocusCheckbox.DEFAULT_CHECKBOX_SCALE);
		
		buttonSoundMusicVolumeUp = new FocusButtonBuilder() //
				.setNinePatchConfig(FocusButton.BUTTON_YELLOW_NINEPATCH_CONFIG) //
				.setNinePatchConfigFocused(FocusButton.BUTTON_YELLOW_NINEPATCH_CONFIG_FOCUSED) //
				.setPosition(850, 296) //
				.setSize(28, 28) //
				.build();
		buttonSoundMusicVolumeUp.scaleBy(FocusButton.DEFAULT_BUTTON_SCALE);
		
		buttonSoundMusicVolumeDown = new FocusButtonBuilder() //
				.setNinePatchConfig(FocusButton.BUTTON_YELLOW_NINEPATCH_CONFIG) //
				.setNinePatchConfigFocused(FocusButton.BUTTON_YELLOW_NINEPATCH_CONFIG_FOCUSED) //
				.setPosition(690, 296) //
				.setSize(28, 28) //
				.build();
		buttonSoundMusicVolumeDown.scaleBy(FocusButton.DEFAULT_BUTTON_SCALE);
		
		buttonSoundEffectVolumeUp = new FocusButtonBuilder() //
				.setNinePatchConfig(FocusButton.BUTTON_YELLOW_NINEPATCH_CONFIG) //
				.setNinePatchConfigFocused(FocusButton.BUTTON_YELLOW_NINEPATCH_CONFIG_FOCUSED) //
				.setPosition(850, 226) //
				.setSize(28, 28) //
				.build();
		buttonSoundEffectVolumeUp.scaleBy(FocusButton.DEFAULT_BUTTON_SCALE);
		
		buttonSoundEffectVolumeDown = new FocusButtonBuilder() //
				.setNinePatchConfig(FocusButton.BUTTON_YELLOW_NINEPATCH_CONFIG) //
				.setNinePatchConfigFocused(FocusButton.BUTTON_YELLOW_NINEPATCH_CONFIG_FOCUSED) //
				.setPosition(690, 226) //
				.setSize(28, 28) //
				.build();
		buttonSoundEffectVolumeDown.scaleBy(FocusButton.DEFAULT_BUTTON_SCALE);
	}
	
	public void draw() {
		if (visible) {
			batch.setProjectionMatrix(camera.combined);
			batch.begin();
			
			background.draw(batch, 150, 150, 850, 450);
			banner.draw(batch, 200, 410, 550, 200);
			buttonBackToMenu.draw(batch);
			buttonSoundMusicVolumeUp.draw(batch);
			buttonSoundMusicVolumeDown.draw(batch);
			buttonSoundEffectVolumeUp.draw(batch);
			buttonSoundEffectVolumeDown.draw(batch);
			
			// checkboxes need to be drawn in a separate batch
			batch.flush();
			checkboxSoundEnabled.draw(batch);
			batch.flush();
			
			drawText();
			
			batch.end();
		}
	}
	
	private void drawText() {
		screenTextWriter.setBatchProjectionMatrix(camera.combined);
		
		screenTextWriter.setColor(Color.BLACK);
		screenTextWriter.setScale(1.2f);
		screenTextWriter.drawText("Sound Settings", 275, 525);
		
		screenTextWriter.setScale(0.8f);
		screenTextWriter.drawText(getButtonTextColorEncoding(buttonBackToMenu) + "Back", 820, 523);
		
		screenTextWriter.setScale(1f);
		screenTextWriter.setColor(Color.BLACK);
		screenTextWriter.drawText("Sound enabled", 280, 400);
		screenTextWriter.drawText("Music volume", 280, 330);
		screenTextWriter.drawText("Effect volume", 280, 260);
		
		screenTextWriter.setScale(1f);
		screenTextWriter.drawText("+", 862, 333);
		screenTextWriter.drawText("-", 702, 333);
		screenTextWriter.drawText("+", 862, 263);
		screenTextWriter.drawText("-", 702, 263);
		
		screenTextWriter.setScale(0.8f);
		screenTextWriter.drawText((int) (backgroundMusicManager.getMusicVolume() * 100) + "%", 730, 330, 120, Align.center, false);
		screenTextWriter.drawText((int) (soundManager.getSoundVolume() * 100) + "%", 730, 260, 120, Align.center, false);
	}
	
	public void setFocusTo(String stateName) {
		unfocusAll();
		
		FocusButton button = null;
		FocusCheckbox checkbox = null;
		switch (stateName) {
			case "soundsettings_dialog_button_back":
				button = buttonBackToMenu;
				break;
			case "soundsettings_dialog_checkbox_sound_enabled":
				checkbox = checkboxSoundEnabled;
				break;
			case "soundsettings_dialog_button_music_volume_up":
				button = buttonSoundMusicVolumeUp;
				break;
			case "soundsettings_dialog_button_music_volume_down":
				button = buttonSoundMusicVolumeDown;
				break;
			case "soundsettings_dialog_button_effect_volume_up":
				button = buttonSoundEffectVolumeUp;
				break;
			case "soundsettings_dialog_button_effect_volume_down":
				button = buttonSoundEffectVolumeDown;
				break;
			default:
				throw new IllegalStateException("Unexpected button state identifier: " + stateName);
		}
		
		if (button != null) {
			button.setFocused(true);
		}
		if (checkbox != null) {
			checkbox.setFocused(true);
		}
	}
	
	public void unfocusAll() {
		buttonBackToMenu.setFocused(false);
		checkboxSoundEnabled.setFocused(false);
		buttonSoundMusicVolumeUp.setFocused(false);
		buttonSoundMusicVolumeDown.setFocused(false);
		buttonSoundEffectVolumeUp.setFocused(false);
		buttonSoundEffectVolumeDown.setFocused(false);
	}
}

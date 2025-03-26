package net.jfabricationgames.gdx.screen.menu.dialog;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.Align;

import net.jfabricationgames.gdx.screen.menu.components.FocusButton;
import net.jfabricationgames.gdx.screen.menu.components.FocusButton.FocusButtonBuilder;
import net.jfabricationgames.gdx.screen.menu.components.MenuBox;
import net.jfabricationgames.gdx.skill.Difficulty;

public class SettingsDialog extends InGameMenuDialog {
	
	private ShapeRenderer shapeRenderer;
	
	private FocusButton buttonControls;
	private FocusButton buttonItemSettings;
	private FocusButton buttonSoundSettings;
	
	private MenuBox backgroundDifficulty;
	private MenuBox bannerDifficulty;
	private FocusButton buttonDifficultyEasy;
	private FocusButton buttonDifficultyNormal;
	private FocusButton buttonDifficultyHard;
	
	public SettingsDialog(OrthographicCamera camera) {
		super(camera);
		shapeRenderer = new ShapeRenderer();
		createControls();
	}
	
	private void createControls() {
		background = new MenuBox(12, 8, MenuBox.TextureType.YELLOW_PAPER);
		backgroundDifficulty = new MenuBox(6, 6, MenuBox.TextureType.YELLOW_PAPER);
		banner = new MenuBox(6, 2, MenuBox.TextureType.BIG_BANNER);
		bannerDifficulty = new MenuBox(4, 2, MenuBox.TextureType.BIG_BANNER_LOW);
		buttonBackToMenu = new FocusButtonBuilder() //
				.setNinePatchConfig(FocusButton.BUTTON_YELLOW_NINEPATCH_CONFIG) //
				.setNinePatchConfigFocused(FocusButton.BUTTON_YELLOW_NINEPATCH_CONFIG_FOCUSED) //
				.setPosition(935, 670) //
				.setSize(110, 40) //
				.build();
		buttonBackToMenu.scaleBy(FocusButton.DEFAULT_BUTTON_SCALE);
		buttonBackToMenu.setFocused(true);
		
		buttonControls = new FocusButtonBuilder() //
				.setNinePatchConfig(FocusButton.BUTTON_YELLOW_NINEPATCH_CONFIG) //
				.setNinePatchConfigFocused(FocusButton.BUTTON_YELLOW_NINEPATCH_CONFIG_FOCUSED) //
				.setPosition(100, 500) //
				.setSize(250, 50) //
				.build();
		buttonControls.scaleBy(FocusButton.DEFAULT_BUTTON_SCALE);
		
		buttonItemSettings = new FocusButtonBuilder() //
				.setNinePatchConfig(FocusButton.BUTTON_YELLOW_NINEPATCH_CONFIG) //
				.setNinePatchConfigFocused(FocusButton.BUTTON_YELLOW_NINEPATCH_CONFIG_FOCUSED) //
				.setPosition(100, 410) //
				.setSize(250, 50) //
				.build();
		buttonItemSettings.scaleBy(FocusButton.DEFAULT_BUTTON_SCALE);
		
		buttonSoundSettings = new FocusButtonBuilder() //
				.setNinePatchConfig(FocusButton.BUTTON_YELLOW_NINEPATCH_CONFIG) //
				.setNinePatchConfigFocused(FocusButton.BUTTON_YELLOW_NINEPATCH_CONFIG_FOCUSED) //
				.setPosition(100, 320) //
				.setSize(250, 50) //
				.build();
		buttonSoundSettings.scaleBy(FocusButton.DEFAULT_BUTTON_SCALE);
		
		buttonDifficultyEasy = new FocusButtonBuilder() //
				.setNinePatchConfig(FocusButton.BUTTON_YELLOW_NINEPATCH_CONFIG) //
				.setNinePatchConfigFocused(FocusButton.BUTTON_YELLOW_NINEPATCH_CONFIG_FOCUSED) //
				.setPosition(600, 430) //
				.setSize(310, 50) //
				.build();
		buttonDifficultyEasy.scaleBy(FocusButton.DEFAULT_BUTTON_SCALE);
		
		buttonDifficultyNormal = new FocusButtonBuilder() //
				.setNinePatchConfig(FocusButton.BUTTON_YELLOW_NINEPATCH_CONFIG) //
				.setNinePatchConfigFocused(FocusButton.BUTTON_YELLOW_NINEPATCH_CONFIG_FOCUSED) //
				.setPosition(600, 300) //
				.setSize(310, 80) //
				.build();
		buttonDifficultyNormal.scaleBy(FocusButton.DEFAULT_BUTTON_SCALE);
		
		buttonDifficultyHard = new FocusButtonBuilder() //
				.setNinePatchConfig(FocusButton.BUTTON_YELLOW_NINEPATCH_CONFIG) //
				.setNinePatchConfigFocused(FocusButton.BUTTON_YELLOW_NINEPATCH_CONFIG_FOCUSED) //
				.setPosition(600, 170) //
				.setSize(310, 80) //
				.build();
		buttonDifficultyHard.scaleBy(FocusButton.DEFAULT_BUTTON_SCALE);
	}
	
	public void draw() {
		if (visible) {
			batch.setProjectionMatrix(camera.combined);
			batch.begin();
			
			background.draw(batch, 20, -20, 1150, 800);
			backgroundDifficulty.draw(batch, 500, 100, 650, 530);
			banner.draw(batch, 80, 595, 400, 200);
			bannerDifficulty.draw(batch, 635, 475, 380, 170);
			buttonBackToMenu.draw(batch);
			
			buttonControls.draw(batch);
			buttonItemSettings.draw(batch);
			buttonSoundSettings.draw(batch);
			
			buttonDifficultyEasy.draw(batch);
			buttonDifficultyNormal.draw(batch);
			buttonDifficultyHard.draw(batch);
			
			drawText();
			drawCurrentDifficultyIndicators();
			
			batch.end();
		}
	}
	
	private void drawText() {
		screenTextWriter.setBatchProjectionMatrix(camera.combined);
		
		screenTextWriter.setColor(Color.BLACK);
		screenTextWriter.setScale(1.2f);
		screenTextWriter.drawText("Settings", 165, 711);
		
		screenTextWriter.setScale(0.8f);
		screenTextWriter.drawText(getButtonTextColorEncoding(buttonBackToMenu) + "Back", 970, 713);
		
		screenTextWriter.setScale(1f);
		screenTextWriter.drawText(getButtonTextColorEncoding(buttonControls) + "Controls", 120, 550, 340, Align.center, false);
		screenTextWriter.drawText(getButtonTextColorEncoding(buttonItemSettings) + "Items", 120, 460, 340, Align.center, false);
		screenTextWriter.drawText(getButtonTextColorEncoding(buttonSoundSettings) + "Sound", 120, 370, 340, Align.center, false);
		
		screenTextWriter.drawText("Difficulty", 710, 572);
		
		screenTextWriter.setScale(0.8f);
		screenTextWriter.drawText(getButtonTextColorEncoding(buttonDifficultyEasy) + "I'm a warrior! (Easy)", 625, 480, 420, Align.center, false);
		screenTextWriter.drawText(getButtonTextColorEncoding(buttonDifficultyNormal) + "I'll drink with the\nEinherjar! (Normal)", 625, 390, 420, Align.center, false);
		screenTextWriter.drawText(getButtonTextColorEncoding(buttonDifficultyHard) + "The allfather knows\nmy name! (Hard)", 625, 260, 420, Align.center, false);
	}
	
	private void drawCurrentDifficultyIndicators() {
		switch (Difficulty.getDifficultyLevel()) {
			case EASY:
				drawDifficultyIndicator(570, 450, true);
				drawDifficultyIndicator(1095, 450, false);
				break;
			case NORMAL:
				drawDifficultyIndicator(570, 340, true);
				drawDifficultyIndicator(1095, 340, false);
				break;
			case HARD:
				drawDifficultyIndicator(570, 210, true);
				drawDifficultyIndicator(1095, 210, false);
				break;
		}
	}
	
	private void drawDifficultyIndicator(int x, int y, boolean leftToRight) {
		shapeRenderer.setProjectionMatrix(camera.combined);
		shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
		shapeRenderer.setColor(Color.BLACK);
		
		if (leftToRight) {
			shapeRenderer.triangle(x, y, x + 20, y + 20, x, y + 40);
		}
		else {
			shapeRenderer.triangle(x, y, x - 20, y + 20, x, y + 40);
		}
		
		shapeRenderer.end();
	}
	
	public void setFocusTo(String stateName) {
		unfocusAll();
		
		FocusButton button = null;
		switch (stateName) {
			case "settingsDialog_button_back":
				button = buttonBackToMenu;
				break;
			case "settingsDialog_button_controls":
				button = buttonControls;
				break;
			case "settingsDialog_button_item_settings":
				button = buttonItemSettings;
				break;
			case "settingsDialog_button_sound_settings":
				button = buttonSoundSettings;
				break;
			case "settingsDialog_button_difficultyEasy":
				button = buttonDifficultyEasy;
				break;
			case "settingsDialog_button_difficultyNormal":
				button = buttonDifficultyNormal;
				break;
			case "settingsDialog_button_difficultyHard":
				button = buttonDifficultyHard;
				break;
			default:
				throw new IllegalStateException("Unexpected button state identifier: " + stateName);
		}
		if (button != null) {
			button.setFocused(true);
		}
	}
	
	private void unfocusAll() {
		buttonBackToMenu.setFocused(false);
		buttonControls.setFocused(false);
		buttonItemSettings.setFocused(false);
		buttonSoundSettings.setFocused(false);
		buttonDifficultyEasy.setFocused(false);
		buttonDifficultyNormal.setFocused(false);
		buttonDifficultyHard.setFocused(false);
	}
	
	@Override
	public void dispose() {
		super.dispose();
		shapeRenderer.dispose();
	}
}

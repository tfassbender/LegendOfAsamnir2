package net.jfabricationgames.gdx.screen.menu.dialog;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.utils.Align;

import net.jfabricationgames.gdx.screen.menu.components.FocusButton;
import net.jfabricationgames.gdx.screen.menu.components.FocusButton.FocusButtonBuilder;
import net.jfabricationgames.gdx.screen.menu.components.MenuBox;

public class SaveGameErrorDialog extends InGameMenuDialog {
	
	public SaveGameErrorDialog(OrthographicCamera camera) {
		super(camera);
		
		createControls();
	}
	
	private void createControls() {
		background = new MenuBox(6, 4, MenuBox.TextureType.YELLOW_PAPER);
		banner = new MenuBox(6, 2, MenuBox.TextureType.BIG_BANNER);
		
		buttonBackToMenu = new FocusButtonBuilder() //
				.setNinePatchConfig(FocusButton.BUTTON_YELLOW_NINEPATCH_CONFIG) //
				.setNinePatchConfigFocused(FocusButton.BUTTON_YELLOW_NINEPATCH_CONFIG_FOCUSED) //
				.setPosition(560, 258) //
				.setSize(60, 40) //
				.build();
		buttonBackToMenu.scaleBy(FocusButton.DEFAULT_BUTTON_SCALE);
	}
	
	public void draw() {
		if (visible) {
			batch.setProjectionMatrix(camera.combined);
			batch.begin();
			
			background.draw(batch, 300, 200, 600, 300);
			banner.draw(batch, 160, 390, 900, 200);
			buttonBackToMenu.draw(batch);
			
			drawText();
			
			batch.end();
		}
	}
	
	private void drawText() {
		screenTextWriter.setBatchProjectionMatrix(camera.combined);
		
		screenTextWriter.setColor(Color.BLACK);
		screenTextWriter.setScale(1.2f);
		screenTextWriter.drawText("Cannot save right now", 275, 506);
		
		screenTextWriter.setScale(0.8f);
		screenTextWriter.drawText(getButtonTextColorEncoding(buttonBackToMenu) + "Ok", 583, 302);
		
		screenTextWriter.setScale(0.75f);
		screenTextWriter.setColor(Color.BLACK);
		screenTextWriter.drawText("The game cannot be saved during cutscenes or bossfights.", 380, 430, 450, Align.left, true);
	}
	
	public void setFocusTo(String stateName) {
		unfocusAll();
		
		FocusButton button = null;
		switch (stateName) {
			case "save_game_error_dialog_button_ok":
				button = buttonBackToMenu;
				break;
			default:
				throw new IllegalStateException("Unexpected button state identifier: " + stateName);
		}
		
		if (button != null) {
			button.setFocused(true);
		}
	}
	
	public void unfocusAll() {
		buttonBackToMenu.setFocused(false);
	}
}

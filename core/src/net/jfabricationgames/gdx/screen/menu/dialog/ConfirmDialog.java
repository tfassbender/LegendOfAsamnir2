package net.jfabricationgames.gdx.screen.menu.dialog;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.utils.Align;

import net.jfabricationgames.gdx.screen.menu.components.FocusButton;
import net.jfabricationgames.gdx.screen.menu.components.FocusButton.FocusButtonBuilder;
import net.jfabricationgames.gdx.screen.menu.components.MenuBox;

public class ConfirmDialog extends InGameMenuDialog {
	
	private FocusButton buttonConfirm;
	// the backToMenu button is used as cancel button
	
	public ConfirmDialog(OrthographicCamera camera) {
		super(camera);
		
		createControls();
	}
	
	private void createControls() {
		background = new MenuBox(6, 4, MenuBox.TextureType.YELLOW_PAPER);
		banner = new MenuBox(6, 2, MenuBox.TextureType.BIG_BANNER);
		
		buttonConfirm = new FocusButtonBuilder() //
				.setNinePatchConfig(FocusButton.BUTTON_YELLOW_NINEPATCH_CONFIG) //
				.setNinePatchConfigFocused(FocusButton.BUTTON_YELLOW_NINEPATCH_CONFIG_FOCUSED) //
				.setPosition(370, 168) //
				.setSize(150, 40) //
				.build();
		buttonConfirm.scaleBy(FocusButton.DEFAULT_BUTTON_SCALE);
		
		buttonBackToMenu = new FocusButtonBuilder() //
				.setNinePatchConfig(FocusButton.BUTTON_YELLOW_NINEPATCH_CONFIG) //
				.setNinePatchConfigFocused(FocusButton.BUTTON_YELLOW_NINEPATCH_CONFIG_FOCUSED) //
				.setPosition(630, 168) //
				.setSize(150, 40) //
				.build();
		buttonBackToMenu.scaleBy(FocusButton.DEFAULT_BUTTON_SCALE);
	}
	
	public void draw() {
		if (visible) {
			batch.setProjectionMatrix(camera.combined);
			batch.begin();
			
			background.draw(batch, 300, 100, 600, 400);
			banner.draw(batch, 160, 340, 900, 200);
			buttonBackToMenu.draw(batch);
			buttonConfirm.draw(batch);
			
			drawText();
			
			batch.end();
		}
	}
	
	private void drawText() {
		screenTextWriter.setBatchProjectionMatrix(camera.combined);
		
		screenTextWriter.setColor(Color.BLACK);
		screenTextWriter.setScale(1.2f);
		screenTextWriter.drawText("Overwrite Saved Game?", 275, 456);
		
		screenTextWriter.setScale(0.8f);
		screenTextWriter.drawText(getButtonTextColorEncoding(buttonConfirm) + "Confirm", 405, 210);
		screenTextWriter.drawText(getButtonTextColorEncoding(buttonBackToMenu) + "Cancel", 675, 210);
		
		screenTextWriter.setScale(0.75f);
		screenTextWriter.setColor(Color.BLACK);
		screenTextWriter.drawText("Are you sure that you want to overwrite the existing game with the current game?", 380, 380, 500, Align.left, true);
	}
	
	public void setFocusTo(String stateName) {
		unfocusAll();
		
		FocusButton button = null;
		switch (stateName) {
			case "confirm_dialog_button_confirm":
				button = buttonConfirm;
				break;
			case "confirm_dialog_button_cancel":
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
		buttonConfirm.setFocused(false);
	}
}

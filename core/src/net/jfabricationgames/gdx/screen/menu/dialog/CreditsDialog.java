package net.jfabricationgames.gdx.screen.menu.dialog;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.utils.Align;

import net.jfabricationgames.gdx.screen.menu.components.FocusButton;
import net.jfabricationgames.gdx.screen.menu.components.FocusButton.FocusButtonBuilder;
import net.jfabricationgames.gdx.screen.menu.components.MenuBox;

public class CreditsDialog extends InGameMenuDialog {
	
	public CreditsDialog(OrthographicCamera camera) {
		super(camera);
		
		createControls();
	}
	
	private void createControls() {
		background = new MenuBox(12, 8, MenuBox.TextureType.YELLOW_PAPER);
		banner = new MenuBox(4, 2, MenuBox.TextureType.BIG_BANNER);
		buttonBackToMenu = new FocusButtonBuilder() //
				.setNinePatchConfig(FocusButton.BUTTON_YELLOW_NINEPATCH_CONFIG) //
				.setNinePatchConfigFocused(FocusButton.BUTTON_YELLOW_NINEPATCH_CONFIG_FOCUSED) //
				.setPosition(935, 590) //
				.setSize(110, 40) //
				.build();
		buttonBackToMenu.scaleBy(FocusButton.DEFAULT_BUTTON_SCALE);
		buttonBackToMenu.setFocused(true);
	}
	
	public void draw() {
		if (visible) {
			batch.setProjectionMatrix(camera.combined);
			batch.begin();
			
			background.draw(batch, 40, -60, 1130, 755);
			banner.draw(batch, -30, 510, 360, 200);
			buttonBackToMenu.draw(batch);
			
			drawText();
			
			batch.end();
		}
	}
	
	private void drawText() {
		screenTextWriter.setBatchProjectionMatrix(camera.combined);
		
		screenTextWriter.setColor(Color.BLACK);
		screenTextWriter.setScale(1.2f);
		screenTextWriter.drawText("Credits", 50, 625);
		
		screenTextWriter.setScale(0.8f);
		screenTextWriter.drawText(getButtonTextColorEncoding(buttonBackToMenu) + "Back", 970, 633);
		
		screenTextWriter.setColor(Color.RED);
		screenTextWriter.setScale(1.4f);
		screenTextWriter.drawText("Programming", 350, 630);
		screenTextWriter.drawText("Story", 475, 490);
		screenTextWriter.drawText("Graphics", 425, 360);
		screenTextWriter.drawText("Engine", 460, 100);
		
		screenTextWriter.setColor(Color.BLACK);
		screenTextWriter.setScale(0.9f);
		screenTextWriter.drawText("Tobias Fassbender", 390, 565);
		screenTextWriter.drawText("Tobias Fassbender", 390, 430);
		screenTextWriter.drawText("LibGDX (libgdx.com)", 380, 40);
		
		screenTextWriter.setScale(0.8f);
		screenTextWriter.drawText("[#FF0000]Characters[#000000] - Elthen's Pixel Art Shop (elthen.itch.io)", 120, 290);
		screenTextWriter.drawText("[#FF0000]UI[#000000] - Pixel Frog (pixelfrog-assets.itch.io)", 200, 240);
		screenTextWriter.drawText("[#FF0000]Tilesets[#000000] - Pipoya (pipoya.itch.io) and Elthen's Pixel Art Shop (elthen.itch.io)", 200, 190, 800, Align.center, true);
	}
}

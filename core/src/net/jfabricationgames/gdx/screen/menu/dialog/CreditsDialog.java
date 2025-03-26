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
				.setPosition(935, 730) //
				.setSize(110, 40) //
				.build();
		buttonBackToMenu.scaleBy(FocusButton.DEFAULT_BUTTON_SCALE);
		buttonBackToMenu.setFocused(true);
	}
	
	public void draw() {
		if (visible) {
			batch.setProjectionMatrix(camera.combined);
			batch.begin();
			
			background.draw(batch, 40, -60, 1130, 895);
			banner.draw(batch, -30, 650, 360, 200);
			buttonBackToMenu.draw(batch);
			
			drawText();
			
			batch.end();
		}
	}
	
	private void drawText() {
		screenTextWriter.setBatchProjectionMatrix(camera.combined);
		
		screenTextWriter.setColor(Color.BLACK);
		screenTextWriter.setScale(1.2f);
		screenTextWriter.drawText("Credits", 50, 765);
		
		screenTextWriter.setScale(0.8f);
		screenTextWriter.drawText(getButtonTextColorEncoding(buttonBackToMenu) + "Back", 970, 773);
		
		screenTextWriter.setColor(Color.RED);
		screenTextWriter.setScale(1.4f);
		screenTextWriter.drawText("Programming", 350, 770);
		screenTextWriter.drawText("Story", 475, 630);
		screenTextWriter.drawText("Graphics", 425, 500);
		screenTextWriter.drawText("Sound", 465, 240);
		screenTextWriter.drawText("Engine", 460, 100);
		
		screenTextWriter.setColor(Color.BLACK);
		screenTextWriter.setScale(0.9f);
		screenTextWriter.drawText("Tobias Fassbender", 390, 705);
		screenTextWriter.drawText("Tobias Fassbender", 390, 570);
		screenTextWriter.drawText("LibGDX (libgdx.com)", 380, 40);
		
		screenTextWriter.setScale(0.8f);
		screenTextWriter.drawText("[#FF0000]Characters[#000000] - Elthen's Pixel Art Shop (elthen.itch.io)", 120, 430);
		screenTextWriter.drawText("[#FF0000]UI[#000000] - Pixel Frog (pixelfrog-assets.itch.io)", 200, 380);
		screenTextWriter.drawText("[#FF0000]Tilesets[#000000] - Pipoya (pipoya.itch.io) and Elthen's Pixel Art Shop (elthen.itch.io)", 200, 330, 800, Align.center, true);
		screenTextWriter.drawText("[#FF0000]Background Music[#000000] - Tim Beek (timbeek.com)", 200, 165);
	}
}

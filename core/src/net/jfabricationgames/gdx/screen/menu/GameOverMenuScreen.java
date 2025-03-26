package net.jfabricationgames.gdx.screen.menu;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.utils.Align;

import net.jfabricationgames.gdx.screen.menu.components.FocusButton;
import net.jfabricationgames.gdx.screen.menu.components.FocusButton.FocusButtonBuilder;
import net.jfabricationgames.gdx.screen.menu.components.MenuBox;

public class GameOverMenuScreen extends InGameMenuScreen<GameOverMenuScreen> {
	
	private static final String INPUT_CONTEXT_NAME = "simpleMenu";
	private static final String GAME_OVER_MENU_STATE_CONFIG = "config/menu/game_over_menu_states.json";
	
	private MenuBox background;
	private MenuBox banner;
	private FocusButton buttonStartNewGame;
	private FocusButton buttonBackToSvartalfheim;
	private FocusButton buttonQuit;
	
	public GameOverMenuScreen(MenuGameScreen gameScreen) {
		super(gameScreen, GAME_OVER_MENU_STATE_CONFIG);
		initialize();
	}
	
	private void initialize() {
		createComponents();
		
		stateMachine.changeToInitialState();
	}
	
	private void createComponents() {
		background = new MenuBox(12, 8, MenuBox.TextureType.GREEN_BOARD);
		banner = new MenuBox(6, 2, MenuBox.TextureType.BIG_BANNER);
		
		int buttonWidth = 490;
		int buttonHeight = 55;
		int buttonPosX = 230;
		int lowestButtonY = 175;
		int buttonGapY = 40;
		buttonStartNewGame = new FocusButtonBuilder().setNinePatchConfig(FocusButton.BUTTON_GREEN_NINEPATCH_CONFIG).setNinePatchConfigFocused(FocusButton.BUTTON_GREEN_NINEPATCH_CONFIG_FOCUSED).setSize(buttonWidth, buttonHeight).setPosition(buttonPosX, lowestButtonY + 2f * (buttonHeight + buttonGapY)).build();
		buttonBackToSvartalfheim = new FocusButtonBuilder().setNinePatchConfig(FocusButton.BUTTON_GREEN_NINEPATCH_CONFIG).setNinePatchConfigFocused(FocusButton.BUTTON_GREEN_NINEPATCH_CONFIG_FOCUSED).setSize(buttonWidth, buttonHeight).setPosition(buttonPosX, lowestButtonY + 1f * (buttonHeight + buttonGapY)).build();
		buttonQuit = new FocusButtonBuilder().setNinePatchConfig(FocusButton.BUTTON_GREEN_NINEPATCH_CONFIG).setNinePatchConfigFocused(FocusButton.BUTTON_GREEN_NINEPATCH_CONFIG_FOCUSED).setSize(buttonWidth, buttonHeight).setPosition(buttonPosX, lowestButtonY).build();
		
		buttonStartNewGame.scaleBy(FocusButton.DEFAULT_BUTTON_SCALE);
		buttonBackToSvartalfheim.scaleBy(FocusButton.DEFAULT_BUTTON_SCALE);
		buttonQuit.scaleBy(FocusButton.DEFAULT_BUTTON_SCALE);
	}
	
	@Override
	public void showMenu() {
		super.showMenu();
		
		takeGameSnapshot();
	}
	
	@Override
	protected String getInputContextName() {
		return INPUT_CONTEXT_NAME;
	}
	
	@Override
	public void render(float delta) {
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		setProjectionMatrixBeforeRendering();
		
		batch.begin();
		drawBackground();
		drawButtons();
		drawBanners();
		batch.end();
		
		drawTexts();
	}
	
	private void drawBackground() {
		gameSnapshotSprite.draw(batch);
		background.draw(batch, 150, 125, 880, 500);
	}
	
	private void drawButtons() {
		buttonStartNewGame.draw(batch);
		buttonBackToSvartalfheim.draw(batch);
		buttonQuit.draw(batch);
	}
	
	private void drawBanners() {
		banner.draw(batch, 160, 440, 850, 350);
	}
	
	private void drawTexts() {
		screenTextWriter.setColor(Color.BLACK);
		screenTextWriter.setScale(2f);
		screenTextWriter.drawText("Game Over", 330, 645);
		
		int buttonTextX = 380;
		int buttonTextWidth = 430;
		screenTextWriter.setScale(1.5f);
		screenTextWriter.drawText("You died...", buttonTextX + 7, 525, buttonTextWidth, Align.center, false);
		
		screenTextWriter.setScale(1.15f);
		screenTextWriter.drawText(getButtonTextColorEncoding(buttonStartNewGame) + "Die again", buttonTextX, 423, buttonTextWidth, Align.center, false);
		screenTextWriter.drawText(getButtonTextColorEncoding(buttonBackToSvartalfheim) + "Back to Svartalfheim", buttonTextX, 327, buttonTextWidth, Align.center, false);
		screenTextWriter.drawText(getButtonTextColorEncoding(buttonQuit) + "Quit", buttonTextX, 231, buttonTextWidth, Align.center, false);
	}
	
	private String getButtonTextColorEncoding(FocusButton button) {
		return button.hasFocus() ? TEXT_COLOR_ENCODING_FOCUS : TEXT_COLOR_ENCODING_NORMAL;
	}
	
	@Override
	public void setFocusTo(String stateName, String leavingState) {
		unfocusAll();
		switch (stateName) {
			case "button_restartGame":
				buttonStartNewGame.setFocused(true);
				break;
			case "button_back_to_svartalfheim":
				buttonBackToSvartalfheim.setFocused(true);
				break;
			case "button_quit":
				buttonQuit.setFocused(true);
				break;
		}
	}
	
	private void unfocusAll() {
		buttonStartNewGame.setFocused(false);
		buttonBackToSvartalfheim.setFocused(false);
		buttonQuit.setFocused(false);
	}
}

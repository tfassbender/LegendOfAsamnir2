package net.jfabricationgames.gdx.screen;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;

import net.jfabricationgames.gdx.character.player.Player;
import net.jfabricationgames.gdx.music.BackgroundMusicManager;
import net.jfabricationgames.gdx.screen.menu.MainMenuScreen;

public class ScreenManager {
	
	public static final String ASSET_GROUP_NAME = "game";
	public static final String INPUT_CONTEXT_NAME = "game";
	
	private static final String MENU_BACKGROUND_MUSIC_NAME = "main_menu";
	
	private static ScreenManager instance;
	
	public static synchronized ScreenManager getInstance() {
		if (instance == null) {
			instance = new ScreenManager();
		}
		return instance;
	}
	
	private Game game;
	private Screen gameScreen;
	
	private ScreenManager() {}
	
	public void changeToGameScreen() {
		if (gameScreen == null) {
			throw new IllegalStateException("backToGameScreen was called, but the gameScreen was not yet set.");
		}
		
		// reload the attack handler because the difficulty could have been changed
		Player.getInstance().beforeChangeToGameScreen();
		
		setScreen(gameScreen);
	}
	
	public void changeToMainMenuScreen() {
		changeToMainMenuScreen(false);
	}
	public void changeToMainMenuScreen(boolean showCredits) {
		MainMenuScreen screen = new MainMenuScreen();
		setScreen(screen);
		if (showCredits) {
			screen.showCreditsDialog();
		}
		
		playMenuBackgroundMusic();
	}
	
	private void playMenuBackgroundMusic() {
		BackgroundMusicManager.getInstance().play(MENU_BACKGROUND_MUSIC_NAME);
	}
	
	public void setGame(Game game) {
		this.game = game;
	}
	
	public void setScreen(Screen screen) {
		Gdx.app.debug(getClass().getSimpleName(), "Changing screen to: " + screen);
		game.setScreen(screen);
	}
	
	public void setGameScreen(Screen gameScreen) {
		this.gameScreen = gameScreen;
	}
}

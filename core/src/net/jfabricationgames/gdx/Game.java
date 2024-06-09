package net.jfabricationgames.gdx;

import java.io.IOException;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;

import net.jfabricationgames.cdi.CdiContainer;
import net.jfabricationgames.cdi.exception.CdiException;
import net.jfabricationgames.gdx.assets.AssetGroupManager;
import net.jfabricationgames.gdx.data.GameDataService;
import net.jfabricationgames.gdx.event.global.GlobalEventListener;
import net.jfabricationgames.gdx.input.InputManager;
import net.jfabricationgames.gdx.screen.ScreenManager;
import net.jfabricationgames.gdx.screen.game.GameScreen;
import net.jfabricationgames.gdx.sound.SoundManager;
import net.jfabricationgames.gdx.text.FontManager;

public class Game extends com.badlogic.gdx.Game {
	
	public static final String ASSET_GROUP_MANAGER_CONFIG_PATH = "config/assets/asset_groups.json";
	public static final String INPUT_PROFILE_CONFIG_PATH = "config/input/profile.xml";
	public static final String SOUND_CONFIG_PATH = "config/sound/sound_sets.json";
	public static final String FONT_CONFIG_PATH = "config/font/fonts.json";
	
	private static Game instance;
	
	private Runnable preGameConfigurator;
	
	public static synchronized Game createInstance(Runnable preGameConfigurator) {
		if (instance == null) {
			instance = new Game(preGameConfigurator);
		}
		return instance;
	}
	
	public static Game getInstance() {
		return instance;
	}
	
	private Game(Runnable preGameConfigurator) {
		this.preGameConfigurator = preGameConfigurator;
	}
	
	@Override
	public void create() {
		preGameConfigurator.run();
		
		initializeCdiContainer();
		
		AssetGroupManager.initialize(ASSET_GROUP_MANAGER_CONFIG_PATH);
		SoundManager.getInstance().loadConfig(SOUND_CONFIG_PATH);
		FontManager.getInstance().loadConfig(FONT_CONFIG_PATH);
		GameDataService.initializeEventListener();
		GlobalEventListener.createGlobalEventListener();
		
		InputMultiplexer multiplexer = new InputMultiplexer();
		Gdx.input.setInputProcessor(multiplexer);
		InputManager.getInstance().createInputProfile(Gdx.files.internal(INPUT_PROFILE_CONFIG_PATH), multiplexer);
		
		ScreenManager screenManager = ScreenManager.getInstance();
		screenManager.setGame(this);
		screenManager.changeToMainMenuScreen();
	}
	
	private void initializeCdiContainer() {
		try {
			CdiContainer.create("net.jfabricationgames.gdx");
		}
		catch (CdiException | IOException e) {
			Gdx.app.error(Game.class.getSimpleName(), "Error while creating the CDI container", e);
		}
	}
	
	public GameScreen getGameScreen() {
		if (screen instanceof GameScreen) {
			return (GameScreen) screen;
		}
		return null;
	}
	
	@Override
	public void dispose() {
		super.dispose();
		AssetGroupManager.getInstance().dispose();
		SoundManager.getInstance().dispose();
		FontManager.getInstance().dispose();
		CdiContainer.destroy();
	}
}

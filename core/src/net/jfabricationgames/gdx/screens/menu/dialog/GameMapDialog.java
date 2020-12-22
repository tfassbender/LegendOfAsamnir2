package net.jfabricationgames.gdx.screens.menu.dialog;

import java.util.function.Consumer;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;

import net.jfabricationgames.gdx.character.container.data.CharacterFastTravelProperties;
import net.jfabricationgames.gdx.cutscene.CutsceneHandler;
import net.jfabricationgames.gdx.event.EventConfig;
import net.jfabricationgames.gdx.event.EventHandler;
import net.jfabricationgames.gdx.event.EventType;
import net.jfabricationgames.gdx.screens.game.GameScreen;
import net.jfabricationgames.gdx.screens.menu.InGameMenuScreen;
import net.jfabricationgames.gdx.screens.menu.components.FocusButton;
import net.jfabricationgames.gdx.screens.menu.components.FocusButton.FocusButtonBuilder;
import net.jfabricationgames.gdx.screens.menu.components.MenuBox;
import net.jfabricationgames.gdx.screens.menu.config.MapConfig;
import net.jfabricationgames.gdx.screens.menu.control.ControlledMenu;
import net.jfabricationgames.gdx.texture.TextureLoader;

public class GameMapDialog extends InGameMenuDialog {
	
	private static final float PLAYER_POSITION_POINTER_BLINK_DELAY = 0.75f;
	private static final Color COLOR_FAST_TRAVEL_POINT_DISABLED = Color.DARK_GRAY;
	private static final Color COLOR_FAST_TRAVEL_POINT_ENABLED = new Color(0.93f, 0.48f, 0f, 1f);
	private static final Color COLOR_FAST_TRAVEL_POINT_SELECTED = new Color(0.78f, 0.27f, 0.11f, 1f);
	
	private float playerPositionPointerBlinkTimer = 0;
	private boolean playerPositionPointerBlinkOn = true;
	
	private ShapeRenderer shapeRenderer;
	private TextureRegion mapTexture;
	
	private MapConfig config;
	private Vector2 playersRelativePositionOnMap;
	
	private Array<CharacterFastTravelProperties> fastTravelPoints;
	private String selectedFastTravelPointId;
	
	private float mapWidth;
	private float mapHeight;
	
	private Runnable backToGameCallback;
	private Consumer<String> playMenuSoundConsumer;
	
	public GameMapDialog(GameScreen gameScreen, Runnable backToGameCallback, Consumer<String> playMenuSoundConsumer) {
		this.backToGameCallback = backToGameCallback;
		this.playMenuSoundConsumer = playMenuSoundConsumer;
		
		updateMapConfig(gameScreen);
		
		loadConfig(gameScreen.getGameMapConfigPath());
		createControls();
		
		shapeRenderer = new ShapeRenderer();
	}
	
	public void updateMapConfig(GameScreen gameScreen) {
		playersRelativePositionOnMap = gameScreen.getPlayersPositionOnMap();
		fastTravelPoints = gameScreen.getFastTravelPositions();
		mapWidth = gameScreen.getMapWidth();
		mapHeight = gameScreen.getMapHeight();
		
		playerPositionPointerBlinkOn = true;
		playerPositionPointerBlinkTimer = 0;
	}
	
	private void loadConfig(String mapConfigPath) {
		if (mapConfigPath == null) {
			Gdx.app.debug(getClass().getSimpleName(), "No map config given in current map. Mini-Map will not be shown.");
			config = new MapConfig();
			config.name = "Map";
			return;
		}
		
		Gdx.app.debug(getClass().getSimpleName(), "Loading map config from file: " + mapConfigPath);
		Json json = new Json();
		config = json.fromJson(MapConfig.class, Gdx.files.internal(mapConfigPath));
		
		mapTexture = new TextureLoader(config.texture).loadDefaultTexture();
	}
	
	private void createControls() {
		background = new MenuBox(12, 8, MenuBox.TextureType.YELLOW_PAPER);
		banner = new MenuBox(8, 2, MenuBox.TextureType.BIG_BANNER);
		buttonBackToMenu = new FocusButtonBuilder().setNinePatchConfig(FocusButton.BUTTON_YELLOW_NINEPATCH_CONFIG)
				.setNinePatchConfigFocused(FocusButton.BUTTON_YELLOW_NINEPATCH_CONFIG_FOCUSED).setPosition(835, 550).setSize(110, 40).build();
		buttonBackToMenu.scaleBy(FocusButton.DEFAULT_BUTTON_SCALE);
		buttonBackToMenu.setFocused(true);
	}
	
	public String getMapStateConfigFile() {
		return config.mapStateConfigFile;
	}
	
	public void draw(float delta) {
		if (visible) {
			batch.begin();
			
			background.draw(batch, 120, -20, 950, 640);
			banner.draw(batch, 200, 480, 600, 200);
			buttonBackToMenu.draw(batch);
			
			float mapTextureX = 0;
			float mapTextureY = 0;
			if (hasMapTexture()) {
				mapTextureX = 100 + (1000 - config.textureHeight) / 2;
				mapTextureY = 40 + (500 - config.textureHeight) / 2;
				batch.draw(mapTexture, mapTextureX, mapTextureY, config.textureWidth, config.textureHeight);
			}
			
			batch.end();
			
			shapeRenderer.begin(ShapeType.Filled);
			drawPlayerPosition(delta, mapTextureX, mapTextureY);
			drawFastTravelPoints(delta, mapTextureX, mapTextureY);
			shapeRenderer.end();
			
			//drawing the map and the text in the same batch won't work for some reason
			batch.begin();
			drawText();
			batch.end();
		}
	}
	
	private boolean hasMapTexture() {
		return mapTexture != null;
	}
	
	private void drawPlayerPosition(float delta, float mapTextureX, float mapTextureY) {
		if (hasMapTexture()) {
			updateBlinkingPlayersPosition(delta);
			
			if (playerPositionPointerBlinkOn) {
				float playerPositionX = mapTextureX + (config.textureWidth * playersRelativePositionOnMap.x);
				float playerPositionY = mapTextureY + (config.textureHeight * playersRelativePositionOnMap.y);
				float markerRadius = 5;
				
				shapeRenderer.setColor(Color.RED);
				shapeRenderer.circle(playerPositionX, playerPositionY, markerRadius);
			}
		}
	}
	
	private void updateBlinkingPlayersPosition(float delta) {
		playerPositionPointerBlinkTimer += delta;
		if (playerPositionPointerBlinkTimer > PLAYER_POSITION_POINTER_BLINK_DELAY) {
			playerPositionPointerBlinkTimer -= PLAYER_POSITION_POINTER_BLINK_DELAY;
			playerPositionPointerBlinkOn = !playerPositionPointerBlinkOn;
		}
	}
	
	private void drawFastTravelPoints(float delta, float mapTextureX, float mapTextureY) {
		for (CharacterFastTravelProperties fastTravelPoint : fastTravelPoints) {
			float relativePositionX = fastTravelPoint.positionOnMapX * GameScreen.SCREEN_TO_WORLD / mapWidth;
			float relativePositionY = fastTravelPoint.positionOnMapY * GameScreen.SCREEN_TO_WORLD / mapHeight;
			
			float positionX = mapTextureX + (config.textureWidth * relativePositionX);
			float positionY = mapTextureY + (config.textureHeight * relativePositionY);
			
			float markerRadius = 6;
			
			if (fastTravelPoint.fastTravelPointId.equals(selectedFastTravelPointId)) {
				shapeRenderer.setColor(COLOR_FAST_TRAVEL_POINT_SELECTED);
			}
			else if (fastTravelPoint.enabled) {
				shapeRenderer.setColor(COLOR_FAST_TRAVEL_POINT_ENABLED);
			}
			else {
				shapeRenderer.setColor(COLOR_FAST_TRAVEL_POINT_DISABLED);
			}
			
			shapeRenderer.circle(positionX, positionY, markerRadius);
		}
	}
	
	private void drawText() {
		screenTextWriter.setColor(Color.BLACK);
		screenTextWriter.setScale(1.2f);
		screenTextWriter.drawText(config.name, 275, 594, 450, Align.center, false);
		
		screenTextWriter.setScale(0.8f);
		screenTextWriter.drawText(getButtonTextColorEncoding(buttonBackToMenu) + "Back", 870, 593);
		
		if (mapTexture == null) {
			screenTextWriter.setScale(1.5f);
			screenTextWriter.drawText("No Map Available for this level", 200, 350, 790, Align.center, true);
		}
	}
	
	private String getButtonTextColorEncoding(FocusButton button) {
		return button.hasFocus() ? InGameMenuScreen.TEXT_COLOR_ENCODING_FOCUS : InGameMenuScreen.TEXT_COLOR_ENCODING_NORMAL;
	}
	
	public void setFocusToBackButton() {
		buttonBackToMenu.setFocused(true);
		selectedFastTravelPointId = null;
	}
	
	public void setFocusTo(String stateName) {
		CharacterFastTravelProperties fastTravelProperties = getFastTravelPropertiesById(stateName);
		if (fastTravelProperties != null) {
			selectedFastTravelPointId = fastTravelProperties.fastTravelPointId;
			buttonBackToMenu.setFocused(false);
		}
	}
	
	private CharacterFastTravelProperties getFastTravelPropertiesById(String stateName) {
		for (CharacterFastTravelProperties fastTravelProperty : fastTravelPoints) {
			if (stateName.equals(fastTravelProperty.fastTravelPointId)) {
				return fastTravelProperty;
			}
		}
		return null;
	}
	
	@Override
	public void dispose() {
		super.dispose();
		shapeRenderer.dispose();
	}
	
	//****************************************************************
	//*** State machine methods (called via reflection)
	//****************************************************************
	
	public void selectFastTravelPoint() {
		Gdx.app.debug(getClass().getSimpleName(), "selectFastTravelPoint was invoked.");
		
		if (CutsceneHandler.getInstance().isCutsceneActive()) {
			playMenuSoundConsumer.accept(ControlledMenu.SOUND_ERROR);
			return;
		}
		
		if (selectedFastTravelPointId != null) {
			CharacterFastTravelProperties fastTravelTargetProperties = getFastTravelPropertiesById(selectedFastTravelPointId);
			if (fastTravelTargetProperties.enabled) {
				EventHandler.getInstance()
						.fireEvent(new EventConfig().setEventType(EventType.FAST_TRAVEL_TO_MAP_POSITION).setStringValue(selectedFastTravelPointId));
				backToGameCallback.run();
			}
			else {
				playMenuSoundConsumer.accept(ControlledMenu.SOUND_ERROR);
			}
		}
	}
}
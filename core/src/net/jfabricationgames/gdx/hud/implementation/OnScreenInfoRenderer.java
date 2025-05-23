package net.jfabricationgames.gdx.hud.implementation;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.ObjectMap;

import net.jfabricationgames.gdx.constants.Constants;
import net.jfabricationgames.gdx.data.handler.GlobalValuesDataHandler;
import net.jfabricationgames.gdx.event.EventConfig;
import net.jfabricationgames.gdx.event.EventHandler;
import net.jfabricationgames.gdx.event.EventListener;
import net.jfabricationgames.gdx.event.EventType;
import net.jfabricationgames.gdx.hud.StatsCharacter;
import net.jfabricationgames.gdx.map.GameMapManager;
import net.jfabricationgames.gdx.text.ScreenTextWriter;
import net.jfabricationgames.gdx.texture.TextureLoader;

public class OnScreenInfoRenderer implements EventListener, Disposable {
	
	private static final String CHARACTER_ACTION_BOW = "BOW";
	private static final String CHARACTER_ACTION_BOMB = "BOMB";
	
	private static final String ITEM_AMMO_TYPE_ARROW = "ARROW";
	private static final String ITEM_AMMO_TYPE_BOMB = "BOMB";
	
	private static final String GLOBAL_VALUES_KEY_ACTIVE_TOKEN_QUEST_NAME = "active_token_quest";
	private static final String GLOBAL_VALUES_KEY_ACTIVE_TOKEN_QUEST_MAP_EXCLUSIVE = "active_token_quest_map_exclusive";
	private static final String GLOBAL_VALUES_KEY_ACTIVE_TOKEN_QUEST_ICON = "active_token_quest_token_icon";
	
	private static final String TEXTURE_CONFIG = "config/hud/on_screen_item_renderer/textures.json";
	private static final float TEXT_SCALE = 0.9f;
	
	private static final float RENDER_SAVE_INFO_TIME = 3f;
	private static final float RENDER_SAVE_INFO_DOTS_CHANGING_TIME = 0.25f;
	
	private OrthographicCamera camera;
	private StatsCharacter character;
	private Vector2 tileUpperRight;
	
	private SpriteBatch batch;
	private ScreenTextWriter screenTextWriter;
	
	private GlobalValuesDataHandler globalValuesDataHandler;
	private GameMapManager gameMapManager;
	
	private TextureRegion coinIcon;
	private TextureRegion keyIcon;
	private TextureRegion metalIngotIcon;
	private ObjectMap<String, TextureRegion> tokenIcons;
	private ObjectMap<String, TextureRegion> specialActionIcons;
	
	private boolean renderSaveInfo;
	private float renderSaveInfoDeltaTime;
	
	public OnScreenInfoRenderer(OrthographicCamera camera, StatsCharacter character, float sceneWidth, float sceneHeight) {
		this.camera = camera;
		this.character = character;
		batch = new SpriteBatch();
		screenTextWriter = new ScreenTextWriter();
		screenTextWriter.setFont(Constants.DEFAULT_FONT_NAME);
		tileUpperRight = new Vector2(sceneWidth - 20f, sceneHeight - 20f);
		
		globalValuesDataHandler = GlobalValuesDataHandler.getInstance();
		gameMapManager = GameMapManager.getInstance();
		
		EventHandler.getInstance().registerEventListener(this);
		
		loadIcons();
	}
	
	private void loadIcons() {
		TextureLoader textureLoader = new TextureLoader(TEXTURE_CONFIG);
		coinIcon = textureLoader.loadTexture("coin");
		keyIcon = textureLoader.loadTexture("key");
		metalIngotIcon = textureLoader.loadTexture("metal_ingot");
		
		tokenIcons = new ObjectMap<>();
		tokenIcons.put("token", textureLoader.loadTexture("token")); // the default token icon
		// load all other token icons that start with the prefix token_
		for (String textureName : textureLoader.getTextureNames()) {
			if (textureName.startsWith("token_")) {
				tokenIcons.put(textureName, textureLoader.loadTexture(textureName));
			}
		}
		
		specialActionIcons = new ObjectMap<>();
		for (String specialAction : character.getActionList()) {
			specialActionIcons.put(specialAction, textureLoader.loadTexture(specialAction));
		}
	}
	
	public void render(float delta) {
		batch.setProjectionMatrix(camera.combined);
		screenTextWriter.setBatchProjectionMatrix(camera.combined);
		
		drawIcons();
		drawIconText();
		if (renderSaveInfo) {
			drawSaveInfo();
			updateSaveInfoTimer(delta);
		}
	}
	
	private void drawIcons() {
		TextureRegion activeSpecialActionIcon = specialActionIcons.get(character.getActiveAction().toLowerCase());
		
		batch.begin();
		batch.draw(coinIcon, tileUpperRight.x - 40f, tileUpperRight.y - 130f, 35f, 35f);
		batch.draw(keyIcon, tileUpperRight.x - 50f, tileUpperRight.y - 185f, 55f, 55f);
		batch.draw(metalIngotIcon, tileUpperRight.x - 40f, tileUpperRight.y - 225f, 35f, 35f);
		batch.draw(activeSpecialActionIcon, tileUpperRight.x - 40f, tileUpperRight.y - 275f, 35f, 35f);
		if (isTokenQuestActive()) {
			batch.draw(chooseTokenIcon(), tileUpperRight.x - 35f, tileUpperRight.y - 320f, 25f, 25f);
		}
		batch.end();
	}
	
	private TextureRegion chooseTokenIcon() {
		String tokenIcon = globalValuesDataHandler.get(GLOBAL_VALUES_KEY_ACTIVE_TOKEN_QUEST_ICON);
		if (tokenIcon == null) {
			tokenIcon = "token";
		}
		
		return tokenIcons.get(tokenIcon);
	}
	
	private void drawIconText() {
		int coins = character.getCoinsForHud();
		int keys = character.getNormalKeys();
		int metalIngots = character.getMetalIngots();
		int ammo;
		int tokens = isTokenQuestActive() ? character.getTokens(getActiveTokenQuestName()) : 0;
		
		switch (character.getActiveAction()) {
			case CHARACTER_ACTION_BOW:
				ammo = character.getAmmo(ITEM_AMMO_TYPE_ARROW);
				break;
			case CHARACTER_ACTION_BOMB:
				ammo = character.getAmmo(ITEM_AMMO_TYPE_BOMB);
				break;
			default:
				ammo = -1;
				break;
		}
		
		chooseTextColor();
		screenTextWriter.setScale(TEXT_SCALE);
		screenTextWriter.drawText(Integer.toString(coins), tileUpperRight.x - 155f, tileUpperRight.y - 103f, 100, Align.right, false);
		screenTextWriter.drawText(Integer.toString(keys), tileUpperRight.x - 155f, tileUpperRight.y - 148f, 100, Align.right, false);
		screenTextWriter.drawText(Integer.toString(metalIngots), tileUpperRight.x - 155f, tileUpperRight.y - 195f, 100, Align.right, false);
		if (ammo > -1) {
			screenTextWriter.drawText(Integer.toString(ammo), tileUpperRight.x - 155f, tileUpperRight.y - 245f, 100, Align.right, false);
		}
		if (isTokenQuestActive()) {
			screenTextWriter.drawText(Integer.toString(tokens), tileUpperRight.x - 155f, tileUpperRight.y - 295f, 100, Align.right, false);
		}
	}
	
	private boolean isTokenQuestActive() {
		String activeTokenQuestName = getActiveTokenQuestName();
		String activeTokenQuestMapExclusive = globalValuesDataHandler.get(GLOBAL_VALUES_KEY_ACTIVE_TOKEN_QUEST_MAP_EXCLUSIVE);
		String currentMapName = gameMapManager.getMap().getCurrentMapIdentifier();
		
		return activeTokenQuestName != null && !activeTokenQuestName.isEmpty() //
				&& (activeTokenQuestMapExclusive == null || activeTokenQuestMapExclusive.isEmpty() || activeTokenQuestMapExclusive.equals(currentMapName));
	}
	
	private String getActiveTokenQuestName() {
		return globalValuesDataHandler.get(GLOBAL_VALUES_KEY_ACTIVE_TOKEN_QUEST_NAME);
	}
	
	private void chooseTextColor() {
		if (GameMapManager.getInstance().getMap().isDungeonMap() || GameMapManager.getInstance().getMap().isBuildingMap()) {
			screenTextWriter.setColor(Color.GRAY);
		}
		else {
			screenTextWriter.setColor(Color.BLACK);
		}
	}
	
	private void drawSaveInfo() {
		chooseTextColor();
		
		String text = getDisplayedSaveInfoText();
		
		screenTextWriter.setScale(0.65f);
		screenTextWriter.drawText(text, 25, 40);
	}
	
	private String getDisplayedSaveInfoText() {
		String text = "Saving";
		final int displayStates = 4; // 0 to 3 dots
		int displayedDots = (int) ((renderSaveInfoDeltaTime % (displayStates * RENDER_SAVE_INFO_DOTS_CHANGING_TIME)) / RENDER_SAVE_INFO_DOTS_CHANGING_TIME);
		for (int i = 0; i < displayedDots; i++) {
			text += ".";
		}
		return text;
	}
	
	private void updateSaveInfoTimer(float delta) {
		renderSaveInfoDeltaTime += delta;
		if (renderSaveInfoDeltaTime > RENDER_SAVE_INFO_TIME) {
			renderSaveInfo = false;
		}
	}
	
	@Override
	public void handleEvent(EventConfig event) {
		if (event.eventType == EventType.QUICKSAVE || event.eventType == EventType.SAVE_GAME) {
			renderSaveInfo = true;
			renderSaveInfoDeltaTime = 0f;
		}
	}
	
	@Override
	public void dispose() {
		batch.dispose();
		EventHandler.getInstance().removeEventListener(this);
	}
}

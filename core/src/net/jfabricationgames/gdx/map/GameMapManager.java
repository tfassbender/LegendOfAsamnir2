package net.jfabricationgames.gdx.map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;

import net.jfabricationgames.gdx.map.implementation.GameMapFactory;
import net.jfabricationgames.gdx.map.implementation.GameMapLoaderFactory;

public class GameMapManager {
	
	public static final String GAME_MAPS_CONFIG_FILE_PATH = "config/map/maps.json";
	
	private static GameMapManager instance;
	
	public static synchronized GameMapManager getInstance() {
		if (instance == null) {
			instance = new GameMapManager();
		}
		return instance;
	}
	
	private Array<GameMapConfig> mapFiles;
	
	private GameMap gameMap;
	
	private GameMapManager() {
		loadMapsConfig();
	}
	
	@SuppressWarnings("unchecked")
	private void loadMapsConfig() {
		Json json = new Json();
		mapFiles = json.fromJson(Array.class, GameMapConfig.class, Gdx.files.internal(GAME_MAPS_CONFIG_FILE_PATH));
	}
	
	public void createGameMap(OrthographicCamera camera) {
		gameMap = GameMapFactory.createGameMap(camera);
	}
	
	public GameMap getMap() {
		return gameMap;
	}
	
	public void showMap(String mapIdentifier, int playerStartingPointId) {
		gameMap.beforeLoadMap(mapIdentifier);
		
		String mapAsset = getMapFilePath(mapIdentifier);
		GameMapLoaderFactory.createGameMapLoader(gameMap, mapAsset).loadMap();
		
		gameMap.afterLoadMap(mapIdentifier, playerStartingPointId);
	}
	
	public String getMapFilePath(String mapName) {
		return getMapConfigById(mapName).map;
	}
	
	private GameMapConfig getMapConfigById(String mapName) {
		for (GameMapConfig config : mapFiles) {
			if (mapName.equals(config.name)) {
				return config;
			}
		}
		throw new IllegalStateException("A map with the name '" + mapName + "' is not found in the config file.");
	}
	
	public String getMapStartConfig(String mapIdentifier) {
		return getMapConfigById(mapIdentifier).mapEnteringConfig;
	}
	
	public String getInitialMapIdentifier() {
		return getInitialMap().name;
	}
	
	public int getInitialStartingPointId() {
		return getInitialMap().initialStartingPointId;
	}
	
	public String getHomeMapIdentifier() {
		return getHomeMap().name;
	}
	
	public int getHomeMapStartingPointId() {
		return getHomeMap().homeMapStartingPointId;
	}
	
	public String getDebugStartConfig() {
		return getInitialMap().debugStartConfig;
	}
	
	private GameMapConfig getInitialMap() {
		for (GameMapConfig config : mapFiles) {
			if (config.initial) {
				return config;
			}
		}
		throw new IllegalStateException("The configuration file '" + GAME_MAPS_CONFIG_FILE_PATH + "' does not configure an initial map.");
	}
	
	private GameMapConfig getHomeMap() {
		for (GameMapConfig config : mapFiles) {
			if (config.homeMap) {
				return config;
			}
		}
		throw new IllegalStateException("The configuration file '" + GAME_MAPS_CONFIG_FILE_PATH + "' does not configure a home map.");
	}
}

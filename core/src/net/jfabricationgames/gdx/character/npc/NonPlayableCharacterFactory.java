package net.jfabricationgames.gdx.character.npc;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.ObjectMap.Entry;

import net.jfabricationgames.gdx.animation.AnimationManager;
import net.jfabricationgames.gdx.constants.Constants;
import net.jfabricationgames.gdx.object.NpcSpawnFactory;
import net.jfabricationgames.gdx.util.FactoryUtil;

public class NonPlayableCharacterFactory {
	
	private NonPlayableCharacterFactory() {}
	
	private static final String CONFIG_FILE = "config/factory/npc_factory.json";
	
	private static Config config;
	private static ObjectMap<String, NonPlayableCharacterTypeConfig> typeConfigs;
	
	private static NpcCharacterMap gameMap;
	
	static {
		config = FactoryUtil.loadConfig(Config.class, CONFIG_FILE);
		loadTypeConfigs();
		loadAnimations();
	}
	
	@SuppressWarnings("unchecked")
	private static void loadTypeConfigs() {
		Json json = new Json();
		ObjectMap<String, String> typeConfigFiles = json.fromJson(ObjectMap.class, String.class, Gdx.files.internal(config.npcTypes));
		typeConfigs = new ObjectMap<>();
		
		for (Entry<String, String> config : typeConfigFiles.entries()) {
			NonPlayableCharacterTypeConfig typeConfig = json.fromJson(NonPlayableCharacterTypeConfig.class, Gdx.files.internal(config.value));
			if (typeConfig.graphicsConfigFile != null) {
				typeConfig.graphicsConfig = json.fromJson(NonPlayableCharacterGraphicsConfig.class, Gdx.files.internal(typeConfig.graphicsConfigFile));
			}
			
			typeConfigs.put(config.key, typeConfig);
		}
	}
	
	private static void loadAnimations() {
		AnimationManager animationManager = AnimationManager.getInstance();
		for (NonPlayableCharacterTypeConfig config : typeConfigs.values()) {
			animationManager.loadAnimations(config.graphicsConfig.animationsConfig);
		}
	}
	
	public static void setGameMap(NpcCharacterMap gameMap) {
		NonPlayableCharacterFactory.gameMap = gameMap;
	}
	
	public static NonPlayableCharacter createNonPlayableCharacter(String type, float x, float y, MapProperties properties) {
		NonPlayableCharacterTypeConfig typeConfig = typeConfigs.get(type);
		if (typeConfig == null) {
			throw new IllegalStateException("No type config known for type: '" + type + "'. Either the type name is wrong or you have to add it to the objectTypesConfig (see \"" + CONFIG_FILE + "\")");
		}
		
		NonPlayableCharacter npc = new NonPlayableCharacter(typeConfig, properties);
		npc.setGameMap(gameMap);
		npc.createPhysicsBody(x * Constants.WORLD_TO_SCREEN, y * Constants.WORLD_TO_SCREEN);
		
		return npc;
	}
	
	public static NonPlayableCharacterFactoryInstance asInstance() {
		return new NonPlayableCharacterFactoryInstance();
	}
	
	public static class NonPlayableCharacterFactoryInstance implements NpcSpawnFactory {
		
		@Override
		public void createAndAddNpc(String type, float x, float y, MapProperties mapProperties, Runnable onRemoveFromMap) {
			NonPlayableCharacter npc = createNonPlayableCharacter(type, x, y, mapProperties);
			npc.setOnRemoveFromMap(onRemoveFromMap);
			gameMap.addNpc(npc);
		}
	}
	
	private static class Config {
		
		public String npcTypes;
	}
}

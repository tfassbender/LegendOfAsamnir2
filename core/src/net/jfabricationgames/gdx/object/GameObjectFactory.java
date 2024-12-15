package net.jfabricationgames.gdx.object;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.utils.ObjectMap;

import net.jfabricationgames.gdx.animation.AnimationManager;
import net.jfabricationgames.gdx.assets.AssetGroupManager;
import net.jfabricationgames.gdx.attack.hit.AttackType;
import net.jfabricationgames.gdx.constants.Constants;
import net.jfabricationgames.gdx.object.destroyable.DestroyableObject;
import net.jfabricationgames.gdx.object.event.EventObject;
import net.jfabricationgames.gdx.object.interactive.AttackActivatedStateSwitchObject;
import net.jfabricationgames.gdx.object.interactive.InteractiveObject;
import net.jfabricationgames.gdx.object.interactive.LockedObject;
import net.jfabricationgames.gdx.object.interactive.MovingObject;
import net.jfabricationgames.gdx.object.interactive.StateSwitchObject;
import net.jfabricationgames.gdx.object.moveable.MovableObject;
import net.jfabricationgames.gdx.object.spawn.SpawnPoint;
import net.jfabricationgames.gdx.util.FactoryUtil;

public class GameObjectFactory {
	
	private GameObjectFactory() {}
	
	private static final String CONFIG_FILE = "config/factory/object_factory.json";
	
	private static Config config;
	private static TextureAtlas atlas;
	private static ObjectMap<String, GameObjectTypeConfig> typeConfigs;
	
	private static GameObjectMap gameMap;
	private static EnemySpawnFactory enemySpawnFactory;
	private static ItemSpawnFactory itemSpawnFactory;
	private static NpcSpawnFactory npcSpawnFactory;
	private static AnimalSpawnFactory animalSpawnFactory;
	private static GameObjectItemDropUtil itemDropUtil;
	private static Class<?> playerObjectClass;
	
	static {
		config = FactoryUtil.loadConfig(Config.class, CONFIG_FILE);
		typeConfigs = FactoryUtil.loadTypeConfigs(config.objectTypesConfig, GameObjectTypeConfig.class);
		atlas = AssetGroupManager.getInstance().get(config.objectAtlas);
		AnimationManager.getInstance().loadAnimations(config.objectAnimations);
	}
	
	public static void setGameMap(GameObjectMap gameMap) {
		GameObjectFactory.gameMap = gameMap;
	}
	
	public static void setEnemySpawnFactory(EnemySpawnFactory enemySpawnFactory) {
		GameObjectFactory.enemySpawnFactory = enemySpawnFactory;
	}
	
	public static void setItemSpawnFactory(ItemSpawnFactory itemSpawnFactory) {
		GameObjectFactory.itemSpawnFactory = itemSpawnFactory;
	}
	
	public static void setNpcSpawnFactory(NpcSpawnFactory npcSpawnFactory) {
		GameObjectFactory.npcSpawnFactory = npcSpawnFactory;
	}
	
	public static void setAnimalSpawnFactory(AnimalSpawnFactory animalSpawnFactory) {
		GameObjectFactory.animalSpawnFactory = animalSpawnFactory;
	}
	
	public static void setItemDropUtil(GameObjectItemDropUtil itemDropUtil) {
		GameObjectFactory.itemDropUtil = itemDropUtil;
	}
	
	public static void setPlayerObjectClass(Class<?> playerObjectClass) {
		GameObjectFactory.playerObjectClass = playerObjectClass;
	}
	
	public static void createAndAddObject(String type, float x, float y, MapProperties mapProperties, Runnable onRemoveFromMap) {
		GameObject object = createObject(type, x, y, mapProperties);
		object.setOnRemoveFromMap(onRemoveFromMap);
		gameMap.addObject(object);
	}
	
	public static GameObject createObject(String type, float x, float y, MapProperties properties) {
		GameObjectTypeConfig typeConfig = typeConfigs.get(type);
		if (typeConfig == null) {
			throw new IllegalStateException("No type config known for type: '" + type + "'. Either the type name is wrong or you have to add it to the objectTypesConfig (see \"" + CONFIG_FILE + "\")");
		}
		
		Sprite sprite = FactoryUtil.createSprite(atlas, x, y, typeConfig.texture);
		sprite.setScale(typeConfig.textureSizeFactorX * Constants.WORLD_TO_SCREEN, typeConfig.textureSizeFactorY * Constants.WORLD_TO_SCREEN);
		
		GameObject object;
		switch (typeConfig.type) {
			case DESTROYABLE:
				object = new DestroyableObject(typeConfig, sprite, properties, gameMap);
				break;
			case INTERACTIVE:
				object = new InteractiveObject(typeConfig, sprite, properties, gameMap);
				break;
			case LOCKED:
				object = new LockedObject(typeConfig, sprite, properties, gameMap);
				break;
			case STATE_SWITCH:
				object = new StateSwitchObject(typeConfig, sprite, properties, gameMap);
				break;
			case MOVING:
				object = new MovingObject(typeConfig, sprite, properties, gameMap);
				break;
			case SPAWN_POINT:
				SpawnPoint spawnPoint = new SpawnPoint(typeConfig, sprite, properties, gameMap);
				spawnPoint.setEnemySpawnFactory(enemySpawnFactory);
				spawnPoint.setItemSpawnFactory(itemSpawnFactory);
				spawnPoint.setNpcSpawnFactory(npcSpawnFactory);
				spawnPoint.setAnimalSpawnFactory(animalSpawnFactory);
				object = spawnPoint;
				break;
			case EVENT_OBJECT:
				object = new EventObject(typeConfig, sprite, properties, gameMap);
				break;
			case MOVABLE:
				object = new MovableObject(typeConfig, sprite, properties, gameMap);
				break;
			// specialized types
			case DWARVEN_GUARDIAN_CONSTRUCT_SWITCH:
				object = new AttackActivatedStateSwitchObject(typeConfig, sprite, properties, gameMap, AttackType.DWARVEN_GUARDIAN_CONSTRUCT_FIST);
				break;
			case DWARVEN_GUARDIAN_CONSTRUCT_TORCH:
				object = new AttackActivatedStateSwitchObject(typeConfig, sprite, properties, gameMap, AttackType.DWARVEN_GUARDIAN_CONSTRUCT_FIRE);
				break;
			default:
				throw new IllegalStateException("Unknown GameObjectType \"" + typeConfig.type + "\" of object type \"" + type + "\"");
		}
		
		object.setItemDropUtil(itemDropUtil);
		object.processMapProperties();
		object.setPlayerObjectClass(playerObjectClass);
		
		object.createPhysicsBody(x * Constants.WORLD_TO_SCREEN, y * Constants.WORLD_TO_SCREEN);
		object.setTextureAtlas(atlas);
		
		return object;
	}
	
	public static class Config {
		
		public String objectAtlas;
		public String objectAnimations;
		public String objectTypesConfig;
	}
}

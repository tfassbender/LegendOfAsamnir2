package net.jfabricationgames.gdx.object;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;

import net.jfabricationgames.gdx.animation.AnimationManager;
import net.jfabricationgames.gdx.assets.AssetGroupManager;
import net.jfabricationgames.gdx.attack.AttackType;
import net.jfabricationgames.gdx.constants.Constants;
import net.jfabricationgames.gdx.object.config.ConfigObject;
import net.jfabricationgames.gdx.object.destroyable.DestroyableObject;
import net.jfabricationgames.gdx.object.event.EventObject;
import net.jfabricationgames.gdx.object.force.ForceObject;
import net.jfabricationgames.gdx.object.interactive.AnimationObject;
import net.jfabricationgames.gdx.object.interactive.AttackActivatedStateSwitchObject;
import net.jfabricationgames.gdx.object.interactive.InteractiveObject;
import net.jfabricationgames.gdx.object.interactive.LockedObject;
import net.jfabricationgames.gdx.object.interactive.MovingObject;
import net.jfabricationgames.gdx.object.interactive.MultiStateSwitchObject;
import net.jfabricationgames.gdx.object.interactive.RotatingPuzzle;
import net.jfabricationgames.gdx.object.interactive.StateSwitchObject;
import net.jfabricationgames.gdx.object.moveable.DraggableObject;
import net.jfabricationgames.gdx.object.moveable.MovableObject;
import net.jfabricationgames.gdx.object.spawn.SpawnPoint;
import net.jfabricationgames.gdx.object.traversable.EnemyPathBlockerObject;
import net.jfabricationgames.gdx.object.traversable.TraverseableObject;
import net.jfabricationgames.gdx.util.FactoryUtil;
import net.jfabricationgames.gdx.util.MapUtil;

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
	private static Class<? extends InteractivePlayer> playerObjectClass;
	
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
	
	public static void setPlayerObjectClass(Class<? extends InteractivePlayer> playerObjectClass) {
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
		
		String texture = getTextureName(properties, typeConfig);
		float textureSizeFactorX = getTextureSizeFactorX(properties, typeConfig);
		float textureSizeFactorY = getTextureSizeFactorY(properties, typeConfig);
		Sprite sprite = FactoryUtil.createSprite(atlas, x, y, texture);
		sprite.setScale(textureSizeFactorX * Constants.WORLD_TO_SCREEN, textureSizeFactorY * Constants.WORLD_TO_SCREEN);
		
		GameObject object;
		switch (typeConfig.type) {
			case BASIC:
				object = new GameObject(typeConfig, sprite, properties, gameMap);
				break;
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
			case MULTI_STATE_SWITCH:
				object = new MultiStateSwitchObject(typeConfig, sprite, properties, gameMap);
				((MultiStateSwitchObject) object).setAdditionalTextures(loadAdditionalTextures(typeConfig.additionalTextures, x, y, typeConfig));
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
			case DRAGGABLE:
				object = new DraggableObject(typeConfig, sprite, properties, gameMap);
				break;
			case ANIMATION:
				object = new AnimationObject(typeConfig, sprite, properties, gameMap);
				break;
			case CONFIG_OBJECT:
				object = new ConfigObject(typeConfig, sprite, properties, gameMap);
				break;
			case FORCE:
				object = new ForceObject(typeConfig, sprite, properties, gameMap);
				break;
			case TRAVERSEABLE:
				object = new TraverseableObject(typeConfig, sprite, properties, gameMap);
				break;
			case ENEMY_PATH_BLOCKER:
				object = new EnemyPathBlockerObject(typeConfig, sprite, properties, gameMap);
				break;
			// specialized types
			case DWARVEN_GUARDIAN_CONSTRUCT_SWITCH:
				object = new AttackActivatedStateSwitchObject(typeConfig, sprite, properties, gameMap, AttackType.DWARVEN_GUARDIAN_CONSTRUCT_FIST);
				break;
			case DWARVEN_GUARDIAN_CONSTRUCT_TORCH:
				object = new AttackActivatedStateSwitchObject(typeConfig, sprite, properties, gameMap, AttackType.DWARVEN_GUARDIAN_CONSTRUCT_FIRE);
				break;
			case ROTATING_PUZZLE:
				object = new RotatingPuzzle(typeConfig, sprite, properties, gameMap);
				((RotatingPuzzle) object).setAdditionalTextures(loadAdditionalTextures(typeConfig.additionalTextures, x, y, typeConfig));
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
	
	private static String getTextureName(MapProperties properties, GameObjectTypeConfig typeConfig) {
		return MapUtil.getMapPropertyConfigValue(properties, "_typeConfig_texture").orElse(typeConfig.texture);
	}
	
	private static float getTextureSizeFactorX(MapProperties properties, GameObjectTypeConfig typeConfig) {
		return MapUtil.getMapPropertyConfigValueAsFloat(properties, "_typeConfig_textureSizeFactorX").orElse(typeConfig.textureSizeFactorX);
	}
	
	private static float getTextureSizeFactorY(MapProperties properties, GameObjectTypeConfig typeConfig) {
		return MapUtil.getMapPropertyConfigValueAsFloat(properties, "_typeConfig_textureSizeFactorY").orElse(typeConfig.textureSizeFactorY);
	}
	
	private static Array<Sprite> loadAdditionalTextures(Array<String> additionalTextures, float x, float y, GameObjectTypeConfig typeConfig) {
		Array<Sprite> additionalSprites = new Array<Sprite>();
		
		for (String texture : additionalTextures) {
			Sprite sprite = FactoryUtil.createSprite(atlas, // 
					x + (typeConfig.additionalTexturesOffsetX / Constants.WORLD_TO_SCREEN), // divide by WORLD_TO_SCREEN to get the offset in the same unit as all other offsets
					y + (typeConfig.additionalTexturesOffsetY / Constants.WORLD_TO_SCREEN), // divide by WORLD_TO_SCREEN to get the offset in the same unit as all other offsets
					texture);
			sprite.setScale(Constants.WORLD_TO_SCREEN, Constants.WORLD_TO_SCREEN);
			additionalSprites.add(sprite);
		}
		
		return additionalSprites;
	}
	
	public static class Config {
		
		public String objectAtlas;
		public String objectAnimations;
		public String objectTypesConfig;
	}
}

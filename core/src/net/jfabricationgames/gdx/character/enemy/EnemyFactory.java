package net.jfabricationgames.gdx.character.enemy;

import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.utils.ObjectMap;

import net.jfabricationgames.gdx.animation.AnimationManager;
import net.jfabricationgames.gdx.character.enemy.implementation.AlarmClock;
import net.jfabricationgames.gdx.character.enemy.implementation.Bat;
import net.jfabricationgames.gdx.character.enemy.implementation.Bugbear;
import net.jfabricationgames.gdx.character.enemy.implementation.Cyclops;
import net.jfabricationgames.gdx.character.enemy.implementation.Dummy;
import net.jfabricationgames.gdx.character.enemy.implementation.DwarvenGuardianConstruct;
import net.jfabricationgames.gdx.character.enemy.implementation.FireTotemDummy;
import net.jfabricationgames.gdx.character.enemy.implementation.FrostGiant;
import net.jfabricationgames.gdx.character.enemy.implementation.Gargoyle;
import net.jfabricationgames.gdx.character.enemy.implementation.GiantGolem;
import net.jfabricationgames.gdx.character.enemy.implementation.GoblinKing;
import net.jfabricationgames.gdx.character.enemy.implementation.Ifrit;
import net.jfabricationgames.gdx.character.enemy.implementation.Minotaur;
import net.jfabricationgames.gdx.character.enemy.implementation.Ogre;
import net.jfabricationgames.gdx.character.enemy.implementation.Phoenixling;
import net.jfabricationgames.gdx.character.enemy.implementation.SkeletonKing;
import net.jfabricationgames.gdx.character.enemy.implementation.SpiderQueen;
import net.jfabricationgames.gdx.character.enemy.implementation.Totem;
import net.jfabricationgames.gdx.character.enemy.implementation.VengefulSpirit;
import net.jfabricationgames.gdx.constants.Constants;
import net.jfabricationgames.gdx.object.EnemySpawnFactory;
import net.jfabricationgames.gdx.projectile.ProjectileSpawnFactory;
import net.jfabricationgames.gdx.util.FactoryUtil;

public class EnemyFactory {
	
	private EnemyFactory() {}
	
	private static final String ENEMY_NAME_BAT = "bat";
	private static final String ENEMY_NAME_TOTEM = "totem";
	private static final String ENEMY_NAME_ALARM_CLOCK = "alarm_clock";
	private static final String ENEMY_NAME_GARGOYLE = "gargoyle";
	private static final String ENEMY_NAME_PHOENIXLING = "phoenixling";
	
	private static final String ENEMY_NAME_DUMMY = "dummy";
	private static final String ENEMY_NAME_DWARVEN_GUARDIAN_CONSTRUCT = "dwarven_guardian_construct";
	private static final String ENEMY_NAME_FIRE_TOTEM_DUMMY = "fire_totem_dummy";
	
	private static final String ENEMY_NAME_MINOTAUR = "minotaur";
	private static final String ENEMY_NAME_CYCLOPS = "cyclops";
	private static final String ENEMY_NAME_GOBLIN_KING = "goblin_king";
	private static final String ENEMY_NAME_OGRE = "ogre";
	private static final String ENEMY_NAME_GIANT_GOLEM = "giant_golem";
	private static final String ENEMY_NAME_BUGBEAR = "bugbear";
	private static final String ENEMY_NAME_FROST_GIANT = "frost_giant";
	private static final String ENEMY_NAME_IFRIT = "ifrit";
	private static final String ENEMY_NAME_SKELETON_KING = "skeleton_king";
	private static final String ENEMY_NAME_SPIDER_QUEEN = "spider_queen";
	private static final String ENEMY_NAME_VENGEFUL_SPIRIT = "vengeful_spirit";
	
	private static final String CONFIG_FILE = "config/factory/enemy_factory.json";
	
	private static Config config;
	private static ObjectMap<String, EnemyTypeConfig> typeConfigs;
	
	private static EnemyCharacterMap gameMap;
	
	static {
		config = FactoryUtil.loadConfig(Config.class, CONFIG_FILE);
		typeConfigs = FactoryUtil.loadTypeConfigs(config.enemyTypesConfig, EnemyTypeConfig.class);
		loadEnemyAnimations();
	}
	
	private static void loadEnemyAnimations() {
		AnimationManager animationManager = AnimationManager.getInstance();
		for (EnemyTypeConfig config : typeConfigs.values()) {
			animationManager.loadAnimations(config.animationsConfig);
		}
	}
	
	public static void setGameMap(EnemyCharacterMap gameMap) {
		EnemyFactory.gameMap = gameMap;
	}
	
	public static Enemy createEnemy(String type, float x, float y, MapProperties properties) {
		EnemyTypeConfig typeConfig = typeConfigs.get(type);
		if (typeConfig == null) {
			throw new IllegalStateException("No type config known for type: '" + type + "'. Either the type name is wrong or you have to add it to the objectTypesConfig (see \"" + CONFIG_FILE + "\")");
		}
		
		Enemy enemy;
		switch (type) {
			case ENEMY_NAME_BAT:
				enemy = new Bat(typeConfig, properties);
				break;
			case ENEMY_NAME_TOTEM:
				enemy = new Totem(typeConfig, properties);
				break;
			case ENEMY_NAME_FIRE_TOTEM_DUMMY:
				enemy = new FireTotemDummy(typeConfig, properties);
				break;
			case ENEMY_NAME_MINOTAUR:
				enemy = new Minotaur(typeConfig, properties);
				break;
			case ENEMY_NAME_CYCLOPS:
				enemy = new Cyclops(typeConfig, properties);
				break;
			case ENEMY_NAME_GOBLIN_KING:
				enemy = new GoblinKing(typeConfig, properties);
				break;
			case ENEMY_NAME_ALARM_CLOCK:
				enemy = new AlarmClock(typeConfig, properties);
				break;
			case ENEMY_NAME_DUMMY:
				enemy = new Dummy(typeConfig, properties);
				break;
			case ENEMY_NAME_OGRE:
				enemy = new Ogre(typeConfig, properties);
				break;
			case ENEMY_NAME_DWARVEN_GUARDIAN_CONSTRUCT:
				enemy = new DwarvenGuardianConstruct(typeConfig, properties);
				break;
			case ENEMY_NAME_GIANT_GOLEM:
				enemy = new GiantGolem(typeConfig, properties);
				break;
			case ENEMY_NAME_GARGOYLE:
				enemy = new Gargoyle(typeConfig, properties);
				break;
			case ENEMY_NAME_BUGBEAR:
				enemy = new Bugbear(typeConfig, properties);
				break;
			case ENEMY_NAME_FROST_GIANT:
				enemy = new FrostGiant(typeConfig, properties);
				break;
			case ENEMY_NAME_PHOENIXLING:
				enemy = new Phoenixling(typeConfig, properties);
				break;
			case ENEMY_NAME_IFRIT:
				enemy = new Ifrit(typeConfig, properties);
				break;
			case ENEMY_NAME_SKELETON_KING:
				enemy = new SkeletonKing(typeConfig, properties);
				break;
			case ENEMY_NAME_SPIDER_QUEEN:
				enemy = new SpiderQueen(typeConfig, properties);
				break;
			case ENEMY_NAME_VENGEFUL_SPIRIT:
				enemy = new VengefulSpirit(typeConfig, properties);
				break;
			default:
				enemy = new Enemy(typeConfig, properties);
				break;
		}
		enemy.setGameMap(gameMap);
		enemy.createPhysicsBody(x * Constants.WORLD_TO_SCREEN, y * Constants.WORLD_TO_SCREEN);
		
		return enemy;
	}
	
	public static EnemyFactoryInstance asInstance() {
		return new EnemyFactoryInstance();
	}
	
	public static class EnemyFactoryInstance implements EnemySpawnFactory, ProjectileSpawnFactory {
		
		@Override
		public void createAndAddEnemy(String type, float x, float y, MapProperties mapProperties) {
			createAndAddEnemy(type, x, y, mapProperties, null);
		}
		
		@Override
		public void createAndAddEnemy(String type, float x, float y, MapProperties mapProperties, Runnable onRemoveFromMap) {
			Enemy enemy = createEnemy(type, x, y, mapProperties);
			enemy.setOnRemoveFromMap(onRemoveFromMap);
			gameMap.addEnemy(enemy);
		}
	}
	
	private static class Config {
		
		public String enemyTypesConfig;
	}
}

package net.jfabricationgames.gdx.projectile;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.ObjectMap;

import net.jfabricationgames.gdx.assets.AssetGroupManager;
import net.jfabricationgames.gdx.factory.AbstractFactory;
import net.jfabricationgames.gdx.map.GameMap;
import net.jfabricationgames.gdx.physics.PhysicsCollisionType;
import net.jfabricationgames.gdx.physics.PhysicsWorld;

public class ProjectileFactory extends AbstractFactory {
	
	private static final String configFile = "config/factory/projectile_factory.json";
	private static Config config;
	
	private static ProjectileFactory instance;
	
	private ObjectMap<String, ProjectileTypeConfig> typeConfigs;
	
	private ProjectileFactory(GameMap gameMap) {
		this.gameMap = gameMap;
		
		if (config == null) {
			config = loadConfig(Config.class, configFile);
		}
		
		loadTypeConfigs();
		
		AssetGroupManager assetManager = AssetGroupManager.getInstance();
		atlas = assetManager.get(config.atlas);
		world = PhysicsWorld.getInstance().getWorld();
	}
	
	public static synchronized ProjectileFactory getInstance() {
		if (instance == null) {
			throw new IllegalStateException("The instance of ProjectileFactory has not yet been created. "
					+ "Use the createInstance(GameMap) method to create the instance.");
		}
		return instance;
	}
	
	public static synchronized ProjectileFactory createInstance(GameMap gameMap) {
		if (instance != null && instance.gameMap.equals(gameMap)) {
			Gdx.app.error(ProjectileFactory.class.getSimpleName(), "A ProjectileFactory for this game map has already been created.");
		}
		instance = new ProjectileFactory(gameMap);
		return instance;
	}
	
	@SuppressWarnings("unchecked")
	private void loadTypeConfigs() {
		typeConfigs = json.fromJson(ObjectMap.class, ProjectileTypeConfig.class, Gdx.files.internal(config.typesConfig));
	}
	
	public Projectile createProjectile(String type, Vector2 position, Vector2 direction, PhysicsCollisionType collisionType) {
		ProjectileTypeConfig typeConfig = typeConfigs.get(type);
		if (typeConfig == null) {
			throw new IllegalStateException("No type config known for type: " + type
					+ ". Either the type name is wrong or you have to add it to the projectileTypesConfig (see \"" + configFile + "\")");
		}
		
		Sprite sprite = createSprite(position.x, position.y, typeConfig.texture);
		
		Projectile projectile;
		switch (type) {
			case "arrow":
				projectile = new Arrow(typeConfig, sprite);
				break;
			default:
				throw new IllegalStateException("Unknown object type: " + type);
		}
		projectile.setGameMap(gameMap);
		projectile.createPhysicsBody(world, position, collisionType);
		projectile.startProjectile(direction);
		
		gameMap.addProjectile(projectile);
		
		return projectile;
	}
	
	public static class Config {
		
		public String atlas;
		public String typesConfig;
	}
}
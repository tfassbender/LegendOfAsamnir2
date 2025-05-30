package net.jfabricationgames.gdx.projectile;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.ObjectMap;

import net.jfabricationgames.gdx.animation.AnimationDirector;
import net.jfabricationgames.gdx.animation.AnimationManager;
import net.jfabricationgames.gdx.assets.AssetGroupManager;
import net.jfabricationgames.gdx.attack.AttackConfig;
import net.jfabricationgames.gdx.physics.PhysicsCollisionType;
import net.jfabricationgames.gdx.util.FactoryUtil;

public class ProjectileFactory {
	
	private ProjectileFactory() {}
	
	private static final String CONFIG_FILE = "config/factory/projectile_factory.json";
	private static final String ANIMATION_CONFIG_FILE = "config/animation/projectiles.json";
	
	private static Config config;
	private static TextureAtlas atlas;
	private static ObjectMap<String, ProjectileTypeConfig> typeConfigs;
	
	private static ProjectileMap gameMap;
	
	private static ProjectileSpawnFactory spawnFactory;
	
	static {
		config = FactoryUtil.loadConfig(Config.class, CONFIG_FILE);
		typeConfigs = FactoryUtil.loadTypeConfigs(config.typesConfig, ProjectileTypeConfig.class);
		AnimationManager.getInstance().loadAnimations(ANIMATION_CONFIG_FILE);
		atlas = AssetGroupManager.getInstance().get(config.atlas);
	}
	
	public static void setGameMap(ProjectileMap gameMap) {
		ProjectileFactory.gameMap = gameMap;
	}
	
	public static void setSpawnFactory(ProjectileSpawnFactory spawnFactory) {
		ProjectileFactory.spawnFactory = spawnFactory;
	}
	
	public static Projectile createProjectileAndAddToMap(AttackConfig attackConfig, Vector2 position, Vector2 direction, PhysicsCollisionType collisionType) {
		String type = attackConfig.projectileType;
		
		if (type == null) {
			throw new IllegalStateException("The 'type' parameter mussn't be null. Maybe the projectileType was not configured in the attack config file?");
		}
		ProjectileTypeConfig typeConfig = typeConfigs.get(type);
		if (typeConfig == null) {
			throw new IllegalStateException("No type config known for type: " + type + ". Either the type name is wrong or you have to add it to the projectileTypesConfig (see \"" + CONFIG_FILE + "\")");
		}
		
		Sprite sprite = null;
		if (typeConfig.texture != null) {
			sprite = FactoryUtil.createSprite(atlas, position.x, position.y, typeConfig.texture);
		}
		AnimationDirector<TextureRegion> animation = null;
		if (typeConfig.animation != null) {
			if (typeConfig.textureAnimation) {
				animation = AnimationManager.getInstance().getTextureAnimationDirectorCopy(typeConfig.animation);
			}
			else {
				animation = AnimationManager.getInstance().getGrowingAnimationDirector(typeConfig.animation);
			}
		}
		
		Projectile projectile;
		switch (typeConfig.attackType) {
			case ARROW:
				projectile = new Arrow(typeConfig, attackConfig, sprite, gameMap);
				break;
			case BOMB:
				projectile = new Bomb(typeConfig, attackConfig, sprite, gameMap);
				break;
			case EXPLOSION:
				projectile = new Explosion(typeConfig, attackConfig, animation, gameMap);
				collisionType = PhysicsCollisionType.EXPLOSION;
				break;
			case WEB:
				projectile = new Web(typeConfig, attackConfig, animation, gameMap);
				break;
			case FIREBALL:
				projectile = new ImpFireball(typeConfig, attackConfig, animation, gameMap);
				break;
			case ROCK:
				projectile = new Rock(typeConfig, attackConfig, sprite, gameMap);
				break;
			case BOOMERANG:
				projectile = new Boomerang(typeConfig, attackConfig, sprite, gameMap);
				break;
			case WAND:
			case MAGIC_WAVE:
				projectile = new MagicWave(typeConfig, attackConfig, sprite, gameMap);
				break;
			case COIN_BAG:
				projectile = new CoinBag(typeConfig, attackConfig, sprite, gameMap);
				break;
			case FORCE_FIELD:
				projectile = new ForceField(typeConfig, attackConfig, animation, gameMap);
				break;
			case DWARVEN_GUARDIAN_CONSTRUCT_FIST:
				projectile = new DwarvenGuardianConstructFist(typeConfig, attackConfig, animation, gameMap);
				break;
			case DWARVEN_GUARDIAN_CONSTRUCT_FIRE:
				projectile = new DwarvenGuardianConstructFire(typeConfig, attackConfig, animation, gameMap);
				break;
			case ANIMATED_HIT:
				projectile = new AnimatedHit(typeConfig, attackConfig, animation, gameMap);
				break;
			case SLINGSHOT:
				projectile = new Slingshot(typeConfig, attackConfig, sprite, gameMap);
				break;
			case HADOUKEN:
				projectile = new Hadouken(typeConfig, attackConfig, animation, gameMap);
				break;
			case FROST_GIANT_AXE_THROW:
				projectile = new FrostGiantAxe(typeConfig, attackConfig, sprite, gameMap);
				break;
			case HOOKSHOT:
				projectile = new Hookshot(typeConfig, attackConfig, sprite, gameMap);
				break;
			case COCOON:
				projectile = new CocoonProjectile(typeConfig, attackConfig, animation, gameMap, true);
				break;
			case COCOON_PROJECTILE:
				projectile = new CocoonProjectile(typeConfig, attackConfig, animation, gameMap, false);
				break;
			case SPECTRAL_SWORD:
				projectile = new SpectralSword(typeConfig, attackConfig, animation, gameMap);
				break;
			default:
				throw new IllegalStateException("Unknown object type: " + type);
		}
		
		projectile.setDamage(attackConfig.damage);
		projectile.setPushForce(attackConfig.pushForce);
		projectile.setPushForceWhenBlocked(attackConfig.pushForceWhenBlocked);
		projectile.setPushForceAffectedByBlock(attackConfig.pushForceAffectedByBlock);
		projectile.setExplosionDamage(attackConfig.explosionDamage);
		projectile.setExplosionPushForce(attackConfig.explosionPushForce);
		projectile.setExplosionPushForceAffectedByBlock(attackConfig.explosionPushForceAffectedByBlock);
		
		projectile.setExplosionFactory(ProjectileFactory::createProjectileAndAddToMap);
		projectile.setSpawnFactory(spawnFactory);
		projectile.createPhysicsBody(position, direction, collisionType);
		projectile.startProjectile(direction);
		
		gameMap.addProjectile(projectile);
		
		return projectile;
	}
	
	public static class Config {
		
		public String atlas;
		public String typesConfig;
	}
}

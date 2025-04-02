package net.jfabricationgames.gdx.object.traversable;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;

import net.jfabricationgames.gdx.constants.Constants;
import net.jfabricationgames.gdx.object.GameObject;
import net.jfabricationgames.gdx.object.GameObjectMap;
import net.jfabricationgames.gdx.object.GameObjectTypeConfig;
import net.jfabricationgames.gdx.physics.PhysicsBodyCreator;
import net.jfabricationgames.gdx.physics.PhysicsBodyCreator.PhysicsBodyProperties;
import net.jfabricationgames.gdx.physics.PhysicsCollisionType;

/**
 * An object blocks the path (like a solid physics object), but only interacts with enemies. 
 * The player can walk through the object and projectiles can fly through it.
 */
public class EnemyPathBlockerObject extends GameObject {
	
	public EnemyPathBlockerObject(GameObjectTypeConfig type, Sprite sprite, MapProperties properties, GameObjectMap gameMap) {
		super(type, sprite, properties, gameMap);
	}
	
	@Override
	public void createPhysicsBody(float x, float y) {
		float width = mapProperties.get("width", Float.class) * Constants.WORLD_TO_SCREEN;
		float height = mapProperties.get("height", Float.class) * Constants.WORLD_TO_SCREEN;
		
		x = mapProperties.get("x", Float.class) * Constants.WORLD_TO_SCREEN + width * 0.5f;
		y = mapProperties.get("y", Float.class) * Constants.WORLD_TO_SCREEN + height * 0.5f;
		
		PhysicsBodyProperties properties = physicsBodyProperties //
				.setX(x) //
				.setY(y) //
				.setWidth(width) //
				.setHeight(height) //
				.setCollisionType(PhysicsCollisionType.OBSTACLE_ENEMY) //
				.setType(BodyType.StaticBody);
		
		body = PhysicsBodyCreator.createRectangularBody(properties);
		body.setUserData(this);
	}
}

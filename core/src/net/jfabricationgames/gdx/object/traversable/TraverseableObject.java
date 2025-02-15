package net.jfabricationgames.gdx.object.traversable;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.maps.MapProperties;

import net.jfabricationgames.gdx.constants.Constants;
import net.jfabricationgames.gdx.event.EventConfig;
import net.jfabricationgames.gdx.event.EventHandler;
import net.jfabricationgames.gdx.event.EventListener;
import net.jfabricationgames.gdx.object.GameObject;
import net.jfabricationgames.gdx.object.GameObjectMap;
import net.jfabricationgames.gdx.object.GameObjectTypeConfig;
import net.jfabricationgames.gdx.physics.PhysicsBodyCreator;
import net.jfabricationgames.gdx.physics.PhysicsBodyCreator.PhysicsBodyProperties;
import net.jfabricationgames.gdx.physics.PhysicsCollisionType;

/**
 * An object that changes it's body to a sensor and back by (configurable) events, so that the player may walk through it.
 */
public class TraverseableObject extends GameObject implements EventListener {
	
	public TraverseableObject(GameObjectTypeConfig type, Sprite sprite, MapProperties properties, GameObjectMap gameMap) {
		super(type, sprite, properties, gameMap);
		
		EventHandler.getInstance().registerEventListener(this);
	}
	
	@Override
	public void createPhysicsBody(float x, float y) {
		float width = mapProperties.get("width", Float.class) * Constants.WORLD_TO_SCREEN;
		float height = mapProperties.get("height", Float.class) * Constants.WORLD_TO_SCREEN;
		
		x = mapProperties.get("x", Float.class) * Constants.WORLD_TO_SCREEN + width * 0.5f;
		y = mapProperties.get("y", Float.class) * Constants.WORLD_TO_SCREEN + height * 0.5f;
		
		PhysicsBodyProperties properties = physicsBodyProperties.setX(x).setY(y).setWidth(width).setHeight(height)
				//change the collision type to OBSTACLE_SENSOR to not interact with projectiles
				.setCollisionType(PhysicsCollisionType.OBSTACLE_SENSOR);
		body = PhysicsBodyCreator.createRectangularBody(properties);
		body.setUserData(this);
	}
	
	@Override
	public void handleEvent(EventConfig event) {
		if (event == null) {
			return;
		}
		
		if (event.equals(typeConfig.changeBodyToSensorEvent)) {
			changeBodyToSensor();
		}
		else if (event.equals(typeConfig.changeBodyToSolidObjectEvent)) {
			changeBodyToNonSensor();
		}
	}
	
	@Override
	public void removeFromMap() {
		super.removeFromMap();
		
		EventHandler.getInstance().removeEventListener(this);
	}
}

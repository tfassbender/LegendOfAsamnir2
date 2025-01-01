package net.jfabricationgames.gdx.object.interactive;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;

import net.jfabricationgames.gdx.event.EventConfig;
import net.jfabricationgames.gdx.event.EventHandler;
import net.jfabricationgames.gdx.event.EventListener;
import net.jfabricationgames.gdx.event.EventType;
import net.jfabricationgames.gdx.object.GameObjectMap;
import net.jfabricationgames.gdx.object.GameObjectTypeConfig;
import net.jfabricationgames.gdx.physics.PhysicsBodyCreator;
import net.jfabricationgames.gdx.physics.PhysicsBodyCreator.PhysicsBodyProperties;
import net.jfabricationgames.gdx.physics.PhysicsCollisionType;
import net.jfabricationgames.gdx.physics.PhysicsWorld;

public class MovingObject extends InteractiveObject implements EventListener {
	
	private static final String MAP_PROPERTY_KEY_MOVING_OBJECT_ID = "movingObjectId";
	private static final String MAP_PROPERTY_KEY_PUSH_FORCE_FACTOR = "pushForceFactor";
	
	private Body movingBody;
	private float movingBodyTimer = 0f;
	private float pushForceFactor = 1f;
	
	public MovingObject(GameObjectTypeConfig typeConfig, Sprite sprite, MapProperties properties, GameObjectMap gameMap) {
		super(typeConfig, sprite, properties, gameMap);
		
		readMapProperties(properties);
		EventHandler.getInstance().registerEventListener(this);
	}
	
	private void readMapProperties(MapProperties properties) {
		pushForceFactor = Float.parseFloat(properties.get(MAP_PROPERTY_KEY_PUSH_FORCE_FACTOR, "1f", String.class));
	}
	
	@Override
	public void draw(float delta, SpriteBatch batch) {
		super.draw(delta, batch);
		
		if (movingBody != null) {
			movingBodyTimer += delta;
			
			if (movingBodyTimer > typeConfig.movingPartLifetimeInSeconds) {
				PhysicsWorld.getInstance().removeBodyWhenPossible(movingBody);
				movingBody = null;
				movingBodyTimer = 0f;
			}
		}
	}
	
	@Override
	protected void executeInteraction() {
		if (movingBody == null) {
			super.executeInteraction();
			PhysicsWorld.getInstance().runAfterWorldStep(this::addMovingObjectToMap);
		}
	}
	
	private void addMovingObjectToMap() {
		// calculate the starting offset by the moving vector and the distance from the type configuration
		Vector2 movingVector = new Vector2( //
				(float) Math.cos(Math.toRadians(typeConfig.movingPartDirectionInDegree)), // 
				(float) Math.sin(Math.toRadians(typeConfig.movingPartDirectionInDegree)));
		float offsetX = movingVector.x * typeConfig.movingPartStartDistance;
		float offsetY = movingVector.y * typeConfig.movingPartStartDistance;
		
		PhysicsBodyProperties properties = new PhysicsBodyProperties().setType(BodyType.DynamicBody) //
				.setX(body.getPosition().x + offsetX) //
				.setY(body.getPosition().y + offsetY) //
				.setCollisionType(PhysicsCollisionType.OBSTACLE) //
				.setRadius(typeConfig.movingPartRadius) //
				.setDensity(typeConfig.density); //
		
		movingBody = PhysicsBodyCreator.createCircularBody(properties);
		
		// apply a force to move the object once because it has no linear damping and the density is very high
		movingBody.applyForceToCenter(movingVector.scl(typeConfig.movingPartPushForce * pushForceFactor), true);
	}
	
	@Override
	public void handleEvent(EventConfig event) {
		if (event.eventType == EventType.ACTIVATE_MOVING_OBJECT) {
			if (event.stringValue.equals(mapProperties.get(MAP_PROPERTY_KEY_MOVING_OBJECT_ID))) {
				executeInteraction();
			}
		}
	}
	
	@Override
	public void removeFromMap() {
		super.removeFromMap();
		EventHandler.getInstance().removeEventListener(this);
	}
}

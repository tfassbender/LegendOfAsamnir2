package net.jfabricationgames.gdx.object.config;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.math.Vector2;

import net.jfabricationgames.gdx.attack.AttackHandler;
import net.jfabricationgames.gdx.event.EventConfig;
import net.jfabricationgames.gdx.event.EventHandler;
import net.jfabricationgames.gdx.event.EventListener;
import net.jfabricationgames.gdx.event.EventType;
import net.jfabricationgames.gdx.object.GameObject;
import net.jfabricationgames.gdx.object.GameObjectMap;
import net.jfabricationgames.gdx.object.GameObjectTypeConfig;
import net.jfabricationgames.gdx.physics.PhysicsBodyCreator;
import net.jfabricationgames.gdx.physics.PhysicsBodyCreator.PhysicsBodyProperties;
import net.jfabricationgames.gdx.physics.PhysicsCollisionType;

/**
 * An object that is used in as configuration point (usually to define positions for characters in a cutscene or for conditions).
 * It does not interact with any other objects.
 */
public class ConfigObject extends GameObject implements EventListener {
	
	private static final String CONFIG_FILE_ATTACKS = "config/objects/config_object_attacks.json";
	
	private AttackHandler attackHandler;
	
	public ConfigObject(GameObjectTypeConfig typeConfig, Sprite sprite, MapProperties mapProperties, GameObjectMap gameMap) {
		super(typeConfig, sprite, mapProperties, gameMap);
		
		EventHandler.getInstance().registerEventListener(this);
	}
	
	@Override
	public void createPhysicsBody(float x, float y) {
		PhysicsBodyProperties properties = physicsBodyProperties.setX(x).setY(y).setWidth(0.1f).setHeight(0.1f)
				//change the collision type to CONFIG_OBJECT to not interact with any other objects
				.setCollisionType(PhysicsCollisionType.CONFIG_OBJECT);
		body = PhysicsBodyCreator.createRectangularBody(properties);
		body.setUserData(this);
		
		changeBodyToSensor();
		
		attackHandler = new AttackHandler(CONFIG_FILE_ATTACKS, body, PhysicsCollisionType.PLAYER_ATTACK); // pretend that the player created the attack
	}
	
	@Override
	public void draw(float delta, SpriteBatch batch) {
		// only attacks need to be handled - the config object has no sprite
		attackHandler.handleAttacks(delta);
	}
	
	@Override
	public void handleEvent(EventConfig event) {
		if (event.eventType == EventType.CUTSCENE_CREATE_ATTACK && event.stringValue != null && event.stringValue.equals(getUnitId())) {
			String attackName = mapProperties.get("attackId", String.class);
			Vector2 attackDirection = getAttackDirection();
			
			attackHandler.startAttack(attackName, attackDirection);
		}
	}
	
	private Vector2 getAttackDirection() {
		// parse the attack direction from the string of the form (x,y)
		String attackDirectionString = mapProperties.get("attackDirection", String.class);
		float attackDirectionX = Float.parseFloat(attackDirectionString.substring(1, attackDirectionString.indexOf(",")).trim());
		float attackDirectionY = Float.parseFloat(attackDirectionString.substring(attackDirectionString.indexOf(",") + 1, attackDirectionString.length() - 1).trim());
		
		return new Vector2(attackDirectionX, attackDirectionY);
	}
	
	@Override
	public void removeFromMap() {
		super.removeFromMap();
		EventHandler.getInstance().removeEventListener(this);
	}
}

package net.jfabricationgames.gdx.object.config;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.badlogic.gdx.utils.Json;

import net.jfabricationgames.gdx.attack.AttackHandler;
import net.jfabricationgames.gdx.constants.Constants;
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
import net.jfabricationgames.gdx.physics.PhysicsWorld;
import net.jfabricationgames.gdx.sound.SoundHandler;
import net.jfabricationgames.gdx.sound.SoundPlayConfig;

/**
 * An object that is used in as configuration point (usually to define positions for characters in a cutscene or for conditions).
 * It does not interact with any other objects.
 */
public class ConfigObject extends GameObject implements EventListener, ContactListener {
	
	private static final String MAP_PROPERTY_KEY_ATTACK_ID = "attackId";
	private static final String MAP_PROPERTY_KEY_ATTACK_DIRECTION = "attackDirection";
	
	private static final String MAP_PROPERTY_KEY_EVENT_DELAY_IN_SECONDS = "eventDelayInSeconds";
	private static final String MAP_PROPERTY_KEY_EVENT_TO_FIRE = "eventToFire";
	private static final String MAP_PROPERTY_KEY_PLAY_CLOCK_TICKING_SOUND_FOR_DELAY = "playClockTickingSoundForDelay";
	
	private static final String CONFIG_FILE_ATTACKS = "config/objects/config_object_attacks.json";
	
	private AttackHandler attackHandler;
	private PhysicsCollisionType attackCollisionType = PhysicsCollisionType.PLAYER_ATTACK;
	
	private String attackName;
	private Vector2 attackDirection;
	
	private EventConfig configuredEvent;
	private float eventDelayInSeconds;
	private boolean playClockTickingSoundForDelay;
	
	private float fireEventTimer = 0; // fire the configured event (if any) after the delay has passed
	private SoundHandler clockTickingSoundHandler;
	
	public ConfigObject(GameObjectTypeConfig typeConfig, Sprite sprite, MapProperties mapProperties, GameObjectMap gameMap) {
		super(typeConfig, sprite, mapProperties, gameMap);
		
		parseMapProperties();
		EventHandler.getInstance().registerEventListener(this);
		PhysicsWorld.getInstance().registerContactListener(this);
	}
	
	private void parseMapProperties() {
		attackName = mapProperties.get(MAP_PROPERTY_KEY_ATTACK_ID, String.class);
		attackDirection = getAttackDirection();
		attackCollisionType = getAttackCollisionType();
		
		configuredEvent = parseEventConfigFromMapProperties();
		eventDelayInSeconds = Float.parseFloat(mapProperties.get(MAP_PROPERTY_KEY_EVENT_DELAY_IN_SECONDS, "0f", String.class));
		playClockTickingSoundForDelay = Boolean.parseBoolean(mapProperties.get(MAP_PROPERTY_KEY_PLAY_CLOCK_TICKING_SOUND_FOR_DELAY, "false", String.class));
	}
	
	private EventConfig parseEventConfigFromMapProperties() {
		String eventConfigJson = mapProperties.get(MAP_PROPERTY_KEY_EVENT_TO_FIRE, String.class);
		if (eventConfigJson != null) {
			return new Json().fromJson(EventConfig.class, eventConfigJson);
		}
		
		return null;
	}
	
	private Vector2 getAttackDirection() {
		// parse the attack direction from the string of the form (x,y)
		String attackDirectionString = mapProperties.get(MAP_PROPERTY_KEY_ATTACK_DIRECTION, String.class);
		if (attackDirectionString == null || attackDirectionString.isEmpty()) {
			return null;
		}
		
		float attackDirectionX = Float.parseFloat(attackDirectionString.substring(1, attackDirectionString.indexOf(",")).trim());
		float attackDirectionY = Float.parseFloat(attackDirectionString.substring(attackDirectionString.indexOf(",") + 1, attackDirectionString.length() - 1).trim());
		
		return new Vector2(attackDirectionX, attackDirectionY);
	}
	
	private PhysicsCollisionType getAttackCollisionType() {
		String attackCollisionTypeString = mapProperties.get("attackCollisionType", String.class);
		if (attackCollisionTypeString != null) {
			try {
				return PhysicsCollisionType.valueOf(attackCollisionTypeString);
			}
			catch (IllegalArgumentException e) {
				Gdx.app.error(getClass().getSimpleName(), "The collision type '" + attackCollisionTypeString + "' defined in the map properties is not valid.", e);
			}
		}
		
		return PhysicsCollisionType.PLAYER_ATTACK; // default
	}
	
	@Override
	public void createPhysicsBody(float x, float y) {
		PhysicsBodyProperties properties = physicsBodyProperties.setX(x).setY(y).setWidth(0.1f).setHeight(0.1f)
				//change the collision type to CONFIG_OBJECT to not interact with any other objects
				.setCollisionType(PhysicsCollisionType.CONFIG_OBJECT);
		body = PhysicsBodyCreator.createRectangularBody(properties);
		body.setUserData(this);
		
		changeBodyToSensor();
		
		attackHandler = new AttackHandler(CONFIG_FILE_ATTACKS, body, attackCollisionType); // pretend that the player created the attack
	}
	
	@Override
	public Vector2 getBodySize() {
		float width = mapProperties.get("width", Float.class);
		float height = mapProperties.get("height", Float.class);
		return new Vector2(width * Constants.WORLD_TO_SCREEN, height * Constants.WORLD_TO_SCREEN);
	}
	
	@Override
	public void draw(float delta, SpriteBatch batch) {
		// only attacks need to be handled - the config object has no sprite
		attackHandler.handleAttacks(delta);
		
		// fire delayed events (NOTE: only one timer per config event is supported - restarting before the event is fired will reset the timer)
		if (fireEventTimer > 0) {
			fireEventTimer -= delta;
			if (fireEventTimer <= 0) {
				EventHandler.getInstance().fireEvent(configuredEvent);
				
				fireEventTimer = 0;
				if (clockTickingSoundHandler != null) {
					clockTickingSoundHandler.stop();
				}
			}
		}
	}
	
	@Override
	public void handleEvent(EventConfig event) {
		if (event.eventType == EventType.CUTSCENE_CREATE_ATTACK && event.stringValue != null && event.stringValue.equals(getUnitId())) {
			attackHandler.startAttack(attackName, attackDirection);
		}
		else if (event.eventType == EventType.CONFIG_GAME_OBJECT_ACTION && event.stringValue != null && event.stringValue.equals(getUnitId())) {
			if (eventDelayInSeconds <= 0) {
				EventHandler.getInstance().fireEvent(configuredEvent);
			}
			else {
				fireEventTimer = eventDelayInSeconds; // set the delay timer to fire the event after the delay has passed
				if (playClockTickingSoundForDelay) {
					clockTickingSoundHandler = soundSet.playSound("clock_ticking", new SoundPlayConfig().setLooping(true).setVolume(0.3f));
				}
			}
		}
	}
	
	@Override
	public void removeFromMap() {
		super.removeFromMap();
		
		if (clockTickingSoundHandler != null) {
			clockTickingSoundHandler.stop();
		}
		EventHandler.getInstance().removeEventListener(this);
		PhysicsWorld.getInstance().removeContactListener(this);
	}
	
	@Override
	public void beginContact(Contact contact) {
		attackHandler.handleAttackDamage(contact);
	}
	
	@Override
	public void endContact(Contact contact) {}
	
	@Override
	public void preSolve(Contact contact, Manifold oldManifold) {}
	
	@Override
	public void postSolve(Contact contact, ContactImpulse impulse) {}
}

package net.jfabricationgames.gdx.object;

import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.utils.ObjectMap;

import net.jfabricationgames.gdx.attack.AttackType;
import net.jfabricationgames.gdx.event.EventConfig;
import net.jfabricationgames.gdx.object.interactive.InteractiveAction;
import net.jfabricationgames.gdx.physics.PhysicsCollisionType;

public class GameObjectTypeConfig {
	
	public GameObjectType type;
	
	public String texture;
	public String animationHit;
	public String hitSound;
	
	public boolean flipTextureX = false;
	public boolean flipTextureY = false;
	
	public float textureSizeFactorX = 1f;
	public float textureSizeFactorY = 1f;
	
	public float physicsBodySizeFactorX = 1f;
	public float physicsBodySizeFactorY = 1f;
	public float physicsBodyOffsetFactorX = 0f;
	public float physicsBodyOffsetFactorY = 0f;
	
	public BodyType bodyType = BodyType.StaticBody;
	public float density = 10f;
	public float friction = 0f;
	public float restitution = 0f;
	public float linearDamping = 10f;
	public PhysicsCollisionType collisionType = PhysicsCollisionType.OBSTACLE;
	public boolean isSensor = false;
	public boolean addSensor = false;
	public float sensorRadius = 0.5f;
	
	public GameObjectAction initAction; // an action that is executed after the game object was added to the map
	
	// map the default drop types to the probability to drop them. The probability sum must be <= 1.
	public ObjectMap<String, Float> drops;
	public float dropPositionOffsetX;
	public float dropPositionOffsetY;
	public boolean renderDropsAboveObject = false;
	
	public boolean hookshotConnectable = false;
	
	//****************************************
	//*** Destroyable Objects
	//****************************************
	
	public String animationBreak;
	public String destroySound;
	public float health = 0f;
	public AttackType requiredAttackType = AttackType.ATTACK;
	
	//****************************************
	//*** Interactive Objects
	//****************************************
	
	public String animationAction;
	public String textureAfterAction;
	public String interactionSound;
	public boolean multipleActionExecutionsPossible = false;
	public boolean hitAnimationAfterAction = false;
	public boolean changeBodyToSensorAfterAction = false;
	public boolean interactByContact = false;
	public float interactionMarkerOffsetX = 0f;
	public float interactionMarkerOffsetY = 0f;
	public InteractiveAction interactiveAction; // references a value from the InteractiveAction enum
	
	// to make an object not interactive, configure it without a sensor (addSensor: false)
	
	//****************************************
	//*** Locked Objects
	//****************************************
	
	public boolean defaultLocked = true;
	public String animationActionReversed;
	
	//****************************************
	//*** State Switch Objects
	//****************************************
	
	public boolean pressureActivated = false;
	public boolean magicActivated = false;
	public boolean canBeDeactivated = true;
	public boolean activateByAttack = false;
	
	//****************************************
	//*** Moving Objects
	//****************************************
	
	public float movingPartRadius = 1f;
	public float movingPartDirectionInDegree = 0f;
	public float movingPartPushForce = 1000f; // must be quite high because usually the density is also high (should be about 10 times the density)
	public float movingPartStartDistance = 0.5f;
	public float movingPartLifetimeInSeconds = 1f; // the moving part will be removed after this period of time
	
	//****************************************
	//*** Movable Objects
	//****************************************
	
	public boolean onlyMovableByPlayer = false; // increase the density of the object if no player is near - a sensor is needed for this to work
	
	//****************************************
	//*** Draggable Objects
	//****************************************
	
	public float maxDistanceToPlayer = 2f;
	
	//****************************************
	//*** Animation Objects
	//****************************************
	
	public String animation;
	
	//****************************************
	//*** Force Objects
	//****************************************
	
	public float pushForce = 5f; // use negative values to pull objects
	public float pushForceWhenBlocking = 2f;
	public boolean pushForceBlockAffected = false;
	
	//****************************************
	//*** Traverseable Objects
	//****************************************
	
	public EventConfig changeBodyToSensorEvent;
	public EventConfig changeBodyToSolidObjectEvent;
	
	public boolean initiallySolid = true;
	
	public String intValueByMapProperty; // the configured map property is used as intValue of the event objects (if configured here)
	
	public String animationTraverseable;
	public String animationSolid;
	
	public float animationTraverseableOffsetX = 0f;
	public float animationTraverseableOffsetY = 0f;
	
	public float animationSolidOffsetX = 0f;
	public float animationSolidOffsetY = 0f;
	
	public PhysicsCollisionType notTraversableCollisionType; // the collision type that is set when the object is solid
}

package net.jfabricationgames.gdx.character.player.implementation;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.Fixture;

import net.jfabricationgames.gdx.constants.Constants;
import net.jfabricationgames.gdx.event.EventConfig;
import net.jfabricationgames.gdx.event.EventHandler;
import net.jfabricationgames.gdx.event.EventType;
import net.jfabricationgames.gdx.item.Item;
import net.jfabricationgames.gdx.map.ground.GameMapGroundType;
import net.jfabricationgames.gdx.physics.CollisionUtil;
import net.jfabricationgames.gdx.physics.PhysicsBodyCreator;
import net.jfabricationgames.gdx.physics.PhysicsBodyCreator.PhysicsBodyProperties;
import net.jfabricationgames.gdx.physics.PhysicsCollisionType;

class CharacterBodyHandler {
	
	private static final float PHYSICS_BODY_SIZE_FACTOR_X = 0.6f;
	private static final float PHYSICS_BODY_SIZE_FACTOR_Y = 0.7f;
	private static final float PHYSICS_BODY_SENSOR_RADIUS = 0.6f;
	private static final float PHYSICS_BODY_LINEAR_DAMPING = 10f;
	
	private Dwarf player;
	
	protected Body body;
	
	protected GameMapGroundType groundProperties = GameMapGroundType.DEFAULT_GROUND_PROPERTIES;
	
	public CharacterBodyHandler(Dwarf player) {
		this.player = player;
		
		createPhysicsBody();
	}
	
	public void createPhysicsBody() {
		PhysicsBodyProperties bodyProperties = new PhysicsBodyProperties() //
				.setType(BodyType.DynamicBody) //
				.setWidth(player.renderer.idleDwarfSprite.getRegionWidth() * Constants.WORLD_TO_SCREEN * PHYSICS_BODY_SIZE_FACTOR_X) //
				.setHeight(player.renderer.idleDwarfSprite.getRegionHeight() * Constants.WORLD_TO_SCREEN * PHYSICS_BODY_SIZE_FACTOR_Y) //
				.setCollisionType(PhysicsCollisionType.PLAYER) //
				.setLinearDamping(PHYSICS_BODY_LINEAR_DAMPING);
		body = PhysicsBodyCreator.createOctagonBody(bodyProperties);
		body.setSleepingAllowed(false);
		
		PhysicsBodyProperties sensorProperties = new PhysicsBodyProperties() //
				.setBody(body) //
				.setSensor(true) //
				.setRadius(PHYSICS_BODY_SENSOR_RADIUS) //
				.setCollisionType(PhysicsCollisionType.PLAYER_SENSOR);
		PhysicsBodyCreator.addCircularFixture(sensorProperties);
		
		body.setUserData(player);
	}
	
	public void resetGroundProperties() {
		groundProperties = GameMapGroundType.DEFAULT_GROUND_PROPERTIES;
		body.setLinearDamping(PHYSICS_BODY_LINEAR_DAMPING);
	}
	
	public void move(float deltaX, float deltaY) {
		float force = 10f * body.getMass();
		if (!groundProperties.ignoreMovementSpeedFactorWhenStopped //
				|| body.getLinearVelocity().len() > 1.5f) { // slow enough to consider the player as stopped
			force *= groundProperties.movementSpeedFactor;
		}
		if (player.isFrozen()) {
			force *= 1f - (0.7f * getFreezingMovementFactor()); // slow down till 30% of the normal speed when frozen
		}
		
		body.applyForceToCenter(deltaX * force, deltaY * force, true);
	}
	
	private float getFreezingMovementFactor() {
		// start the movement reduction slowly and end slowly to adapt to the color gradient
		float progress = 1f;
		float freezingTimer = player.getFreezingTimer();
		float totalFreezingTime = player.getTotalFreezingTimeInSeconds();
		float colorGradientTime = 1f; // time to blend in and out
		
		if (freezingTimer > totalFreezingTime - colorGradientTime) {
			progress = ((totalFreezingTime / colorGradientTime) - freezingTimer);
		}
		else if (freezingTimer < colorGradientTime) {
			progress = freezingTimer;
		}
		
		return progress;
	}
	
	public void stopMovement() {
		body.setLinearVelocity(0, 0);
	}
	
	public void beginContact(Contact contact) {
		Fixture fixtureA = contact.getFixtureA();
		Fixture fixtureB = contact.getFixtureB();
		
		if (CollisionUtil.containsCollisionType(PhysicsCollisionType.PLAYER_SENSOR, fixtureA, fixtureB)) {
			//collect stuff that touches the player sensor (usually items)
			Object sensorCollidingUserData = CollisionUtil.getOtherTypeUserData(PhysicsCollisionType.PLAYER_SENSOR, fixtureA, fixtureB);
			
			if (sensorCollidingUserData instanceof Item) {
				Item item = (Item) sensorCollidingUserData;
				player.itemDataHandler.collectItem(item, true);
				if (item.isSpecialKey()) {
					EventHandler.getInstance().fireEvent(new EventConfig().setEventType(EventType.SPECIAL_KEY_ITEM_PICKED_UP));
				}
			}
		}
		
		player.attackHandler.handleAttackDamage(contact);
	}
	
	public void preSolve(Contact contact) {
		GameMapGroundType updatedGroundProperties = GameMapGroundType.handleGameMapGroundContact(contact, PhysicsCollisionType.PLAYER, groundProperties);
		if (updatedGroundProperties != null) {
			groundProperties = updatedGroundProperties;
			if (groundProperties.linearDamping >= 0) {
				body.setLinearDamping(groundProperties.linearDamping);
			}
			else {
				body.setLinearDamping(PHYSICS_BODY_LINEAR_DAMPING);
			}
		}
	}
	
	public void pushByHit(Vector2 hitCenter, float force, float forceWhenBlocked, boolean blockAffected) {
		if (player.isAlive()) {
			Vector2 pushDirection = player.getPushDirection(body.getPosition(), hitCenter);
			float pushForce = 10f * body.getMass();
			if (player.isBlocking()) {
				if (forceWhenBlocked >= 0) {
					pushForce *= forceWhenBlocked;
				}
				else if (blockAffected) {
					pushForce *= force * 0.33;
				}
			}
			else {
				pushForce *= force;
			}
			
			body.applyForceToCenter(pushDirection.x * pushForce, pushDirection.y * pushForce, true);
		}
	}
}

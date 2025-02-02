package net.jfabricationgames.gdx.object.force;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.badlogic.gdx.utils.Array;

import net.jfabricationgames.gdx.attack.Hittable;
import net.jfabricationgames.gdx.constants.Constants;
import net.jfabricationgames.gdx.object.GameObject;
import net.jfabricationgames.gdx.object.GameObjectMap;
import net.jfabricationgames.gdx.object.GameObjectTypeConfig;
import net.jfabricationgames.gdx.physics.CollisionUtil;
import net.jfabricationgames.gdx.physics.PhysicsBodyCreator;
import net.jfabricationgames.gdx.physics.PhysicsBodyCreator.PhysicsBodyProperties;
import net.jfabricationgames.gdx.physics.PhysicsWorld;

/**
 * A game object that applies a force to other objects when they collides with it. 
 */
public class ForceObject extends GameObject implements ContactListener {
	
	private Array<Hittable> affectedObjects = new Array<>();
	
	public ForceObject(GameObjectTypeConfig typeConfig, Sprite sprite, MapProperties mapProperties, GameObjectMap gameMap) {
		super(typeConfig, sprite, mapProperties, gameMap);
		
		registerAsContactListener();
	}
	
	private void registerAsContactListener() {
		PhysicsWorld physicsWorld = PhysicsWorld.getInstance();
		physicsWorld.registerContactListener(this);
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
				.setSensor(true);
		body = PhysicsBodyCreator.createRectangularBody(properties);
		body.setUserData(this);
	}
	
	@Override
	public void draw(float delta, SpriteBatch batch) {
		super.draw(delta, batch);
		
		applyForceToAffectedObjects();
	}
	
	private void applyForceToAffectedObjects() {
		for (Hittable hittable : affectedObjects) {
			if (hittable != null && body != null) {
				hittable.pushByHit(body.getPosition().cpy(), typeConfig.pushForce, typeConfig.pushForceWhenBlocking, typeConfig.pushForceBlockAffected);
			}
		}
	}
	
	@Override
	public void beginContact(Contact contact) {
		Fixture fixtureA = contact.getFixtureA();
		Fixture fixtureB = contact.getFixtureB();
		
		Object userData = CollisionUtil.getCollisionTypeUserData(typeConfig.collisionType, fixtureA, fixtureB);
		Fixture fixture = CollisionUtil.getOtherTypeFixture(typeConfig.collisionType, fixtureA, fixtureB);
		if (userData == this && !fixture.isSensor()) {
			Object collidingUserData = CollisionUtil.getOtherTypeUserData(typeConfig.collisionType, fixtureA, fixtureB);
			
			// add the affected object to the list if it's hittable
			if (collidingUserData instanceof Hittable) {
				Hittable hittable = (Hittable) collidingUserData;
				if (!affectedObjects.contains(hittable, true)) {
					affectedObjects.add(hittable);
				}
			}
		}
	}
	
	@Override
	public void endContact(Contact contact) {
		Fixture fixtureA = contact.getFixtureA();
		Fixture fixtureB = contact.getFixtureB();
		
		Object userData = CollisionUtil.getCollisionTypeUserData(typeConfig.collisionType, fixtureA, fixtureB);
		Fixture fixture = CollisionUtil.getOtherTypeFixture(typeConfig.collisionType, fixtureA, fixtureB);
		if (userData == this && !fixture.isSensor()) {
			Object collidingUserData = CollisionUtil.getOtherTypeUserData(typeConfig.collisionType, fixtureA, fixtureB);
			
			if (collidingUserData instanceof Hittable) {
				affectedObjects.removeValue((Hittable) collidingUserData, true);
			}
		}
	}
	
	@Override
	public void preSolve(Contact contact, Manifold oldManifold) {}
	
	@Override
	public void postSolve(Contact contact, ContactImpulse impulse) {}
	
	@Override
	public void removeFromMap() {
		super.removeFromMap();
		
		PhysicsWorld.getInstance().removeContactListener(this);
	}
}

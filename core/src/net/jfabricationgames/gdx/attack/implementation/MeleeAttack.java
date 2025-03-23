package net.jfabricationgames.gdx.attack.implementation;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.Fixture;

import net.jfabricationgames.gdx.attack.Attack;
import net.jfabricationgames.gdx.attack.AttackConfig;
import net.jfabricationgames.gdx.attack.AttackInfo;
import net.jfabricationgames.gdx.attack.AttackType;
import net.jfabricationgames.gdx.attack.Hittable;
import net.jfabricationgames.gdx.physics.CollisionUtil;
import net.jfabricationgames.gdx.physics.PhysicsBodyCreator;
import net.jfabricationgames.gdx.physics.PhysicsBodyCreator.PhysicsBodyProperties;
import net.jfabricationgames.gdx.physics.PhysicsBodyCreator.PhysicsBodyShape;
import net.jfabricationgames.gdx.physics.PhysicsCollisionType;
import net.jfabricationgames.gdx.physics.PhysicsWorld;

public class MeleeAttack extends Attack {
	
	public MeleeAttack(AttackConfig config, Vector2 direction, Body body, PhysicsCollisionType collisionType) {
		super(config, direction, body, collisionType);
		
		hitFixtureProperties = new PhysicsBodyProperties() //
				.setBody(body) //
				.setCollisionType(collisionType) //
				.setSensor(true) //
				.setPhysicsBodyShape(PhysicsBodyShape.CIRCLE) //
				.setRadius(config.hitFixtureRadius) //
				.setFixturePosition(getFixturePosition(direction));
		
		if (!config.type.isSubTypeOf(AttackType.MELEE)) {
			Gdx.app.error(getClass().getSimpleName(), "A MeleeAttack was created with a config type that is not a melee attack: " + config.type);
		}
	}
	
	private Vector2 getFixturePosition(Vector2 direction) {
		return direction.nor().scl(config.distFromCenter);
	}
	
	@Override
	protected void start() {
		hitFixture = PhysicsBodyCreator.addFixture(hitFixtureProperties);
		started = true;
	}
	
	@Override
	protected void remove() {
		if (hitFixture != null) {
			PhysicsWorld.getInstance().removeFixture(hitFixture, hitFixtureProperties.body);
		}
	}
	
	@Override
	protected void dealAttackDamage(Contact contact) {
		Fixture fixtureA = contact.getFixtureA();
		Fixture fixtureB = contact.getFixtureB();
		
		if (fixtureA.isSensor() && fixtureB.isSensor()) {
			// no attack damage if both are sensors (the attack might be a sensor, but the attacked object must not be an enemy sensor)
			return;
		}
		
		if (CollisionUtil.containsCollisionType(collisionType, fixtureA, fixtureB)) {
			Object attackUserData = CollisionUtil.getCollisionTypeUserData(collisionType, fixtureA, fixtureB);
			Object attackedObjectUserData = CollisionUtil.getOtherTypeUserData(collisionType, fixtureA, fixtureB);
			
			if (attackedObjectUserData != null && attackUserData == hitFixtureProperties.body.getUserData() && attackedObjectUserData instanceof Hittable) {
				Hittable attackedObject = ((Hittable) attackedObjectUserData);
				attackedObject.pushByHit(hitFixture.getBody().getPosition(), config.pushForce, config.pushForceWhenBlocked, config.pushForceAffectedByBlock);
				attackedObject.takeDamage(config.damage, AttackInfo.from(config));
			}
		}
	}
}

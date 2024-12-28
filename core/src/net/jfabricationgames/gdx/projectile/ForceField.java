package net.jfabricationgames.gdx.projectile;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.utils.Array;

import net.jfabricationgames.gdx.animation.AnimationDirector;
import net.jfabricationgames.gdx.attack.hit.Hittable;
import net.jfabricationgames.gdx.physics.CollisionUtil;
import net.jfabricationgames.gdx.physics.PhysicsBodyCreator.PhysicsBodyProperties;
import net.jfabricationgames.gdx.physics.PhysicsBodyCreator.PhysicsBodyShape;

public class ForceField extends Projectile {
	
	private Array<Hittable> affectedObjects = new Array<>();
	
	public ForceField(ProjectileTypeConfig typeConfig, AnimationDirector<TextureRegion> animation, ProjectileMap gameMap) {
		super(typeConfig, animation, gameMap);
	}
	
	@Override
	protected PhysicsBodyProperties createShapePhysicsBodyProperties() {
		return new PhysicsBodyProperties().setPhysicsBodyShape(PhysicsBodyShape.CIRCLE).setRadius(typeConfig.radius).setSensor(true);
	}
	
	@Override
	public void update(float delta) {
		super.update(delta);
		
		pushAffectedObjects();
	}
	
	private void pushAffectedObjects() {
		for (Hittable hittable : affectedObjects) {
			if (hittable != null && body != null && !isProjectileNotAffectedByForceField(hittable)) {
				hittable.pushByHit(body.getPosition().cpy(), typeConfig.pushForce, typeConfig.pushForce, false);
			}
		}
	}
	
	private boolean isProjectileNotAffectedByForceField(Hittable hittable) {
		if (hittable instanceof Projectile) {
			Projectile projectile = (Projectile) hittable;
			return !projectile.typeConfig.affectedByForceField;
		}
		
		return false;
	}
	
	@Override
	protected void processContact(Object contactUserData) {
		if (contactUserData instanceof Hittable) {
			Hittable hittable = (Hittable) contactUserData;
			if (!affectedObjects.contains(hittable, true)) {
				affectedObjects.add(hittable);
			}
		}
	}
	
	@Override
	public void endContact(Contact contact) {
		Fixture fixtureA = contact.getFixtureA();
		Fixture fixtureB = contact.getFixtureB();
		
		Object attackUserData = CollisionUtil.getCollisionTypeUserData(collisionType, fixtureA, fixtureB);
		Fixture attackedFixture = CollisionUtil.getOtherTypeFixture(collisionType, fixtureA, fixtureB);
		if (attackUserData == this && !attackedFixture.isSensor()) {
			Object attackedUserData = CollisionUtil.getOtherTypeUserData(collisionType, fixtureA, fixtureB);
			
			if (attackedUserData instanceof Hittable) {
				affectedObjects.removeValue((Hittable) attackedUserData, true);
			}
		}
	}
}

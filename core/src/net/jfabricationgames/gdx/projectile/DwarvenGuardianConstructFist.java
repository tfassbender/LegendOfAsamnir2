package net.jfabricationgames.gdx.projectile;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;

import net.jfabricationgames.gdx.animation.AnimationDirector;
import net.jfabricationgames.gdx.physics.PhysicsBodyCreator;
import net.jfabricationgames.gdx.physics.PhysicsBodyCreator.PhysicsBodyProperties;
import net.jfabricationgames.gdx.physics.PhysicsBodyCreator.PhysicsBodyShape;
import net.jfabricationgames.gdx.physics.PhysicsCollisionType;

public class DwarvenGuardianConstructFist extends Projectile {
	
	public DwarvenGuardianConstructFist(ProjectileTypeConfig typeConfig, AnimationDirector<TextureRegion> animation, ProjectileMap gameMap) {
		super(typeConfig, animation, gameMap);
	}
	
	@Override
	protected void createPhysicsBody(Vector2 position, Vector2 direction, PhysicsCollisionType collisionType) {
		this.collisionType = PhysicsCollisionType.PLAYER_ATTACK; // the guardian construct is used by the player and should not attack him
		
		float xOffset;
		if (direction.x < 0) {
			xOffset = -1.9f;
		}
		else {
			xOffset = 1.9f;
		}
		
		PhysicsBodyProperties bodyProperties = createShapePhysicsBodyProperties() //
				.setType(BodyType.DynamicBody) //
				.setX(position.x + xOffset) //
				.setY(position.y) //
				.setCollisionType(collisionType) //
				.setLinearDamping(typeConfig.damping);
		
		body = PhysicsBodyCreator.createBody(bodyProperties);
		body.setUserData(this);
	}
	
	@Override
	protected void startProjectile(Vector2 direction) {
		super.startProjectile(direction.cpy()); // use a copy of the direction vector because the super method will change it
		
		setImageOffset(0f, 0.2f); // x offset is done by the body position
		if (direction.x < 0 == !animation.getKeyFrame().isFlipX()) {
			animation.flip(true, false);
		}
	}
	
	@Override
	protected PhysicsBodyProperties createShapePhysicsBodyProperties() {
		return new PhysicsBodyProperties().setPhysicsBodyShape(PhysicsBodyShape.CIRCLE).setRadius(1.5f);
	}
	
	@Override
	protected void stopProjectileAfterObjectHit() {
		// do nothing here because the fist is not stopped by hitting an object
	}
}

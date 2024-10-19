package net.jfabricationgames.gdx.projectile;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;

import net.jfabricationgames.gdx.animation.AnimationDirector;
import net.jfabricationgames.gdx.physics.PhysicsBodyCreator;
import net.jfabricationgames.gdx.physics.PhysicsBodyCreator.PhysicsBodyProperties;
import net.jfabricationgames.gdx.physics.PhysicsBodyCreator.PhysicsBodyShape;
import net.jfabricationgames.gdx.physics.PhysicsCollisionType;

public class DwarvenGuardianConstructFire extends Projectile {
	
	public DwarvenGuardianConstructFire(ProjectileTypeConfig typeConfig, AnimationDirector<TextureRegion> animation, ProjectileMap gameMap) {
		super(typeConfig, animation, gameMap);
	}
	
	@Override
	protected void createPhysicsBody(Vector2 position, Vector2 direction, PhysicsCollisionType collisionType) {
		this.collisionType = PhysicsCollisionType.PLAYER_ATTACK; // the guardian construct is used by the player and should not attack him
		
		float xOffset;
		float yOffset = -0.7f;
		if (direction.x < 0) {
			xOffset = -1.5f;
		}
		else {
			xOffset = 1.5f;
		}
		
		PhysicsBodyProperties bodyProperties = createShapePhysicsBodyProperties() //
				.setType(BodyType.DynamicBody) //
				.setX(position.x + xOffset) //
				.setY(position.y + yOffset) //
				.setCollisionType(this.collisionType) //
				.setSensor(true) //
				.setLinearDamping(typeConfig.damping);
		
		body = PhysicsBodyCreator.createBody(bodyProperties);
		body.setUserData(this);
	}
	
	@Override
	protected void startProjectile(Vector2 direction) {
		super.startProjectile(direction.cpy()); // use a copy of the direction vector because the super method will change it
		
		if (direction.x < 0) {
			setImageOffset(0.7f, 0.9f); // offsets are also done by the body position
		}
		else {
			setImageOffset(-0.7f, 0.9f); // offsets are also done by the body position
		}
		
		if (direction.x < 0 == !animation.getKeyFrame().isFlipX()) {
			animation.flip(true, false);
		}
	}
	
	@Override
	protected PhysicsBodyProperties createShapePhysicsBodyProperties() {
		return new PhysicsBodyProperties().setPhysicsBodyShape(PhysicsBodyShape.CIRCLE).setRadius(1f);
	}
	
	@Override
	protected void stopProjectileAfterObjectHit() {
		// do nothing here because the fire is not stopped by hitting an object
	}
}

package net.jfabricationgames.gdx.projectile;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

import net.jfabricationgames.gdx.animation.AnimationDirector;
import net.jfabricationgames.gdx.attack.AttackConfig;
import net.jfabricationgames.gdx.character.player.PlayableCharacter;
import net.jfabricationgames.gdx.character.player.Player;
import net.jfabricationgames.gdx.physics.PhysicsBodyCreator.PhysicsBodyProperties;
import net.jfabricationgames.gdx.physics.PhysicsBodyCreator.PhysicsBodyShape;

public class HomingInertialMassProjectile extends Projectile {
	
	protected PlayableCharacter player;
	
	protected HomingInertialMassProjectile(ProjectileTypeConfig typeConfig, AttackConfig attackConfig, AnimationDirector<TextureRegion> animation, ProjectileMap gameMap) {
		super(typeConfig, attackConfig, animation, gameMap);
		
		player = Player.getInstance();
	}
	
	@Override
	public void update(float delta) {
		super.update(delta);
		
		move(delta);
	}
	
	protected void move(float delta) {
		if (body != null) { // check whether the projectile still exists on the map
			// make the projectile follow the player
			moveTo(player.getPosition());
		}
	}
	
	protected void moveTo(Vector2 targetPos) {
		Vector2 position = body.getPosition();
		Vector2 velocity = body.getLinearVelocity();
		
		// Desired velocity (where we want to go)
		Vector2 desiredVelocity = targetPos.cpy().sub(position).nor().scl(typeConfig.speed);
		
		// Steering force = desired - current
		Vector2 steering = desiredVelocity.sub(velocity);
		
		// Limit how strong the steering can be
		float maxForce = 10f * body.getMass();
		if (steering.len2() > maxForce * maxForce) {
			steering.nor().scl(maxForce);
		}
		
		body.applyForceToCenter(steering, true);
		
		// Rotate based on actual movement, not desired direction
		if (velocity.len2() > 0.01f) {
			rotateProjectile(velocity);
		}
	}
	
	@Override
	protected PhysicsBodyProperties createShapePhysicsBodyProperties() {
		return new PhysicsBodyProperties().setPhysicsBodyShape(PhysicsBodyShape.CIRCLE).setRadius(0.4f);
	}
}

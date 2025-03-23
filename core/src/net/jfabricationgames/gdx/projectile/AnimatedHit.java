package net.jfabricationgames.gdx.projectile;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

import net.jfabricationgames.gdx.animation.AnimationDirector;
import net.jfabricationgames.gdx.attack.AttackConfig;
import net.jfabricationgames.gdx.physics.PhysicsBodyCreator.PhysicsBodyProperties;
import net.jfabricationgames.gdx.physics.PhysicsBodyCreator.PhysicsBodyShape;

public class AnimatedHit extends Projectile {
	
	public AnimatedHit(ProjectileTypeConfig typeConfig, AttackConfig attackConfig, AnimationDirector<TextureRegion> animation, ProjectileMap gameMap) {
		super(typeConfig, attackConfig, animation, gameMap);
	}
	
	@Override
	protected void startProjectile(Vector2 direction) {
		super.startProjectile(direction.cpy()); // use a copy of the direction vector because the super method will change it
		
		if (typeConfig.flipAnimationToAttackDirection && direction.x < 0 == !animation.getKeyFrame().isFlipX()) {
			animation.flip(true, false);
		}
	}
	
	@Override
	protected float getInitialOffsetX(Vector2 direction) {
		float xOffset = typeConfig.distanceFromCenter;
		if (direction.x < 0) {
			xOffset *= -1;
		}
		
		return xOffset;
	}
	
	@Override
	protected PhysicsBodyProperties createShapePhysicsBodyProperties() {
		return new PhysicsBodyProperties().setPhysicsBodyShape(PhysicsBodyShape.CIRCLE).setRadius(typeConfig.hitFixtureRadius);
	}
}

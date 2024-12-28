package net.jfabricationgames.gdx.projectile;

import com.badlogic.gdx.graphics.g2d.TextureRegion;

import net.jfabricationgames.gdx.animation.AnimationDirector;
import net.jfabricationgames.gdx.attack.hit.Hittable;
import net.jfabricationgames.gdx.physics.PhysicsBodyCreator.PhysicsBodyProperties;
import net.jfabricationgames.gdx.physics.PhysicsBodyCreator.PhysicsBodyShape;

public class Hadouken extends Projectile {
	
	private static final int ANGLE_OFFSET_SPRITE_VECTOR = 90;
	
	protected Hadouken(ProjectileTypeConfig typeConfig, AnimationDirector<TextureRegion> animation, ProjectileMap gameMap) {
		super(typeConfig, animation, gameMap);
	}
	
	@Override
	protected PhysicsBodyProperties createShapePhysicsBodyProperties() {
		return new PhysicsBodyProperties().setPhysicsBodyShape(PhysicsBodyShape.CIRCLE).setRadius(0.2f);
	}
	
	@Override
	protected float getSpriteVectorAngleOffset() {
		return ANGLE_OFFSET_SPRITE_VECTOR;
	}
	
	@Override
	protected void processContact(Object contactUserData) {
		super.processContact(contactUserData);
		
		if (contactUserData instanceof Hittable) { // prevents the hadouken from hitting the NPC that fired it (impa)
			removeFromMap();
		}
	}
}

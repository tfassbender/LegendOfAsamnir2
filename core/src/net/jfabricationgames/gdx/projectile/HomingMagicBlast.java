package net.jfabricationgames.gdx.projectile;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.physics.box2d.Contact;

import net.jfabricationgames.gdx.animation.AnimationDirector;
import net.jfabricationgames.gdx.attack.AttackConfig;
import net.jfabricationgames.gdx.attack.Hittable;
import net.jfabricationgames.gdx.physics.PhysicsBodyCreator.PhysicsBodyProperties;
import net.jfabricationgames.gdx.physics.PhysicsBodyCreator.PhysicsBodyShape;

public class HomingMagicBlast extends Projectile {
	
	private static final int ANGLE_OFFSET_SPRITE_VECTOR = 90;
	
	protected HomingMagicBlast(ProjectileTypeConfig typeConfig, AttackConfig attackConfig, AnimationDirector<TextureRegion> animation, ProjectileMap gameMap) {
		super(typeConfig, attackConfig, animation, gameMap);
	}
	
	// TODO make the projectile follow the player
	
	@Override
	protected PhysicsBodyProperties createShapePhysicsBodyProperties() {
		return new PhysicsBodyProperties().setPhysicsBodyShape(PhysicsBodyShape.CIRCLE).setRadius(0.4f);
	}
	
	@Override
	protected float getSpriteVectorAngleOffset() {
		return ANGLE_OFFSET_SPRITE_VECTOR;
	}
	
	@Override
	protected void processContact(Object contactUserData, Contact contact) {
		super.processContact(contactUserData, contact);
		
		if (contactUserData instanceof Hittable) { // prevents the hadouken from hitting the NPC that fired it (impa)
			removeFromMap();
		}
	}
}

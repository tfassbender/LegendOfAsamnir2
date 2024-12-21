package net.jfabricationgames.gdx.projectile;

import com.badlogic.gdx.graphics.g2d.Sprite;

import net.jfabricationgames.gdx.physics.PhysicsBodyCreator.PhysicsBodyProperties;
import net.jfabricationgames.gdx.physics.PhysicsBodyCreator.PhysicsBodyShape;

public class Slingshot extends Projectile {
	
	public Slingshot(ProjectileTypeConfig typeConfig, Sprite sprite, ProjectileMap gameMap) {
		super(typeConfig, sprite, gameMap);
		setImageOffset(0f, 0f);
	}
	
	@Override
	protected PhysicsBodyProperties createShapePhysicsBodyProperties() {
		return new PhysicsBodyProperties().setPhysicsBodyShape(PhysicsBodyShape.CIRCLE).setRadius(0.1f);
	}
	
	@Override
	protected void stopProjectileAfterObjectHit() {
		super.stopProjectileAfterObjectHit();
		changeBodyToSensor();
	}
}

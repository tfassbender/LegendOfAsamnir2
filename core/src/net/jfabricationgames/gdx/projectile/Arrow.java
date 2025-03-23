package net.jfabricationgames.gdx.projectile;

import com.badlogic.gdx.graphics.g2d.Sprite;

import net.jfabricationgames.gdx.attack.AttackConfig;
import net.jfabricationgames.gdx.physics.PhysicsBodyCreator.PhysicsBodyProperties;
import net.jfabricationgames.gdx.physics.PhysicsBodyCreator.PhysicsBodyShape;

public class Arrow extends Projectile {
	
	private static final int ANGLE_OFFSET_SPRITE_VECTOR = 90;
	
	protected Arrow(ProjectileTypeConfig typeConfig, AttackConfig attackConfig, Sprite sprite, ProjectileMap gameMap) {
		super(typeConfig, attackConfig, sprite, gameMap);
	}
	
	@Override
	protected PhysicsBodyProperties createShapePhysicsBodyProperties() {
		return new PhysicsBodyProperties().setPhysicsBodyShape(PhysicsBodyShape.RECTANGLE).setWidth(0.75f).setHeight(0.25f);
	}
	
	@Override
	protected float getSpriteVectorAngleOffset() {
		return ANGLE_OFFSET_SPRITE_VECTOR;
	}
}

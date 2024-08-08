package net.jfabricationgames.gdx.projectile;

import com.badlogic.gdx.graphics.g2d.Sprite;

import net.jfabricationgames.gdx.map.ground.MapObjectType;
import net.jfabricationgames.gdx.object.GameObject;
import net.jfabricationgames.gdx.object.GameObjectType;
import net.jfabricationgames.gdx.physics.PhysicsBodyCreator.PhysicsBodyProperties;
import net.jfabricationgames.gdx.physics.PhysicsBodyCreator.PhysicsBodyShape;

public class MagicWave extends Projectile {
	
	public MagicWave(ProjectileTypeConfig typeConfig, Sprite sprite, ProjectileMap gameMap) {
		super(typeConfig, sprite, gameMap);
	}
	
	@Override
	protected PhysicsBodyProperties createShapePhysicsBodyProperties() {
		return new PhysicsBodyProperties().setPhysicsBodyShape(PhysicsBodyShape.CIRCLE).setRadius(0.9f).setSensor(true);
	}
	
	@Override
	protected void processContact(Object contactUserData) {
		if (contactUserData == MapObjectType.SOLID_OBJECT || isMovableObject(contactUserData)) {
			removeFromMap();
		}
	}
	
	private boolean isMovableObject(Object contactUserData) {
		return contactUserData instanceof GameObject && ((GameObject) contactUserData).getType() == GameObjectType.MOVABLE;
	}
}

package net.jfabricationgames.gdx.object.moveable;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Joint;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.badlogic.gdx.physics.box2d.joints.RopeJointDef;

import net.jfabricationgames.gdx.object.GameObjectMap;
import net.jfabricationgames.gdx.object.GameObjectTypeConfig;
import net.jfabricationgames.gdx.object.InteractivePlayer;
import net.jfabricationgames.gdx.physics.CollisionUtil;
import net.jfabricationgames.gdx.physics.PhysicsCollisionType;
import net.jfabricationgames.gdx.physics.PhysicsWorld;

public class DraggableObject extends MovableObject implements ContactListener {
	
	private Joint joint; // box2d joint that connects the object body to the player body
	
	public DraggableObject(GameObjectTypeConfig typeConfig, Sprite sprite, MapProperties mapProperties, GameObjectMap gameMap) {
		super(typeConfig, sprite, mapProperties, gameMap);
		
		PhysicsWorld.getInstance().registerContactListener(this);
	}
	
	public void toggleDrag(Body body) {
		if (joint == null) {
			createJoint(body);
		}
		else {
			dissolveJoint();
		}
	}
	
	private void createJoint(Body playerBody) {
		RopeJointDef jointDef = new RopeJointDef();
		jointDef.bodyA = body;
		jointDef.bodyB = playerBody;
		jointDef.localAnchorA.set(0, 0);
		jointDef.localAnchorB.set(0, 0);
		
		float currentDistance = body.getPosition().dst(body.getPosition());
		jointDef.maxLength = Math.max(typeConfig.maxDistanceToPlayer, currentDistance);
		jointDef.collideConnected = true;
		
		joint = PhysicsWorld.getInstance().createJoint(jointDef);
	}
	
	private void dissolveJoint() {
		PhysicsWorld.getInstance().destroyJoint(joint);
		joint = null;
	}
	
	@Override
	public void drawShapes(ShapeRenderer shapeRenderer) {
		super.drawShapes(shapeRenderer);
		
		if (joint != null) {
			shapeRenderer.setColor(Color.BROWN);
			shapeRenderer.rectLine(body.getPosition(), joint.getBodyB().getPosition(), 0.05f);
		}
	}
	
	@Override
	public void beginContact(Contact contact) {
		InteractivePlayer player = CollisionUtil.getObjectCollidingWith(this, PhysicsCollisionType.OBSTACLE_SENSOR, contact, playerObjectClass);
		if (player != null) {
			player.setDraggableObject(this);
		}
	}
	
	@Override
	public void endContact(Contact contact) {
		InteractivePlayer player = CollisionUtil.getObjectCollidingWith(this, PhysicsCollisionType.OBSTACLE_SENSOR, contact, playerObjectClass);
		if (player != null) {
			player.setDraggableObject(null);
		}
	}
	
	@Override
	public void preSolve(Contact contact, Manifold oldManifold) {}
	
	@Override
	public void postSolve(Contact contact, ContactImpulse impulse) {}
	
	@Override
	public void removeFromMap() {
		super.removeFromMap();
		PhysicsWorld.getInstance().removeContactListener(this);
	}
}

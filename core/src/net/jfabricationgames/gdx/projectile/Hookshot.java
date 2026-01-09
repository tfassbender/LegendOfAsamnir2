package net.jfabricationgames.gdx.projectile;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.Joint;
import com.badlogic.gdx.physics.box2d.joints.RopeJointDef;

import net.jfabricationgames.gdx.attack.AttackConfig;
import net.jfabricationgames.gdx.event.EventConfig;
import net.jfabricationgames.gdx.event.EventHandler;
import net.jfabricationgames.gdx.event.EventType;
import net.jfabricationgames.gdx.object.GameObject;
import net.jfabricationgames.gdx.physics.PhysicsWorld;

public class Hookshot extends Boomerang {
	
	private static final float PULL_FORCE = 100f;
	
	private Joint playerJoint;
	private Joint targetJoint;
	
	private float dragTimer = 0f;
	
	public Hookshot(ProjectileTypeConfig typeConfig, AttackConfig attackConfig, Sprite sprite, ProjectileMap gameMap) {
		super(typeConfig, attackConfig, sprite, gameMap);
	}
	
	@Override
	protected void rotateProjectile(float delta) {
		if (!attachedToTarget()) {
			super.rotateProjectile(delta);
		}
	}
	
	public boolean attachedToTarget() {
		return targetJoint != null;
	}
	
	@Override
	protected void startProjectile(Vector2 direction) {
		super.startProjectile(direction);
		
		// connect to the player by sending him an event that he can subscribe to (playerBody is not yet set)
		EventConfig event = new EventConfig().setEventType(EventType.HOOKSHOT_ATTACK_STARTED).setParameterObject(this);
		EventHandler.getInstance().fireEvent(event);
		connectToPlayer(event.parameterObject);
		
		// change traversable objects to sensors here - the hookshot cannot collide with them anyway, since it's a projectile
		EventHandler.getInstance().fireEvent(new EventConfig().setEventType(EventType.TRAVERSABLE_OBJECT_CHANGE_BODY_TO_SENSOR));
	}
	
	private void connectToPlayer(Object eventParameterObject) {
		if (eventParameterObject == null) {
			Gdx.app.error(getClass().getSimpleName(), "No body to connect subscribed to the hookshot event. The hookshot cannot connect to the player.");
			return;
		}
		if (!(eventParameterObject instanceof Body)) {
			Gdx.app.error(getClass().getSimpleName(), "The subscribed object is no Box2D Body (" + eventParameterObject.getClass().getName() + "). The hookshot cannot connect to it.");
			return;
		}
		
		Body playerBody = (Body) eventParameterObject;
		
		RopeJointDef jointDef = new RopeJointDef();
		jointDef.bodyA = body;
		jointDef.bodyB = playerBody;
		jointDef.localAnchorA.set(0, 0);
		jointDef.localAnchorB.set(0, 0);
		
		jointDef.maxLength = 15f; // greater than the max reach of the hookshot
		jointDef.collideConnected = false;
		
		playerJoint = PhysicsWorld.getInstance().createJoint(jointDef);
	}
	
	@Override
	public void update(float delta) {
		if (attachedToTarget()) {
			dragPlayer();
			
			dragTimer += delta;
			if (dragTimer > 5f) { // resolve the hookshot after 5 seconds (which is much longer than the player should need to be dragged)
				// fail safe to prevent the hookshot from being stuck on a position where the player cannot reach it
				removeAfterMovedBackToPlayer();
				return;
			}
		}
		
		super.update(delta);
	}
	
	private void dragPlayer() {
		Body playerBody = playerJoint.getBodyB();
		Vector2 pullDirection = body.getPosition().cpy().sub(playerBody.getPosition()).nor();
		float force = PULL_FORCE * playerBody.getMass();
		playerBody.applyForceToCenter(pullDirection.x * force, pullDirection.y * force, true);
	}
	
	@Override
	public void removeAfterMovedBackToPlayer() {
		// resolve the joints to the player and to the target
		PhysicsWorld.getInstance().destroyJoint(playerJoint);
		if (targetJoint != null) {
			PhysicsWorld.getInstance().destroyJoint(targetJoint);
		}
		
		super.removeAfterMovedBackToPlayer();
		
		// tells the player that the attack is finished so the player is able to move again
		EventHandler.getInstance().fireEvent(new EventConfig().setEventType(EventType.HOOKSHOT_ATTACK_FINISHED));
		
		// change traversable objects back to solid bodies after the hookshot finishes
		EventHandler.getInstance().fireEvent(new EventConfig().setEventType(EventType.TRAVERSABLE_OBJECT_CHANGE_BODY_TO_SOLID_OBJECT));
	}
	
	@Override
	protected void processContact(Object contactUserData, Contact contact) {
		super.processContact(contactUserData, contact);
		
		if (contactUserData instanceof GameObject && ((GameObject) contactUserData).isHookshotConnectable()) {
			// the joint cannot be created in a world step
			PhysicsWorld.getInstance().runAfterWorldStep(() -> connectToTarget((GameObject) contactUserData, contact));
		}
	}
	
	private void connectToTarget(GameObject hitGameObject, Contact contact) {
		RopeJointDef jointDef = new RopeJointDef();
		jointDef.bodyA = body;
		jointDef.bodyB = hitGameObject.getBody();
		
		// get the contact point to connect the hookshot to the target
		Vector2 contactPoint = contact.getWorldManifold().getPoints()[0];
		Vector2 localAnchor = hitGameObject.getBody().getLocalPoint(contactPoint);
		
		jointDef.localAnchorA.set(0, 0);
		jointDef.localAnchorB.set(localAnchor);
		
		jointDef.maxLength = 0f;
		jointDef.collideConnected = false;
		
		targetJoint = PhysicsWorld.getInstance().createJoint(jointDef);
	}
	
	@Override
	public void draw(float delta, SpriteBatch batch) {
		super.draw(delta, batch);
	}
	
	@Override
	public void drawShapes(float delta, ShapeRenderer shapeRenderer) {
		super.drawShapes(delta, shapeRenderer);
		
		if (playerJoint != null) {
			// draw the rope that connects the player to the hookshot
			shapeRenderer.setColor(Color.BROWN);
			shapeRenderer.rectLine(body.getPosition(), playerJoint.getBodyB().getPosition(), 0.05f);
		}
	}
}

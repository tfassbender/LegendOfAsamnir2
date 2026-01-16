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
import com.badlogic.gdx.physics.box2d.WorldManifold;
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
	
	private Vector2 contactPoint;
	
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
		
		PhysicsWorld.getInstance().createJoint(jointDef, joint -> this.playerJoint = joint);
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
	public void beginContact(Contact contact) {
		contactPoint = null;
		
		WorldManifold manifold = contact.getWorldManifold();
		Vector2[] points = manifold.getPoints();
		if (points != null && points.length > 0) {
			/*
			 * IMPORTANT BOX2D NOTE:
			 *
			 * The contact point MUST be copied immediately at the very beginning of
			 * beginContact().
			 *
			 * Box2D Contact objects (including WorldManifold and its points array)
			 * become INVALID as soon as the physics world is modified in any way.
			 * This can happen indirectly during contact processing, e.g. by:
			 *  - dealing damage
			 *  - destroying bodies or fixtures
			 *  - changing collision filters
			 *  - creating or destroying joints
			 *
			 * Even if all of this happens inside the same beginContact callback,
			 * Box2D is allowed to invalidate the contact internally. Accessing
			 * contact.getWorldManifold() after that can lead to native crashes
			 * (EXCEPTION_ACCESS_VIOLATION in gdx-box2d).
			 *
			 * Therefore:
			 *  - read the contact data first
			 *  - copy it (Vector2.cpy())
			 *  - never access the Contact object again afterward
			 */
			contactPoint = points[0].cpy();
		}
		
		super.beginContact(contact);
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
		
		// we already got the contact point to connect the hookshot to the target in beginContact
		if (contactPoint == null) {
			Gdx.app.error(getClass().getSimpleName(), "Cannot create hookshot target joint because the contact point is null.");
			return;
		}
		Vector2 localAnchor = hitGameObject.getBody().getLocalPoint(contactPoint);
		
		jointDef.localAnchorA.set(0, 0);
		jointDef.localAnchorB.set(localAnchor);
		
		jointDef.maxLength = 0f;
		jointDef.collideConnected = false;
		
		PhysicsWorld.getInstance().createJoint(jointDef, joint -> this.targetJoint = joint);
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
